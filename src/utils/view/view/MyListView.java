package utils.view.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.htk.moment.ui.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 主页 listView
 * 显示动态
 * <p/>
 * Created by Administrator on 2014/11/20.
 */
public class MyListView extends ListView implements AbsListView.OnScrollListener {


    private static final int TAP_TO_REFRESH = 0;            //（未刷新）
    private static final int PULL_TO_REFRESH = 1;            // 下拉刷新
    private static final int RELEASE_TO_REFRESH = 2;        // 释放刷新
    private static final int REFRESHING = 3;                // 正在刷新
    private static final int TAP_TO_LOAD_MORE = 4;            // 未加载更多
    private static final int LOADING = 5;                    // 正在加载

    private OnScrollListener listViewOnScrollListener;                // 列表滚动监听器

    private OnRefreshListener onRefreshListener;                // 列表滚动监听器
    private LayoutInflater inflater;                        // 用于加载布局文件

    private RelativeLayout refreshHeaderLayout;                // 刷新视图(也就是头部那部分)
    private TextView refreshViewText;                        // 刷新提示文本
    private ImageView refreshArrow;                            // 刷新向上向下的那个图片
    private ProgressBar refreshViewProgressBar;                // 这里是圆形进度条
    private TextView refreshLastTime;                    // 最近更新的文本

    private RelativeLayout loadMoreLayout;                    // 加载更多
    private TextView loadMoreText;                            // 提示文本
    private ProgressBar loadProcessBar;                        // 加载更多进度条


    private int currentScrollState;                            // 当前滚动位置
    private int refreshState;                                // 刷新状态
    private int loadState;                                    // 加载状态

    private RotateAnimation pullAnimation;                    // 下拉动画
    private RotateAnimation reverseAnimation;                // 恢复动画

    private int refreshViewHeight;                            // 刷新视图高度
    private int refreshOriginalTopPadding;                    // 原始上部间隙
    private int lastMotionY;                                // 记录点击位置

    /**
     * Interface definition for a callback to be invoked when list should be
     * refreshed.
     * 接口定义一个回调方法当列表应当被刷新
     */
    public interface OnRefreshListener {
        /**
         * Called when the list should be refreshed.
         * 当列表应当被刷新是调用这个方法
         * <p/>
         * A call to {@link PullToRefreshListView #onRefreshComplete()} is
         * expected to indicate that the refresh has completed.
         */
        public void onRefresh();

        public void onLoadMore();
    }


    public MyListView(Context context) {

        super(context);
        initView(context);

    }

    public MyListView(Context context, AttributeSet attrs) {

        super(context, attrs);
        initView(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        initView(context);
    }


    private void initView(Context context) {
        // 参数：1.旋转开始的角度 2.旋转结束的角度 3. X轴伸缩模式 4.X坐标的伸缩值 5.Y轴的伸缩模式 6.Y坐标的伸缩值
        pullAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        pullAnimation.setInterpolator(new LinearInterpolator());
        pullAnimation.setDuration(250);                // 设置持续时间
        pullAnimation.setFillAfter(true);                // 动画执行完是否停留在执行完的状态

        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(250);
        reverseAnimation.setFillAfter(true);

        // 获取LayoutInflater实例对象
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        // 加载下拉刷新的头部视图
        refreshHeaderLayout = (RelativeLayout) inflater.inflate(
                R.layout.pull_to_refresh_header, this, false);
        refreshViewText =
                (TextView) refreshHeaderLayout.findViewById(R.id.pull_to_refresh_text);
        refreshArrow =
                (ImageView) refreshHeaderLayout.findViewById(R.id.pull_to_refresh_arrow);
        refreshViewProgressBar =
                (ProgressBar) refreshHeaderLayout.findViewById(R.id.pull_to_refresh_circle_process_bar);
        refreshLastTime =
                (TextView) refreshHeaderLayout.findViewById(R.id.last_time_refresh_text);

        loadMoreLayout = (RelativeLayout) inflater.inflate(R.layout.index_list_view_loadmore_footer, this, false);
        loadMoreText = (TextView) loadMoreLayout.findViewById(R.id.load_more_text);
        loadProcessBar = (ProgressBar) loadMoreLayout.findViewById(R.id.load_more_progress);

        refreshArrow.setMinimumHeight(50);        // 设置图片最小高度

        refreshOriginalTopPadding = refreshHeaderLayout.getPaddingTop();

        refreshState = TAP_TO_REFRESH;                // 初始刷新状态
        loadState = TAP_TO_LOAD_MORE;

        addHeaderView(refreshHeaderLayout);            // 增加头部视图
        addFooterView(loadMoreLayout);                // 增加尾部视图

        super.setOnScrollListener(this);

        measureView(refreshHeaderLayout);                // 测量顶部视图
        refreshViewHeight = refreshHeaderLayout.getMeasuredHeight();    // 得到视图的高度
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        setSelection(1);
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls.
     *
     * @param l The scroll listener.
     */
    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        listViewOnScrollListener = l;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    /**
     * Set a text to represent when the list was last updated.
     * 设置一个文本来表示最近更新的列表，显示的是最近更新列表的时间
     *
     * @param lastUpdated Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            refreshLastTime.setVisibility(View.VISIBLE);
            refreshLastTime.setText("更新于: " + lastUpdated);
        } else {
            refreshLastTime.setVisibility(View.GONE);
        }
    }

    private void applyHeaderPadding(MotionEvent ev) {
        final int historySize = ev.getHistorySize();

        // Workaround for getPointerCount() which is unavailable in 1.5
        // (it's always 1 in 1.5)
        int pointerCount = 1;
        try {
            Method method = MotionEvent.class.getMethod("getPointerCount");
            pointerCount = (Integer) method.invoke(ev);
        } catch (NoSuchMethodException e) {
            pointerCount = 1;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IllegalAccessException e) {
            System.err.println("unexpected " + e);
        } catch (InvocationTargetException e) {
            System.err.println("unexpected " + e);
        }

        for (int h = 0; h < historySize; h++) {
            for (int p = 0; p < pointerCount; p++) {
                if (refreshState == RELEASE_TO_REFRESH) {
                    int historicalY = 0;
                    try {
                        // For Android > 2.0
                        Method method = MotionEvent.class.getMethod(
                                "getHistoricalY", Integer.TYPE, Integer.TYPE);
                        historicalY = ((Float) method.invoke(ev, p, h)).intValue();
                    } catch (NoSuchMethodException e) {
                        // For Android < 2.0
                        historicalY = (int) (ev.getHistoricalY(h));
                    } catch (IllegalArgumentException e) {
                        throw e;
                    } catch (IllegalAccessException e) {
                        System.err.println("unexpected " + e);
                    } catch (InvocationTargetException e) {
                        System.err.println("unexpected " + e);
                    }

                    // Calculate the padding to apply, we divide by 1.7 to
                    // simulate a more resistant effect during pull.
                    int topPadding = (int) (((historicalY - lastMotionY)
                            - refreshViewHeight) / 1.7);

                    // 设置上、下、左、右四个位置的间隙间隙
                    refreshHeaderLayout.setPadding(
                            refreshHeaderLayout.getPaddingLeft(),
                            topPadding,
                            refreshHeaderLayout.getPaddingRight(),
                            refreshHeaderLayout.getPaddingBottom());
                }
            }
        }
    }

    /**
     * Sets the header padding back to original size.
     * 设置头部填充会原始大小
     */
    private void resetHeaderPadding() {
        refreshHeaderLayout.setPadding(
                refreshHeaderLayout.getPaddingLeft(),
                refreshOriginalTopPadding,
                refreshHeaderLayout.getPaddingRight(),
                refreshHeaderLayout.getPaddingBottom());
    }

    /**
     * Resets the header to the original state.
     * 重新设置头部为原始状态
     */
    private void resetHeader() {
        if (refreshState != TAP_TO_REFRESH) {
            refreshState = TAP_TO_REFRESH;
	        resetHeaderPadding();
	        refreshViewText.setVisibility(GONE);
	        // Clear the full rotation animation
            refreshArrow.clearAnimation();
            // Hide progress bar and arrow.
            refreshArrow.setVisibility(View.GONE);
            refreshViewProgressBar.setVisibility(View.GONE);
         }
    }

    /**
     * 重设ListView尾部视图为初始状态
     */
    private void resetFooter() {
        if (loadState != TAP_TO_LOAD_MORE) {
            loadState = TAP_TO_LOAD_MORE;
            // 进度条设置为不可见
            loadProcessBar.setVisibility(View.GONE);
            // 按钮的文本替换为“加载更多”
            loadMoreText.setText("加载更多");
        }
    }

    /**
     * 测量视图的大小
     *
     * @param child 头部
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
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

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int y = (int) event.getY();     // 获取点击位置的Y坐标
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:        // 手指抬起
                //
                if (getFirstVisiblePosition() == 0 && refreshState == RELEASE_TO_REFRESH) {

	                onRefresh();
	                setSelection(1);
                }
                if (getLastVisiblePosition() == (getCount() - 1)) {
                    onLoadMore();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                lastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
	            System.out.println("--------- move");

		        applyHeaderPadding(event);

                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // When the refresh view is completely visible, change the text to say
        // "Release to refresh..." and flip the arrow drawable.
        if (currentScrollState == SCROLL_STATE_TOUCH_SCROLL && refreshState != REFRESHING) {
	        if (firstVisibleItem == 0) {        // 如果第一个可见条目为0
                refreshArrow.setVisibility(View.VISIBLE);    // 让指示箭头变得可见
                /**如果头部视图相对与父容器的位置大于其自身高度+20或者头部视图的顶部位置>0,并且要在刷新状态不等于"释放以刷新"**/

                if ((refreshHeaderLayout.getBottom() > refreshViewHeight + 20 || refreshHeaderLayout.getTop() >= 0) && (refreshState != RELEASE_TO_REFRESH)) {

                    refreshViewText.setText(R.string.pull_to_refresh_release_label);        // 设置刷新文本为"Release to refresh..."
                    refreshArrow.clearAnimation();                    // 清除动画
                    refreshArrow.startAnimation(pullAnimation);    // 启动动画
                    refreshState = RELEASE_TO_REFRESH;              // 更改刷新状态为“释放以刷新"


                } else if (refreshHeaderLayout.getBottom() < refreshViewHeight + 20 && refreshState != PULL_TO_REFRESH) {

                    refreshViewText.setText(R.string.pull_to_refresh_pull_label);// 设置刷新文本为"Pull to refresh..."
                    //if (refreshState != TAP_TO_REFRESH) {

                        refreshArrow.clearAnimation();
                        refreshArrow.startAnimation(reverseAnimation);
                    //}
	                refreshState = PULL_TO_REFRESH;
                }
            } else {
                resetHeader();    // 重新设置头部为原始状态
            }
        } else if (currentScrollState == SCROLL_STATE_FLING && firstVisibleItem == 0) {
            // 变为停止状态
	        currentScrollState = SCROLL_STATE_IDLE;
	        resetHeader();
	        setSelection(1);
            System.out.println("------------------  滑动很快 处理  ");
        }

        if (listViewOnScrollListener != null) {
            listViewOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        currentScrollState = scrollState;
        if (getLastVisiblePosition() == (getCount() - 1)) {
            onLoadMore();
        }
        if (listViewOnScrollListener != null) {
            listViewOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    /**
     * 为刷新做准备*
     */
    public void prepareForRefresh() {
        resetHeaderPadding();
        refreshArrow.setVisibility(View.GONE);            // 去掉刷新的箭头
        // We need this hack, otherwise it will keep the previous drawable.
        refreshViewProgressBar.setVisibility(View.VISIBLE);    // 圆形进度条变为可见
        // Set refresh view text to the refreshing label
        refreshViewText.setText(R.string.pull_to_refresh_refreshing_label);
        refreshState = REFRESHING;
    }

    /**
     * 为加载更多做准备*
     */
    public void prepareForLoadMore() {
        loadProcessBar.setVisibility(View.VISIBLE);
        loadMoreText.setVisibility(VISIBLE);
        loadMoreText.setText("加载更多");
        loadState = LOADING;
    }

    public void onRefresh() {
	    System.out.println("on refresh----------------");
	    prepareForRefresh();
        if (onRefreshListener != null) {
            onRefreshListener.onRefresh();
        }
    }

    public void onLoadMore() {
        prepareForLoadMore();
        if (onRefreshListener != null) {
            onRefreshListener.onLoadMore();
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

        resetHeader();

        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        if (refreshHeaderLayout.getBottom() > 0) {
            invalidateViews();
            setSelection(1);
        }
    }

    public void onLoadMoreComplete() {
        resetFooter();
    }
}
