package utils.android.photo;

import com.htk.moment.ui.LaunchActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2014/8/27.
 */
public class UploadTool {
	private static String url = LaunchActivity.url + ":8080/UploadPhoto";

	public static HttpURLConnection getUrlConnection() throws IOException {

		URL url = new URL(UploadTool.url);

		return (HttpURLConnection) url.openConnection();
	}
}
