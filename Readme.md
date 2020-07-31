# Vier Gewinnt (PiS, SoSe 2020) 

Autor: Benjamin Schichtholz

Ich habe die Zulassung für PiS im SoSe 2020 bei Herrn Herzberg erhalten.

## Einleitung

Dies ist die Dokumentation zur Implementierung des Spiels "Vier Gewinnt". Die Spiellogik wurde mit der Programmiersprache <b>Kotlin</b> realisiert, die Oberfläche wurde mit <b>HTML,CSS</b> und <b>JavaScript</b> gestaltet und kann im Browser aufgerufen werden. Das Spiel beinhaltet eine Spielengine, welche mithilfe des Alpha-Beta-Algorithmus den bestmöglichen Zug ermittelt. Dieses Projekt ist die Prüfungsleistung für das Modul "Programmierung interaktive Systeme" im Studiengang Informatik an der Technischen Hochschule Mittelhessen.

### Spielregeln

Das Spielbrett hat 8 Reihen und 6 Spalten. Es gibt zwei Spieler, welche Spielsteine ihrer jeweiligen Farbe in eine bestimmte Spalte setzen dürfen. Dieser gesetzte Spielstein kommt in das von unten gesehen erste freie Feld. Die Spielsteine können also aufeinander gelegt werden. Schafft es ein Spieler, vier Spielsteine so anzuordnen dass sie vertikal, horizontal oder vertikal eine Reihe ergeben, hat dieser Spieler gewonnen.

### Bedienungsanleitung
#### Screenshot
![Screenshot](Screenshot.png)

#### Oberfläche
Auf der <b>linken Seite</b> der Benutzeroberfläche ist das Spielbrett zu sehen. Bei den jeweiligen Spalten in die man Spielsteine setzen kann, kann man durch den Klick auf eines der Dreiecke sein Spielstein in eine Spalte setzen. Oben links über dem Spielbrett befindet sich eine Zeitanzeige, welche die Dauer zum Ermitteln des besten Zuges vom letzten computergenerierten Zug anzeigt.
Auf der <b>rechten Seite</b> hat der Benutzer weitere Auswahlmöglichkeiten. Im oberen Abschnitt (New Game) kann ein neues Spiel gestartet werden. Dabei kann ausgewählt werden, welcher Spieler ein menschlicher Spieler oder ein computergesteuerter Spieler ist. Hierbei ist Player1 immer der beginnende Spieler. Darunter befinden sich drei weitere Knöpfe. Mit "Make Best Move" kann der sich menschliche Spieler für den nächsten Zug vom Computer den bestmöglichen Zug spielen lassen. Mit "Undo Move" wird der zuletzt ausgeführte Spielzug rückgängig gemacht. Spielt der Mensch gegen den Computer und der letzte Zug des Computers wird rückgängig gemacht, passiert folgendes: Der Mensch darf zuerst den Zug des Computers machen und danach seinen eigenen Zug. Danach geht das Spiel wie gewohnt weiter und der Computer antwortet automatisch auf den Zug seines Gegenspielers. Der Knopf "Run Tests" startet 5 Testfälle, welche auf der Konsole ausgegeben werden.

#### Spielmodi
Die Auswahlmöglichkeiten im Feld "New Game" auf der rechten Seite lassen folgende Spielkonstellationen zu:
* Mensch gegen Computer (Standardeinstellung, Mensch beginnt)
* Computer gegen Mensch (Computer beginnt)
* Mensch gegen Mensch (Spieler 1 beginnt)
* Computer gegen Computer (Spieler 1 beginnt)

### Engine
In diesem Abschnitt werden Details zur Engine erläutert. Der Fokus dabei liegt auf der Klasse <b>Board</b>.
#### Basis-Implementierung des Spiels
* ##### Interface Game
Das Interface Game wird von der Klasse Board implementiert. Dieses Interface beschreibt die wichtigsten Funktionen, welche ein Spiel mit einer Implementierung für die Berechnung des bestmöglichen Zuges haben sollte. Außerdem zeigt sich bereits im Interface, das eine Implementierung des Interface immutabel sein muss. So geben die Methoden, welche das Spielbrett verändern (z.B. makeMove(),undoMove()) ein neues Game mit den geänderten Werten zurück. Außerdem sind hier bereits Funktionen vorhanden, welche mit dem Alpha-Beta-Algorithmus zusammenspielen: bestMove(), makeBestMove(), symmetricBoard(), possibleMoves().

* #### Klasse Board
Die Klasse Board enthält die gesamte Spiellogik. Hier werden alle wichtigen Informationen zum aktuellen Spielbrett gespeichert.

(Aufbau, Klassenvariablen)
Grundlegende Methoden erläutern (makeMove, isGameOver, undoMove, possiblMoves)
→ toString (evtl. Hinweis auf Zeichen für Spielsteine & wo sie geändert werden können)
Quelle angeben (Herzberg GitHub)
#### Monte-Carlo
→ Einzelne Methoden beschreiben (playRandomGame(),simulateGames(),evaluateMoves())
→ Bewertungslogik beschreiben (warum wurde der Durchschnitt genommen)
Quelle angeben (Herzberg TicTacToe)
#### Alpha-Beta
→ Grundsätzliches (Algorithmus läuft immer aus Sicht von Spieler 1)
→ bestMove()-Aufruf mit invBoard()
→ Parameter (depth, maximize, alpha, beta, cache)
→ Rückgabewert Pair<Int?,Int>
→ getBitPair beschreiben (Wozu ist die Funktion da?)
→ Algorithmus beschreiben (shuffled(), Blick auf das nächste Board)
→ Zusammenspiel mit MonteCarlo (Wann wird bewertet?)
→ HashMap

### Testfälle

### Technische Umsetzung der Oberfläche

### Dateiübersicht

    \build.gradle
    \README.md
    \bin\main\public\index.html
    \bin\main\TicTacToe\App.kt
    \bin\main\TicTacToe\T3.kt
    \src\main\kotlin\TicTacToe\App.kt
    \src\main\kotlin\TicTacToe\T3.kt
    \src\main\resources\public\index.html

    -------------------------------------------------------------------------------
    Language                     files          blank        comment           code
    -------------------------------------------------------------------------------
    Markdown                         1             71              0            270
    Kotlin                           3             27              3            113
    HTML                             1             11             17             80
    XML                              2              0              0             41
    Gradle                           1              8             12             16
    INI                              1              0              0             13
    -------------------------------------------------------------------------------
    SUM:                             9            117             32            533
    -------------------------------------------------------------------------------
