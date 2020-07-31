var http = new XMLHttpRequest();
let getBestMoveReturn = false;

//Sends Move to Server and sets Indicator
function sendMove(field){activate_Buttons(false);
    if(field=='best'){send('bestMove');getBestMoveReturn=true;setIndicator('computer is generating move...');}else{send('move?col=' + field);}
    if (!(document.querySelector('input[name=first]:checked').value == "human" && document.querySelector('input[name=second]:checked').value == "human")){
    getBestMoveReturn=true;setIndicator('computer is generating move...');}}

//Activates or deactivates the buttons to make moves
function activate_Buttons(b){
    for (i = 0; i<=6; i++){
        if (b==true) {
            document.getElementById("col" + i).setAttribute("onClick", "sendMove(" + i + ");" );}
        else {document.getElementById("col" + i).onclick = null; }}}

//Sets Indicator to given String
function setIndicator(s){document.getElementById("compIndicator").innerHTML = s}

//Sends any HTTP Repuest
function send(field){http.open('GET',field);http.send();}

//Get New Game
function sendNewGame(){
    setIndicator('');
    send('newGame?first=' + document.querySelector('input[name=first]:checked').value +
        '&second=' + document.querySelector('input[name=second]:checked').value);}


/*
    Server has sent response
    → If time was submitted, set timestamp
    → If a player has won, set Indicator to winning player
    → Send another request, if opponent is computer
    → If it's humans turn, activate buttons to make moves and set Indicator to empty String
*/
http.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
        var s = this.responseText;
        if (this.readyState == 4 && this.status == 200) {
            var s = this.responseText;
            if(s.substr(s.length-4,1)=="."){document.getElementById("time").innerHTML=s.substr(s.length-5,s.length)+' s';}
            if (s.substr(0,3) == "win"){
                displayBoard(s.substr(6,s.length));
                if (s.substr(5,1) == "t") setIndicator('Player 1 has won!');
                if (s.substr(5,1) == "f") setIndicator('Player 2 has won!');
                activate_Buttons(true);
            }else if (document.querySelector('input[name=first]:checked').value == "human" && document.querySelector('input[name=second]:checked').value == "human"){
                setIndicator('');displayBoard(s);activate_Buttons(true);
            }else if (document.querySelector('input[name=first]:checked').value == "comp" &&
                    document.querySelector('input[name=second]:checked').value == "comp" || (getBestMoveReturn == true)){
                send('bestMoveReturn');displayBoard(s);
                if(!(document.querySelector('input[name=first]:checked').value == "human" && document.querySelector('input[name=second]:checked').value == "human")){
                    getBestMoveReturn=false;}
            }else{
                setIndicator('');displayBoard(s);activate_Buttons(true);}};}}

/*
    Sets background images for board
    → function receives 2 bitboards from server as string with binary digits
    → sets first bitboard items (resposeText substring from 7 to 54) to green circle
    → sets second bitboard items (responseText substring from 63 to 110) to red circle
    → deactivates button triangles if height is reached
*/
function displayBoard(s){
    for(i = 7; i <= 54; i++){if (document.body.contains(document.getElementById(i-7))){
                if (s.substr(i,1)=='1'){document.getElementById(i-7).style="background-image: url('green-circle.png');";
                }else if (s.substr(i,1)=='0' && document.getElementById(i-7).style.backgroundImage=='url("green-circle.png")'){
                    document.getElementById(i-7).style="background-image: url('');";}}}
        for(i = 63; i <= 110; i++){
            if (document.body.contains(document.getElementById(i-63))){if (s.substr(i,1)=='1'){
                document.getElementById(i-63).style="background-image: url('red-circle.png') ;";
                }else if (s.substr(i,1)=='0' && document.getElementById(i-63).style.backgroundImage=='url("red-circle.png")'){
                document.getElementById(i-63).style="background-image: url('');";}} }
        for (i = 0; i<= 6; i++){
            if (document.body.contains(document.getElementById(5+i*7))){
                if(document.getElementById(5+i*7).style.backgroundImage=='url("green-circle.png")' ||
                    document.getElementById(5+i*7).style.backgroundImage=='url("red-circle.png")'){
                    document.getElementById("col" + i).style.borderTopColor='rgb(55, 102, 37)'
                }else{
                    document.getElementById("col" + i).style.borderTopColor='rgb(136, 212, 35)' }}} }        