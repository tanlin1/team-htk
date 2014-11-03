package com.htk.moment.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import utils.android.sdcard.Read;
import utils.check.Check;
import utils.internet.GetConnection;
import utils.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

/**
 * Created by HP on 2014/7/18.
 */
public class RegisterActivity extends Activity {

	public static String SESSIONID = null;
	private String name;
	private String password;
	private String passwordConfirm;
	private String emailAddress;
	private CheckBox checkBox;

	private EditText nameFind;
	private EditText passwordFind;
	private EditText emailAddressFind;
	private EditText passwordConfirmFind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 设置无标题，全屏幕显示
		 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);

		ImageView back1 = (ImageView) findViewById(R.id.backImage);
		TextView back2 = (TextView) findViewById(R.id.backText);

		/**
		 * 找到各个编辑框
		 */
		nameFind = ((EditText) findViewById(R.id.register_name_edit));
		passwordFind = ((EditText) findViewById(R.id.register_password_edit));
		passwordConfirmFind = (EditText) findViewById(R.id.register_password_confirm_edit);
		emailAddressFind = ((EditText) findViewById(R.id.register_email_edit));

		//checkBox = (CheckBox) findViewById(R.id.checkbox);
		name = nameFind.getText().toString();
		password = passwordFind.getText().toString();
		passwordConfirm = passwordConfirmFind.getText().toString();
		emailAddress = emailAddressFind.getText().toString();
		/**
		 * 获取注册按钮
		 */
		Button register = (Button) findViewById(R.id.button_register);
		Button reset = (Button) findViewById(R.id.reset);

		back1.setOnClickListener(new BackOnClickListener());
		back2.setOnClickListener(new BackOnClickListener());

		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/** 当所有项目都填写完毕之后，
				 * 点击注册就直接发送响应的数据给服务器就好
				 */
				if (canRegister()) {
					new registerThread().start();
				}
			}
		});


		reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nameFind.setText("");
				passwordFind.setText("");
				emailAddressFind.setText("");
				passwordConfirmFind.setText("");
			}
		});

//		checkBox.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				/**
//				 * 打开新窗口用户协议给用户浏览
//				 */
//				TextView protocol = (TextView) findViewById(R.id.protocol);
//				protocol.getScrollBarStyle();
//				if (!checkBox.isChecked()) {
//					String s = getProtocolMessage(getResources().openRawResource(R.raw.test));
//					protocol.setMovementMethod(new ScrollingMovementMethod());
//					protocol.setText(s);
//				} else {
//					protocol.setText("");
//				}
//			}
//		});
		nameFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					name = nameFind.getText().toString();
					if (name.length() == 0) {
						Toast.makeText(getApplication(), R.string.name_null, Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		passwordFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					password = passwordFind.getText().toString();
					if (password.length() == 0) {
						Toast.makeText(getApplication(), R.string.password_null, Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		passwordConfirmFind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					passwordConfirm = passwordConfirmFind.getText().toString();
					if (!password.equals(passwordConfirm)) {
						Toast.makeText(getApplication(), R.string.password_equal, Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		//在填写邮箱的完毕会连接服务器并检测此邮箱是否已经注册过本网站
		emailAddressFind.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
					emailAddress = emailAddressFind.getText().toString();
					//自动以藏输入键盘
					InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					}
					if (!Check.isEmail(emailAddress) && !Check.isPhoneNumber(emailAddress)) {
						Toast.makeText(getApplication(), R.string.format_wrong, Toast.LENGTH_SHORT).show();
						emailAddressFind.setText("");
					} else {
						new emailCheckThread().start();
					}
					return true;
				}
				return false;
			}
		});
	}

	//处理子线程传回的数据（消息）
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			//注册成功
			if ("ok".equals(data.getString("register"))) {
				Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
				//进入个人主页（设置）
				//或者进入动态页
				System.out.println("正在尝试进入主页，敬请期待！");

			} else if ("yes".equals(data.getString("emailIsUsed"))) {
				//邮箱被注册过
				AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
				dialog.setMessage(R.string.email_used);
				dialog.setCancelable(true);
				dialog.setTitle(R.string.register_error);
				emailAddressFind.setText("");
				dialog.create().show();
			} else if ("false".equals(data.getString("edited"))) {
				Toast.makeText(RegisterActivity.this, R.string.name_null, Toast.LENGTH_SHORT).show();
			} else if ("yes".equals(data.getString("timeout"))) {
				Toast.makeText(RegisterActivity.this, R.string.timeout, Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 检测用户注册的时候，邮箱是否符合注册标准
	 * 必须在子线程中执行
	 */
	private class emailCheckThread extends Thread {
		public void run() {
			emailCheck();
		}
	}
	private void emailCheck() {
		String emailCheckUrl = "/checkEmail";
		HttpURLConnection connection;
		try {
			connection = GetConnection.getConnect(emailCheckUrl);
			connection.connect();
			JSONObject object = new JSONObject();
			object.put("account", emailAddress);
			connection.getOutputStream().write(object.toString().getBytes());
			//读取服务器返回的消息
			JSONObject jsonObject = new JSONObject(Read.read(connection.getInputStream()));
			String accountResult = jsonObject.getString("accountResult");
			if (accountResult.equals("exist")) {
				sendMessage("emailIsUsed", "yes");
				connection.disconnect();
			} else if(accountResult.equals("formatError")) {
				System.out.println("格式错误");
			} else if(accountResult.equals("error")) {
				System.out.println("服务器错误");
			} else if(accountResult.equals("not_exist")) {
				System.out.println("可以注册");
			}else {
				System.out.println("服务器 404 错误");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 *
	 * @param inputStream 一个输入流
	 * @return 从该输入流读取的内容
	 */
//	private String getProtocolMessage(InputStream inputStream) {
//
//		InputStreamReader inputStreamReader;
//		String content = null;
//		try {
//			inputStreamReader = new InputStreamReader(inputStream, "utf-8");
//
//			BufferedReader reader = new BufferedReader(inputStreamReader);
//			StringBuilder sb = new StringBuilder();
//			String line;
//
//			while ((line = reader.readLine()) != null) {
//				sb.append(line);
//				sb.append("\n");
//			}
//			content = sb.toString();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return content;
//	}

	private class registerThread extends Thread {
		@Override
		public void run() {
			//Register();

			HttpURLConnection connection;
			JSONObject info = new JSONObject();

			String registerUrl = "/sign_up";

			connection = GetConnection.getConnect(registerUrl);
			info.put("name", name);
			info.put("userAccount", emailAddress);
			info.put("password", password);
			try {
				connection.connect();
				OutputStream writeToServer = connection.getOutputStream();
				writeToServer.write(info.toString().getBytes());
				// 取得输入流，并使用Reader读取
				JSONObject object = Read.read(connection.getInputStream());
				String result = object.getString("accountResult");
				System.out.println(object.toString());
				if(result.equals("success")){
					sendMessage("register", "ok");
				} else if(result.equals("formatError")){
					System.out.println("格式错误");
				} else if(result.equals("error")){
					System.out.println("服务器错了");
				} else if(result.equals("exist")){
					System.out.println("用户名存在！");
				} else {
					System.out.println(object + "\n这里出错了？？");
				}
				// 断开连接
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				sendMessage("timeout", "yes");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				connection.disconnect();
			}
		}
	}

	private boolean canRegister() {
		if (name.length() == 0 || password.length() == 0 || emailAddress.length() == 0) {
			sendMessage("edited", "false");
			return false;
		}
		return true;
	}

	private void sendMessage(String key, String value) {
		Bundle data = new Bundle();
		Message msg = new Message();
		data.putString(key, value);
		msg.setData(data);
		handler.sendMessage(msg);
	}

	private class BackOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			startActivity(new Intent(RegisterActivity.this, LaunchActivity.class));
		}
	}
}
