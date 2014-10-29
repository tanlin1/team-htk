package utils.adapter;

import android.support.v4.app.Fragment;
import android.widget.BaseAdapter;

/**
 * Created by Administrator on 2014/10/27.
 */
public abstract class SlideMenuAdapter extends BaseAdapter{
	/**
	 * Should return fragment for menu item
	 * @param position position of menu item
	 * @param id       identification of menu item
	 * @return Fragment or null. If null will be returned content won't be changed
	 */
	public abstract Fragment getFragment(int position, long id);
}
