package us.orion.server;

import java.net.InetAddress;

public class ServerClient {
	public String name;
	public InetAddress address;
	public int port;
	private final int ID;
	public int attempt = 0;
	
	public ServerClient(String name, InetAddress address, int port, final int ID) {
		this.ID = ID;
		this.address = address;
		this.port = port;
		this.name = name;
	}
	
	public int getID() {
		return ID;
	}
}
