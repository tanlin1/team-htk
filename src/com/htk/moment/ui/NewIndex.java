package com.htk.moment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import utils.android.photo.CameraActivity;
import utils.view.animation.ButtonAnimationSet;
import utils.view.animation.InOutAnimation;
import utils.view.view.HideImageButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2014/11/2.
 *
 * @author 谭林
 * @version 1.0
 */
public class NewIndex extends Activity {

	private boolean areButtonsShowing;
	private ViewGroup composerButtonsWrapper;


	private TextView index;
	private TextView message;
	// ImageView
	private View button;
	private View icon;

	private TextView search;
	private TextView me;

	// 动画
	private Animation addButtonIn;
	private Animation addButtonOut;

	private ListView content;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.index);
		LayoutInflater listInflater = getLayoutInflater();

		index = (TextView) findViewById(R.id.index);
		message = (TextView) findViewById(R.id.message);

		composerButtonsWrapper = (ViewGroup) findViewById(R.id.composer_buttons_wrapper);

		button = findViewById(R.id.composer_buttons_show_hide_button);
		icon = findViewById(R.id.composer_buttons_show_hide_button_icon);

		addButtonIn = AnimationUtils.loadAnimation(this, R.anim.rotate_story_add_button_in);
		addButtonOut = AnimationUtils.loadAnimation(this, R.anim.rotate_story_add_button_out);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleComposerButtons();
			}
		});

		search = (TextView) findViewById(R.id.search);
		me = (TextView) findViewById(R.id.me);
//		content = (ListView) findViewById(R.id.indexListView);
//		listInflater.inflate(R.layout.indexcontent, null);
//
//		System.out.println("----------");
//		ImageView imageView = (ImageView) content.findViewById(R.id.headPhotoIndex);
//		imageView.setImageResource(R.drawable.image_default);
//		System.out.println("----------");

		toOtherMenu();
		startListener();
		test();
	}

	private void toOtherMenu() {
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

	private void startListener() {
		int count = composerButtonsWrapper.getChildCount();
		final Intent intentToCamera = new Intent(NewIndex.this, CameraActivity.class);
		for (int i = 0; i < count; i++) {
			HideImageButton hide = (HideImageButton) composerButtonsWrapper.getChildAt(i);
			switch (hide.getId()) {
				case R.id.composer_button_photo: //照相机
					hide.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							intentToCamera.putExtra("what", "camera");
							startActivity(intentToCamera);
							toggleComposerButtons();
						}
					});
					break;
				case R.id.composer_button_people: // 打开图库
					hide.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							intentToCamera.putExtra("what", "picture");
							startActivity(intentToCamera);
							toggleComposerButtons();
						}
					});
					break;
			}
		}
	}

	private void toggleComposerButtons() {
		if (!areButtonsShowing) {
			ButtonAnimationSet.startAnimations(composerButtonsWrapper, InOutAnimation.Direction.IN);
			icon.startAnimation(addButtonIn);
		} else {
			ButtonAnimationSet.startAnimations(composerButtonsWrapper, InOutAnimation.Direction.OUT);
			icon.startAnimation(addButtonOut);
		}
		areButtonsShowing = !areButtonsShowing;
	}

	public void test(){

		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> contentMap = new HashMap<String, Object>();
		contentMap.put("text","test 中文");
		contentMap.put("detail","");
		list.add(contentMap);
	}
	private class MyContentListViewAdaper extends BaseAdapter{
		public MyContentListViewAdaper(){
		}

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return null;
		}
	}
}
