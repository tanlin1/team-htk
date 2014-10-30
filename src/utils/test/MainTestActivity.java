package utils.test;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import com.htk.moment.ui.R;

import java.util.List;

/**
 * Created by Administrator on 2014/10/30.
 */
public class MainTestActivity extends Activity {

	PackageManager pm = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.test);

		Button button1 = (Button) findViewById(R.id.test_button_1);
		Button button2 = (Button) findViewById(R.id.test_button_2);
		Button button3 = (Button) findViewById(R.id.test_button_3);
		Button button4 = (Button) findViewById(R.id.test_button_4);
		Button button5 = (Button) findViewById(R.id.test_button_5);
		Button button6 = (Button) findViewById(R.id.test_button_6);


		button1.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {

				pm = MainTestActivity.this.getPackageManager();
				List<PackageInfo> packs = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				String packageName;
				for (PackageInfo pi : packs) {
					packageName = pi.packageName.toLowerCase();
					// 有的手机名字不一样
					if((packageName.contains("gallery") || packageName.contains("camera"))
							&& packageName.contains("android") ){ //Android 表示系统的相机

						Intent intent = pm.getLaunchIntentForPackage(pi.packageName);
						if (intent != null) {
							startActivity(intent);
						}
					}
				}
			}
		});
	}
}
