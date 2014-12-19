package utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.htk.moment.ui.LaunchActivity;
import com.htk.moment.ui.R;
import come.htk.bean.IndexInfoBean;
import utils.android.sdcard.Read;
import utils.android.sdcard.Write;
import utils.internet.ConnectionHandler;
import utils.internet.UrlSource;
import utils.json.JSONArray;
import utils.json.JSONObject;
import utils.view.fragment.IndexFragment;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;


/**
 * 动态首页listView
 * <p/>
 * Created by Administrator on 2014/11/21.
 */
public class IndexPullRefreshListView extends ListView implements AbsListView.OnScrollListener {

	private static final String TAG = "IndexPullRefreshListView";

	// 正常阅读模式
	private final int NORMAL_STATE_TO_READ = 0;

	// 下拉刷新
	private final int PULL_TO_REFRESH = 1;

	// 释放刷新
	private final int RELEASE_TO_REFRESH = 2;

	// 正在刷新
	private final int REFRESHING = 3;

	private final int LOAD_MORE = 5;

	private final int LOADING = 6;

	// 一次完全的屏幕事件，第一次按下的位置的Y坐标
	private int startY;

	private int endY;

	private int mLastRefreshState;

	// 头部视图高度
	private int mHeadViewHeight;

	public boolean canRefresh;

	public boolean canLoadMore;


	private int mRefreshState;

	private int mLoadMoreState;

	private int oldPaddingTop;

	private RotateAnimation pullAnimation;                    // 下拉动画

	private RotateAnimation releaseAnimation;                 // 恢复动画


	private LayoutInflater mInflater;

	private RelativeLayout mListHeadLayout;

	private RelativeLayout mListFootLayout;

	private TextView mRefreshText;

	private TextView mLastTimeText;

	private ImageView mRefreshImageArrow;

	private ProgressBar mRefreshBar;


	private TextView mLoadMoreText;

	private ProgressBar mLoadMoreBar;

	private OnRefreshListener mOnRefreshListener;


	/**
	 * 监听器，刷新还是加载更多
	 */
	public interface OnRefreshListener {

		/**
		 * 刷新
		 */
		public void refresh();

		/**
		 * 加载更多
		 */
		public void loadMore();
	}

	public IndexPullRefreshListView(Context context) {

		super(context);
		init(context);
	}

	public IndexPullRefreshListView(Context context, AttributeSet attrs) {

		super(context, attrs);
		init(context);
	}

	public IndexPullRefreshListView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 初始化控件，加载布局文件
	 *
	 * @param context 上下文
	 */
	private void init(Context context) {

		mInflater = LayoutInflater.from(context);

		mListHeadLayout = (RelativeLayout) mInflater.inflate(R.layout.pull_to_refresh_header, this, false);
		mListFootLayout = (RelativeLayout) mInflater.inflate(R.layout.index_list_view_loadmore_footer, this, false);

		mRefreshText = (TextView) mListHeadLayout.findViewById(R.id.pull_to_refresh_text);
		mLastTimeText = (TextView) mListHeadLayout.findViewById(R.id.last_time_refresh_text);
		mRefreshImageArrow = (ImageView) mListHeadLayout.findViewById(R.id.pull_to_refresh_arrow);
		mRefreshBar = (ProgressBar) mListHeadLayout.findViewById(R.id.pull_to_refresh_circle_process_bar);

		mLoadMoreText = (TextView) mListFootLayout.findViewById(R.id.load_more_text);
		mLoadMoreBar = (ProgressBar) mListFootLayout.findViewById(R.id.load_more_progress);

		addHeaderView(mListHeadLayout);
		addFooterView(mListFootLayout);

		// 测量头部高度，listView对其中的子项的高度不会作限制
		measureView(mListHeadLayout);
		mHeadViewHeight = mListHeadLayout.getMeasuredHeight();
		oldPaddingTop = mListHeadLayout.getPaddingTop();

		// init the state
		mRefreshState = NORMAL_STATE_TO_READ;
		mLastRefreshState = mRefreshState;
		mLoadMoreState = NORMAL_STATE_TO_READ;

		// init animal
		pullAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		pullAnimation.setInterpolator(new LinearInterpolator());
		pullAnimation.setDuration(250);                // 设置持续时间
		pullAnimation.setFillAfter(true);                // 动画执行完是否停留在执行完的状态

		releaseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		releaseAnimation.setInterpolator(new LinearInterpolator());
		releaseAnimation.setDuration(250);
		releaseAnimation.setFillAfter(true);

		canRefresh = true;
		canLoadMore = true;

		setOnScrollListener(this);
		// 不显示头部（动态加载的布局文件）
//		setSelection(1);
		// 将第一项移动到屏幕顶端
		scrollTo(0, mHeadViewHeight + getDividerHeight() / 2);
	}


	/**
	 * 测量视图的（宽高模式）
	 *
	 * @param child 子视图
	 */
	private void measureView(View child) {

		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}


	private void headViewFitSide(MotionEvent ev) {

		final int historySize = ev.getHistorySize();

		for (int h = 0; h < historySize; h++) {

			int historicalY = (int) ev.getHistoricalY(0);
			// 1.7 拉升效果
			int topPadding = (int) (((historicalY - startY) - mHeadViewHeight) / 1.7);

			// 主要更改顶部视图的高度
			mListHeadLayout.setPadding(mListHeadLayout.getPaddingLeft(), topPadding, mListHeadLayout.getPaddingRight(), mListHeadLayout.getPaddingBottom());
		}
	}

	private int currentScrollState;

	/**
	 * Sets the header padding back to original size. 设置头部填充会原始大小
	 */
	private void resetHeaderPadding() {

		mListHeadLayout.setPadding(mListHeadLayout.getPaddingLeft(), oldPaddingTop, mListHeadLayout.getPaddingRight(), mListHeadLayout.getPaddingBottom());
	}

	/**
	 * Resets the header to the original state. 重新设置头部为原始状态
	 */
	private void resetHeader() {

		resetHeaderPadding();
		if (mRefreshState != NORMAL_STATE_TO_READ) {
			mRefreshState = NORMAL_STATE_TO_READ;

			mRefreshText.setVisibility(GONE);
			// Hide progress bar and arrow.
			mRefreshImageArrow.setVisibility(View.GONE);
			mRefreshBar.setVisibility(GONE);
		}
	}

	/**
	 * 重设ListView尾部视图为初始状态
	 */
	private void resetFooter() {

		if (mLoadMoreState != NORMAL_STATE_TO_READ) {
			mLoadMoreState = NORMAL_STATE_TO_READ;
			// 进度条设置为不可见
			mLoadMoreBar.setVisibility(View.GONE);
			mLoadMoreText.setVisibility(GONE);
			// 按钮的文本替换为“加载更多”
			mLoadMoreText.setText("加载更多");
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		currentScrollState = scrollState;
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		if (currentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
			if (firstVisibleItem == 0 && canRefresh) {
				if ((mListHeadLayout.getBottom() > mHeadViewHeight * 4)) {
					mRefreshState = RELEASE_TO_REFRESH;
					mRefreshImageArrow.setVisibility(VISIBLE);
					mRefreshImageArrow.clearAnimation();
					mRefreshImageArrow.startAnimation(pullAnimation);
					mRefreshText.setVisibility(VISIBLE);

					mRefreshText.setText(R.string.pull_to_refresh_release_label);
					if(IndexFragment.myHandler != null){
						IndexFragment.sendMessage("fresh", "sub_thread");
					}

				} else if ((mListHeadLayout.getBottom() < mHeadViewHeight * 3) || mListHeadLayout.getTop() < 1) {
					mRefreshState = PULL_TO_REFRESH;

					mRefreshImageArrow.setVisibility(VISIBLE);
					mRefreshImageArrow.clearAnimation();
					mRefreshText.setVisibility(VISIBLE);

					mLastRefreshState = mRefreshState;
					mRefreshImageArrow.startAnimation(releaseAnimation);

					mRefreshText.setText(R.string.pull_to_refresh_pull_label);
					if(IndexFragment.myHandler != null){
						IndexFragment.sendMessage("fresh", "sub_thread");
					}
				} else {
					resetHeaderPadding();
				}
			}
			if ((getLastVisiblePosition() == totalItemCount - 1) && (startY > endY) && canLoadMore && (mLoadMoreState == NORMAL_STATE_TO_READ)) {

				mLoadMoreState = LOAD_MORE;
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startY = (int) ev.getY();
				break;
			case MotionEvent.ACTION_UP:
				endY = (int) ev.getY();
				headViewFitSide(ev);
				checkState();
				break;
			case MotionEvent.ACTION_MOVE:
				// 处理头部拉伸
				headViewFitSide(ev);
				break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		return super.onInterceptTouchEvent(ev);
	}

	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {

		mOnRefreshListener = onRefreshListener;
	}

	/**
	 * 判断以/更改   刷新状态/加载状态）
	 */
	private void checkState() {

		int mRefreshStateOld = mRefreshState;
		int mLoadMoreStateOld = mLoadMoreState;

		// 如果list停下来的时候显示在头部，将自动定位到第一项
		if (currentScrollState == SCROLL_STATE_FLING) {
			if (getFirstVisiblePosition() == 0 && endY - startY > 0) {
				resetHeader();
				setSelection(1);
				System.out.println("我已经设置了。。。。。。。。。。。。。。。");
			}
		}

		switch (mRefreshStateOld) {
			case RELEASE_TO_REFRESH:
				mRefreshState = REFRESHING;
				// 禁止再次刷新
				canRefresh = false;
				canLoadMore = false;
				refresh();
				break;
			case NORMAL_STATE_TO_READ:
			case PULL_TO_REFRESH:
				resetHeader();
				mRefreshState = NORMAL_STATE_TO_READ;
				break;
			default:
				mRefreshState = NORMAL_STATE_TO_READ;
				break;
		}
		switch (mLoadMoreStateOld) {
			case LOAD_MORE:
				mLoadMoreState = LOADING;
				canLoadMore = false;
				canRefresh = false;
				loadMore();
				break;
			default:
				mLoadMoreState = NORMAL_STATE_TO_READ;
				break;
		}
	}
	/**
	 * 为刷新做准备*
	 */
	public void prepareForRefresh() {

		resetHeaderPadding();
		mRefreshImageArrow.setVisibility(View.GONE);            // 去掉刷新的箭头
		// We need this hack, otherwise it will keep the previous drawable.
		mRefreshBar.setVisibility(View.VISIBLE);    // 圆形进度条变为可见
		// Set refresh view text to the refreshing label
		mRefreshText.setText(R.string.pull_to_refresh_refreshing_label);
		mRefreshState = REFRESHING;
	}

	/**
	 * 为加载更多做准备*
	 */
	public void prepareForLoadMore() {

		mLoadMoreBar.setVisibility(View.VISIBLE);
		mLoadMoreText.setVisibility(VISIBLE);
		mLoadMoreText.setText("加载更多");
	}

	private void refresh() {

		prepareForRefresh();
		refreshData("refresh");
		//		mOnRefreshListener.refresh();

	}

	private void loadMore() {

		prepareForLoadMore();
		//		mOnRefreshListener.loadMore();
		refreshData("load");
	}


	/**
	 * Set a text to represent when the list was last updated. 设置一个文本来表示最近更新的列表，显示的是最近更新列表的时间
	 *
	 * @param lastUpdated Last updated at.
	 */
	public void setLastUpdated(CharSequence lastUpdated) {

		if (lastUpdated != null) {
			mLastTimeText.setVisibility(View.VISIBLE);
			mLastTimeText.setText("更新于: " + lastUpdated);
		} else {
			mLastTimeText.setVisibility(View.GONE);
		}
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 *
	 * @param lastUpdated Last updated at.
	 */
	public void onRefreshComplete(CharSequence lastUpdated) {

		setLastUpdated(lastUpdated);    // 显示更新时间

		// If refresh view is visible when loading completes, scroll down to
		// the next item.
		invalidateViews();
		resetHeader();
		if ((mListHeadLayout.getBottom() > 0) && (getFirstVisiblePosition() == 0)) {
			setSelection(1);
		}
		canRefresh = true;
		canLoadMore = true;
	}

	public void onLoadMoreComplete() {

		invalidateViews();
		canLoadMore = true;
		canRefresh = true;
		resetFooter();
	}


	/**
	 * 刷新 / 加载
	 */
	private void refreshData(String way) {

		new freshThread(way).start();
	}

	public static class freshThread extends Thread {

		private String way;

		public freshThread(String way) {

			this.way = way;
		}

		@Override
		public void run() {

			if (way.equals("refresh")) {
				IndexFragment.sendMessage("fresh", "sub_thread");
				internetRefresh();
			} else if (way.equals("load")) {
				internetLoad();
			} else {
				System.out.println("wrong fresh way !");
			}
		}
	}

	public static int rs_id = 0;
	
	private static void internetRefresh() {

		HttpURLConnection connection = null;
		JSONObject outToServer = new JSONObject();

		try {
			// 取得一个连接 多 part的 connection
			connection = ConnectionHandler.getConnect(UrlSource.LOAD_STATUS, LaunchActivity.JSESSIONID);
			OutputStream out = connection.getOutputStream();
			outToServer.put("rs_id", rs_id);
			outToServer.put("before", true);

			Write.writeToHttp(out, outToServer.toString().getBytes());

			System.out.println(rs_id + "  更新的时候      服务器消息  -  " + connection.getResponseCode());

			String temp = Read.read(connection.getInputStream());
			if (temp == null) {
				return;
			}
			// 分离子串
			String[] serverData = temp.split("]");
			IndexInfoBean indexBean;
			// 第一个是数组
			JSONArray array = new JSONArray(serverData[0] += "]");
			JSONObject status = new JSONObject(serverData[1]);
			if (!status.has("status")) {
				return;
			}
			if (!status.getString("status").equals("SUCCESS")) {
				Log.i(TAG, "status is not SUCCESS");
			}
			int length = array.length();

			System.out.println("本次 刷新 共有 " + length + "条消息");
			for (int i = 0; i < length; i++) {
				indexBean = new IndexInfoBean();
				JSONObject serverDataObj = array.getJSONObject(i);
				setObject(indexBean, serverDataObj);
				IndexFragment.refreshQueue.put(indexBean);
				IndexFragment.sendMessage("fresh", "refresh_data_completed");
			}
			IndexFragment.sendMessage("fresh", "refresh_data_completed");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 写完一次，关闭连接，释放服务器资源
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private static void internetLoad() {

		HttpURLConnection connection = null;
		JSONObject object = new JSONObject();

		try {
			// 取得一个连接 多 part的 connection
			connection = ConnectionHandler.getConnect(UrlSource.LOAD_STATUS, LaunchActivity.JSESSIONID);
			OutputStream out = connection.getOutputStream();
			object.put("rs_id", 0);
			object.put("before", false);

			Write.writeToHttp(out, object.toString().getBytes());

			System.out.println("load  服务器消息 -  " + connection.getResponseCode());

			String temp = Read.read(connection.getInputStream());
			if (temp == null) {
				return;
			}
			// 分离子串
			String[] serverData = temp.split("]");
			IndexInfoBean indexBean;
			// 第一个是数组
			JSONArray array = new JSONArray(serverData[0] += "]");
			JSONObject status = new JSONObject(serverData[1]);
			if (!status.has("status")) {
				return;
			}
			if (!status.getString("status").equals("SUCCESS")) {
				Log.i(TAG, "status is not SUCCESS");
			} else {
				int length = array.length();

				System.out.println("本次 加载 共有 " + length + "条消息");
				for (int i = 0; i < length; i++) {
					indexBean = new IndexInfoBean();
					JSONObject serverDataObj = array.getJSONObject(i);
					setObject(indexBean, serverDataObj);
					IndexFragment.loadQueue.put(indexBean);
				}
				IndexFragment.sendMessage("fresh", "load_data_completed");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 写完一次，关闭连接，释放服务器资源
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private static void setObject(IndexInfoBean bean, JSONObject obj) {

		if (obj.has("ID")) {
			bean.setId(obj.getInt("ID"));
		}
		if (obj.has("rs_id")) {
			bean.setRs_id(obj.getInt("rs_id"));
		}
		if (obj.has("detailPhoto")) {
			bean.setDetailPhoto(obj.getString("detailPhoto"));
		}
		if (obj.has("isLocated")) {
			bean.setIsLocated(obj.getString("isLocated"));
		}
		if (obj.has("sharesNumber")) {
			bean.setSharesNumber(obj.getInt("sharesNumber"));
		}
		if (obj.has("myWords")) {
			bean.setMyWords(obj.getString("myWords"));
		}
		if (obj.has("commentsNumber")) {
			bean.setCommentNumber(obj.getInt("commentsNumber"));
		}
		if (obj.has("likesNumber")) {
			bean.setLikeNumber(obj.getInt("likesNumber"));
		}
		if (obj.has("time")) {
			bean.setTime(obj.getString("time"));
		}
		if (obj.has("viewPhoto")) {
			bean.setViewPhoto(obj.getString("viewPhoto"));
		}
		if (obj.has("hasDetail")) {
			bean.setHasDetail(obj.getString("hasDetail"));
		}
		if(obj.has("isLocated") && Boolean.valueOf(obj.getString("isLocated"))){
			bean.setLocation(obj.getString("location"));
		}
		//        if (obj.has("location")) {
		//            bean.setLocation(obj.getJSONArray("location"));
		//        }
	}
}
