package utils.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import com.htk.moment.ui.R;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Administrator on 2014/11/21.
 */
public class LoadDataService extends Service {


	MyBinder myBinder = new MyBinder();

	public class MyBinder extends Binder{
		public LoadDataService getService(){
			return LoadDataService.this;
		}
	}


	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public IBinder onBind(Intent intent) {

		return myBinder;
	}



	public HashMap<String, Object> function(){
		System.out.println("what   hold   ?");

		HashMap<String, Object> dataMap = new HashMap<String, Object>();

		dataMap.put("photoHead", R.drawable.head2);
		dataMap.put("userName", String.valueOf(new Random(100)));
		dataMap.put("userAddress", "好莱坞----");
		dataMap.put("userPicture", R.drawable.index_aother_user_picture);
		dataMap.put("explain", "用户文字描述");

		return dataMap;
	}
}
