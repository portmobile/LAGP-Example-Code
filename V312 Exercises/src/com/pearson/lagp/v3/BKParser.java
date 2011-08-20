package com.pearson.lagp.v3;

import java.util.HashMap;

import org.anddev.andengine.level.LevelParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.pearson.lagp.v3.BKLoader.IBKEntityLoader;

public class BKParser extends DefaultHandler   {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	private final StringBuilder mStringBuilder = new StringBuilder();
	
	private final HashMap<String, IBKEntityLoader> mBKEntityLoaders;

	// ===========================================================
	// Constructors
	// ===========================================================
	public BKParser(final HashMap<String, IBKEntityLoader> pEntityLoaders) {
		this.mBKEntityLoaders = pEntityLoaders;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void startElement(final String pUri, final String pLocalName, final String pQualifiedName, final Attributes pAttributes) throws SAXException {
	}
	
	@Override
	public void characters(final char[] pCharacters, final int pStart, final int pLength) throws SAXException {
		this.mStringBuilder.append(pCharacters, pStart, pLength);
	}

	@Override
	public void endElement(final String pUri, final String pLocalName, final String pQualifiedName) throws SAXException {
		final IBKEntityLoader entityLoader = this.mBKEntityLoaders.get(pLocalName);
		if(entityLoader != null) {
			entityLoader.onLoadEntity(pLocalName, null, mStringBuilder.toString().trim());
		} else {
			throw new IllegalArgumentException("BK: Unexpected tag: '" + pLocalName + "'.");
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