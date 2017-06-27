package de.unitrier.st.PII.ueb03;

import java.io.File;
import java.io.IOException;

/**
 * Doppelt verkettete Liste.
 */
public class List {
    protected Element begin;
    protected Element end;
    protected int length;

    /**
     * Füge ein Element am Ende der Liste hinzu.
     * @param e Neues Listenelement.
     */
    public void append(Element e) {
        if (begin == null) {
            begin = e;
            end = e;
            e.setPrev(null);
            e.setNext(null);
        } else {
            Element tmp = end;
            end = e;
            tmp.setNext(e);
            e.setPrev(tmp);
            e.setNext(null);
        }
        length++;
    }

    /**
     * Erzeuge ein Element, das den Wert v enthält, und füge es am Ende der Liste hinzu.
     * @param value Wert des neuen Elements.
     */
    public void append(double value) {
        append(new Element(value));
    }

    /**
     * Füge die Elemente aus der übergebenen Liste am Ende dieser Liste hinzu.
     * @param list Die Liste, deren Elemente angefügt werden sollen.
     */
    public void append(List list) {
        Element e = list.getBegin();
        while (e != null) {
            Element next = e.getNext(); // append sets e.next to null
            append(e);
            e = next;
        }
    }

    /**
     * Erzeuge ein neues Element pro Wert im übergebenen Array und füge die neuen Elemente am Ende der Liste hinzu.
     * @param values Array mit Double-Werten, die eingefügt werden sollen.
     */
    public void append(double[] values) {
        for (double value : values) {
            append(value);
        }
    }

    /**
     * Erzeuge ein Array mit den Elementen aus der Liste (gleiche Reihenfolge).
     * @return Array mit Elementen der Liste (in gleicher Reihenfolge).
     */
    public double[] asArray() {
        double[] array = new double[length];
        Element element = begin;
        int arrayPos = 0;
        while (element != null) {
            array[arrayPos] = element.value;
            arrayPos++;
            element = element.next;
        }
        return array;
    }

    /**
     * Gib die Liste auf der Konsole aus.
     */
    public void print() {
        if (isEmpty()) {
            System.out.println("Empty");
        } else {
            Element pos = begin;
            while (pos != null) {
                pos.print();
                pos = pos.next;
            }
        }
    }

    /**
     * Leere die Liste.
     */
    public void empty() {
        begin = null;
        end = null;
        length = 0;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Element getBegin() {
        return begin;
    }

    public void setBegin(Element begin) {
        this.begin = begin;
    }

    public Element getEnd() {
        return end;
    }

    public void setEnd(Element end) {
        this.end = end;
    }

    public boolean isEmpty() {
        return (begin == null) && (end == null) && (length == 0);
    }

    /**
     * Liest Werte aus einer CSV-Datei and fügt sie an die Liste an.
     *
     * @param file CSV-Datei, aus der gelesen wird.
     */
    public void readFromFile(File file) {

        //TODO: Übungsblatt 3, Aufgabe 1

    }

    /**
     * Schreibt Werte in der Liste in eine CSV-Datei.
     *
     * @param file CSV-Datei, in die geschrieben wird.
     * @throws IOException Falls die Datei file bereits existiert oder Fehler beim Schreiben auftreten.
     */
    public void writeToFile(File file) throws IOException {

        //TODO: Übungsblatt 3, Aufgabe 2

    }

    /**
     * Die Klasse für die Elemente der doppelt verketteten Liste.
     */
    public static class Element {
        private double value;
        private Element next;
        private Element prev;

        public Element(double value) {
            setValue(value);
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public Element getNext() {
            return next;
        }

        public void setNext(Element next) {
            this.next = next;
        }

        public Element getPrev() {
            return prev;
        }

        public void setPrev(Element prev) {
            this.prev = prev;
        }

        void print() {
            System.out.println(value);
        }
    }

}
