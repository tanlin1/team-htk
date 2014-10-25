package com.htk.moment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;

public class NewMainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.temp);
	    System.out.println("++++++++++++++++");
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action){
			case MotionEvent.ACTION_HOVER_MOVE:
				System.out.println("*****************");
				Intent intent = new Intent();
				intent.setClass(this,TheLeftActivity.class);
				break;
		}

		return false;
	}
}
