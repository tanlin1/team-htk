package utils.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.htk.moment.ui.R;
import utils.view.view.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 消息中心
 *
 * 一个大容器，里面放有小容器，ViewPager
 *
 * @author Administrator tanlin
 */
public class MessageFragment extends Fragment {

	private ViewPager mViewPager;

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

		View convertView = inflater.inflate(R.layout.message_index, container, false);

		mViewPager = (ViewPager) convertView.findViewById(R.id.index_message_content_page);

		mViewPager.setAdapter(new android.support.v4.app.FragmentStatePagerAdapter(getChildFragmentManager()) {

			@Override
			public Fragment getItem(int position) {

				if (position == 0) {
					return NoticeFrag.getFragment();
				}
				return PrivateContentFrag.getFragment();
			}
			@Override
			public int getCount() {

				return 2;
			}
		});
		mViewPager.setCurrentItem(0, false);

		return convertView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
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


	/**
	 * 通知容器
	 */
	private final static class NoticeFrag extends Fragment {

		// 不创建多个对象，将构造方法私有化
		private static NoticeFrag mNoticeFrag;
		private NoticeFrag() {

		}

		public static Fragment getFragment() {

			if (null == mNoticeFrag) {
				mNoticeFrag = new NoticeFrag();
			}
			return mNoticeFrag;
		}
		//三个一般必须重载的方法
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
			View convertView = inflater.inflate(R.layout.message_index_page_notice_list, container, false);
			ListView mListView = (ListView) convertView.findViewById(R.id.notice_list_view);
			mListView.setAdapter(new MyListViewAdapter(getActivity(), MessageFragment.getSomeStaticData()));

			return convertView;
		}
	}

	/**
	 * 私信容器
	 */
	private final static class PrivateContentFrag extends Fragment {
		//三个一般必须重载的方法

		private static PrivateContentFrag mPrivateContentFrag;
		private PrivateContentFrag() {

		}
		public static Fragment getFragment() {

			if (mPrivateContentFrag == null) {
				mPrivateContentFrag = new PrivateContentFrag();
			}
			return mPrivateContentFrag;
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
			View convertView = inflater.inflate(R.layout.message_index_page_private_list, container, false);
			ListView mListView = (ListView) convertView.findViewById(R.id.private_list_view);
			mListView.setAdapter(new TestA(getActivity(), MessageFragment.getSomeStaticData()));
			return convertView;
		}
	}


	/**
	 * 通知页适配器
	 */
	private static class MyListViewAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, Object>> mData;
		private LayoutInflater mInflater;


		private class MyListViewHolder {

			CircleImageView mCircleImagePhotoHead;
			TextView mUserName;
			TextView mComment;
			TextView mTimeOfComment;
			ImageView mUserPisture;
		}

		public MyListViewAdapter(Context context, ArrayList<HashMap<String, Object>> maps) {

			mData = maps;
			mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {

			return mData.size();
		}
		@Override
		public Object getItem(int position) {

			return mData.get(position);
		}
		@Override
		public long getItemId(int position) {

			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			MyListViewHolder mMyListViewHolder;
			if (convertView == null) {
				mMyListViewHolder = new MyListViewHolder();
				convertView = mInflater.inflate(R.layout.message_index_page_notice_content, null);
				mMyListViewHolder.mCircleImagePhotoHead = (CircleImageView) convertView.findViewById(R.id.message_index_user_photo_head);
				mMyListViewHolder.mComment = (TextView) convertView.findViewById(R.id.message_index_comment_text);
				mMyListViewHolder.mTimeOfComment = (TextView) convertView.findViewById(R.id.message_index_time_of_comment);
				mMyListViewHolder.mUserName = (TextView) convertView.findViewById(R.id.message_index_user_name);
				mMyListViewHolder.mUserPisture = (ImageView) convertView.findViewById(R.id.message_index_picture_commented_by_someone);
				convertView.setTag(mMyListViewHolder);
			} else {
				mMyListViewHolder = (MyListViewHolder) convertView.getTag();
			}
			mMyListViewHolder.mCircleImagePhotoHead.setImageResource(R.drawable.head2);
			mMyListViewHolder.mUserPisture.setImageResource(R.drawable.index_a_usr_picture);

			return convertView;
		}
	}
	/**
	 * @return 带有静态数据的listMap
	 */
	private static ArrayList<HashMap<String, Object>> getSomeStaticData() {

		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> dataMap;
		for (int i = 0; i < 10; i++) {
			dataMap = new HashMap<String, Object>();
			dataMap.put("photo_head", R.drawable.head2);
			data.add(dataMap);
		}
		return data;
	}
	/**
	 * 私信页
	 *
	 * 准备更新内容，现在实现的跟通知页的内容一样
	 */
	private static class TestA extends MyListViewAdapter {

		public TestA(Context context, ArrayList<HashMap<String, Object>> maps) {

			super(context, maps);
		}
		@Override
		public int getCount() {

			return super.getCount();
		}
		@Override
		public Object getItem(int position) {

			return super.getItem(position);
		}
		@Override
		public long getItemId(int position) {

			return super.getItemId(position);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {




			return super.getView(position, convertView, parent);
		}
	}
	/**
	 * 点击通知或者私信
	 */
	private void testButton(){

	}

}
