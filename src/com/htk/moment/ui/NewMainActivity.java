package com.htk.moment.ui;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import utils.view.CircleView;

public class NewMainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.temp);
        ImageView view = (ImageView) findViewById(R.id.test);
        view.setBackgroundResource(R.drawable.menu);
    }
    public void test(View v){
        CircleView circleView = new CircleView(this);
        circleView.draw(new Canvas());
        v.setBackgroundResource(R.drawable.menu_36);
   }
}
