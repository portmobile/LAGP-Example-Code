package com.pearson.lagp.v3;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.MoveYModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.BuildableTexture;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.builder.ITextureBuilder.TextureSourcePackingException;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.modifier.ease.EaseQuadOut;

import android.util.Log;

public class Level1Activity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "Level1Activity";

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;

	protected Scene mMainScene;

	private Texture mLevel1BackTexture;
	private BuildableTexture mObstacleBoxTexture;
	private TextureRegion mBoxTextureRegion;
	private TextureRegion mLevel1BackTextureRegion;
	private TextureRegion mBulletTextureRegion;
	private TextureRegion mCrossTextureRegion;
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
		TextureRegionFactory.setAssetBasePath("gfx/Level1/");
		mLevel1BackTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevel1BackTextureRegion = TextureRegionFactory.createFromAsset(this.mLevel1BackTexture, this, "Level1Bk.png", 0, 0);
		mEngine.getTextureManager().loadTexture(this.mLevel1BackTexture);
		
		mObstacleBoxTexture = new BuildableTexture(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mBoxTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, this, "Obstaclebox.png");
		mBulletTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, this, "Bullet.png");
		mCrossTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, this, "Cross.png");
		mHatchetTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, this, "Hatchet.png");
		   try {
			      mObstacleBoxTexture.build(new BlackPawnTextureBuilder(2));
			   } catch (final TextureSourcePackingException e) {
			      Log.d(tag, "Sprites won't fit in mObstacleBoxTexture");
			   }
		this.mEngine.getTextureManager().loadTexture(this.mObstacleBoxTexture);
		}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);

		/* Center the camera. */
		final int centerX = (CAMERA_WIDTH - mLevel1BackTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - mLevel1BackTextureRegion.getHeight()) / 2;

		/* Create the sprites and add them to the scene. */
		final Sprite background = new Sprite(centerX, centerY, mLevel1BackTextureRegion);
		scene.getLastChild().attachChild(background);
		final Sprite obstacleBox = new Sprite(0.0f, CAMERA_HEIGHT - mBoxTextureRegion.getHeight(), mBoxTextureRegion);
		scene.getLastChild().attachChild(obstacleBox);
		final Sprite bullet = new Sprite(20.0f, CAMERA_HEIGHT - 40.0f, mBulletTextureRegion);
		bullet.registerEntityModifier(				
				new SequenceEntityModifier(
						new ParallelEntityModifier(
								new MoveYModifier(3, 0.0f, CAMERA_HEIGHT - 40.0f, EaseQuadOut.getInstance() ),
								new AlphaModifier(3, 0.0f, 1.0f),
								new ScaleModifier(3, 0.5f, 1.0f)
						),
				new RotationModifier(3, 0, 360)
				)
		);
		scene.getLastChild().attachChild(bullet);
		final Sprite cross = new Sprite(bullet.getInitialX() + 40.0f, CAMERA_HEIGHT - 40.0f, mCrossTextureRegion);
		cross.registerEntityModifier(				
				new SequenceEntityModifier(
						new ParallelEntityModifier(
								new MoveYModifier(4, 0.0f, CAMERA_HEIGHT - 40.0f, EaseQuadOut.getInstance() ),
								new AlphaModifier(4, 0.0f, 1.0f),
								new ScaleModifier(4, 0.5f, 1.0f)
						),
				new RotationModifier(2, 0, 360)
				)
		);
		cross.registerEntityModifier(new AlphaModifier(10.0f, 0.0f, 1.0f));
		scene.getLastChild().attachChild(cross);
		final Sprite hatchet = new Sprite(cross.getInitialX() + 40.0f, CAMERA_HEIGHT - 40.0f, mHatchetTextureRegion);
		hatchet.registerEntityModifier(				
				new SequenceEntityModifier(
						new ParallelEntityModifier(
								new MoveYModifier(5, 0.0f, CAMERA_HEIGHT - 40.0f, EaseQuadOut.getInstance() ),
								new AlphaModifier(5, 0.0f, 1.0f),
								new ScaleModifier(5, 0.5f, 1.0f)
						),
				new RotationModifier(2, 0, 360)
				)
		);
		hatchet.registerEntityModifier(new AlphaModifier(15.0f, 0.0f, 1.0f));
		scene.getLastChild().attachChild(hatchet);
		scene.registerEntityModifier(new AlphaModifier(10, 0.0f, 1.0f));
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}


}
