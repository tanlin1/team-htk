package com.htk.moment.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import utils.android.photo.CameraActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * App 主页 所有关注人的动态 时间倒序（最近的在最顶端）顺序排列
 *
 * @author 谭林
 * @version 2014/11/2.
 */
public class NewIndex extends Activity {

	private boolean areButtonsShowing;

	private ViewGroup composerButtonsWrapper;

	private ImageView view_index;

	private ImageView view_message;

	// ImageView
	private View button;

	private View icon;

	private ImageView view_search;

	private ImageView view_me;

	private View view_plus_button;

	// 动画
	private Animation addButtonIn;

	private Animation addButtonOut;

	private ListView listView;

	private MyContentListViewAdaper listViewAdaper;


	private TextView userName;

	private TextView userAddress;

	private TextView focus;

	private ImageView userPicture;

	private TextView explain;

	private ImageView share;

	private ImageView comment;

	private ImageView good;


	private ImageView theCameraButton;

	private ImageView thePictureBitton;


	private ImageView theFillingView;

	private View fillingCameraButton;

	private View fillingPictureButton;

	private FrameLayout theAnimalLiner;

	private static int cameraMaxX = 0;

	private static int pictureMaxX = 0;

	private WindowManager mWindowMgr;

	private WindowManager.LayoutParams cameraParams;

	private WindowManager.LayoutParams pictureParams;

	public final static int LEFT_IN = 0;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage (Message msg) {

			switch (msg.what) {
				case LEFT_IN: {
					if (cameraParams.x >= cameraMaxX || pictureParams.x <= pictureMaxX) {
						mWindowMgr.removeView(fillingCameraButton);
						mWindowMgr.removeView(fillingPictureButton);
						removeMessages(LEFT_IN);
						theAnimalLiner.setVisibility(View.VISIBLE);
						theCameraButton.setVisibility(View.VISIBLE);
						thePictureBitton.setVisibility(View.VISIBLE);
						return;
					}
					mWindowMgr.updateViewLayout(fillingCameraButton, cameraParams);
					mWindowMgr.updateViewLayout(fillingPictureButton, pictureParams);
					cameraParams.x += 10;
					pictureParams.x -= 10;
					sendMessageDelayed(obtainMessage(LEFT_IN), 10);
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate (Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.index);
		init();
		widgetsListener();
	}

	private void disappear () {

		theAnimalLiner.setVisibility(View.GONE);
		theCameraButton.setVisibility(View.GONE);
		thePictureBitton.setVisibility(View.GONE);
	}

	/**
	 * 初始化相关控件
	 */
	public void init () {

		initWidgets();
		initViewPager();
		initPlusButtonAnimal();
	}

	/**
	 * 加载动画布局
	 * <p/>
	 * “+” 按钮的动画
	 */
	private void initPlusButtonAnimal () {

		addButtonIn = AnimationUtils.loadAnimation(this, R.anim.plus_button_roate_open);
		addButtonOut = AnimationUtils.loadAnimation(this, R.anim.plus_button_roate_shutdown);
	}

	/**
	 * 初始化组件（控件）
	 * <p/>
	 * 从布局文件中找到相应的控件 以便对相应的事件进行响应
	 */
	private void initWidgets () {

		theFillingView = (ImageView) findViewById(R.id.filling_view);

		view_index = (ImageView) findViewById(R.id.index_index_image);
		view_message = (ImageView) findViewById(R.id.index_message_image);
		view_search = (ImageView) findViewById(R.id.index_search_image);
		view_me = (ImageView) findViewById(R.id.index_about_me_image);

		theAnimalLiner = (FrameLayout) findViewById(R.id.theAnimalLiner);

		mWindowMgr = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		fillingCameraButton = LayoutInflater.from(this).inflate(R.layout.window, null);
		fillingPictureButton = LayoutInflater.from(this).inflate(R.layout.window2, null);

		theCameraButton = (ImageView) findViewById(R.id.the_camera_button);
		thePictureBitton = (ImageView) findViewById(R.id.the_picture_button);

		button = findViewById(R.id.the_plus_button);
		icon = findViewById(R.id.index_plus_button_image);
	}

	/**
	 * 初始化ViewPager
	 * <p/>
	 * 完成页面加载
	 */
	private void initViewPager () {
		/**
		 * ViewPager 包含的页面
		 */
		List<View> pageList = new ArrayList<View>();

		pages = (ViewPager) findViewById(R.id.multi_page);

		LayoutInflater inflater = LayoutInflater.from(this);

		pageList.add(inflater.inflate(R.layout.newindex, null));

		pageList.add(inflater.inflate(R.layout.home, null));

		pages.setAdapter(new myViewPagerAdapter(pageList));
		pages.setCurrentItem(0);
	}

	private ViewPager pages;


	private void widgetsListener () {

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {
				// 添加WindowView（不受父控件约束）
				cameraParams = getWindowParams("camera");
				pictureParams = getWindowParams("picture");

				mWindowMgr.addView(fillingCameraButton, cameraParams);
				mWindowMgr.addView(fillingPictureButton, pictureParams);
				mHandler.sendEmptyMessage(LEFT_IN);
				// 旋转
				makeYourChoise();
			}
		});

		theCameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {

				disappear();
				System.out.println("Im running now!");
			}
		});

		thePictureBitton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {

				System.out.println("pic running now !");
			}
		});
		pages.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch (View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					System.out.println("不能滑动！！！！");
					return true;
				}
				return false;
			}
		});

		menuListener();

	}

	/**
	 * @param tag 标志某个窗口动画文件
	 *
	 * @return 动画参数
	 */
	private WindowManager.LayoutParams getWindowParams (String tag) {

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				0, 0,
				WindowManager.LayoutParams.TYPE_TOAST,
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
				PixelFormat.RGBA_8888);
		params.gravity = Gravity.LEFT | Gravity.TOP;

		int h = findViewById(R.id.bottomTarBar).getHeight();
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


	private void menuListener () {
		// 进入首页

		view_index.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {

				view_index.setImageResource(R.drawable.home_after);
				view_message.setImageResource(R.drawable.topic);
				view_search.setImageResource(R.drawable.explore);
				view_me.setImageResource(R.drawable.user);
			}
		});


		// 进入消息中心
		view_message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {

				view_index.setImageResource(R.drawable.home);
				view_message.setImageResource(R.drawable.topic_after);
				view_search.setImageResource(R.drawable.explore);
				view_me.setImageResource(R.drawable.user);
				//startActivity(new Intent());
			}
		});

		/** 拍照上传或者是选择照片上传
		 * 添加动画，扇形选项
		 */

		// 搜索联系人、热门动态
		view_search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {

				view_index.setImageResource(R.drawable.home);
				view_message.setImageResource(R.drawable.topic);
				view_search.setImageResource(R.drawable.explore_after);
				view_me.setImageResource(R.drawable.user);
				//startActivity(new Intent());
			}
		});
		// 进入个人中心
		view_me.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {

				view_index.setImageResource(R.drawable.home);
				view_message.setImageResource(R.drawable.topic);
				view_search.setImageResource(R.drawable.explore);
				view_me.setImageResource(R.drawable.user_after);


			}
		});
	}

	private void startListener () {

		int count = composerButtonsWrapper.getChildCount();
		final Intent intentToCamera = new Intent(NewIndex.this, CameraActivity.class);
		for (int i = 0; i < 2; i++) {
			View hide = composerButtonsWrapper.getChildAt(i);
			//HideImageButton hide = (HideImageButton) composerButtonsWrapper.getChildAt(i);
			switch (hide.getId()) {
				case R.id.the_camera_button: //照相机
					hide.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick (View v) {

							intentToCamera.putExtra("what", "camera");
							startActivity(intentToCamera);
							makeYourChoise();
						}
					});
					break;
				case R.id.the_picture_button: // 打开图库
					hide.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick (View v) {

							intentToCamera.putExtra("what", "picture");
							startActivity(intentToCamera);
							makeYourChoise();
						}
					});
					break;
			}
		}
	}

	private void makeYourChoise () {

		if (!areButtonsShowing) {
			//ButtonAnimationSet.startAnimations(composerButtonsWrapper, InOutAnimation.Direction.IN);
			icon.startAnimation(addButtonIn);
		} else {
			//ButtonAnimationSet.startAnimations(composerButtonsWrapper, InOutAnimation.Direction.OUT);
			icon.startAnimation(addButtonOut);
		}
		areButtonsShowing = !areButtonsShowing;
	}

	private class MyContentListViewAdaper extends BaseAdapter {

		private Context context;

		private List<HashMap<String, Object>> listdata;


		private LayoutInflater listContainer;           //视图容器

		public final class LiStItemsView {

			public ImageView photoHead;

			public TextView userName;

			public TextView userAddress;

			public ImageView userPicture;

			public TextView explain;

			public TextView share;

			public TextView comment;
		}

		public MyContentListViewAdaper (Context context, List<HashMap<String, Object>> content) {

			this.context = context;
			listContainer = LayoutInflater.from(context);
			this.listdata = content;
		}

		@Override
		public int getCount () {

			return listdata.size();
		}

		@Override
		public Object getItem (int position) {

			return listdata.get(position);
		}

		@Override
		public long getItemId (int position) {

			return position;
		}

		@Override
		public View getView (int position, View convertView, ViewGroup parent) {

			LiStItemsView listItemsView = null;
			if (convertView == null) {
				listItemsView = new LiStItemsView();
				convertView = listContainer.inflate(R.layout.indexcontent, null);
				listItemsView.photoHead = (ImageView) convertView.findViewById(R.id.index_head_photo_thumbnail);
				listItemsView.userName = (TextView) convertView.findViewById(R.id.userNameIndex);
				listItemsView.userAddress = (TextView) convertView.findViewById(R.id.address);
				listItemsView.userPicture = (ImageView) convertView.findViewById(R.id.userPicture);
				listItemsView.explain = (TextView) convertView.findViewById(R.id.thePictureExplain);

				// 设置控件集到convertView中
				convertView.setTag(listItemsView);
			} else {
				listItemsView = (LiStItemsView) convertView.getTag();
			}

			HashMap<String, Object> map = listdata.get(position);
			listItemsView.photoHead.setImageResource((Integer) map.get("photoHead"));
			listItemsView.userName.setText((String) map.get("userName"));
			listItemsView.userAddress.setText((String) map.get("userAddress"));
			listItemsView.userPicture.setImageResource((Integer) map.get("userPicture"));
			listItemsView.explain.setText((String) map.get("explain"));
			//
			//

			return convertView;
		}
	}

	private List<HashMap<String, Object>> getListItems () {

		List<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("photoHead", R.drawable.me);
		map.put("userName", "喜欢");
		map.put("userAddress", "好望角海域");
		map.put("userPicture", R.drawable.howlay);
		map.put("explain", "我说了什么吗？");
		items.add(map);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("photoHead", R.drawable.head2);
		map2.put("userName", "不喜欢");
		map2.put("userAddress", "成都双流");
		map2.put("userPicture", R.drawable.grass);
		map2.put("explain", "我说了很多？");
		items.add(map2);


		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("photoHead", R.drawable.head3);
		map3.put("userName", "hellen");
		map3.put("userAddress", "北京");
		map3.put("userPicture", R.drawable.grass);
		map3.put("explain", "我说了很多aassdfsfdgsdg2222sdasd");
		items.add(map3);
		//
		//
		return items;
	}

	/**
	 * 将页面添加到viewpager里面去
	 */
	private class myViewPagerAdapter extends PagerAdapter {

		private List<View> listView;

		public myViewPagerAdapter (List<View> listView) {

			this.listView = listView;
		}

		@Override
		public void destroyItem (ViewGroup container, int position, Object object) {

			container.removeView(listView.get(position));
		}

		@Override
		public Object instantiateItem (ViewGroup container, int position) {

			container.addView(listView.get(position), 0);
			return listView.get(position);
		}

		@Override
		public int getCount () {

			return listView.size();
		}

		@Override
		public boolean isViewFromObject (View arg0, Object arg1) {

			return arg0 == arg1;
		}

	}

	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrolled (int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged (int arg0) {

		}

		@Override
		public void onPageSelected (final int arg0) {
			// 用户当前在 ViewPager 首页（ 或者 末页)
		}

	}

}
