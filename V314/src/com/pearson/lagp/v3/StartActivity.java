package com.pearson.lagp.v3;

import java.io.IOException;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
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
import org.anddev.andengine.util.Debug;

import android.content.Intent;
import android.content.SharedPreferences;
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
	static protected Music mMusic;
	private SharedPreferences audioOptions, scores;
	private SharedPreferences.Editor audioEditor, scoresEditor;

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
		audioOptions = getSharedPreferences("audio", MODE_PRIVATE);
		audioEditor = audioOptions.edit();
		if (!audioOptions.contains("musicOn")){
			audioEditor.putBoolean("musicOn", true);
			audioEditor.putBoolean("effectsOn", true);
			audioEditor.commit();
		}
		scores = getSharedPreferences("scores", MODE_PRIVATE);
		scoresEditor = scores.edit();
		if (!scores.contains("WAV")){
			scoresEditor.putInt("WhAV-4", 0);
			scoresEditor.putInt("WhAV-3", 0);
			scoresEditor.putInt("WhAV-2", 0);
			scoresEditor.putInt("WhAV-1", 0);
			scoresEditor.putInt("WhAV-0", 0);
			scoresEditor.putInt("Level1-4", 0);
			scoresEditor.putInt("Level1-3", 0);
			scoresEditor.putInt("Level1-2", 0);
			scoresEditor.putInt("Level1-1", 0);
			scoresEditor.putInt("Level1-0", 0);
			scoresEditor.putInt("IV-4", 0);
			scoresEditor.putInt("IV-3", 0);
			scoresEditor.putInt("IV-2", 0);
			scoresEditor.putInt("IV-1", 0);
			scoresEditor.putInt("IV-0", 0);

			scoresEditor.commit();
		}
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera).setNeedsMusic(true));
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
		MusicFactory.setAssetBasePath("mfx/");
		try {
			StartActivity.mMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), getApplicationContext(), "bach_fugue.ogg");
			StartActivity.mMusic.setLooping(true);
		} catch (final Exception e) {
			Debug.e(e);
		}


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
		
		//Start the music!
		mMusic.play();
	   	if (!audioOptions.getBoolean("musicOn", false)) {
	   		 mMusic.pause();
	   	}
		return scene;

	}

	@Override
	public void onLoadComplete() {
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		StartActivity.mMusic.pause();
	}
	
	@Override
	public void onResumeGame() {
		super.onResumeGame();
	   	if (audioOptions.getBoolean("musicOn", false)) StartActivity.mMusic.resume();
		mHandler.postDelayed(mLaunchMenuTask,3000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mLaunchMenuTask);
	}

    private Runnable mLaunchMenuTask = new Runnable() {
        public void run() {
    		Intent myIntent = new Intent(StartActivity.this, MainMenuActivity.class);
    		StartActivity.this.startActivity(myIntent);
        }
     };
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
