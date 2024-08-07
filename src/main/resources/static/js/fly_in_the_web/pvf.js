var lastSelectedField;
var lastSelectedFieldIndex;
var fieldSelected = false;

var locations;
var connectionMap;


function newGame() {
  fetch("/fly-in-the-web/api/game/pvc/new-game", {
     method: "POST",
     credentials: 'include',
        headers: {
          "Content-Type": "application/x-www-form-urlencoded"
         },
        body: "mode=" + encodeURIComponent("pvf")
   });
   createBoard();
}


//-------------- Load board data --------------

function getBoardDataFromServer() {
    fetch("/fly-in-the-web/api/game/pvc/get-positions", {
       method: "POST",
          headers: {
            "Content-Type": "application/json"
           },
          body: JSON.stringify({})
     }).then(response => response.json())
         .then(data => {
            locations = data;
             placePieces(data);
          });
}

function fetchConnections() {
    fetch('/fly-in-the-web/api/game/pvc/get-connections')
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
    var flyHTML = '<img src="/img/fly_in_the_web/fly.png" alt="fly" class="piece"/>';
    var spiderHTML = '<img src="/img/fly_in_the_web/spider.png" alt="spider" class="piece"/>';

    for (var i = 0; i <= 27; i++) {
        document.getElementById("field" + i).innerHTML = "";
    }

    document.getElementById('field' + pieceLocations[0]).innerHTML = flyHTML;

    for (var i = 1; i < pieceLocations.length; i++) {
        document.getElementById('field' + pieceLocations[i]).innerHTML = spiderHTML;
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
}

function selectField(selectedField, selectedFieldIndex) {
    var correctLocation = false;

    for (let i = 0; i < locations.length; i++) {
        if (locations[i] == selectedFieldIndex) {
            correctLocation = true;
            fieldSelected = true;
        }
    }

    if (correctLocation) {
        lastSelectedFieldIndex = parseInt(selectedFieldIndex);
        lastSelectedField = selectedField;
        document.getElementById(selectedField).classList.add('selected');
    }
}

function moveToField(from, to) {
    document.getElementById(lastSelectedField).classList.remove('selected');
    fieldSelected = false;

    const moveParams = new URLSearchParams();
        moveParams.append('from', from);
        moveParams.append('to', to);

        fetch("/fly-in-the-web/api/game/pvc/pvf", {
       method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
           },
          body: moveParams.toString()
     }).then(response => response.text())
         .then(data => {
          if(data == 1 || data == 2) {
            gameWon(data);
          } else {
            getBoardDataFromServer();
          }
          });
    
}

function gameWon(piece) {
  let text = document.getElementById("won");

  if (piece == 1) {
    text.innerText = "Fly won!"
    openPopUp("gameOver");
    closePopUp("gameboard");
    clearBoard();
  } 
  else if (piece == 2) {
    text.innerText = "Spiders won!"
    openPopUp("gameOver");
    closePopUp("gameboard");
    clearBoard();
  }
}

newGame();