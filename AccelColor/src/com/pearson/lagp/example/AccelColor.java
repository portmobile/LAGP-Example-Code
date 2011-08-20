package com.pearson.lagp.example;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class AccelColor extends BaseGameActivity implements IAccelerometerListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	private Scene scene;


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
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		this.enableAccelerometerSensor(this);
	}

	@Override
	public Scene onLoadScene() {
		scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.0f, 1.0f, 0.0f));
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
	
	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
		String message = "X= "+pAccelerometerData.getX()+"; Y="+pAccelerometerData.getY()+"; Z="+pAccelerometerData.getZ()+";";
		//Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		if (pAccelerometerData.getY() > 5.0f){
			scene.setBackground(new ColorBackground(1.0f, 0.0f, 0.0f));
		} else {
			scene.setBackground(new ColorBackground(0.0f, 1.0f, 0.0f));
		}
	}
	

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
