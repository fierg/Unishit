package de.unitrier.st.PII.oueb03.group2.aufgabe1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Klasse, die den äußeren JFrame implementiert, auf dem z.B. die Buttons zum Steuern des Spiel platziert werden können.
 * Kann sich mit Server verbinden, um Spielfield zu synchronisieren.
 */
public class MinenFegerClient extends JFrame implements Runnable {
    private Field field; // Das Spielfeld mit den einzelnen Positionen
    private int clientId;
    private boolean connected;
    // GUI
    private JTextField ipTextField;
    private JButton connectButton;
    private JButton resetButton;
    // Networking
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public MinenFegerClient() {
        clientId = -1;
        connected = false;

        setTitle("MinenFegerClient");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));

        Container cp = getContentPane();
        setLayout(new BorderLayout());

        // Spielfeld
        field = new Field();
        field.setPositionMouseAdapter(new PositionMouseAdapter());
        cp.add(field, BorderLayout.CENTER);

        // Header panel
        JPanel headerPanel = new JPanel();
        // IP address label
        JLabel ipLabel = new JLabel("IP:");
        ipLabel.setPreferredSize(new Dimension(20, 30));
        headerPanel.add(ipLabel);
        // IP address text field
        ipTextField = new JTextField("localhost");
        ipTextField.setPreferredSize(new Dimension(150, 30));
        headerPanel.add(ipTextField);
        // Connect button
        connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(120, 30));
        connectButton.addActionListener(new ConnectButtonListener());
        headerPanel.add(connectButton);
        cp.add(headerPanel, BorderLayout.NORTH);

        // Footer panel
        JPanel footerPanel = new JPanel();
        // ResetMessage button
        resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(120, 30));
        resetButton.addActionListener(new RestButtonListener());
        footerPanel.add(resetButton);
        cp.add(footerPanel, BorderLayout.SOUTH);

        pack(); // Größe der Komponenten an "preferredSize" anpassen (siehe oben)
        setLocationRelativeTo(null); // Fenster auf Bildschirm zentrieren
        setVisible(true); // MinenFeger anzeigen
    }

    @Override
    public void run() {
        log("Running...");

        while (!Thread.currentThread().isInterrupted()) {
            if (connected) {
                // Positions-Updates des Servers empfangen und anwenden

                // Auf Eingaben vom Server warten.
                Object obj = null;
                try {
                    obj = in.readObject();
                } catch (SocketException e) {
                    disconnect();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                // Wenn das empfangene Objekt ein PositionUpdate-Objekt ist, wende dieses Update an.
                if (obj instanceof PositionUpdate) {
                    log("Received position update from server...");
                    PositionUpdate update = (PositionUpdate)obj;
                    // Update nicht anwenden, falls es von diesem Client stammt.
                    if (!(update.clientId == clientId)) {
                        updatePosition(update);
                    }
                } else  if(obj instanceof ResetMessage){

                // TODO: Onlineübung 3 Aufgabe 2 (Empfang der ResetMessage, Durchführung des Resets und IllegalArgumentException implementieren)

                    log("recieved reset");
                    ResetMessage reset = (ResetMessage)obj;
                    localReset(reset.field);
                } else {
                    throw new IllegalArgumentException("Empfangenes Objekt weder PositionUpdate noch ResetMessage");
                }

            } else {
                try {
                    synchronized (this) {
                        wait(); // Warten, bis Verbindung aufgebaut ist.
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }

        disconnect();
    }

    private void updatePosition(PositionUpdate update) {
        field.updatePosition(update);

        if (field.isVictory() || field.isDefeat()) {
            showDialog();
        }
    }

    private void showDialog() {
        Object[] options = {"Continue", "End"};
        String msg;
        if (field.isVictory()) {
            msg = "Congratulations! You win :-)";
        } else {
            msg = "Boooom! You loose :-(";
        }
        Object selectedOption = JOptionPane.showOptionDialog(this, msg, "The End",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
        if (selectedOption.equals(1)) {
            disconnect();
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    private void connect() {
        // Verbindung zu Server herstellen und Spielfeld empfangen

        log("Connecting to server...");
        String host = ipTextField.getText();

        try {
            socket = new Socket(host, MinenFegerServer.port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Auf Eingaben vom Server warten.
            Object obj = in.readObject();

            // Wenn das empfangene Object ein Integer ist, verwende dieses als clientId,
            // ansonsten unterbreche die Verbindung.
            if (obj instanceof Integer) {
                log("Received ID, connected to server...");
                clientId = (int)obj;
            } else {
                disconnect();
            }

            // Auf Eingaben vom Server warten.
            obj = in.readObject();

            // Wenn das empfangene Objekt ein Position[][]-Array, verwende dieses,
            // ansonsten unterbreche die Verbindung.
            if (obj instanceof Position[][]) {
                log("Received field from server..");
                setField((Position[][])obj);
            } else {
                disconnect();
            }

            connected = true;
            connectButton.setText("Disconnect");
            synchronized (this) {
                notify(); // Thread aufwecken, sobald Verbindung aufgebaut ist.
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        if (connected) {
            log("Disconnecting from server...");
            try {
                socket.close();
                in.close();
                out.close();
                connected = false;
                connectButton.setText("Connect");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setField(Position[][] serverField) {
        field.importPositionArray(serverField);
        field.setPositionMouseAdapter(new PositionMouseAdapter());
        pack();
    }

    private void log(String message) {
        if (clientId == -1) {
            System.out.println("Client: " + message);
        } else {
            System.out.println("Client " + clientId + ": " + message);
        }

    }

    private void reset() {
        if (connected) {
            distributedReset(); // verteiltes zurücksetzen
        } else {
            localReset(); // lokales Feld zurücksetzen
        }
    }

    private void localReset() {
        // lokales Feld zurücksetzen
        this.field.init();
        this.field.setPositionMouseAdapter(new PositionMouseAdapter());
        pack();
    }

    private void localReset(Position[][] field) {
        // lokales Feld zurücksetzen
        this.field.init(field);
        this.field.setPositionMouseAdapter(new PositionMouseAdapter());
        pack();
    }

    private void distributedReset() {
        // verteiltes zurücksetzen
        try {
            out.writeObject(new ResetMessage(clientId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ConnectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (connected) {
                // Verbindung zu Server unterbrechen
                disconnect();
            } else {
                // Verbindung zu Server aufbauen
                connect();
            }
        }
    }

    private class RestButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            reset();
        }
    }

    // MouseAdapter, der beim Klicken auf das Spielfeld die für eine Aktualisierung des Spielfeldes benötigten Daten
    // aus dem MouseEvent ausliest.
    private class PositionMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent event) {
            Position target = (Position) event.getSource();
            PositionUpdate update = new PositionUpdate(clientId, target.getColumnIndex(), target.getRowIndex(),
                    event.isMetaDown());
            // Update an die anderen Clients propagieren.
            if (connected) {
                try {
                    log("Propagating position update to server...");
                    out.writeObject(update);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Update lokal anwenden.
            updatePosition(update);

            repaint();
        }
    }

    public static void main(String[] args) {
        new Thread(new MinenFegerClient()).start();
    }
}

