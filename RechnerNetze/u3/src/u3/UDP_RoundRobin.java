package u3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import org.asysob.Statistics;

public class UDP_RoundRobin {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) Usage("\"join <my_port> <peer_host> <peer_port>\" or \"start|stop|console <host> <port>\"");
        switch (args[0].toLowerCase()) {
            case "join":
                if (args.length != 4) Usage("Arguments: join <my_port> <peer_host> <peer_port>");
                Node(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
                break;
            case "start":
            case "stop":
                if (args.length != 3) Usage("Arguemnts: start|stop <host> <port>");
                Control(args[0].toLowerCase(), args[1], Integer.parseInt(args[2]));
                break;
            case "console":
                if (args.length != 3) Usage("Arguemnts: console <host> <port>");
                String host = args[1];
                int port = Integer.parseInt(args[2]);
                Console(args[1], Integer.parseInt(args[2]));
                break;
            default:
                System.err.println("First argument must be join, start or stop");
        }
    }

    private static final boolean debug = false;

    public static void Node(int my_port, String peer_host, int peer_port) throws IOException {
        Statistics stats = new Statistics(5);
        System.out.printf("Peer forwarding messages from port <%d> to <%s,%d>\n", my_port, peer_host, peer_port);
        DatagramSocket my_socket = new DatagramSocket(my_port);
        InetSocketAddress peer_address = new InetSocketAddress(peer_host, peer_port);
        boolean keep_on_running = true;
        while (keep_on_running) {
            stats.StartMeasure();
            String message = Receive(my_socket);
            stats.StopMeasure();
            if (debug) System.out.printf("Received <%s> after %f ms waiting\n", message, stats.Duration());
            if (stats.NumberOfMeasures() % 100000 == 0)
                System.out.printf(".");
            switch (message.toLowerCase()) {
                case "stop":
                    keep_on_running = false;
                    break;
                case "start":
                    message = "token";
                    break;
                default:
                    // todo adding some information to the message
            }
            Send(my_socket, peer_address, message);
        }
        my_socket.close();
        System.out.printf("\nStatistics: %s\n",stats.Report());
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

    private static void Console ( String host, int port ) throws IOException {
        BufferedReader cli=new BufferedReader(new InputStreamReader(System.in));
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
                    Control(token[0].toLowerCase(),host,port);
                    break;
                default:
                    System.out.printf("Unknown command <%s>\n",token[0]);
            }
        }
    }

    private static void Usage(String s) {
        System.err.println(s);
        System.exit(-1);
    }
}
