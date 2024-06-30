let friendRequestList = {};
let friendRequestCount = 0;
let gameInvitationList = {};
let gameInvitationCount = 0;

var stompClient = null;

function connectToWebSocket() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/invites', function (message) {
            console.log('Received message: ' + message.body);
            loadNavBar();
        });
    });
}
connectToWebSocket();

async function getFriendInvites() {
    const response = await fetch('/nav-bar/friend-invites');
    const data = await response.json();
    friendRequestList = data;
}

async function getFriendInviteCount() {
    const response = await fetch('/nav-bar/friend-invites-count');
    const data = await response.json();
    friendRequestCount = data;
}

async function getGameInvites() {
    const response = await fetch('/nav-bar/game-invites');
    const data = await response.json();
    gameInvitationList = data;
}

async function getGameInviteCount() {
    const response = await fetch('/nav-bar/game-invites-count');
    const data = await response.json();
    gameInvitationCount = data;
}

async function loadNavBar() {
    await Promise.all([
        getFriendInvites(),
        getFriendInviteCount(),
        getGameInvites(),
        getGameInviteCount()
    ]);

    const friendRequestText = document.getElementById('no-friend-request');
    const gameInvitationText = document.getElementById('no-game-invitation');

    const friendRequestListDoc = document.getElementById('friend-request-list');
    const friendRequestCountDoc = document.getElementById('friend-requests-count');
    const gameInvitationListDoc = document.getElementById('game-invitation-list');
    const gameInvitationCountDoc = document.getElementById('game-invitation-count');

    // Friend request list
    friendRequestListDoc.innerHTML = '';
    for (let request of friendRequestList) {
        const li = document.createElement('li');
        li.innerHTML = `
            <div class="row">
                <h3 class="requestMessage">${request} has sent a friend request</h3>
                <form action="/friends/add" method="post">
                    <input type="hidden" name="invited" value="${request}" />
                    <button class="button" type="submit">Accept</button>
                </form>
                <form action="/friends/decline-friend-request" method="post">
                    <input type="hidden" name="inviter" value="${request}" />
                    <button class="button" type="submit">Decline</button>
                </form>
            </div>
        `;
        friendRequestListDoc.appendChild(li);
    }

    // Friend request count
    if (friendRequestCount > 0) {
        friendRequestCountDoc.textContent = friendRequestCount;
        friendRequestCountDoc.style.display = 'flex';
        friendRequestText.style.display = 'none';
    } else {
        friendRequestText.textContent = 'You do not have any friend request';
    }

    // Game invitation list
    gameInvitationListDoc.innerHTML = '';
    for (let invite of gameInvitationList) {
        const li = document.createElement('li');
        if (invite.game == "FITW") {
            li.innerHTML = `
            <div class="row">
                <h3 class="invMessage">${invite.inviter} invited you to play ${invite.game}</h3>
                <form action="/fly-in-the-web/lobby/join" method="post">
                    <input type="hidden" name="inviter" value="${invite.inviter}" />
                    <button class="button" type="submit">Join</button>
                </form>
                <form action="/fly-in-the-web/lobby/decline-lobby-invitation" method="post">
                    <input type="hidden" name="inviter" value="${invite.inviter}" />
                    <button class="button" type="submit">Decline</button>
                </form>
            </div>
        `;
        }
        if (invite.game == "TicTacToe") {
            li.innerHTML = `
            <div class="row">
                <h3 class="invMessage">${invite.inviter} invited you to play ${invite.game}</h3>
                <form action="/tic-tac-toe/join" method="post">
                    <input type="hidden" name="inviter" value="${invite.inviter}" />
                    <button class="button" type="submit">Join</button>
                </form>
                <form action="/tic-tac-toe/decline-lobby-invitation" method="post">
                    <input type="hidden" name="inviter" value="${invite.inviter}" />
                    <button class="button" type="submit">Decline</button>
                </form>
            </div>
        `;
        }
        
        gameInvitationListDoc.appendChild(li);
    }

    // Game invitation count
    if (gameInvitationCount > 0) {
        gameInvitationCountDoc.textContent = gameInvitationCount;
        gameInvitationCountDoc.style.display = 'flex';
        gameInvitationText.style.display = 'none';
    } else {
        gameInvitationText.textContent = 'You do not have any invitation';
    }
}

loadNavBar();
