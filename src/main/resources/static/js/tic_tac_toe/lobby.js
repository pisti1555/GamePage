var stompClient = null;

async function connectToWebSocket() {
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
        stompClient.subscribe('/user/topic/chat/lobby', function (message) {
            console.log('Received message: ' + message.body);
            getMessages();
        });
    });
}

async function getLobbyUsers() {
    try {
        const response = await fetch("/api/lobby/get-lobby-users?game=TicTacToe");
        
        if (!response.ok) {
            throw new Error(`HTTP error - Status: ${response.status}`);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching data', error);
    }
}

async function getInvitedUsers() {
    try {
        const response = await fetch("/api/lobby/already-invited-friend-list?game=TicTacToe");
        
        if (!response.ok) {
            throw new Error(`HTTP error - Status: ${response.status}`);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching data', error);
    }
}

async function getUninvitedUsers() {
    try {
        const response = await fetch("/api/lobby/uninvited-friend-list?game=TicTacToe");
        
        if (!response.ok) {
            throw new Error(`HTTP error - Status: ${response.status}`);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching data', error);
    }
}

async function getFriendsToInvite() {
    const lobbyUsers = await getLobbyUsers();
    const alreadyInvitedFriends = await getInvitedUsers();
    const uninvitedFriends = await getUninvitedUsers();
    
    let friendList = document.getElementById('friend-list');
    friendList.innerHTML = '';

    if (lobbyUsers != null && lobbyUsers[1] != null) {
        const span = document.createElement('span');
        span.textContent = 'Can not invite friends because the lobby is full';
        friendList.appendChild(span);
        return null;
    }

    for (let friend of alreadyInvitedFriends) {
        if (friend == lobbyUsers[0] || friend == lobbyUsers[1]) continue;
            const li = document.createElement('li');
            li.innerHTML = `
                <h3>${friend}</h3>
                <button disabled class="button-disabled">Invited</button>
            `;
            friendList.appendChild(li);
    }
    for (let friend of uninvitedFriends) {
        if (friend == lobbyUsers[0] || friend == lobbyUsers[1]) continue;
        const li = document.createElement('li');
        li.innerHTML = `
            <h3>${friend}</h3>
            <form action="/tic-tac-toe/invite" method="post">
                <input type="hidden" name="friendUsername" value="${friend}"/>
                <button class="button" type="submit">Invite</button>
            </form>
        `;
        friendList.appendChild(li);
    }
}

// --------------------- Lobby status --------------------------

async function getLobbyUsers() {
    try {
        const response = await fetch("/api/lobby/get-lobby-users?game=TicTacToe");
        if (!response.ok) {
            throw new Error(`HTTP error - Status: ${response.status}`);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching lobby users', error);
    }
}

async function getInvitedUsers() {
    try {
        const response = await fetch("/api/lobby/already-invited-friend-list?game=TicTacToe");
        if (!response.ok) {
            throw new Error(`HTTP error - Status: ${response.status}`);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching invited users', error);
    }
}

async function getUninvitedUsers() {
    try {
        const response = await fetch("/api/lobby/uninvited-friend-list?game=TicTacToe");
        if (!response.ok) {
            throw new Error(`HTTP error - Status: ${response.status}`);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching uninvited users', error);
    }
}




// ------------------------------- Chat ---------------------------------------

async function getMessages() {
    const lobby = await getLobbyUsers();
    const username = lobby[0];
    try {
        const response = await fetch("/chat/get-lobby-chat?game=TicTacToe");
        if (!response.ok) {
            throw new Error(`HTTP error - Status: ${response.status}`);
        }
        const data = await response.json();
        const chatContent = document.getElementById('chat-content');
        chatContent.innerHTML = '';
        for (var i = 0; i < data.length; i++) {
            let li = document.createElement('li');
            if (username == data[i].username) {
                console.log('ugyanaz');
                li.innerHTML = `
                    <span class="sender right-side">${data[i].username}</span>
                    <span class="message right-side">${data[i].message}</span>
                `;
            } else {
                console.log('nem ugyanaz');
                console.log(username);
                console.log('data username: ' + data.username);
                li.innerHTML = `
                    <span class="sender">${data[i].username}</span>
                    <span class="message">${data[i].message}</span>
                `;
            }
            
            chatContent.appendChild(li);
        }
    } catch (error) {
        console.error('Error fetching chat messages', error);
    }
}


async function sendMessage() {
    const message = document.getElementById('message-content').value;
    if (message == '') return null;

    const params = new URLSearchParams();
    params.append('message', message);
    params.append('game', 'TicTacToe');
    await fetch("/chat/send-message-to-lobby", {
        method: "POST",
           headers: {
             "Content-Type": "application/x-www-form-urlencoded"
            },
           body: params.toString()
      });

      document.getElementById('message-content').value = '';
      getMessages();
}

document.addEventListener("DOMContentLoaded", function() {
    var chatContent = document.getElementById('chat-content');
    chatContent.scrollTop = chatContent.scrollHeight;

    var messageInput = document.getElementById('message-content');
    messageInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            sendMessage();
        }
    });
});




connectToWebSocket();
getFriendsToInvite();
getMessages();