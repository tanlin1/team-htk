package com.htk.moment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import utils.SlideMenuActivity;

public class NewMainActivity extends SlideMenuActivity {
    /**
     * Called when the activity is first created.
     */
    private GestureDetector gestureDetector;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_home);
	    gestureDetector = new GestureDetector(this, onGestureListener);
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener(){
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float x = e2.getX() - e1.getX();
			float y = e2.getY() - e1.getY();
			if(Math.abs(x) > 20){
				//右边滑
				if(x > LaunchActivity.screenWidth / 4){
					System.out.println("右边");
					startActivity(new Intent(NewMainActivity.this, test.TheLeftActivity.class));
					overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
				}else if(x < -LaunchActivity.screenWidth / 4){//左边滑动
					System.out.println("左边");
					startActivity(new Intent(NewMainActivity.this, test.TheRightActivity.class));
				}
			}

			return false;
		}
	};

}
