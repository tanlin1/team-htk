package utils.view.animation;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import com.htk.moment.ui.R;
import utils.view.view.HideImageButton;

public class ButtonAnimationSet extends InOutAnimation {

	public static final int DURATION = 150;

	public ButtonAnimationSet(Direction direction, long l, View views) {
		super(direction, l, new View[]{views});
	}

	public static void startAnimations(ViewGroup viewgroup, InOutAnimation.Direction direction) {
		switch (direction) {
			case IN:
				startAnimationsIn(viewgroup);
				break;
			case OUT:
				startAnimationsOut(viewgroup);
				break;
		}
	}

	private static void startAnimationsIn(ViewGroup viewgroup) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			if (viewgroup.getChildAt(i) instanceof HideImageButton) {
				HideImageButton hideImageButton = (HideImageButton) viewgroup.getChildAt(i);
				ButtonAnimationSet animation =
						new ButtonAnimationSet(InOutAnimation.Direction.IN, DURATION, hideImageButton);
				// 根据需要 添加开始动画延时
				animation.setFillAfter(true);
				animation.setInterpolator(new OvershootInterpolator(2F));
				hideImageButton.startAnimation(animation);
			}
		}
	}

	private static void startAnimationsOut(ViewGroup viewgroup) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			if (viewgroup.getChildAt(i) instanceof HideImageButton) {
				HideImageButton hideImageButton = (HideImageButton) viewgroup.getChildAt(i);
				ButtonAnimationSet animation =
						new ButtonAnimationSet(InOutAnimation.Direction.OUT, DURATION, hideImageButton);
				// 从哪里消失
				animation.setInterpolator(new AnticipateInterpolator(2F));
				hideImageButton.startAnimation(animation);
			}
		}
	}

	@Override
	protected void addInAnimation(View[] views) {

		if (views[0].getId() == R.id.the_camera_button) {

			addAnimation(new TranslateAnimation(45, 0, 90, 0));

		} else if (views[0].getId() == R.id.the_picture_button) {
			addAnimation(new TranslateAnimation(-45, 0, 90, 0));
		}
	}

	@Override
	protected void addOutAnimation(View[] views) {
		if (views[0].getId() == R.id.the_camera_button) {
			addAnimation(new TranslateAnimation(0, 45, 0, 90));
		} else if (views[0].getId() == R.id.the_picture_button) {
			addAnimation(new TranslateAnimation(0, -45, 0, 90));
		}
	}
}
