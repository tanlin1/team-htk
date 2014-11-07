package com.htk.moment.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Administrator on 2014/11/5.
 */
public class LoadMessage extends Activity{
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
}
