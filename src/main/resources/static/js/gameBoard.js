
var lastSelectedField;
var lastSelectedFieldIndex;
var fieldSelected = false;

var locations;
var connectionMap;


function newGame(gameMode) {
    fetch("http://localhost:8080/api/game/newGame", {
       method: "POST",
       credentials: 'include',
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
           },
          body: "mode=" + encodeURIComponent(gameMode)
     }).then(response => response.text())
         .then(data => {
             createBoard(gameMode);
          }).catch(error => console.error("Error:", error));
}

function loadGame() {
    var gameMode;
    fetch('http://localhost:8080/api/game/getGameMode')
        .then(response => response.json())
        .then(data => {
            if (data == 1) {
                gameMode = "pvp";
            }
            if (data == 2) {
                gameMode = "pvs";
            }
            if (data == 3) {
                gameMode = "pvf";
            }
            createBoard(gameMode);
        })
        .catch(error => console.error('Error:', error));
}

function clearBoard() {
    document.getElementById("gameboard").innerHTML = "<canvas id='canvas' width='700' height='700'></canvas>";
}

function createField(className, id, onclick) {
    var field = document.createElement("div");
    field.className = className;
    field.id = id;
    field.onclick = onclick;
    return field;
}

function createBoard(gameMode) {
    var gameboard = document.getElementById("gameboard");

    for (var i = 0; i < 28; i++) {
        var className = "subfield";
        if (i == 5 || i == 10 || i == 14 || i == 18 || i == 22 || i == 27) {
            className = "field";
        }
        var id = "field" + i;
        var field = createField(className, id, function() { handleOnClick(this.id, gameMode); });
        gameboard.appendChild(field);
    }

    prepareBoard();
}



function prepareBoard() {
    getBoardDataFromServer();
    fetchConnections();
}

function handleOnClick(selectedField, gameMode) {
    var idIndex = parseInt(selectedField.match(/\d+/)[0]);

    if(lastSelectedField == null) {
        lastSelectedFieldIndex = parseInt(idIndex);
        lastSelectedField = "field" + idIndex;
    }

    if(!fieldSelected) {
        selectField(selectedField, idIndex);
    } else {
        moveToField(lastSelectedFieldIndex, idIndex, gameMode);
    }

    fieldSelected = !fieldSelected;
}

function selectField(selectedField, selectedFieldIndex) {
    lastSelectedFieldIndex = parseInt(selectedFieldIndex);
    lastSelectedField = selectedField;

    document.getElementById(selectedField).style.background = 'rgb(0, 0, 65)';
}

function moveToField(from, to, gameMode) {
    document.getElementById(lastSelectedField).style.background = 'rgb(0, 4, 120)';

    const moveParams = new URLSearchParams();
        moveParams.append('from', from);
        moveParams.append('to', to);


    if (gameMode == 'pvp') {
        fetch("http://localhost:8080/api/game/playVsPlayer", {
       method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
           },
          body: moveParams.toString()
     }).then(response => response.text())
         .then(data => {
            gameWon(data);
            getBoardDataFromServer();
          }).catch(error => console.error("Error:", error));
    }
    else if (gameMode == 'pvs') {
        fetch("http://localhost:8080/api/game/playVsSpider", {
       method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
           },
          body: moveParams.toString()
     }).then(response => response.text())
         .then(data => {
            gameWon(data);
            getBoardDataFromServer();
          }).catch(error => console.error("Error:", error));
    }
    else if (gameMode == 'pvf') {
        fetch("http://localhost:8080/api/game/playVsFly", {
       method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
           },
          body: moveParams.toString()
     }).then(response => response.text())
         .then(data => {
            gameWon(data);
            getBoardDataFromServer();
          }).catch(error => console.error("Error:", error));
    } 
    else {
        console.log("Error while moving piece");
    }
}

function getBoardDataFromServer() {
    fetch("http://localhost:8080/api/game/getPositions", {
       method: "POST",
          headers: {
            "Content-Type": "application/json"
           },
          body: JSON.stringify({})
     }).then(response => response.json())
         .then(data => {
             locations = data;
             placePieces(locations);
          }).catch(error => console.error("Error: ", error));
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


function fetchConnections() {
    fetch('http://localhost:8080/api/game/getConnections')
        .then(response => response.json())
        .then(data => {
            console.log('Connections data:', data);

            connectionMap = data;
            processConnections();
        })
        .catch(error => console.error('Error:', error));
}

function processConnections() {
    for (const [key, value] of Object.entries(connectionMap)) {

        for (var i in value) {
            drawConnections(key, value[i]);
        }
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



function gameWon(piece) {
    let window = document.getElementById("gameOver");

    if (piece == 1) {
        fetch('http://localhost:8080/api/game/getFlyStepsDone')
        .then(response => response.json())
        .then(data => {
            console.log(data);
            window.innerHTML = "<h1>Fly won</h1> <div id='stats'><h3>Steps made: " + data + "</h3></div> <a class='button' id='quit' onclick=\"openPopUp('gameModeMain'); closePopUp('gameOver');\">Quit game</a>";
            openPopUp("gameOver");
            closePopUp("gameboard");
            clearBoard();
        }).catch(error => {
            console.error("Error:", error)
            window.innerHTML = "<h1>Fly won</h1> <a class='button' id='quit' onclick=\"openPopUp('gameModeMain'); closePopUp('gameOver');\">Quit game</a>";
            openPopUp("gameOver");
            closePopUp("gameboard");
            clearBoard();
        });
    } 
    else if (piece == 2) {
        fetch('http://localhost:8080/api/game/getSpiderStepsDone')
        .then(response => response.json())
        .then(data => {
            console.log(data);
            window.innerHTML = "<h1>Spiders won</h1> <div id='stats'><h3>Steps made: " + data + "</h3></div> <a class='button' id='quit' onclick=\"openPopUp('gameModeMain'); closePopUp('gameOver');\">Quit game</a>";
            openPopUp("gameOver");
            closePopUp("gameboard");
            clearBoard();
        }).catch(error => {
            console.error("Error:", error)
            window.innerHTML = "<h1>Spiders won</h1> <a class='button' id='quit' onclick=\"openPopUp('gameModeMain'); closePopUp('gameOver');\">Quit game</a>";
            openPopUp("gameOver");
            closePopUp("gameboard");
            clearBoard();
        });
    }
}