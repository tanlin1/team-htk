package utils.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.htk.moment.ui.LaunchActivity;
import com.htk.moment.ui.R;
import come.htk.bean.IndexListViewItemBean;
import utils.android.sdcard.Read;
import utils.internet.ConnectionHandler;
import utils.internet.UrlSource;
import utils.json.JSONObject;
import utils.view.vertical.VerticalViewPager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 个人中心，上下滑动页面
 * （本来应该是像通知栏那样上下拉的，留给第二版改进）
 *
 * @author Administrator 谭林
 *         <p/>
 *         time: 14/11/15
 */
public class MeFragment extends Fragment {

	public static String TAG = "MeFragment";

	private VerticalViewPager verticalViewPager;

	private TextView mPhotoText;

	private TextView mPhotoNum;

	private TextView mFollowText;

	private TextView mFollowNum;

	private TextView mFansText;

	private TextView mFansNum;

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.user_home_index, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		init();
		createAHandler();
	}

	@Override
	public void onStart() {

		super.onStart();
		new MyGetThreeNumThread().start();
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public void onStop() {

		super.onStop();
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onDetach() {

		super.onDetach();
	}

	private void init() {

		initVerticalPager();
	}

	private void initWidgets() {

		mPhotoText = (TextView) getView().findViewById(R.id.user_home_photo_text);
		mFollowText = (TextView) getView().findViewById(R.id.user_home_follow_text);
		mFansText = (TextView) getView().findViewById(R.id.user_home_fans_text);

		mPhotoNum = (TextView) getView().findViewById(R.id.user_home_photo_num);
		mFollowNum = (TextView) getView().findViewById(R.id.user_home_follow_num);
		mFansNum = (TextView) getView().findViewById(R.id.user_home_fans_num);

		mPhotoText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				verticalViewPager.setCurrentItem(1);
			}
		});
		mFollowText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				System.out.println(" ------------  点击  -----------  ");
				new MyGetFollowBodyThread().start();
			}
		});
	}

	/**
	 * 初始化竖直pager
	 */
	private void initVerticalPager() {

		verticalViewPager = (VerticalViewPager) getView().findViewById(R.id.verticalViewPager);
		final UserHomeBefore before = new UserHomeBefore();
		final UserHomeAfter after = new UserHomeAfter();
		verticalViewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {

			@Override
			public Fragment getItem(int position) {

				if (position == 0) {
					return before;
				}
				return after;
			}

			@Override
			public int getCount() {

				return 2;
			}
		});

		verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {

				/**
				 * 当前所在哪一页
				 * 第一页：主页的照片，关注，粉丝
				 */
				switch (position) {
					case 0:
						goToCommentDetail();
						break;
					case 1:
						goToPhotoDetail();
						break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		verticalViewPager.setCurrentItem(0);
	}


	/**
	 * 查看关注人信息
	 */
	private void goToCommentDetail() {

		new MyGetThreeNumThread().start();
	}

	private class MyGetThreeNumThread extends Thread {

		@Override
		public void run() {

			if (hasSomeThing()) {
				// （非UI线程）子线程是不能去更新界面
				sendMessage("threeDataOk", "ok");
			}
		}
	}

	private class MyGetFollowBodyThread extends Thread {

		@Override
		public void run() {

			getFollowMan();
		}
	}

	private int photoNum;

	private int followNum;

	private int fansNum;


	/**
	 * @return true 存在那三个数据
	 */
	private boolean hasSomeThing() {

		HttpURLConnection connection = null;
		JSONObject objectI;
		JSONObject objectO = new JSONObject();
		String response = null;
		try {
			// 取得一个连接 多 part的 connection
			connection = ConnectionHandler.getConnect(UrlSource.GET_THREE_NUMBER, LaunchActivity.JSESSIONID);

			objectO.put("ID", 74);

			connection.getOutputStream().write((objectO.toString()).getBytes());
			Log.i(TAG, "server response code: " + connection.getResponseCode());

			String m = Read.read(connection.getInputStream());
			objectI = new JSONObject(m);

			if (objectI.has("status")) {
				response = objectI.getString("status");
			}

			// 说明有数据，正常查询状态
			if (response == null) {
				// 获取照片数量，关注人数，粉丝数量
				photoNum = objectI.getInt("photosNumber");
				followNum = objectI.getInt("fansNum");
				fansNum = objectI.getInt("followingsNum");

				return true;
			} else if (response.equals("SQLERROR")) {
				System.out.println("server info : sql error");
				return false;
			} else if (response.equals("JSONFORMATERROR")) {
				System.out.println("server info : json format error");
				return false;
			} else {
				System.out.println("server info : give me nothing");
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("what ?????????");
		} finally {
			// 写完一次，关闭连接，释放服务器资源
			if (connection != null) {
				connection.disconnect();
			}
		}
		return false;
	}

	private void getFollowMan() {

		HttpURLConnection connection = null;

		try {
			// 取得一个连接 多 part的 connection
			connection = ConnectionHandler.getConnect(UrlSource.GET_FOLLOWINGS_INFO, LaunchActivity.JSESSIONID);

			System.out.println("服务器消息  ----------  " + connection.getResponseCode());
			System.out.println(Read.read(connection.getInputStream()));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 写完一次，关闭连接，释放服务器资源
			if (connection != null) {
				connection.disconnect();
			}
		}
	}


	private GridView mGridView;

	private ImageView mScanSelfImageView;

	private ImageView mShowAllPhotoImageView;

	private ListView mListView;

	private void goToPhotoDetail() {

		mGridView = (GridView) getView().findViewById(R.id.user_home_photo_classes);
		mShowAllPhotoImageView = (ImageView) getView().findViewById(R.id.user_home_index_show_all);
		mScanSelfImageView = (ImageView) getView().findViewById(R.id.user_home_index_scan_self);
		mListView = (ListView) getView().findViewById(R.id.user_home_self_index_list_view);


		mGridView.setAdapter(new MyGridViewAdapter(getActivity()));
		mGridView.setHorizontalSpacing(1);
		mGridView.setVerticalSpacing(1);

		// 进入个人主页
		mScanSelfImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mScanSelfImageView.setImageResource(R.drawable.user_home_index_scan_self_after_img);
				mShowAllPhotoImageView.setImageResource(R.drawable.user_home_index_show_all_before_img);

				//startActivity(new Intent(getActivity(), UserOnlyHimselfActivity.class));

				mListView.setVisibility(View.VISIBLE);
				mGridView.setVisibility(View.GONE);
				mListView.setAdapter(new MyContentListViewAdapter(getActivity(), getListItems()));

			}
		});

		mShowAllPhotoImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mShowAllPhotoImageView.setImageResource(R.drawable.user_home_index_show_all_after_img);
				mScanSelfImageView.setImageResource(R.drawable.user_home_index_scan_self_before_img);
				mListView.setVisibility(View.GONE);
				mGridView.setVisibility(View.VISIBLE);
			}
		});

	}


	private class MyGridViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyGridViewAdapter(Context context) {

			super();
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			return 10;
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = mInflater.inflate(R.layout.photo_choose_layout, null);
			ImageView mImageView = (ImageView) view.findViewById(R.id.image);
			mImageView.setImageResource(R.drawable.head2);
			mImageView.setScaleType(ImageView.ScaleType.CENTER);
			mImageView.setPadding(2, 1, 2, 1);
			return view;
		}
	}

	private class MyContentListViewAdapter extends BaseAdapter {

		private List<HashMap<String, Object>> listData;

		//视图容器
		private LayoutInflater listContainer;

		public MyContentListViewAdapter(Context context, List<HashMap<String, Object>> content) {

			listContainer = LayoutInflater.from(context);
			this.listData = content;
		}

		@Override
		public int getCount() {

			return listData.size();
		}

		@Override
		public Object getItem(int position) {

			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			IndexListViewItemBean userHomeIndexItem;

			if (convertView == null) {
				userHomeIndexItem = new IndexListViewItemBean();
				convertView = listContainer.inflate(R.layout.user_self_index_list_content, null);
				userHomeIndexItem.photoHead = (ImageView) convertView.findViewById(R.id.index_head_photo_thumbnail);
				userHomeIndexItem.userName = (TextView) convertView.findViewById(R.id.userNameIndex);
				userHomeIndexItem.userName.setTextScaleX(1.2f);
				userHomeIndexItem.userAddress = (TextView) convertView.findViewById(R.id.address);
				userHomeIndexItem.showingPicture = (ImageView) convertView.findViewById(R.id.showingPicture);
				userHomeIndexItem.photoDescribe = (TextView) convertView.findViewById(R.id.index_photo_describe);


				userHomeIndexItem.likeSum = (TextView) convertView.findViewById(R.id.index_photo_like_num);
				userHomeIndexItem.likeText = (TextView) convertView.findViewById(R.id.index_photo_like_text);
				userHomeIndexItem.commentSum = (TextView) convertView.findViewById(R.id.index_photo_comment_num);
				userHomeIndexItem.commentText = (TextView) convertView.findViewById(R.id.index_photo_comment_text);


				// 设置控件集到convertView中
				convertView.setTag(userHomeIndexItem);
			} else {
				userHomeIndexItem = (IndexListViewItemBean) convertView.getTag();
			}

			HashMap<String, Object> map = listData.get(position);
			userHomeIndexItem.photoHead.setImageResource((Integer) map.get("photoHead"));
			userHomeIndexItem.userName.setText((String) map.get("userName"));
			userHomeIndexItem.userAddress.setText((String) map.get("userAddress"));
			userHomeIndexItem.showingPicture.setImageResource((Integer) map.get("userPicture"));
			userHomeIndexItem.photoDescribe.setText((String) map.get("explain"));

			return convertView;
		}
	}

	private List<HashMap<String, Object>> getListItems() {

		ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("photoHead", R.drawable.head1);
		map.put("userName", "随心所欲");
		map.put("userAddress", "成都");
		map.put("userPicture", R.drawable.cloud_xiling);
		map.put("explain", "西岭云海");
		items.add(map);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("photoHead", R.drawable.head1);
		map2.put("userName", "随心所欲");
		map2.put("userAddress", "双流");
		map2.put("userPicture", R.drawable.nine);
		map2.put("explain", "寝室小LOL一把");
		items.add(map2);

		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("photoHead", R.drawable.head1);
		map3.put("userName", "随心所欲");
		map3.put("userAddress", "双流");
		map3.put("userPicture", R.drawable.one);
		map3.put("explain", "周末，坐等12下钟声");
		items.add(map3);

		return items;
	}

	/**
	 * 点击 me 所显示的界面
	 */
	public class UserHomeBefore extends Fragment {

		// 不创建多个对象，将构造方法私有化
		private UserHomeBefore() {

		}

		//三个一般必须重载的方法
		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			return inflater.inflate(R.layout.user_home_before, container, false);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {

			super.onActivityCreated(savedInstanceState);
			MeFragment.this.initWidgets();
			new MyGetThreeNumThread().start();
		}

		@Override
		public void onStart() {

			super.onStart();
		}

		@Override
		public void onResume() {
			super.onResume();
			// 再次返回的时候，请求一次

		}

		@Override
		public void onPause() {

			super.onPause();
		}

		@Override
		public void onStop() {

			super.onStop();
		}

		@Override
		public void onDestroyView() {

			super.onDestroyView();
		}

		@Override
		public void onDestroy() {

			super.onDestroy();
		}

		@Override
		public void onDetach() {

			super.onDetach();
		}

	}

	/**
	 * 在 me 界面向上滑动的时候
	 */
	public class UserHomeAfter extends Fragment {

		//三个一般必须重载的方法
		// 私有化
		private UserHomeAfter() {

		}

		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
		}

		@Override
		public void onPause() {

			super.onPause();
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			return inflater.inflate(R.layout.user_home_after, container, false);
		}
	}

	private class MyHandler extends Handler {

		Bundle mData;

		@Override
		public void handleMessage(Message msg) {

			mData = msg.getData();

			if ("ok".equals(mData.getString("threeDataOk"))) {
				System.out.println("可以更新界面了");

				mPhotoNum.setText(String.valueOf(photoNum));
				mFollowNum.setText(String.valueOf(followNum));
				mFansNum.setText(String.valueOf(fansNum));

			} else if ("ok".equals(mData.getString("something"))) {
				System.out.println("some thing is ok !");
			} else {
				Log.e(TAG, "sub thread send the bad message");
			}
		}
	}

	static MyHandler myHandler;

	private void createAHandler() {

		myHandler = new MyHandler();
	}

	/**
	 * @param key   主键
	 * @param value （消息）值
	 */
	private void sendMessage(String key, String value) {

		Bundle dataBundle = new Bundle();

		Message dataMessage = new Message();

		dataBundle.putString(key, value);
		dataMessage.setData(dataBundle);

		myHandler.sendMessage(dataMessage);
	}


}
