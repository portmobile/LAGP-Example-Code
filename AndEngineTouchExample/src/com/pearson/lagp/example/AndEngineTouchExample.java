package com.pearson.lagp.example;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.BuildableTexture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.builder.ITextureBuilder.TextureSourcePackingException;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class AndEngineTouchExample extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "AndEngineTouchExample";

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;

	protected Scene mMainScene;
	protected Sprite mIcon;

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
		mIconTextureRegion = TextureRegionFactory.createFromAsset(this.mIconTexture, this, "icon.png");
		   try {
			      mIconTexture.build(new BlackPawnTextureBuilder(2));
			   } catch (final TextureSourcePackingException e) {
			      Log.d(tag, "Sprites won't fit in mIconTexture");
			   }
		this.mEngine.getTextureManager().loadTexture(this.mIconTexture);
	}
	
	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.1f, 0.6f, 0.9f));
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
				switch(pSceneTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
						Toast.makeText(AndEngineTouchExample.this, "Scene touch DOWN", Toast.LENGTH_SHORT).show();
						break;
					case TouchEvent.ACTION_UP:
						Toast.makeText(AndEngineTouchExample.this, "Scene touch UP", Toast.LENGTH_SHORT).show();
						break;
				}
				return true;
			}
		});
		
		mIcon = new Sprite(100, 100, this.mIconTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pAreaTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					Toast.makeText(AndEngineTouchExample.this, "Sprite touch DOWN", Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_UP:
					Toast.makeText(AndEngineTouchExample.this, "Sprite touch UP", Toast.LENGTH_SHORT).show();
					break;
				}
				return true;
			}
		};
		
		scene.getLastChild().attachChild(mIcon);		
		scene.registerTouchArea(mIcon);
		scene.setTouchAreaBindingEnabled(true);

		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
	

	
	

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
