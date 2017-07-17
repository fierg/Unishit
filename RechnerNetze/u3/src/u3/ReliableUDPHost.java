package u3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReliableUDPHost {

	private static final boolean DEBUG = true;
	private static final int WINDOW_SIZE = 5;
	private static final int TIMEOUT_MIL = 5000;
	public static final int PADDING_SIZE = 128;
	
	private static int measureSize;
	private static int messageID = 0;
	private static BlockingList<Integer> messagesSend = new BlockingList<>(WINDOW_SIZE);
	private static Map<Integer, Future> timers = new HashMap<Integer, Future>();
	private static ExecutorService executorService = Executors.newFixedThreadPool(WINDOW_SIZE);

	public static void main(String[] args) throws IOException {
		if (args.length < 3)
			Usage("measure UDP-Package from <my_port> to <peer_host> <peer_port>");
		switch (args[0].toLowerCase()) {
		case "measure":
			if (args.length < 5)
				Usage("Arguments: measure <my_port> <peer_host> <peer_port> <measureSize>");
			if (args.length == 5) {
				System.out.println("Start single message measure!");
				measureSize = Integer.parseInt(args[4]);
				receiveNodeMeasureSingle(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
			} else if (args[5].equals("-w")) {
				measureSize = Integer.parseInt(args[4]);
				receiveNodeMeasureWindowOfMessages(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
			} else {
				Usage("Arguments: measure <my_port> <peer_host> <peer_port> <measureSize> -w(optional) for sending multiple messages at once");
			}
			break;
		case "console":
			if (args.length != 3)
				Usage("Arguemnts: console <host> <port>");
			Console(args[1], Integer.parseInt(args[2]));
			break;
		default:
			System.err.println("First argument must be measure or console");

		}
	}

	public static void receiveNodeMeasureWindowOfMessages(int my_port, String peer_host, int peer_port)
			throws IOException {
		System.out.printf("send messages from port <%d> to host <%s,%d>\n", my_port, peer_host, peer_port);
		DatagramSocket my_socket = new DatagramSocket(my_port);
		InetSocketAddress peer_address = new InetSocketAddress(peer_host, peer_port);
		boolean keep_on_running = true;

		System.out.println("Start measure");
		long start = System.currentTimeMillis();

		sendTimedMessage(my_socket, peer_address, executorService, String.valueOf(0));

		if (DEBUG)
			System.out.println("Sending initial message");

		while (keep_on_running && messageID < measureSize) {

			String message = Receive(my_socket);
			if (DEBUG)
				System.out.printf("Received:\t <%s> \t\n", message, messageID);
			switch (message.toLowerCase()) {
			case "stop":
				keep_on_running = false;
				message = "stop";
				break;
			case "start":
				message = String.valueOf(messageID);
				sendTimedMessage(my_socket, peer_address, executorService, message);
				break;
			default:
				if (message.startsWith("rak=")) {
					int id = Integer.parseInt(message.split("=")[1]);
					if (messagesSend.contains(id)) {
						timers.get(messageID).cancel(true);
						timers.remove(messageID);
						messagesSend.removeItem(messageID);

						message = String.valueOf(++messageID);
						sendTimedMessage(my_socket, peer_address, executorService, message);
					} else {
						System.err.println("rak not in list!");
					}
				} else {
					System.err.println(message + " bad format!");
				}
			}

		}
		long duration = System.currentTimeMillis() - start;
		Send(my_socket, peer_address, "stop");
		my_socket.close();

		System.out.println("\n" + messageID + " in " + String.valueOf(duration) + " milliseconds");
		double bitsTransmitted = (Math.log10(measureSize) * Integer.SIZE);

		System.out.println("transfer rate: " + bitsTransmitted / duration + " kb/s");
		System.exit(0);

	}

	private static void sendTimedMessage(DatagramSocket my_socket, InetSocketAddress peer_address,
			ExecutorService executorService, String message) throws IOException {
		try {
			messagesSend.put(messageID);
			Send(my_socket, peer_address, message);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}

		Thread timer = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(TIMEOUT_MIL);
					sendTimedMessage(my_socket, peer_address, executorService, message);
				} catch (InterruptedException e1) {
					if (DEBUG)
						System.out.println("timer " + message + " canceld!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		Future<?> future = executorService.submit(timer);

		timers.put(messageID, future);
	}

	public static void receiveNodeMeasureSingle(int my_port, String peer_host, int peer_port) throws IOException {
		System.out.printf("send messages from port <%d> to host <%s,%d>\n", my_port, peer_host, peer_port);
		DatagramSocket my_socket = new DatagramSocket(my_port);
		InetSocketAddress peer_address = new InetSocketAddress(peer_host, peer_port);
		boolean keep_on_running = true;

		Send(my_socket, peer_address, String.valueOf(messageID));
		if (DEBUG)
			System.out.println("Send initial message");

		System.out.println("Start measure");
		long start = System.currentTimeMillis();

		while (keep_on_running && messageID < measureSize) {
			String message = Receive(my_socket);
			if (DEBUG)
				System.out.printf("Received:\t <%s> \t\n", message, messageID);

			if (message.split("=")[1].equals(String.valueOf(messageID))) {
				message = String.valueOf(++messageID);
			} else {
				System.err.println(message + "bad format");
			}

			Send(my_socket, peer_address, message);
		}

		long duration = System.currentTimeMillis() - start;
		Send(my_socket, peer_address, "stop");
		my_socket.close();

		System.out.println("\n" + messageID + " in " + String.valueOf(duration) + " milliseconds");
		double bitsTransmitted = (Math.log10(measureSize) * Integer.SIZE);

		System.out.println("transfer rate: " + bitsTransmitted / duration + " kb/s");

	}

	public static String Receive(DatagramSocket socket) throws IOException {
		final int buffer_size = 1024;
		byte[] buffer = new byte[buffer_size];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		byte[] data = packet.getData();
		data = Arrays.copyOfRange(data, 0, (data.length-PADDING_SIZE-1));
		String message = new String(data, 0, data.length, "UTF-8");
		return message;
	}

	public static void Send(DatagramSocket socket, InetSocketAddress receiver, String message) throws IOException {
		byte[] buffer = concatenate((message).getBytes("UTF-8"),new byte[PADDING_SIZE]);
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
	public static byte[] concatenate (byte[] bs, byte[] bs2) {
	    int aLen = bs.length;
	    int bLen = bs2.length;

	    byte[] c = new byte[aLen + bLen];
	    System.arraycopy(bs, 0, c, 0, aLen);
	    System.arraycopy(bs2, 0, c, aLen, bLen);

	    return c;
	}

}
