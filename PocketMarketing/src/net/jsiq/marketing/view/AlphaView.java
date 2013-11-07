package net.jsiq.marketing.view;

import net.jsiq.marketing.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class AlphaView extends ImageView {
	private Drawable alphaDrawable;
	private boolean showBkg;
	private int choose;
	private String[] ALPHAS;
	private OnAlphaChangedListener listener;

	public AlphaView(Context context) {
		super(context);
		initAlphaView();
	}

	public AlphaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAlphaView();
	}

	public AlphaView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAlphaView();
	}

	private void initAlphaView() {
		showBkg = false;
		choose = -1;
		setImageResource(R.drawable.alpha_normal);
		alphaDrawable = getDrawable();

		ALPHAS = new String[28];
		ALPHAS[0] = " ";
		ALPHAS[27] = "#";
		for (int i = 0; i < 26; i++) {
			ALPHAS[i + 1] = String.valueOf((char) (65 + i));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (showBkg) {
			setImageResource(R.drawable.alpha_pressed);
			alphaDrawable = getDrawable();

			alphaDrawable.setBounds(0, 0, getWidth(), getHeight());
		} else {
			setImageResource(R.drawable.alpha_normal);
			alphaDrawable = getDrawable();

			alphaDrawable.setBounds(0, 0, getWidth(), getHeight());
		}

		canvas.save();
		alphaDrawable.draw(canvas);
		canvas.restore();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final float y = event.getY();
		final int oldChoose = choose;
		final int c = (int) (y / getHeight() * 28);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < ALPHAS.length) {
					listener.OnAlphaChanged(ALPHAS[c], c);
					choose = c;
				}
			}
			invalidate();
			break;

		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c >= 0 && c < ALPHAS.length) {
					listener.OnAlphaChanged(ALPHAS[c], c);
					choose = c;
				}
			}
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	public void setOnAlphaChangedListener(OnAlphaChangedListener listener) {
		this.listener = listener;
	}

	public interface OnAlphaChangedListener {
		public void OnAlphaChanged(String s, int index);
	}

}
