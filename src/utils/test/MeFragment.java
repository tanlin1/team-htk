package utils.test;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.htk.moment.ui.R;
import utils.view.vertical.VerticalViewPager;


/**
 * Created with IntelliJ IDEA.
 * Author: wangjie  email:wangjie@cyyun.com
 * Date: 13-6-14
 * Time: 下午2:39
 */
public class MeFragment extends Fragment implements View.OnTouchListener{
    FragmentManager manager;
	public FragmentManager getManager() {

		return manager;
	}
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("EEEEEEEEEEEE____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("EEEEEEEEEEEE____onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("EEEEEEEEEEEE____onCreateView");
        return inflater.inflate(R.layout.user_home_index, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
	    init();
        System.out.println("EEEEEEEEEEEE____onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("EEEEEEEEEEEE____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("EEEEEEEEEEEE____onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("EEEEEEEEEEEE____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("EEEEEEEEEEEE____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("EEEEEEEEEEEE____onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("EEEEEEEEEEEE____onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("EEEEEEEEEEEE____onDetach");
    }

	VerticalViewPager verticalViewPager;

	private void init(){
		verticalViewPager = (VerticalViewPager) getView().findViewById(R.id.verticalViewPager);

		verticalViewPager.setAdapter(new android.support.v4.app.FragmentPagerAdapter(getFragmentManager()) {

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
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		System.out.println("------------" + v + "-------" + event.getAction());
		return false;
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
		// 不创建多个对象，将构造方法私有化
		private UserHomeBefore(){

		}

		public static Fragment getFragment(){
			return new UserHomeBefore();
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

		private UserHomeAfter(){
		}
		public static Fragment getFragment(){
			return new UserHomeAfter();
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
