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
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
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
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

public class Level1Activity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "Level1Activity";
	
	private static final int TOUCH_VAMP_SCORE = 50;
	private static final int BULLET_VAMP_SCORE = 100;
	private static final int HATCHET_VAMP_SCORE = 200;
	private static final int CROSS_VAMP_SCORE = 500;

	private static final int NUKE_BULLET = 1;
	private static final int NUKE_HATCHET = NUKE_BULLET+1;
	private static final int NUKE_CROSS = NUKE_HATCHET+1;
	private static final int NUKE_TOUCH = NUKE_CROSS+1;
	
	private static final boolean PLAYER_WINS = true;
	private static final boolean VAMPIRES_WIN = false;

	// Location of MissB's front door
	private static final Rectangle 	MissBs = new Rectangle(35.0f, 195.0f, 15.0f, 35.0f);

	// ===========================================================
	// Fields
	// ===========================================================

	private Handler mHandler;
	
	protected Camera mCamera;

	private Texture mLevel1BackTexture;
	private Texture mScrumTexture;
	private Texture mSarahTexture;
	private BuildableTexture mObstacleBoxTexture;
	private Texture mPopUpTexture;
	private TextureRegion mBoxTextureRegion;
	private TextureRegion mLevel1BackTextureRegion;
	private TextureRegion mBulletTextureRegion;
	private TextureRegion mCrossTextureRegion;
	private TextureRegion mHatchetTextureRegion;
	private TiledTextureRegion mScrumTextureRegion;
	private TiledTextureRegion mSarahTextureRegion;
	private TextureRegion mEndBackTextureRegion;
	private TextureRegion mAgainButtonTextureRegion;
	private TextureRegion mQuitButtonTextureRegion;
	private TextureRegion mNextButtonTextureRegion;
	private TextureRegion mNewHighTextureRegion;
	private Texture mFontTexture;
	private Font mFont32;
	private ChangeableText mCurrScore;
	
	private Sprite bullet, cross, hatchet;
	private Sprite endBack, newHigh, againButton, quitButton, nextButton;
	private AnimatedSprite[] asprVamp = new AnimatedSprite[40];
	private AnimatedSprite asprSarah;
	private int nVamp, nVampsKilled;
	private Rectangle touchRect;
	private boolean touchActive = false;
	private ParticleSystem particleSystem;
	private BaseParticleEmitter particleEmitter;
	
	private Sound mExploSound, mGunshotSound, mWhiffleSound, mSaveMeSound;
	private SharedPreferences audioOptions;
	private SharedPreferences scores;
	private SharedPreferences difficulty;
	private SharedPreferences.Editor scoresEditor;
	private SharedPreferences.Editor diffEditor;
	private int[] highScores = new int[5];
	private int thisScore = 0;
	
	private Music mOCSMusic;

	private AStar[] aStar = new AStar[40];
	private Path[] pathVamp = new Path[40];
	private int mWins, mPlays;
	private int mMaxVamps;
	private int mVampRate;
	private boolean mDistract;
	private boolean mPlayerWon;
	private boolean mActivityVisible = true;
	
	
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
		scores = getSharedPreferences("scores", MODE_PRIVATE);
		scoresEditor = scores.edit();
		highScores[4] = scores.getInt("Level1-4", 0);
		highScores[3] = scores.getInt("Level1-3", 0);
		highScores[2] = scores.getInt("Level1-2", 0);
		highScores[1] = scores.getInt("Level1-1", 0);
		highScores[0] = scores.getInt("Level1-0", 0);
		
		difficulty = getSharedPreferences("difficulty", MODE_PRIVATE);
		diffEditor = difficulty.edit();
		mMaxVamps = difficulty.getInt("Lvl1.MAX_VAMPS", 10);
		mVampRate = difficulty.getInt("Lvl1.VAMP_RATE", 4000);
		mDistract = difficulty.getBoolean("Lvl1.DISTRACT", true);
		mWins = difficulty.getInt("Lvl1.WINS", 0);
		mPlays = difficulty.getInt("Lvl1.PLAYS", 0);
		
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
		
		mSarahTexture = new Texture(256, 64, TextureOptions.DEFAULT);
		mSarahTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mSarahTexture, getApplicationContext(), "sarahanim.png", 0, 0, 6, 1);
		mEngine.getTextureManager().loadTexture(this.mSarahTexture);
		
		TextureRegionFactory.setAssetBasePath("gfx/Scoring/");
		mPopUpTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mEndBackTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, getApplicationContext(), "endback.png", 0, 0);
		mAgainButtonTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, getApplicationContext(), "againbutton.png", 0, 330);
		mQuitButtonTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, getApplicationContext(), "quitbutton.png", 50, 330);
		mNextButtonTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, getApplicationContext(), "nextbutton.png", 100, 330);
		mNewHighTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, getApplicationContext(), "newhigh.png", 100, 400);
		mEngine.getTextureManager().loadTexture(this.mPopUpTexture);
		TextureRegionFactory.setAssetBasePath("gfx/Level1/");

		this.mFontTexture = new Texture(256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		FontFactory.setAssetBasePath("font/");
		mFont32 = FontFactory.createFromAsset(this.mFontTexture, this, "Flubber.ttf", 32, true, Color.RED);
		mEngine.getTextureManager().loadTexture(this.mFontTexture);
		mEngine.getFontManager().loadFont(this.mFont32);

		// Load sounds
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mExploSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), getApplicationContext(), "fireball.ogg");
			this.mGunshotSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), getApplicationContext(), "gunshot.ogg");
			this.mWhiffleSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), getApplicationContext(), "whiffle.ogg");
			this.mSaveMeSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), getApplicationContext(), "saveme.ogg");
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

		// Load the pathfinding grid
		for (int i=0; i<40; i++) {
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
       	nVampsKilled = 0;
		mHandler.postDelayed(mStartVamp,3000);

		// If distractions are enabled, start the first one
		if (mDistract) {
			mHandler.postDelayed(mStartSarah, 5000);
		}
		
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
				
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				for (int i=0; i<nVamp; i++){
					if (asprVamp[i].collidesWith(bullet)){
						mNukeVamp(i, NUKE_BULLET);
					}
					if (asprVamp[i].collidesWith(hatchet)){
						mNukeVamp(i, NUKE_HATCHET);
					}
					if (asprVamp[i].collidesWith(cross)){
						mNukeVamp(i, NUKE_CROSS);
					}
					if (asprVamp[i].collidesWith(MissBs)){
						//gamelet over, vampires win
						mGameOver(VAMPIRES_WIN);
					}
					if ((touchActive) && (asprVamp[i].collidesWith(touchRect))){
						mNukeVamp(i, NUKE_TOUCH);
					}
				}
			}
		});

		// Score display
		mCurrScore = new ChangeableText(0.6f*CAMERA_WIDTH, 10.0f, mFont32, "Score: 0", "Score: XXXXXX".length());
		scene.getLastChild().attachChild(mCurrScore);
		
		// Create Sprites for result screens - don't attach yet
		endBack = new Sprite((CAMERA_WIDTH - mEndBackTextureRegion.getWidth()) / 2, (CAMERA_HEIGHT - mEndBackTextureRegion.getHeight()) / 2, mEndBackTextureRegion);
		newHigh = new Sprite(0.0f, 0.0f, mNewHighTextureRegion);
		againButton = new Sprite(0.0f, 0.0f, mAgainButtonTextureRegion){
       		@Override
       		public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
       			switch(pAreaTouchEvent.getAction()) {
       			case TouchEvent.ACTION_DOWN:
       				mEndCleanup();
       				mHandler.post(mPlayThis);
       				finish();
       				break;
       			}
       			return true;
       		}
		};
		nextButton = new Sprite(0.0f, 0.0f, mNextButtonTextureRegion){
       		@Override
       		public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
       			switch(pAreaTouchEvent.getAction()) {
       			case TouchEvent.ACTION_DOWN:
       				mEndCleanup();
       				mHandler.post(mPlayNext);
       				finish();
       				break;
       			}
       			return true;
       		}
		};
		quitButton = new Sprite(0.0f, 0.0f, mQuitButtonTextureRegion){
       		@Override
       		public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
       			switch(pAreaTouchEvent.getAction()) {
       			case TouchEvent.ACTION_DOWN:
       				mEndCleanup();
       				finish();
       				break;
       			}
       			return true;
       		}
		};
		
       	asprSarah = new AnimatedSprite(15.0f, 90.0f, mSarahTextureRegion);	
       	asprSarah.setVisible(false);
        scene.getLastChild().attachChild(asprSarah);

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
		mSaveMeSound.stop();
		mActivityVisible = false;
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
		mActivityVisible = true;
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
        	int i = nVamp;
        	Scene scene = Level1Activity.this.mEngine.getScene();
           	float startY = gen.nextFloat()*(CAMERA_HEIGHT - 50.0f);
           	asprVamp[i] = new AnimatedSprite(CAMERA_WIDTH - 30.0f, startY, mScrumTextureRegion.clone()) {			
           		@Override
           		public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
           			switch(pAreaTouchEvent.getAction()) {
           			case TouchEvent.ACTION_DOWN:
           				/* Is there a vampire close by? */
           				touchRect = new Rectangle (pAreaTouchEvent.getX(), pAreaTouchEvent.getY(), 20.0f, 20.0f);
           				touchActive = true;
           				break;
           			case TouchEvent.ACTION_UP:
           				touchActive = false;
            		}
           			return true;
				}
           	};
           	nVamp++;
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
        	if (nVamp < mMaxVamps){
        		mHandler.postDelayed(mStartVamp,mVampRate);
        	}
        }
     };
     
     private Runnable mStartSarah = new Runnable() {
         public void run() {
        	 final long[] frameDurations = new long[6];
        	 Arrays.fill(frameDurations, 200);
        	 asprSarah.setVisible(true);
        	 asprSarah.animate(frameDurations, 0, 5, false);
             playSound(mSaveMeSound);
        	 mHandler.postDelayed(mStartSarah, (long)(gen.nextFloat()*7000.0f + 8000.0f));
        	 mHandler.postDelayed(mEndSarah, 2000);
         }
      };

      private Runnable mEndSarah = new Runnable() {
          public void run() {
         	 asprSarah.setVisible(false);
          }
      };

      private Runnable mEndPESpawn = new Runnable() {
         public void run() {
     		particleSystem.setParticlesSpawnEnabled(false);
         }
     };
     
     private void playSound (Sound mSound){
    	 if ((audioOptions.getBoolean("effectsOn", false)) && 
    			 (mActivityVisible)){
    		 mSound.play();
    	 }
     }
     
     private void mWarnVampires(float pThreatY){
    	 // There's a potential threat to vampires at pThreatY
     	Scene scene = Level1Activity.this.mEngine.getScene();
    	 
    	 for (int i=0; i<nVamp; i++){
    		 if (Math.abs(asprVamp[i].getY() - pThreatY) < 10.0f) {
    			 asprVamp[i].clearEntityModifiers();
    			 float startY = asprVamp[i].getY() - 20.0f;
    			 if (startY < 0) startY = 0;
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
     
     private void mNukeVamp(int pID, int pMethod){
      	final Scene scene = Level1Activity.this.mEngine.getScene();
		particleEmitter.setCenter(asprVamp[pID].getX(), asprVamp[pID].getY());
		particleSystem.setParticlesSpawnEnabled(true);
		playSound(mExploSound);
   		mHandler.postDelayed(mEndPESpawn,2000);
   		asprVamp[pID].clearEntityModifiers();
		asprVamp[pID].registerEntityModifier(
			new AlphaModifier(1.0f, 1.0f, 0.0f));
		asprVamp[pID].stopAnimation();
		asprVamp[pID].setPosition(CAMERA_WIDTH, gen.nextFloat()* CAMERA_HEIGHT);
		nVampsKilled++;
		switch (pMethod){
		case (NUKE_BULLET):
			mAddScore(BULLET_VAMP_SCORE);
			break;
		case (NUKE_HATCHET):
			mAddScore(HATCHET_VAMP_SCORE);
			this.runOnUpdateThread(new Runnable() {
				@Override
				public void run() {
					/* Now it is save to remove the entity! */
					scene.getLastChild().detachChild(Level1Activity.this.hatchet);
				}
			});
			hatchet.setPosition(0.0f, 0.0f);
			hatchet.setAlpha(0.0f);
			break;
		case (NUKE_CROSS):
			mAddScore(CROSS_VAMP_SCORE);
			this.runOnUpdateThread(new Runnable() {
				@Override
				public void run() {
					/* Now it is save to remove the entity! */
					scene.getLastChild().detachChild(Level1Activity.this.cross);
				}
			});
			cross.setPosition(0.0f, 0.0f);
			cross.setAlpha(0.0f);
			break;
		case (NUKE_TOUCH):
			mAddScore(TOUCH_VAMP_SCORE);
			break;
		}
		if (nVampsKilled == mMaxVamps){
			// Last vampire killed
			mGameOver(PLAYER_WINS);
		}
     }
     
     private void mGameOver(boolean pWin){
    	 // Called when gamelet is over - pWin=true if player won
    	 Scene scene = Level1Activity.this.mEngine.getScene();
    	 boolean newTop = false;
    	 int[] newHighScores = {0,0,0,0,0};
    	 for (int i=4; i>-1; i--){
    		 if (thisScore > highScores[i]){
    			 newHighScores[i] = thisScore;
    			 for (int j=i-1; j>-1; j--){
    				 newHighScores[j] = highScores[j+1];
    			 }
    			 if (i==4) newTop = true;
    			 break;
    		 } else {
    			 newHighScores[i] = highScores[i];
    		 }
    	 }
    	 for (int i=0; i<5; i++) highScores[i] = newHighScores[i];
		 scoresEditor.putInt("Level1-4", highScores[4]);
		 scoresEditor.putInt("Level1-3", highScores[3]);
		 scoresEditor.putInt("Level1-2", highScores[2]);
		 scoresEditor.putInt("Level1-1", highScores[1]);
		 scoresEditor.putInt("Level1-0", highScores[0]);
		 scoresEditor.commit();

    	 if (pWin){
    		 mPlayerWon = true;
    		 scene.setChildScene(mCreateEndScene(newTop, "Congratulations!!"), false, true, true);
    	 } else {
    		 mPlayerWon = false;
    		 scene.setChildScene(mCreateEndScene(false, "You Suck! \n....blood"));
    	 }
     }
     
     private Scene mCreateEndScene(boolean pNewHigh, String pTitle){
    	 Scene endScene = new Scene(2);
    	 endScene.getLastChild().attachChild(endBack);
 		 Text mTitle = new Text( 50.0f, 50.0f, mFont32, pTitle);
    	 endScene.getLastChild().attachChild(mTitle);
    	 if (pNewHigh) {
    		 newHigh.setPosition(300.0f, 50.0f);
    		 endScene.getLastChild().attachChild(newHigh);
    	 }
    	 Text mYourScore = new Text( 50.0f, 150.0f, mFont32, "Your Score: " + thisScore);
    	 Text mHighScore = new Text( 50.0f, 200.0f, mFont32, "High Score: " + highScores[4]);
    	 endScene.getLastChild().attachChild(mYourScore);
    	 endScene.getLastChild().attachChild(mHighScore);
    	 againButton.setPosition(50.0f, 260.0f);
 		 endScene.registerTouchArea(againButton);
		 endScene.setTouchAreaBindingEnabled(true);
		 endScene.getLastChild().attachChild(againButton);
		 quitButton.setPosition(150.0f, 260.0f);
 		 endScene.registerTouchArea(quitButton);
		 endScene.getLastChild().attachChild(quitButton);
		 nextButton.setPosition(300.0f, 260.0f);
 		 endScene.registerTouchArea(nextButton);
		 endScene.getLastChild().attachChild(nextButton);
    	 
    	 return endScene;
     }
     
     private void mAddScore(int pAdder){
    	 thisScore += pAdder;
    	 mCurrScore.setText("Score: " + thisScore);
     }
     private void mIncreaseDifficulty() {
    	 // Make the gamelet a little harder
		if (mMaxVamps < 40) mMaxVamps += 5;
		if (mVampRate > 500) mVampRate -= 500;
		if (mWins > 5) mDistract = true;
	}

    private void mSaveDifficulty() {
		 diffEditor.putInt("Lvl1.MAX_VAMPS", mMaxVamps);
		 diffEditor.putInt("Lvl1.VAMP_RATE", mVampRate);
		 diffEditor.putBoolean("Lvl1.DISTRACT", mDistract);
		 diffEditor.putInt("Lvl1.WINS", mWins);
		 diffEditor.putInt("Lvl1.PLAYS", mPlays);
    }
    
    private void mEndCleanup() {
				mPlays++;
   				if (mPlayerWon) {
   					mIncreaseDifficulty();
   					mWins++;
   				}
   				mSaveDifficulty();
	}

    private Runnable mPlayThis = new Runnable() {
         public void run() {
     		Intent myIntent = new Intent(Level1Activity.this, Level1Activity.class);
     		Level1Activity.this.startActivity(myIntent);
     		finish();
         }
    };

    private Runnable mPlayNext = new Runnable() {
         public void run() {
      		Intent myIntent = new Intent(Level1Activity.this, WAVActivity.class);
      		Level1Activity.this.startActivity(myIntent);
      		finish();
         }
    };
}
