package com.pearson.lagp.v3;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.ScaleAtModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;

public class OptionsActivity extends BaseGameActivity implements IOnMenuItemClickListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;

	protected static final int MENU_MUSIC = 0;
	protected static final int MENU_EFFECTS = MENU_MUSIC + 1;
	protected static final int MENU_WAV = MENU_EFFECTS + 1;
	protected static final int MENU_IV = MENU_WAV + 1;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;

	protected Scene mMainScene;
	protected Handler mHandler;

	private Texture mMenuBackTexture;
	private TextureRegion mMenuBackTextureRegion;

	protected MenuScene mOptionsMenuScene;
	private TextMenuItem mTurnMusicOff, mTurnMusicOn;
	private TextMenuItem mTurnEffectsOff, mTurnEffectsOn;
	private TextMenuItem mWAV, mIV;
	private IMenuItem musicMenuItem;
	private IMenuItem effectsMenuItem;
	private IMenuItem WAVMenuItem;
	private IMenuItem IVMenuItem;

	private Texture mFontTexture;
	private Font mFont;
	
	private SharedPreferences audioOptions;
	private SharedPreferences.Editor audioEditor;

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
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		/* Load Font/Textures. */
		this.mFontTexture = new Texture(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		FontFactory.setAssetBasePath("font/");
		this.mFont = FontFactory.createFromAsset(this.mFontTexture, this, "Flubber.ttf", 32, true, Color.WHITE);
		this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
		this.mEngine.getFontManager().loadFont(this.mFont);

		this.mMenuBackTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mMenuBackTextureRegion = TextureRegionFactory.createFromAsset(this.mMenuBackTexture, this, "gfx/OptionsMenu/OptionsMenuBk.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mMenuBackTexture);
	
		mTurnMusicOn = new TextMenuItem(MENU_MUSIC, mFont, "Turn Music On");
		mTurnMusicOff = new TextMenuItem(MENU_MUSIC, mFont, "Turn Music Off");
		mTurnEffectsOn = new TextMenuItem(MENU_EFFECTS, mFont, "Turn Effects On");
		mTurnEffectsOff = new TextMenuItem(MENU_EFFECTS, mFont, "Turn Effects Off");
		
		mWAV = new TextMenuItem(MENU_WAV, mFont, "Whack A Vampire");
		mIV = new TextMenuItem(MENU_IV, mFont, "Irate Villagers");
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.createOptionsMenuScene();

		/* Center the background on the camera. */
		final int centerX = (CAMERA_WIDTH - this.mMenuBackTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.mMenuBackTextureRegion.getHeight()) / 2;

		this.mMainScene = new Scene(1);
		/* Add the background and static menu */
		final Sprite menuBack = new Sprite(centerX, centerY, this.mMenuBackTextureRegion);
		mMainScene.getLastChild().attachChild(menuBack);
		mMainScene.setChildScene(mOptionsMenuScene);

		return this.mMainScene;
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
		mMainScene.registerEntityModifier(new ScaleAtModifier(0.5f, 0.0f, 1.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
		mOptionsMenuScene.registerEntityModifier(new ScaleAtModifier(0.5f, 0.0f, 1.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
	}

	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_MUSIC:
				if (audioOptions.getBoolean("musicOn", true)) {
					audioEditor.putBoolean("musicOn", false);
					if (StartActivity.mMusic.isPlaying()) StartActivity.mMusic.pause();
				} else {
					audioEditor.putBoolean("musicOn", true);
					StartActivity.mMusic.resume();
				}
				audioEditor.commit();
				createOptionsMenuScene();
				mMainScene.clearChildScene();
				mMainScene.setChildScene(mOptionsMenuScene);										
				return true;
			case MENU_EFFECTS:
				if (audioOptions.getBoolean("effectsOn", true)) {
					audioEditor.putBoolean("effectsOn", false);
				} else {
					audioEditor.putBoolean("effectsOn", true);
				}
				audioEditor.commit();
				createOptionsMenuScene();
				mMainScene.clearChildScene();
				mMainScene.setChildScene(mOptionsMenuScene);										
				return true;
			case MENU_WAV:
				mMainScene.registerEntityModifier(new ScaleAtModifier(0.5f, 1.0f, 0.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
				mOptionsMenuScene.registerEntityModifier(new ScaleAtModifier(0.5f, 1.0f, 0.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
				mHandler.postDelayed(mLaunchWAVTask,1000);
				return true;
			case MENU_IV:
				mMainScene.registerEntityModifier(new ScaleAtModifier(0.5f, 1.0f, 0.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
				mOptionsMenuScene.registerEntityModifier(new ScaleAtModifier(0.5f, 1.0f, 0.0f, CAMERA_WIDTH/2, CAMERA_HEIGHT/2));
				mHandler.postDelayed(mLaunchIVTask,1000);
				return true;
			default:
				return false;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
	protected void createOptionsMenuScene() {
		this.mOptionsMenuScene = new MenuScene(this.mCamera);

		if (audioOptions.getBoolean("musicOn", true)) {
			musicMenuItem = new ColorMenuItemDecorator( mTurnMusicOff, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		} else {
			musicMenuItem = new ColorMenuItemDecorator( mTurnMusicOn, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		}
		musicMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mOptionsMenuScene.addMenuItem(musicMenuItem);

		if (audioOptions.getBoolean("effectsOn", true)) {
			effectsMenuItem = new ColorMenuItemDecorator( mTurnEffectsOff, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		} else {
			effectsMenuItem = new ColorMenuItemDecorator( mTurnEffectsOn, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);			
		}
		effectsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mOptionsMenuScene.addMenuItem(effectsMenuItem);

		WAVMenuItem = new ColorMenuItemDecorator( mWAV, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		WAVMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mOptionsMenuScene.addMenuItem(WAVMenuItem);

		IVMenuItem = new ColorMenuItemDecorator( mIV, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
		IVMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mOptionsMenuScene.addMenuItem(IVMenuItem);

		this.mOptionsMenuScene.buildAnimations();
		
		this.mOptionsMenuScene.setBackgroundEnabled(false);

		this.mOptionsMenuScene.setOnMenuItemClickListener(this);
	}

    private Runnable mLaunchWAVTask = new Runnable() {
        public void run() {
    		Intent myIntent = new Intent(OptionsActivity.this, WhAVActivity.class);
    		OptionsActivity.this.startActivity(myIntent);
        }
     };

     private Runnable mLaunchIVTask = new Runnable() {
         public void run() {
     		Intent myIntent = new Intent(OptionsActivity.this, IVActivity.class);
     		OptionsActivity.this.startActivity(myIntent);
         }
      };

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
