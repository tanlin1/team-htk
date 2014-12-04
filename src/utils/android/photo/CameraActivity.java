package utils.android.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.htk.moment.ui.AppIndexActivity;
import com.htk.moment.ui.R;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * 拍照和选择图片
 * <p/>
 * Created by Administrator on 2014/8/18.
 */
public class CameraActivity extends Activity {

	public static final String TAG = "CameraActivity";

	private static final int CAMERA_ASK = 0;

	private String photoName;

	private File directory;

	private LinearLayout liner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.take_photo_layout);
		liner = (LinearLayout) findViewById(R.id.camera_liner);
		takeSomePhoto();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String path;
		Bitmap bitmap;
		ImageView goUpload = (ImageView) findViewById(R.id.camera_button_handle_photo);
		ImageView imageView = (ImageView) findViewById(R.id.camera_photo_scanning);

		//成功（虽然Intent为空，那是因为我们指定了保存路径，Intent返回的是一个内容提供者Content）
		if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_ASK) {
			//提交原图
			path = directory + "/" + photoName;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			bitmap = BitmapFactory.decodeFile(path, options);
			while (bitmap == null || imageView == null) {
				Log.w("TAG", "请等待图片加载！");
				System.out.println("请等待图片加载");
			}
			imageView.setImageBitmap(bitmap);

			final String sendPath = path;
			goUpload.setOnClickListener(new View.OnClickListener() {
				//点击上传原图，就开启上传线程
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CameraActivity.this, UploadPhoto.class);
					intent.putExtra("photoPath", sendPath);
					intent.putExtra("measure", "CAMERA_ASK");
					startActivity(intent);
				}
			});


			addFilter();
		} else {
			// 返回主页
			startActivity(new Intent(CameraActivity.this, AppIndexActivity.class));
		}
	}

	/**
	 * 用户准备拍照
	 * <p/>
	 * 并将照片保存到指定目录
	 */
	private void takeSomePhoto() {
		//以日期命名jpg格式
		photoName = DateFormat.format("yy-MM-dd-hh-mm-ss",
				Calendar.getInstance(Locale.CHINA)).toString() + ".jpg";
		// SD 卡存在
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//
			StorageManager manager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
			try {
				// 利用反射， 调用系统（主机）有几张 SD 卡
				Method methodMnt = manager.getClass().getMethod("getVolumePaths");
				String[] path = (String[]) methodMnt.invoke(manager);
				// 在SD card0 （内置）中创建目录
				directory = new File(path[0] + "/moment/photo/");
				if (!directory.exists()) {
					// 创建多级目录
					if (!directory.mkdirs()) {
						Log.e(TAG, new Date() + ": SD 卡创建目录失败！");
					}
				}
				File photo = new File(directory, photoName);
				// 意图（调用相机）
				Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				takePhoto.addCategory(Intent.CATEGORY_DEFAULT);

				//指定你保存路径，不会在系统默认路径下（当然可以指定）
				takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
				//调用系统相机
				startActivityForResult(takePhoto, CAMERA_ASK);

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private void addFilter(){

		LayoutInflater inflater = LayoutInflater.from(this);

		liner.addView(inflater.inflate(R.layout.filter_items, null));
	}
}