package u3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReliableUDPHost {

	private static final int WINDOW_SIZE = 5;
	private static final int TIMEOUT_MIL = 5000;
	private static final boolean DEBUG = true;
	private static int messageCount;
	private static int messageID = 0;
	private static List<Integer> messagesSend;
	private static Map<Integer, Future> timers;

	public static void main(String[] args) throws IOException {
		if (args.length < 3)
			Usage("send UDP-Package from <my_port> to <peer_host> <peer_port>");
		switch (args[0].toLowerCase()) {
		case "send":
			if (args.length != 5)
				Usage("Arguments: send <my_port> <peer_host> <peer_port>");
			receiveNodeMeasureWindowOfMessages(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
			messageCount = Integer.parseInt(args[4]);
			break;
		case "measure":
			if (args.length < 5)
				Usage("Arguments: measure <my_port> <peer_host> <peer_port> <messageCount>");
			System.out.println("Start single message measure!");
			messageCount = Integer.parseInt(args[4]);
			receiveNodeMeasureSingle(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
			break;
		case "console":
			if (args.length != 3)
				Usage("Arguemnts: console <host> <port>");
			Console(args[1], Integer.parseInt(args[2]));
			break;
		default:
			System.err.println("First argument must be send");

		}
	}

	public static void receiveNodeMeasureWindowOfMessages(int my_port, String peer_host, int peer_port) throws IOException {
		System.out.printf("send messages from port <%d> to host <%s,%d>\n", my_port, peer_host, peer_port);
		DatagramSocket my_socket = new DatagramSocket(my_port);
		InetSocketAddress peer_address = new InetSocketAddress(peer_host, peer_port);
		boolean keep_on_running = true;

		Send(my_socket, peer_address, String.valueOf(messageID));
		if (DEBUG)
			System.out.println("Send initial message");

		ExecutorService executorService = Executors.newFixedThreadPool(WINDOW_SIZE);
		while (keep_on_running) {

			String message = Receive(my_socket);
			if (DEBUG)
				System.out.printf("Received:\t <%s> \t\n", message, messageID);
			switch (message.toLowerCase()) {
			case "stop":
				keep_on_running = false;
				break;
			case "start":
				message = String.valueOf(messageID);
				break;
			default:
				if (message.startsWith("rak=")) {
					if (message.split("=")[1].equals(String.valueOf(messageID))) {
						timers.get(messageID).cancel(true);
						timers.remove(messageID);
						message = String.valueOf(++messageID);
					} else {
						//TODO
					}
				} else {
					System.err.println(message + "bad format");
					System.exit(1);
				}
			}
			Send(my_socket, peer_address, message);
			Thread timer = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(TIMEOUT_MIL);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						Send(my_socket, peer_address, String.valueOf(messageID));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			Future<?> future = executorService.submit(timer);

			timers.put(messageID, future);

		}
		my_socket.close();

	}

	public static void receiveNodeMeasureSingle(int my_port, String peer_host, int peer_port) throws IOException {
		System.out.printf("send messages from port <%d> to host <%s,%d>\n", my_port, peer_host, peer_port);
		DatagramSocket my_socket = new DatagramSocket(my_port);
		InetSocketAddress peer_address = new InetSocketAddress(peer_host, peer_port);
		boolean keep_on_running = true;

		Send(my_socket, peer_address, String.valueOf(messageID));
		if (DEBUG)
			System.out.println("Send initial message");

		long start = System.currentTimeMillis();
		System.out.println("Start measure");

		while (keep_on_running && messageID < messageCount) {
			String message = Receive(my_socket);
			if (DEBUG)
				System.out.printf("Received:\t <%s> \t\n", message, messageID);

			if (message.split("=")[1].equals(String.valueOf(messageID))) {
				message = String.valueOf(++messageID);
			} else {
				System.err.println(message + "bad format");
				System.exit(1);
			}

			Send(my_socket, peer_address, message);
		}

		long duration = System.currentTimeMillis() - start;
		Send(my_socket, peer_address, "stop");
		my_socket.close();

		System.out.println("\n" + messageID + " in " + String.valueOf(duration)  + " milliseconds");
		 double bitsTransmitted = (Math.log10(messageCount)* Integer.SIZE);
		
		System.out.println("transfer rate: " + bitsTransmitted/duration + " kb/s");
		
		}

	public static String Receive(DatagramSocket socket) throws IOException {
		final int buffer_size = 1024;
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

	private static void Control(String command, String host, int port) throws IOException {
		System.out.printf("Sending command <%s> to <%s,%d>\n", command, host, port);
		InetSocketAddress address = new InetSocketAddress(host, port);
		DatagramSocket my_socket = new DatagramSocket();
		Send(my_socket, address, command);
		my_socket.close();
	}

	private static void Console(String host, int port) throws IOException {
		BufferedReader cli = new BufferedReader(new InputStreamReader(System.in));
		boolean keep_on_running = true;
		while (keep_on_running) {
			System.out.print("udprr> ");
			String command = cli.readLine();
			String[] token = command.split("\\s+");
			if (token.length == 0) {
				System.out.println("Seems to be no sensible input");
				continue;
			}
			switch (token[0].toLowerCase()) {
			case "quit":
			case "exit":
				keep_on_running = false;
				break;
			case "start":
			case "stop":
				Control(token[0].toLowerCase(), host, port);
				break;
			default:
				System.out.printf("Unknown command <%s>\n", token[0]);
			}
		}
	}

	private static void Usage(String s) {
		System.err.println(s);
		System.exit(-1);
	}
}
