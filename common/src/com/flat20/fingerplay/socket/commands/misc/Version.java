package com.flat20.fingerplay.socket.commands.misc;

import com.flat20.fingerplay.socket.commands.SocketCommand;
import com.flat20.fingerplay.socket.commands.SocketStringCommand;

public class Version extends SocketStringCommand {

	public Version() {
		super(SocketCommand.COMMAND_VERSION);
	}
 
	public Version(String version) {
		super(SocketCommand.COMMAND_VERSION, version);
	}

}
