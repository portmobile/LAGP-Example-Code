package com.pearson.lagp.v3;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.entity.particle.emitter.BaseParticleEmitter;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.os.Handler;
import android.widget.Toast;

public class ParticlePlayerActivity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "ParticlePlayActivity";

	// ===========================================================
	// Fields
	// ===========================================================

	private Handler mHandler;
	
	protected Camera mCamera;

	protected Scene mMainScene;

	private ParticleSystem particleSystem;
	private BaseParticleEmitter particleEmitter;
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
		TextureRegionFactory.setAssetBasePath("gfx/");
	}
	
	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		String pxFileName = "gfx/particles/test.px";
		final Scene scene = new Scene(1);

		/* Center the camera. */
		final int centerX = (CAMERA_WIDTH  / 2);
		final int centerY = (CAMERA_HEIGHT  / 2);

		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent( final Scene pScene, final TouchEvent pTouchEvent) {
				if (pTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
						particleEmitter.setCenter(pTouchEvent.getX(), pTouchEvent.getY());
						particleSystem.setParticlesSpawnEnabled(true);
						return true;
				}
				return false;
			}});
		try {
			final PXLoader pxLoader = new PXLoader(this, this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA );
			particleSystem = pxLoader.createFromAsset(this, pxFileName);
		} catch (final PXLoadException pxle) {
			Debug.e(pxle);
		}
		particleSystem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		particleSystem.setParticlesSpawnEnabled(false);
		particleEmitter = (BaseParticleEmitter) particleSystem.getParticleEmitter();

		scene.getLastChild().attachChild(particleSystem);
		Toast.makeText(ParticlePlayerActivity.this, "Touch to show " + pxFileName, Toast.LENGTH_SHORT).show();
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
	
     
     private Runnable mEndPESpawn = new Runnable() {
         public void run() {
     		particleSystem.setParticlesSpawnEnabled(false);
         }
     };
}
