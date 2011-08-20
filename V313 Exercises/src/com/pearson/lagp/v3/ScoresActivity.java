package com.pearson.lagp.v3;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.SharedPreferences;
import android.graphics.Color;

public class ScoresActivity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;

	protected Scene mScoresScene;
	private Text mTitle, mWAV, mLevel1;;
	private Texture mFontTexture;
	private Font mFont;
	private SharedPreferences scores;
	private SharedPreferences.Editor scoresEditor;

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
		scores = getSharedPreferences("scores", MODE_PRIVATE);
		scoresEditor = scores.edit();
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		/* Load Font/Textures. */
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		FontFactory.setAssetBasePath("font/");
		this.mFont = FontFactory.createFromAsset(this.mFontTexture, this, "Flubber.ttf", 32, true, Color.RED);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);
	}

	@Override
	public Scene onLoadScene() {
		/* Center the background on the camera. */
		final int centerX = (CAMERA_WIDTH) / 2;
		final int centerY = (CAMERA_HEIGHT) / 2;

		this.mScoresScene = new Scene(1);
		/* Add the background and scores */
		mScoresScene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));
		mTitle = new Text( centerX - 200, centerY - 100, mFont, "Scores");
		mWAV = new Text( centerX - 100, centerY - 50, mFont, "WAV\t\t" + scores.getInt("WAV", -1));
		mLevel1 = new Text( centerX - 100, centerY, mFont, "Level1\t\t" + scores.getInt("Level1", -1));
		mScoresScene.getLastChild().attachChild(mTitle);
		mScoresScene.getLastChild().attachChild(mWAV);
		mScoresScene.getLastChild().attachChild(mLevel1);
		return this.mScoresScene;
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
