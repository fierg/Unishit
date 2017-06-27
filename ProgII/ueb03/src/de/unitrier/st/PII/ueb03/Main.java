package de.unitrier.st.PII.ueb03;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        // TODO: Diese Methode können Sie gerne anpassen, um Ihre Lösung zu testen.

        File import_file = new File("src/de/unitrier/st/PII/ueb03/data/data_import.csv");
        File export_file = new File("src/de/unitrier/st/PII/ueb03/data/data_export.csv");

        // lösche die Export-Datei, falls sie bereits existiert
        if (export_file.exists()) {
            if (!export_file.delete()) {
                System.err.println("Error while deleting file: " + export_file.getAbsolutePath());
            }
        }

        List list = new List();

        // Übungsblatt 3 Aufgabe 1
        list.readFromFile(import_file);

        System.out.println();

        // Übungsblatt 3 Aufgabe 2
        try {
            list.writeToFile(export_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
