//-------------- WebSocket --------------

var stompClient = null;

function connectToWebSocket() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/game/update', function (message) {
            console.log('Received message: ' + message.body);
            window.location.reload();
        });
        stompClient.subscribe('/user/topic/game/return-to-lobby', function (message) {
            console.log('Received message: ' + message.body);
            window.location.reload();
        });
    });
}
connectToWebSocket();


var lastSelectedField;
var lastSelectedFieldIndex;
var fieldSelected = false;

var locations;
var connectionMap;


//-------------- Load board data --------------
function loadGame() {
    fetch('/api/game/pvp/getGameMode');
    createBoard(); 
}

function getBoardDataFromServer() {
    fetch("/api/game/pvp/getPositions", {
       method: "POST",
          headers: {
            "Content-Type": "application/json"
           },
          body: JSON.stringify({})
     }).then(response => response.json())
         .then(data => {
             placePieces(data);
          });
}

function fetchConnections() {
    fetch('/api/game/pvp/getConnections')
        .then(response => response.json())
        .then(data => {
            connectionMap = data;
            processConnections();
        });
}

function processConnections() {
    for (const [key, value] of Object.entries(connectionMap)) {
        for (var i in value) {
            drawConnections(key, value[i]);
        }
    }
}


//-------------- Draw board --------------

function clearBoard() {
    document.getElementById("gameboard").innerHTML = "<canvas id='canvas' width='700' height='700'></canvas>";
}

function createBoard() {
    var gameboard = document.getElementById("gameboard");

    for (var i = 0; i < 28; i++) {
        var className = "subfield";
        if (i == 0 || i == 5 || i == 10 || i == 14 || i == 18 || i == 22 || i == 27) {
            className = "field";
        }
        var id = "field" + i;
        var field = createField(className, id, function() { handleOnClick(this.id); });
        gameboard.appendChild(field);
    }

    getBoardDataFromServer();
    fetchConnections();
}

function createField(className, id, onclick) {
    var field = document.createElement("div");
    field.className = className;
    field.id = id;
    field.onclick = onclick;
    return field;
}

function placePieces(pieceLocations) {
    var flyHTML = '<img src="/img/fly.png" alt="fly" class="piece"/>';
    var spiderHTML = '<img src="/img/spider.png" alt="spider" class="piece"/>';

    for (var i = 0; i <= 27; i++) {
        document.getElementById("field" + i).innerHTML = "";
      }

    var fieldIDs = [];
    for (var i = 0; i < pieceLocations.length; i++) {
        fieldIDs.push("field" + pieceLocations[i]);
      }

      document.getElementById(fieldIDs[0]).innerHTML = flyHTML;
    for (var i = 1; i < fieldIDs.length; i++) {
        document.getElementById(fieldIDs[i]).innerHTML = spiderHTML;
    }
}

function drawConnections(from ,to) {
    var canvas = document.getElementById("canvas");
        var ctx = canvas.getContext("2d");

        var field1 = document.getElementById("field" + from);
        var field2 = document.getElementById("field" + to);

        var x1 = field1.offsetLeft;
        var y1 = field1.offsetTop;

        var x2 = field2.offsetLeft;
        var y2 = field2.offsetTop;

        ctx.beginPath();
        ctx.moveTo(x1, y1);
        ctx.lineTo(x2, y2);
        ctx.stroke();
}



//-------------- Gameplay --------------

function handleOnClick(selectedField) {
    var idIndex = parseInt(selectedField.match(/\d+/)[0]);

    if(lastSelectedField == null) {
        lastSelectedFieldIndex = parseInt(idIndex);
        lastSelectedField = "field" + idIndex;
    }

    if(!fieldSelected) {
        selectField(selectedField, idIndex);
    } else {
        moveToField(lastSelectedFieldIndex, idIndex);
    }

    fieldSelected = !fieldSelected;
}

function selectField(selectedField, selectedFieldIndex) {
    lastSelectedFieldIndex = parseInt(selectedFieldIndex);
    lastSelectedField = selectedField;

    document.getElementById(selectedField).style.background = 'rgb(0, 0, 65)';
}

function moveToField(from, to) {
    document.getElementById(lastSelectedField).style.background = 'rgb(0, 4, 120)';

    const moveParams = new URLSearchParams();
        moveParams.append('from', from);
        moveParams.append('to', to);

        fetch("/api/game/pvp/move", {
       method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
           },
          body: moveParams.toString()
     }).then(response => response.text())
         .then(data => {
            getBoardDataFromServer();
          });
    
}

createBoard();