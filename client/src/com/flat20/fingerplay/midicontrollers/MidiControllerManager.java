package com.flat20.fingerplay.midicontrollers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.util.Set;

import android.hardware.Sensor;
import android.util.Log;

import com.flat20.fingerplay.network.ConnectionManager;
import com.flat20.fingerplay.socket.commands.midi.MidiControlChange;
import com.flat20.fingerplay.socket.commands.midi.MidiNoteOff;
import com.flat20.fingerplay.socket.commands.midi.MidiNoteOn;
import com.flat20.fingerplay.socket.commands.midi.MidiSocketCommand;
import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.flat20.gui.widgets.IWidget;
import com.flat20.gui.widgets.MidiWidget;
import com.flat20.gui.widgets.SensorSlider;
import com.flat20.gui.widgets.SensorXYPad;
import com.flat20.gui.widgets.WidgetContainer;

/**
 * TODO Should parse the same XML data as the LayoutManager and assign controllerNumber
 * in here rather than relying on LayoutManager for that.
 * 
 * @author andreas
 *
 */
public class MidiControllerManager {

    private LinkedHashMap<IMidiController, Integer> mMidiControllers = new LinkedHashMap<IMidiController, Integer>();
	private int mControllerIndex = 0;

	private ConnectionManager mConnectionManager = ConnectionManager.getInstance();

	// Singleton
	private static MidiControllerManager mInstance = null;
	public static MidiControllerManager getInstance() {
		if (mInstance == null)
			mInstance = new MidiControllerManager();
		return mInstance;
	}

	private MidiControllerManager() {
		mConnectionManager.addConnectionListener(mConnectionListener);
	}

	// Assigns a separate controller number to each IMidiController if LayoutManager hasn't
	// assigned it already.
    public void addMidiController(IMidiController midiController) {
		midiController.setOnControlChangeListener( onControlChangeListener );
/*
		if (midiController.getControllerNumber() == IMidiController.CONTROLLER_NUMBER_UNASSIGNED)
			midiController.setControllerNumber( mControllerIndex );
*/
    	mMidiControllers.put(midiController, Integer.valueOf(mControllerIndex));
    	mControllerIndex += midiController.getParameters().length;
    }

    public Set<IMidiController> getMidiControllers() {
    	Set<IMidiController> mcs = (Set<IMidiController>) mMidiControllers.keySet();
    	return mcs;//(IMidiController[]) mMidiControllers.keySet().toArray();
    }

    public IMidiController getMidiControllerByName(String name) {
    	Set<IMidiController> mcs = (Set<IMidiController>) mMidiControllers.keySet();
    	for (IMidiController mc : mcs) {
    		if (mc.getName().equals(name)) {
    			return mc;
    		}
    	}
    	return null;
    }

    // TODO Make names unique and get the Sensor controllers by class or even better, some integer ID.
    public ArrayList<IMidiController> getAllMidiControllersByName(String name) {
    	ArrayList<IMidiController> found = new ArrayList<IMidiController>();
    	Set<IMidiController> mcs = (Set<IMidiController>) mMidiControllers.keySet();
    	for (IMidiController mc : mcs) {
    		if (mc.getName().equals(name)) {
    			found.add(mc);
    		}
    	}
    	return found;
    }

	public int getIndex(IMidiController midiController) {
		return (int) mMidiControllers.get(midiController);
	}

	// Add all midi controllers inside widgetContainer
	public void addMidiControllersIn(WidgetContainer widgetContainer) {
		IWidget[] widgets = widgetContainer.getWidgets();
        for (int i=0; i<widgets.length; i++) {
        	IWidget w = widgets[i];
        	if (w instanceof MidiWidget) {
				MidiWidget midiWidget = (MidiWidget) w;
				midiWidget.setOnControlChangeListener( onControlChangeListener );
	        	addMidiController( midiWidget.getMidiController() );
        	} else if (w instanceof WidgetContainer) {
				WidgetContainer wc = (WidgetContainer) w;
				addMidiControllersIn(wc);
			}
        }
	}

	private IOnControlChangeListener onControlChangeListener = new IOnControlChangeListener() {

		// Cached to limit garbage collects. 
		final private MidiControlChange mControlChange = new MidiControlChange();
		final private MidiNoteOn mNoteOn = new MidiNoteOn();
		final private MidiNoteOff mNoteOff = new MidiNoteOff();

		@Override
    	public void onControlChange(IMidiController midiController, int channel, int controllerNumber, int value) {
			//System.out.println("onControlChange " + 0xB0 + ", " + channel + ", " + controllerNumber + ", " + value);
			mControlChange.set(0xB0, channel, controllerNumber, value);
			mConnectionManager.send( mControlChange );
    	}

    	@Override
    	public void onNoteOn(IMidiController midiController, int channel, int key, int velocity) {
    		//System.out.println("onNoteOn " + channel + ", " + key + ", " + velocity);
			//int controllerIndex = (int) getIndex(midiController);
			// midi channel, key, velocity
			mNoteOn.set(channel, key, velocity);
			//socketCommand = new MidiNoteOn(0, controllerIndex, velocity);
			mConnectionManager.send( mNoteOn );
    	}

    	@Override
    	public void onNoteOff(IMidiController midiController, int channel, int key, int velocity) {
    		//System.out.println("onNoteOff " + channel + ", " + key + ", " + velocity);
			//int controllerIndex = (int) getIndex(midiController);
			//socketCommand = new MidiNoteOff(0, controllerIndex, velocity);
			mNoteOff.set(channel, key, velocity);
			mConnectionManager.send(mNoteOff);
    	}

    };

    // Handler receiving MIDI commands from the server.
    // TODO Server code isn't sending data yet.  
    private ConnectionManager.IConnectionListener mConnectionListener = new ConnectionManager.IConnectionListener() {

    	public void onConnect() {
    	}

    	public void onDisconnect() {
    	}

    	public void onError(String errorMessage) {
    	}

    	public void onSocketCommand(SocketCommand sm) {
			if (sm.command == SocketCommand.COMMAND_MIDI_SHORT_MESSAGE) {
				Log.i("mcm", "server sent cc message");
				MidiSocketCommand msc = (MidiSocketCommand) sm;
				int ccIndex = msc.data1;
				Log.i("mcm", " msc = " + msc + ", ccIndex: " + ccIndex);
				Log.i("mcm", "channel: " + msc.channel + ", " + msc.command + ", " + msc.data1 + ", " + msc.data2);
			}
    	}
    };


    // Sensor Code

	// onSensorChanged won't be called if the listeners haven't been registered
	// in the Activity, so sensorsEnabled shouldn't be necessary.


	//private boolean sensorsEnabled;
	//private final long SEND_DELAY = 75;	//milliseconds	


/*
    public void stopSensors() {
    	sensorsEnabled = false;
    }

    public void startSensors() {
    	sensorsEnabled = true;
    }
    */
    public void onSensorChanged(Sensor sensor, float[] sensorValues) {
    	
    	//if (!sensorsEnabled)
    		//return;
    	
    	//MidiWidget mw;
    	//float[] val;
    	//float max;
    	
    	ArrayList<IMidiController> controllers;


		//Scale values depending on sensor type:
		switch (sensor.getType())
		{
		case Sensor.TYPE_ACCELEROMETER:	//A constant describing an accelerometer sensor type.
	    	//Log.i("sensor", "accele type = " + sensor.getType() + ", " + Sensor.TYPE_ACCELEROMETER);
/*
			values[0]: Acceleration minus Gx on the x-axis 
			values[1]: Acceleration minus Gy on the y-axis 
			values[2]: Acceleration minus Gz on the z-axis
 */
			controllers = getAllMidiControllersByName("Sensor accelerometer");
			for (IMidiController controller : controllers) {
				// TODO Move onSensorChange and all logic to the controller.
				((SensorXYPad)controller.getView()).onSensorChanged(sensor, sensorValues);
				//((SensorXYPad)controller)onSensorChanged(sensor, sensorValues);
			}
			/*
			spad = (SensorXYPad) getMidiControllerByName("Sensor accelerometer");
			if (spad == null)
			{
				Log.e("SENSOR", "No sensor \"accelerometer\" (type " + sensor.getType() + ") found!");
				return;
			}
			spad.onSensorChanged(sensor, sensorValues);
			*/
			break;

		case Sensor.TYPE_GYROSCOPE:	//A constant describing a gyroscope sensor type
			break;

		case Sensor.TYPE_LIGHT:	//A constant describing a light sensor type.
			controllers = getAllMidiControllersByName("Sensor light");
			for (IMidiController controller : controllers) {
				((SensorSlider)controller.getView()).onSensorChanged(sensor, sensorValues);
			}
/*
			ss = (SensorSlider)getMidiControllerByName("Sensor light");
			if (ss == null)
			{
				Log.e("SENSOR", "No sensor \"light\" (type " + sensor.getType() + ") found!");
				return;
			}
			ss.onSensorChanged(sensor, sensorValues);
*/
			/*
			if (SystemClock.uptimeMillis() > mw.getLastSendTime() + SEND_DELAY)
	    	{
				val = new float[1];
				max = sensor.getMaximumRange();
				//scale to 0..1 range:
				val[0] = (sensorValues[0]+max)/(2*max);
				if (mw instanceof Slider) {
					((Slider)mw).setMeterHeight(val[0]);
				}
				mw.sendControlChange(0, (int)(val[0]*128.0f));
	    	}*/
		break;
		case Sensor.TYPE_MAGNETIC_FIELD:	//A constant describing a magnetic field sensor type.
			controllers = getAllMidiControllersByName("Sensor magfield");
			for (IMidiController controller : controllers) {
				((SensorXYPad)controller.getView()).onSensorChanged(sensor, sensorValues);
			}
/*
			spad = (SensorXYPad) getMidiControllerByName("Sensor magfield");
			if (spad == null)
			{
				Log.e("SENSOR", "No sensor \"magfield\" (type " + sensor.getType() + ") found!");
				return;
			}
			spad.onSensorChanged(sensor, sensorValues);
*/
			/*
			//Stop sensor update flooding
			if (SystemClock.uptimeMillis() > mw.getLastSendTime() + SEND_DELAY)
	    	{
				val = new float[3];
				max = 100.0f;
				//Log.i("SENSOR", "sensor \"magfield\" " + sensor.getName() + " has max range of " + max);
				//scale to 0..1 range:
				val[0] = (sensorValues[0]+max)/(2*max);
				val[1] = (sensorValues[1]+max)/(2*max);
				val[2] = (sensorValues[2]+max)/(2*max);
				if (mw instanceof XYPad) {
					((XYPad)mw).setMeterx((int)(val[2]*128.0f));
					((XYPad)mw).setMetery((int)(val[1]*128.0f));
				}    	
				mw.sendControlChange(0, (int)(val[0]*128.0f));
				mw.sendControlChange(1, (int)(val[1]*128.0f));
				mw.sendControlChange(2, (int)(val[2]*128.0f));
	    	}*/
		break;
		case Sensor.TYPE_ORIENTATION:	//A constant describing an orientation sensor type.
/*			values[0]: Azimuth, angle between the magnetic north direction and the Y axis, around the Z axis (0 to 359). 0=North, 90=East, 180=South, 270=West 
			values[1]: Pitch, rotation around X axis (-180 to 180), with positive values when the z-axis moves toward the y-axis. 
			values[2]: Roll, rotation around Y axis (-90 to 90), with positive values when the x-axis moves away from the z-axis.
*/
			controllers = getAllMidiControllersByName("Sensor orientation");
			for (IMidiController controller : controllers) {
				((SensorXYPad)controller.getView()).onSensorChanged(sensor, sensorValues);
			}
/*
			spad = (SensorXYPad) getMidiControllerByName("Sensor orientation");
			if (spad == null)
			{
				Log.e("SENSOR", "No sensor \"magfield\" (type " + sensor.getType() + ") found!");
				return;
			}
			spad.onSensorChanged(sensor, sensorValues);
*/
/*
			mw = (MidiWidget)getMidiControllerByName("Sensor orientation");
			if (mw == null)
			{
				Log.e("SENSOR", "No sensor \"orientation\" (type " + sensor.getType() + ") found!");
				return;
			}
			//Stop sensor update flooding
			if (SystemClock.uptimeMillis() > mw.getLastSendTime() + SEND_DELAY)
	    	{
				val = new float[3];
				//scale to 0..1 range:
				val[0] = sensorValues[0]/359.0f;
				val[1] = (sensorValues[1]+180.0f)/359.0f;
				val[2] = (sensorValues[2]+90.0f)/180.0f;
				if (mw instanceof XYPad) {
					((XYPad)mw).setMeterx((int)(val[2]*128.0f));
					((XYPad)mw).setMetery((int)(val[1]*128.0f));
				}
				mw.sendControlChange(0, (int)(val[0]*128.0f));
				mw.sendControlChange(1, (int)(val[1]*128.0f));
				mw.sendControlChange(2, (int)(val[2]*128.0f));
	    	}
*/
			break;
		//TODO untested
		case Sensor.TYPE_PRESSURE:	//A constant describing a pressure sensor type
			controllers = getAllMidiControllersByName("Sensor pressure");
			for (IMidiController controller : controllers) {
				((SensorSlider)controller.getView()).onSensorChanged(sensor, sensorValues);
			}
/*
			ss = (SensorSlider)getMidiControllerByName("Sensor pressure");
			if (ss == null)
			{
				Log.e("SENSOR", "No sensor \"light\" (type " + sensor.getType() + ") found!");
				return;
			}
			ss.onSensorChanged(sensor, sensorValues);
*/
			/*
			mw = (MidiWidget)getMidiControllerByName("Sensor pressure");
			if (mw == null)
			{
				Log.e("SENSOR", "No sensor \"pressure\" (type " + sensor.getType() + ") found!");
				return;
			}
			//Stop sensor update flooding
			if (SystemClock.uptimeMillis() > mw.getLastSendTime() + SEND_DELAY)
	    	{
				val = new float[1];
				max = sensor.getMaximumRange();
				//scale to 0..1 range:
				val[0] = (sensorValues[0]+max)/(2*max);
				if (mw instanceof Slider) {
					((Slider)mw).setMeterHeight(val[0]);
				}
				mw.sendControlChange(0, (int)(val[0]*128.0f));
	    	}*/
			break;
		//TODO untested
		case Sensor.TYPE_PROXIMITY:	//A constant describing an proximity sensor type.
			controllers = getAllMidiControllersByName("Sensor proximity");
			for (IMidiController controller : controllers) {
				((SensorSlider)controller.getView()).onSensorChanged(sensor, sensorValues);
			}
			/*
			ss = (SensorSlider)getMidiControllerByName("Sensor proximity");
			if (ss == null)
			{
				Log.e("SENSOR", "No sensor \"light\" (type " + sensor.getType() + ") found!");
				return;
			}
			ss.onSensorChanged(sensor, sensorValues);
			*/
/*
			mw = (MidiWidget)getMidiControllerByName("Sensor proximity");
			if (mw == null)
			{
				Log.e("SENSOR", "No sensor \"proximity\" (type " + sensor.getType() + ") found!");
				return;
			}
	    	//Stop sensor update flooding
			if (SystemClock.uptimeMillis() > mw.getLastSendTime() + SEND_DELAY)
	    	{
				val = new float[1];
				max = sensor.getMaximumRange();
				//scale to 0..1 range:
				val[0] = (sensorValues[0]+max)/(2*max);
				if (mw instanceof Slider) {
					((Slider)mw).setMeterHeight(val[0]);
				}
				mw.sendControlChange(0, (int)(val[0]*128.0f));
	    	}
*/
			break;
		//TODO untested
		case Sensor.TYPE_TEMPERATURE:	//A constant describing a temperature sensor type
			controllers = getAllMidiControllersByName("Sensor temperature");
			for (IMidiController controller : controllers) {
				((SensorSlider)controller.getView()).onSensorChanged(sensor, sensorValues);
			}
/*
			ss = (SensorSlider)getMidiControllerByName("Sensor temperature");
			if (ss == null)
			{
				Log.e("SENSOR", "No sensor \"light\" (type " + sensor.getType() + ") found!");
				return;
			}
			ss.onSensorChanged(sensor, sensorValues);
*/
/*
			mw = (MidiWidget)getMidiControllerByName("Sensor temperature");
			if (mw == null)
			{
				Log.e("SENSOR", "No sensor \"temperature\" (type " + sensor.getType() + ") found!");
				return;
			}
	    	//Stop sensor update flooding
			if (SystemClock.uptimeMillis() > mw.getLastSendTime() + SEND_DELAY)
	    	{
				val = new float[1];
				max = sensor.getMaximumRange();
				//scale to 0..1 range:
				val[0] = (sensorValues[0]+max)/(2*max);
				if (mw instanceof Slider) {
					((Slider)mw).setMeterHeight(val[0]);
				}
				mw.sendControlChange(0, (int)(val[0]*128.0f));		
	    	}
*/	    	
			break;
		}
	}

}