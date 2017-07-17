package de.unitrier.st.PII.oueb03.group2.aufgabe1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Der Server verwaltet ein Spielfeld und synchronisert es mit den angemeldeten Clients.
 */
public class MinenFegerServer implements Runnable {
    public static final int port = 6666;

    private ServerSocket socket;
    private LinkedList<MinenFegerServerClientThread> clientThreads;
    private Field field;

    public MinenFegerServer() {
        clientThreads = new LinkedList<>();
        field = new Field();
    }

    public void log(String message) {
        System.out.println("Server: " + message);
    }

    @Override
    public void run() {
        log("Running...");

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            // Verbindungen über Socket annehmen und Client-Threads erzeugen
            try {
                Socket clientSocket = socket.accept();
                log("Accepted client connection...");
                MinenFegerServerClientThread clientThread = new MinenFegerServerClientThread(clientSocket, this);
                new Thread(clientThread).start();
                clientThreads.add(clientThread);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateField(PositionUpdate update) {
        log("Propagating position update to client threads...");
        for (MinenFegerServerClientThread clientThread : clientThreads) {
            // Update nicht zurück an Absender propagieren
            if (!(update.clientId == clientThread.getClientId())) {
                clientThread.updateClient(update);
            }
        }
        log("Applying update to server field...");
        field.updatePosition(update);
        if (field.isVictory() || field.isDefeat()) {
            log("Game over...");
            field = new Field();
        }
    }

    public void sendResetMessage(ResetMessage resetMessage) {
        // generating new field and appending position array to reset message
        log("Generating new server field...");
        field = new Field();
        resetMessage.field = field.getPositionArray();

        // TODO: Onlineübung 3 Aufgabe 2 (ResetMessage an ClientThreads weitergeben)
        for (MinenFegerServerClientThread clientThread: clientThreads) {
            clientThread.sendResetMessage(resetMessage);
        }
    }

    public void disconnectClient(MinenFegerServerClientThread clientThread) {
        clientThreads.remove(clientThread);
    }

    synchronized  public Field getField() {
        return field;
    }

    public static void main(String[] args) {
        new Thread(new MinenFegerServer()).start();
    }
}
