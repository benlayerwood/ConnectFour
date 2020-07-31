# Vier Gewinnt (PiS, SoSe 2020) 

Autor: Benjamin Schichtholz

Ich habe die Zulassung für PiS im SoSe 2020 bei Herrn Herzberg erhalten.

## Einleitung

Dies ist die Dokumentation zur Implementierung des Spiels "Vier Gewinnt". Die Spiellogik wurde mit der Programmiersprache <b>Kotlin</b> realisiert, die Oberfläche wurde mit <b>HTML, CSS</b> und <b>JavaScript</b> gestaltet und kann im Browser aufgerufen werden. Das Spiel beinhaltet eine Spielengine, welche mithilfe des Alpha-Beta-Algorithmus den bestmöglichen Zug ermittelt. Dieses Projekt ist die Prüfungsleistung für das Modul "Programmierung interaktive Systeme" im Studiengang Informatik an der Technischen Hochschule Mittelhessen.

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

* ##### Klasse Board
Die Klasse Board enthält die gesamte Spiellogik. Hier werden alle wichtigen Informationen zum aktuellen Spielbrett gespeichert.
Das Spielbrett wird in zwei sogenannten Bitboards abgespeichert. Das heißt, dass die Information über die Position der Spielsteine in zwei Long-Werten abgespeichert sind und dass einzelne Bits dieser Werte Positionen auf dem Spielbrett widerspiegeln. Die folgende Abbildung zeigt, welche Bits beim Spiel Vier Gewinnt an welcher Position stehen. Dabei ist das 0te Bit das sogenannte least significant Bit, also das erste Bit von rechts aus gesehen.

          6 13 20 27 34 41 48   55 62     Zusätzliche Zeile
        +---------------------+
        | 5 12 19 26 33 40 47 | 54 61     Oberste Zeile
        | 4 11 18 25 32 39 46 | 53 60
        | 3 10 17 24 31 38 45 | 52 59
        | 2  9 16 23 30 37 44 | 51 58
        | 1  8 15 22 29 36 43 | 50 57
        | 0  7 14 21 28 35 42 | 49 56 63  Unterste Zeile
        +---------------------+
        (Abbildung Bitboard)

Die grundlegende Idee zur Implementierung von Bitboards, die Klassenvariablen und die dazugehörigen Methoden (makeMove(),undoMove(),possibleMoves(),isWinning(player)) für das Spiel Vier Gewinnt stammt aus dem Artikel <b>Bitboards and Connect Four</b>, welches von Dominikus Herzberg auf GitHub veröffentlicht wurde (https://github.com/denkspuren/BitboardC4/blob/master/BitboardDesign.md).

Die beiden Bitboards sind in Klassenvariablen und werden gemeinsam in einem Array <b>bitboard</b> abgespeichert, sodass mit bitboard[0] bzw bitboard[1] darauf zugegriffen werden kann.

Die Klassenvariable <b>counter</b> wird bei jedem gemachten Zug (makeMove()) um 1 erhöht bzw. bei der Methode undoMove() um 1 erniedrigt. Anhand von counter kann man leicht ermitteln, welcher Spieler momentan am Zug ist. Verbindet man counter mit der logischen Verknüpfung und 1 (counter and 1), gibt es für eine gerade counter-Zahl 0 zurück (Spieler 0) und für jede ungerade Zahl 1 (Spieler 1).

Die Klassenvariable <b>height</b> speichert die aktuelle Höhe für die jeweilige Spalte in einem Array ab. Am Anfang entspricht die Höhe der jeweiligen Spalten [0, 7, 14, 21, 28, 35, 42] (siehe Abbildung Bitboard).

Die Klassenvariable <b>moves</b> ist eine Liste aus Integer-Werten, welche die bereits gespielten Züge abspeichert. Entfernt man den letzten zugehörigen Spielstein aus der angegebenen Spalte, kann der zuletzt getätigte Zug rückgängig gemacht werden.

Die Methoden makeMove(), undoMove() wurden aus dem oben genannten Artikel übernommen und so angepasst, dass sie nicht das aktuelle Spielfeld verändern, sondern ein neues Spielfeld zurückgeben. Die anderen beiden Methoden (isWinning(player), possibleMoves()) wurden hauptsächlich übernommen und in Kotlin-Code übersetzt.

Die Methode toString() geht die einzelnen Bits in den Bitboards aus und gibt dann Spielbrett aus. Um die Spielsteine darzustellen, wurden Kreise verwendet. Je nach Betriebssystem kann es sein, dass diese Zeichen jedoch in einem unpraktischen Format angezeigt werden. Ist dies der Fall, können die Zeichen in den Zeilen 254 und 255 in der Datei Board.kt zu einem beliebigen Zeichen geändert werden.

#### Monte-Carlo
Für die Monte-Carlo-Implementierung, wurden Ideen aus dem Video "Die Monte Carlo Tree Search" (https://www.youtube.com/watch?v=CjldSexfOuU), welches von Dominikus Herzberg produziert wurde, übernommen. Der Kern der Implementierung besteht aus den drei Funktionen playRandomGame(), simulateGames() und evaluateMoves().

* ##### playRandomGame()
Diese Funktion spielt auf einer Kopie des aktuellen Board-Objektes zufällige Züge, bis das Spiel zu Ende ist. Da die Funktion immer aus der Sicht von Spieler 1 gespielt wird, wird das Board invertiert (invBoard()), wenn der aktuelle Spieler Spieler 0 ist. Die Funktion invBoard() gibt eine Kopie des aktuellen Boards zurück, bei welcher die beiden Bitboards vertauscht sind. Danach werden die zufälligen Züge ausgespielt. Bei einem Sieg von Spieler 1 gibt die Funktion 1 zurück, bei einem Sieg von Spieler 0 gibt die Funktion -1 zurück.

* ##### simulateGames()
In dieser Methode wird playRandomGame() nacheinander aufgerufen. Wie oft sie aufgerufen wird, ist abhängig von der klassenweiten Variable <b>monteCarloIterations</b>, welche die Anzahl an Durchläufen festlegt. Das Anpassen dieser Zahl wirkt sich einerseits auf die Genauigkeit der Monte-Carlo-Bewertung, andererseits auf die Laufzeit aus. Wird ein hoher Wert (z.B. 1000) gewählt, ist die Genauigkeit der Bewertung gegeben, die Zeit zur Berechnung der ersten Züge kann dann jedoch mehr als 4 Sekunden dauern. Ist der Wert niedrig (z.B. 100), ist die Zeit für die Ermittlung des besten Zuges niedrig, die Qualität des besten Zuges lässt dann aber nach. Wird der Alpha-Beta-Algorithmus mit einer Tiefe von 4 gespielt, ist ein Wert im Bereich 300 bis 500 gut geeignet. Die Methode simulateGames() zählt letztendlich die Niederlagen von Spieler 1 bzw. die Siege von Spieler 0. Liegt bei der aktuellen Position bereits ein Gewinn für Spieler 1 vor, gibt die Methode den Wert der Variable monteCarloIterations zurück.

* ##### evaluateMoves()
Diese Methode führt für den aktuellen Spieler alle möglichen Züge aus und ruft für das sich daraus ergebende Board die Methode simulateGames() auf. Es werden also für die jeweilige generierte Spielstellung (nach Ausführen des Zuges) die Niederlagen vom Gegner gezählt. Als Ergebnis liefert die Methode die Anzahl an Siegen nach jeweiligen Ausführen aller möglichen Züge. Dieses Ergebnis wird in Form einer Liste gespeichert, welche Werte vom Datentyp Pair enthält. Der erste Wert in der Pair ist der gewählte Zug, der zweite Wert ist die Anzahl an Siegen.
Der Durchschnittswert, welcher in der Methode averageBoard() die Werte aus evaluateMoves() verwendet, bildet die Bewertungsfunktion für den Alpha-Beta-Algorithmus.


→ <b>Beispiel</b>
Folgende Spielstellung liegt vor und Spieler X ist am Zug.
Die Variable monteCarloIterations ist auf 500 gesetzt.

    . . . . . X . 
    . . . . . 0 . 
    . . . 0 . X . 
    . 0 X 0 . 0 0 
    . 0 X X . X X 
    X 0 0 X 0 0 X 

Nun werden alle möglichen Züge ausprobiert und die Anzahl an Siegen in einer Liste zurückgegeben.

[(0, 194), (1, 281), (2, 251), (3, 184), (4, 500), (6, 185)]

An der Liste kann man erkennen, dass Spieler X beim Zug 4 in jedem Fall gewonnen hat.
Diese Spielstellung würde die Bewertung (194 + 281 + 251 + 184 + 500 + 185) / 7 = 227 bekommen.

#### Alpha-Beta
Die Methode alphaBeta wird von der Methode bestMove aufgerufen und liefert ein Pair zurück, bei welchem der erste Wert der bestmögliche Zug ist, und der zweite Wert die dazugehörige Bewertung.

Beim Aufruf von alphaBeta innerhalb der Funktion bestMove() gibt es nun zwei Fälle:
    1. Spieler 1 ist am Zug → Rufe Alpha-Beta auf
    2. Spieler 0 ist am Zug → Invertiere das Spielbrett und rufe dann Alpha-Beta auf

Das Board wird im zweiten Fall deshalb invertiert, weil der Alpha-Beta-Algorithmus so konzipiert wurde, dass er <b>immer</b> aus der Sicht von Spieler 1 spielt.

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
