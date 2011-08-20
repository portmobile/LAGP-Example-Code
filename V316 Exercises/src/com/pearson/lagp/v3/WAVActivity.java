package com.pearson.lagp.v3;

import java.util.ArrayList;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXProperties;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTile;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTileProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;

public class WAVActivity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "WAVActivity";

	private static final int CLOSE_COFFIN_SCORE = 100;
	private static final boolean PLAYER_WINS = true;
	private static final boolean VAMPIRES_WIN = false;
	
	// ===========================================================
	// Fields
	// ===========================================================

	private Handler mHandler;
	
	protected Camera mCamera;

	protected Scene mMainScene;

	private TMXTiledMap mWAVTMXMap;
	private TMXLayer tmxLayer;
	private TMXTile tmxTile;

	private Texture mPopUpTexture;
	private TextureRegion mEndBackTextureRegion;
	private TextureRegion mAgainButtonTextureRegion;
	private TextureRegion mQuitButtonTextureRegion;
	private TextureRegion mNextButtonTextureRegion;
	private TextureRegion mNewHighTextureRegion;
	private Texture mFontTexture;
	private Font mFont32;
	private ChangeableText mCurrScore;
	private Sprite endBack, newHigh, againButton, quitButton, nextButton;

	private int[] coffins = new int[50];
	private int coffinPtr = 0;
	private int mCoffinGID = -1;
	private int mOpenCoffinGID = 1;
	
	private SharedPreferences scores, difficulty;
	private SharedPreferences.Editor scoresEditor, diffEditor;
	private int[] highScores = new int[5];
	private int thisScore = 0;
	private int mNumClosed = 0;
	private ArrayList<Integer> openCoffins = new ArrayList<Integer>();
	private int mOpenRate;
	private int mOpensPerGame;
	private int mStayOpen;
	private int mWins, mPlays;
	private boolean mPlayerWon;

	private Random gen;
	
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
		scores = getSharedPreferences("scores", MODE_PRIVATE);
		scoresEditor = scores.edit();
		highScores[4] = scores.getInt("WhAV-4", 0);
		highScores[3] = scores.getInt("WhAV-3", 0);
		highScores[2] = scores.getInt("WhAV-2", 0);
		highScores[1] = scores.getInt("WhAV-1", 0);
		highScores[0] = scores.getInt("WhAV-0", 0);
		
		difficulty = getSharedPreferences("difficulty", MODE_PRIVATE);
		diffEditor = difficulty.edit();
		mOpenRate = difficulty.getInt("WhAV.OPEN_RATE", 4000);
		mStayOpen = difficulty.getInt("WhAV.STAY_OPEN", 2000);
		mOpensPerGame = difficulty.getInt("WhAV.OPENS_PER_GAME", 10);
		mWins = difficulty.getInt("WhAV.WINS", 0);
		mPlays = difficulty.getInt("WhAV.PLAYS", 0);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
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
				
	}
	
	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		try {
			final TMXLoader tmxLoader = new TMXLoader(this, this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
					if(pTMXTileProperties.containsTMXProperty("coffin", "true")) {
						coffins[coffinPtr++] = pTMXTile.getTileRow() * 15 + pTMXTile.getTileColumn();
						if (mCoffinGID<0){
							mCoffinGID = pTMXTile.getGlobalTileID();
						}
					}
				}
			});
			this.mWAVTMXMap = tmxLoader.loadFromAsset(this, "gfx/WAV/WAVmap.tmx");
		} catch (final TMXLoadException tmxle) {
			Debug.e(tmxle);
		}

		tmxLayer = this.mWAVTMXMap.getTMXLayers().get(0);
		scene.getFirstChild().attachChild(tmxLayer);	
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
				switch(pSceneTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
						/* Get the touched tile */
						tmxTile = tmxLayer.getTMXTileAt(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
						if((tmxTile != null) && (tmxTile.getGlobalTileID() == mOpenCoffinGID)) {
							mAddScore(CLOSE_COFFIN_SCORE);
							tmxTile.setGlobalTileID(mWAVTMXMap, mCoffinGID);
						}
						break;
					case TouchEvent.ACTION_UP:
						break;
				}
				return true;
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

		mHandler.postDelayed(openCoffin,gen.nextInt(mOpenRate));
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}

	@Override
	public void onPauseGame(){
		super.onPauseGame();
		mHandler.removeCallbacks(openCoffin);
		mHandler.removeCallbacks(closeCoffin);
	}
	
	private Runnable openCoffin = new Runnable() {
        public void run() {
        	int openThis = gen.nextInt(coffinPtr);
        	int tileRow = coffins[openThis]/15;
        	int tileCol = coffins[openThis] % 15;
        	tmxTile = tmxLayer.getTMXTileAt(tileCol*32 + 16, tileRow*32 + 16);
        	tmxTile.setGlobalTileID(mWAVTMXMap, mOpenCoffinGID);
        	openCoffins.add(openThis);
        	int openTime = gen.nextInt(mOpenRate);
    		mHandler.postDelayed(openCoffin, openTime);        	
    		mHandler.postDelayed(closeCoffin, openTime+mStayOpen);        	
        }
     };

 	private Runnable closeCoffin = new Runnable() {
        public void run() {
        	int closeThis = openCoffins.get(0);
        	openCoffins.remove(0);
        	int tileRow = coffins[closeThis]/15;
        	int tileCol = coffins[closeThis] % 15;
        	tmxTile = tmxLayer.getTMXTileAt(tileCol*32 + 16, tileRow*32 + 16);
			tmxTile.setGlobalTileID(mWAVTMXMap, mCoffinGID);
			if (++mNumClosed > mOpensPerGame) mGameOver(PLAYER_WINS);
        }
     };

     private void mGameOver(boolean pWin){
    	 // Called when gamelet is over - pWin=true if player won
    	 Scene scene = WAVActivity.this.mEngine.getScene();
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
		 scoresEditor.putInt("WhAV-4", highScores[4]);
		 scoresEditor.putInt("WhAV-3", highScores[3]);
		 scoresEditor.putInt("WhAV-2", highScores[2]);
		 scoresEditor.putInt("WhAV-1", highScores[1]);
		 scoresEditor.putInt("WhAV-0", highScores[0]);
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
		if (mOpenRate > 1000) mOpenRate -= 1000;
		if (mStayOpen > 500) mStayOpen -= 200;
		if (mOpensPerGame < 50) mOpensPerGame += 10;
	}

    private void mSaveDifficulty() {
		 diffEditor.putInt("WhAV.OPEN_RATE", mOpenRate);
		 diffEditor.putInt("WhAV.STAY_OPEN", mStayOpen);
		 diffEditor.putInt("WhAV.OPENS_PER_GAME", mOpensPerGame);
		 diffEditor.putInt("WhAV.WINS", mWins);
		 diffEditor.putInt("WhAV.PLAYS", mPlays);
		 diffEditor.commit();
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
     		Intent myIntent = new Intent(WAVActivity.this, WAVActivity.class);
     		WAVActivity.this.startActivity(myIntent);
     		finish();
         }
    };

    private Runnable mPlayNext = new Runnable() {
         public void run() {
      		Intent myIntent = new Intent(WAVActivity.this, IVActivity.class);
      		WAVActivity.this.startActivity(myIntent);
      		finish();
         }
    };
}
