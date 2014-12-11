package utils.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import utils.view.IndexPullRefreshListView;

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

		// 每一个动态在主页上显示的内容分，
		public final class ListItemsView {

			public ImageView photoHead;

			public TextView userName;

			public ImageView location;

			public TextView userAddress;

			public TextView dasyaAgo;

			public ImageView clockImage;

			public TextView photoDescribe;

			public ImageView showingPicture;

			/**
			 * 该照片被喜欢的人数
			 */

			public TextView likeSum;

			/**
			 * “Like” 提供点击进入查看那些人喜欢
			 */
			public TextView likeText;

			/**
			 * 该图片被评论的数量
			 */
			public TextView commentSum;

			/**
			 * “Comment”
			 */
			public TextView commentText;

			// 喜欢等图片按钮
			public ImageView loveHeartImage;

			public ImageView commentImage;

			public ImageView sharImage;

			public ImageView moreMenu;

		}

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

			ListItemsView listItemsView;

			if (convertView == null) {
				listItemsView = new ListItemsView();
				convertView = listContainer.inflate(R.layout.index_listview_content, null);
				listItemsView.photoHead = (ImageView) convertView.findViewById(R.id.index_head_photo_thumbnail);
				listItemsView.userName = (TextView) convertView.findViewById(R.id.userNameIndex);
				listItemsView.userName.setTextScaleX(1.2f);
				listItemsView.userAddress = (TextView) convertView.findViewById(R.id.address);
				listItemsView.showingPicture = (ImageView) convertView.findViewById(R.id.showingPicture);
				listItemsView.photoDescribe = (TextView) convertView.findViewById(R.id.index_photo_describe);


				listItemsView.likeSum = (TextView) convertView.findViewById(R.id.index_photo_like_num);
				listItemsView.likeText = (TextView) convertView.findViewById(R.id.index_photo_like_text);
				listItemsView.commentSum = (TextView) convertView.findViewById(R.id.index_photo_comment_num);
				listItemsView.commentText = (TextView) convertView.findViewById(R.id.index_photo_comment_text);


				// 设置控件集到convertView中
				convertView.setTag(listItemsView);
			} else {
				listItemsView = (ListItemsView) convertView.getTag();
			}

			HashMap<String, Object> map = listData.get(position);
			listItemsView.photoHead.setImageResource((Integer) map.get("photoHead"));
			listItemsView.userName.setText((String) map.get("userName"));
			listItemsView.userAddress.setText((String) map.get("userAddress"));
			listItemsView.showingPicture.setImageResource((Integer) map.get("userPicture"));
			listItemsView.photoDescribe.setText((String) map.get("explain"));

			listItemsView.showingPicture.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(getActivity(), PictureScanActivity.class);
					intent.putExtra("position", position);
					startActivity(intent);
					getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			});


			viewTheComment(listItemsView.likeSum);
			viewTheComment(listItemsView.likeText);

			viewTheComment(listItemsView.commentSum);
			viewTheComment(listItemsView.commentText);


			return convertView;
		}
	}

	private List<HashMap<String, Object>> getListItems() {

		items = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("photoHead", R.drawable.head1);
		map.put("userName", "方伦");
		map.put("userAddress", "哥伦贝尔草原");
		map.put("userPicture", R.drawable.index_another_user_picture);
		map.put("explain", "在哥伦贝尔大草原上，呼吸着、感受着");
		items.add(map);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("photoHead", R.drawable.head2);
		map2.put("userName", "桂杰双");
		map2.put("userAddress", "成都");
		map2.put("userPicture", R.drawable.nine);
		map2.put("explain", "赏金怒拿五杀，这个游戏如此的简单啊。。！");
		items.add(map2);


		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("photoHead", R.drawable.images);
		map3.put("userName", "康乐");
		map3.put("userAddress", "常乐村");
		map3.put("userPicture", R.drawable.twleve);
		map3.put("explain", "成信的图书馆，最安静，最喜欢的地方");
		items.add(map3);
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
				try {
					for (int i = 0; i < refreshQueue.size(); i++) {

						items.add(0, refreshQueue.take());
					}

					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
					String date = format.format(new Date());
					// Call onRefreshComplete when the list has been refreshed.
					listView.onRefreshComplete(date);
					listView.invalidateViews();
					listViewAdapter.notifyDataSetChanged();


				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if ("load_completed".equals(message)) { // 加载完成
				try {

					items.add(items.size(), loadQueue.take());
					listView.onLoadMoreComplete();


				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if ("".equals(message)) {

			} else if ("".equals(message)) {

			} else {
				Log.e(TAG, "wrong message in bundle !");
			}
		}
	}

	public static void sendMessage(String msgKey, String msgValue) {

		Bundle mBundle = new Bundle();
		Message mMessage = new Message();

		mBundle.putString(msgKey, msgValue);
		mMessage.setData(mBundle);

		myHandler.sendMessage(mMessage);
	}

	private static BlockingQueue<HashMap<String, Object>> refreshQueue = new ArrayBlockingQueue<HashMap<String, Object>>(10);

	private static BlockingQueue<HashMap<String, Object>> loadQueue = new ArrayBlockingQueue<HashMap<String, Object>>(10);

}
