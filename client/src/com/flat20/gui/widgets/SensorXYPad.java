package com.flat20.gui.widgets;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.SystemClock;
import com.flat20.fingerplay.midicontrollers.IMidiController;

// TODO Register sensor listener inside this class?
// Is one listener for each SensorSlider efficient or will it slow down Android?
public class SensorXYPad extends XYPad {

	private final long SEND_DELAY = 75;	//milliseconds	

	// Timestamp for last send.
	private long mLastSendTime = SystemClock.uptimeMillis();
	
	private final float mVal[] = new float[3];

	public SensorXYPad(IMidiController midiController) {
		super(midiController);
	}

	@Override
	public boolean onTouchMove(int touchX, int touchY, float pressure, int pointerId) {
		return true;
	}

	@Override
	public boolean onTouchUp(int touchX, int touchY, float pressure, int pointerId) {
		return true;
	}

	@Override
	public boolean onTouchUpOutside(int touchX, int touchY, float pressure, int pointerId) {
		return true;
	}

	// Consider having one class per Sensor type so we don't need the switch statement. 
	public void onSensorChanged(Sensor sensor, float[] sensorValues) {

		// A bit silly, but don't want to do a new float[] every update
		// so we get less garbage collection.
		final float val[] = mVal;
		switch (sensor.getType()) {

			case Sensor.TYPE_ACCELEROMETER:	//A constant describing a light sensor type.

				
				/*
				values[0]: Acceleration minus Gx on the x-axis 
				values[1]: Acceleration minus Gy on the y-axis 
				values[2]: Acceleration minus Gz on the z-axis
	 */

		    	//Stop sensor update flooding
		    	if (SystemClock.uptimeMillis() > mLastSendTime + SEND_DELAY) {
					//float val = new float[3];
					final float max = sensor.getMaximumRange() * SensorManager.STANDARD_GRAVITY;	//max is reported in g's
					//scale to 0..1 range (from - max..+max):
					val[0] = (sensorValues[0] + max) / (2 * max);
					val[1] = (sensorValues[1] + max) / (2 * max);
					val[2] = (sensorValues[2] + max - SensorManager.STANDARD_GRAVITY) / (2 * max);	//TODO Futureproof me ;)
					
					setMeterX((int)(val[0]*width));
					setMeterY((int)(val[1]*height));

					getMidiController().sendParameter(CC_X, (int)(val[0]*0x7F));
					getMidiController().sendParameter(CC_Y, (int)(val[1]*0x7F));
					getMidiController().sendParameter(CC_TOUCH, (int)(val[2]*0x7F));

					mLastSendTime = SystemClock.uptimeMillis();
		    	}
		    break;

			case Sensor.TYPE_MAGNETIC_FIELD:
				if (SystemClock.uptimeMillis() > mLastSendTime + SEND_DELAY) {
					final float max = 100.0f;
					//Log.i("SENSOR", "sensor \"magfield\" " + sensor.getName() + " has max range of " + max);
					//scale to 0..1 range:
					val[0] = (sensorValues[0]+max)/(2*max);
					val[1] = (sensorValues[1]+max)/(2*max);
					val[2] = (sensorValues[2]+max)/(2*max);
					
					setMeterX((int)(val[1]*width));
					setMeterX((int)(val[2]*height));
					
					getMidiController().sendParameter(CC_TOUCH, (int)(val[0]*0x7F));
					getMidiController().sendParameter(CC_X, (int)(val[1]*0x7F));
					getMidiController().sendParameter(CC_Y, (int)(val[2]*0x7F));

					mLastSendTime = SystemClock.uptimeMillis();
		    	}
				break;

			case Sensor.TYPE_ORIENTATION:	//A constant describing an orientation sensor type.
				/*			values[0]: Azimuth, angle between the magnetic north direction and the Y axis, around the Z axis (0 to 359). 0=North, 90=East, 180=South, 270=West 
							values[1]: Pitch, rotation around X axis (-180 to 180), with positive values when the z-axis moves toward the y-axis. 
							values[2]: Roll, rotation around Y axis (-90 to 90), with positive values when the x-axis moves away from the z-axis.
				*/
				
				//Stop sensor update flooding
				if (SystemClock.uptimeMillis() > mLastSendTime + SEND_DELAY)
		    	{
					//scale to 0..1 range:
					val[0] = sensorValues[0]/359.0f;
					val[1] = (359.0f-(sensorValues[1]+180.0f))/359.0f; // Flip upside down because it looks nicer
					val[2] = (180.0f-(sensorValues[2]+90.0f))/180.0f; // and the user expects low values bottom left.
					
					setMeterX((int)(val[1]*width));
					setMeterY((int)(val[2]*height));

					getMidiController().sendParameter(CC_TOUCH, (int)(val[0]*0x7F));
					getMidiController().sendParameter(CC_X, (int)(val[1]*0x7F));
					getMidiController().sendParameter(CC_Y, (int)(val[2]*0x7F));

					mLastSendTime = SystemClock.uptimeMillis();
		    	}
				break;
		}

	}
	
	private void setMeterX(int newX)
	{
		mMeter.x = Math.max(0, Math.min(width-32, newX-16));
		mMeterOff.x = mMeter.x;
		lastValueX = newX;		
	}

	private void setMeterY(int newY)
	{	
		mMeter.y = Math.max(0, Math.min(height-32, newY-16));
		mMeterOff.y = mMeter.y;
		lastValueY = newY;
	}
	

}
