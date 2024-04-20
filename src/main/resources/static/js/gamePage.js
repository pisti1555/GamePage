function checkForAnyUnfinishedGame() {
    let window = document.getElementById("gameFoundPopUp");

    fetch('http://localhost:8080/api/game/isGameRunning')
        .then(response => response.json())
        .then(data => {
            if (data == true) {
                openPopUp("gameFoundPopUp");
                closePopUp("gameModeMain");
            }
        });
}

function gameWon(piece) {
    let window = document.getElementById("gameOver");

    if (piece == 1) {
        fetch('http://localhost:8080/api/game/getFlyStepsDone')
        .then(response => response.json())
        .then(data => {
            window.innerHTML = "<h1>Fly won</h1> <div id='stats'><h3>Steps made: " + data + "</h3></div> <a class='button' id='quit' onclick=\"openPopUp('gameModeMain'); closePopUp('gameOver');\">Quit game</a>";
            openPopUp("gameOver");
            closePopUp("gameboard");
            clearBoard();
        });
    } 
    else if (piece == 2) {
        fetch('http://localhost:8080/api/game/getSpiderStepsDone')
        .then(response => response.json())
        .then(data => {
            window.innerHTML = "<h1>Spiders won</h1> <div id='stats'><h3>Steps made: " + data + "</h3></div> <a class='button' id='quit' onclick=\"openPopUp('gameModeMain'); closePopUp('gameOver');\">Quit game</a>";
            openPopUp("gameOver");
            closePopUp("gameboard");
            clearBoard();
        });
    }
}