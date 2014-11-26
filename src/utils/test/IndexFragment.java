package utils.test;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.htk.moment.ui.R;
import utils.view.IndexPullRefreshListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * 主页Fragment
 *
 * @author tanlin
 * time：14/11/26
 */
public class IndexFragment extends Fragment {

	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		System.out.println("AAAAAAAAAA____onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		System.out.println("AAAAAAAAAA____onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		System.out.println("AAAAAAAAAA____onCreateView");
		return inflater.inflate(R.layout.after_login_listview_layout, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		test1();
		System.out.println("AAAAAAAAAA____onActivityCreated");
	}

	@Override
	public void onStart() {

		super.onStart();

		System.out.println("AAAAAAAAAA____onStart");
	}

	@Override
	public void onResume() {

		super.onResume();
		System.out.println("AAAAAAAAAA____onResume");
	}

	@Override
	public void onPause() {

		super.onPause();
		System.out.println("AAAAAAAAAA____onPause");
	}

	@Override
	public void onStop() {

		super.onStop();
		System.out.println("AAAAAAAAAA____onStop");
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
		System.out.println("AAAAAAAAAA____onDestroyView");
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		System.out.println("AAAAAAAAAA____onDestroy");
	}

	@Override
	public void onDetach() {

		super.onDetach();
		System.out.println("AAAAAAAAAA____onDetach");
	}
	static IndexPullRefreshListView listView;

	public void test1() {

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
		listView.setAdapter(new MyContentListViewAdapter(getActivity(), getListItems()));
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
			public TextView theCommentText;

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
		public View getView(int position, View convertView, ViewGroup parent) {

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

			return convertView;
		}
	}

	private List<HashMap<String, Object>> getListItems() {

		items = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("photoHead", R.drawable.head2);
		map.put("userName", "喜欢");
		map.put("userAddress", "好望角海域");
		map.put("userPicture", R.drawable.index_aother_user_picture);
		map.put("explain", "这里是用户文字描述");
		items.add(map);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("photoHead", R.drawable.head2);
		map2.put("userName", "不喜欢");
		map2.put("userAddress", "成都双流");
		map2.put("userPicture", R.drawable.index_aother_user_picture);
		map2.put("explain", "我说了很多？");
		items.add(map2);


		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("photoHead", R.drawable.application);
		map3.put("userName", "hellen");
		map3.put("userAddress", "北京");
		map3.put("userPicture", R.drawable.index_aother_user_picture);
		map3.put("explain", "我说了很多aassdfsfdgsdg2222sdasd");
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
				dataMap.put("photoHead", R.drawable.head2);
				dataMap.put("userName", String.valueOf(name++));
				dataMap.put("userAddress", "好莱坞----");
				dataMap.put("userPicture", R.drawable.index_aother_user_picture);
				dataMap.put("explain", "用户文字描述");

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

	static int name = 10;


	private final static int PULL_TO_REFRESH = 3;
	private final static int LOAD_MORE = 4;
}
