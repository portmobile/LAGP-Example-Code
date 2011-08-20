package com.pearson.lagp.v3;

import java.io.IOException;

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
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.extension.svg.opengl.texture.source.SVGAssetTextureSource;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.source.ITextureSource;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;
import org.xml.sax.Attributes;

import android.hardware.SensorManager;

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

	private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

	// ===========================================================
	// Fields
	// ===========================================================

	private Texture mTexture;

	private TextureRegion mStakeTextureRegion;
	private TextureRegion mGlassTextureRegion;
	private TextureRegion mWoodTextureRegion;
	private TextureRegion mStoneTextureRegion;
	private TextureRegion mMatHeadTextureRegion;
	private TextureRegion mSlingTextureRegion;
	private Sprite stakesprite;
	private Sprite sling;
	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;

	private boolean isStakeSpawning = false;
	private float stakeX, stakeY;
	private float velX, velY;
	private Line stakeLine;
	
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
	private String mVerts;
	
	private float PtoM = PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
	

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
		return new Engine(engineOptions);
	}

	@Override
	public void onLoadResources() {
		/* Textures. */
		this.mTexture = new Texture(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		TextureRegionFactory.setAssetBasePath("gfx/IV/");

		/* TextureRegions. */
		ITextureSource mSlingTextureSource = new SVGAssetTextureSource(this,  "gfx/IV/sling.svg",  1.0f);
		ITextureSource mStakeTextureSource = new SVGAssetTextureSource(this,  "gfx/IV/stake.svg",  1.0f);
		ITextureSource mGlassTextureSource = new SVGAssetTextureSource(this,  "gfx/IV/glass.svg",  1.0f);
		ITextureSource mStoneTextureSource = new SVGAssetTextureSource(this,  "gfx/IV/stone.svg",  1.0f);
		ITextureSource mMatHeadTextureSource = new SVGAssetTextureSource(this,  "gfx/IV/mathead.svg",  1.0f);
		mSlingTextureRegion = TextureRegionFactory.createFromSource(this.mTexture, mSlingTextureSource, 0, 0);
		mStakeTextureRegion = TextureRegionFactory.createFromSource(this.mTexture, mStakeTextureSource, 0, 40);
		mGlassTextureRegion = TextureRegionFactory.createFromSource(this.mTexture, mGlassTextureSource, 0, 80);
		mStoneTextureRegion = TextureRegionFactory.createFromSource(this.mTexture, mStoneTextureSource, 0, 120);
		mMatHeadTextureRegion = TextureRegionFactory.createFromSource(this.mTexture, mMatHeadTextureSource, 0, 160);
		this.mWoodTextureRegion = TextureRegionFactory.createFromAsset(this.mTexture, getApplicationContext(), "wood.png", 0, 210);
		this.mEngine.getTextureManager().loadTexture(this.mTexture);
		
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

		final Sprite sling = new Sprite(60, 300, mStakeTextureRegion);
		mScene.getLastChild().attachChild(sling);
		
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
		mScene.registerUpdateHandler(this.mPhysicsWorld);		
		return mScene;
	}

	@Override
	public void onLoadComplete() {
		
		this.mPhysicsWorld.setContactListener( new ContactListener() {
	         @Override
	         public void beginContact(Contact contact) {
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
		final Scene scene = this.mEngine.getScene();
		stakesprite = new Sprite(pX, pY, this.mStakeTextureRegion);
		stakesprite.registerEntityModifier(new RotationModifier(0.1f, 0.0f, (float) ((360.0f/Math.PI)*Math.atan(velY/velX))));
		stake = PhysicsFactory.createBoxBody(this.mPhysicsWorld, stakesprite, BodyType.DynamicBody, FIXTURE_DEF);
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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
