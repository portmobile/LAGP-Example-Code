package com.pearson.lagp.v3;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.modifier.MoveYModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.modifier.RotationAtModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.entity.particle.emitter.BaseParticleEmitter;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.BuildableTexture;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.builder.ITextureBuilder.TextureSourcePackingException;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.ease.EaseQuadOut;

import android.content.SharedPreferences;
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
	
	private Sprite bullet, cross, hatchet;
	private AnimatedSprite[] asprVamp = new AnimatedSprite[10];
	private int nVamp;
	private ParticleSystem particleSystem;
	private BaseParticleEmitter particleEmitter;
	
	private Sound mExploSound, mGunshotSound, mWhiffleSound;
	private SharedPreferences audioOptions;
	private Music mOCSMusic;

	private AStar[] aStar = new AStar[10];
	private Path[] pathVamp = new Path[10];
	
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
		audioOptions = getSharedPreferences("audio", MODE_PRIVATE);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera).setNeedsSound(true).setNeedsMusic(true));
	}

	@Override
	public void onLoadResources() {
		/* Load Textures. */
		TextureRegionFactory.setAssetBasePath("gfx/Level1/");
		mLevel1BackTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevel1BackTextureRegion = TextureRegionFactory.createFromAsset(this.mLevel1BackTexture, getApplicationContext(), "level1bk.png", 0, 0);
		mEngine.getTextureManager().loadTexture(this.mLevel1BackTexture);
		
		mObstacleBoxTexture = new BuildableTexture(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mBoxTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, getApplicationContext(), "obstaclebox.png");
		mBulletTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, getApplicationContext(), "bullet.png");
		mCrossTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, getApplicationContext(), "cross.png");
		mHatchetTextureRegion = TextureRegionFactory.createFromAsset(mObstacleBoxTexture, getApplicationContext(), "hatchet.png");
		   try {
			      mObstacleBoxTexture.build(new BlackPawnTextureBuilder(2));
			   } catch (final TextureSourcePackingException e) {
			      Log.d(tag, "Sprites won't fit in mObstacleBoxTexture");
			   }
		this.mEngine.getTextureManager().loadTexture(this.mObstacleBoxTexture);
		
		mScrumTexture = new Texture(512, 256, TextureOptions.DEFAULT);
		mScrumTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mScrumTexture, getApplicationContext(), "scrum_tiled.png", 0, 0, 8, 4);
		mEngine.getTextureManager().loadTexture(this.mScrumTexture);
		
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mExploSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), getApplicationContext(), "fireball.ogg");
			this.mGunshotSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), getApplicationContext(), "gunshot.ogg");
			this.mWhiffleSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), getApplicationContext(), "whiffle.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
		MusicFactory.setAssetBasePath("mfx/");
		try {
			this.mOCSMusic = MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), getApplicationContext(), "OCS.ogg");
			this.mOCSMusic.setLooping(false);
		} catch (final IOException e) {
			Debug.e(e);
		}
		
		for (int i=0; i<10; i++) {
			aStar[i] = new AStar(16, 24, CAMERA_WIDTH, CAMERA_HEIGHT);
			aStar[i].setObstacle(12,0);
			aStar[i].setObstacle(13,0);
			aStar[i].setObstacle(14,0);
			aStar[i].setObstacle(15,0);
			aStar[i].setObstacle(8,5);
			aStar[i].setObstacle(8,6);
			aStar[i].setObstacle(8,7);
			aStar[i].setObstacle(9,5);
			aStar[i].setObstacle(9,6);
			aStar[i].setObstacle(9,7);
			aStar[i].setObstacle(10,5);
			aStar[i].setObstacle(10,6);
			aStar[i].setObstacle(10,7);
			aStar[i].setObstacle(6,13);
			aStar[i].setObstacle(7,13);
			aStar[i].setObstacle(8,13);
			aStar[i].setObstacle(6,14);
			aStar[i].setObstacle(7,14);
			aStar[i].setObstacle(8,14);
			aStar[i].setObstacle(6,15);
			aStar[i].setObstacle(7,15);
			aStar[i].setObstacle(8,15);
			aStar[i].setObstacle(10,17);
			aStar[i].setObstacle(11,17);
			aStar[i].setObstacle(12,17);
			aStar[i].setObstacle(10,18);
			aStar[i].setObstacle(11,18);
			aStar[i].setObstacle(12,18);
			aStar[i].setObstacle(10,19);
			aStar[i].setObstacle(11,19);
			aStar[i].setObstacle(12,19);
		}
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
		bullet = new Sprite(20.0f, CAMERA_HEIGHT - 40.0f, mBulletTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pAreaTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					//Toast.makeText(Level1Activity.this, "Sprite touch DOWN", Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_UP:
					mWarnVampires(pAreaTouchEvent.getY());
					fireBullet(pAreaTouchEvent.getX(), pAreaTouchEvent.getY());
					break;
				case TouchEvent.ACTION_MOVE:
					this.setPosition(pAreaTouchEvent.getX() - this.getWidth() / 2, pAreaTouchEvent.getY() - this.getHeight() / 2);
					break;
				}
				return true;
			}
		};
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
		scene.registerTouchArea(bullet);
		scene.setTouchAreaBindingEnabled(true);
		scene.getLastChild().attachChild(bullet);
		
		cross = new Sprite(bullet.getInitialX() + 40.0f, CAMERA_HEIGHT - 40.0f, mCrossTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pAreaTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					//Toast.makeText(Level1Activity.this, "Sprite touch DOWN", Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_UP:
					if (audioOptions.getBoolean("musicOn", false)) {
						mOCSMusic.play();
			    	}
					break;
				case TouchEvent.ACTION_MOVE:
					this.setPosition(pAreaTouchEvent.getX() - this.getWidth() / 2, pAreaTouchEvent.getY() - this.getHeight() / 2);
					break;
				}
				return true;
			}
		};
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
		scene.registerTouchArea(cross);
		scene.getLastChild().attachChild(cross);
		
		hatchet = new Sprite(cross.getInitialX() + 40.0f, CAMERA_HEIGHT - 40.0f, mHatchetTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pAreaTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					//Toast.makeText(Level1Activity.this, "Sprite touch DOWN", Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_UP:
					mWarnVampires(pAreaTouchEvent.getY());
					throwHatchet(pAreaTouchEvent.getX(), pAreaTouchEvent.getY());
					break;
				case TouchEvent.ACTION_MOVE:
					this.setPosition(pAreaTouchEvent.getX() - this.getWidth() / 2, pAreaTouchEvent.getY() - this.getHeight() / 2);
					break;
				}
				return true;
			}
		};
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
		scene.registerTouchArea(hatchet);
		scene.registerEntityModifier(new AlphaModifier(10, 0.0f, 1.0f));
		
       	// Add first vampire (which will add the others)
       	nVamp = 0;
		mHandler.postDelayed(mStartVamp,3000);
		
		try {
			final PXLoader pxLoader = new PXLoader(this, this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA );
			particleSystem = pxLoader.createFromAsset(this, "gfx/particles/explo.px");
		} catch (final PXLoadException pxle) {
			Debug.e(pxle);
		}
		particleSystem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		particleSystem.setParticlesSpawnEnabled(false);
		particleEmitter = (BaseParticleEmitter) particleSystem.getParticleEmitter();

		scene.getLastChild().attachChild(particleSystem);
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
	
	@Override
	public void onPauseGame() {
		super.onPauseGame();
		mGunshotSound.stop();
		mExploSound.stop();
		mOCSMusic.stop();
	}

	private void fireBullet( float pX, float pY){
		// rotate bullet sprite 90 degrees cw, move rapidly to right, and play gunshot effect
		bullet.registerEntityModifier(new SequenceEntityModifier (
				new IEntityModifierListener() {
					@Override
					public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity) {
						Level1Activity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								bullet.setVisible(false);
								bullet.setPosition(0,0);
							}
						});
					}
				},
				new RotationModifier(0.5f, 0.0f, 90.0f),
				new MoveXModifier(0.5f, pX, CAMERA_WIDTH),
				new AlphaModifier(0.1f, 1.0f, 0.0f)));
		
		mHandler.postDelayed(mPlayGunshot, 500);
	}

	private void throwHatchet( float pX, float pY){
		// hatchet flies to right, rotating about eccentric point
		hatchet.registerEntityModifier(new ParallelEntityModifier (
				new IEntityModifierListener() {
					@Override
					public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity) {
						Level1Activity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								hatchet.setVisible(false);
								hatchet.setPosition(0,0);
							}
						});
					}
				},
				new RotationAtModifier(5.0f, 0.0f, 7.0f*360.0f, 20.0f, 20.0f),
				new MoveXModifier(5.0f, pX, CAMERA_WIDTH)));
		
		playSound(mWhiffleSound);
	}
	
    private Runnable mPlayGunshot = new Runnable() {
        public void run() {
        	playSound(mGunshotSound);
        }
    };
    
    private Runnable mStartVamp = new Runnable() {
        public void run() {
        	int i = nVamp++;
        	Scene scene = Level1Activity.this.mEngine.getScene();
           	float startY = gen.nextFloat()*(CAMERA_HEIGHT - 50.0f);
           	asprVamp[i] = new AnimatedSprite(CAMERA_WIDTH - 30.0f, startY, mScrumTextureRegion.clone()) {			
           		@Override
           		public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
           			switch(pAreaTouchEvent.getAction()) {
           			case TouchEvent.ACTION_DOWN:
           				//Toast.makeText(Level1Activity.this, "Sprite touch DOWN", Toast.LENGTH_SHORT).show();
           				/* Is there a vampire close by? */
           				for (int j=0; j<nVamp; j++){
           					if ( (Math.abs(asprVamp[j].getX() + (asprVamp[j].getWidth()/2) - pAreaTouchEvent.getX()) < 10.0f) &&
           							( Math.abs(asprVamp[j].getY() + (asprVamp[j].getHeight()/2) - pAreaTouchEvent.getY()) < 10.0f)) {
           						particleEmitter.setCenter(pAreaTouchEvent.getX(), pAreaTouchEvent.getY());
           						particleSystem.setParticlesSpawnEnabled(true);
           						playSound(mExploSound);
           		        		mHandler.postDelayed(mEndPESpawn,3000);
           		        		asprVamp[j].clearEntityModifiers();
                   				asprVamp[j].registerEntityModifier(
                       					new AlphaModifier(1.0f, 1.0f, 0.0f));
                   				asprVamp[j].setPosition(CAMERA_WIDTH, gen.nextFloat()* CAMERA_HEIGHT);
           					}
           				}
           				break;
            		}
           			return true;
				}
           	};
    		scene.registerTouchArea(asprVamp[i]);
        	final long[] frameDurations = new long[26];
        	Arrays.fill(frameDurations, 500);
            asprVamp[i].animate(frameDurations, 0, 25, true);
            float lagTime = gen.nextFloat()*20.0f;
            float startX = asprVamp[i].getX() - (lagTime/60.0f) * (asprVamp[i].getX() - 30.0f);
            pathVamp[i] = aStar[i].getPath(startX, 1, asprVamp[i].getY(), 10, asprVamp[i].getWidth(), asprVamp[i].getHeight());
           	asprVamp[i].registerEntityModifier(
           			new SequenceEntityModifier (
           						new AlphaModifier(5.0f, 0.0f, 1.0f),
          						new MoveXModifier(lagTime, asprVamp[i].getX(), startX),
                  				new PathModifier(60.0f - lagTime, pathVamp[i])
           						));
           	scene.getLastChild().attachChild(asprVamp[i]);
        	if (nVamp < 10){
        		mHandler.postDelayed(mStartVamp,3000);
        	}
        }
     };
     
     private Runnable mEndPESpawn = new Runnable() {
         public void run() {
     		particleSystem.setParticlesSpawnEnabled(false);
         }
     };
     
     private void playSound (Sound mSound){
    	 if (audioOptions.getBoolean("effectsOn", false)) {
    		 mSound.play();
    	 }
     }
     
     private void mWarnVampires(float pThreatY){
    	 // There's a potential threat to vampires at pThreatY
     	Scene scene = Level1Activity.this.mEngine.getScene();
    	 
    	 for (int i=0; i<nVamp; i++){
    		 if (Math.abs(asprVamp[i].getY() - pThreatY) < 10.0f) {
    			 asprVamp[i].clearEntityModifiers();
    	            pathVamp[i] = aStar[i].getPath(asprVamp[i].getX(), 1, asprVamp[i].getY() - 20.0f, 10, asprVamp[i].getWidth(), asprVamp[i].getHeight());
    	           	asprVamp[i].registerEntityModifier(
    	           			new SequenceEntityModifier (
    	          						new MoveYModifier(5.0f, asprVamp[i].getY(), asprVamp[i].getY() - 20.0f),
    	                  				new PathModifier(60.0f, pathVamp[i])
    	           						));
    	           	scene.getLastChild().attachChild(asprVamp[i]);    			 
    		 }
    	 }
     }
}
