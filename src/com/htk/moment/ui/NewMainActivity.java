package com.htk.moment.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import utils.SlideMenuPanel;
import utils.adapter.SlideMenuAdapter;

public class NewMainActivity extends FragmentActivity {
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		SlideMenuPanel panel = new SlideMenuPanel(this);
		panel.setAdapter(new SlideAdapter(this), getSupportFragmentManager());
		setContentView(panel);
	}


	private class SlideAdapter extends SlideMenuAdapter {
		private final LayoutInflater _mInflater;

		SlideAdapter(Context context) {
			_mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		/**
		 * 当用户点击的时候调用此方法
		 *
		 * @param position position of menu item
		 * @param id       identification of menu item
		 * @return
		 */
		@Override
		public Fragment getFragment(int position, long id) {
//			if (position == 4)
//				return new TestFragment2();
			Bundle bundleData = new Bundle();
			bundleData.putInt("index", position + 1);
			Fragment fragment = new TestFragment();
			fragment.setArguments(bundleData);
			return fragment;
		}

		@Override
		public int getCount() {
			return 6;
		}

		@Override
		public Object getItem(final int index) {
			return null;
		}

		@Override
		public long getItemId(final int index) {
			return index;
		}

		@Override
		public View getView(final int index, View view, final ViewGroup viewGroup) {
			TextView text = null;
			ImageView image = null;
			if (view == null) {
				if (index > 0) {
					view = _mInflater.inflate(R.layout.imageandtext, viewGroup, false);
					text = (TextView) view.findViewById(R.id.menu_text);
					image = (ImageView) view.findViewById(R.id.menu_image);
				} else {
					view = _mInflater.inflate(R.layout.listdata, viewGroup, false);
					image = (ImageView) view.findViewById(R.id.head_image);
				}
			}

			if (text != null) {
				switch (index) {
					case 0:
						break;
					case 1:
						text.setText("Home");
						image.setImageResource(R.drawable.user_home);
						break;
					case 2:
						text.setText("Notify");
						image.setImageResource(R.drawable.notice);
						break;
					case 3:
						text.setText("Message");
						image.setImageResource(R.drawable.chat_message);
						break;
					case 4:
						text.setText("Hot");
						image.setImageResource(R.drawable.hot);
						break;
					case 5:
						text.setText("Setting");
						image.setImageResource(R.drawable.settings);
				}
			}
			return view;
		}
	}

	public static class TestFragment extends Fragment {
		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
			TextView view = new TextView(inflater.getContext());
			view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			view.setBackgroundColor(Color.WHITE);
			view.setTextColor(Color.BLACK);
			view.setGravity(Gravity.CENTER);
			view.setText("Fragment " + getArguments().getInt("index"));
			view.setScrollContainer(true);
			return view;
		}
	}

	public static class TestFragment2 extends ListFragment {
		@Override
		public void onViewCreated(final View view, final Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1);
			for (int index = 0; index < 20; ++index) {
				adapter.add("Content line " + (index + 1));
			}
			setListAdapter(adapter);
		}
	}
}