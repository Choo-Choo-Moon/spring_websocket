const REQUEST_CREATE_ROOM = "createRoom"
const REQUEST_JOIN_ROOM = "joinRoom"
const REQUEST_LEAVE_ROOM = "leaveRoom"

const RECEIVE_JOINED_LOBBY = "joinedLobby"
const RECEIVE_CREATED_ROOM = "createdRoom"
const RECEIVE_JOINED_ROOM = "joinedRoom"
const RECEIVE_LEAVED_ROOM = "leavedRoom"
const RECEIVE_DISCONNECTED = "disconnected"
const RECEIVE_MESSAGE = "msg"

let ws
let messageCallback

const state = {
    get isConnected() {
        return (ws && 1 === ws.readyState)
    }
}

const connect = (event) => {
    if (ws && 1 === ws.readyState) {
        ws.close(1000, "bye")
    }

    ws = new WebSocket("ws://localhost:8080/ws/multi")

    ws.onopen = () => {
        if (event) {
            event()
        }
    }
    ws.onmessage = (e) => {
        const message = JSON.parse(e.data)
        if (messageCallback) {
            messageCallback(message)
        }
    }
    ws.onclose = () => {
        ws = null;
    }
}

const onMessage = (callback) => {
    messageCallback = callback
}

const disconnect = () => {
    if (state.isConnected) {
        ws.onclose(1000, "bye")
    }
}

const createRoom = (roomName) => {
    if (state.isConnected) {
        const message = {
            cmd: REQUEST_CREATE_ROOM,
            payload: {
                roomName: roomName,
            },
        }
        sendMessage(message)
    }
}

const joinRoom = (roomName) => {
    if (state.isConnected) {
        const message = {
            cmd: REQUEST_JOIN_ROOM,
            payload: {
                roomName: roomName,
            },
        }
        sendMessage(message)
    }
}

const leaveRoom = (roomName) => {
    if (state.isConnected) {
        const message = {
            cmd: REQUEST_LEAVE_ROOM,
            payload: {
                roomName: roomName,
            },
        }
        sendMessage(message)
    }
}

const sendMessage = (message) => {
    if (state.isConnected) {
        ws.send(JSON.stringify(message))
    }
}

export {connect, disconnect, createRoom, joinRoom, leaveRoom, sendMessage, onMessage,}
export {
    RECEIVE_JOINED_LOBBY,
    RECEIVE_CREATED_ROOM,
    RECEIVE_JOINED_ROOM,
    RECEIVE_LEAVED_ROOM,
    RECEIVE_DISCONNECTED,
    RECEIVE_MESSAGE,
    state,
}