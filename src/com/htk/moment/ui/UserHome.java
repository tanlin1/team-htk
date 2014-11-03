package com.htk.moment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by Administrator on 2014/8/10.
 */
public class UserHome extends Activity {
	private TextView index;
	private TextView message;
	private TextView add;
	private TextView search;
	private TextView me;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);
		index = (TextView) findViewById(R.id.index);
		message = (TextView) findViewById(R.id.message);
		add = (TextView) findViewById(R.id.add);
		search = (TextView) findViewById(R.id.search);
		me = (TextView) findViewById(R.id.me);

		// 进入首页
		index.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserHome.this, NewIndex.class));
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
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
				//startActivity(new Intent(UserHome.this, NewIndex.class));
			}
		});
	}
}
