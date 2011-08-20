package com.pearson.lagp.v3;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;

public class PXLoader {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Context mContext;
	private final TextureManager mTextureManager;
	private final TextureOptions mTextureOptions;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PXLoader(final Context pContext, final TextureManager pTextureManager) {
		this(pContext, pTextureManager, TextureOptions.DEFAULT);
	}

	public PXLoader(final Context pContext, final TextureManager pTextureManager, final TextureOptions pTextureOptions) {
		this.mContext = pContext;
		this.mTextureManager = pTextureManager;
		this.mTextureOptions = pTextureOptions;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public ParticleSystem createFromAsset(final Context pContext, final String pAssetPath) throws PXLoadException {
		try {
			return this.load(pContext.getAssets().open(pAssetPath));
		} catch (final IOException e) {
			throw new PXLoadException("Could not load ParticleSystem from asset: " + pAssetPath, e);
		}
	}

	public ParticleSystem load(final InputStream pInputStream) throws PXLoadException {
		try{
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			final SAXParser sp = spf.newSAXParser();

			final XMLReader xr = sp.getXMLReader();
			final PXParser pxParser = new PXParser(this.mContext, this.mTextureManager, this.mTextureOptions);
			xr.setContentHandler(pxParser);

			xr.parse(new InputSource(new BufferedInputStream(pInputStream)));

			return pxParser.getPXSystem();
		} catch (final SAXException e) {
			throw new PXLoadException(e);
		} catch (final ParserConfigurationException pe) {
			/* Doesn't happen. */
			return null;
		} catch (final IOException e) {
			throw new PXLoadException(e);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

		// ===========================================================
		// Final Fields
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

}
