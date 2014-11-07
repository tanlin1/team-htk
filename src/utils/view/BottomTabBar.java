package utils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.htk.moment.ui.R;

/**
 * Created by Administrator on 2014/11/6.
 */
public class BottomTabBar extends LinearLayout {

	public BottomTabBar(Context context) {
		super(context);
		init(context);
	}

	public BottomTabBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}

	private void init(Context context) {
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);

		LayoutInflater mInflater = LayoutInflater.from(context);

		View v = mInflater.inflate(R.layout.my_bottom_tabs, null);
		addView(v,lp);
	}
}
