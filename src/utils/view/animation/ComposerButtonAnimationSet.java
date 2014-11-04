package utils.view.animation;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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

		if(views[0].getId() == R.id.composer_button_photo){
			addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0 ,Animation.RELATIVE_TO_SELF, -0.9f,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, -0.7f));

		}else if(views[0].getId() == R.id.composer_button_people){
			addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.9f,
					Animation.RELATIVE_TO_SELF,  0.5f, Animation.RELATIVE_TO_SELF, -0.7f));
		}else {
			System.out.println("wrong--------------");
		}
		this.setFillAfter(true);
	}

	@Override
	protected void addOutAnimation(View[] views) {
		if(views[0].getId() == R.id.composer_button_photo){
			System.out.println("相机动画");
			addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.9f ,Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, -0.7f, Animation.RELATIVE_TO_SELF, 0.5f));
		}else if(views[0].getId() == R.id.composer_button_people){
			System.out.println("图片动画");
			addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.9f, Animation.RELATIVE_TO_SELF, 0,
					Animation.RELATIVE_TO_SELF, -0.7f, Animation.RELATIVE_TO_SELF, 0.5f));
		}else {
			System.out.println("wrong--------------");
		}
	}
}
