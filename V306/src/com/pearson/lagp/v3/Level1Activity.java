package com.pearson.lagp.v3;

import java.util.Arrays;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.DelayModifier;
import org.anddev.andengine.entity.modifier.FadeInModifier;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveYModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.BuildableTexture;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.builder.ITextureBuilder.TextureSourcePackingException;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.modifier.ease.EaseQuadOut;

import android.content.Intent;
import android.os.Handler;
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

	private Handler mHandler;
	
	protected Camera mCamera;

	protected Scene mMainScene;

	private Texture mLevel1BackTexture;
	private Texture mScrumTexture;
	private BuildableTexture mObstacleBoxTexture;
	private TextureRegion mBoxTextureRegion;
	private TextureRegion mLevel1BackTextureRegion;
	private TextureRegion mBulletTextureRegion;
	private TextureRegion mCrossTextureRegion;
	private TextureRegion mHatchetTextureRegion;
	private TiledTextureRegion mScrumTextureRegion;
	
	private AnimatedSprite[] asprVamp = new AnimatedSprite[10];
	private int nVamp;
	Random gen;
	
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
		gen = new Random();
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		/* Load Textures. */
		TextureRegionFactory.setAssetBasePath("gfx/Level1/");
		mLevel1BackTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevel1BackTextureRegion = TextureRegionFactory.createFromAsset(this.mLevel1BackTexture, this, "level1bk.png", 0, 0);
		mEngine.getTextureManager().loadTexture(this.mLevel1BackTexture);
		
		mObstacleBoxTexture = new BuildableTexture(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mBoxTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, this, "obstaclebox.png");
		mBulletTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, this, "bullet.png");
		mCrossTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, this, "cross.png");
		mHatchetTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, this, "hatchet.png");
		   try {
			      mObstacleBoxTexture.build(new BlackPawnTextureBuilder(2));
			   } catch (final TextureSourcePackingException e) {
			      Log.d(tag, "Sprites won't fit in mObstacleBoxTexture");
			   }
		this.mEngine.getTextureManager().loadTexture(this.mObstacleBoxTexture);
		
		mScrumTexture = new Texture(512, 256, TextureOptions.DEFAULT);
		mScrumTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mScrumTexture, this, "scrum_tiled.png", 0, 0, 8, 4);
		mEngine.getTextureManager().loadTexture(this.mScrumTexture);
		
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
		
       	// Add first vampire (which will add the others)
       	nVamp = 0;
		mHandler.postDelayed(mStartVamp,5000);
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
	
    private Runnable mStartVamp = new Runnable() {
        public void run() {
        	int i = nVamp++;
        	Scene scene = Level1Activity.this.mEngine.getScene();
           	float startY = gen.nextFloat()*(CAMERA_HEIGHT - 50.0f);
           	asprVamp[i] = new AnimatedSprite(CAMERA_WIDTH - 30.0f, startY, mScrumTextureRegion.clone());
        	final long[] frameDurations = new long[26];
        	Arrays.fill(frameDurations, 500);
            asprVamp[i].animate(frameDurations, 0, 25, true);
           	asprVamp[i].registerEntityModifier(
           			new SequenceEntityModifier (
           						new AlphaModifier(5.0f, 0.0f, 1.0f),
          						new MoveModifier(60.0f, asprVamp[i].getX(), 30.0f, 
         							asprVamp[i].getY(), (float)CAMERA_HEIGHT/2)));
           	scene.getLastChild().attachChild(asprVamp[i]);
        	if (nVamp < 10){
        		mHandler.postDelayed(mStartVamp,5000);
        	}
        }
     };
}
