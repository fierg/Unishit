package u3;

import java.io.IOException;
import java.net.*;

public class ReliableUDPClient {

	private static final boolean debug = false;
	// private static List<Integer> messagesReceived = new
	// LinkedList<Integer>();

	public static void main(String[] args) throws IOException {
		if (args.length < 3)
			Usage("receive UDP-Package at <my_port> from <peer_host> <peer_port>");
		switch (args[0].toLowerCase()) {
		case "receive":
			if (args.length != 4)
				Usage("Arguments: receive <my_port> <peer_host> <peer_port>");
			receiveNode(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
			break;
		default:
			System.err.println("First argument must be receive");
		}
	}

	public static void receiveNode(int my_port, String peer_host, int peer_port) throws IOException {
		System.out.printf("receive messages from port <%d> from host <%s,%d>\n", my_port, peer_host, peer_port);
		DatagramSocket my_socket = new DatagramSocket(my_port);
		InetSocketAddress peer_address = new InetSocketAddress(peer_host, peer_port);
		boolean keep_on_running = true;
		while (keep_on_running) {
			String message = Receive(my_socket);
			if (debug)
				System.out.printf("Received:\t <%s> \t\n", message);
			switch (message.toLowerCase()) {
			case "stop":
				System.out.println("recieved stop message!");
				keep_on_running = false;
				break;
			case "start":
				System.err.println("unable to start as Client");
				break;
			default:
				if (message.matches("-?[0-9]+")) {
					message = "rak=" + message ;
					Send(my_socket, peer_address, message);
					if(debug)
					System.out.println("send "+ message);
					break;
				} else {
					if(debug)
					System.err.println("message fail! received: " + message);
				}
			}
		}
		my_socket.close();
	}

	public static String Receive(DatagramSocket socket) throws IOException {
		final int buffer_size = 2048;
		byte[] buffer = new byte[buffer_size];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		String message = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
		return message;
	}

	public static void Send(DatagramSocket socket, InetSocketAddress receiver, String message) throws IOException {
		byte[] buffer = message.getBytes("UTF-8");
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiver);
		socket.send(packet);
	}



	private static void Usage(String s) {
		System.err.println(s);
		System.exit(-1);
	}
}
