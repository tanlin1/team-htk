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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.htk.moment.ui.R;
import utils.view.vertical.VerticalViewPager;


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

	GridView mGridView;
	private void goToPhotoDetail(){
		mGridView = (GridView) getView().findViewById(R.id.user_home_photo_classes);
		mGridView.setAdapter(new MyGridViewAdapter(getActivity()));
		mGridView.setHorizontalSpacing(1);
		mGridView.setVerticalSpacing(1);
	}


	private class MyGridViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyGridViewAdapter(Context context) {

			super();
			mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {

			return 20;
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

			View view = mInflater.inflate(R.layout.photo_choose_layout,null);
			ImageView mImageView = (ImageView) view.findViewById(R.id.image);
			mImageView.setImageResource(R.drawable.head2);
			mImageView.setScaleType(ImageView.ScaleType.CENTER);
			mImageView.setPadding(2,1,2,1);
			return view;
		}
	}

//
//	public class DummyAdapter extends FragmentPagerAdapter {
//
//		public DummyAdapter(FragmentManager fm) {
//			super(fm);
//		}
//
//		@Override
//		public android.app.Fragment getItem(int position) {
//			// getItem is called to instantiate the fragment for the given page.
//			// Return a PlaceholderFragment (defined as a static inner class below).
//
//			if(position == 0){
//				return UserHomeBefore.getFragment();
//			}
//			return UserHomeAfter.getFragment();
//		}
//
//		@Override
//		public int getCount() {
//			// Show 2 total pages.
//			return 2;
//		}
//
//	}

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
