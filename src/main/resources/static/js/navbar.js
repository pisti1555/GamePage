let friendRequestList = {};
let friendRequestCount = 0;
let gameInvitationList = {};
let gameInvitationCount = 0;
let avatar = 'default';

var stompClient = null;

function connectToWebSocket() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/user/topic/invites', function (message) {
            loadNavBar();
        });
    });
}

async function declineGameInvitation(game, name) {
    await fetch('/api/lobby/decline-lobby-invitation?inviter=' + name + '&game=' + game);
    await loadNavBar();
}

async function getAvatar() {
    const response = await fetch('/nav-bar/avatar');
    const data = await response.text();
    avatar = data;
}

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
        getAvatar(),
        getFriendInvites(),
        getFriendInviteCount(),
        getGameInvites(),
        getGameInviteCount()
    ]);

    const friendRequestText = document.getElementById('no-friend-request');
    const gameInvitationText = document.getElementById('no-game-invitation');

    const avatarContainerDoc = document.getElementById('avatar-container');
    const friendRequestListDoc = document.getElementById('friend-request-list');
    const friendRequestCountDoc = document.getElementById('friend-requests-count');
    const gameInvitationListDoc = document.getElementById('game-invitation-list');
    const gameInvitationCountDoc = document.getElementById('game-invitation-count');

    //Avatar
    avatarContainerDoc.innerHTML = `
        <img src="/img/avatar/${avatar}.png"></img>
    `;

    // Friend request list
    friendRequestListDoc.innerHTML = '';
    for (let request of friendRequestList) {
        const li = document.createElement('li');
        li.innerHTML = `
            <div class="row">
                <h3 class="requestMessage">${request} has sent a friend request</h3>
                <form action="/friends/add" method="post">
                    <input type="hidden" name="invited" value="${request}" />
                    <button class="nav-button" type="submit">Accept</button>
                </form>
                <form action="/friends/decline-friend-request" method="post">
                    <input type="hidden" name="inviter" value="${request}" />
                    <button class="nav-button" type="submit">Decline</button>
                </form>
            </div>
        `;
        friendRequestListDoc.appendChild(li);
    }

    // Friend request count
    friendRequestCountDoc.textContent = friendRequestCount;
    friendRequestText.textContent = 'You do not have any friend request';
    if (friendRequestCount > 0) {
        friendRequestCountDoc.style.display = 'flex';
        friendRequestText.style.display = 'none';
    } else {
        friendRequestCountDoc.style.display = 'none';
        friendRequestText.style.display = 'flex';
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
                    <button class="nav-button" type="submit">Join</button>
                </form>
                <button class="nav-button" onclick="declineGameInvitation('${invite.game}', '${invite.inviter}')">Decline</button>
            </div>
        `;
        }
        if (invite.game == "TicTacToe") {
            li.innerHTML = `
            <div class="row">
                <h3 class="invMessage">${invite.inviter} invited you to play ${invite.game}</h3>
                <form action="/tic-tac-toe/join" method="post">
                    <input type="hidden" name="inviter" value="${invite.inviter}" />
                    <button class="nav-button" type="submit">Join</button>
                </form>
                <button class="nav-button" onclick="declineGameInvitation('${invite.game}', '${invite.inviter}')">Decline</button>
            </div>
        `;
        }
        
        gameInvitationListDoc.appendChild(li);
    }

    // Game invitation count
    gameInvitationText.textContent = 'You do not have any invitation';
    gameInvitationCountDoc.textContent = gameInvitationCount;
    if (gameInvitationCount > 0) {
        
        gameInvitationCountDoc.style.display = 'flex';
        gameInvitationText.style.display = 'none';
    } else {
        gameInvitationCountDoc.style.display = 'none';
        gameInvitationText.style.display = 'flex';
    }
}

loadNavBar();
connectToWebSocket();