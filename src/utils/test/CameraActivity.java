package utils.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.htk.moment.ui.R;

/**
 * Created by Administrator on 2014/10/30.
 */
public class CameraActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
	}
}
