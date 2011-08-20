package com.pearson.lagp.example;

import java.util.ArrayList;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.BuildableTexture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.builder.ITextureBuilder.TextureSourcePackingException;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

public class VoiceRecExample extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "VoiceRecExample";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	// ===========================================================
	// Fields
	// ===========================================================

	protected Camera mCamera;

	protected Scene mMainScene;
	protected Sprite mIcon;

	private BuildableTexture mIconTexture;
	private TextureRegion mIconTextureRegion;
	
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
		
		mIcon = new Sprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, this.mIconTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch(pAreaTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					startVoiceRecognitionActivity();
					break;
				case TouchEvent.ACTION_UP:
					
					break;
				}
				return true;
			}
		};
		
		scene.getLastChild().attachChild(mIcon);		
		scene.registerTouchArea(mIcon);
		scene.setTouchAreaBindingEnabled(true);
		Toast.makeText(VoiceRecExample.this, "Touch icon and say move left/right/up/down", Toast.LENGTH_SHORT).show();
		return scene;
	}

	@Override
	public void onLoadComplete() {
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            for (String match : matches){
            	if (match.equalsIgnoreCase("move left")){
            		mIcon.setPosition(mIcon.getX()-10.0f, mIcon.getY());
            	}
            	if (match.equalsIgnoreCase("move right")){
            		mIcon.setPosition(mIcon.getX()+10.0f, mIcon.getY());
            	}
            	if (match.equalsIgnoreCase("move up")){
            		mIcon.setPosition(mIcon.getX(), mIcon.getY()-10.0f);
            	}
            	if (match.equalsIgnoreCase("move down")){
            		mIcon.setPosition(mIcon.getX(), mIcon.getY()+10.0f);
            	}
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
