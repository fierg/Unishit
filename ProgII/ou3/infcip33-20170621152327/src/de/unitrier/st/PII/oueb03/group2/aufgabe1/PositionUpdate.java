package de.unitrier.st.PII.oueb03.group2.aufgabe1;

import java.io.Serializable;

/**
 * In dieser Klasse werden die zum Aktualisieren einer Position des Spielfelds benötigten Daten gespeichert.
 */
public class PositionUpdate implements Serializable {
    private static final long serialVersionUID = 1L;

    public final int clientId;
    public final int columnIndex;
    public final int rowIndex;
    public final boolean rightClick;

    public PositionUpdate(int clientId, int columnIndex, int rowIndex, boolean rightClick) {
        this.clientId = clientId;
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
        this.rightClick = rightClick;
    }
}