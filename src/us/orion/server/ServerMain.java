package us.orion.server;

public class ServerMain {
	
	private int port;
	private Server server;
	
	public ServerMain(int port) {
		this.port = port;
		server = new Server(port);
	}
	
	public static void main(String[] args) {
		int port = -1;
		if(args.length > 1) {
			System.out.println("Usage: java -jar GreatyChat.jar [port]");
			return;
		} else if(args.length == 0)
			port = 8192;
		if(port != 8192)
			port = Integer.parseInt(args[0]);
		new ServerMain(port);
	}
	
}
