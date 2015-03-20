package utils.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.htk.moment.ui.FollowActivity;
import com.htk.moment.ui.LaunchActivity;
import com.htk.moment.ui.R;
import come.htk.bean.IndexListViewItemBean;
import come.htk.bean.SmallPhotoBean;
import utils.android.sdcard.Read;
import utils.internet.ConnectionHandler;
import utils.internet.UrlSource;
import utils.json.JSONArray;
import utils.json.JSONObject;
import utils.view.vertical.VerticalViewPager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


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

    public final boolean LOG = true;
    private int userId;

    private int rs_id = 100;

	private VerticalViewPager verticalViewPager;

    private UserHomeBefore before;
    private UserHomeAfter after;

    private ArrayList<SmallPhotoBean> smallPhotoBeanArrayList;

    private BlockingQueue<SmallPhotoBean> smallPhotoBeanBlockingQueue;

	private TextView mPhotoText;

	private TextView mPhotoNum;

	private TextView mFollowText;

	private TextView mFollowNum;

	private TextView mFansText;

	private TextView mFansNum;

    private int photoNum;

    private int followNum;

    private int fansNum;

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
		new MyGetThreeNumThread(userId).start();
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

	private void init() {
        userId = getArguments().getInt("user_id");
        initVerticalPager();
        myHandler = new MyHandler();
	}

	private void initVerticalPager() {

		verticalViewPager = (VerticalViewPager) getView().findViewById(R.id.verticalViewPager);
		before = new UserHomeBefore();
		after = new UserHomeAfter();

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
                        goToCommentDetail(userId);
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
	 * 第一个页面
	 */
	private void goToCommentDetail(int id) {

        Log.i(TAG, "start the first page.");
		//new MyGetThreeNumThread(id).start();
	}

    /**
     * 切换到第二个页面
     */
    private void goToPhotoDetail() {

        Log.i(TAG, "start the second page.");
        // 后续 需增加rs_id参数
        //new MyGetSmallPhotoThread().start();

    }

    private class MyGetThreeNumThread extends Thread {

        private int id;

        public MyGetThreeNumThread(int id){
            this.id = id;
        }
        @Override
        public void run() {

            if (hasThreeData(id)) {
                // （非UI线程）子线程是不能去更新界面
                sendMessage("MeFragment", "threeDataOk");
            }
        }
    }

    /**
     * 查看照片 关注 粉丝 数量
     * @return true 存在那三个数据
     */
    private boolean hasThreeData(int id) {

        HttpURLConnection connection = null;
        JSONObject objectI;
        JSONObject objectO = new JSONObject();
        String response = null;
        try {
            // 取得一个连接 多 part的 connection
            connection = ConnectionHandler.getConnect(UrlSource.GET_THREE_NUMBER, LaunchActivity.JSESSIONID);

            objectO.put("ID", id);

            connection.getOutputStream().write((objectO.toString()).getBytes());
            connection.getOutputStream().flush();
            Log.i(TAG, "server response code: " + connection.getResponseCode());

            String tmp = Read.read(connection.getInputStream());
            objectI = new JSONObject(tmp);

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
                if(LOG)
                    System.out.println("server info : get three number sql error");
                return false;
            } else if (response.equals("JSONFORMATERROR")) {
                if(LOG)
                    System.out.println("server info : get three number json format error");
                return false;
            } else {
                if(LOG)
                    System.out.println("server info : get three number give me nothing");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 写完一次，关闭连接，释放服务器资源
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }

    private class MySmallPhotoPathThread extends Thread {

        int userId;
        int rs_id;

        public MySmallPhotoPathThread(int userId, int rs_id){
            this.userId = userId;
            this.rs_id = rs_id;
        }
		@Override
		public void run() {

			HttpURLConnection smallConnection = ConnectionHandler.getConnect(UrlSource.GET_MORE_SMALL_PHOTO,
					LaunchActivity.JSESSIONID);

			try {
				OutputStream outToServer = smallConnection.getOutputStream();
				JSONObject outToServerDataObj = new JSONObject();
				outToServerDataObj.put("ID", userId);
				outToServerDataObj.put("rs_id", rs_id);
				outToServer.write(outToServerDataObj.toString().getBytes());

				/**
				 * 将得到的数据通过某种形式传递 给 grid View
				 */
                String tmp = Read.read(smallConnection.getInputStream());
				if(LOG)
                    Log.i(TAG, "缩略图信息: = " + tmp);

                handleSmallPhoto(tmp);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        /**
         * 将服务器返回的数据处理后抽象成对象放入队列
         *
         * @param msg 从服务器得到的消息
         */
        private void handleSmallPhoto(String msg){

            smallPhotoBeanBlockingQueue = new ArrayBlockingQueue<SmallPhotoBean>(10);

            JSONArray photos;

            if(msg == null){
                return ;
            }
            if(msg.startsWith("[")){
                photos = new JSONArray(msg);

            } else if(msg.startsWith("{")){
                JSONObject objTmp = new JSONObject(msg);
                if(objTmp.has("status")){
                    String status = objTmp.getString("status");
                    if(status.equals("SQLERROR")){
                        if(LOG){
                            Log.e(TAG, "server SQLERROR");
                        }
                    }else if(status.equals("JSONFORMATERROR")){
                        if(LOG){
                            Log.e(TAG, "JSONFORMATERROR");
                        }
                    }
                }
                return ;
            } else {
                return ;
            }
            int length = photos.length();

            JSONObject objItem;
            SmallPhotoBean smallPhotoBean;

            /**
             * 将得到的数据，存入队列
             */
            if(LOG)
                Log.i(TAG, "我的 长度  嗯？？？ = " + length);
            for(int i = 0; i < length; i++){
                objItem = photos.getJSONObject(i);

                smallPhotoBean = new SmallPhotoBean();

                if(objItem.has("ID")){
                    smallPhotoBean.setUserId(objItem.getInt("ID"));
                }
                if(objItem.has("rs_id")){
                    smallPhotoBean.setRs_id(objItem.getInt("rs_id"));
                }
                if(objItem.has("more_small_photo")){
                    smallPhotoBean.setAddrPath(objItem.getString("more_small_photo"));
                }
                if(objItem.has("album")){
                    smallPhotoBean.setAlbumName(objItem.getString("album"));
                }

                try {
                    smallPhotoBeanBlockingQueue.put(smallPhotoBean);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sendMessage("MeFragment", "pathOk");
            if(LOG)
                System.out.println("获取 全部 路径 消息----");
        }
	}

    /**
     * 通过得到的图片路径去得到缩略图
     */
    private class MyGetSmallPhotoThread extends Thread{

        SmallPhotoBean photoBean;
        HttpURLConnection photoConnection;
        String url;
        @Override
        public void run() {
            if(smallPhotoBeanArrayList == null){
                smallPhotoBeanArrayList = new ArrayList<SmallPhotoBean>();
            }
            while (true){
                try {
                    photoBean = smallPhotoBeanBlockingQueue.take();

                    url = photoBean.getAddrPath();
                    if (url.contains("mks")) {
                        url = UrlSource.getUrl(url);
                    }

                    photoConnection = ConnectionHandler.getGetConnect(url);
                    InputStream is = photoConnection.getInputStream();

                    photoBean.setBitmap(BitmapFactory.decodeStream(is));

                    smallPhotoBeanArrayList.add(photoBean);

                    sendMessage("MeFragment", "photoOk");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	private class MyGridViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

        private ArrayList<SmallPhotoBean> beans;
		public MyGridViewAdapter(Context context , ArrayList<SmallPhotoBean> list) {

			super();
			mInflater = LayoutInflater.from(context);
            beans = list;
		}

        private class MImageView{
            ImageView mImageView;
        }
		@Override
		public int getCount() {

			return beans.size();
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

            MImageView mImageView;

            if(convertView == null){
                mImageView = new MImageView();
                convertView = mInflater.inflate(R.layout.my_small_image_lay, null);
                mImageView.mImageView = (ImageView) convertView.findViewById(R.id.my_small_image);
                convertView.setTag(mImageView);

            }else{
                mImageView = (MImageView) convertView.getTag();
            }
            Bitmap bitmap = beans.get(position).getBitmap();
            mImageView.mImageView.setScaleType(ImageView.ScaleType.CENTER);
            mImageView.mImageView.setPadding(2, 1, 2, 1);
            mImageView.mImageView.setImageBitmap(bitmap);
            return convertView;
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
     * 主要包括自定义的背景图片，自己上传过的所有照片
     * 数量 关注了什么人 被哪些人关注了等信息
	 */
	private class UserHomeBefore extends Fragment {

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
            initBeforeWidgets();
			new MyGetThreeNumThread(userId).start();
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
        private void initBeforeWidgets() {

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

                    if(LOG)
                        System.out.println("点 击  关 注-----------");
                    /**
                     * Toast 弹出消息？
                     *
                     * 还是转换到Activity
                     *
                     */
                    Intent intent = new Intent(getActivity(), FollowActivity.class);
                    startActivity(intent);

                }
            });
        }
	}

	/**
	 * 在 me 界面向上滑动的时候所要显示的界面
     *
     * 主页：  里面所展现的是用户个人所发的所有照动态
     * 缩略图：这些照片所生成的缩略图
     * 地图信息：
     * 用户本身喜欢的照片集合
	 */
	private class UserHomeAfter extends Fragment {

        public MyGridViewAdapter myGridViewAdapter;

        private GridView mGridView;

        private ImageView mScanSelfImageView;

        private ImageView mShowAllPhotoImageView;

        private ListView mListView;

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

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {

            super.onActivityCreated(savedInstanceState);
            // 开启线程，获取缩略图
            if(LOG)
                System.out.println("已开启缩略图线程 - user id = " + userId + "rs_id = " + rs_id);
            initAfterWidgets();
            new MySmallPhotoPathThread(userId, rs_id).start();
        }

        private void initAfterWidgets() {
            mGridView = (GridView) getView().findViewById(R.id.user_home_photo_classes);
            mShowAllPhotoImageView = (ImageView) getView().findViewById(R.id.user_home_index_show_all);
            mScanSelfImageView = (ImageView) getView().findViewById(R.id.user_home_index_scan_self);
            mListView = (ListView) getView().findViewById(R.id.user_home_self_index_list_view);
            smallPhotoBeanArrayList = new ArrayList<SmallPhotoBean>();
            myGridViewAdapter = new MyGridViewAdapter(getActivity(), smallPhotoBeanArrayList);

            mGridView.setAdapter(myGridViewAdapter);
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
    }


    /**
     * 消息接受器
     */
	private class MyHandler extends Handler {

		Bundle mData;

		@Override
		public void handleMessage(Message msg) {

			mData = msg.getData();

            String msgFlag = mData.getString("MeFragment");

			if ("threeDataOk".equals(msgFlag)) {
				mPhotoNum.setText(String.valueOf(photoNum));
				mFollowNum.setText(String.valueOf(followNum));
				mFansNum.setText(String.valueOf(fansNum));

			} else if ("pathOk".equals(msgFlag)) {
                // 路径全部获取到之后，开启获取缩略图线程得到图片
                new MyGetSmallPhotoThread().start();

			} else if("photoOk".equals(msgFlag)){

                Log.i(TAG, "通知缩略图 GRID VIEW 刷新");
                // 将得到的图片显示到GridView、上
                after.myGridViewAdapter.notifyDataSetChanged();
            } else {
				Log.e(TAG, "sub thread send the bad message， please check out !");
			}
		}
	}

	private static MyHandler myHandler;

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
