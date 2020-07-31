# Vier Gewinnt (PiS, SoSe 2020) 

Autor: Benjamin Schichtholz (5097555)

Ich habe die Zulassung für PiS im SoSe 2020 bei Herrn Herzberg erhalten.

----

## Einleitung

Dies ist die Dokumentation zur Implementierung des Spiels "Vier Gewinnt". Die Spiellogik wurde mit der Programmiersprache <b>Kotlin</b> realisiert, die Oberfläche wurde mit <b>HTML, CSS</b> und <b>JavaScript</b> gestaltet und kann im Browser aufgerufen werden. Das Spiel beinhaltet eine Spielengine, welche mithilfe des Alpha-Beta-Algorithmus den bestmöglichen Zug ermittelt. Dieses Projekt ist die Prüfungsleistung für das Modul "Programmierung interaktive Systeme" im Studiengang Informatik an der Technischen Hochschule Mittelhessen.

### Spielregeln

Das Spielbrett hat 8 Reihen und 6 Spalten. Es gibt zwei Spieler, welche Spielsteine ihrer jeweiligen Farbe in eine bestimmte Spalte setzen dürfen. Dieser gesetzte Spielstein kommt in das von unten gesehen erste freie Feld. Die Spielsteine können also aufeinander gelegt werden. Schafft es ein Spieler, vier Spielsteine so anzuordnen dass sie vertikal, horizontal oder vertikal eine Reihe ergeben, hat dieser Spieler gewonnen.

### Bedienungsanleitung
#### Screenshot
![Screenshot](Screenshot.png)

#### Oberfläche
Auf der <b>linken Seite</b> der Benutzeroberfläche ist das Spielbrett zu sehen. Bei den jeweiligen Spalten in die man Spielsteine setzen kann, kann man durch den Klick auf eines der Dreiecke sein Spielstein in eine Spalte setzen. Oben links über dem Spielbrett befindet sich eine Zeitanzeige, welche die Dauer zum Ermitteln des besten Zuges vom letzten computergenerierten Zug anzeigt.

Auf der <b>rechten Seite</b> hat der Benutzer weitere Auswahlmöglichkeiten. Im oberen Abschnitt (New Game) kann ein neues Spiel gestartet werden. Dabei kann ausgewählt werden, welcher Spieler ein menschlicher Spieler oder ein computergesteuerter Spieler ist. Hierbei ist Player 1 immer der beginnende Spieler. Darunter befinden sich drei weitere Knöpfe. Mit "Make Best Move" kann der sich menschliche Spieler für den nächsten Zug vom Computer den bestmöglichen Zug spielen lassen. Mit "Undo Move" wird der zuletzt ausgeführte Spielzug rückgängig gemacht. Spielt der Mensch gegen den Computer und der letzte Zug des Computers wird rückgängig gemacht, passiert folgendes: Der Mensch darf zuerst den Zug des Computers machen und danach seinen eigenen Zug. Danach geht das Spiel wie gewohnt weiter und der Computer antwortet automatisch auf den Zug seines Gegenspielers. Der Knopf "Run Tests" startet 5 Testfälle, welche auf der Konsole ausgegeben werden.

#### Spielmodi
Die Auswahlmöglichkeiten im Feld "New Game" auf der rechten Seite lassen folgende Spielkonstellationen zu:
* Mensch gegen Computer (Standardeinstellung, Mensch beginnt)
* Computer gegen Mensch (Computer beginnt)
* Mensch gegen Mensch (Spieler 1 beginnt)
* Computer gegen Computer (Spieler 1 beginnt)

----
## Dateiübersicht

    /src
    /src/main
    /src/main/resources
    /src/main/resources/Public
    /src/main/resources/Public/red-circle.png
    /src/main/resources/Public/index.js
    /src/main/resources/Public/index.css
    /src/main/resources/Public/index.html
    /src/main/resources/Public/green-circle.png
    /src/main/kotlin
    /src/main/kotlin/connectFour
    /src/main/kotlin/connectFour/Game.kt
    /src/main/kotlin/connectFour/App.kt
    /src/main/kotlin/connectFour/Test.kt
    /src/main/kotlin/connectFour/Board.kt
    /Readme.md
    /build.gradle


    -------------------------------------------------------------------------------
    Language                     files          blank        comment           code
    -------------------------------------------------------------------------------
    Kotlin                           4             54            109            421
    CSS                              1             14              8            116
    HTML                             1              9              2             87
    JavaScript                       1              8             19             54
    -------------------------------------------------------------------------------
    SUM:                             7             85            138            678
    -------------------------------------------------------------------------------


----
## Engine
Feature    | AB  | H+S | MC  | -   | Im+B+I | Summe
-----------|-----|-----|-----|-----|------|----
Umsetzung  | 120 | 100 | 100 |   0 |  100 |
Gewichtung | 0.4 | 0.3 | 0.3 | 0.3 |  0.3 | 
Ergebnis   |  48 |  30 |  30 |   0 |   30 | **138%**


In diesem Abschnitt werden Details zur Engine erläutert. Der Fokus dabei liegt auf der Klasse <b>Board</b>.
### Basis-Implementierung des Spiels
#### Interface Game
Das Interface Game wird von der Klasse Board implementiert. Dieses Interface beschreibt die wichtigsten Funktionen, welche ein Spiel mit einer Implementierung für die Berechnung des bestmöglichen Zuges haben sollte. Außerdem setzt das Interface bereits voraus, das dessen des Interface immutabel sein muss. So geben die Methoden, welche das Spielbrett verändern (z.B. makeMove(),undoMove()) ein neues Game mit den geänderten Werten zurück. Außerdem sind hier bereits Funktionen vorhanden, welche mit dem Alpha-Beta-Algorithmus zusammenspielen: bestMove(), makeBestMove(), symmetricBoard(), possibleMoves().

#### Klasse Board
##### Bitboard
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

##### Klassenvariablen
Die Klassenvariable <b>counter</b> wird bei jedem gemachten Zug (makeMove()) um 1 erhöht bzw. bei der Methode undoMove() um 1 erniedrigt. Anhand von counter kann man leicht ermitteln, welcher Spieler momentan am Zug ist. Verbindet man counter mit der logischen Verknüpfung und 1 (counter and 1), gibt es für eine gerade counter-Zahl 0 zurück (Spieler 0) und für jede ungerade Zahl 1 (Spieler 1).

Die Klassenvariable <b>height</b> speichert die aktuelle Höhe für die jeweilige Spalte in einem Array ab. Am Anfang entspricht die Höhe der jeweiligen Spalten [0, 7, 14, 21, 28, 35, 42] (siehe Abbildung Bitboard).

Die Klassenvariable <b>moves</b> ist eine Liste aus Integer-Werten, welche die bereits gespielten Züge abspeichert. Entfernt man den letzten zugehörigen Spielstein aus der angegebenen Spalte, kann der zuletzt getätigte Zug rückgängig gemacht werden.

##### Methoden
Die Methoden makeMove(), undoMove() wurden aus dem oben genannten Artikel übernommen und so angepasst, dass sie nicht das aktuelle Spielfeld verändern, sondern ein neues Spielfeld zurückgeben (Stichwort Immutabilität). Die anderen beiden Methoden (isWinning(player), possibleMoves()) wurden hauptsächlich übernommen und in Kotlin-Code übersetzt.

Die Methode toString() geht die einzelnen Bits in den Bitboards aus und gibt dann das Spielbrett aus.

##### Symmetrisches Board
Diese Methode gibt das symmetrische Gegenstück zum aktuellen Board zurück. Dabei werden die Spalten einzeln verschoben. Die erste Spalte wird zur letzten Spalte, die zweite Spalte zur vorletzten, die dritte Spalte zur drittletzten und umgekehrt. Die Spalte in der Mitte bleibt gleich, weil sie die "Symmetrieachse" bildet.
* In der Variable <b>index</b> sind die Anzahl an Bits gespeichert, wie oft die Werte der jeweiligen Spalte verschoben werden müssen

        → z.B. müssen die Bits der 1.Spalte 42 Bits nach links verschoben werden, damit sie an die Stelle der letzten Spalte kommen
* In den Variablen l und r sind die Bits auf 1 gesetzt, die zur jeweiligen Spalte gehören
        
        → z.B. Spalte 1: l[0] = 0b111111L, Spalte 2: l[1] = 0b1111110000000
* In resl (result left) und resr (result right) werden die neue linke und rechte Hälfte des Spielbretts zwischengespeichert
* In resm (result middle) wird die mittlere Spalte zwischengespeichert
* Am Ende werden mittels einer OR-Verknüpfung resl, resm und resr verbunden und das symmetrische Board kann zurückgegeben werden

### Klasse Test
In der Test-Klasse ist ein Board als veränderliche Klassenvariable abgespeichert.
Innerhalb der Test-Klasse können die 5 geforderten Tests mit der Methode <b>runTests()</b> aufgerufen werden. Die Testergebnisse werden auf der Konsole angezeigt.
Bei der Entwicklung der Engine war Sinn und Zweck der Test-Klasse, die Funktionalität und Korrektheit der Engine zu überprüfen. Deshalb sind hier diverse Methoden vorhanden, welche Werte auf die Konsole schreiben.

Zusätzlich kann die Test-Klasse mit der Methode testBestMoveExamples() "kritische" Testfälle generieren. Die Methode spielt zufällige Züge, bis der Alpha-Beta ein anderes Ergebnis vorweist im Vergleich zum besten Zug der Monte-Carlo-Methode. Dadurch werden die "einfachen Beispiele" meist herausgefiltert, z.B. solche Beispiele, bei welchen der Spieler im nächsten Zug gewinnen kann.
Mit der Methode <b>playAlphaBeta()</b> kann man den Alpha-Beta gegen sich selbst spielen lassen und den Fortschritt auf der Konstole überprüfen, während man mit der Methode <b>playAgainstAlphaBeta()</b> selbst gegen den Alpha-Beta spielen kann.

### Monte-Carlo
Für die Monte-Carlo-Implementierung, wurden Ideen aus dem Video "Die Monte Carlo Tree Search" (https://www.youtube.com/watch?v=CjldSexfOuU), welches von Dominikus Herzberg produziert wurde, übernommen. Der Kern der Implementierung besteht aus den drei Funktionen playRandomGame(), simulateGames() und evaluateMoves().

* #### playRandomGame()
Diese Funktion spielt auf einer Kopie des aktuellen Board-Objektes zufällige Züge, bis das Spiel zu Ende ist. Da die Funktion immer aus der Sicht von Spieler 1 gespielt wird, wird das Board invertiert, wenn der aktuelle Spieler Spieler 0 ist. Die Funktion <b>invBoard()</b> gibt eine Kopie des aktuellen Boards zurück, bei welcher die beiden Bitboards vertauscht sind. Danach werden die zufälligen Züge ausgespielt. Bei einem Sieg von Spieler 1 gibt die Funktion 1 zurück, bei einem Sieg von Spieler 0 gibt die Funktion -1 zurück.

* #### simulateGames()
In dieser Methode wird playRandomGame() nacheinander aufgerufen. Wie oft sie aufgerufen wird, ist abhängig von der klassenweiten Variable <b>monteCarloIterations</b>, welche die Anzahl an Durchläufen festlegt. Das Anpassen dieser Zahl wirkt sich einerseits auf die Genauigkeit der Monte-Carlo-Bewertung, andererseits auf die Laufzeit aus. Wird ein hoher Wert (z.B. 1000) gewählt, ist die Genauigkeit der Bewertung gegeben, die Zeit zur Berechnung der ersten Züge kann dann jedoch mehr als 4 Sekunden dauern. Ist der Wert niedrig (z.B. 100), ist die Zeit für die Ermittlung des besten Zuges niedrig, die Qualität des besten Zuges lässt dann aber nach. Wird der Alpha-Beta-Algorithmus mit einer Tiefe von 4 gespielt, ist ein Wert im Bereich 300 bis 500 gut geeignet. Die Methode simulateGames() zählt letztendlich die Niederlagen von Spieler 1 bzw. die Siege von Spieler 0. Liegt bei der aktuellen Position bereits ein Gewinn für Spieler 1 vor, gibt die Methode den Wert der Variable monteCarloIterations zurück.

* #### evaluateMoves()
Diese Methode führt für den aktuellen Spieler alle möglichen Züge aus und ruft für das sich daraus ergebende Board die Methode simulateGames() auf. Es werden also für die jeweilige generierte Spielstellung (nach Ausführen des Zuges) die Niederlagen vom Gegner gezählt. Als Ergebnis liefert die Methode die Anzahl an Siegen nach jeweiligen Ausführen aller möglichen Züge. Dieses Ergebnis wird in Form einer Liste gespeichert, welche Werte vom Datentyp Pair enthält. Der erste Wert in der Pair ist der gewählte Zug, der zweite Wert ist die Anzahl an Siegen.
Der Durchschnittswert, welcher in der Methode <B>averageBoard()</b> die Werte aus evaluateMoves() verwendet, bildet die Bewertungsfunktion für den Alpha-Beta-Algorithmus.


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

Die Anzahl der möglichen Züge ist hier 6, da die vorletzte Spalte bereits voll ist. An der Liste kann man erkennen, dass Spieler X beim Zug 4 in jedem Fall gewonnen hat.
Diese Spielstellung würde die Bewertung 

    (194 + 281 + 251 + 184 + 500 + 185) / 6 = 265
 bekommen.

### Alpha-Beta
#### Allgemeines
Die Methode <b>alphaBeta</b> wird von der Methode <b>bestMove</b> aufgerufen und liefert ein <b>Pair<Int?,Int></b> zurück, bei welchem der erste Wert der bestmögliche Zug ist und der zweite Wert die dazugehörige Bewertung. Der erste Wert kann null werden, weil innerhalb vom Alpha-Beta-Algorithmus manchmal nur Bewertungen zurückgegeben werden, aber keine Züge. In diesem Fall wird der erste Pair-Wert null und der zweite Wert entspricht der Bewertung.

Beim Aufruf von alphaBeta innerhalb der Funktion bestMove() gibt es nun zwei Fälle:
1. Spieler 1 ist am Zug → Rufe Alpha-Beta auf
2. Spieler 0 ist am Zug → Invertiere das Spielbrett und rufe dann Alpha-Beta auf
Das Board wird im zweiten Fall deshalb invertiert, weil der Alpha-Beta-Algorithmus so konzipiert wurde, dass er <b>immer</b> aus der Sicht von Spieler 1 spielt.

#### Parameter
Die Parameter des Alpha-Beta sind:
* <b>depth</b> (Aktuelle Tiefe)
* <b>maximize</b> (Ist der aktuelle Spieler der maximierende Spieler?)
* <b>alpha</b> (Alpha-Wert des maximierenden Spielers)
* <b>beta</b> (Beta-Wert des minimierenden Spielers)
* <b>cache</b> (HashMap zum Zwischenspeichern von Spielstellungen)).

#### Algorithmus
* Im ersten Abschnitt des Alpha-Beta-Algorithmus wird geprüft, ob ein Wert zum aktuellen Board in der <b>HashMap</b>(cache) abgespeichert ist. Die HashMap ist folgendermaßen strukturiert:

    → Key: Pair( Pair(bitboard[0],bitboard[1]), maximize)

    → Value: Bewertung

    Die Hilfsfunktion getBitPair() wandelt das Array mit den beiden Bitboards in ein Pair-Wert um. In der HashMap wird außerdem noch der maximize-Wert angegeben, deshalb wird die getBitPair-Methode mit dem aktuellen maximize-Wert verbunden und in der Variable index zwischengespeichert. Mit diesem Index kann nun geprüft werden, ob zur aktuellen Spielstellung bereits ein wert abgespeichert ist. Ist dies der Fall, gibt die alphaBeta-Methode die Stellungsbewertung zurück. Analog dazu wird zum symmetrischen Board die HashMap durchsucht, da die Stellungsbewertungen eines Boards auch für das symmetrische Gegenstück zutreffen.

* Im nächsten Schritt werden zunächst Kopien der Variablen alpha (al) und beta (be) angelegt, da sie innerhalb des Alpha-Beta noch verändert werden können

* Nun folgt die Abbruchbedingung des rekursiven Algorithmus: Wurde die Tiefe 0 erreicht oder ist das Spiel vorbei, können folgende Fälle zutreffen:
    * Spieler 1 hat gewonnen -> Bewertung = monteCarloIterations
    * Spieler 2 hat gewonnen -> Bewertung = 0
    * Die Tiefe wurde erreicht und noch kein Spieler hat gewonnen -> Bewertung = <b>averageBoard()</b>

* Wenn die Abbruchbedingung nicht gegriffen hat, wird zunächst ein Initialwert für die beste Bewertung vergeben:
    * maximize == true -> bestRes = Int.MIN_VALUE
    * maximize == false -> bestRes = Int.MIN_VALUE

* Danach folgt der "Kern" des Alpha-Beta-Algorithmus
    * Fall 1: Maximierender Spieler ist am Zug
        * (Schleifenbeginn) Es werden alle möglichen Züge in zufälliger Reihenfolge durchlaufen
        * <b>Erweiterung</b>: Ist es bereits ersichtlich, dass der nächste Zug für den maximierenden Spieler einen Gewinn bringt, wird dieser Zug und die max. Bewertung zurückgegeben
        * Alpha-Beta wird nach dem Spielen dieses Zuges rekursiv aufgerufen
        * Die Bewertung für den aktuellen Zug wird verglichen mit der maximalen Bewertung
        * Der Alpha-Beta bricht ggf. vorzeitig ab, wenn der alpha-Wert größer oder gleich dem beta-Wert ist
        * (Schleifenende)
        * <b>Erweiterung</b>: War der ermittelte beste Zug kein Gewinnzug, prüfe, ob der Gegner im nächsten Zug gewinnen kann und verhindere den Gewinn
        * Falls dieses Board oder das symmetrische Board noch nicht in der HashMap gespeichert ist, speichere dieses Board und die Bewertung

    * Fall 2: Minimierender Spieler ist am Zug
        * (Schleifenbeginn) Es werden alle möglichen Züge in zufälliger Reihenfolge durchlaufen
        * <b>Erweiterung</b>: Ist es bereits ersichtlich, dass der nächste Zug für den minimierenden Spieler einen Gewinn bringt, wird dieser Zug und die minimale Bewertung zurückgegeben
        * Alpha-Beta wird nach dem Spielen dieses Zuges rekursiv aufgerufen
        * Die Bewertung für den aktuellen Zug wird verglichen mit der minimalen Bewertung
        * Der Alpha-Beta bricht ggf. vorzeitig ab, wenn der beta-Wert kleiner oder gleich dem alpha-Wert ist
        * (Schleifenende)
        * Falls dieses Board oder das symmetrische Board noch nicht in der HashMap gespeichert ist, speichere dieses Board und die Bewertung
---

## Testfälle

Szenario |  1  |  2  |  3  |  4  |  5  | Summe
---------|-----|-----|-----|-----|-----|-------
ok       |  X  |  X  |  X  |  X  |  X  | 1

Die Tests werden wie folgt ausgeführt: In der Oberfläche auf den Button "Run Tests" klicken.
1. Die Spiel-Engine kann im nächsten Zug gewinnen (Sichttiefe 1)
2. Die Spiel-Engine kann im übernächsten Zug gewinnen (Sichttiefe 3)
3. Die Spiel-Engine kann im überübernächsten Zug gewinnen (Sichttiefe 5) 
4. Die Spiel-Engine vereitelt eine unmittelbare Gewinnbedrohung des Gegners (Sichttiefe 2)
5. Die Spiel-Engine vereitelt ein Drohung, die den Gegner im übernächsten Zug ansonsten einen
Gewinn umsetzen lässt (Sichttiefe 4)

Bei den Tests 1 bis 3 werden jeweils die besten Züge gegeneinander ausgespielt bis das Spiel vorbei ist. Bei den Tests 4 und 5 wird jeweils nur ein Zug angezeigt.

Die Testausführung protokolliert sich über die Konsole wie folgt:

    ++++++
    Test 1
    ++++++
    . . . . . X . 
    . . . . . 0 . 
    . . . 0 . X . 
    . 0 X 0 . 0 0 
    . 0 X X . X X 
    X 0 0 X 0 0 X 

    ---------------------
    Best Move: 4 (Player X)
    Depth: 1
    . . . . . X . 
    . . . . . 0 . 
    . . . 0 . X . 
    . 0 X 0 . 0 0 
    . 0 X X X X X 
    X 0 0 X 0 0 X 

    ---------------------

    ++++++
    Test 2
    ++++++
    . 0 . . . . . 
    . 0 . . . . . 
    0 X . . . . . 
    X X 0 . X X . 
    0 0 X 0 0 X 0 
    X 0 X X 0 0 X 

    ---------------------
    Best Move: 6 (Player X)
    Depth: 1
    . 0 . . . . . 
    . 0 . . . . . 
    0 X . . . . . 
    X X 0 . X X X 
    0 0 X 0 0 X 0 
    X 0 X X 0 0 X 

    ---------------------

    Best Move: 3 (Player 0)
    Depth: 2
    . 0 . . . . . 
    . 0 . . . . . 
    0 X . . . . . 
    X X 0 0 X X X 
    0 0 X 0 0 X 0 
    X 0 X X 0 0 X 

    ---------------------

    Best Move: 3 (Player X)
    Depth: 3
    . 0 . . . . . 
    . 0 . . . . . 
    0 X . X . . . 
    X X 0 0 X X X 
    0 0 X 0 0 X 0 
    X 0 X X 0 0 X 

    ---------------------

    ++++++
    Test 3
    ++++++
    . . . . . . 0 
    . . . X X . 0 
    . . . 0 0 . X 
    0 . . X X . 0 
    X . 0 X X 0 X 
    0 . 0 0 X X 0 

    ---------------------
    Best Move: 1 (Player X)
    Depth: 1
    . . . . . . 0 
    . . . X X . 0 
    . . . 0 0 . X 
    0 . . X X . 0 
    X . 0 X X 0 X 
    0 X 0 0 X X 0 

    ---------------------

    Best Move: 1 (Player 0)
    Depth: 2
    . . . . . . 0 
    . . . X X . 0 
    . . . 0 0 . X 
    0 . . X X . 0 
    X 0 0 X X 0 X 
    0 X 0 0 X X 0 

    ---------------------

    Best Move: 2 (Player X)
    Depth: 3
    . . . . . . 0 
    . . . X X . 0 
    . . . 0 0 . X 
    0 . X X X . 0 
    X 0 0 X X 0 X 
    0 X 0 0 X X 0 

    ---------------------

    Best Move: 5 (Player 0)
    Depth: 4
    . . . . . . 0 
    . . . X X . 0 
    . . . 0 0 . X 
    0 . X X X 0 0 
    X 0 0 X X 0 X 
    0 X 0 0 X X 0 

    ---------------------

    Best Move: 2 (Player X)
    Depth: 5
    . . . . . . 0 
    . . . X X . 0 
    . . X 0 0 . X 
    0 . X X X 0 0 
    X 0 0 X X 0 X 
    0 X 0 0 X X 0 

    ---------------------

    ++++++
    Test 4
    ++++++
    . . . . . . . 
    . . . . . . . 
    . . . . . . . 
    . . . . . 0 . 
    . . . X . X 0 
    X . X X 0 0 0 

    ---------------------
    Best Move: 1 (Player 0)
    Depth: 1
    . . . . . . . 
    . . . . . . . 
    . . . . . . . 
    . . . . . 0 . 
    . . . X . X 0 
    X 0 X X 0 0 0 

    ---------------------

    ++++++
    Test 5
    ++++++
    . . . . . . . 
    . . . . . . . 
    . . . X . . . 
    . . . 0 . 0 . 
    . X . X 0 0 . 
    X 0 . X 0 X 0 

    ---------------------
    Best Move: 5 (Player X)
    Depth: 1
    . . . . . . . 
    . . . . . . . 
    . . . X . X . 
    . . . 0 . 0 . 
    . X . X 0 0 . 
    X 0 . X 0 X 0 

    ---------------------

---

## Technische Umsetzung der Oberfläche
Die Oberfläche wurde in 3 Dateien (index.html,index.css,index.js) aufgeteilt.
### HTML
Da die JavaScript- sowie die CSS-Datei ausgelagert sind, ist die reine HTML-Datei recht einfach aufgebaut. Das Spielfeld besteht aus mehreren div-Elementen. Die Dreiecke, womit die Spielsteine in die Spalten gesetzt werden können werden mit der jeweiligen id (col<0|1|2|3|4|5|6>) identifiziert. Außerdem wurden für die Dreiecke onclick-Ereignisse vermerkt, welche JavaScript-Funktionen aufrufen.

Beim eigentlichen Spielfeld entsprechen id's der einzelnen Felder den dazugehörigen Bits im Bitboard.

        +---------------------+
        | 5 12 19 26 33 40 47 | 
        | 4 11 18 25 32 39 46 | 
        | 3 10 17 24 31 38 45 | 
        | 2  9 16 23 30 37 44 | 
        | 1  8 15 22 29 36 43 | 
        | 0  7 14 21 28 35 42 | 
        +---------------------+
        (Abbildung Spielfeld in HTML)

Die Auswahlmöglichkeiten für ein neues Spiel befinden sich in einem <b>fieldset</b>, welches seinerseits mehrere <b>radio buttons</b> sowie einen <b>submit button</b> enthält. Dabei löst das submit button ein onclick-Event aus.

Die restlichen drei Buttons (make best Move, Undo Move, Run Tests) sowie die Statusanzeige sind wieder als div-Elemente definiert.

### CSS
In die CSS-Datei wurden keine externen Bibliotheken eingebunden.

Im Element body sind die grundlegenden Einstellungen (Schriftart, Schriftgröße, Hintergrundfarbe etc.) hinterlegt.

Die Elemente column, column.left und column.right teilen den Bildschirmbereich auf, sodass das Spielfeld 60% von der Breite, und die daneben liegenden Schaltflächen 40% von der Breite verwenden.

Das Spielbrett wurde als grid-container realisiert, welches einzelne Felder (grid-item) sowie die buttons (grid-item-button) beinhaltet.

Auf der rechten Seite wurden beim New-Game-Bereich nur optische und farbliche Anpassungen vorgenommen, während die drei Buttons sowie die anfangs ausgeblendete Statusanzeige zu einem grid gehören (game-button-container).

### JavaScript
Durch den JavaScript-Anteil wird die Oberfläche interaktiv und kann mit dem Kotlin-Backend kommunizieren.
#### Spielzug durchführen
Wird eines der Dreiecke angeklickt, wird die <b>sendMove(field)</b>-Funktion aufgerufen. Übergeben wird die jeweilige Spalte. 
Die Funktion <b>activate_Buttons(b)</b> aktiviert oder deaktiviert die Dreiecke, mit welchen man die Spalte auswählen kann. Wird der beste Zug angefordert, werden die Dreicks-Schaltflächen gesperrt, damit der Computer seinen Zug ermitteln kann. Nach Erhalt der Antwort werden die Schaltflächen wieder mit activate_Buttons(true) aktiviert.
Wann immer der Computer seinen besten Zug berechnet, wird in der Statusanzeige "computer is generating move" angezeigt. Dafür wird in der Funktion <b>setIndicator(s)</b> der Inhalt des Element mit der id "compIndicator" verändert.
Eine globale Variable namens <b>getBestMoveReturn</b> wird gesetzt, falls nach Erhalten des HTML-Requests ein weiterer angefordert werden soll.
An sendMove wird übergeben:
* Wenn der beste Zug generiert werden soll: 'best'
* Wenn ein benutzerdefinierter Zug getätigt werden soll: < Spaltenzahl >

#### Neues Spiel Starten
Die Funktion <b>sendNewGame()</b> fordert ein neues Spiel an und bezieht sich in der Anfrage auf die radio buttons des New-Game-Feldes im HTML-Dokument. Mit der Abfrage <b>document.querySelector</b> kann geprüft werden, ob Spieler 1 bzw. Spieler 2 ein menschlicher bzw. ein computergesteuerter Spieler sein soll.

#### HTML-Response verarbeiten
Die Funktion, welche durch das Event <b>http.onreadystatechange</b> ausgelöst wird, verarbeitet hauptsächlich den response-String vom Server.
Es werden folgende Fälle geprüft:
* Falls eine Zeit übertragen wurde: Setze die Zeitanzeige auf die letzten 5 Zeichen des Strings
* Falls die ersten drei Zeichen == "win" sind: Setze die Statusanzeige auf "Player < Zahl > has won!"
* Falls beide Spieler Menschen sind: Zeige das Spielbrett und aktiviere die Dreiecke
* Falls beide Spieler Computer sind oder die getBestMoveReturn-Variable == true ist: Sende den nächsten besten Zug und zeige das Spielbrett
* Ansonsten: Setze die Statusanzeige auf null, zeige das Board und aktiviere die Dreiecke

#### Board anzeigen
In jedem HTML-Response-Text kommen die beiden Bitboards als binäre Strings vor. Die Funktion <b>displayBoard(s)</b> bekommt den Response-Text übergeben und geht jedes Zeichen durch. Dabei arbeitet die Funktion nach folgender Logik:
* Falls das aktuelle Zeichen '1' ist: Setze das Hintergrundbild des aktuellen Elementes auf 'green-circle.png' bzw. 'red-circle.png'
* Falls das aktuelle Zeichen '0' ist und das Hintergrundbild zum aktuellen Board gehört: Setze das Hintergrundbild auf null

Zudem werden in dieser Funktion die Dreiecke bearbeitet. Ist in einer Spalte das oberste Feld belegt, wird das Dreieck der zugehörigen Spalte ausgeblendet (Die Farbe des Dreiecks wird mit der Farbe des grid-containers gleichgesetzt).

### Server (App.kt)
In der Klasse App wird die Server-Seite realisiert. Die Klasse enthält ein veränderliches Board, auf welchem die Züge ausgeführt werden bzw. der beste Zug ermittelt wird.
#### Bitboard in String umwandeln
Die Funktion <b>getBitBoardString()</b> gibt zum Klassen-Board ein String zurück, welcher die beiden Long-Werte in ein Binärstring in umgekehrter Reihenfolge zurückgibt. In umgekehrer Reihenfolge bedeutet, dass das least significant Bit an der linken Stelle ist und das most significant Bit an der rechten Stelle.

#### Zeit zum Berechnen des besten Zuges ausgeben
Soll auf dem Board der beste Zug ausgeführt werden, wird die Funktion <b>getBestMoveAndTime()</b> ausgeführt. Diese Funktion gibt zwei Werte zurück:
* Den besten Zug
* Die Zeit zum Berechnen des besten Zuges

Um die Zeit zu ermitteln, wurde die Kotlin-eigene Funktion "measureNanoTime" verwendet. Zurückgegeben wird dieser Wert multipiliziert mit 10^(-9) und auf die dritte Nachkommastelle abgerundet.

## Hinweise
* Die Alpha-Beta-Funktion gibt nur korrekte Werte zurück, wenn die Tiefe eine gerade Zahl ist. Vermutlich hängt es damit zusammen, dass der Alpha-Beta nur aus der Sicht eines Spielers funktioniert.
* Die Dreiecke verändern anfangs ihre Farbe, wenn mit der Maus darübergefahren wird. Nachdem JavaScript die Farbe der Dreiecke neu setzt (also nach dem ersten Klick auf ein Dreieck) verschwindet dieses Feature.
---