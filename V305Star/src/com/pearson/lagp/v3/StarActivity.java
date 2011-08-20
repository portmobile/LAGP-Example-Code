package com.pearson.lagp.v3;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class StarActivity extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "SpriteTestActivity";

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;

	protected Scene mMainScene;
	
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
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));

		/* Center the camera. */
		final int centerX = CAMERA_WIDTH  / 2;
		final int centerY = CAMERA_HEIGHT / 2;
		
		/* Draw the star */
		Line star1 = new Line(centerX-100, centerY-40, centerX+100, centerY-40, 3.0f);
		star1.setColor(1.0f, 0.0f, 0.0f);
		Line star2 = new Line(200, 0, 40, 125, 3.0f);
		star2.setColor(1.0f, 0.0f, 0.0f);
		Line star3 = new Line(-160, 125, -100, -75, 3.0f);
		star3.setColor(1.0f, 0.0f, 0.0f);
		Line star4 = new Line(-100, -75, -30, 125, 3.0f);
		star4.setColor(1.0f, 0.0f, 0.0f);
		Line star5 = new Line(-30, 125, -200, 0, 3.0f);
		star5.setColor(1.0f, 0.0f, 0.0f);
		star1.attachChild(star2);
		star1.getLastChild().attachChild(star3);
		star1.getLastChild().attachChild(star4);
		star1.getLastChild().attachChild(star5);
		star1.setRotationCenter(100, 40);
		star1.registerEntityModifier(new RotationModifier(5.0f, 0.0f, 360.0f));
		scene.getLastChild().attachChild(star1);
		
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}
}
