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
import android.widget.AbsListView.OnScrollListener;
import com.htk.moment.ui.R;

import java.util.HashMap;


public class NewPull extends ListView implements OnScrollListener {

	public static String TAG = "NewPull";


	private final static int PULL_TO_REFRESH = 0;       // 下拉过程
	private final static int RELEASE_TO_REFRESH = 1;    // 从下拉返回到不刷新的状态值
	private final static int REFRESHING = 2;            // 正在刷新
	private final static int TAP_TO_REFRESH = 3;        //刷新完成

	private final static int LOADING = 4;               //正在加载
	private static final int TAP_TO_LOAD_MORE = 5;            // 未加载更多

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private LayoutInflater inflater;

	// ListView头部下拉刷新的布局
	private RelativeLayout headerLayout;
	private TextView pullToRefreshText;
	private TextView lastRefreshTimeText;
	private ImageView pullToRefreshArrow;
	private ProgressBar pullToRefreshProcessBar;


	private RelativeLayout loadMoreFooterLayout;                // 加载更多
	private TextView mLoadMoreText;                            // 提示文本
	private ProgressBar mLoadMoreProgress;                    // 加载更多进度条

	// 定义头部下拉刷新的布局的高度
	private int headerContentHeight;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;
	private int refreshState;
	// 刷新的上一个状态
	private int lastRefreshState;
	private int loadMoreState;

	private boolean isBack;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecord;

	private OnRefreshListener refreshListener;

	private boolean enableRefresh;
	private boolean enableLoadMore;

	public NewPull(Context context) {

		super(context);
		init(context);
	}

	public NewPull(Context context, AttributeSet attrs) {

		super(context, attrs);
		init(context);
	}
	public NewPull(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context context) {

		inflater = LayoutInflater.from(context);

		headerLayout = (RelativeLayout) inflater.inflate(R.layout.pull_to_refresh_header, null);
		pullToRefreshText = (TextView) headerLayout.findViewById(R.id.pull_to_refresh_text);
		lastRefreshTimeText = (TextView) headerLayout.findViewById(R.id.last_time_refresh_text);
		pullToRefreshProcessBar = (ProgressBar) headerLayout.findViewById(R.id.pull_to_refresh_circle_process_bar);

		pullToRefreshArrow = (ImageView) headerLayout.findViewById(R.id.pull_to_refresh_arrow);
		// 设置下拉刷新图标的最小高度和宽度
		pullToRefreshArrow.setMinimumWidth(70);
		pullToRefreshArrow.setMinimumHeight(50);

		// 底部加载
		loadMoreFooterLayout = (RelativeLayout) inflater.inflate(R.layout.loadmore_footer, null);
		mLoadMoreText = (TextView) loadMoreFooterLayout.findViewById(R.id.load_more_text);
		mLoadMoreProgress = (ProgressBar) loadMoreFooterLayout.findViewById(R.id.load_more_progress);


		measureView(headerLayout);
		headerContentHeight = headerLayout.getMeasuredHeight();
		// 设置内边距，正好距离顶部为一个负的整个布局的高度，正好把头部隐藏
		headerLayout.setPadding(0, -1 * headerContentHeight, 0, 0);
		// 重绘一下
		headerLayout.invalidate();
		// 将下拉刷新的布局加入ListView的顶部
		addHeaderView(headerLayout);
		addFooterView(loadMoreFooterLayout);
		// 设置滚动监听事件
		setOnScrollListener(this);

		// 设置箭头旋转动画事件
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		// 一开始的状态就是不刷新,不加载数据
		refreshState = TAP_TO_REFRESH;
		loadMoreState = TAP_TO_LOAD_MORE;
		lastRefreshState = refreshState;

		// 是否正在刷新
		enableRefresh = false;
		enableLoadMore = false;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (view.getLastVisiblePosition() == view.getCount() - 1) {
			enableLoadMore = true;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		if (firstVisibleItem == 0) {
			enableRefresh = true;
		} else {
			enableRefresh = false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int lastY = (int) ev.getY();

		switch (ev.getAction()){
			case MotionEvent.ACTION_UP :
				// 屏幕首页，且不是正在刷新状态
				if((getFirstVisiblePosition() == 0) && (refreshState != REFRESHING)){

				}

			break;
		}


		return super.onTouchEvent(ev);
	}

	private void changeRecordState(int tempY) {

		if (!isRecord) {
			isRecord = true;
			startY = tempY;
		}
	}
	/**
	 * 手指松开屏幕，有可能刷新或者加载更多数据
	 */
	private void itShouldLoadSome() {

		if (refreshState != REFRESHING && refreshState != LOADING) {
			if (refreshState == PULL_TO_REFRESH) {
				refreshState = TAP_TO_REFRESH;
				changeHeaderViewByState();
			}
			if (refreshState == RELEASE_TO_REFRESH) {
				refreshState = REFRESHING;
				onRefresh();
				changeHeaderViewByState();
				enableLoadMore = false;
			}
		}
		if (enableLoadMore) {
			enableRefresh = false;
			loadMoreState = LOADING;
			onLoadMore();
		}
	}

	private void doFingerMove(int lastY) {

		if (refreshState != REFRESHING && isRecord && refreshState != LOADING) {
			// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
			// 可以松手去刷新了
			if (refreshState == RELEASE_TO_REFRESH) {
				setSelection(0);
				// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
				if (((lastY - startY) / RATIO < headerContentHeight)// 由松开刷新状态转变到下拉刷新状态
						&& (lastY - startY) > 0) {
					refreshState = PULL_TO_REFRESH;
					changeHeaderViewByState();
				}
				// 一下子推到顶了
				else if (lastY - startY <= 0) {// 由松开刷新状态转变到done状态
					refreshState = TAP_TO_REFRESH;
					changeHeaderViewByState();
				}
			}
			// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
			if (refreshState == PULL_TO_REFRESH) {
				setSelection(0);
				// 下拉到可以进入RELEASE_TO_REFRESH的状态
				if ((lastY - startY) / RATIO >= headerContentHeight) {// 由done或者下拉刷新状态转变到松开刷新
					refreshState = RELEASE_TO_REFRESH;
					isBack = true;
					changeHeaderViewByState();
				}
				// 上推到顶了
				else if (lastY - startY <= 0) {// 由DOne或者下拉刷新状态转变到done状态
					refreshState = TAP_TO_REFRESH;
					changeHeaderViewByState();
				}
			}
			// done状态下
			if (refreshState == TAP_TO_REFRESH) {
				if (lastY - startY > 0) {
					refreshState = PULL_TO_REFRESH;
					changeHeaderViewByState();
				}
			}
			// 更新headView的size
			if (refreshState == PULL_TO_REFRESH) {
				headerLayout.setPadding(0, -1 * headerContentHeight + (lastY - startY) / RATIO, 0, 0);

			}
			// 更新headView的paddingTop
			if (refreshState == RELEASE_TO_REFRESH) {
				headerLayout.setPadding(0, (lastY - startY) / RATIO - headerContentHeight, 0, 0);
			}
		}
	}


	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {

		switch (refreshState) {
			case RELEASE_TO_REFRESH:
				pullToRefreshProcessBar.setVisibility(View.GONE);
				pullToRefreshArrow.setVisibility(View.VISIBLE);
				pullToRefreshText.setVisibility(View.VISIBLE);
				lastRefreshTimeText.setVisibility(View.VISIBLE);

				pullToRefreshArrow.clearAnimation();// 清除动画
				pullToRefreshArrow.startAnimation(animation);// 开始动画效果
				pullToRefreshText.setText("松开刷新");
				break;
			case PULL_TO_REFRESH:
				pullToRefreshProcessBar.setVisibility(View.GONE);
				pullToRefreshText.setVisibility(View.VISIBLE);
				lastRefreshTimeText.setVisibility(View.VISIBLE);
				pullToRefreshArrow.clearAnimation();
				pullToRefreshArrow.setVisibility(View.VISIBLE);
				// 是由RELEASE_To_REFRESH状态转变来的
				if (isBack) {
					isBack = false;
					pullToRefreshArrow.clearAnimation();
					pullToRefreshArrow.startAnimation(reverseAnimation);
					pullToRefreshText.setText("下拉刷新");
				} else {
					pullToRefreshText.setText("下拉刷新");
				}
				break;

			case REFRESHING:
				headerLayout.setPadding(0, 0, 0, 0);

				pullToRefreshProcessBar.setVisibility(View.VISIBLE);
				pullToRefreshArrow.clearAnimation();
				pullToRefreshArrow.setVisibility(View.GONE);
				pullToRefreshText.setText("正在刷新...");
				lastRefreshTimeText.setVisibility(View.VISIBLE);
				break;
			case TAP_TO_REFRESH:
				headerLayout.setPadding(0, -1 * headerContentHeight, 0, 0);

				pullToRefreshProcessBar.setVisibility(View.GONE);
				pullToRefreshArrow.clearAnimation();
				pullToRefreshArrow.setImageResource(R.drawable.indicator_arrow);
				pullToRefreshText.setText("下拉刷新");
				lastRefreshTimeText.setVisibility(View.VISIBLE);
				break;
		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	private void measureView(View child) {

		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
		int lpHeight = params.height;
		int childHeightSpec;

		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {

		this.refreshListener = refreshListener;
		enableRefresh = true;
	}


	/** 为刷新做准备* */
	public void prepareForRefresh() {

		pullToRefreshArrow.setVisibility(View.GONE);            // 去掉刷新的箭头
		// We need this hack, otherwise it will keep the previous drawable.
		pullToRefreshProcessBar.setVisibility(View.VISIBLE);    // 圆形进度条变为可见

		// Set refresh view text to the refreshing label
		pullToRefreshText.setText(R.string.pull_to_refresh_refreshing_label);

		refreshState = REFRESHING;
	}

	/** 为加载更多做准备* */
	public void prepareForLoadMore() {

		mLoadMoreProgress.setVisibility(View.VISIBLE);
		mLoadMoreText.setVisibility(VISIBLE);
		mLoadMoreText.setText("正在加载");
		loadMoreState = LOADING;
	}

	public void onRefresh() {

		Log.d(TAG, "onRefresh");
		prepareForRefresh();
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	public void onLoadMore() {

		prepareForLoadMore();
		Log.d(TAG, "onLoadMore");
		if (refreshListener != null) {
			refreshListener.onLoadMore();
		}
	}

	/**
	 * Set a text to represent when the list was last updated. 设置一个文本来表示最近更新的列表，显示的是最近更新列表的时间
	 *
	 * @param lastUpdated Last updated at.
	 */
	public void setLastUpdated(CharSequence lastUpdated) {

		if (lastUpdated != null) {
			lastRefreshTimeText.setVisibility(View.VISIBLE);
			lastRefreshTimeText.setText("更新于: " + lastUpdated);
		} else {
			lastRefreshTimeText.setVisibility(View.GONE);
		}
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 *
	 * @param lastUpdated Last updated at.
	 */
	public void onRefreshComplete(CharSequence lastUpdated) {

		setLastUpdated(lastUpdated);    // 显示更新时间
		onRefreshComplete();
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 */
	public void onRefreshComplete() {

		Log.d(TAG, "onRefreshComplete");
		resetHeader();
		// If refresh view is visible when loading completes, scroll down to
		// the next item.
		if (headerLayout.getBottom() > 0) {
			invalidateViews();
			setSelection(1);
		}
	}

	public void onLoadMoreComplete(HashMap<String, Object> value) {

		Log.d(TAG, "onLoadMoreComplete");
		resetFooter();
	}

	public void onLoadMoreComplete() {

		Log.d(TAG, "onLoadMoreComplete");
		resetFooter();
		invalidateViews();

	}


	/**
	 * Resets the header to the original state. 重新设置头部为原始状态
	 */
	private void resetHeader() {

		if (refreshState != TAP_TO_REFRESH) {
			refreshState = TAP_TO_REFRESH;
			changeHeaderViewByState();
			// Set refresh view text to the pull label
			// Replace refresh drawable with arrow drawable
			pullToRefreshArrow.setImageResource(R.drawable.indicator_arrow);
			// Clear the full rotation animation
			pullToRefreshArrow.clearAnimation();
			// Hide progress bar and arrow.
			pullToRefreshArrow.setVisibility(View.GONE);
			pullToRefreshProcessBar.setVisibility(View.GONE);
		}
	}

	/**
	 * 重设ListView尾部视图为初始状态
	 */
	private void resetFooter() {

		if (loadMoreState != TAP_TO_LOAD_MORE) {
			loadMoreState = TAP_TO_LOAD_MORE;
			// 进度条设置为不可见
			mLoadMoreProgress.setVisibility(View.GONE);
			// 按钮的文本替换为“加载更多”
			mLoadMoreText.setVisibility(GONE);
			mLoadMoreText.setText("加载更多");
		}
	}


	public interface OnRefreshListener {

		/**
		 * Called when the list should be refreshed. 当列表应当被刷新是调用这个方法
		 */
		public void onRefresh();

		/**
		 * 当用户需要加载更多数据的时候。
		 * <p/>
		 * 注：更多数据不一定都是本地数据，因为本地的数据不会很多。 当用户需要，应该是向服务请求数据，与刷新类似，从服务器获取数据。
		 */

		public void onLoadMore();
	}


}
