package com.pearson.lagp.example;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.BuildableTexture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.builder.ITextureBuilder.TextureSourcePackingException;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.sensor.location.ILocationListener;
import org.anddev.andengine.sensor.location.LocationProviderStatus;
import org.anddev.andengine.sensor.location.LocationSensorOptions;
import org.anddev.andengine.sensor.orientation.IOrientationListener;
import org.anddev.andengine.sensor.orientation.OrientationData;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AndEngineSensorExample extends BaseGameActivity implements IOrientationListener, ILocationListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "AndEngineSensorExample";
	private static final boolean USE_MOCK_LOCATION = false;
	private static final boolean USE_ACTUAL_LOCATION = !USE_MOCK_LOCATION;


	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;
	private Location mUserLocation;

	protected Scene mMainScene;
	protected Sprite mIcon;

	private BuildableTexture mIconTexture;
	private TextureRegion mIconTextureRegion;
	private Location mLocation ;
	
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.mLocation = new Location(LocationManager.GPS_PROVIDER);
	}

	@Override
	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		mIconTexture = new BuildableTexture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mIconTextureRegion = TextureRegionFactory.createFromAsset(this.mIconTexture, this, "icon.png");
		   try {
			      mIconTexture.build(new BlackPawnTextureBuilder(2));
			   } catch (final TextureSourcePackingException e) {
			      Log.d(tag, "Sprites won't fit in mIconTexture");
			   }
		this.mEngine.getTextureManager().loadTexture(this.mIconTexture);
	}
	
	@Override
	public Scene onLoadScene() {
		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.1f, 0.6f, 0.9f));
		
		mIcon = new Sprite(100, 100, this.mIconTextureRegion);
		scene.getLastChild().attachChild(mIcon);		

		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		this.enableOrientationSensor(AndEngineSensorExample.this);

		final LocationSensorOptions locationSensorOptions = new LocationSensorOptions();
		locationSensorOptions.setAccuracy(Criteria.ACCURACY_COARSE);
		locationSensorOptions.setMinimumTriggerTime(0);
		locationSensorOptions.setMinimumTriggerDistance(0);
		this.enableLocationSensor(AndEngineSensorExample.this, locationSensorOptions);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.mEngine.disableOrientationSensor(this);
		this.mEngine.disableLocationSensor(this);
	}

	@Override
	public void onOrientationChanged(final OrientationData pOrientationData) {
		float yaw = pOrientationData.getYaw() / 360.0f;
		mIcon.setPosition( CAMERA_WIDTH/2, yaw * CAMERA_HEIGHT);
	}

	@Override
	public void onLocationChanged(final Location pLocation) {
		String tst = "Lat: " + pLocation.getLatitude()+"  Lng: " + pLocation.getLongitude();
		Toast.makeText(AndEngineSensorExample.this, tst, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onLocationLost() {
	}

	@Override
	public void onLocationProviderDisabled() {
	}

	@Override
	public void onLocationProviderEnabled() {
	}

	@Override
	public void onLocationProviderStatusChanged(final LocationProviderStatus pLocationProviderStatus, final Bundle pBundle) {
	}


	
	

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
