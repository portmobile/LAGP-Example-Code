package com.pearson.lagp.modex;


import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.ColorModifier;
import org.anddev.andengine.entity.modifier.DelayModifier;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

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
	private Texture mTexture;
	private TextureRegion mFaceTextureRegion;

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
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera).setNeedsMusic(true));
	}

	@Override
	public void onLoadResources() {
		TextureRegionFactory.setAssetBasePath("gfx/");
		this.mTexture = new Texture(512, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mFaceTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, this, "mathead.png", 0, 0);
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
	}

	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(1);

		/* Center the face on the camera. */
		final int centerX = (CAMERA_WIDTH - this.mFaceTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - this.mFaceTextureRegion.getHeight()) / 2;

		/* Create the face sprite and add it to the scene. */
		final Sprite face = new Sprite(centerX, centerY, this.mFaceTextureRegion);

//		face.registerEntityModifier(new ScaleModifier(10.0f, 0.0f, 1.0f));
		face.registerEntityModifier(new ParallelEntityModifier(
				new MoveModifier(3.0f, 0.0f, CAMERA_WIDTH/2, 0.0f, CAMERA_HEIGHT/2),
				new SequenceEntityModifier(
						new ColorModifier(2.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f),
						new ColorModifier(2.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f),
						new ColorModifier(2.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f)
						),
				new SequenceEntityModifier(
						new ScaleModifier(1.0f, 1.0f, 1.0f),
						new ScaleModifier(2.0f, 1.0f, 0.5f),
						new ScaleModifier(2.0f, 0.5f, 1.0f)
						),
				new SequenceEntityModifier(
						new DelayModifier(2.0f),
						new RotationModifier(2.0f, 0.0f, 720.0f)
						)
				)
		);
		
		scene.getLastChild().attachChild(face);
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
