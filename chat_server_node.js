//Constants
const CONNECTION_EVENT_VALUE = "connection";
const DISCONNECT_EVENT_VALUE = "disconnect";
const CONNECT_USER_EVENT_VALUE = "connect_user";
const DISCONNECT_USER_EVENT_VALUE = "disconnect_user";
const CONNECTED_EVENT_VALUE = "connected";//подтверждение успешного коннекта
const MESSAGE_EVENT_VALUE = "message";
const INFO_USER_EVENT_VALUE = "info_user";
const INFO_MANAGER_EVENT_VALUE = "info_manager";
const ROOM_IN_EVENT_VALUE = "room_in";
const ROOM_OUT_EVENT_VALUE = "room_out";
const ROOM_LIST_EVENT_VALUE = "room_list";

var express = require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
http.listen(3000);
app.use(express.static('public'));

app.get('*', function(req, res) {
	res.send('imge no found');
  });

// Подключаем модуль и ставим на прослушивание 8080-порта - 80й обычно занят под http-сервер
// var io = require('socket.io').listen(3000); 
var roomList = [];
//for push
var FCM = require('fcm-push');
var serverKey = 'AAAAxoQ3Ee0:APA91bEoI0P2nDwXOQ5GdXDXiKUYonMGvPufKu6YtEo1fMWGn1MO_4LLmCa8ZzR7wmdxmCdnz6yrcrG76aIy74bQjn6yq3LlwlDOawMuMdYCU0B2Yk9tjcbuCdFsKngVL8S2qwj-pxqp';
var fcm = new FCM(serverKey);
// Отключаем вывод полного лога - пригодится в production'е
//io.set('log level', 1);
//static files____________________________
// var express = require('express');
var path = require('path');
// var app = express();

// Навешиваем обработчик на подключение нового клиента
io.on(CONNECTION_EVENT_VALUE, function (socket) {
	// Т.к. чат простой - в качестве ников пока используем первые 5 символов от ID сокета
	var ID = (socket.id).toString().substr(0, 5);
	var time = (new Date).toLocaleTimeString();
	var roomName;
	var typeClient;
	var isFirstMessage = true;
	// Посылаем клиенту сообщение о том, что он успешно подключился и его имя
	socket.emit(CONNECTED_EVENT_VALUE, ID);
	// Навешиваем обработчик на входящее сообщение
	socket.on(MESSAGE_EVENT_VALUE, function (type, msg) {
		var time = (new Date).toLocaleTimeString();
		console.log('time', time);

		// Уведомляем клиента, что его сообщение успешно дошло до сервера
		//
		//остальным в комнате
		if (type == "text") {
			console.log('message', msg);
			console.log('type', type);
			socket.broadcast.to(roomName).json.send({'type': type, 'name_client': ID, 'text': msg , 'time': time});
		}else if (type == "image") {
			var nameImage = ID + '_' + (new Date()).getTime() + '.jpg';
			var fs  = require('fs');
			fs.writeFile("/Users/user/Desktop/node_js_files/public/" + nameImage, msg, 'binary', function(err) {
    		if(err)
      			console.log(err);
    		else
      			console.log("The file was saved!");
  			}); 
			socket.broadcast.to(roomName).json.send({'type': type, 'name_client': ID, 'name_image': nameImage , 'time': time});
		};
		console.log('message', msg);
		if (isFirstMessage && typeClient == 'user'){
			isFirstMessage = false;
			console.log('push', 'sending...');
			sendPushCreateRoomToManagers(roomName, ID, msg);
		}
	});
	// При отключении клиента - уведомляем остальных
	socket.on(DISCONNECT_EVENT_VALUE, function() {
		console.log('disconnect_user', ID);
		//var time = (new Date).toLocaleTimeString();
		socket.broadcast.emit(DISCONNECT_USER_EVENT_VALUE, ID);
		changeStatusClient(roomName, ID, false);
	});
	// Info from user
	socket.on(INFO_USER_EVENT_VALUE, function(name, room, type) {
		setLogInfo(name, room, type);
		var roomListPosition = getPositionRoom(room)
		if (room && typeof roomListPosition !== 'undefined') {
			addToRoom(roomListPosition, name, socket.id);
		}else{
			addNewRoom(name, room, socket.id);
		};
		typeClient = type;
		ID = name;
		roomName = room;
		// Посылаем всем сообщение о том, что клиент успешно подключился и его имя
		socket.broadcast.emit(CONNECT_USER_EVENT_VALUE, 'change name to ' + ID);
		socket.join(roomName)
	});
	// Info from manager
	socket.on(INFO_MANAGER_EVENT_VALUE, function(name) {
		ID = name;
		typeClient = 'manager';
		setLogInfo(name, 'empty', typeClient);
		// Отправляем список комнат
		socket.emit(ROOM_LIST_EVENT_VALUE, roomList);
		socket.on(ROOM_OUT_EVENT_VALUE, function(){
		if (roomName) {
			socket.leave(roomName);
			changeStatusClient(roomName, ID, false);
			};
		});
		socket.on(ROOM_IN_EVENT_VALUE, function(room){
			roomName = room;
			if (room) {
				socket.join(room);
				addToRoom(getPositionRoom(room), ID, socket.id);
			};
		});
	});
});

function changeStatusClient(room, name, status){
	if (typeof roomList[getPositionRoom(room)] !== 'undefined' && typeof roomList[getPositionRoom(room)]['listClient'] !== 'undefined') {
		roomList[getPositionRoom(room)]['listClient'].forEach(function(item, i, arr) {
  			if (item["nickname"] == name) {
  				item["isConnected"] = status;
  				return;
  			};
		});
	};
}

function getPositionRoom(room){
	console.log('checkRoom', 'roomName = ' + room);
	var pos;
	roomList.forEach(function(item, i, arr) {
  		if (item["roomName"] == room){
  			isNewRoom = false;
  			console.log('checkRoom', 'pos = ' + i);
  			pos = i;
  			return i;
  		}
	});
	return pos;
}

function addToRoom(position, name, socketId){
	console.log('addToRoom', 'position = ' + position);
	console.log('addToRoom', 'roomList = ' + roomList);
	var isNewCLient  = true;
	roomList[position]["listClient"].forEach(function(item, i, arr) {
  		if (item["nickname"] == name) {
  			item["isConnected"] = true;
  			isNewCLient = false;
  			return;
  		};
	});
	if (isNewCLient) {
		var clientInfo = {};
        clientInfo["id"] = socketId;
        clientInfo["nickname"] = name;
        clientInfo["isConnected"] = true;
        roomList[position]["listClient"].push(clientInfo);
	};
}

function addNewRoom(name, room, socketId){
		console.log('addNewRoom', 'roomName = ' + room);
		var clientInfo = {};
        clientInfo["id"] = socketId;
        clientInfo["nickname"] = name;
        clientInfo["isConnected"] = true;
		var roomInfo = {};
		roomInfo["roomName"] = room;
        roomInfo["nicknameCreator"] = name;
        roomInfo["timeCreated"] = (new Date()).getTime();
        roomInfo["listClient"] = [];
        roomInfo["listClient"].push(clientInfo);
        roomList.push(roomInfo);
        io.sockets.emit(ROOM_LIST_EVENT_VALUE, roomList);
}

function setLogInfo(name, room, type){
		console.log('***', '*******************');
		console.log('name', name);
		console.log('room', room);
		console.log('type', type);
		console.log('---', '-------------------');
}

function sendPushCreateRoomToManagers(roomName, ID, msg){
			//create message for IOS
			var messageIOS = {
    			to: '/topics/manager_ios', // required fill with device token or topics
			    data: {
       				room : roomName,
					name_user : ID,
					message : 'Test message',
                	type : 'manager'
    			},
    				notification: {
        			title: ID,
        			body: msg
    			}
			};
			//create message for Android
			var messageAndroid = {
    			to: '/topics/manager_android', // required fill with device token or topics
			    data: {
       				room : roomName,
					name_user : ID,
					message : msg,
                	type : 'manager'
    			}
    		};
    		//send push for IOS
    		fcm.send(messageIOS)
    			.then(function(response){
       			 console.log("IOS_Successfully sent with response: ", response);
   			 })
    		.catch(function(err){
        	console.log("IOS_Something has gone wrong!");
        	console.error(err);
    	})
    		//send push for Android
    		fcm.send(messageAndroid)
    			.then(function(response){
       			 console.log("Android_Successfully sent with response: ", response);
   			 })
    		.catch(function(err){
        	console.log("Android_Something has gone wrong!");
        	console.error(err);
    	})
}
