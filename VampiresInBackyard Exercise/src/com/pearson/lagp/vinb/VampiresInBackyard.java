package com.pearson.lagp.vinb;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.extension.augmentedreality.BaseAugmentedRealityGameActivity;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.sensor.orientation.IOrientationListener;
import org.anddev.andengine.sensor.orientation.OrientationData;

import android.graphics.Color;
import android.os.Handler;
import android.widget.Toast;

public class VampiresInBackyard extends BaseAugmentedRealityGameActivity implements IOrientationListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private static final int VAMP_RATE = 2000;
	private static final int MAX_VAMPS = 10;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	private Handler mHandler;
	private Texture mScrumTexture;
	private TiledTextureRegion mScrumTextureRegion;
	private Texture mFontTexture;
	private Font mFont32;
	private ChangeableText mCurrHeading;

	private AnimatedSprite[] asprVamp = new AnimatedSprite[10];
	private int nVamp;
	
	private Random gen;
	NumberFormat nf;

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
		Toast.makeText(this, "If you don't see a vampire moving over the screen, try starting this while already being in Landscape orientation!!", Toast.LENGTH_LONG).show();
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		mHandler = new Handler();
		gen = new Random();
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
			TextureRegionFactory.setAssetBasePath("gfx/VinB/");
			mScrumTexture = new Texture(512, 256, TextureOptions.DEFAULT);
			mScrumTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mScrumTexture, getApplicationContext(), "scrum_tiled.png", 0, 0, 8, 4);
			mEngine.getTextureManager().loadTexture(this.mScrumTexture);	
			this.getEngine().getTextureManager().loadTexture(this.mScrumTexture);
			
			this.mFontTexture = new Texture(256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			FontFactory.setAssetBasePath("font/");
			mFont32 = FontFactory.createFromAsset(this.mFontTexture, this, "Flubber.ttf", 32, true, Color.RED);
			mEngine.getTextureManager().loadTexture(this.mFontTexture);
			mEngine.getFontManager().loadFont(this.mFont32);
	}

	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f, 0.0f));

       	// Add first vampire (which will add the others)
       	nVamp = 0;
		mHandler.postDelayed(mStartVamp,3000);
		
		mCurrHeading = new ChangeableText(0.5f*CAMERA_WIDTH, 10.0f, mFont32, "Heading: 0", "Heading: XXXXXX".length());
		scene.getLastChild().attachChild(mCurrHeading);

		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				for (int i=0; i<nVamp; i++){
					if (asprVamp[i].getX() < 30.0f){
						//move vampire back to right side of screen
			           	float startY = gen.nextFloat()*(CAMERA_HEIGHT - 50.0f);
			           	asprVamp[i].clearEntityModifiers();
			           	asprVamp[i].registerEntityModifier(
			          		new MoveModifier(40.0f, CAMERA_WIDTH - 30.0f, 0.0f, startY, 340.0f)
			           	);
					}
				}
			}
		});
		return scene;
	}

	@Override
	public void onLoadComplete() {

	}
	
	@Override
	protected void onResume() {
		super.onResume();

		this.enableOrientationSensor(VampiresInBackyard.this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.mEngine.disableOrientationSensor(this);
	}
	public void onOrientationChanged(OrientationData pOD) {		
		// Update compass display
		float head = pOD.getYaw();
    	 mCurrHeading.setText("Heading: " + nf.format(head));
	}

	// ===========================================================
	// Methods
	// ===========================================================
    private Runnable mStartVamp = new Runnable() {
        public void run() {
        	int i = nVamp;
        	Scene scene = VampiresInBackyard.this.mEngine.getScene();
           	float startY = gen.nextFloat()*(CAMERA_HEIGHT - 50.0f);
           	asprVamp[i] = new AnimatedSprite(CAMERA_WIDTH - 30.0f, startY, mScrumTextureRegion.clone()) ;
           	nVamp++;
    		scene.registerTouchArea(asprVamp[i]);
        	final long[] frameDurations = new long[26];
        	Arrays.fill(frameDurations, 500);
            asprVamp[i].animate(frameDurations, 0, 25, true);
           	asprVamp[i].registerEntityModifier(
           			new SequenceEntityModifier (
           						new AlphaModifier(5.0f, 0.0f, 1.0f),
          						new MoveModifier(40.0f, CAMERA_WIDTH - 30.0f, 0.0f, startY, 340.f)
           						));
           	scene.getLastChild().attachChild(asprVamp[i]);
        	if (nVamp < MAX_VAMPS){
        		mHandler.postDelayed(mStartVamp,VAMP_RATE);
        	}
        }
     };

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
