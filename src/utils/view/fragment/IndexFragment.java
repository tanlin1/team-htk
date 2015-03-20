package utils.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;
import com.htk.moment.ui.*;
import come.htk.bean.IndexInfoBean;
import come.htk.bean.IndexListViewItemBean;
import come.htk.bean.UserInfoBean;
import utils.android.sdcard.Read;
import utils.internet.ConnectionHandler;
import utils.internet.UrlSource;
import utils.json.JSONObject;
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

	public final static String TAG = "IndexFragment";

	public static BlockingQueue<IndexInfoBean> refreshQueue = new ArrayBlockingQueue<IndexInfoBean>(10);

	public static BlockingQueue<IndexInfoBean> loadQueue = new ArrayBlockingQueue<IndexInfoBean>(10);

	public static MyContentListViewAdapter listViewAdapter;

	private static ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

	private static boolean theFirstTimeRefresh = true;

	/**
	 * 模拟栈
	 */
	private BlockingQueue<HashMap<String, Object>> indexDequeStack = new ArrayBlockingQueue<HashMap<String, Object>>(1);


	public static MyHandler myHandler;

	private int loadMoreNum = 0;

	private int refreshNum = 0;


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
		initAll();
		new IndexPullRefreshListView.freshThread("refresh").start();
		listViewAdapter.notifyDataSetChanged();
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	@Override
	public void onResume() {

		super.onResume();
		listViewAdapter.notifyDataSetChanged();
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

	private void initAll() {


		if(myHandler == null){
			myHandler = new MyHandler();
		}
		initListView();
	}

	public void initListView() {

		listView = (IndexPullRefreshListView) this.getView().findViewById(R.id.index_pull_to_refresh_list_view);
		listViewAdapter = new MyContentListViewAdapter(getActivity(), getListItems());
		listView.setAdapter(listViewAdapter);
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

			final IndexListViewItemBean listItem;

			if (convertView == null) {
				listItem = new IndexListViewItemBean();
				convertView = listContainer.inflate(R.layout.index_listview_content, null);
				listItem.photoHead = (ImageView) convertView.findViewById(R.id.index_head_photo_thumbnail);
				listItem.userName = (TextView) convertView.findViewById(R.id.userNameIndex);
				listItem.userName.setTextScaleX(1.2f);
				listItem.userAddress = (TextView) convertView.findViewById(R.id.address);
				listItem.showingPicture = (ImageView) convertView.findViewById(R.id.showingPicture);
				listItem.photoDescribe = (TextView) convertView.findViewById(R.id.index_photo_describe);

				listItem.progressBar = (android.widget.ProgressBar) convertView.findViewById(R.id.index_processBar);
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
				listItem.photoHead.setImageResource(R.drawable.head2);
			}

			listItem.userName.setText((CharSequence) map.get("userName"));
			listItem.userAddress.setText((CharSequence) map.get("userAddress"));
			listItem.showingPicture.setImageBitmap((Bitmap) map.get("userPicture"));
			if (map.get("userPicture") != null) {
				listItem.progressBar.setVisibility(View.GONE);
			}
			listItem.photoDescribe.setText((CharSequence) map.get("myWords"));

			listItem.commentSum.setText(String.valueOf(map.get("commentsNumber")));
			listItem.likeSum.setText(String.valueOf(map.get("commentsNumber")));

			listItem.showingPicture.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(getActivity(), PictureScanActivity.class);

					Integer userId = (Integer) listData.get(position).get("id");
					Integer rs_id = (Integer) listData.get(position).get("rs_id");
					String detail = (String) listData.get(position).get("detailPhoto");

					intent.putExtra("userId", userId);
					intent.putExtra("rs_id", rs_id);

					intent.putExtra("detailPhoto", detail);

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
		//		map.put("myWords", "在哥伦贝尔大草原上，呼吸着、感受着");
		//		items.add(map);
		//
		//		HashMap<String, Object> map2 = new HashMap<String, Object>();
		//		map2.put("photoHead", R.drawable.head2);
		//		map2.put("userName", "桂杰双");
		//		map2.put("userAddress", "成都");
		//		map2.put("userPicture", R.drawable.nine);
		//		map2.put("myWords", "赏金怒拿五杀，这个游戏如此的简单啊。。！");
		//		items.add(map2);
		//
		//
		//		HashMap<String, Object> map3 = new HashMap<String, Object>();
		//		map3.put("photoHead", R.drawable.images);
		//		map3.put("userName", "康乐");
		//		map3.put("userAddress", "常乐村");
		//		map3.put("userPicture", R.drawable.twleve);
		//		map3.put("myWords", "成信的图书馆，最安静，最喜欢的地方");
		//		items.add(map3);

		return items;
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

	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			Bundle dataBundle = msg.getData();
			String message = dataBundle.getString("fresh");
			if ("upLoadOk".equals(message)) {  //上传提示
				/**
				 * 真的可以更新界面了，
				 * 为了界面友好，考虑放一个processBar提示
				 */
				Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
				listViewAdapter.notifyDataSetChanged();

			} else if ("upLoadError".equals(message)) {
				Toast.makeText(getActivity(), "上传出错", Toast.LENGTH_SHORT).show();
				listViewAdapter.notifyDataSetChanged();
			} else if ("load_completed".equals(message)) { // 加载完成

				Toast.makeText(getActivity(), "本次加载有 " + loadMoreNum + " 条数据", Toast.LENGTH_SHORT).show();
				listView.onLoadMoreComplete();
				listView.invalidateViews();
				listViewAdapter.notifyDataSetChanged();
			} else if ("refresh_data_completed".equals(message)) {
				/**
				 * 刷新完毕，服务器给出数据
				 * 客户端开启线程解析
				 *
				 * 1. 解析队列数据
				 *
				 * 2. 存入栈
				 *
				 * 头像：下一个环节处理，设计多个请求
				 */
				//				new PhotoThread(PULL_TO_REFRESH).start();
				//				refreshNum = refreshQueue.size();
				refreshNum = refreshQueue.size();
				if (theFirstTimeRefresh) {
					new QueueToStack().start();
					new StackToUi().start();
					theFirstTimeRefresh = false;
				} else if (refreshNum == 0) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
					String date = format.format(new Date());
					listView.onRefreshComplete(date);
					//Toast.makeText(getActivity(), "当前数据已是最新", Toast.LENGTH_SHORT).show();
				}
				listViewAdapter.notifyDataSetChanged();
			} else if ("indexPhotoOk".equals(message)) {

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
				String date = format.format(new Date());
				listView.onRefreshComplete(date);
				listView.invalidateViews();
				listViewAdapter.notifyDataSetChanged();

			} else if ("indexNameOk".equals(message)) {

				listView.invalidateViews();
				listViewAdapter.notifyDataSetChanged();

			} else if ("load_data_completed".equals(message)) {
				loadMoreNum = loadQueue.size();
				if (loadMoreNum == 0) {
					Toast.makeText(getActivity(), "已经显示所有数据", Toast.LENGTH_SHORT).show();
				}
				listView.onLoadMoreComplete();
				listViewAdapter.notifyDataSetChanged();
			} else if ("sub_thread".equals(message)) {
				listViewAdapter.notifyDataSetChanged();
			} else if("SESSIONERROR".equals(message)){
				Toast.makeText(getActivity(), "SESSION 过期，请重新登录", Toast.LENGTH_SHORT).show();
			} else {
				Log.e(TAG, "wrong message in bundle !");
			}
		}
	}

	/**
	 * 向本消息队列中放入消息，供主线程查询
	 *
	 * @param msgKey   消息键
	 * @param msgValue 消息值(数据)
	 */
	public static void sendMessage(String msgKey, String msgValue) {

		Bundle mBundle = new Bundle();
		Message mMessage = new Message();

		mBundle.putString(msgKey, msgValue);
		mMessage.setData(mBundle);
		myHandler.sendMessage(mMessage);
	}

	/**
	 * 开启线程充队列中获取数据，并解析存入栈中
	 * 因为得到的是最新数据
	 * 应该显示到屏幕的最顶部
	 */
	private class QueueToStack extends Thread {

		@Override
		public void run() {

			HttpURLConnection userInfoConnection;
			IndexInfoBean indexDataBean;
			UserInfoBean userInfo;
			while (true) {
				try {
					indexDataBean = refreshQueue.take();
					userInfo = new UserInfoBean();

					userInfo.setID(indexDataBean.getId());

					userInfoConnection = ConnectionHandler.getConnect(UrlSource.GET_USER_INFO, LaunchActivity.JSESSIONID);

					JSONObject userOut = new JSONObject();
					JSONObject userObj;

					userInfoConnection.getOutputStream();
					userOut.put("ID", userInfo.getID());

					userInfoConnection.getOutputStream().write(userOut.toString().getBytes());

					String inString = Read.read(userInfoConnection.getInputStream());
					userObj = new JSONObject(inString);
					if (userObj.has("name")) {
						userInfo.setName(userObj.getString("name"));
					}

					HashMap<String, Object> indexNameBundle = new HashMap<String, Object>();
					indexNameBundle.put("indexDataBean", indexDataBean);
					indexNameBundle.put("userInfoBean", userInfo);
					// 存入栈
					indexDequeStack.put(indexNameBundle);

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 阻塞栈，（/队列）
	 * <p/>
	 * 将“栈”里面的数据提取出来，获取图片
	 */
	private class StackToUi extends Thread {

		IndexInfoBean indexBean;

		private HashMap<String, Object> bundle;

		HttpURLConnection photoConnection = null;

		String url;

		@Override
		public void run() {

			while (true) {
				try {
					bundle = indexDequeStack.take();
					changeBeanToItems(bundle, false);

					sendMessage("fresh", "indexNameOk");

					indexBean = (IndexInfoBean) bundle.get("indexDataBean");

					url = indexBean.getViewPhoto();
					if (url.contains("mks")) {
						url = UrlSource.getUrl(url);
					}
					photoConnection = ConnectionHandler.getGetConnect(url);
					InputStream is = photoConnection.getInputStream();
					indexBean.setPictureShow(BitmapFactory.decodeStream(is));

					changeBeanToItems(bundle, true);
					bundle = null;
					sendMessage("fresh", "indexPhotoOk");

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (photoConnection != null) {
						photoConnection.disconnect();
					}
				}
			}
		}
	}

	private void changeBeanToItems(HashMap<String, Object> bundle, boolean photoOk) {

		UserInfoBean userInfo;
		IndexInfoBean indexInfo;
		HashMap<String, Object> map;
		indexInfo = (IndexInfoBean) bundle.get("indexDataBean");
		userInfo = (UserInfoBean) bundle.get("userInfoBean");

		if (!photoOk) {
			map = new HashMap<String, Object>();
			// 用户
            map.put("id", userInfo.getID());
			map.put("userName", userInfo.getName());
			map.put("userAddress", "呼伦贝尔草原");
			// 资源
			map.put("rs_id", indexInfo.getRs_id());
			map.put("sharesNumber", indexInfo.getSharesNumber());
			map.put("commentsNumber", indexInfo.getCommentNumber());
			map.put("likesNumber", indexInfo.getLikeNumber());
			map.put("myWords", indexInfo.getMyWords());
			map.put("time", indexInfo.getTime());
			map.put("album", indexInfo.getAlbum());

			map.put("viewPhoto", indexInfo.getViewPhoto());
			map.put("detailPhoto", indexInfo.getDetailPhoto());
			if (indexInfo.getIsLocated().equals("true")) {
				map.put("location", indexInfo.getLocation());
			}
			if (items.size() == 0) {
				items.add(0, map);
			} else if (indexInfo.getRs_id() > (Integer) items.get(items.size() - 1).get("rs_id")) {
				items.add(0, map);
			} else {
				items.add(items.size(), map);
			}
		} else {
			for (HashMap<String, Object> item : items) {
				map = item;
				if (map.get("rs_id").equals(indexInfo.getRs_id())) {
					map.put("userPicture", indexInfo.getPictureShow());
				}
			}
		}
		IndexPullRefreshListView.rs_id = (Integer) items.get(0).get("rs_id");
	}
}
