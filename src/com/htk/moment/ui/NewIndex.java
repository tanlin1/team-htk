package com.htk.moment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import utils.view.animation.ComposerButtonAnimationSet;
import utils.view.animation.InOutAnimation;

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

	//private TextView add;
	private TextView search;
	private TextView me;

	// 动画
	private Animation rotateStoryAddButtonIn;
	private Animation rotateStoryAddButtonOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.index);
		index = (TextView) findViewById(R.id.index);
		message = (TextView) findViewById(R.id.message);

		composerButtonsWrapper = (ViewGroup) findViewById(R.id.composer_buttons_wrapper);

		button = findViewById(R.id.composer_buttons_show_hide_button);
		icon = findViewById(R.id.composer_buttons_show_hide_button_icon);

		rotateStoryAddButtonIn = AnimationUtils.loadAnimation(this, R.anim.rotate_story_add_button_in);
		rotateStoryAddButtonOut = AnimationUtils.loadAnimation(this, R.anim.rotate_story_add_button_out);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleComposerButtons();
			}
		});
		for (int i = 0; i < composerButtonsWrapper.getChildCount(); i++) {
			composerButtonsWrapper.getChildAt(i).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(v.getId() == R.id.composer_button_photo){
						System.out.println("点击了照相机--");
					}else if(v.getId() == R.id.composer_button_people){
						System.out.println("点击了 相册");
					}else {
						System.out.println("this block can not appear ！");
					}
				}
			});
		}
		//
		//add = (TextView) findViewById(R.id.add);
		search = (TextView) findViewById(R.id.search);
		me = (TextView) findViewById(R.id.me);

		jump();

	}

	private void jump() {
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

//		add.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//startActivity(new Intent());
//			}
//		});

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

	private void toggleComposerButtons() {
		if (!areButtonsShowing) {
			ComposerButtonAnimationSet.startAnimations(composerButtonsWrapper, InOutAnimation.Direction.IN);
			icon.startAnimation(rotateStoryAddButtonIn);
		} else {
			ComposerButtonAnimationSet.startAnimations(composerButtonsWrapper, InOutAnimation.Direction.OUT);
			icon.startAnimation(rotateStoryAddButtonOut);
		}
		areButtonsShowing = !areButtonsShowing;
	}
}
