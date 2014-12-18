package com.htk.moment.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import come.htk.bean.FollowBean;
import utils.android.sdcard.Read;
import utils.internet.ConnectionHandler;
import utils.internet.UrlSource;
import utils.json.JSONArray;
import utils.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * Created by Administrator on 2014/12/17.
 */
public class FollowActivity extends Activity {

	private static String TAG = "FollowActivity";

	private ListView mFollowListView;

	private static MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.follow);
		initView();
	}

	private void initView() {

		mFollowListView = (ListView) findViewById(R.id.listView);
		new MyGetFollowBodyThread().start();
		createAHandler();
		adapter = new MyAdapter(FollowActivity.this, getItems());
		mFollowListView.setAdapter(adapter);
	}

	private ArrayList<HashMap<String, Object>> items;

	private class MyGetFollowBodyThread extends Thread {

		@Override
		public void run() {

			getFollowMan();
		}
	}

	private ArrayList<HashMap<String, Object>> getItems() {

		if (items == null) {
			items = new ArrayList<HashMap<String, Object>>();
		}
		return items;
	}

	private void getFollowMan() {

		HttpURLConnection connection = null;

		try {
			connection = ConnectionHandler.getConnect(UrlSource.GET_FOLLOWINGS_INFO, LaunchActivity.JSESSIONID);

			String temp = Read.read(connection.getInputStream());
			if (temp == null) {
				return;
			}
			JSONArray followData = new JSONArray(temp);
			int length = followData.length();
			FollowBean follow;
			JSONObject obj;

			System.out.println("length ==== " + length);

			for (int i = 0; i < length; i++) {
				follow = new FollowBean();
				obj = followData.getJSONObject(i);
				follow.setId(obj.getInt("ID"));
				follow.setName(obj.getString("name"));

				msgQueue.put(follow);
			}
			sendMessage("threeData", "ok");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 写完一次，关闭连接，释放服务器资源
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private BlockingQueue<FollowBean> msgQueue = new ArrayBlockingQueue<FollowBean>(10);


	private class MyAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, Object>> mMaps;

		private LayoutInflater inflater;

		public MyAdapter(Context context, ArrayList<HashMap<String, Object>> maps) {

			super();
			mMaps = maps;
			inflater = LayoutInflater.from(context);
		}

		private class ViewHolder {

			TextView id;

			TextView name;
		}

		@Override
		public int getCount() {

			return mMaps.size();
		}

		@Override
		public Object getItem(int position) {

			return mMaps.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(R.layout.follow_items, null);

				viewHolder.id = (TextView) convertView.findViewById(R.id.follow_id);
				viewHolder.name = (TextView) convertView.findViewById(R.id.follow_name);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.id.setText(String.valueOf(mMaps.get(position).get("ID")));
			viewHolder.name.setText((CharSequence) mMaps.get(position).get("name"));

			return convertView;
		}
	}

	private class MyHandler extends Handler {

		Bundle mData;

		@Override
		public void handleMessage(Message msg) {

			mData = msg.getData();
			if ("ok".equals(mData.getString("oneDataOk"))) {
				adapter.notifyDataSetChanged();
			} else if ("ok".equals(mData.getString("threeData"))) {
				int length = msgQueue.size();
				for(int i = 0; i < length; i++){
					putDataToList();
				}
			} else {
				Log.e(TAG, "sub thread send the bad message");
			}
		}
	}

	static MyHandler myHandler;

	private void createAHandler() {

		myHandler = new MyHandler();
	}
	private void putDataToList(){
		FollowBean bean;
		try {
			bean = msgQueue.take();
			// 队列取数据
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ID", bean.getId());
			map.put("name", bean.getName());
			items.add(map);
			sendMessage("oneDataOk","ok");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param key   主键
	 * @param value （消息）值
	 */
	public static void sendMessage(String key, String value) {

		Bundle dataBundle = new Bundle();

		Message dataMessage = new Message();

		dataBundle.putString(key, value);
		dataMessage.setData(dataBundle);

		myHandler.sendMessage(dataMessage);
	}
}
