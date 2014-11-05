package utils.android.photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.htk.moment.ui.NewIndex;
import com.htk.moment.ui.R;
import utils.check.Check;
import utils.createrequest.PartFactory;
import utils.internet.GetConnection;
import utils.json.JsonTool;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * Created by Administrator on 2014/9/2.
 */
public class UploadPhoto extends Activity {

	public static String BOUNDARY = "---------------------------7de8c1a80910";

	private HttpURLConnection connection = null;
	private LinearLayout liner;
	private String photoPath = null;

	ImageButton back;
	ImageButton uploadButton;

	private byte[] start;
	private byte[] first;
	private byte[] end;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.upload);
		back = (ImageButton) findViewById(R.id.back_to_camera);
		uploadButton = (ImageButton) findViewById(R.id.UploadPhoto);
		intent = getIntent();
		liner = (LinearLayout) findViewById(R.id.liner);
		String way = intent.getStringExtra("way");

		// 得到照片的路径
		if ("PICTURE_ASK".equals(way)) {
			displayTheSelectedPhoto();
		} else if ("CAMERA_ASK".equals(way)) {
			photoPath = intent.getStringExtra("photo_path");
			System.out.println("路径是: " + photoPath);
			if (photoPath != null) {
				camera();
			}
		}else {
			System.out.println("what is wrong ??????");
		}
		listen();
	}

	private void displayTheSelectedPhoto() {
		Bitmap bitmap;
		ImageView image;
		String path;
		ViewGroup.LayoutParams pq = liner.getLayoutParams();
		ImageView im = new ImageView(this);
		im.setLayoutParams(pq);
		ViewGroup.LayoutParams params = im.getLayoutParams();
		params.width = 120;
		params.height = 90;
		// 得到从 本地获取到的所有图片路径
		// 注意：本HashMap中的key为图片在屏幕上的位置0-size，
		// 因此在找路径的时候可能或混淆
		HashMap<Integer, String> hashMap = (HashMap<Integer, String>) intent.getSerializableExtra("selectMessage");
		// 目前这样处理起来好像很费时间，算法有待改进

		for(int temp : hashMap.keySet()){
			image = new ImageView(this);
			//
			if(ImageLoader.hashBitmaps.containsKey(temp)) {
				image.setImageBitmap(ImageLoader.hashBitmaps.get(temp));
			} else {
				path = hashMap.get(temp);
				// 此方法将得到图片的绝对路径，不包含由程序生成缩略图文件夹
				bitmap = BitmapFactory.decodeFile(path);
				image.setImageBitmap(ImageCompressUtil.zoomImage(bitmap, 120, 90));
				image.setPadding(2, 0, 2, 1);
			}
			liner.addView(image);
		}
		liner.addView(im);
	}

	private void camera() {
		ViewGroup.LayoutParams pq = liner.getLayoutParams();
		//在这些图片后面添加一个空白区域，后续有用
		ImageView im = new ImageView(this);
		im.setLayoutParams(pq);
		ViewGroup.LayoutParams params = im.getLayoutParams();
		params.width = 120;
		params.height = 90;
		liner.addView(im);
	}

	private void listen(){
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UploadPhoto.this, CameraActivity.class));
			}
		});
		uploadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//如果用户已连接上Internet，就开启上传线程，并进入主界面（返回至登录时候的界面）
				if (Check.internetIsEnable(UploadPhoto.this)) {
					System.out.println("用户在线");
					new UploadPhotoThread().start();
				} else {
					System.out.println("用户不在线");
					//保存至本地，等到下次用户连接上internet的时候上传图片
				}
				startActivity(new Intent(UploadPhoto.this, NewIndex.class));
			}
		});
	}
	//进行上传操作
	private class UploadPhotoThread extends Thread {
		@Override
		public void run() {
			//是不是可以直接加代码在这个地方
			upLoadPhoto();
		}
	}

	//处理拍摄好的照片
	private void upLoadPhoto() {

		Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
		try {
			// 取得一个连接connection
			connection = GetConnection.getConnect("/upload_photo");
			//设置连接状态（属性）
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(3000);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("User-Agent", "Android 4.0.1");

			connection.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			connection.connect();
			connection.setReadTimeout(5000);

			ByteArrayOutputStream photoByteArray = new ByteArrayOutputStream();

			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, photoByteArray)) {

				start = PartFactory.PartBuilder("text", "text", "text/plain", JsonTool.createJsonString("head", "content_内容-数据").getBytes());
				first = PartFactory.PartBuilder("photo", "first", "image/jpeg", photoByteArray.toByteArray());
				end = PartFactory.PartBuilder("photo", "second", "image/jpeg", photoByteArray.toByteArray(), true);

				connection.getOutputStream().write(start);
				connection.getOutputStream().write(first);
				connection.getOutputStream().write(end);
			}
			System.out.println(connection.getResponseCode() + "----------上传线程已经完成！");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	// handler 处理子线程完成状态消息
}
