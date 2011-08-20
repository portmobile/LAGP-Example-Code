package com.pearson.lagp.v3;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.os.Handler;

public class StartActivity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	private Texture mTexture, mBatTexture;
	private TextureRegion mSplashTextureRegion;
	private TiledTextureRegion mBatTextureRegion;
	private Handler mHandler;

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
		mHandler = new Handler();
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		TextureRegionFactory.setAssetBasePath("gfx/Splash/");
		this.mTexture = new Texture(512, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mSplashTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "Splashscreen.png", 0, 0);
		this.mBatTexture = new Texture(256, 256, TextureOptions.DEFAULT);
		this.mBatTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mBatTexture, this, "bat_tiled.png", 0, 0, 2, 2);
		//this.mBatTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "bat_tiled.png", 0, 513, 2, 2);
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		this.mEngine.getTextureManager().loadTexture(this.mBatTexture);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);

		/* Center the splash on the camera. */
		final int centerX = (CAMERA_WIDTH - this.mSplashTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.mSplashTextureRegion.getHeight()) / 2;

		/* Create the background sprite and add it to the scene. */
		final Sprite splash = new Sprite(centerX, centerY, this.mSplashTextureRegion);
		scene.getLastChild().attachChild(splash);
		
		/* Create the animated bat sprite and add to scene */
		final AnimatedSprite bat = new AnimatedSprite(350, 100, this.mBatTextureRegion);
		bat.animate(100);
		scene.getLastChild().attachChild(bat);
		return scene;
	}

	@Override
	public void onLoadComplete() {
		mHandler.postDelayed(mLaunchTask,5000);
	}

    private Runnable mLaunchTask = new Runnable() {
        public void run() {
    		Intent myIntent = new Intent(StartActivity.this, MainMenuActivity.class);
    		StartActivity.this.startActivity(myIntent);
    		StartActivity.this.finish();
        }
     };
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
