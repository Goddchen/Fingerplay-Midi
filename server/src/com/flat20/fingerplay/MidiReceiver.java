package com.flat20.fingerplay;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class MidiReceiver implements Receiver {
	private static final String[] SYSTEM_MESSAGE_TEXT = {
		"System Exclusive (should not be in ShortMessage!)",
		"MTC Quarter Frame: ",
		"Song Position: ",
		"Song Select: ",
		"Undefined",
		"Undefined",
		"Tune Request",
		"End of SysEx (should not be in ShortMessage!)",
		"Timing clock",
		"Undefined",
		"Start",
		"Continue",
		"Stop",
		"Undefined",
		"Active Sensing",
		"System Reset"
	};

	private static final String[] QUARTER_FRAME_MESSAGE_TEXT = {
		"frame count LS: ",
		"frame count MS: ",
		"seconds count LS: ",
		"seconds count MS: ",
		"minutes count LS: ",
		"minutes count MS: ",
		"hours count LS: ",
		"hours count MS: "
	};

	private static final String[] FRAME_TYPE_TEXT = {
		"24 frames/second",
		"25 frames/second",
		"30 frames/second (drop)",
		"30 frames/second (non-drop)",
	};

	private IMidiListener mListener;

	public MidiReceiver(IMidiListener listener) {
		mListener = listener;
	}

	public void close() {
	}

	public void send(MidiMessage message, long timestamp) {
		System.out.println(timestamp + " " );
	}

	public void decodeMessage(ShortMessage message) {
		switch (message.getCommand()) {
			case 0x80:
				// note off note getData1 ,vel = getData2
				mListener.onNoteOff(message.getChannel(), message.getData1(), message.getData2());
				//strMessage = "note Off " + message.getData1() + " velocity: " + message.getData2();
				break;

			case 0x90:
				mListener.onNoteOn(message.getChannel(), message.getData1(), message.getData2());
				//strMessage = "note On " + message.getData1() + " velocity: " + message.getData2();
				break;

			case 0xa0:
				//strMessage = "polyphonic key pressure " + message.getData1() + " pressure: " + message.getData2();
				break;

			case 0xb0:
				// 0xb0 -> 0xbF ?????
				// control1 = 0xB0 - message.getCommand()
				mListener.onControlChange(message.getChannel(), message.getData1(), message.getData2());
				//strMessage = "control change " + message.getData1() + " value: " + message.getData2();
				break;

			case 0xF0:
				System.out.println("MidiReceiver " + SYSTEM_MESSAGE_TEXT[message.getChannel()]);
				if ( message.getChannel() == 8 ) {
					System.out.println("..." + message.getChannel());
				}

				//if (message.getChannel() == 8) {
					//System.out.println("MidiReceiver " + )
				//}
				//strMessage = SYSTEM_MESSAGE_TEXT[message.getChannel()];
				/*
				switch (message.getChannel()) {
				
				}*/
				/*
				case 0x1:
					int	nQType = (message.getData1() & 0x70) >> 4;
					int	nQData = message.getData1() & 0x0F;
					if (nQType == 7)
					{
						nQData = nQData & 0x1;
					}
					strMessage += QUARTER_FRAME_MESSAGE_TEXT[nQType] + nQData;
					if (nQType == 7)
					{
						int	nFrameType = (message.getData1() & 0x06) >> 1;
						strMessage += ", frame type: " + FRAME_TYPE_TEXT[nFrameType];
					}
					break;

				case 0x2:
					strMessage += get14bitValue(message.getData1(), message.getData2());
					break;

				case 0x3:
					strMessage += message.getData1();
					break;
				}*/
				break;


			default:
				break;
		}
	}
/*
	public String decodeMessage(ShortMessage message) {
		String	strMessage = null;
		switch (message.getCommand()) {
		case 0x80:
			strMessage = "note Off " + message.getData1() + " velocity: " + message.getData2();
			break;

		case 0x90:
			strMessage = "note On " + message.getData1() + " velocity: " + message.getData2();
			break;

		case 0xa0:
			strMessage = "polyphonic key pressure " + message.getData1() + " pressure: " + message.getData2();
			break;

		case 0xb0:
			strMessage = "control change " + message.getData1() + " value: " + message.getData2();
			break;

		case 0xc0:
			strMessage = "program change " + message.getData1();
			break;

		case 0xd0:
			strMessage = "key pressure " + message.getData1() + " pressure: " + message.getData2();
			break;

		case 0xe0:
			//strMessage = "pitch wheel change " + get14bitValue(message.getData1(), message.getData2());
			break;

		case 0xF0:
			strMessage = SYSTEM_MESSAGE_TEXT[message.getChannel()];
			switch (message.getChannel())
			{
			case 0x1:
				int	nQType = (message.getData1() & 0x70) >> 4;
				int	nQData = message.getData1() & 0x0F;
				if (nQType == 7)
				{
					nQData = nQData & 0x1;
				}
				strMessage += QUARTER_FRAME_MESSAGE_TEXT[nQType] + nQData;
				if (nQType == 7)
				{
					int	nFrameType = (message.getData1() & 0x06) >> 1;
					strMessage += ", frame type: " + FRAME_TYPE_TEXT[nFrameType];
				}
				break;

			case 0x2:
				//strMessage += get14bitValue(message.getData1(), message.getData2());
				break;

			case 0x3:
				strMessage += message.getData1();
				break;
			}
			break;

		default:
			strMessage = "unknown message: status = " + message.getStatus() + ", byte1 = " + message.getData1() + ", byte2 = " + message.getData2();
			break;
		}
		if (message.getCommand() != 0xF0)
		{
			int	nChannel = message.getChannel() + 1;
			String	strChannel = "channel " + nChannel + ": ";
			strMessage = strChannel + strMessage;
		}
		return strMessage;//"["+getHexString(message)+"] "+strMessage;
	}
*/
	public interface IMidiListener {
		public void onNoteOn(int channel, int key, int velocity);
		public void onNoteOff(int channel, int key, int velocity);
		public void onControlChange(int channel, int key, int velocity);
	}

}
