package com.pearson.lagp.v3;

import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.entity.particle.emitter.BaseParticleEmitter;
import org.anddev.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.anddev.andengine.entity.particle.emitter.CircleParticleEmitter;
import org.anddev.andengine.entity.particle.emitter.PointParticleEmitter;
import org.anddev.andengine.entity.particle.emitter.RectangleOutlineParticleEmitter;
import org.anddev.andengine.entity.particle.emitter.RectangleParticleEmitter;
import org.anddev.andengine.entity.particle.initializer.AccelerationInitializer;
import org.anddev.andengine.entity.particle.initializer.AlphaInitializer;
import org.anddev.andengine.entity.particle.initializer.ColorInitializer;
import org.anddev.andengine.entity.particle.initializer.GravityInitializer;
import org.anddev.andengine.entity.particle.initializer.RotationInitializer;
import org.anddev.andengine.entity.particle.initializer.VelocityInitializer;
import org.anddev.andengine.entity.particle.modifier.AlphaModifier;
import org.anddev.andengine.entity.particle.modifier.ColorModifier;
import org.anddev.andengine.entity.particle.modifier.ExpireModifier;
import org.anddev.andengine.entity.particle.modifier.RotationModifier;
import org.anddev.andengine.entity.particle.modifier.ScaleModifier;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class PXParser extends DefaultHandler implements PXConstants   {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private boolean mInPX;
	private boolean mInEmitter;
	private boolean mInTexture;
	private boolean mInInitAccel;
	private boolean mInInitAlpha;
	private boolean mInInitColor;
	private boolean mInInitGravity;
	private boolean mInInitRotation;
	private boolean mInInitVelocity;
	private boolean mInModAlpha;
	private boolean mInModColor;
	private boolean mInModExpire;
	private boolean mInModRotation;
	private boolean mInModScale;
	private boolean mInSystem;

	private boolean mGravity;
	
	private final StringBuilder mStringBuilder = new StringBuilder();

	private Context mContext;
	private TextureManager mTextureManager;
	private TextureOptions mTextureOptions;
	private Texture mParticleTexture;
	private TextureRegion mParticleTextureRegion;
	
	private BaseParticleEmitter mPXEmitter;
	private ParticleSystem mPXSystem;
	
	private String mEmitter;
	private String mTextureFile;
	
	
	// ===========================================================
	// Constructors
	// ===========================================================

	public PXParser(final Context pContext, final TextureManager pTextureManager, final TextureOptions pTextureOptions) {
		this.mContext = pContext;
		this.mTextureManager = pTextureManager;
		this.mTextureOptions = pTextureOptions;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	ParticleSystem getPXSystem() {
		return this.mPXSystem;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void startElement(final String pUri, final String pLocalName, final String pQualifiedName, final Attributes pAttributes) throws SAXException {
		if(pLocalName.equals(TAG_PCONF)){
			this.mInPX = true;
		} else if(pLocalName.equals(TAG_SYSTEM)){
			this.mInSystem = true;
			if (mPXEmitter == null) {
				throw new PXParseException("Must define emitter before system.");
			}
			if ((mTextureFile = pAttributes.getValue(TAG_SYSTEM_TEXTURE)) == null) {
				throw new PXParseException("Texture is required.");
			}
			
			mParticleTexture = new Texture(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			mParticleTextureRegion = TextureRegionFactory.createFromAsset(this.mParticleTexture, mContext, mTextureFile, 0, 0);
			mTextureManager.loadTexture(this.mParticleTexture);
			mPXSystem = new ParticleSystem(mPXEmitter, 
					new Integer(pAttributes.getValue(TAG_SYSTEM_MIN_RATE)), 
					new Integer(pAttributes.getValue(TAG_SYSTEM_MAX_RATE)), 
					new Integer(pAttributes.getValue(TAG_SYSTEM_MAX_PARTICLES)), 
					mParticleTextureRegion);
		} else if(pLocalName.equals(TAG_EMITTER)){
		this.mInEmitter = true;
		mEmitter = pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_SHAPE);
			if (mEmitter.equals(TAG_EMITTER_ATTRIBUTE_SHAPE_VALUE_CIRCLE)) {
				mPXEmitter = new CircleParticleEmitter(
						new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_CENTER_X)), 
						new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_CENTER_Y)), 
						new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_RADIUS_X)), 
						new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_RADIUS_Y))
				);
			} else if (mEmitter.equals(TAG_EMITTER_ATTRIBUTE_SHAPE_VALUE_CIRCLE_OUTLINE)) {
				mPXEmitter = new CircleOutlineParticleEmitter(
						new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_CENTER_X)), 
						new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_CENTER_Y)), 
						new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_RADIUS_X)), 
						new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_RADIUS_Y))
					);				
				} else if (mEmitter.equals(TAG_EMITTER_ATTRIBUTE_SHAPE_VALUE_POINT)) {
					mPXEmitter = new PointParticleEmitter(
							new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_CENTER_X)), 
							new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_CENTER_Y))
					);
				} else if (mEmitter.equals(TAG_EMITTER_ATTRIBUTE_SHAPE_VALUE_RECTANGLE)) {
					mPXEmitter = new RectangleParticleEmitter(
							new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_CENTER_X)), 
							new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_CENTER_Y)), 
							new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_WIDTH)), 
							new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_HEIGHT))
					);	
				} else if (mEmitter.equals(TAG_EMITTER_ATTRIBUTE_SHAPE_VALUE_RECTANGLE_OUTLINE)) {
					mPXEmitter = new RectangleOutlineParticleEmitter(
							new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_CENTER_X)), 
							new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_CENTER_Y)), 
							new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_WIDTH)), 
							new Float(pAttributes.getValue(TAG_EMITTER_ATTRIBUTE_HEIGHT))
					);	
				}
		} else if(pLocalName.equals(TAG_INITIAL_ACCELERATION)){
			if (!mInSystem) {
				throw new PXParseException("Must define initial acceleration inside system.");
			}
			this.mInInitAccel = true;
			mPXSystem.addParticleInitializer(new AccelerationInitializer(
					new Float(pAttributes.getValue(TAG_INITIAL_ACCELERATION_ATTRIBUTE_MIN_X)),
					new Float(pAttributes.getValue(TAG_INITIAL_ACCELERATION_ATTRIBUTE_MAX_X)),
					new Float(pAttributes.getValue(TAG_INITIAL_ACCELERATION_ATTRIBUTE_MIN_Y)),
					new Float(pAttributes.getValue(TAG_INITIAL_ACCELERATION_ATTRIBUTE_MAX_Y))
			));
		} else if(pLocalName.equals(TAG_INITIAL_ALPHA)) {
			if (!mInSystem) {
				throw new PXParseException("Must define initial alpha inside system.");
			}
			this.mInInitAlpha = true;
			mPXSystem.addParticleInitializer(new AlphaInitializer(
					new Float(pAttributes.getValue(TAG_INITIAL_ALPHA_ATTRIBUTE_MIN_ALPHA)),
					new Float(pAttributes.getValue(TAG_INITIAL_ALPHA_ATTRIBUTE_MAX_ALPHA))
			));
		} else if(pLocalName.equals(TAG_INITIAL_COLOR)) {
			if (!mInSystem) {
				throw new PXParseException("Must define initial color inside system.");
			}
			this.mInInitColor = true;
			mPXSystem.addParticleInitializer(new ColorInitializer(
				new Float(pAttributes.getValue(TAG_INITIAL_COLOR_ATTRIBUTE_MIN_RED)),
				new Float(pAttributes.getValue(TAG_INITIAL_COLOR_ATTRIBUTE_MAX_RED)),
				new Float(pAttributes.getValue(TAG_INITIAL_COLOR_ATTRIBUTE_MIN_GREEN)),
				new Float(pAttributes.getValue(TAG_INITIAL_COLOR_ATTRIBUTE_MAX_GREEN)),
				new Float(pAttributes.getValue(TAG_INITIAL_COLOR_ATTRIBUTE_MIN_BLUE)),
				new Float(pAttributes.getValue(TAG_INITIAL_COLOR_ATTRIBUTE_MAX_BLUE))
				));
			
		} else if(pLocalName.equals(TAG_INITIAL_GRAVITY)) {
			if (!mInSystem) {
				throw new PXParseException("Must define initial gravity inside system.");
			}
			this.mInInitGravity = true;
			mGravity = new Boolean(pAttributes.getValue(TAG_INITIAL_GRAVITY_ATTRIBUTE_VALUE));
			if (mGravity) mPXSystem.addParticleInitializer(new GravityInitializer());
		} else if(pLocalName.equals(TAG_INITIAL_ROTATION)) {
			if (!mInSystem) {
				throw new PXParseException("Must define initial rotation inside system.");
			}
			this.mInInitRotation = true;
			mPXSystem.addParticleInitializer(new RotationInitializer(
					new Float(pAttributes.getValue(TAG_INITIAL_ROTATION_ATTRIBUTE_MIN_ROTATION)), 
					new Float(pAttributes.getValue(TAG_INITIAL_ROTATION_ATTRIBUTE_MAX_ROTATION))
			));
		} else if(pLocalName.equals(TAG_INITIAL_VELOCITY)) {
			if (!mInSystem) {
				throw new PXParseException("Must define initial velocity inside system.");
			}
			this.mInInitVelocity = true;
			mPXSystem.addParticleInitializer(new VelocityInitializer(
					new Float(pAttributes.getValue(TAG_INITIAL_VELOCITY_ATTRIBUTE_MIN_X)), 
					new Float(pAttributes.getValue(TAG_INITIAL_VELOCITY_ATTRIBUTE_MAX_X)), 
					new Float(pAttributes.getValue(TAG_INITIAL_VELOCITY_ATTRIBUTE_MIN_Y)), 
					new Float(pAttributes.getValue(TAG_INITIAL_VELOCITY_ATTRIBUTE_MAX_Y))
			));
		} else if(pLocalName.equals(TAG_MODIFY_ALPHA)) {
			if (!mInSystem) {
				throw new PXParseException("Must define alpha modifier inside system.");
			}
			this.mInModAlpha = true;
			mPXSystem.addParticleModifier(new AlphaModifier(
					new Float(pAttributes.getValue(TAG_MODIFY_ALPHA_ATTRIBUTE_FROM_ALPHA)),
					new Float(pAttributes.getValue(TAG_MODIFY_ALPHA_ATTRIBUTE_TO_ALPHA)),
					new Float(pAttributes.getValue(TAG_MODIFY_ALPHA_ATTRIBUTE_FROM_TIME)),
					new Float(pAttributes.getValue(TAG_MODIFY_ALPHA_ATTRIBUTE_TO_TIME))
			));
		} else if(pLocalName.equals(TAG_MODIFY_COLOR)) {
			if (!mInSystem) {
				throw new PXParseException("Must define color modifier inside system.");
			}
			this.mInModColor = true;
			mPXSystem.addParticleModifier(new ColorModifier(
					new Float(pAttributes.getValue(TAG_MODIFY_COLOR_ATTRIBUTE_FROM_RED)),
					new Float(pAttributes.getValue(TAG_MODIFY_COLOR_ATTRIBUTE_TO_RED)),
					new Float(pAttributes.getValue(TAG_MODIFY_COLOR_ATTRIBUTE_FROM_GREEN)),
					new Float(pAttributes.getValue(TAG_MODIFY_COLOR_ATTRIBUTE_TO_GREEN)),
					new Float(pAttributes.getValue(TAG_MODIFY_COLOR_ATTRIBUTE_FROM_BLUE)),
					new Float(pAttributes.getValue(TAG_MODIFY_COLOR_ATTRIBUTE_TO_BLUE)),
					new Float(pAttributes.getValue(TAG_MODIFY_COLOR_ATTRIBUTE_FROM_TIME)),
					new Float(pAttributes.getValue(TAG_MODIFY_COLOR_ATTRIBUTE_TO_TIME))
			));
		} else if(pLocalName.equals(TAG_MODIFY_EXPIRE)) {
			if (!mInSystem) {
				throw new PXParseException("Must define expire modifier inside system.");
			}
			this.mInModExpire = true;
			mPXSystem.addParticleModifier(new ExpireModifier(
					new Float(pAttributes.getValue(TAG_MODIFY_EXPIRE_ATTRIBUTE_MIN_LIFETIME)),
					new Float(pAttributes.getValue(TAG_MODIFY_EXPIRE_ATTRIBUTE_MAX_LIFETIME))
			));
		} else if(pLocalName.equals(TAG_MODIFY_ROTATION)) {
			if (!mInSystem) {
				throw new PXParseException("Must define rotation modifier inside system.");
			}
			this.mInModRotation = true;
			mPXSystem.addParticleModifier(new RotationModifier(
					new Float(pAttributes.getValue(TAG_MODIFY_ROTATION_ATTRIBUTE_FROM_ROTATION)),
					new Float(pAttributes.getValue(TAG_MODIFY_ROTATION_ATTRIBUTE_TO_ROTATION)),
					new Float(pAttributes.getValue(TAG_MODIFY_ROTATION_ATTRIBUTE_FROM_TIME)),
					new Float(pAttributes.getValue(TAG_MODIFY_ROTATION_ATTRIBUTE_TO_TIME))
			));
		} else if(pLocalName.equals(TAG_MODIFY_SCALE)) {
			if (!mInSystem) {
				throw new PXParseException("Must define scale modifier inside system.");
			}
			this.mInModScale = true;
			mPXSystem.addParticleModifier(new ScaleModifier(
					new Float(pAttributes.getValue(TAG_MODIFY_SCALE_ATTRIBUTE_FROM_SCALE_X)),
					new Float(pAttributes.getValue(TAG_MODIFY_SCALE_ATTRIBUTE_TO_SCALE_X)),
					new Float(pAttributes.getValue(TAG_MODIFY_SCALE_ATTRIBUTE_FROM_SCALE_Y)),
					new Float(pAttributes.getValue(TAG_MODIFY_SCALE_ATTRIBUTE_TO_SCALE_Y)),
					new Float(pAttributes.getValue(TAG_MODIFY_SCALE_ATTRIBUTE_FROM_TIME)),
					new Float(pAttributes.getValue(TAG_MODIFY_SCALE_ATTRIBUTE_TO_TIME))
			));
		}
	}
	
	@Override
	public void characters(final char[] pCharacters, final int pStart, final int pLength) throws SAXException {
		this.mStringBuilder.append(pCharacters, pStart, pLength);
	}

	@Override
	public void endElement(final String pUri, final String pLocalName, final String pQualifiedName) throws SAXException {
		if(pLocalName.equals(TAG_PCONF)){
			this.mInPX = false;
		} else if(pLocalName.equals(TAG_EMITTER)){
			this.mInEmitter = false;
		} else if(pLocalName.equals(TAG_INITIAL_ACCELERATION)){
			this.mInInitAccel = false;
		} else if(pLocalName.equals(TAG_INITIAL_ALPHA)) {
			this.mInInitAlpha = false;
		} else if(pLocalName.equals(TAG_INITIAL_COLOR)) {
			this.mInInitColor = false;
		} else if(pLocalName.equals(TAG_INITIAL_GRAVITY)) {
			this.mInInitGravity = false;
		} else if(pLocalName.equals(TAG_INITIAL_ROTATION)){
			this.mInInitRotation = false;
		} else if(pLocalName.equals(TAG_INITIAL_VELOCITY)){
			this.mInInitVelocity = false;
		} else if(pLocalName.equals(TAG_MODIFY_ALPHA)){
			this.mInModAlpha = false;
		} else if(pLocalName.equals(TAG_MODIFY_COLOR)){
			this.mInModColor = false;
		} else if(pLocalName.equals(TAG_MODIFY_EXPIRE)){
			this.mInModExpire = false;
		} else if(pLocalName.equals(TAG_MODIFY_ROTATION)){
			this.mInModRotation = false;
		} else if(pLocalName.equals(TAG_MODIFY_SCALE)){
			this.mInModScale = false;
		} else if(pLocalName.equals(TAG_SYSTEM)){
			this.mInSystem = false;
		} else {
			throw new PXParseException("Unexpected end tag: '" + pLocalName + "'.");
		}

		/* Reset the StringBuilder. */
		this.mStringBuilder.setLength(0);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}