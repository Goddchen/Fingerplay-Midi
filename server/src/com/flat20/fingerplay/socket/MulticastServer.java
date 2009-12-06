package com.flat20.fingerplay.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastServer implements Runnable {

	private String multicastIP;
	private int multicastPort;
	private byte[] outputMessageBuffer;

	public MulticastServer(String multicastIP, int multicastPort, String outputMessage) throws Exception {
		this.multicastIP = multicastIP;
		this.multicastPort = multicastPort;
		//System.out.println("Broadcasting " + outputMessage + " on " + multicastIP + ":" + multicastPort);
		//outputMessage += "\0";
		//outputMessageBuffer = new byte[outputMessage.length()+2];
		outputMessageBuffer = outputMessage.getBytes();
		//outputMessageBuffer[outputMessage.length()-1] = 0;
	}

	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(this.multicastIP);
			while (true) {
				DatagramPacket packet = new DatagramPacket(outputMessageBuffer, outputMessageBuffer.length, address, this.multicastPort);
				socket.send(packet);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			System.out.println(e.toString());
		}
		System.out.println("MulticastServerThread exit.");
	}
}
