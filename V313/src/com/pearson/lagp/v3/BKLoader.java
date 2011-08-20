package com.pearson.lagp.v3;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.anddev.andengine.level.LevelLoader;
import org.anddev.andengine.level.LevelLoader.IEntityLoader;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.StreamUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class BKLoader extends LevelLoader {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private final HashMap<String, IBKEntityLoader> mEntityLoaders = new HashMap<String, IBKEntityLoader>();
	
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public void registerEntityLoader(final String pEntityName, final IBKEntityLoader pEntityLoader) {
		this.mEntityLoaders.put(pEntityName, pEntityLoader);
	}

	public void registerEntityLoader(final String[] pEntityNames, final IBKEntityLoader pEntityLoader) {
		final HashMap<String, IBKEntityLoader> entityLoaders = this.mEntityLoaders;

		for(int i = pEntityNames.length - 1; i >= 0; i--) {
			entityLoaders.put(pEntityNames[i], pEntityLoader);
		}
	}

	@Override
	public void loadLevelFromStream(final InputStream pInputStream) throws IOException {
		try{
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			final SAXParser sp = spf.newSAXParser();

			final XMLReader xr = sp.getXMLReader();

			this.onBeforeLoadLevel();

			final BKParser mBKParser = new BKParser(this.mEntityLoaders);
			xr.setContentHandler(mBKParser);

			xr.parse(new InputSource(new BufferedInputStream(pInputStream)));

			this.onAfterLoadLevel();
		} catch (final SAXException se) {
			Debug.e(se);
			/* Doesn't happen. */
		} catch (final ParserConfigurationException pe) {
			Debug.e(pe);
			/* Doesn't happen. */
		} finally {
			StreamUtils.close(pInputStream);
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================


	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface IBKEntityLoader extends IEntityLoader {
		// ===========================================================
		// Constants
		// ===========================================================

		// ===========================================================
		// Methods
		// ===========================================================

		public void onLoadEntity(final String pEntityName, final Attributes pAttributes, final String mStringBuilder);
	}

}
