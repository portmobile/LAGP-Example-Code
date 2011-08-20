package com.pearson.lagp.v3;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.BuildableTexture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.builder.ITextureBuilder.TextureSourcePackingException;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.util.Log;

public class SpriteTestActivity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "SpriteTestActivity";

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;

	protected Scene mMainScene;

	private BuildableTexture mTestTexture;
	private TextureRegion mMadMatTextureRegion;
	private TextureRegion mHatchetTextureRegion;
	
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
		/* Load Textures. */
		TextureRegionFactory.setAssetBasePath("gfx/SpriteTest/");
		mTestTexture = new BuildableTexture(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mMadMatTextureRegion = TextureRegionFactory.createFromAsset(mTestTexture, this, "madmat.png");
		mHatchetTextureRegion = TextureRegionFactory.createFromAsset(mTestTexture, this, "hatchet40.png");
		   try {
			   mTestTexture.build(new BlackPawnTextureBuilder(2));
			   } catch (final TextureSourcePackingException e) {
			      Log.d(tag, "Sprites won't fit in mTestTexture");
			   }
		this.mEngine.getTextureManager().loadTexture(this.mTestTexture);
		}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));

		/* Center the camera. */
		final int centerX = CAMERA_WIDTH  / 2;
		final int centerY = CAMERA_HEIGHT / 2;

		/* Create the sprites and add them to the scene. */
		final Sprite madMat0 = new Sprite(centerX - (mMadMatTextureRegion.getWidth() / 2) - 100.0f, 
							centerY - (mMadMatTextureRegion.getHeight() / 2), mMadMatTextureRegion);
		scene.getLastChild().attachChild(madMat0);
		final Sprite hatchet0 = new Sprite(madMat0.getInitialX() + 44.0f, madMat0.getInitialY() + 20.0f, mHatchetTextureRegion);
		scene.getLastChild().attachChild(hatchet0);
		
		final Sprite madMat1 = new Sprite(centerX - (mMadMatTextureRegion.getWidth() / 2), 
				centerY - (mMadMatTextureRegion.getHeight() / 2), mMadMatTextureRegion);
		scene.getLastChild().attachChild(madMat1);
		final Sprite hatchet1 = new Sprite(madMat1.getInitialX() + 44.0f, madMat1.getInitialY() + 20.0f, mHatchetTextureRegion);
		madMat1.registerEntityModifier(				
				new RotationModifier(3, 0, 360)
				);
		hatchet1.registerEntityModifier(				
				new RotationModifier(3, 0, 360)
				);
		scene.getLastChild().attachChild(hatchet1);
		
		final Sprite madMat2 = new Sprite(centerX - (mMadMatTextureRegion.getWidth() / 2) + 100.0f, 
				centerY - (mMadMatTextureRegion.getHeight() / 2), mMadMatTextureRegion);
		final Sprite hatchet2 = new Sprite( 44.0f,  20.0f, mHatchetTextureRegion);
		madMat2.attachChild(hatchet2);
		madMat2.registerEntityModifier(				
				new RotationModifier(3, 0, 360)
				);
		scene.getLastChild().attachChild(madMat2);
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
}
