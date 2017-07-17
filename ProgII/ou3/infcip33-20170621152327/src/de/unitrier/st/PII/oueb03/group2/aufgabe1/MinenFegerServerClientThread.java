package de.unitrier.st.PII.oueb03.group2.aufgabe1;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Der Server kommuniziert mit jedem Client über einen dedizierten ClientThread.
 */
public class MinenFegerServerClientThread implements Runnable {
    private static int counter = 0;

    private boolean connected;
    private int clientId;
    private Socket socket;
    private MinenFegerServer server;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public MinenFegerServerClientThread(Socket socket, MinenFegerServer server) {
        this.clientId = getNewClientId();
        this.socket = socket;
        this.server = server;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        connected = true;
    }

    synchronized private static Integer getNewClientId() {
        return ++counter;
    }

    @Override
    public void run() {
        log("Running...");

        try {
            // Zunächst dem Client eine ID zuweisen...
            log("Assigning ID " + clientId + " to client...");
            out.writeObject(clientId);
            // ...und ihm dann das aktuelle Spielfeld senden.
            log("Sending field to client...");
            out.writeObject(server.getField().getPositionArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            // Anschließend auf Eingaben des Clients warten
            Object obj = null;
            try {
                obj = in.readObject();
            } catch (SocketException | EOFException e) {
                disconnect();
                stop();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            // Falls der Client ein PositionUpdate gesendet hat, wende dieses auf dem Server an.
            if (obj instanceof PositionUpdate) {
                log("Received position update from client...");
                log("Asking server to propagate position update...");
                server.updateField((PositionUpdate)obj);
            } else if (obj instanceof ResetMessage) {
                log("Received reset message from client...");
                log("Asking server to propagate reset message...");
                server.sendResetMessage((ResetMessage)obj);
            } else {
                throw new IllegalArgumentException("Empfangenes Objekt weder PositionUpdate noch ResetMessage");
            }

            // TODO: Onlineübung 3 Aufgabe 2 (IllegalArgumentException)
        }

        // Verbindung trennen, bevor der Thread terminiert.
        disconnect();
    }

    public void updateClient(PositionUpdate update) {
        try {
            log("Sending position update to client...");
            out.writeObject(update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendResetMessage(ResetMessage resetMessage) {
        // TODO: Onlineübung 3 Aufgabe 2 (ResetMessage an Client senden)
        try{
            log("Sending reset to other clients");
            out.writeObject(resetMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        Thread.currentThread().interrupt();
    }

    private void disconnect() {
        if (connected) {
            log("Disconnecting...");
            try {
                in.close();
                out.close();
                socket.close();
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.disconnectClient(this);
        }
    }

    public int getClientId() {
        return clientId;
    }

    private void log(String message) {
        System.out.println("ClientThread " + clientId + ": " + message);
    }
}
