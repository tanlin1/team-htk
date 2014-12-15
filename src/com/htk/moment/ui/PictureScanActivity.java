package com.htk.moment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import utils.android.sdcard.Read;
import utils.internet.ConnectionHandler;
import utils.internet.UrlSource;
import utils.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;


/**
 * Created by Administrator on 2014/12/1.
 */
public class PictureScanActivity extends Activity {

	private static boolean showInfo = false;

	private final int LIKE = 0;

	private final int SHARE = 1;

	private final int COMMENT = 2;

	private Intent mIntent;

	private int position;

	private boolean isLike = true;


	private ImageView mImageView;

	// 喜欢（赞）
	private ImageView mLikeImageView;

	private ImageView mCommentImageView;

	private ImageView mShareImageView;


	private TextView mTextView;

	private RelativeLayout mRelativeLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_scan_mode_big);

		initImg();

		mIntent = getIntent();
		position = mIntent.getIntExtra("position", 0);
		listen();
	}

	private void initImg() {

		mImageView = (ImageView) findViewById(R.id.picture_scan_mode_big_image);
		mTextView = (TextView) findViewById(R.id.picture_scan_mode_big_describe);
		mRelativeLayout = (RelativeLayout) findViewById(R.id.picture_scan_relative);
		mLikeImageView = (ImageView) findViewById(R.id.like_img_of_index_picture_scan_mode_big);
		mShareImageView = (ImageView) findViewById(R.id.share_img_of_index_picture_scan_mode_big);
		mCommentImageView = (ImageView) findViewById(R.id.comment_img_of_index_picture_scan_mode_big);

	}

	private void listen() {

		mImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!showInfo) {
					mRelativeLayout.setVisibility(View.VISIBLE);
				} else {
					mRelativeLayout.setVisibility(View.GONE);
				}
				showInfo = !showInfo;
			}
		});

		mLikeImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				System.out.println("dianji zan  发送消息 --- ");
				new MyLikeThread(LIKE).start();
			}
		});

		mShareImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				System.out.println("share clink------");
				new MyLikeThread(SHARE).start();
			}
		});

		mCommentImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				System.out.println("comment clink ----------");
				new MyLikeThread(COMMENT).start();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}


	private class MyLikeThread extends Thread {

		private int request;

		public MyLikeThread(int req) {

			request = req;
		}

		@Override
		public void run() {


			switch (request) {
				case LIKE: {
					HttpURLConnection con = ConnectionHandler.getConnect(UrlSource.LIKE_STATUS);
					OutputStream os;
					try {
						os = con.getOutputStream();
						JSONObject outObject = new JSONObject();
						outObject.put("rs_id", 10);
						outObject.put("isLike", isLike);
						os.write(outObject.toString().getBytes());
						System.out.println(Read.read(con.getInputStream()));
						isLike = !isLike;
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
				case SHARE: {
					break;
				}
				case COMMENT: {

					HttpURLConnection con = ConnectionHandler.getConnect(UrlSource.COMMENT_STATUS);
					OutputStream os;
					try {
						os = con.getOutputStream();
						JSONObject outObject = new JSONObject();
						outObject.put("rs_id", 10);
						// 被评论者的ID
						outObject.put("commented", 10);
						outObject.put("comment", "哇，这电杆，真直啊");
						outObject.put("isLike", isLike);
						os.write(outObject.toString().getBytes());
						System.out.println(Read.read(con.getInputStream()));
					} catch (IOException e) {
						e.printStackTrace();
					}

					break;
				}
				default: {
					System.out.println("********************* switch ********");
					break;
				}
			}
		}
	}
}
