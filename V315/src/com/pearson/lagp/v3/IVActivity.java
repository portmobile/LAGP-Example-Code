package com.pearson.lagp.v3;

import java.io.IOException;
import java.util.ArrayList;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
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
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;
import org.xml.sax.Attributes;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.pearson.lagp.v3.BKLoader.IBKEntityLoader;

public class IVActivity extends BaseGameActivity implements IOnSceneTouchListener, BKConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private static final int VAMPIRE_FLOORED = 200;
	private static final boolean PLAYER_WINS = true;
	private static final boolean VAMPIRES_WIN = false;
	private static final int MAX_STAKES = 5;

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
	
	// ===========================================================
	// Fields
	// ===========================================================

	private BuildableTexture mTexture;
	private Texture mPopUpTexture;
	private TextureRegion mEndBackTextureRegion;
	private TextureRegion mAgainButtonTextureRegion;
	private TextureRegion mQuitButtonTextureRegion;
	private TextureRegion mNextButtonTextureRegion;
	private TextureRegion mNewHighTextureRegion;

	private TextureRegion mStakeTextureRegion;
	private TextureRegion mGlassTextureRegion;
	private TextureRegion mWoodTextureRegion;
	private TextureRegion mStoneTextureRegion;
	private TextureRegion mMatHeadTextureRegion;
	private Sprite stakesprite;
	private Scene mScene;
	private Texture mFontTexture;
	private Font mFont32;
	private ChangeableText mCurrScore;
	private Sprite endBack, newHigh, againButton, quitButton, nextButton;

	private PhysicsWorld mPhysicsWorld;

	private boolean isStakeSpawning = false;
	private float stakeX, stakeY;
	private float velX, velY;
	private Line stakeLine;
	private int numStakes = 0;
	
	private Vector2 gravity;
	private Body stake;
	
	private Sound mOofSound;
	
	private float mX, mY;
	private float mWidth, mHeight;
	private float mRotation;
	private boolean mIsDynamic;
	private BodyType mBodyType;
	private String mShape;
	private String mPhysicsAndID;
	private float mDensity;
	private float mFriction;
	private float mElasticity;
	private String mID;
	
	private float PtoM = PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
	private int numHeads = 0;
	private ArrayList<Integer> deadHeads = new ArrayList<Integer>();
	private SharedPreferences audioOptions, scores;
	private SharedPreferences.Editor scoresEditor;
	private int[] highScores = new int[5];
	private int thisScore = 0;

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
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera).setNeedsSound(true);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		audioOptions = getSharedPreferences("audio", MODE_PRIVATE);
		scores = getSharedPreferences("scores", MODE_PRIVATE);
		scoresEditor = scores.edit();
		highScores[4] = scores.getInt("IV-4", 0);
		highScores[3] = scores.getInt("IV-3", 0);
		highScores[2] = scores.getInt("IV-2", 0);
		highScores[1] = scores.getInt("IV-1", 0);
		highScores[0] = scores.getInt("IV-0", 0);
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		/* Textures. */
		this.mTexture = new BuildableTexture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		TextureRegionFactory.setAssetBasePath("gfx/IV/");

		/* TextureRegions. */
		this.mStakeTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, getApplicationContext(), "stake40.png"); 
		this.mGlassTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, getApplicationContext(), "glass.png");
		this.mWoodTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, getApplicationContext(), "wood.png");
		this.mStoneTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, getApplicationContext(), "stone.png");
		this.mMatHeadTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, getApplicationContext(), "mathead.png");
		   try	{
				 mTexture.build(
					new BlackPawnTextureBuilder(2));
				} catch (final TextureSourcePackingException e) {
				   	Log.d("V3", 
					"Sprites won't fit in mTexture");
				}
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
		TextureRegionFactory.setAssetBasePath("gfx/Scoring/");
		mPopUpTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mEndBackTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, getApplicationContext(), "endback.png", 0, 0);
		mAgainButtonTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, getApplicationContext(), "againbutton.png", 0, 330);
		mQuitButtonTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, getApplicationContext(), "quitbutton.png", 50, 330);
		mNextButtonTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, getApplicationContext(), "nextbutton.png", 100, 330);
		mNewHighTextureRegion = TextureRegionFactory.createFromAsset(this.mPopUpTexture, getApplicationContext(), "newhigh.png", 100, 400);
		mEngine.getTextureManager().loadTexture(this.mPopUpTexture);
		
		this.mFontTexture = new Texture(256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		FontFactory.setAssetBasePath("font/");
		mFont32 = FontFactory.createFromAsset(this.mFontTexture, this, "Flubber.ttf", 32, true, Color.RED);
		mEngine.getTextureManager().loadTexture(this.mFontTexture);
		mEngine.getFontManager().loadFont(this.mFont32);
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mOofSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "oof.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		mScene = new Scene(2);
		mScene.setOnSceneTouchListener(this);
		
		/* Center the camera. */
		final int centerX = CAMERA_WIDTH / 2;
		final int centerY = CAMERA_HEIGHT / 2;
		mScene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));
		mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		final BKLoader bkLoader = new BKLoader();
		bkLoader.setAssetBasePath("level/iv/");

		bkLoader.registerEntityLoader(TAG_LEVEL, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});

		bkLoader.registerEntityLoader(TAG_BODY, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
				if(mShape.equals(TAG_SHAPE_VALUE_SQUARE)) {
					final TextureRegion mTR = selectTexture(mID);
					final Sprite bodyShape = new Sprite(mX - mTR.getWidth()/2, mY - mTR.getHeight()/2, mTR);
					bodyShape.setScaleX(mWidth/mTR.getWidth());
					bodyShape.setScaleY(mHeight/mTR.getHeight());
					if (mRotation != 0.0f) {
						bodyShape.setRotation(mRotation);
					}
					final Body mBody = PhysicsFactory.createBoxBody(mPhysicsWorld, bodyShape, mBodyType, 
							PhysicsFactory.createFixtureDef(mDensity, mElasticity, mFriction));
					mBody.setUserData(mID);
					mScene.getLastChild().attachChild(bodyShape);
					mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(bodyShape, mBody, true, true));
				} else if(mShape.equals(TAG_SHAPE_VALUE_CIRCLE)) {
					final TextureRegion mTR = mMatHeadTextureRegion;
					Sprite bodyShape = new Sprite(mX - mTR.getWidth()/2, mY - mTR.getHeight()/2, mTR);
					bodyShape.setScaleX(mWidth/mTR.getWidth());
					bodyShape.setScaleY(mHeight/mTR.getHeight());
					if (mRotation != 0.0f) {
						bodyShape.setRotation(mRotation);
					}
					final Body mBody = PhysicsFactory.createCircleBody(mPhysicsWorld, bodyShape, mBodyType, 
							PhysicsFactory.createFixtureDef(mDensity, mElasticity, mFriction));
					mBody.setUserData(mID);
					mScene.getFirstChild().attachChild(bodyShape);
					mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(bodyShape, mBody, true, true));
				} else if(mShape.equals(TAG_SHAPE_VALUE_POLYGON)) {
					// Unimplemented
				} else {
					throw new IllegalArgumentException();
				}
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});
			
		bkLoader.registerEntityLoader(TAG_X, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
				mX = new Float(pValue);
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});

		bkLoader.registerEntityLoader(TAG_Y, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
				mY = new Float(pValue);
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});
		bkLoader.registerEntityLoader(TAG_WIDTH, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
				mWidth = new Float(pValue);
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});
		bkLoader.registerEntityLoader(TAG_HEIGHT, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
				mHeight = new Float(pValue);
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});
		bkLoader.registerEntityLoader(TAG_ROTATION, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
				mRotation = new Float(pValue);
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});
		bkLoader.registerEntityLoader(TAG_ISDYNAMIC, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
				mIsDynamic = true;
				if (pValue.equals("false")) mIsDynamic = false;
				mBodyType = BodyType.StaticBody;
				if (mIsDynamic) mBodyType = BodyType.DynamicBody;
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});
		bkLoader.registerEntityLoader(TAG_SHAPE, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
				mShape = pValue;
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});
		bkLoader.registerEntityLoader(TAG_PHYSICSANDID, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
				mPhysicsAndID = pValue;
				final String[] physTokens = mPhysicsAndID.split(",");
				mDensity = Float.valueOf(physTokens[1]).floatValue();
				mFriction = Float.valueOf(physTokens[0]).floatValue();
				mElasticity = Float.valueOf(physTokens[2]).floatValue();
				mID = trimQuotes(physTokens[3]);
				if (mID.equals("vamp")) {
					numHeads++;
					mID = "vamp"+numHeads;
				}
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});
		bkLoader.registerEntityLoader(TAG_VERTS, new IBKEntityLoader() {
			@Override
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String pValue) {
			}
			public void onLoadEntity(final String pEntityName, final Attributes pAttributes) {
			}
		});

		try {
			bkLoader.loadLevelFromAsset(getApplicationContext(), "iv1.lvl");
		} catch (final IOException e) {
			Debug.e(e);
		}
		
		// Score display
		mCurrScore = new ChangeableText(0.6f*CAMERA_WIDTH, 10.0f, mFont32, "Score: 0", "Score: XXXXXX".length());
		mScene.getLastChild().attachChild(mCurrScore);
		
		// Create Sprites for result screens - don't attach yet
		endBack = new Sprite((CAMERA_WIDTH - mEndBackTextureRegion.getWidth()) / 2, (CAMERA_HEIGHT - mEndBackTextureRegion.getHeight()) / 2, mEndBackTextureRegion);
		newHigh = new Sprite(0.0f, 0.0f, mNewHighTextureRegion);
		againButton = new Sprite(0.0f, 0.0f, mAgainButtonTextureRegion){
       		@Override
       		public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
       			switch(pAreaTouchEvent.getAction()) {
       			case TouchEvent.ACTION_DOWN:
       				
       			}
       			return true;
       		}
		};
		nextButton = new Sprite(0.0f, 0.0f, mNextButtonTextureRegion){
       		@Override
       		public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
       			switch(pAreaTouchEvent.getAction()) {
       			case TouchEvent.ACTION_DOWN:
       				
       			}
       			return true;
       		}
		};
		quitButton = new Sprite(0.0f, 0.0f, mQuitButtonTextureRegion){
       		@Override
       		public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
       			switch(pAreaTouchEvent.getAction()) {
       			case TouchEvent.ACTION_DOWN:
       				
       			}
       			return true;
       		}
		};


		mScene.registerUpdateHandler(this.mPhysicsWorld);		
		return mScene;
	}

	@Override
	public void onLoadComplete() {
		
		this.mPhysicsWorld.setContactListener( new ContactListener() {
	         @Override
	         public void beginContact(Contact contact) {
	        	 Body bodyA = contact.getFixtureA().getBody();
	        	 Body bodyB = contact.getFixtureB().getBody();
	        	 String idA = (String)bodyA.getUserData();
	        	 String idB = (String)bodyB.getUserData();
	        	 if ((idA.startsWith("vamp")) && (idB.equals("floor"))) {
	        		 playSound(mOofSound);
	        		 int vampID = Integer.parseInt(idA.substring(4, 5));
	        		 if (!deadHeads.contains(vampID)) deadHeads.add(vampID);
	        		 mAddScore(VAMPIRE_FLOORED);
	        		 if (deadHeads.size() == numHeads) mGameOver(PLAYER_WINS);
	        	 }
	        	 if ((idB.startsWith("vamp")) && (idA.equals("floor"))) {
	        		 playSound(mOofSound);
	        		 int vampID = Integer.parseInt(idB.substring(4, 5));
	        		 if (!deadHeads.contains(vampID)) deadHeads.add(vampID);
	        		 mAddScore(VAMPIRE_FLOORED);
	        		 if (deadHeads.size() == numHeads) mGameOver(PLAYER_WINS);
	        	 }
	         }
             public void endContact(Contact contact) {
	         }
		});
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			final Scene scene = this.mEngine.getScene();
			if(pSceneTouchEvent.isActionDown()) {
				isStakeSpawning = true;
				stakeX = pSceneTouchEvent.getX();
				stakeY = pSceneTouchEvent.getY();
				return true;
			}
			if (pSceneTouchEvent.isActionMove()){
				if (isStakeSpawning){
					// Draw line, angle stake
					if (stakeLine == null){
						stakeLine = new Line(pSceneTouchEvent.getX(), pSceneTouchEvent.getY(), stakeX, stakeY);					
					} else{
						stakeLine.setPosition(pSceneTouchEvent.getX(), pSceneTouchEvent.getY(), stakeX, stakeY);
					}
					scene.getLastChild().attachChild(stakeLine);
					return true;
				}
			}
			if (pSceneTouchEvent.isActionUp()){
				// Launch stake
				velX = (stakeX - pSceneTouchEvent.getX())/6.0f;
				velY = (stakeY - pSceneTouchEvent.getY())/6.0f;
				this.addStake(stakeX, stakeY, velX, velY);
				if (stakeLine != null) stakeLine.setPosition(0.0f, 0.0f, 0.0f, 0.0f);
				isStakeSpawning = false;
				return true;
			}
		}
		return false;
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	private void addStake(final float pX, final float pY, float velX, float velY) {
		/* If player has used up their stakes, game is over */
		if (numStakes++ > MAX_STAKES) mGameOver(VAMPIRES_WIN);
		final Scene scene = this.mEngine.getScene();
		stakesprite = new Sprite(pX, pY, this.mStakeTextureRegion);
		stakesprite.registerEntityModifier(new RotationModifier(0.1f, 0.0f, (float) ((360.0f/Math.PI)*Math.atan(velY/velX))));
		stake = PhysicsFactory.createBoxBody(this.mPhysicsWorld, stakesprite, BodyType.DynamicBody, FIXTURE_DEF);
		stake.setUserData("stake");
		stake.setBullet(true);
		stake.setLinearVelocity(new Vector2(velX, velY));
		stake.setSleepingAllowed(true);

		scene.getLastChild().attachChild(stakesprite);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(stakesprite, stake, true, true));
	}

	private TextureRegion selectTexture(String id){
		if (id.equals("wood")){
			return mWoodTextureRegion;
		} else if (id.equals("stone")){
			return mStoneTextureRegion;
		} else {
			return mGlassTextureRegion;
		}
	}
	
  public static String trimQuotes( String value )
  {
    if ( value == null )
      return value;

    value = value.trim( );
    if ( value.startsWith( "\'" ) && value.endsWith( "\'" ) )
      return value.substring( 1, value.length( ) - 1 );
    
    return value;
  }

     private void mGameOver(boolean pWin){
    	 // Called when gamelet is over - pWin=true if player won
    	 Scene scene = IVActivity.this.mEngine.getScene();
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
		 scoresEditor.putInt("IV-4", highScores[4]);
		 scoresEditor.putInt("IV-3", highScores[3]);
		 scoresEditor.putInt("IV-2", highScores[2]);
		 scoresEditor.putInt("IV-1", highScores[1]);
		 scoresEditor.putInt("IV-0", highScores[0]);
		 scoresEditor.commit();

    	 if (pWin){
    		 scene.setChildScene(mCreateEndScene(newTop, true, "Congratulations!!"), false, true, true);
    	 } else {
    		 scene.setChildScene(mCreateEndScene(false, false, "You're Out of Stakes!"));
    	 }
     }
     
     private Scene mCreateEndScene(boolean pNewHigh, boolean pWin, String pTitle){
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
     
     private void playSound (Sound mSound){
    	 if (audioOptions.getBoolean("effectsOn", false)) {
    		 mSound.play();
    	 }
     }
     
     private void mAddScore(int pAdder){
    	 thisScore += pAdder;
    	 mCurrScore.setText("Score: " + thisScore);
     }

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
