package utils.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.htk.moment.ui.R;


/**
 * Created by Administrator on 2014/11/14.
 */
public class IndexHeadAndDate extends LinearLayout {

	public IndexHeadAndDate (Context context) {

		super(context);
		initView(context);
	}

	public IndexHeadAndDate (Context context, AttributeSet attrs) {

		super(context, attrs);
		initView(context);
	}

	public IndexHeadAndDate (Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * 加载另一个布局文件
	 *
	 * @param context 谁需要加载当前布局
	 */
	private void initView (Context context) {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);

		LayoutInflater mInflater = LayoutInflater.from(context);

		View v = mInflater.inflate(R.layout.index_user_head_and_date, null);
		addView(v,lp);
	}

}
