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
            <div class="image-container">
                <img src="/img/avatar/${request.avatar}.png" />
            </div>
            <h3 class="requestMessage">${request.username} has sent a friend request</h3>
            <button class="nav-button" onclick="acceptFriendRequest('${request.username}')">Accept</button>
            <button class="nav-button" onclick="declineFriendRequest('${request.username}')">Decline</button>
        `;
        friendRequestListDoc.appendChild(li);
    }

    // Friend request count
    friendRequestCountDoc.textContent = friendRequestCount;
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
                <div class="image-container">
                    <img src="/img/avatar/${invite.inviter.avatar}.png" />
                </div>
                <h3 class="invMessage">${invite.inviter.username} invited you to play ${invite.game}</h3>
                <form action="/fly-in-the-web/lobby/join" method="post">
                    <input type="hidden" name="inviter" value="${invite.inviter.username}" />
                    <button class="nav-button" type="submit">Join</button>
                </form>
                <button class="nav-button" onclick="declineGameInvitation('${invite.game}', '${invite.inviter.username}')">Decline</button>
            `;
        }
        if (invite.game == "TicTacToe") {
            li.innerHTML = `
                <div class="image-container">
                    <img src="/img/avatar/${invite.inviter.avatar}.png" />
                </div>
                <h3 class="invMessage">${invite.inviter.username} invited you to play ${invite.game}</h3>
                <form action="/tic-tac-toe/join" method="post">
                    <input type="hidden" name="inviter" value="${invite.inviter.username}" />
                    <button class="nav-button" type="submit">Join</button>
                </form>
                <button class="nav-button" onclick="declineGameInvitation('${invite.game}', '${invite.inviter.username}')">Decline</button>
            `;
        }
        
        gameInvitationListDoc.appendChild(li);
    }

    // Game invitation count
    gameInvitationCountDoc.textContent = gameInvitationCount;
    if (gameInvitationCount > 0) {
        
        gameInvitationCountDoc.style.display = 'flex';
        gameInvitationText.style.display = 'none';
    } else {
        gameInvitationCountDoc.style.display = 'none';
        gameInvitationText.style.display = 'flex';
    }
}

async function acceptFriendRequest(username) {
    try {
        await fetch('/friends/add?invited=' + username);
        await loadNavBar();
    } catch (error) {
        console.error('Error sending friend request:', error);
    }
}

async function declineFriendRequest(username) {
    try {
        await fetch('/friends/decline-friend-request?inviter=' + username);
        await loadNavBar();
    } catch (error) {
        console.error('Error deleting friend:', error);
    }
}

loadNavBar();
connectToWebSocket();