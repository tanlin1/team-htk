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
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import utils.android.Read;
import utils.android.Write;
import utils.android.judgment.Login;
import utils.internet.CheckInternetTool;
import utils.json.JSONObject;
import utils.json.JSONStringer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

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
	public static String url = "http://192.168.191.1";
	//public static String url = "http://10.10.117.120";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//全屏模式，无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//得到屏幕的尺寸，方便后续使用
		WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screenWidth = vm.getDefaultDisplay().getWidth();
		screenHeight = vm.getDefaultDisplay().getHeight();

		//当前Activity 是哪一个布局文件，以何种方式显示
		setContentView(R.layout.main);
		//检查网络
		//checkInternet(this);

		//依次找到布局中的各个控件，并为之设置监听器，便于处理
		ImageView imageView = (ImageView) findViewById(R.id.MainView);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(screenWidth,screenHeight*2/3));
		imageView.setBackgroundDrawable(getWallpaper().getCurrent());

		//登录（注册）按钮
		Button login = (Button) findViewById(R.id.button_login);
		Button register = (Button) findViewById(R.id.button_register);

		//登录填写的邮箱，密码编辑框
		emailEdit = (EditText) findViewById(R.id.set_name);
		passwordEdit = (EditText) findViewById(R.id.set_password);

		//记住密码
		CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);
		TextView toFindPassword = (TextView) findViewById(R.id.find_password);

		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					try {
						Write.write(LaunchActivity.this, "test.txt", "yes\r\n你好吗？");
					} catch (IOException e) {
						System.out.println("写入文件出错！");
						e.printStackTrace();
					}
				}

			}
		});
		toFindPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//连接到服务器找回密码
				//startActivity(new Intent().setClass(LaunchActivity.this, Index.class));
                startActivity(new Intent().setClass(LaunchActivity.this, NewMainActivity.class));
			}
		});

		//获取昵称编辑框的数据（通过焦点转移）
		emailEdit.setOnFocusChangeListener(new emailFocus());
		passwordEdit.setOnFocusChangeListener(new passwordFocus());
		//为编辑框设置回车键检测
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
				startActivity(new Intent().setClass(LaunchActivity.this, RegisterActivity.class));
			}
		});
		//点击登录，进行验证用户名以及密码。
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (email == null || password == null) {
					Toast.makeText(getApplication(), R.string.login_warning, Toast.LENGTH_SHORT).show();
				} else {
					new LoginThread().start();
				}
			}
		});
	}

	/**
	 *
	 * @param context 上下文
	 * @return  true: 已经接入网络，不管是Internet 还是移动网络
	 *          false: 当前没有网络连接
	 */
	private boolean checkInternet(Context context) {
		if(!CheckInternetTool.checkInternet(context)){
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
//			dialog.setMultiChoiceItems(items,itemsCheck,new DialogInterface.OnMultiChoiceClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//					System.out.println("null");
//				}
//			});
			dialog.create().show();
			return false;
		}
		return true;
	}

	//昵称编辑框焦点侦听
	private class emailFocus implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				email = emailEdit.getText().toString();
				if (!Login.isEmail(email)) {
					Toast.makeText(getApplicationContext(), R.string.email_wrong, Toast.LENGTH_SHORT).show();
					emailEdit.setText("");
					email = null;
				}
			}
		}
	}

	//密码编辑框焦点侦听
	private class passwordFocus implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				password = passwordEdit.getText().toString();
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			if ("true".equals(data.getString("password"))) {
				//Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(LaunchActivity.this, Index.class));
			} else if ("false".equals(data.getString("password"))) {
				Toast.makeText(LaunchActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
			} else if ("yes".equals(data.getString("timeout"))) {
				Toast.makeText(LaunchActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
			} else {
				startActivity(new Intent(LaunchActivity.this, Index.class));
			}
		}
	};

	private class LoginThread extends Thread {
		@Override
		public void run() {
			login();
		}
	}

	/**
	 * @param url 具体的url
	 *
	 * @return 此URL的HttpURLConnection连接
	 */
	private HttpURLConnection getUrlConnect(String url) {
		URL loginUrl;
		HttpURLConnection connection = null;
		try {
			loginUrl = new URL(LaunchActivity.url + url);
			connection = (HttpURLConnection) loginUrl.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(5000);
			//设置请求头字段
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//          这个属性将被用于大文件传输，有效的提高效率
//			connection.setRequestProperty("Content-Type","multipart/form-data");
			//有相同的属性则覆盖
			connection.setRequestProperty("user-agent", "Android 4.0.1");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}

	private void login() {

		String concreteUrl = ":8080/phone_login";
		HttpURLConnection connection = getUrlConnect(concreteUrl);

		//构造json字符串，并发送
		JSONStringer jsonStringer = new JSONStringer();
		String transfer;
		transfer = jsonStringer.object().key("email").value(email).key("password").value(password)
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
			if (serverInformation.getString("isPassed").equals("no")) {//|| serverInformation.getString("server").equals("error")) {
				sendMessage("password", "false");
			}
			if (serverInformation.getString("isPassed").equals("yes")) {
				sendMessage("password", "true");
			}
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			sendMessage("timeout", "yes");
			//startActivity(new Intent(this, HomeActivity.class));
		} catch (SocketException e) {
			System.out.println("服务器没有打开！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == 0) {
			Toast.makeText(this, data.getStringExtra("key"), Toast.LENGTH_LONG).show();
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