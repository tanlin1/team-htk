package utils.view.animation;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import com.htk.moment.ui.R;
import utils.view.view.HideImageButton;

public class ComposerButtonAnimationSet extends InOutAnimation {

	public static final int DURATION = 200;

	public ComposerButtonAnimationSet(Direction direction, long l, View views) {
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
				ComposerButtonAnimationSet animation =
						new ComposerButtonAnimationSet(InOutAnimation.Direction.IN, DURATION, hideImageButton);
				// 根据需要 添加开始动画延时
				animation.setFillAfter(true);
				animation.setInterpolator(new OvershootInterpolator(3F));
				hideImageButton.startAnimation(animation);
			}
		}
	}

	private static void startAnimationsOut(ViewGroup viewgroup) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			if (viewgroup.getChildAt(i) instanceof HideImageButton) {
				HideImageButton hideImageButton = (HideImageButton) viewgroup.getChildAt(i);
				ComposerButtonAnimationSet animation =
						new ComposerButtonAnimationSet(InOutAnimation.Direction.OUT, DURATION, hideImageButton);
				// 从哪里消失
				animation.setInterpolator(new AnticipateInterpolator(2F));
				hideImageButton.startAnimation(animation);
			}
		}
	}
	@Override
	protected void addInAnimation(View[] views) {

		if (views[0].getId() == R.id.composer_button_photo) {

			addAnimation(new TranslateAnimation(33, 0, 110, 0));
//			addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, fxA, Animation.RELATIVE_TO_SELF, txA,
//					Animation.RELATIVE_TO_SELF, fyA, Animation.RELATIVE_TO_SELF, tyA));

		} else if (views[0].getId() == R.id.composer_button_people) {
			addAnimation(new TranslateAnimation(-33, 0, 110, 0));
//			addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, fxB, Animation.RELATIVE_TO_SELF, txB,
//					Animation.RELATIVE_TO_SELF, fyB, Animation.RELATIVE_TO_SELF, tyB));
		} else {
			System.out.println("wrong--------------");
		}
		this.setFillAfter(true);
	}

	@Override
	protected void addOutAnimation(View[] views) {
		if (views[0].getId() == R.id.composer_button_photo) {
			addAnimation(new TranslateAnimation(0, 33, 0, 110));

//			addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, txA, Animation.RELATIVE_TO_SELF, fxA,
//					Animation.RELATIVE_TO_SELF, tyA, Animation.RELATIVE_TO_SELF, -fyA));
		} else if (views[0].getId() == R.id.composer_button_people) {
			addAnimation(new TranslateAnimation(0, -33, 0, 110));

//			addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, txB, Animation.RELATIVE_TO_SELF, fxB,
//					Animation.RELATIVE_TO_SELF, tyB, Animation.RELATIVE_TO_SELF, -fyB));
		} else {
			System.out.println("wrong--------------");
		}
	}
}
