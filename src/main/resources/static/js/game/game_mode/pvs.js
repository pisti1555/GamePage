function moveToField(from, to) {
    document.getElementById(lastSelectedField).style.background = 'rgb(0, 4, 120)';

    const moveParams = new URLSearchParams();
        moveParams.append('from', from);
        moveParams.append('to', to);


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

