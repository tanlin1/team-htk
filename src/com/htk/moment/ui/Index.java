package com.htk.moment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.*;
import android.widget.ImageButton;
import utils.android.photo.ImageLoader;
import utils.android.photo.CameraActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2014/7/19.
 */
public class Index extends Activity {

	//
	public static  ImageLoader imageLoader;

	private ViewPager viewPager;//页卡内容

	private int firstY; //出现在屏幕上的第一个触点
	private int secondY; //出现在屏幕上的第二个触点

	// 布局文件
	private View face;
	private View notice;
	private View contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.index_interface);
		imageLoader = ImageLoader.getInstance(this);

		// 初始化ViewPager
		initViewPager();
		// 初始化按钮（face布局中的按钮）
		initButton(face);
	}

	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		List<View> listViews = new ArrayList<View>();

		LayoutInflater mInflater = LayoutInflater.from(this);
		//null：将所添加的页面不放入任何一个ViewGroup
		face = mInflater.inflate(R.layout.face, null);
		notice = mInflater.inflate(R.layout.notice, null);
		contact = mInflater.inflate(R.layout.contact, null);

		// 添加布局到当前布局
		listViews.add(notice);
		listViews.add(face);
		listViews.add(contact);

		viewPager.setAdapter(new myViewPagerAdapter(listViews));
		//设置当前显示的是第二个页面（顺序为add时候的顺序）
		viewPager.setCurrentItem(1);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		viewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				doSomething(event);
				return false;
			}
		});
	}

	/**
	 * @param v 一个页面
	 */
	private void initButton(View v) {
		ImageButton button_to_notice = (ImageButton) v.findViewById(R.id.home_to_notice);
		ImageButton button_to_hot = (ImageButton) v.findViewById(R.id.home_to_hot);
		ImageButton button_to_index = (ImageButton) v.findViewById(R.id.home_to_index);
		ImageButton button_to_contact = (ImageButton) v.findViewById(R.id.home_to_contact);

		button_to_notice.setOnClickListener(new MyOnClickListener(0));
		button_to_hot.setOnClickListener(new MyOnClickListener(1));
		button_to_index.setOnClickListener(new MyOnClickListener(2));
		button_to_contact.setOnClickListener(new MyOnClickListener(3));
	}

	/**
	 *
	 * @param event 动作事件
	 */
	private void doSomething(MotionEvent event) {
		int action = event.getAction();

		switch (action) {
			case MotionEvent.ACTION_POINTER_1_UP:
				//考虑到用户可能不是两个手指同时离开屏幕
				openCameraOrPicture((int) event.getY(0), (int) event.getY(1));
				break;
			case MotionEvent.ACTION_POINTER_2_DOWN:
				//第一个点的纵坐标
				//先只考虑纵坐标
				firstY = (int) event.getY(0);
				//第二个点的纵坐标
				secondY = (int) event.getY(1);

				break;
			case MotionEvent.ACTION_POINTER_2_UP:
				//或者在第一个手指抬起的时候出发
				openCameraOrPicture((int) event.getY(0), (int) event.getY(1));
				break;
		}
	}

	/**
	 * 打开相册或者是相机，根据参数决定
	 *
	 * @param lastPositionY_1 第一个点最后一次在屏幕上的位置
	 * @param lastPositionY_2 第二个点最后一次在屏幕上的位置
	 */
	private void openCameraOrPicture(int lastPositionY_1, int lastPositionY_2) {
		//开启压缩线程
		Intent open = new Intent(this, CameraActivity.class);

		//双指向下滑动
		if (lastPositionY_1 - firstY > 20 && lastPositionY_2 - secondY > 20) {
			//打开照相机
			open.putExtra("what", "camera");
		}
		if (firstY - lastPositionY_1 > 20 && secondY - lastPositionY_2 > 20) {
			//打开图库
			open.putExtra("what", "picture");
			imageLoader.enable();
		}
		//开启线程（读取图片）
		startActivity(open);
	}
	//主界面的按钮触发
	private class MyOnClickListener implements View.OnClickListener {
		private int index = 0;
		public MyOnClickListener(int i) {
			index = i;
		}
		public void onClick(View v) {
			//
			switch (index) {
				case 0: // 当前界面上有四个控件，第 1个控件被点击
					viewPager.setCurrentItem(index);
					break;
				case 1: // 第 2 个按钮被点击
					//打开热门
					startActivity(new Intent(Index.this, HotActivity.class));
					overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
					break;
				case 2: // 第 3 个按钮被点击
					//打开主页
					startActivity(new Intent(Index.this, UserHome.class));
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					break;
				case 3: // 第 4 个按钮被点击
					viewPager.setCurrentItem(index);
					break;
			}
		}
	}

	/**
	 * 页面适配器
	 */
	private class myViewPagerAdapter extends PagerAdapter {
		private List<View> listView;

		public myViewPagerAdapter(List<View> listView) {
			this.listView = listView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(listView.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(listView.get(position), 0);
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

	//页面改变侦听
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageSelected(final int arg0) {
			// 用户当前在 ViewPager 首页（ 或者 末页)
			if (arg0 == 0 || arg0 == 2) {
				ImageButton back_from_notice = (ImageButton) notice.findViewById(R.id.back_home_from_notice);
				ImageButton back_from_contact = (ImageButton) contact.findViewById(R.id.back_home_from_contact);
				back_from_contact.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						viewPager.setCurrentItem(1);
					}
				});
				back_from_notice.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						viewPager.setCurrentItem(1);
					}
				});
			}
		}
	}
}