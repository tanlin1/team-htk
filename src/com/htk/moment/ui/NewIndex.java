package com.htk.moment.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import utils.android.photo.CameraActivity;
import utils.view.animation.ButtonAnimationSet;
import utils.view.animation.InOutAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2014/11/2.
 *
 * @author 谭林
 * @version 1.0
 */
public class NewIndex extends Activity {

	private boolean areButtonsShowing;
	private ViewGroup composerButtonsWrapper;

	private ImageView view_index;
	private ImageView view_message;
	// ImageView
	private View button;
	private View icon;

	private ImageView view_search;
	private ImageView view_me;
	private View view_plus_button;
	// 动画
	private Animation addButtonIn;
	private Animation addButtonOut;

	private ListView listView;
	private MyContentListViewAdaper listViewAdaper;


	private ImageView photoHead;
	private TextView userName;
	private TextView userAddress;
	private TextView focus;
	private ImageView userPicture;
	private TextView explain;
	private ImageView share;
	private ImageView comment;
	private ImageView good;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.index);

		View bar = findViewById(R.id.bottomTarBar);
//		bar.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(NewIndex.this, CameraActivity.class);
//				intent.putExtra("what","camera");
//				startActivity(intent);
//			}
//		});
		view_index = (ImageView) findViewById(R.id.index_index_image);
		view_message = (ImageView) findViewById(R.id.index_message_image);
		view_search = (ImageView) findViewById(R.id.index_search_image);
		view_me = (ImageView) findViewById(R.id.index_about_me_image);


//		View view_plus_button = findViewById(R.id.in_out_button_plus);
//		view_plus_button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				System.out.println("***************");
//			}
//		});

		composerButtonsWrapper = (ViewGroup) findViewById(R.id.view_animal_group);
		button = findViewById(R.id.the_plus_button);
		icon = findViewById(R.id.index_plus_button_image);




//
//		View view = View.inflate(this, R.layout.my_bottom_tabs, null);
//		ImageView image = (ImageView) view.findViewById(R.id.im_change);
//
//		image.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				setContentView(R.layout.index);
//				System.out.println("****************");
//			}
//		});



//		index = (ImageView) findViewById(R.id.index_image_button);
//		message = (ImageView) findViewById(R.id.message_image_button);
//		search = (ImageView) findViewById(R.id.search_image_button);
//		me = (ImageView) findViewById(R.id.me_image_button);
//
//		LayoutInflater listInflater = getLayoutInflater();

		AssetManager assetManager = getAssets();
		Typeface myTextType = Typeface.createFromAsset(assetManager, "fonts/segore_script.ttf");
		TextView textView = (TextView) findViewById(R.id.moment);
		textView.setTypeface(myTextType);

//		init();

		listView = (ListView) findViewById(R.id.indexListView);
		listViewAdaper = new MyContentListViewAdaper(this, getListItems());
		listView.setAdapter(listViewAdaper);

//
//		index = (ImageView) findViewById(R.id.index);
//
//		message = (ImageView) findViewById(R.id.message);
//
//		composerButtonsWrapper = (ViewGroup) findViewById(R.id.composer_buttons_wrapper);
//
//		button = findViewById(R.id.composer_buttons_show_hide_button);
//		icon = findViewById(R.id.composer_buttons_show_hide_button_icon);
//
		addButtonIn = AnimationUtils.loadAnimation(this, R.anim.rotate_story_add_button_in);
		addButtonOut = AnimationUtils.loadAnimation(this, R.anim.rotate_story_add_button_out);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleComposerButtons();
			}
		});
//
//		search = (ImageView) findViewById(R.id.search);
//		me = (ImageView) findViewById(R.id.me);
//
//		listView = (ListView) findViewById(R.id.indexListView);

//		content = (ListView) findViewById(R.id.indexListView);
//		listInflater.inflate(R.layout.indexcontent, null);
//
//		System.out.println("----------");
//		ImageView imageView = (ImageView) content.findViewById(R.id.headPhotoIndex);
//		imageView.setImageResource(R.drawable.image_default);
//		System.out.println("----------");

		toOtherMenu();
		startListener();
	}

	public void init(){

		photoHead = (ImageView) findViewById(R.id.headPhotoIndex);
		userName = (TextView) findViewById(R.id.userNameIndex);
		userAddress = (TextView) findViewById(R.id.address);
		userPicture = (ImageView) findViewById(R.id.userPicture);
		explain = (TextView) findViewById(R.id.thePictureExplain);
		share = (ImageView) findViewById(R.id.share);
		comment = (ImageView) findViewById(R.id.comment);
	}

	private void toOtherMenu() {
		// 进入首页

		view_index.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				view_index.setImageResource(R.drawable.home_after);
				view_message.setImageResource(R.drawable.topic);
				view_search.setImageResource(R.drawable.explore);
				view_me.setImageResource(R.drawable.user);
			}
		});


		// 进入消息中心
		view_message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				view_index.setImageResource(R.drawable.home);
				view_message.setImageResource(R.drawable.topic_after);
				view_search.setImageResource(R.drawable.explore);
				view_me.setImageResource(R.drawable.user);
				//startActivity(new Intent());
			}
		});

		/** 拍照上传或者是选择照片上传
		 * 添加动画，扇形选项
		 */

		// 搜索联系人、热门动态
		view_search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				view_index.setImageResource(R.drawable.home);
				view_message.setImageResource(R.drawable.topic);
				view_search.setImageResource(R.drawable.explore_after);
				view_me.setImageResource(R.drawable.user);
				//startActivity(new Intent());
			}
		});
		// 进入个人中心
		view_me.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				view_index.setImageResource(R.drawable.home);
				view_message.setImageResource(R.drawable.topic);
				view_search.setImageResource(R.drawable.explore);
				view_me.setImageResource(R.drawable.user_after);
				startActivity(new Intent(NewIndex.this, UserHome.class));
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
	}

	private void startListener() {
		int count = composerButtonsWrapper.getChildCount();
		final Intent intentToCamera = new Intent(NewIndex.this, CameraActivity.class);
		for (int i = 0; i < count; i++) {
			View hide = composerButtonsWrapper.getChildAt(i);
			//HideImageButton hide = (HideImageButton) composerButtonsWrapper.getChildAt(i);
			switch (hide.getId()) {
				case R.id.the_camera_button: //照相机
					hide.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							intentToCamera.putExtra("what", "camera");
							startActivity(intentToCamera);
							toggleComposerButtons();
						}
					});
					break;
				case R.id.the_picture_button: // 打开图库
					hide.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							intentToCamera.putExtra("what", "picture");
							startActivity(intentToCamera);
							toggleComposerButtons();
						}
					});
					break;
			}
		}
	}

	private void toggleComposerButtons() {
		if (!areButtonsShowing) {
			ButtonAnimationSet.startAnimations(composerButtonsWrapper, InOutAnimation.Direction.IN);
			icon.startAnimation(addButtonIn);
		} else {
			ButtonAnimationSet.startAnimations(composerButtonsWrapper, InOutAnimation.Direction.OUT);
			icon.startAnimation(addButtonOut);
		}
		areButtonsShowing = !areButtonsShowing;
	}

	private class MyContentListViewAdaper extends BaseAdapter {
		private Context context;
		private List<HashMap<String, Object>> listdata;


		private LayoutInflater listContainer;           //视图容器
		public final class LiStItemsView {

			public ImageView photoHead;
			public TextView userName;
			public TextView userAddress;
			public ImageView userPicture;
			public TextView explain;
			public TextView share;
			public TextView comment;
		}

		public MyContentListViewAdaper(Context context, List<HashMap<String, Object>> content) {
			this.context = context;
			listContainer = LayoutInflater.from(context);
			this.listdata = content;
		}

		@Override
		public int getCount() {
			return listdata.size();
		}

		@Override
		public Object getItem(int position) {
			return listdata.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LiStItemsView listItemsView = null;
			if(convertView == null) {
				listItemsView = new LiStItemsView();
				convertView = listContainer.inflate(R.layout.indexcontent,null);
				listItemsView.photoHead = (ImageView) convertView.findViewById(R.id.headPhotoIndex);
				listItemsView.userName = (TextView) convertView.findViewById(R.id.userNameIndex);
				listItemsView.userAddress = (TextView) convertView.findViewById(R.id.address);
				listItemsView.userPicture = (ImageView) convertView.findViewById(R.id.userPicture);
				listItemsView.explain = (TextView) convertView.findViewById(R.id.thePictureExplain);

				// 设置控件集到convertView中
				convertView.setTag(listItemsView);
			}else {
				listItemsView = (LiStItemsView) convertView.getTag();
			}

			HashMap<String, Object> map = listdata.get(position);
			listItemsView.photoHead.setImageResource((Integer) map.get("photoHead"));
			listItemsView.userName.setText((String) map.get("userName"));
			listItemsView.userAddress.setText((String)map.get("userAddress"));
			listItemsView.userPicture.setImageResource((Integer) map.get("userPicture"));
			listItemsView.explain.setText((String)map.get("explain"));
			//
			//

			return convertView;
		}
	}
	private List<HashMap<String, Object>> getListItems(){
		List<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("photoHead",R.drawable.me);
		map.put("userName","喜欢");
		map.put("userAddress","好望角海域");
		map.put("userPicture",R.drawable.howlay);
		map.put("explain","我说了什么吗？");
		items.add(map);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("photoHead",R.drawable.head2);
		map2.put("userName","不喜欢");
		map2.put("userAddress","成都双流");
		map2.put("userPicture",R.drawable.grass);
		map2.put("explain","我说了很多？");
		items.add(map2);



		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("photoHead",R.drawable.head3);
		map3.put("userName","hellen");
		map3.put("userAddress","北京");
		map3.put("userPicture",R.drawable.grass);
		map3.put("explain","我说了很多aassdfsfdgsdg2222sdasd");
		items.add(map3);
		//
		//
		return items;
	}
}
