package de.unitrier.st.PII.oueb03.group2.aufgabe1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.Serializable;

/**
 * Klasse, die das gesamte Spielfeld von MinenFeger (bestehend aus Objekten der Klasse Position) repräsentiert.
 */
public class Field extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int defaultWidth = 15;
    private static final int defaultHeight = 10;
    private static double defaultMineProbability = 0.05;

    private int fieldWidth;
    private int fieldHeight;
    private double mineProbability; // Wahrscheinlichkeit, dass an einer Position eine Mine platziert wird.
    private Position[][] field;
    private boolean victory; // Hat der Spieler mit dem letzten Zug (setPosition) gewonnen?
    private boolean defeat; // Hat der Spieler mit dem letzten Zug (setPosition) verloren?

    public Field(int fieldWidth, int fieldHeight, double mineProbability) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.field = new Position[fieldWidth][fieldHeight];
        this.mineProbability = mineProbability;

        setLayout(new GridLayout(fieldHeight, fieldWidth));
        init();
    }

    public Field() {
        this(defaultWidth, defaultHeight, defaultMineProbability);
    }

    public Field(double mineProbability) {
        this(defaultWidth, defaultHeight, mineProbability);
    }

    /**
     * Initialisiert das Spielfeld mit zufälligen Feldern.
     */
    public void init() {
        victory = false;
        defeat = false;

        Position[][] newField = new Position[fieldWidth][fieldHeight];
        for (int columnIndex = 0; columnIndex < fieldWidth; columnIndex++) {
            for (int rowIndex = 0; rowIndex < fieldHeight; rowIndex++) {
                newField[columnIndex][rowIndex] = new Position(columnIndex, rowIndex, mineProbability);
            }
        }

        init(newField);
    }

    /**
     * Initialisert das Spielfeld mit vorgegebenen Feldern.
     * @param field Array mit Positions-Buttons, die eingefügt werden sollen.
     */
    public void init(Position[][] field) {
        // Breite des übergebenen Feldes validieren
        if (field.length != this.fieldWidth) {
            throw new IllegalArgumentException("Field width should be " + this.fieldWidth
                    + ", but was " + field.length + ".");
        }

        victory = false;
        defeat = false;

        // Alle Positions-Buttons entfernen und neu erstellen
        removeAll();

        // Äußere for-Schleife muss über Zeilen laufen, um Container.add in der richtigen Reihenfolge aufzurufen
        for (int rowIndex = 0; rowIndex < this.fieldHeight; rowIndex++) {
            for (int columnIndex = 0; columnIndex < this.fieldWidth; columnIndex++) {
                // Höhe der aktuellen Spalte validieren
                if (field[columnIndex].length != this.fieldHeight) {
                    throw new IllegalArgumentException("Field height should be " + this.fieldHeight
                            + ", but height of column + " + columnIndex + "was " + field[columnIndex].length + ".");
                }
                Position newPosition = field[columnIndex][rowIndex];
                this.field[columnIndex][rowIndex] = newPosition;
                add(newPosition);
            }
        }

        countNeighboringMines();

        // Komponente neu zeichnen lassen
        revalidate();
        repaint();
    }

    private void countNeighboringMines() {
        for (int columnIndex = 0; columnIndex < fieldWidth; columnIndex++) {
            for (int rowIndex = 0; rowIndex < fieldHeight; rowIndex++) {
                int count = getMinesCount(columnIndex - 1, rowIndex - 1)
                        + getMinesCount(columnIndex, rowIndex - 1)
                        + getMinesCount(columnIndex + 1, rowIndex - 1)
                        + getMinesCount(columnIndex - 1, rowIndex)
                        + getMinesCount(columnIndex + 1, rowIndex)
                        + getMinesCount(columnIndex - 1, rowIndex + 1)
                        + getMinesCount(columnIndex, rowIndex + 1)
                        + getMinesCount(columnIndex + 1, rowIndex + 1);
                field[columnIndex][rowIndex].setNeighboringMinesCount(count);
            }
        }
    }

    private int getMinesCount(int columnIndex, int rowIndex) {
        if ((columnIndex >= 0) && (columnIndex < fieldWidth) && (rowIndex >= 0) && (rowIndex < fieldHeight)) {
            return (field[columnIndex][rowIndex].isMine() ? 1 : 0);
        } else {
            return 0;
        }
    }

    public void setPositionMouseAdapter(MouseAdapter positionMouseAdapter) {
        for (int columnIndex = 0; columnIndex < fieldWidth; columnIndex++) {
            for (int rowIndex = 0; rowIndex < fieldHeight; rowIndex++) {
                field[columnIndex][rowIndex].addMouseListener(positionMouseAdapter);
            }
        }
    }

    private void propagate(int columnIndex, int rowIndex) {
        if ((columnIndex >= 0) && (columnIndex < fieldWidth) && (rowIndex >= 0) && (rowIndex < fieldHeight)) {
            Position target = field[columnIndex][rowIndex];
            if (!target.isRevealed()) {
                target.setRevealed(true);
                if (target.getNeighboringMinesCount() == 0) {
                    propagate(columnIndex - 1, rowIndex - 1);
                    propagate(columnIndex, rowIndex - 1);
                    propagate(columnIndex + 1, rowIndex - 1);
                    propagate(columnIndex - 1, rowIndex);
                    propagate(columnIndex + 1, rowIndex);
                    propagate(columnIndex - 1, rowIndex + 1);
                    propagate(columnIndex, rowIndex + 1);
                    propagate(columnIndex + 1, rowIndex + 1);
                }
            }
        }
    }

    private void markPosition(int columnIndex, int rowIndex) {
        field[columnIndex][rowIndex].setMarked(true);
    }

    private void setPosition(int columnIndex, int rowIndex) {
        Position target = field[columnIndex][rowIndex];
        if (target.isMarked()) {
            return;
        }
        if (target.isMine()) {
            target.setRevealed(true);
            defeat = true;
        } else {
            propagate(columnIndex, rowIndex);
            if (!hasHiddenPositions()) {
                victory = true;
            }
        }
    }

    private boolean hasHiddenPositions() {
        for (int columnIndex = 0; columnIndex < fieldWidth; columnIndex++) {
            for (int rowIndex = 0; rowIndex < fieldHeight; rowIndex++) {
                if (!field[columnIndex][rowIndex].isMine() && !field[columnIndex][rowIndex].isRevealed()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void reveal() {
        for (int columnIndex = 0; columnIndex < fieldWidth; columnIndex++) {
            for (int rowIndex = 0; rowIndex < fieldHeight; rowIndex++) {
                field[columnIndex][rowIndex].setRevealed(true, false);
            }
        }
    }

    public boolean isVictory() {
        return victory && !defeat;
    }

    public boolean isDefeat() {
        return defeat && !victory;
    }

    public Position[][] getPositionArray() {
        return field;
    }

    public void updatePosition(PositionUpdate update) {
        Position pos = field[update.columnIndex][update.rowIndex];
        if (pos.isRevealed()) {
            return;
        }
        if (update.rightClick) {
            markPosition(update.columnIndex, update.rowIndex);
        } else {
            setPosition(update.columnIndex, update.rowIndex);
        }

        if (isVictory() || isDefeat()) {
            reveal();
        }
    }

    /**
     * Ersetzt das Spielfeld durch das übergebene Position-Array.
     * @param field Die Position-Objekte des neuen Spielfeldes.
     */
    public void importPositionArray(Position[][] field) {
        int width = field.length;
        if (width < 1) {
            return;
        }
        int height = field[1].length;
        if (height < 1) {
            return;
        }

        this.fieldWidth = width;
        this.fieldHeight = height;
        this.field = new Position[fieldWidth][fieldHeight];
        setLayout(new GridLayout(fieldHeight, fieldWidth));
        init(field);
    }
}
