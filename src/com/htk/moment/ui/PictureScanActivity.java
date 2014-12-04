package com.htk.moment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by Administrator on 2014/12/1.
 */
public class PictureScanActivity extends Activity{

	private Intent mIntent;
	private int position;
	private ImageView mImageView;
	private TextView mTextView;
	private RelativeLayout mRelativeLayout;

	private static boolean showInfo = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_scan_mode_big);

		testImg();

		mIntent = getIntent();
		position = mIntent.getIntExtra("position", 0);
		listen();
	}

	private void testImg(){
		mImageView = (ImageView) findViewById(R.id.picture_scan_mode_big_image);
		mTextView = (TextView) findViewById(R.id.picture_scan_mode_big_describe);
		mRelativeLayout = (RelativeLayout) findViewById(R.id.picture_scan_relative);
	}
	private void listen(){
		mImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!showInfo){
					mRelativeLayout.setVisibility(View.VISIBLE);
				}else {
					mRelativeLayout.setVisibility(View.GONE);
				}
				showInfo = !showInfo;
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}
		return true;
//		return super.onKeyDown(keyCode, event);
	}
}
