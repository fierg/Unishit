package de.unitrier.st.PII.oueb03.group2.aufgabe1;

import javax.swing.*;
import java.io.Serializable;

/**
 * Klasse, die eine einzelne Position im Spielfeld von MinenFeger repräsentiert.
 */
public class Position extends JButton implements Serializable {
    private static final long serialVersionUID = 1L;

    private int columnIndex;
    private int rowIndex;
    private boolean revealed; // Wurde diese Position bereits aufgedeckt?
    private boolean marked; // Wurde diese Position bereits markiert?
    private boolean mine; // Befindet sich an dieser Position eine Mine?
    private int neighboringMinesCount; // Anzahl Minen in der direkten Umgebung

    Position(int columnIndex, int rowIndex, boolean isMine) {
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
        revealed = false;
        marked = false;
        mine = isMine;
        setEnabled(true);
        setText("??");
    }

    Position(int columnIndex, int rowIndex, double mineProbability) {
        this(columnIndex, rowIndex, (Math.random() < mineProbability));
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public boolean isMine() {
        return mine;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        setRevealed(revealed, true);
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;

        if (marked) {
            setText("!!");
        } else {
            setText("??");
        }
    }

    public int getNeighboringMinesCount() {
        return neighboringMinesCount;
    }

    public void setNeighboringMinesCount(int neighboringMinesCount) {
        this.neighboringMinesCount = neighboringMinesCount;
    }

    public void setRevealed(boolean revealed, boolean disable) {
        this.revealed = revealed;
        if (disable) {
            setEnabled(false);
        }
        if (mine) {
            setText("M");
        } else {
            setText(Integer.toString(getNeighboringMinesCount()));
        }
    }
}
