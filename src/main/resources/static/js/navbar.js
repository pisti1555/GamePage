var stompClient = null;

function connectToWebSocket() {
    var socket = new SockJS('/app');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/invites', function (message) {
            console.log('Received message: ' + message.body);
            window.location.reload();
        });
    });
}

connectToWebSocket();