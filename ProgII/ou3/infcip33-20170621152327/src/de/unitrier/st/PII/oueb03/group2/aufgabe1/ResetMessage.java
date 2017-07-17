package de.unitrier.st.PII.oueb03.group2.aufgabe1;

import java.io.Serializable;

/**
 * Diese Klasse wird als Nachricht beim verteilten Zurücksetzen des Spielfelds versendet.
 */
public class ResetMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public final int clientId;
    public Position[][] field;

    public ResetMessage(int clientId, Position[][] field) {
        this.clientId = clientId;
        this.field = field;
    }

    public ResetMessage(int clientId) {
        this(clientId, null);
    }
}