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
import com.htk.moment.ui.LaunchActivity;
import com.htk.moment.ui.NewIndex;
import com.htk.moment.ui.R;
import utils.android.sdcard.Read;
import utils.check.Check;
import utils.createrequest.PartFactory;
import utils.internet.GetConnection;
import utils.json.JSONArray;
import utils.json.JSONObject;
import utils.json.JsonTool;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
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
			System.out.println("upload 路径是: " + photoPath);
			if (photoPath != null) {
				camera();
			}
		} else {
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
		hashMap = (HashMap<Integer, String>) intent.getSerializableExtra("selectMessage");
		// 目前这样处理起来好像很费时间，算法有待改进

		for (int temp : hashMap.keySet()) {
			image = new ImageView(this);
			//
			if (ImageLoader.hashBitmaps.containsKey(temp)) {
				image.setImageBitmap(ImageLoader.hashBitmaps.get(temp));
				arrayList.add(hashMap.get(temp));
			} else {
				path = hashMap.get(temp);
				// 此方法将得到图片的绝对路径，不包含由程序生成缩略图文件夹
				bitmap = BitmapFactory.decodeFile(path);
				image.setImageBitmap(ImageCompressUtil.zoomImage(bitmap, 120, 90));
				arrayList.add(path);
			}
			liner.addView(image);
		}
		liner.addView(im);
	}
	HashMap<Integer, String> hashMap;
	static ArrayList<String> arrayList = new ArrayList<String>();
	private void camera() {
		ViewGroup.LayoutParams pq = liner.getLayoutParams();
		ImageView cureentPhoto = new ImageView(this);
		cureentPhoto.setImageBitmap(BitmapFactory.decodeFile(photoPath));
		liner.addView(cureentPhoto);

		//在这些图片后面添加一个空白区域，后续有用
		ImageView im = new ImageView(this);

		im.setLayoutParams(pq);
		ViewGroup.LayoutParams params = im.getLayoutParams();
		params.width = 120;
		params.height = 90;
		liner.addView(im);
	}

	private void listen() {
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
					new UploadPhotoThread(arrayList).start();
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
		private ArrayList<String> list;
		public UploadPhotoThread(ArrayList<String> paths){
			list = paths;
		}
		@Override
		public void run() {
			//是不是可以直接加代码在这个地方
			upLoadPhoto(list.get(0));
		}
	}

	//处理拍摄好的照片
	private void upLoadPhoto(String photoPath) {
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
			connection.setRequestProperty("cookie", "JSESSIONID=" + LaunchActivity.JSESSIONID);
			connection.connect();
			connection.setReadTimeout(5000);

			ByteArrayOutputStream photoByteArray = new ByteArrayOutputStream();

			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, photoByteArray)) {

				start = PartFactory.PartBuilder("mainInfo", "test.txt", "text/plain", JsonTool.createJsonString("head", "content_内容-数据").getBytes());
				first = PartFactory.PartBuilder("photo", "first", "image/jpeg", photoByteArray.toByteArray());
				end = PartFactory.PartBuilder("photo", "second", "image/jpeg", photoByteArray.toByteArray(), true);

				connection.getOutputStream().write(start);
				connection.getOutputStream().write(first);
				connection.getOutputStream().write(end);
			}
			System.out.println(Read.read(connection.getInputStream()).toString());
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
	// 构建post请求，此次传输，仅有一次
	private void createTheFirstPart() {

		PartFactory.PartBuilder("0", "dataInfo", "text/explain", createJson("","","","",new JSONArray(),"",new JSONArray(),new JSONArray()).toString().getBytes());
	}

	/**
	 * 创建第一个part
	 * 格式跟后续的不一样，必须单独创建
	 * @param userName 用户名
	 * @param albumName 专辑名
	 * @param olderWords 原来的描述
	 * @param myWords 现在的描述
	 * @param photoLocation 照片的位置信息
	 * @param photoClass 照片分类
	 * @param photoAt 指定通知某个好友
	 * @param photoTopic 此张照片表达的主题
	 * @return 已经构建好的JSONObject
	 */
	private JSONObject createJson(String userName, String albumName, String olderWords, String myWords,
	                              JSONArray photoLocation, String photoClass, JSONArray photoAt, JSONArray photoTopic){
		JSONObject content = new JSONObject();
		// 用户名
		content.put("ID",userName);
		// 专辑名字
		content.put("albumName", albumName);
		content.put("olderWords", olderWords);
		content.put("myWords", myWords);
		content.put("photoLocation", photoLocation);
		content.put("photoClass",photoClass);
		content.put("photoAt", photoAt);
		content.put("photoTopic", photoTopic);
		return content;
	}
	private JSONArray getLocation() {
		JSONArray array = new JSONArray();
		return  array;
	}
	private JSONArray getAtSomeOne() {
		JSONArray array = new JSONArray();
		return  array;
	}
	private JSONArray getPhotoClass() {
		JSONArray array = new JSONArray();
		return  array;
	}
	private JSONArray getPhotoTopic() {
		JSONArray array = new JSONArray();
		return  array;
	}

	/**
	 * 构建后续part
	 * @param partName name
	 * @param fileName 文件名字
	 * @param content 内容
	 * @return
	 */
	private JSONObject createAfterParte(String partName, String fileName, String content){
		JSONObject object = new JSONObject();
		return object;
	}
	private String getPartName(){
		String name = "";
		return name;
	}
	private String getFileName(){
		String fileName = "";
		return fileName;
	}
	private String getContent(String photoPath){
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
		return outputStream.toString();
	}
}
