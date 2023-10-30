// Precondition: You need to require socket.io.js in your html page
// Reference link https://socket.io
// <script src="socket.io.js"></script>

const gameId = '63ee9ff4-40e0-4f82-adef-5ae7c0c6fba7';
const playerId = 'player1-xxx-xxx-xxx';

// Connecto to API App server
const apiServer = 'https://codefest.techover.io';
const socket = io.connect(apiServer, { reconnect: true, transports: ['websocket'] });


// LISTEN SOCKET.IO EVENTS

// It it required to emit `join channel` event every time connection is happened
socket.on('connect', () => {
    document.getElementById('connected-status').innerHTML = 'ON';
    document.getElementById('socket-status').innerHTML = 'Connected';
    console.log('[Socket] connected to server');    
    // API-1a
    socket.emit('join game', { game_id: gameId, player_id: playerId });
});

socket.on('disconnect', () => {
    console.warn('[Socket] disconnected');
    document.getElementById('socket-status').innerHTML = 'Disconnected';
});

socket.on('connect_failed', () => {
    console.warn('[Socket] connect_failed');
    document.getElementById('socket-status').innerHTML = 'Connected Failed';
});


socket.on('error', (err) => {
    console.error('[Socket] error ', err);
    document.getElementById('socket-status').innerHTML = 'Error!';
});


// SOCKET EVENTS

// API-1b
socket.on('join game', (res) => {
    console.log('[Socket] join-game responsed', res);
    document.getElementById('joingame-status').innerHTML = 'ON';
});

//API-2
socket.on('ticktack player', (res) => {
    console.info('> ticktack');
    console.log('[Socket] ticktack-player responsed, map_info: ', res.map_info);
    document.getElementById('ticktack-status').innerHTML = 'ON';
});

// API-3a
// socket.emit('drive player', { direction: '111b333222' });

//API-3b
socket.on('drive player', (res) => {
    console.log('[Socket] drive-player responsed, res: ', res);
});