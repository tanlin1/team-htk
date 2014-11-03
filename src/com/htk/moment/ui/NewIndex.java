package com.htk.moment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/11/2.
 * @author 谭林
 * @version 1.0
 */
public class NewIndex extends Activity{

	// 多页面
	private ViewPager viewPager;

	// 页面布局文件
	private View indexLayout;
	private View messageLayout;
	private View searchLayout;
	private View meLayout;

	private TextView index;
	private TextView message;
	private TextView add;
	private TextView search;
	private TextView me;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.index);
		index = (TextView) findViewById(R.id.index);
		message = (TextView) findViewById(R.id.message);
		add = (TextView) findViewById(R.id.add);
		search = (TextView) findViewById(R.id.search);
		me = (TextView) findViewById(R.id.me);

		initViewPager();

	}
	private void jump(){
		// 进入首页
		index.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent());
			}
		});

		// 进入消息中心
		message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent());
			}
		});

		/** 拍照上传或者是选择照片上传
		 * 添加动画，扇形选项
		 */

		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent());
			}
		});

		// 搜索联系人、热门动态
		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent());
			}
		});
		// 进入个人中心
		me.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(NewIndex.this, UserHome.class));
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
	}

	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		List<View> listViews = new ArrayList<View>();

		LayoutInflater mInflater = LayoutInflater.from(this);
		//null：将所添加的页面不放入任何一个ViewGroup
		indexLayout = mInflater.inflate(R.layout.indexitem, null);
		messageLayout = mInflater.inflate(R.layout.message, null);
		searchLayout = mInflater.inflate(R.layout.search, null);
		meLayout = mInflater.inflate(R.layout.home,null);

		// 添加布局到当前布局
		listViews.add(indexLayout);
		listViews.add(messageLayout);
		listViews.add(searchLayout);
		listViews.add(meLayout);

		viewPager.setAdapter(new myViewPagerAdapter(listViews));
		//设置当前显示的是首页（顺序为add时候的顺序）
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		viewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//doSomething(event);
				return false;
			}
		});
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
	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
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
//				ImageButton back_from_notice = (ImageButton) indexLayout.findViewById(R.id.back_home_from_notice);
//				ImageButton back_from_contact = (ImageButton) contact.findViewById(R.id.back_home_from_contact);
//				back_from_contact.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						viewPager.setCurrentItem(1);
//					}
//				});
//				back_from_notice.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						viewPager.setCurrentItem(1);
//					}
//				});
				System.out.println(" 测试 完成 ！");
			}
		}
	}
}
