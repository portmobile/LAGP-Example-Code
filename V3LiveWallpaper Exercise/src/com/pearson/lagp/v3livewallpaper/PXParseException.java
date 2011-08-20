package com.pearson.lagp.v3livewallpaper;

import org.xml.sax.SAXException;

public class PXParseException extends SAXException {
	// ===========================================================
	// Constants
	// ===========================================================

	//private static final long serialVersionUID = 2213964295487921492L;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public PXParseException() {
		super();
	}

	public PXParseException(final String pDetailMessage) {
		super(pDetailMessage);
	}

	public PXParseException(final Exception pException) {
		super(pException);
	}

	public PXParseException(final String pMessage, final Exception pException) {
		super(pMessage, pException);
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

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
