package com.pearson.lagp.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AndroidTouchExample extends Activity implements OnTouchListener {
    /** Called when the activity is first created. */

	private LinearLayout linear;
	private ImageView image;

@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);
        image = new ImageView(this);
        image.setImageResource(R.drawable.icon);
        image.setOnTouchListener(this);
        linear.addView(image);
        setContentView(linear);
    }

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			Toast.makeText(this, "Down", Toast.LENGTH_SHORT).show();
		}
		return false;
	}

}