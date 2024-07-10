var playersTurn = 'X';
const boardContainer = document.getElementById('board-container');
const playersTurnText = document.getElementById('playersTurnText');
const board = document.getElementById('gameboard');
const fields = document.querySelectorAll('.field');
const gameOverScreen = document.getElementById('game-over-screen');
const whoWonText = document.getElementById('whoWonText');

playersTurnText.textContent = playersTurn + " 's turn";

fields.forEach(field => {
    field.addEventListener("click", () => click(field));
})

function click(field) {
    if (!field.classList.contains('X')
        && !field.classList.contains('O')) {
            const fieldID = field.id;
            const[idRow, idCol] = fieldID.split('-');
            const row = parseInt(idRow);
            const col = parseInt(idCol);

            try {
                movePvC(row, col);
            } catch (error) {
                throw new Error;
            }
    }
}

async function updateBoard() {
    await fetch("/tic-tac-toe/api/game/get-positions-pvc")
    .then(response => response.json())
          .then(data => {
              placePieces(data);
           });
}

async function movePvC(row, col) {
    const moveParams = new URLSearchParams();
        moveParams.append('row', row);
        moveParams.append('col', col);

        await fetch("/tic-tac-toe/api/game/move-pvc", {
       method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
           },
          body: moveParams.toString()
     }).then(response => response.text())
         .then(data => {
            if(data) {
                updateBoard();
                gameWon();
            } else {
                updateBoard();
            }
          });
}

function placePieces(locations) {
    for (var i = 0; i < 3; i++) {
        for (var j = 0; j < 3; j++) {
            const fieldID = document.getElementById(i + '-' + j);
            if (fieldID.classList.contains('X')) {
                fieldID.classList.remove('X');
            }
            if (fieldID.classList.contains('O')) {
                fieldID.classList.remove('O');
            }
            if (locations[i][j] == 1) fieldID.classList.add('X');
            if (locations[i][j] == 2) fieldID.classList.add('O');
        }
    }
}

async function gameWon() {
    await fetch('/tic-tac-toe/api/game/which-won-pvc')
    .then(response => response.json())
    .then(data => {
        if (data == 1) {
            gameOverScreen.classList.remove('hidden');
            whoWonText.textContent = "X Won!"
        } else if (data == 2) {
            gameOverScreen.classList.remove('hidden');
            whoWonText.textContent = "O Won!"
        } else if(data == 3) {
            gameOverScreen.classList.remove('hidden');
            whoWonText.textContent = "Draw"
        }
    });
}

async function newGame() {
    fetch('/tic-tac-toe/api/game/new-game-pvc');
    await updateBoard();
    gameOverScreen.classList.add('hidden');
    await updateBoard();
}