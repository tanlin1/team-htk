package com.htk.moment.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import utils.android.photo.CameraActivity;
import utils.android.photo.LocalPictureLibrary;
import utils.test.*;
import utils.view.IndexPullRefreshListView;
import utils.view.NotFilingViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * App 主页 所有关注人的动态 时间倒序（最近的在最顶端）顺序排列
 *
 * @author 谭林
 * @version 2014/11/2.
 */
public class UserMainCoreActivity extends FragmentActivity {

    // 侦听器发出的按钮飞入消息标识
	public final static int FLING_IN = 0;
	public final static int FLING_OUT = 1;

    private final static int INIT_COMPLETED = 2;
    private final static int PULL_TO_REFRESH = 3;
    private final static int LOAD_MORE = 4;
    private static int speed = 10;
    private final String TAG = "PullToRefreshListView";
    private final static int DELAY = 10;


	private RadioGroup mRadioGroup;
	private ArrayList<Fragment> fragments;
	private FragmentTabAdapter tabAdapter;



    // 菜单栏中间的按钮是否处于动画中
	private boolean buttonIsOnscreen;

	// 首页按钮
	private ImageView view_index;

	// 消息按钮
	private ImageView view_message;

	//
	private TextView logoName;

	// ImageView
	private View icon;

	// 搜索按钮
	private ImageView view_search;

	// 个人中心
	private ImageView view_me;

	// App 菜单栏中间按钮动画
	private Animation addButtonIn;

	private Animation addButtonOut;

//	private ListView listView;

//	private MyContentListViewAdapter listViewAdapter;

	/**
	 * 页面容器
	 * <p/>
	 * 跟传统的ViewPager不一样，它可以根据你的需要不水平滑动
	 */
	private NotFilingViewPager pages;

	// 照相机按钮（图标）
	private ImageView theCameraButton;

	// 图库 图标
	private ImageView thePictureButton;

	// 从屏幕左边飞入的相机按钮
	private View flingCameraButton;

	// 从屏幕右边飞入的图库按钮
	private View flingPictureButton;

	// 这两个按钮原始位置所在的布局
	private FrameLayout filingButtonLayout;

	// 相机按钮停止的X坐标
	private static int cameraMaxX = 0;

	// 图库按钮停止的X坐标
	private static int pictureMaxX = 0;

	// 窗口管理器，用它实现两个按钮从屏幕边缘出现
	private WindowManager windowManager;

	// 窗口布局参数
	private WindowManager.LayoutParams cameraParams;

	private WindowManager.LayoutParams pictureParams;

//	private VerticalViewPager mVerticalViewPager;


	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bundle data = msg.getData();
			int info = data.getInt("messageClass");
			switch (info) {
				case FLING_IN: {
					appear();
				}
				break;
				case INIT_COMPLETED: {
					//test();
				}
				break;
				case FLING_OUT: {
					disappear();
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.after_login_layout);
		initAll();
		widgetsListener();
		sendLayoutCompleted();
//		logoName = (TextView) findViewById(R.id.the_logo_name);
//		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/script_mt_bold.ttf");
//		logoName.setTypeface(font);
	}

	/**
	 * 边缘进入的动画出现
	 */
	private void appear() {

		if (cameraParams.x >= cameraMaxX || pictureParams.x <= pictureMaxX) {
			windowManager.removeView(flingCameraButton);
			windowManager.removeView(flingPictureButton);
			mHandler.removeMessages(FLING_IN);
			filingButtonLayout.setVisibility(View.VISIBLE);
			theCameraButton.setVisibility(View.VISIBLE);
			thePictureButton.setVisibility(View.VISIBLE);
			cameraParams.x = 0;
			pictureParams.x = 0;
			return;
		}
		windowManager.updateViewLayout(flingCameraButton, cameraParams);
		windowManager.updateViewLayout(flingPictureButton, pictureParams);
		cameraParams.x += speed++;
		pictureParams.x -= speed++;
		mHandler.sendMessageDelayed(createAMsg("messageClass", FLING_IN), DELAY);
	}

	private void sendInMessage() {

		mHandler.sendMessage(createAMsg("messageClass", FLING_IN));
	}
	private void sendOutMessage() {

		mHandler.sendMessage(createAMsg("messageClass", FLING_OUT));
	}

	/**
	 * 消失
	 */
	private void disappear() {

		filingButtonLayout.setVisibility(View.GONE);
		theCameraButton.setVisibility(View.GONE);
		thePictureButton.setVisibility(View.GONE);

	}

	/**
	 * 初始化相关控件
	 */
	public void initAll() {

		initWidgets();
//		initViewPager();
		initPlusButtonAnimal();
		initFlingButton();
		initFragment();
	}
	private void initFragment(){
		fragments = new ArrayList<Fragment>();
		fragments.add(new IndexFragment());
		fragments.add(new MessageFragment());
		fragments.add(new SearchFragment());
		fragments.add(new MeFragment());
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		ft.add(R.id.container, fragments.get(0));
		ft.commit();
	}

	int currentFragmentIndex = 0;
	/**
	 * set the current fragment for View
	 *
	 * @param index index of fragment
	 *              Note:the index begin 0
	 */
	private void setFragmentIndex(int index){

		if(index < 0 || index > 4){
			Log.w(TAG, "your index must select between zero and four!\n");
			return;
		}
		// 上一个frament
		Fragment lastFragment = getCurrentFragment();

		// 准备显示的fragment
		Fragment newFragment = fragments.get(index);
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		if(newFragment.isVisible()) {
			System.out.println("重复了---------------------------------------------------");
			return;
		}
		if (newFragment.isAdded()) {
			newFragment.onResume();
			ft.show(newFragment);
		} else {
			ft.add(R.id.container, newFragment);
			ft.show(newFragment);
		}
		ft.hide(lastFragment);
		ft.commit();
		currentFragmentIndex = index;
		System.out.println("---------------状态改变   " + currentFragmentIndex);
	}
	/**
	 * 得到上一次的Fragment，避免重复
	 *
	 * @return 上一次的Fragment
	 */
	private Fragment getCurrentFragment(){
		return fragments.get(currentFragmentIndex);
	}

	/**
	 * 加载动画布局
	 * <p/>
	 * “+” 按钮的动画
	 */
	private void initPlusButtonAnimal() {

		addButtonIn = AnimationUtils.loadAnimation(this, R.anim.plus_button_roate_open);
		addButtonOut = AnimationUtils.loadAnimation(this, R.anim.plus_button_roate_shutdown);
	}

	/**
	 * 初始化组件（控件）
	 * <p/>
	 * 从布局文件中找到相应的控件 以便对相应的事件进行响应
	 */
	private void initWidgets() {

		view_index = (ImageView) findViewById(R.id.index_index_image);
		view_message = (ImageView) findViewById(R.id.index_message_image);
		view_search = (ImageView) findViewById(R.id.index_search_image);
		view_me = (ImageView) findViewById(R.id.index_about_me_image);

		filingButtonLayout = (FrameLayout) findViewById(R.id.theAnimalLayout);

		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		flingCameraButton = LayoutInflater.from(this).inflate(R.layout.the_fling_camera, null);
		flingPictureButton = LayoutInflater.from(this).inflate(R.layout.the_fling_picture, null);

		theCameraButton = (ImageView) findViewById(R.id.the_camera_button);
		thePictureButton = (ImageView) findViewById(R.id.the_picture_button);

		icon = findViewById(R.id.index_plus_button_image);
	}

	/**
	 * 初始化ViewPager
	 * <p/>
	 * 完成页面加载
	 */
	private void initViewPager() {
		/**
		 * ViewPager 包含的页面
		 */
		List<View> pageList = new ArrayList<View>();

		//pages = (NotFilingViewPager) findViewById(R.id.multi_page);

		LayoutInflater inflater = LayoutInflater.from(this);

		pageList.add(inflater.inflate(R.layout.after_login_listview_layout, null));
		pageList.add(inflater.inflate(R.layout.message_index, null));
		pageList.add(inflater.inflate(R.layout.search_index, null));
		pageList.add(inflater.inflate(R.layout.user_home_index, null));


		pages.setAdapter(new myViewPagerAdapter(pageList));
		pages.setOnPageChangeListener(new MyOnPageChangeListener());
		currentItem = (LinearLayout) pages.findViewById(R.id.after_login_list_view);
		pages.setCurrentItem(0);
		// 限制水平滑动
		pages.enableHorizonScroll(false);
	}
	/**
	 * 通知UI线程，布局加载完成，可以对响应的控件进行事件侦听
	 */
	private void sendLayoutCompleted() {

		mHandler.sendMessageDelayed(createAMsg("messageClass", INIT_COMPLETED), DELAY);
	}

	private Message createAMsg(String key, int value) {

		Bundle data = new Bundle();
		Message msg = new Message();
		data.putInt(key, value);
		msg.setData(data);
		return msg;
	}

	private void widgetsListener() {

		icon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				openOrShutdownButton();
			}
		});


		theCameraButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				shutdown();
				startActivity(new Intent(UserMainCoreActivity.this, CameraActivity.class));
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});

		thePictureButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				shutdown();
				startActivity(new Intent(UserMainCoreActivity.this, LocalPictureLibrary.class));
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});

		view_index.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

//				view_index.setImageResource(R.drawable.home_after);
//				view_message.setImageResource(R.drawable.topic);
//				view_search.setImageResource(R.drawable.explore);
//				view_me.setImageResource(R.drawable.user);
				//pages.setCurrentItem(0, false);
				System.out.println("-----   0   -----");
				setFragmentIndex(0);
				shutdown();

			}
		});

		// 进入消息中心
		view_message.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

//				view_index.setImageResource(R.drawable.home);
//				view_message.setImageResource(R.drawable.topic_after);
//				view_search.setImageResource(R.drawable.explore);
//				view_me.setImageResource(R.drawable.user);
//				pages.setCurrentItem(1, false);
				System.out.println("-----   1   -----");
				setFragmentIndex(1);
				shutdown();
				//startActivity(new Intent());
			}
		});

		/** 拍照上传或者是选择照片上传
		 * 添加动画，扇形选项
		 */

		// 搜索联系人、热门动态
		view_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

//				view_index.setImageResource(R.drawable.home);
//				view_message.setImageResource(R.drawable.topic);
//				view_search.setImageResource(R.drawable.explore_after);
//				view_me.setImageResource(R.drawable.user);
//				pages.setCurrentItem(2, false);
				System.out.println("-----   2   -----");
				setFragmentIndex(2);
				shutdown();
				//startActivity(new Intent());
			}
		});
		// 进入个人中心
		view_me.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

//				view_index.setImageResource(R.drawable.home);
//				view_message.setImageResource(R.drawable.topic);
//				view_search.setImageResource(R.drawable.explore);
//				view_me.setImageResource(R.drawable.user_after);
//				pages.setCurrentItem(3, false);
				System.out.println("-----   3   -----");
				setFragmentIndex(3);
				shutdown();
			}
		});
	}

	private void initFlingButton() {

		cameraParams = getWindowParams("camera");
		pictureParams = getWindowParams("picture");
	}

	private void addWindowButton() {

		// 添加WindowView（不受父控件约束）
		windowManager.addView(flingCameraButton, cameraParams);
		windowManager.addView(flingPictureButton, pictureParams);
	}

	/**
	 * 从屏幕的下边飞入两个按钮
	 *
	 * @param tag 标志某个窗口动画文件
	 *
	 * @return 动画参数
	 */
	private WindowManager.LayoutParams getWindowParams(String tag) {

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				0, 0,
				WindowManager.LayoutParams.TYPE_TOAST,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.RGBA_8888);
		params.gravity = Gravity.LEFT | Gravity.TOP;
		int h = findViewById(R.id.bottomTarBar).getHeight();
//		int h = findViewById(R.id.tabs_rg).getHeight();
		Rect rect = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		params.y = rect.bottom - h * 2;
		if (tag == null) {
			Log.e("wrong", "It mustn't be give a par");
			return null;
		} else if (tag.equals("camera")) {
			cameraMaxX = (rect.right - h) / 2;
			params.x = 0;
		} else {
			pictureMaxX = (rect.right + h) / 2;
			params.x = rect.right;
		}
		return params;
	}

	/**
	 * 相机按钮出现或是消失
	 */
	private void openOrShutdownButton() {

		if (!buttonIsOnscreen) {
			icon.startAnimation(addButtonIn);
			initFlingButton();
			addWindowButton();
			sendInMessage();
		} else {
			icon.startAnimation(addButtonOut);
			sendOutMessage();
		}
		buttonIsOnscreen = !buttonIsOnscreen;
	}
	// 停止显示相机按钮
	private void shutdown() {

		if (buttonIsOnscreen) {
			icon.startAnimation(addButtonOut);
		}
		disappear();
		buttonIsOnscreen = false;
	}

	private class MyContentListViewAdapter extends BaseAdapter {

		private List<HashMap<String, Object>> listData;

		//视图容器
		private LayoutInflater listContainer;

		// 每一个动态在主页上显示的内容分，
		public final class ListItemsView {

			public ImageView photoHead;

			public TextView userName;

			public ImageView location;

			public TextView userAddress;

			public TextView dasyaAgo;
			public ImageView clockImage;

			public TextView photoDescribe;

			public ImageView showingPicture;

			/**
			 * 该照片被喜欢的人数
			 */

			public TextView likeSum;
			/**
			 * “Like” 提供点击进入查看那些人喜欢
			 */
			public TextView likeText;
			/**
			 * 该图片被评论的数量
			 */
			public TextView commentSum;
			/**
			 * “Comment”
			 */
			public TextView theCommentText;

			// 喜欢等图片按钮
			public ImageView loveHeartImage;
			public ImageView commentImage;
			public ImageView sharImage;
			public ImageView moreMenu;

		}

		public MyContentListViewAdapter(Context context, List<HashMap<String, Object>> content) {

			listContainer = LayoutInflater.from(context);
			this.listData = content;
		}

		@Override
		public int getCount() {

			return listData.size();
		}

		@Override
		public Object getItem(int position) {

			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ListItemsView listItemsView;

			if (convertView == null) {
				listItemsView = new ListItemsView();
				convertView = listContainer.inflate(R.layout.index_listview_content, null);
				listItemsView.photoHead = (ImageView) convertView.findViewById(R.id.index_head_photo_thumbnail);
				listItemsView.userName = (TextView) convertView.findViewById(R.id.userNameIndex);
				listItemsView.userName.setTextScaleX(1.2f);
				listItemsView.userAddress = (TextView) convertView.findViewById(R.id.address);
				listItemsView.showingPicture = (ImageView) convertView.findViewById(R.id.showingPicture);
				listItemsView.photoDescribe = (TextView) convertView.findViewById(R.id.index_photo_describe);

				// 设置控件集到convertView中
				convertView.setTag(listItemsView);
			} else {
				listItemsView = (ListItemsView) convertView.getTag();
			}

			HashMap<String, Object> map = listData.get(position);
			listItemsView.photoHead.setImageResource((Integer) map.get("photoHead"));
			listItemsView.userName.setText((String) map.get("userName"));
			listItemsView.userAddress.setText((String) map.get("userAddress"));
			listItemsView.showingPicture.setImageResource((Integer) map.get("userPicture"));
			listItemsView.photoDescribe.setText((String) map.get("explain"));

			return convertView;
		}
	}

	private List<HashMap<String, Object>> getListItems() {

		items = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("photoHead", R.drawable.head2);
		map.put("userName", "喜欢");
		map.put("userAddress", "好望角海域");
		map.put("userPicture", R.drawable.index_aother_user_picture);
		map.put("explain", "这里是用户文字描述");
		items.add(map);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("photoHead", R.drawable.head2);
		map2.put("userName", "不喜欢");
		map2.put("userAddress", "成都双流");
		map2.put("userPicture", R.drawable.index_aother_user_picture);
		map2.put("explain", "我说了很多？");
		items.add(map2);


		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("photoHead", R.drawable.application);
		map3.put("userName", "hellen");
		map3.put("userAddress", "北京");
		map3.put("userPicture", R.drawable.index_aother_user_picture);
		map3.put("explain", "我说了很多aassdfsfdgsdg2222sdasd");
		items.add(map3);
		return items;
	}

	/**
	 * 将页面添加到viewpager里面去
	 */
	private class myViewPagerAdapter extends PagerAdapter {

		private List<View> listView;
		int tagFlag = 1;

		public myViewPagerAdapter(List<View> listView) {

			this.listView = listView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			container.removeView(listView.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			View v = listView.get(position);
			v.setTag("tag" + tagFlag++);
			container.addView(v, 0);
			return listView.get(position);
		}

		@Override
		public int getCount() {

			return listView.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {

			return arg0 == arg1;
		}
	}

	public class MyOnPageChangeListener implements NotFilingViewPager.OnPageChangeListener {

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageSelected(final int arg0) {
			// 用户处于某一页
			switch (arg0) {
				case 0: {
					currentItem = (LinearLayout) pages.findViewById(R.id.after_login_list_view);
					test3();
				}
				break;
				case 3:{
					currentItem = (LinearLayout) pages.findViewById(R.id.user_home_index);
					if(currentItem == null){
						break;
					}
					initVerticalViewPager();
				}
				break;
			}
		}
	}

	private void initVerticalViewPager(){
		ArrayList<View> views = new ArrayList<View>();
		LayoutInflater inflater = LayoutInflater.from(this);
//		mVerticalViewPager = (VerticalViewPager) currentItem.findViewById(R.id.verticalViewPager);
//		views.add(inflater.inflate(R.layout.user_home1, null));
//		views.add(inflater.inflate(R.layout.user_home2, null));
////		mVerticalViewPager.setAdapter(new MyVerticalViewPagerAdapter(views));
//		mVerticalViewPager.setCurrentItem(0);


	}

	private class MyVerticalViewPagerAdapter extends utils.view.vertical.PagerAdapter{

		ArrayList<View> viewArrayList;
		public MyVerticalViewPagerAdapter(ArrayList<View> viewData){
			viewArrayList = viewData;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return true;
		}

		@Override
		public int getCount() {
			return viewArrayList.size();
		}

	}


	private LinearLayout currentItem;
	public void test() {

		currentItem = (LinearLayout) pages.findViewWithTag("tag1");

		if (currentItem == null) {
			System.out.println("currentItem is null");
			return;
		}
		//test4();
		test3();
	}

	private static List<HashMap<String, Object>> items = null;

	public static IndexPullRefreshListView indexPullListView;

	static MyContentListViewAdapter myContentListViewAdapter;

	private void test3() {
		indexPullListView = (IndexPullRefreshListView) currentItem.findViewById(R.id.index_pull_to_refresh_list_view);
		indexPullListView.setOnRefreshListener(new IndexPullRefreshListView.OnRefreshListener() {

			@Override
			public void refresh() {
				// Do work to refresh the list here.
				new GetDataTask(UserMainCoreActivity.this, PULL_TO_REFRESH).execute();
			}

			@Override
			public void loadMore() {
				new GetDataTask(UserMainCoreActivity.this, LOAD_MORE).execute();
			}
		});
		myContentListViewAdapter = new MyContentListViewAdapter(UserMainCoreActivity.this, getListItems());
		indexPullListView.setAdapter(myContentListViewAdapter);

	}

	private class GetDataTask extends AsyncTask<Void, Void, HashMap<String,Object>> {

		private Context context;
		private int index;

		public GetDataTask(Context context, int index) {

			this.context = context;
			this.index = index;
		}

		@Override
		protected HashMap<String,Object> doInBackground(Void... params) {
			// Simulates a background job.
			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			try {
				Thread.sleep(2000);
				dataMap.put("photoHead", R.drawable.head2);
				dataMap.put("userName", String.valueOf(name++));
				dataMap.put("userAddress", "好莱坞----");
				dataMap.put("userPicture", R.drawable.index_aother_user_picture);
				dataMap.put("explain", "用户文字描述");

			} catch (InterruptedException e) {
				Log.d(TAG, "Sleep Exception");
			}

			return dataMap;
		}

		@Override
		protected void onPostExecute(HashMap<String,Object> result) {
			if (index == PULL_TO_REFRESH) {
				// 将字符串“Added after refresh”添加到顶部
				//mListItems.addFirst("","Added after refresh...");
				items.add(0, result);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
				String date = format.format(new Date());
				// Call onRefreshComplete when the list has been refreshed.
				indexPullListView.onRefreshComplete(date);

			} else if (index == LOAD_MORE) {
				items.add(items.size(), result);
				indexPullListView.onLoadMoreComplete();
			}
//			myContentListViewAdapter.notifyDataSetChanged();
		}
		@Override
		protected void onCancelled(HashMap<String, Object> stringObjectHashMap) {
			super.onCancelled(stringObjectHashMap);
		}
	}
static int name = 10;
}