'use strict';

(function () {
    var header = document.getElementById('header');
    var content = document.getElementById('content');
    var input = document.getElementById('input');
    var status = document.getElementById('status');
    var myName = false;
    var author = null;
    var logged = false;
    var socket = atmosphere;
    var subSocket, transport;

    var request = {
        url: '/async/subscribe',
        contentType: "application/json",
        logLevel: 'debug',
        transport: 'websocket',
        fallbackTransport: 'long-polling',
        trackMessageLength: true,
        reconnectInterval: 5000
    };

    request.onOpen = function (response) {
        console.log('Trying to use transport: ' + response.transport);
        content.innerHTML = '<p>Atmosphere connected using ' + response.transport + '</p>';
        input.removeAttribute('disabled');
        status.innerHTML = 'Choose name:';
        transport = response.transport;
        // Carry the UUID. This is required if you want to call
        // subscribe(request) again.
        request.uuid = response.request.uuid;
    };

    request.onClientTimeout = function (r) {
        content.innerHTML = '<p>Client closed the connection after a timeout. Reconnecting in ' + request.reconnectInterval + '</p>';
        subSocket.push(atmosphere.util
                .stringifyJSON({
                    author : author,
                    message : 'is inactive and closed the connection. Will reconnect in '
                    + request.reconnectInterval
                }));
        input.setAttribute('disabled', 'disabled');
        setTimeout(function() {
            subSocket = socket.subscribe(request);
        }, request.reconnectInterval);
    };

    request.onClose = function (response) {
        content.innerHTML = '<p>Server closed the connection after a timeout</p>';
        if (subSocket) {
            subSocket.push(atmosphere.util.stringifyJSON({
                author : author,
                message : 'disconnecting'
            }));
        }
        input.setAttribute('disabled', 'disabled');
    };

    request.onTransportFailure = function(errorMsg, request) {
        atmosphere.util.info(errorMsg);
        request.fallbackTransport = "long-polling";
        header.innerHTML = '<h3>Atmosphere Chat. Default transport is WebSocket, fallback is ' + request.fallbackTransport + '</h3>';
    };

    request.onError = function(response) {
        content.innerHTML = '<p>Sorry, but there\'s some problem with your socket or the server is down';
        logged = false;
    };

    request.onReopen = function(response) {
        console.log('Atmosphere re-connected using ' + response.transport);
        input.removeAttribute('disabled');
        content.innerHTML = '<p>Atmosphere re-connected using ' + response.transport + '</p>';
    };

    request.onMessage = function (response) {
        var message = response.responseBody;
        try {
            var json = atmosphere.util.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message);
            return;
        }

        input.removeAttribute('disabled');
        if (!logged && myName) {
            logged = true;
            status.innerHTML = myName + ': ';
            status.style.color = 'blue';
        } else {
            var me = json.author == author;
            var date = typeof (json.time) == 'string' ? parseInt(json.time)
                : json.time;
            addMessage(json.author, json.message, me ? 'blue' : 'black', new Date(date));
        }
    };

    input.onkeydown = function(e) {
        if (e.keyCode === 13) {
            var msg = this.value;

            // First message is always the author's name
            if (author == null) {
                author = msg;
            }

            subSocket.push(atmosphere.util.stringifyJSON({
                author : author,
                message : msg
            }));
            this.value = '';

            input.setAttribute('disabled', 'disabled');
            if (myName === false) {
                myName = msg;
            }
        }
    };

    function addMessage(author, message, color, datetime) {
        var p = document.createElement('P');
        var span = document.createElement('SPAN');
        span.style.color = color;
        span.appendChild(document.createTextNode(author));
        p.appendChild(span);
        var text = ' @ ' + (datetime.getHours() < 10 ? '0' + datetime.getHours() : datetime.getHours()) + ':' + (datetime.getMinutes() < 10 ? '0' + datetime.getMinutes() : datetime.getMinutes()) + ': ' + message;
        p.appendChild(document.createTextNode(text));
        content.appendChild(p);
    }

    subSocket = socket.subscribe(request);
})();