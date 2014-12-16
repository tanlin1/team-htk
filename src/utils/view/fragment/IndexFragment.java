package utils.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.htk.moment.ui.PictureScanActivity;
import com.htk.moment.ui.R;
import com.htk.moment.ui.ViewLikeOrComment;
import come.htk.bean.IndexInfoBean;
import come.htk.bean.IndexListViewItemBean;
import come.htk.bean.UserInfoBean;
import utils.internet.ConnectionHandler;
import utils.view.IndexPullRefreshListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * 主页Fragment
 *
 * @author tanlin
 *         time：14/11/26
 */
public class IndexFragment extends Fragment {

	private final static int PULL_TO_REFRESH = 3;

	private final static int LOAD_MORE = 4;

	public final static String TAG = "IndexFragment";

	public static int name = 10;

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

		return inflater.inflate(R.layout.after_login_listview_layout, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		test();
		addDataToListView();
	}

	@Override
	public void onStart() {

		super.onStart();
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

	private static IndexPullRefreshListView listView;

	public void addDataToListView() {

		listView = (IndexPullRefreshListView) this.getView().findViewById(R.id.index_pull_to_refresh_list_view);

		listView.setOnRefreshListener(new IndexPullRefreshListView.OnRefreshListener() {

			@Override
			public void refresh() {
				// Do work to refresh the list here.
				new GetDataTask(getActivity(), PULL_TO_REFRESH).execute();
			}

			@Override
			public void loadMore() {

				new GetDataTask(getActivity(), LOAD_MORE).execute();
			}
		});
		listViewAdapter = new MyContentListViewAdapter(getActivity(), getListItems());
		listView.setAdapter(listViewAdapter);
		//		listView.setAdapter(new MyContentListViewAdapter(getActivity(), getListItems()));
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

			IndexListViewItemBean listItem;

			if (convertView == null) {
				listItem = new IndexListViewItemBean();
				convertView = listContainer.inflate(R.layout.index_listview_content, null);
				listItem.photoHead = (ImageView) convertView.findViewById(R.id.index_head_photo_thumbnail);
				listItem.userName = (TextView) convertView.findViewById(R.id.userNameIndex);
				listItem.userName.setTextScaleX(1.2f);
				listItem.userAddress = (TextView) convertView.findViewById(R.id.address);
				listItem.showingPicture = (ImageView) convertView.findViewById(R.id.showingPicture);
				listItem.photoDescribe = (TextView) convertView.findViewById(R.id.index_photo_describe);


				listItem.likeSum = (TextView) convertView.findViewById(R.id.index_photo_like_num);
				listItem.likeText = (TextView) convertView.findViewById(R.id.index_photo_like_text);
				listItem.commentSum = (TextView) convertView.findViewById(R.id.index_photo_comment_num);
				listItem.commentText = (TextView) convertView.findViewById(R.id.index_photo_comment_text);


				// 设置控件集到convertView中
				convertView.setTag(listItem);
			} else {
				listItem = (IndexListViewItemBean) convertView.getTag();
			}

			HashMap<String, Object> map = listData.get(position);

			if (map.get("photoHead") instanceof Bitmap) {

				listItem.photoHead.setImageBitmap((Bitmap) map.get("photoHead"));
			} else {
				listItem.photoHead.setImageResource((Integer) map.get("photoHead"));
			}

			listItem.userName.setText((CharSequence) map.get("name"));
			listItem.userAddress.setText((CharSequence) map.get("userAddress"));

			if (map.get("userPicture") instanceof Bitmap) {

				listItem.showingPicture.setImageBitmap((Bitmap) map.get("userPicture"));
			}

			listItem.photoDescribe.setText((CharSequence) map.get("myWords"));

			listItem.showingPicture.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(getActivity(), PictureScanActivity.class);
					intent.putExtra("position", position);
					startActivity(intent);
					getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			});


			viewTheComment(listItem.likeSum);
			viewTheComment(listItem.likeText);

			viewTheComment(listItem.commentSum);
			viewTheComment(listItem.commentText);


			return convertView;
		}
	}

	private List<HashMap<String, Object>> getListItems() {

		items = new ArrayList<HashMap<String, Object>>();

//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("photoHead", R.drawable.head1);
//		map.put("userName", "方伦");
//		map.put("userAddress", "哥伦贝尔草原");
//		map.put("userPicture", R.drawable.index_another_user_picture);
//
//		map.put("explain", "在哥伦贝尔大草原上，呼吸着、感受着");
//		items.add(map);
//
//		HashMap<String, Object> map2 = new HashMap<String, Object>();
//		map2.put("photoHead", R.drawable.head2);
//		map2.put("userName", "桂杰双");
//		map2.put("userAddress", "成都");
//		map2.put("userPicture", R.drawable.nine);
//		map2.put("explain", "赏金怒拿五杀，这个游戏如此的简单啊。。！");
//		items.add(map2);
//
//
//		HashMap<String, Object> map3 = new HashMap<String, Object>();
//		map3.put("photoHead", R.drawable.images);
//		map3.put("userName", "康乐");
//		map3.put("userAddress", "常乐村");
//		map3.put("userPicture", R.drawable.twleve);
//		map3.put("explain", "成信的图书馆，最安静，最喜欢的地方");
//		items.add(map3);

		return items;
	}

	private static ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();


	private class GetDataTask extends AsyncTask<Void, Void, HashMap<String, Object>> {

		private Context context;

		private int index;

		public GetDataTask(Context context, int index) {

			this.context = context;
			this.index = index;
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {
			// Simulates a background job.
			HashMap<String, Object> dataMap = new HashMap<String, Object>();
			try {
				Thread.sleep(2000);
				dataMap.put("photoHead", R.drawable.eight);
				dataMap.put("userName", "Alby");
				dataMap.put("userAddress", "new york");
				dataMap.put("userPicture", R.drawable.zero);
				dataMap.put("explain", "NBA 。。。NBA");

			} catch (InterruptedException e) {
				Log.d("test", "Sleep Exception");
			}

			return dataMap;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {

			if (index == PULL_TO_REFRESH) {
				// 将字符串“Added after refresh”添加到顶部
				//mListItems.addFirst("","Added after refresh...");
				items.add(0, result);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
				String date = format.format(new Date());
				// Call onRefreshComplete when the list has been refreshed.
				listView.onRefreshComplete(date);

			} else if (index == LOAD_MORE) {
				items.add(items.size(), result);
				listView.onLoadMoreComplete();
			}
			//			myContentListViewAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onCancelled(HashMap<String, Object> stringObjectHashMap) {

			super.onCancelled(stringObjectHashMap);
		}
	}


	/**
	 * 查看评论
	 *
	 * @param v 屏幕上的某个可点击视图
	 */
	private void viewTheComment(View v) {

		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(getActivity(), ViewLikeOrComment.class));
			}
		});
	}

	private void test() {

		System.out.println("创建 handler =====  ");
		myHandler = new MyHandler();
	}


	private static MyContentListViewAdapter listViewAdapter;

	public static MyHandler myHandler;


	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			Bundle dataBundle = msg.getData();
			String message = dataBundle.getString("fresh");
			if ("refresh_completed".equals(message)) {  //刷新完成
				/**
				 * 真的可以更新界面了，
				 * 为了界面友好，考虑放一个processBar提示
				 */
				System.out.println("put 数据成功 = " + putDataToList());

			} else if ("load_completed".equals(message)) { // 加载完成
				try {
					items.add(items.size(), loadQueue.take());
					listView.onLoadMoreComplete();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} else if ("data_completed".equals(message)) {
				/**
				 * 第一次请求数据成功，紧接着应该请求主页
				 * 图片，头像（请求指定的用户ID的头像）
				 *
				 * 头像：下一个环节处理，设计多个请求
				 */
				new PhotoThread().start();
				// 并立即放入放入第二个队列，后面需要，不然出队之后就找不到了
			} else if ("viewPhotoCompleted".equals(message)) {
			} else {
				Log.e(TAG, "wrong message in bundle !");
			}
		}
	}

	IndexInfoBean indexDataDetail;

	private boolean putDataToList() {

		int length = refreshQueue1.size();

		HashMap<String, Object> map;

		for (int i = 0; i < length; i++) {

			map = new HashMap<String, Object>();
			try {
				IndexInfoBean test = refreshQueue1.take();

				if (test.getPhotoHead() != null) {
					map.put("photoHead", test.getPhotoHead());
				} else {
					map.put("photoHead", R.drawable.head1);
				}
				map.put("userName", "name-waiting");
				map.put("userAddress", "photo_address");

				if (test.getPictureShow() != null) {
					map.put("userPicture", test.getPictureShow());
				}
				map.put("myWords", test.getMyWords());
				// 添加到listMap中
				items.add(0, map);

			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		String date = format.format(new Date());

		listView.onRefreshComplete(date);
		listView.invalidateViews();
		listViewAdapter.notifyDataSetChanged();
		return true;
	}


	public static void sendMessage(String msgKey, String msgValue) {

		Bundle mBundle = new Bundle();
		Message mMessage = new Message();

		mBundle.putString(msgKey, msgValue);
		mMessage.setData(mBundle);

		myHandler.sendMessage(mMessage);
	}

	public static BlockingQueue<IndexInfoBean> refreshQueue = new ArrayBlockingQueue<IndexInfoBean>(10);

	private static BlockingQueue<IndexInfoBean> refreshQueue1 = new ArrayBlockingQueue<IndexInfoBean>(10);

	private static BlockingQueue<UserInfoBean> userInfoQueue = new ArrayBlockingQueue<UserInfoBean>(10);


	public static BlockingQueue<HashMap<String, Object>> loadQueue = new ArrayBlockingQueue<HashMap<String, Object>>(10);

	private class PhotoThread extends Thread {

		String way;

		@Override
		public void run() {

			int length = refreshQueue.size();
			HttpURLConnection con = null;

			UserInfoBean userInfo;

			for (int i = 0; i < length; i++) {
				try {
					/**
					 * 从队列取出数据
					 */
					indexDataDetail = refreshQueue.take();
					way = indexDataDetail.getViewPhoto();
					if (!way.contains("mks")) {
						System.out.println("路径 似乎错了喔 ！");
					} else {
						String url = getUrl(way);
						if (url.endsWith(".jpg")) {
							userInfo = new UserInfoBean();
							userInfo.setID(indexDataDetail.getId());


							System.out.println(url);

							con = ConnectionHandler.getGetConnect(url);
							InputStream is = con.getInputStream();
							indexDataDetail.setPictureShow(BitmapFactory.decodeStream(is));
							/**
							 * 放入有数据的新队列
							 */
							refreshQueue1.put(indexDataDetail);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					// 必须断开连接
					if (con != null) {
						con.disconnect();
					}
				}
			}
			sendMessage("fresh", "refresh_completed");
		}
	}

	private String getUrl(String path) {
		return path.split("mks")[1];
	}
}
