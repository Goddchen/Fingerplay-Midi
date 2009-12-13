package com.flat20.fingerplay.socket.commands.midi;

/**
 * control1 should be with 0xB0 
 * channel is 0-16
 * @author andreas
 *
 */
public class MidiControlChange extends MidiSocketCommand {

	final public static int CC_COMMAND = 0xB0;

	public MidiControlChange() {
		super();
	}

	//TODO Remove 0xB0 ? server: control1=0xB1 -> client.decode control1=0xB0+0xB1 ??
	public MidiControlChange(int control1, int channel, int control2, int value) {
		//super(0xB0 + control1, channel, control2, value);
		super(control1, channel, control2, value);
		//super((byte)((0xB0 + control1) & 0xFF), (byte) (channel & 0xFF), (byte) (control2 & 0xFF), (byte) (value & 0xFF));
	}

	public void set(int control1, int channel, int control2, int value) {
		//super.set(0xB0 + control1, channel, control2, value);
		super.set(control1, channel, control2, value);
	}

}
