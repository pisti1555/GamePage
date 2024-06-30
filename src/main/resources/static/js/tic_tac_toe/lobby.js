var stompClient = null;

function connectToWebSocket() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/lobby/update', function (message) {
            console.log('Received message: ' + message.body);
            window.location.reload();
        });
        stompClient.subscribe('/user/topic/lobby/start', function (message) {
            console.log('Received message: ' + message.body);
            window.location.href = '/tic-tac-toe/game/pvp';
        });
        stompClient.subscribe('/user/topic/invites', function (message) {
            console.log('Received message: ' + message.body);
            window.location.reload();
        });
    });
}

connectToWebSocket();