package com.pearson.lagp.example;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.StrokeFont;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.HorizontalAlign;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Toast;

public class TextExample extends BaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;
	private Texture mFontTexture, mStrokeFontTexture;
	private Font mFont;
	private StrokeFont mStrokeFont;

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
		this.mFontTexture = new Texture(256, 256, 
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.mStrokeFontTexture = new Texture(256, 256, 
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.mFont = new Font(this.mFontTexture, 
				Typeface.create(Typeface.DEFAULT, 
				Typeface.BOLD), 32, true, Color.BLACK);

		this.mStrokeFont = new StrokeFont(this.mStrokeFontTexture, 
				Typeface.create(Typeface.DEFAULT, 
				Typeface.BOLD), 32, true, Color.RED, 2.0f, Color.WHITE, true);

		this.mEngine.getTextureManager().loadTexture(
				 this.mFontTexture);
		this.mEngine.getTextureManager().loadTexture(
				 this.mStrokeFontTexture);
			this.mEngine.getFontManager().loadFont(this.mFont);
			this.mEngine.getFontManager().loadFont(this.mStrokeFont);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.1f, 0.6f, 0.9f));

		final Text textCenter = new Text(100, 60, this.mFont, 
				//"ABCDEFGHIJKLMNOP\n" +
				//"QRSTUVWXYZ\n" +
				//"abcdefghijklmnop\n" +
				//"qrstuvwxyz",
				"Show this centered \n on two lines.", 
				HorizontalAlign.CENTER);

		final Text textStroke = new Text(100, 160, this.mStrokeFont, 
				//		"ABCDEFGHIJKLMNOP\n" +
				//		"QRSTUVWXYZ\n" +
				//		"abcdefghijklmnop\n" +
				//		"qrstuvwxyz",
						"Stroke font example \n also on two lines.", 
						HorizontalAlign.CENTER);

		scene.getLastChild().attachChild(textCenter);
		scene.getLastChild().attachChild(textStroke);

		return scene;
	}

	@Override
	public void onLoadComplete() {
		Toast.makeText(this, "This is a Toast.", Toast.LENGTH_LONG).show();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
