package com.htk.moment.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import utils.view.vertical.VerticalViewPager;


public class MainActivity extends Activity {

    private static final float MIN_SCALE = 0.75f;
    private static final float MIN_ALPHA = 0.75f;


	VerticalViewPager verticalViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.user_home_index);
		init();
//        verticalViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
//            @Override
//            public void transformPage(View view, float position) {
//                int pageWidth = view.getWidth();
//                int pageHeight = view.getHeight();
//
//                if (position < -1) { // [-Infinity,-1)
//                    // This page is way off-screen to the left.
//                    view.setAlpha(0);
//
//                } else if (position <= 1) { // [-1,1]
//                    // Modify the default slide transition to shrink the page as well
//                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
//                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
//                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
//                    if (position < 0) {
//                        view.setTranslationY(vertMargin - horzMargin / 2);
//                    } else {
//                        view.setTranslationY(-vertMargin + horzMargin / 2);
//                    }
//
//                    // Scale the page down (between MIN_SCALE and 1)
//                    view.setScaleX(scaleFactor);
//                    view.setScaleY(scaleFactor);
//
//                    // Fade the page relative to its size.
//                    view.setAlpha(MIN_ALPHA +
//                            (scaleFactor - MIN_SCALE) /
//                                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));
//
//                } else { // (1,+Infinity]
//                    // This page is way off-screen to the right.
//                    view.setAlpha(0);
//                }
//            }
//        });

    }
	private void init(){
		verticalViewPager = (VerticalViewPager) findViewById(R.id.verticalViewPager);

		verticalViewPager.setAdapter(new DummyAdapter(getFragmentManager()));
	}


    public class DummyAdapter extends FragmentPagerAdapter {

        public DummyAdapter(FragmentManager fm) {
	        super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

	        if(position == 0){
		        return UserHomeBefore.getFragment();
	        }
            return UserHomeAfter.getFragment();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

    }

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
