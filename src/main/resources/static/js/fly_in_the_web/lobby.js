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
            window.location.href = '/fly-in-the-web/game/pvp';
        });
        stompClient.subscribe('/user/topic/invites', function (message) {
            console.log('Received message: ' + message.body);
            window.location.reload();
        });
    });
}


async function getLobbyUsers() {
    try {
        const response = await fetch("/api/lobby/get-lobby-users?game=FITW");
        
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
        const response = await fetch("/api/lobby/already-invited-friend-list?game=FITW");
        
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
        const response = await fetch("/api/lobby/uninvited-friend-list?game=FITW");
        
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
            <form action="/fly-in-the-web/lobby/invite" method="post">
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
        const response = await fetch("/api/lobby/get-lobby-users?game=FITW");
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
        const response = await fetch("/api/lobby/already-invited-friend-list?game=FITW");
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
        const response = await fetch("/api/lobby/uninvited-friend-list?game=FITW");
        if (!response.ok) {
            throw new Error(`HTTP error - Status: ${response.status}`);
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching uninvited users', error);
    }
}

connectToWebSocket();
getFriendsToInvite();
