package utils.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2014/11/6.
 */
public class MyImageView extends ImageView {
	private static Paint paint = new Paint();

	public MyImageView(Context context) {
		super(context);
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void onDraw(Canvas canvas) {
		Rect m_rect = canvas.getClipBounds();
		canvas.save();

		paint.setColor(0xff000000);

		float m_left = m_rect.left;
		float m_top = m_rect.top;
		float m_right = m_rect.right;
		float m_bottom = (1.2f)*(m_rect.bottom);

		RectF m_rectf = new RectF(m_left,m_top,m_right,m_bottom);
		canvas.drawOval(m_rectf, paint);

		canvas.restore();

		super.onDraw(canvas);
	}
}
