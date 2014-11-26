package utils.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.htk.moment.ui.R;

/**
 * 主要是为了home页面的menu滑动按钮
 * Created by Administrator on 2014/11/24.
 */
public class UserHomeMenuLayout extends LinearLayout {
	public UserHomeMenuLayout(Context context) {
		super(context);
		init(context);
	}

	public UserHomeMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public UserHomeMenuLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private void init( Context context) {
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);

		LayoutInflater mInflater = LayoutInflater.from(context);

		View v = mInflater.inflate(R.layout.user_home_menu_layout, null);
		addView(v, lp);
	}
}
