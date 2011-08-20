package com.pearson.lagp.example;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnAreaTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.opengl.texture.BuildableTexture;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.builder.ITextureBuilder.TextureSourcePackingException;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

public class AndEngineGestureExample extends BaseGameActivity implements IOnAreaTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "GestureExample";

	// ===========================================================
	// Fields
	// ===========================================================

	private Handler mHandler;
	
	protected Camera mCamera;

	protected Scene mMainScene;

	private BuildableTexture mIconTexture;
	private TextureRegion mIconTextureRegion;
	
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
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		mIconTexture = new BuildableTexture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mIconTextureRegion = TextureRegionFactory.createFromResource(mIconTexture, this, R.drawable.icon);
		this.mEngine.getTextureManager().loadTexture(this.mIconTexture);
	}
	
	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.1f, 0.6f, 0.9f));
		scene.setTouchAreaBindingEnabled(true);
		scene.setOnAreaTouchListener(this);
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
	

	@Override
	public boolean onSingleTap() {
//	  if (mSurfaceGestureDetector.onTouchEvent(event))
	    return true;
	  else
	    return false;
	}

	class ExampleGestureListener extends GestureDetector.SimpleOnGestureListener{
		  @Override
		  public boolean onSingleTapUp(MotionEvent ev) {
				Toast.makeText(AndEngineGestureExample.this, "Single tap up.", Toast.LENGTH_SHORT).show();
		    return true;
		  }

		  @Override
		  public void onShowPress(MotionEvent ev) {
				Toast.makeText(AndEngineGestureExample.this, "Show press.", Toast.LENGTH_SHORT).show();
		  }

		  @Override
		  public void onLongPress(MotionEvent ev) {
				Toast.makeText(AndEngineGestureExample.this, "Long press.", Toast.LENGTH_SHORT).show();
		  }

		  @Override
		  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				Toast.makeText(AndEngineGestureExample.this, "Scroll.", Toast.LENGTH_SHORT).show();
		    return true;
		  }

		  @Override
		  public boolean onDown(MotionEvent ev) {
				Toast.makeText(AndEngineGestureExample.this, "Down.", Toast.LENGTH_SHORT).show();
		    return true;
		  }

		  @Override
		  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				Toast.makeText(AndEngineGestureExample.this, "Fling.", Toast.LENGTH_SHORT).show();
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
