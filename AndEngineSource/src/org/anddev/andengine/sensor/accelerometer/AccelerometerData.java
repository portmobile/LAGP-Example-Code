package org.anddev.andengine.sensor.accelerometer;

import java.util.Arrays;

import org.anddev.andengine.sensor.BaseSensorData;

import android.hardware.SensorManager;

/**
 * @author Nicolas Gramlich
 * @since 16:50:44 - 10.03.2010
 */
public class AccelerometerData extends BaseSensorData {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public AccelerometerData() {
		super(3);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public float getX() {
		return this.mValues[SensorManager.DATA_X];
	}

	public float getY() {
		return this.mValues[SensorManager.DATA_Y];
	}

	public float getZ() {
		return this.mValues[SensorManager.DATA_Z];
	}

	public void setX(final float pX) {
		this.mValues[SensorManager.DATA_X] = pX;
	}

	public void setY(final float pY) {
		this.mValues[SensorManager.DATA_Y] = pY;
	}

	public void setZ(final float pZ) {
		this.mValues[SensorManager.DATA_Z]  = pZ;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public String toString() {
		return "Accelerometer: " + Arrays.toString(this.mValues);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
