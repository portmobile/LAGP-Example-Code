package com.pearson.lagp.example;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.StrokeFont;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

public class AndroidGestureExample extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	private Texture mFontTexture, mStrokeFontTexture;
	private Font mFont;
	private StrokeFont mStrokeFont;
	
	private GestureDetector mGestureDetector;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onLoadEngine() {
		mGestureDetector = new GestureDetector(this, new ExampleGestureListener());
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.1f, 0.6f, 0.9f));


		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	  if (mGestureDetector.onTouchEvent(event))
	    return true;
	  else
	    return false;
	}

	class ExampleGestureListener extends GestureDetector.SimpleOnGestureListener{
		  @Override
		  public boolean onSingleTapUp(MotionEvent ev) {
				Toast.makeText(AndroidGestureExample.this, "Single tap up.", Toast.LENGTH_SHORT).show();
		    return true;
		  }

		  @Override
		  public void onShowPress(MotionEvent ev) {
				Toast.makeText(AndroidGestureExample.this, "Show press.", Toast.LENGTH_SHORT).show();
		  }

		  @Override
		  public void onLongPress(MotionEvent ev) {
				Toast.makeText(AndroidGestureExample.this, "Long press.", Toast.LENGTH_SHORT).show();
		  }

		  @Override
		  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				Toast.makeText(AndroidGestureExample.this, "Scroll.", Toast.LENGTH_SHORT).show();
		    return true;
		  }

		  @Override
		  public boolean onDown(MotionEvent ev) {
				Toast.makeText(AndroidGestureExample.this, "Down.", Toast.LENGTH_SHORT).show();
		    return true;
		  }

		  @Override
		  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				Toast.makeText(AndroidGestureExample.this, "Fling.", Toast.LENGTH_SHORT).show();
		    return true;

		  }
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
