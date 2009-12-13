package com.flat20.fingerplay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Updater {

	
	public static final boolean update(String currentVersion) {
		//byte[] data = httpGet("thesundancekid.net", "/FingerPlay/version.txt");
		String version = httpGetText("thesundancekid.net", "/FingerPlay/version.txt");
		//System.out.println("[" + version + "]");
		if (version != null) {

			if (!version.equals(currentVersion)) {
				System.out.println("There's a newer version of this server available on http://thesundancekid.net/.\nPlease upgrade to v" + version + ".\n");

			//TODO only download if we have a new version.

				boolean result = httpGetFile("thesundancekid.net", "/FingerPlay/FingerPlayServer.zip", "FingerPlayServer-" + version + ".zip");
				System.out.println("Downloaded FingerPlayServer-" + version + ".zip");
			//unzip("FingerPlayServer-" + version + ".zip");
				return true;
			}
		}
		//System.out.println(args[0]);
			/*
			try {
				//Process proc = Runtime.getRuntime().exec("java -jar FingerPlayServer.jar");
				Process proc = Runtime.getRuntime().exec("javac");
				InputStream stderr = proc.getErrorStream();
				InputStreamReader isr = new InputStreamReader(stderr);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				System.out.println("<ERROR>");
				while ( (line = br.readLine()) != null)
					System.out.println(line);
				System.out.println("</ERROR>");
				int exitVal = proc.waitFor();
				System.out.println("Process exitValue: " + exitVal);
			} catch (IOException e) {
				System.out.println(e.toString());
			} catch (InterruptedException e) {
				System.out.println(e.toString());
			}*/
		return false;
	}

	private static final byte[] httpGet(String host, String filename) {

		byte[] output = null;

		try {
			InetSocketAddress socketAddr = new InetSocketAddress(host, 80);
			Socket socket    = new Socket();
			socket.connect(socketAddr, 2000);
			//OutputStream os   = socket.getOutputStream();
			boolean autoflush = true;
			PrintWriter out   = new PrintWriter( socket.getOutputStream(), autoflush );

			// send an HTTP request to the web server
			out.println("GET " +filename + " HTTP/1.1");
			out.println("Host: " + host + ":80");
			out.println("Connection: Close");
			out.println();


			// read the response

			BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

			byte[] buffer = new byte[1024*16000]; // ~16MB
			int bufferPos = 0;
			int bytesRead = 0;
			do {
				bytesRead = in.read(buffer, bufferPos, buffer.length-bufferPos);
				if (bytesRead >= 0)
					bufferPos += bytesRead;
			} while(bytesRead > -1);

			String header = new String(buffer);
			int headerEnd = header.indexOf("\r\n\r\n")+4;
			//header = header.substring(0, headerEnd-4);
			//System.out.println(header);

			output = new byte[bufferPos-headerEnd];
			for (int i=0; i<bufferPos-headerEnd; i++) {
				output[i] = buffer[i+headerEnd];
			}

			socket.close();

		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + host);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return output;
	}

	private static final String httpGetText(String host, String filename) {
		byte[] data = httpGet(host, filename);
		if (data == null)
			return null;
		else
			return new String(data);
	}

	private static final boolean httpGetFile(String host, String filename, String saveFilename) {
		byte[] data = httpGet(host, filename);
		if (data == null)
			return false;
		try {
			FileOutputStream fos = new FileOutputStream(saveFilename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(data, 0, data.length);
			bos.flush();
			bos.close();
			return true;
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
		return false;
	}

	private static final void unzip(String filename) {
		Enumeration entries;
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(filename);

			entries = zipFile.entries();

			while(entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)entries.nextElement();

				if(entry.isDirectory()) {
					// Assume directories are stored parents first then children.
					System.err.println("Extracting directory: " + entry.getName());
					// This is not robust, just for demonstration purposes.
					(new File(entry.getName())).mkdir();
					continue;
				}

				System.err.println("Extracting file: " + entry.getName());
				copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(entry.getName())));
			}

			zipFile.close();
		} catch (IOException ioe) {
			System.err.println("Unhandled exception:");
			ioe.printStackTrace();
			return;
		}
	}

	public static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}



}

