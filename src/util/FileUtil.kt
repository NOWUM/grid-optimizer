package de.fhac.ewi.util

import java.io.File

/**
 * Legt eine neue Datei und ggf. fehlende Ordner an oder leert die bestehende Datei.
 * Nach Aufruf dieser Funktion ist, sofern kein Fehler aufgetreten ist, die Datei ohne Inhalt existent.
 *
 * @receiver File die erstellt oder geleert werden soll
 */
fun File.createOrEmptyFile() {
    if (exists())
        writeText("")
    else
        createNewFileAndFolder()

}

/**
 * Erstellt zuerst fehlende übergeordnete Ordner und dann die fehlende Datei.
 *
 * @receiver File die erstellt werden soll
 */
fun File.createNewFileAndFolder() {
    if (parentFile != null && !parentFile.exists())
        parentFile.mkdirs()
    createNewFile()
}

/**
 * List alle Zeilen der Datei mit Zeilenindex beginnend bei 0 ein und führt die Aktion aus.
 *
 * Hierfür wird ein BufferedReader für die Datei geöffnet, alle Zeilen gelesen und die Aktion ausgeführt.
 * Der verwendete BufferedReader wird zum Schluss geschlossen.
 *
 * @receiver File von dem die Zeilen gelesen werden sollen.
 * @param action Function2<Int, String, Unit> Aktion die ausgeführt werden soll.
 */
fun File.readLinesIndexed(action: (Int, String) -> Unit) = useLines { it.forEachIndexed(action) }
