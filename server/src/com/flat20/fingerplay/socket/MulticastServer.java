package com.flat20.fingerplay.socket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

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
			// SocketException: No buffer space available (maximum connections reached?): Datagram send failed
			// so timeout?
			InetAddress group = InetAddress.getByName(this.multicastIP);
			MulticastSocket socket = new MulticastSocket(this.multicastPort);
			socket.joinGroup(group);
			
			//DatagramSocket socket = new DatagramSocket();
			while (true) {
				DatagramPacket packet = new DatagramPacket(outputMessageBuffer, outputMessageBuffer.length, group, this.multicastPort);
				socket.send(packet);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		System.out.println("MulticastServerThread exit.");
	}
}
