package com.htk.moment.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import utils.android.sdcard.Read;
import utils.check.Check;
import utils.internet.GetConnection;
import utils.json.JSONObject;
import utils.json.JSONStringer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class LaunchActivity extends Activity {

	/**
	 * Called when the activity is first created.
	 */
	public static int screenWidth = 0;
	public static int screenHeight = 0;

	private EditText emailEdit;
	private EditText passwordEdit;
	private String password;
	private String email;
	//public static String url = "http://192.168.1.102";
	public static String url = "http://120.24.68.64:8080/mks";
	//public static String url = "http://10.10.117.120";

	public static String JSESSIONID = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//全屏模式，无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//得到屏幕的尺寸，方便后续使用
		WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screenWidth = vm.getDefaultDisplay().getWidth();
		screenHeight = vm.getDefaultDisplay().getHeight();

		//当前Activity 是哪一个布局文件，以何种方式显示
		setContentView(R.layout.main);

		//依次找到布局中的各个控件，并为之设置监听器，便于处理
		ImageView imageView = (ImageView) findViewById(R.id.headPhotoMain);
		//imageView.setLayoutParams(new LinearLayout.LayoutParams(screenWidth,screenHeight*2/3));
		//imageView.setBackgroundDrawable(getWallpaper().getCurrent());
		//imageView.setImageDrawable(getWallpaper().getCurrent());

		//登录（注册）按钮
		Button login = (Button) findViewById(R.id.button_login);
		TextView register = (TextView) findViewById(R.id.button_register);

		//登录填写的邮箱，密码编辑框
		emailEdit = (EditText) findViewById(R.id.set_name);
		passwordEdit = (EditText) findViewById(R.id.set_password);

		//记住密码
		//CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);
		TextView toFindPassword = (TextView) findViewById(R.id.find_password);

		toFindPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//连接到服务器找回密码
				//Toast.makeText(getApplication(), "服务器暂时不能处理找回密码", Toast.LENGTH_SHORT).show();
				//startActivity(new Intent().setClass(LaunchActivity.this, Index.class));
				startActivity(new Intent().setClass(LaunchActivity.this, NewIndex.class));
			}
		});

		// 获取昵称编辑框的数据（通过焦点转移）
		emailEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					email = emailEdit.getText().toString();
					if (!Check.isEmail(email) && !Check.isPhoneNumber(email)) {
						Toast.makeText(getApplicationContext(), R.string.format_wrong, Toast.LENGTH_SHORT).show();
						emailEdit.setText("");
						email = null;
					}
				}
			}
		});
		passwordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					password = passwordEdit.getText().toString();
				}
			}
		});
		// 编辑框设置回车隐藏
		passwordEdit.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					password = passwordEdit.getText().toString();
					//自动以藏输入键盘
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					}
					return true;
				}
				return false;
			}
		});

		//点击注册，跳转到注册页面
		//验证用户是否存在等信息在注册页面进行
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LaunchActivity.this, RegisterActivity.class));
			}
		});
		//点击登录，进行验证用户名以及密码。
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (email == null || password == null || password.length() == 0 || email.length() == 0) {
					Toast.makeText(getApplication(), R.string.login_warning, Toast.LENGTH_SHORT).show();
				} else {
					if (!Check.internetIsEnable(LaunchActivity.this)) {
						//Toast.makeText(getApplication(), "网络没有打开，无法使用。", Toast.LENGTH_SHORT).show();
						goSetting();
					} else {
						new LoginThread().start();
					}
				}
			}
		});
	}

	/**
	 * 当检测到用户没哟连入互联网的时候，提示重新设置网络
	 */
	private void goSetting() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(LaunchActivity.this);

		dialog.setTitle(R.string.login_dialog_title);
		dialog.setMessage(R.string.net_warning);
		dialog.setPositiveButton(R.string.login_dialog_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startActivity(new Intent(Settings.ACTION_SETTINGS));
			}
		});
		dialog.setNegativeButton(R.string.login_dialog_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.create().show();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			if ("true".equals(data.getString("password"))) {
				//Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(LaunchActivity.this, NewIndex.class));
			} else if ("passwordWrong".equals(data.getString("result"))) {
				Toast.makeText(LaunchActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
			} else if ("timeOut".equals(data.getString("result"))) {
				Toast.makeText(LaunchActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
			} else if ("formatError".equals(data.getString("result"))) {
				Log.e("CLIENT", "格式错误！");
				//startActivity(new Intent(LaunchActivity.this, NewIndex.class));
			}
		}
	};

	private class LoginThread extends Thread {
		@Override
		public void run() {
			String concreteUrl = "/login";
			HttpURLConnection connection = GetConnection.getConnect(concreteUrl);
			//构造json字符串，并发送
			JSONStringer jsonStringer = new JSONStringer();
			String transfer;
			transfer = jsonStringer.object().key("account").value(email).key("password").value(password)
					.endObject().toString();
			System.out.println(transfer);
			try {
				connection.connect();

				OutputStream writeToServer = connection.getOutputStream();
				writeToServer.write(transfer.getBytes());
				writeToServer.flush();
				writeToServer.close();

				// 取得输入流，并使用Reader读取
				JSONObject serverInformation = Read.read(connection.getInputStream());
				String result = serverInformation.getString("accountResult");
				if (result.equals("success")) {
					// 登录成功
					sendMessage("password", "true");
					JSESSIONID = serverInformation.getString("JSESSIONID");
				} else if (result.equals("dataWrong")) {
					// 密码错误/此用户名不存在，报告给用户处理
					sendMessage("result", "passwordWrong");
				} else if (result.equals("formatError")) {
					// 数据格式错误，由程序员处理
					sendMessage("result", "formatError");
				} else {
					sendMessage("result", "timeOut");
					System.out.println("服务器没有响应" + result);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				sendMessage("result", "timeOut");
			} catch (SocketException e) {
				Toast.makeText(getApplication(), "网络没有打开，无法使用。", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				connection.disconnect();
			}
		}
	}

	private void sendMessage(String key, String value) {
		Bundle data = new Bundle();
		Message msg = new Message();
		data.putString(key, value);
		msg.setData(data);
		handler.sendMessage(msg);
	}
}