package com.pearson.lagp.v3;

import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObjectGroup;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXProperties;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTile;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTileProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.os.Handler;

public class WAVActivity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "WAVActivity";

	// ===========================================================
	// Fields
	// ===========================================================

	private Handler mHandler;
	
	protected Camera mCamera;

	protected Scene mMainScene;

	private TMXTiledMap mWAVTMXMap;
	private TMXLayer tmxLayer;
	private TMXTile tmxTile;
	
	private int[] coffins = new int[50];
	private int coffinPtr = 0;
	private int mCoffinGID = -1;
	private int mTombstoneGID = -1;
	private int mBooGID = 7;
	private int mOpenCoffinGID = 1;
	
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
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
				
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
					if(pTMXTileProperties.containsTMXProperty("tombstone", "true")) {
						if (mTombstoneGID<0){
							mTombstoneGID = pTMXTile.getGlobalTileID();
						}
					}
				}
			});
			this.mWAVTMXMap = tmxLoader.loadFromAsset(this, "gfx/WAV/WAVmapEx.tmx");
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
							tmxTile.setGlobalTileID(mWAVTMXMap, mCoffinGID);
						} else {
						if((tmxTile != null) && (tmxTile.getGlobalTileID() == mTombstoneGID)) {
							tmxTile.setGlobalTileID(mWAVTMXMap, mBooGID);
						}}
						break;
					case TouchEvent.ACTION_UP:
						break;
				}
				return true;
			}
		});

		mHandler.postDelayed(openCoffin,gen.nextInt(2000));
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
    private Runnable openCoffin = new Runnable() {
        public void run() {
        	int openThis = gen.nextInt(coffinPtr);
        	int tileRow = coffins[openThis]/15;
        	int tileCol = coffins[openThis] % 15;
        	tmxTile = tmxLayer.getTMXTileAt(tileCol*32 + 16, tileRow*32 + 16);
        	tmxTile.setGlobalTileID(mWAVTMXMap, mOpenCoffinGID);
    		mHandler.postDelayed(openCoffin,gen.nextInt(4000));        	
        }
     };

}
