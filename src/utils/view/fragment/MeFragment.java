package utils.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.htk.moment.ui.R;
import utils.view.vertical.VerticalViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * Author: wangjie  email:wangjie@cyyun.com
 * Date: 13-6-14
 * Time: 下午2:39
 */
public class MeFragment extends Fragment {

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
	    initVerticalPager();
	    addPictureToGridView();
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

	VerticalViewPager verticalViewPager;

	private void initVerticalPager(){
		verticalViewPager = (VerticalViewPager) getView().findViewById(R.id.verticalViewPager);

		verticalViewPager.setAdapter(new FragmentPagerAdapter(getFragmentManager()) {

			@Override
			public Fragment getItem(int position) {

				if(position == 0){
					return UserHomeBefore.getFragment();
				}
				return UserHomeAfter.getFragment();
			}
			@Override
			public int getCount() {

				return 2;
			}
		});
	}


	private void addPictureToGridView(){
		verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}
			@Override
			public void onPageSelected(int position) {

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
	}

	private void goToCommentDetail(){

	}

	private GridView mGridView;
	private ImageView mScanSelfImageView;
	private ImageView mShowAllPhotoImageView;
	private ListView mListView;

	private void goToPhotoDetail(){
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
			mImageView.setPadding(2,1,2,1);
			return view;
		}
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
				convertView = listContainer.inflate(R.layout.user_self_index_list_content, null);
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


			return convertView;
		}
	}

	private static ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
	private List<HashMap<String, Object>> getListItems() {

		items = new ArrayList<HashMap<String, Object>>();

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
	 * A placeholder fragment containing a simple view.
	 */
	public static class UserHomeBefore extends Fragment {
		private static UserHomeBefore mUserHomeBefore;

		// 不创建多个对象，将构造方法私有化
		private UserHomeBefore(){

		}

		public static Fragment getFragment(){
			if(mUserHomeBefore == null){
				mUserHomeBefore = new UserHomeBefore();
			}
			return mUserHomeBefore;
		}
		//三个一般必须重载的方法
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			System.out.println("ExampleFragment--onCreate");
		}

		@Override
		public void onPause()
		{
			super.onPause();
			System.out.println("ExampleFragment--onPause");
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			return inflater.inflate(R.layout.user_home1, container, false);
		}
	}
	public static class UserHomeAfter extends Fragment {
		//三个一般必须重载的方法

		private static UserHomeAfter mUserHomeAfter;
		// 私有化
		private UserHomeAfter(){
		}
		public static Fragment getFragment(){

			if(mUserHomeAfter == null){
				mUserHomeAfter = new UserHomeAfter();
			}
			return mUserHomeAfter;
		}

		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			System.out.println("ExampleFragment--onCreate");
		}

		@Override
		public void onPause()
		{
			super.onPause();
			System.out.println("ExampleFragment--onPause");
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			return inflater.inflate(R.layout.user_home2, container, false);
		}
	}


}
