package utils.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.htk.moment.ui.PictureScanActivity;
import com.htk.moment.ui.R;

import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2014/12/11.
 */
public class MyContentListViewAdapter extends BaseAdapter {

	private Context mContext;

	private List<HashMap<String, Object>> listData;

	//视图容器
	private LayoutInflater listContainer;

	// 每一个动态在主页上显示的内容分，
	public final class ListItemsView {

		public ImageView photoHead;

		public TextView userName;

		public ImageView location;

		public TextView userAddress;

		public TextView dasyaAgo;
		public ImageView clockImage;

		public TextView photoDescribe;

		public ImageView showingPicture;

		/**
		 * 该照片被喜欢的人数
		 */

		public TextView likeSum;
		/**
		 * “Like” 提供点击进入查看那些人喜欢
		 */
		public TextView likeText;
		/**
		 * 该图片被评论的数量
		 */
		public TextView commentSum;
		/**
		 * “Comment”
		 */
		public TextView commentText;

		// 喜欢等图片按钮
		public ImageView loveHeartImage;
		public ImageView commentImage;
		public ImageView sharImage;
		public ImageView moreMenu;

	}

	public MyContentListViewAdapter(Context context, List<HashMap<String, Object>> content) {

		mContext = context;
		listContainer = LayoutInflater.from(context);
		this.listData = content;
	}

	@Override
	public int getCount() {

		return listData.size();
	}

	@Override
	public Object getItem(int position) {

		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ListItemsView listItemsView;

		if (convertView == null) {
			listItemsView = new ListItemsView();
			convertView = listContainer.inflate(R.layout.index_listview_content, null);
			listItemsView.photoHead = (ImageView) convertView.findViewById(R.id.index_head_photo_thumbnail);
			listItemsView.userName = (TextView) convertView.findViewById(R.id.userNameIndex);
			listItemsView.userName.setTextScaleX(1.2f);
			listItemsView.userAddress = (TextView) convertView.findViewById(R.id.address);
			listItemsView.showingPicture = (ImageView) convertView.findViewById(R.id.showingPicture);
			listItemsView.photoDescribe = (TextView) convertView.findViewById(R.id.index_photo_describe);


			listItemsView.likeSum = (TextView) convertView.findViewById(R.id.index_photo_like_num);
			listItemsView.likeText = (TextView) convertView.findViewById(R.id.index_photo_like_text);
			listItemsView.commentSum = (TextView) convertView.findViewById(R.id.index_photo_comment_num);
			listItemsView.commentText = (TextView) convertView.findViewById(R.id.index_photo_comment_text);


			// 设置控件集到convertView中
			convertView.setTag(listItemsView);
		} else {
			listItemsView = (ListItemsView) convertView.getTag();
		}

		HashMap<String, Object> map = listData.get(position);
		listItemsView.photoHead.setImageResource((Integer) map.get("photoHead"));
		listItemsView.userName.setText((String) map.get("userName"));
		listItemsView.userAddress.setText((String) map.get("userAddress"));
		listItemsView.showingPicture.setImageResource((Integer) map.get("userPicture"));
		listItemsView.photoDescribe.setText((String) map.get("explain"));

		listItemsView.showingPicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PictureScanActivity.class);
				intent.putExtra("position", position);
				mContext.startActivity(intent);
			}
		});

		return convertView;
	}
}
