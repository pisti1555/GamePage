var stompClient = null;

function connectToWebSocket() {
    var socket = new SockJS('/app');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/lobby', function (message) {
            console.log('Received message: ' + message.body);
            window.location.reload();
        });
        stompClient.subscribe('/topic/lobby/start', function (message) {
            console.log('Received message: ' + message.body);
        });
    });
}

connectToWebSocket();