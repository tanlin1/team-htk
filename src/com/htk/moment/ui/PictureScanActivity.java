package com.htk.moment.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
 * 浏览大图
 *
 * @author 谭林
 *         Created by Administrator on 2014/12/1.
 */
public class PictureScanActivity extends Activity {

	private static boolean showInfo = false;

	private final int LIKE = 0;

	private final int SHARE = 1;

	private final int COMMENT = 2;

	private final int GET_BIG_PHOTO = 3;

	private boolean isLike = false;

	// 大图
	private ImageView mImageView;

	// 喜欢（赞）
	private ImageView mLikeImageView;

	private ImageView mCommentImageView;

	private ImageView mShareImageView;

	private ProgressBar mProgressBar;

	private static MyHandler myHandler;

	private RelativeLayout mRelativeLayout;

	TextView mTextView;

	Intent mIntent;


	int rs_id;
	int userId;

	String detail;

	String detailUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_scan_mode_big);

		initAll();
		listen();
	}

	@Override
	protected void onPause() {
		bitmap.recycle();
		super.onPause();
	}

	private void initAll() {

		initImg();
		mIntent = getIntent();
		myHandler = new MyHandler();
		rs_id = mIntent.getIntExtra("rs_id", 0);
		detail = mIntent.getStringExtra("detailPhoto");
		userId = mIntent.getIntExtra("userId", 0);
		detailUrl = UrlSource.getUrl(detail);

		new MyLikeThread(GET_BIG_PHOTO).start();
	}

	private void initImg() {

		mImageView = (ImageView) findViewById(R.id.picture_scan_mode_big_image);
		mTextView = (TextView) findViewById(R.id.picture_scan_mode_big_describe);
		mRelativeLayout = (RelativeLayout) findViewById(R.id.picture_scan_relative);
		mLikeImageView = (ImageView) findViewById(R.id.like_img_of_index_picture_scan_mode_big);
		mShareImageView = (ImageView) findViewById(R.id.share_img_of_index_picture_scan_mode_big);
		mCommentImageView = (ImageView) findViewById(R.id.comment_img_of_index_picture_scan_mode_big);
		mProgressBar = (ProgressBar) findViewById(R.id.big_photo_progress);
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
				isLike = !isLike;
				System.out.println("dianji zan  发送消息 --- " + isLike);
				if(isLike){
					mLikeImageView.setImageResource(R.drawable.like_after);
				}else {
					mLikeImageView.setImageResource(R.drawable.like_image_button_hollow);
				}
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
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}
	Bitmap bitmap;

	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			Bundle data = msg.getData();

			String message = data.getString("fresh");

			if("bigPhotoOk".equals(message)){
				if(bitmap != null){
					mProgressBar.setVisibility(View.GONE);
					mImageView.setImageBitmap(bitmap);
				}
				System.out.println("---------------------------------------");
			}
		}
	}

	/**
	 * 向本消息队列中放入消息，供主线程查询
	 *
	 * @param msgKey   消息键
	 * @param msgValue 消息值(数据)
	 */
	public static void sendMessage(String msgKey, String msgValue) {

		Bundle mBundle = new Bundle();
		Message mMessage = new Message();

		mBundle.putString(msgKey, msgValue);
		mMessage.setData(mBundle);

		myHandler.sendMessage(mMessage);
	}


	private class MyLikeThread extends Thread {

		private int request;

		public MyLikeThread(int req) {

			request = req;
		}

		@Override
		public void run() {
			HttpURLConnection con;

			switch (request) {
				case LIKE: {
					con = ConnectionHandler.getConnect(UrlSource.LIKE_STATUS, LaunchActivity.JSESSIONID);
					OutputStream os;
					try {
						os = con.getOutputStream();
						JSONObject outObject = new JSONObject();
						outObject.put("rs_id", rs_id);
						outObject.put("isLike", isLike);
						outObject.put("likeder", userId);
						os.write(outObject.toString().getBytes());
						System.out.println(Read.read(con.getInputStream()));
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						con.disconnect();
					}

					break;
				}
				case SHARE: {
					break;
				}
				case COMMENT: {

					con = ConnectionHandler.getConnect(UrlSource.COMMENT_STATUS, LaunchActivity.JSESSIONID);
					OutputStream os;
					try {
						os = con.getOutputStream();
						JSONObject outObject = new JSONObject();
						outObject.put("rs_id", rs_id);
						// 被评论者的ID
						outObject.put("commented", userId);
						outObject.put("comment", "哇，这电杆，真直啊");
						os.write(outObject.toString().getBytes());
						System.out.println(Read.read(con.getInputStream()));
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						con.disconnect();
					}

					break;
				}
				case GET_BIG_PHOTO:
					con = ConnectionHandler.getGetConnect(detailUrl);
					try {
						bitmap = BitmapFactory.decodeStream(con.getInputStream());
						sendMessage("fresh", "bigPhotoOk");

					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						con.disconnect();
					}
				default: {
					System.out.println("********************* switch ********");
					break;
				}
			}

		}
	}
}
