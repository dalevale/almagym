function checkUnread(){
	$.ajax({
		headers: {"X-CSRF-TOKEN": config.csrf.value},
	    type : "GET",
	    url : config.rootUrl + "messages/unread/",
	    success: data => { 
	    	console.log(data.unread)
	    	var num = data.unread;
	    	if(num > 0){
	    		if(num == 1)
		    		$("#navMensajes").text(num + " Nuevo Mensaje");
	    		else
		    		$("#navMensajes").text(num + " Nuevos Mensajes");
	    		$("#navMensajes").addClass("forNewMessages");
	    	}
	    	else{
	    		$("#navMensajes").text("Mensajes");
	    		$("#navMensajes").removeClass("forNewMessages");
	    	}
			$("#navMensajes").prepend('<i class="fas fa-info-circle"></i>');
	    },
	    error: e => console.log(e)
		});
}

function readNewMessages(otherId){
	var messageIdsArray = $("#convoBox.user"+ otherId +" div.convoLine input.messageId");
	var messageDateReadArray = $("#convoBox.user"+ otherId +" div.convoLine input.messageDateRead");
	var i = 0;
	while(messageDateReadArray.eq(i).val() == "null"){
		if(messageIdsArray.eq(i).parent().hasClass("toUser")){
			$.ajax({
				headers: {"X-CSRF-TOKEN": config.csrf.value},
				type : "GET",
				url : config.rootUrl + "messages/readMsg/"+ messageIdsArray.eq(i).val(),
				success: data => { 
					messageDateReadArray.eq(i).val(data);
				},
				error: e => console.log(e)
			});
		}
		i++;
	}
	var div = $("#usersList div.user" + otherId );
	if($("#convoBox").hasClass("user" + otherId)){
		div.removeClass("newMessage");
		div.addClass("convo");
	}
}

function addUserDestination(){
	var firstUsername = $("#convoTitle h3").text();
	var usernameInputs = $('<p>Para (separado por comas): <input type="text" value="'+ firstUsername +'"></p>');
	var searchUser = $('<div id="searchUser">'+
			'Busca un usuario: <input type="text" maxLength="200" name="username" placeholder="nombre usuario" autocomplete="off">'+
			'</div>');
	$("#convoTitle").empty();
	$("#convoTitle").append(usernameInputs);
	$("#convoTitle").append(searchUser);
	$("#convosList").remove();
	$("#convoBox").remove();
	var tArea = $("#newMessageBox textarea.inputNewMessage");
	tArea.removeClass('inputNewMessage');
	tArea.addClass('biggerInputNewMessage');
	var returnButton = $('<button type="button">Volver</button>');
	returnButton.click(function(){
		window.location.href = "http://localhost:8080/messages/";
	});
	$("#newMessageBox").append(returnButton);
	$("#newMessageBtn").prop("onclick", null).off("click");
	$("#newMessageBtn").click(function(){
		var idArray = new Array();var usernameStr = $("#convoTitle p input").val();
		var arr = usernameStr.split(",").map(item => item.trim());
		var filtered = arr.filter(Boolean);
		var valid = checkUsernames(filtered, idArray);
		if (valid)
			sendMsgToMany(filtered, idArray);
	});
	searchUser.keyup(function(){
		$("#searchUser div.dropDownUserList-content").remove();
		var username = $(this).children().val();
		$.ajax({
			headers: {"X-CSRF-TOKEN": config.csrf.value},
			type : "GET",
			url : config.rootUrl + "user/searchUsers/"+ username,
			success: data => { 
				$("#searchUser input").addClass('dropDownUserList');
				var searchList = $('<div></div>');
				searchList.addClass('dropDownUserList-content');
				$("#searchUser").append(searchList);
				for(var i = 0; i < data.length; i++){
					var user = data[i];
					var img = config.rootUrl + 'user/'+ user.id + '/photo';
					var userResult = $('<div><input type="hidden" value="'+ user.id +'"><img src="'+ img +'" width="50px" length="50px">' +user.username+'</div>');
					userResult.addClass('userResult');
					userResult.click(function(){
						searchList.remove();
						var newUsername = $(this).text();
						var initVal = usernameInputs.children().val();
						var arr = initVal.split(",").map(item => item.trim());
						var filtered = arr.filter(Boolean);
						if(!filtered.includes(newUsername)){
							var str = filtered[0];
							for(var i = 1; i < filtered.length; i++)
								str += ", " + filtered[i];
							usernameInputs.children().val(str +", " + $(this).text());
						}
						else
							alert(newUsername + " is already on the list!");
					});
					searchList.append(userResult);
				}
			},
			error: e => console.log(e)
		});
	});
	
}

function checkUsernames(usernameArray, idArray){
	var valid = true;
	for(var i = 0; i < usernameArray.length && valid; i++){
		$.ajax({
			headers: {"X-CSRF-TOKEN": config.csrf.value},
			type : "GET",
			url : config.rootUrl + "user/getIdByUsername/"+ usernameArray[i],
			async: false,
			success: data => { 
				idArray.push(data);
				if(!data){
					valid = false;
					alert("Usuario " + usernameArray[i] + " no existe.");
				}
			},
			error: e => console.log(e)
		});
	}
	return valid;
}
	
function sendMsgToMany(usernameArray, idArray){
	var message = $("#newMessageBox textarea").val();
	if(!message)
		alert("Mensaje vacio no valido.");
	else{
		for(var i = 0; i < usernameArray.length; i++){
			var data = {
				"from": $("#username").text(),
				"to": usernameArray[i],
				"text": message
		}
			$.ajax({
				headers: {"X-CSRF-TOKEN": config.csrf.value},
				type : "POST",
				data: JSON.stringify(data),
				contentType: "application/json",
				async: false,
				url : config.rootUrl + "messages/sendMsgToUser/"+ idArray[i],
				error: e => console.log(e)
			});
		}
	}

	alert("Message sent");
	window.location.href = "http://localhost:8080/messages/";
}

function loadMessages(otherId, otherUsername, otherImg){
	$("#convoTitle").empty();
	$("#convoTitle").append($('<h3><img src="'+ otherImg +'" width="50px" length="50px">'+ otherUsername + '</h3>'));
	var addUserButton = $('<button type="button">Añadir Usuario</button>');
	$("#convoTitle").append(addUserButton);
	addUserButton.click(function(){
		addUserDestination();
	});
	$.ajax({
		headers: {"X-CSRF-TOKEN": config.csrf.value},
		type : "GET",
		url : config.rootUrl + "messages/getConvosWithUser/"+ otherId,
		success: data => { 
			$("#convoBox").empty();
			$("#convoBox").removeClass();
			$("#convoBox").addClass('user' + otherId);
			for(var i = data.length-1; i >= 0; i--){
				var message = data[i];
				var otherUserSent = message.from == otherUsername;
				var divClass = otherUserSent? 'toUser' : 'fromUser';
				var img = otherUserSent? otherImg : $("#userPhoto").attr('src');
				printMessage(img, message.text, divClass, otherId, message.id, message.sent, message.received);
			}
			$("#newMsgUsername").val(otherUsername);
			$("#newMessageBtn").val(otherId);
			readNewMessages(otherId);
		},
		error: e => console.log(e)
	});
}

function printMessage(img, msg, className, userId, messageId, dateSent, dateRead){
	var newConvoLine = $(
			'<div class="convoLine ' + className + '">' +
			'<input type="hidden" class="messageId" value="'+ messageId + '">' +
			'<input type="hidden" class="messageDateSent" value="'+ dateSent + '">' +
			'<input type="hidden" class="messageDateRead" value="'+ dateRead + '">' +
			'<img src="'+ img +'" width="50px" height="50px">'+ msg +'</div>'
			);
	$("#convoBox.user" + userId).prepend(newConvoLine);
}

function sendMessage(message, toUserId, toUserImg, ajaxData){
	$.ajax({
		headers: {"X-CSRF-TOKEN": config.csrf.value},
		type : "POST",
		contentType: "application/json",
		url : config.rootUrl + "messages/sendMsgToUser/"+ toUserId,
		data: JSON.stringify(ajaxData),
		success: msg => { 
			printMessage(toUserImg, message, 'fromUser', toUserId, msg.id, msg.dateSent, "");
			updateUsersList(toUserId, message);
		},
		error: e => console.log(e)
	});
}

function updateUsersList(toUserId, newMsg){
	var usersList = $("#usersList");
	if(usersList.length <= 0)
		initializeUsersList();
		
	
	var convoToMove = $("#usersList div.user"+ toUserId);
	if(convoToMove.length > 0){
		$("#usersList div.user"+ toUserId + " div p.convoLastMsg").text(newMsg);
		convoToMove.remove();
		$("#usersList").prepend(convoToMove);
		convoToMove.removeClass('convoWithUsers');
		convoToMove.addClass('convoWithUsers');
		convoToMove.click(function(){
			var username = convoToMove.children().eq(2).children().eq(0).text();
			var img = config.rootUrl + "user/" + toUserId + "/photo";
			loadMessages(toUserId, username, img);
		});
	}
	else{
		initializenewConvo();
	}
}

function initializeNewConvo(toUserId, newMsg){
	var convoWithNewUser = $(
			'<div class="user'+ toUserId +'">' +
				'<input class="userId" type="hidden" value="'+ toUserId +'">' +
				'<img src="/user/'+ toUserId +'/photo" width="50px" height="50px">'+
				'<div>'+//TODO get username
					'<p class="username">'+ $("#convoTitle h3").text() +'</p>'+
					'<p class="convoLastMsg">'+ newMsg +'</p>'+
				'</div>'+
			'</div>');
	convoWithNewUser.addClass('convo');
	convoWithNewUser.addClass('convoWithUsers');
	$("#usersList").prepend(convoWithNewUser);
}

function initializeUserList(){
	var usersList = $('<div id="usersList"></div>');
	$("#convosList").append(usersList);
}

setInterval(function(){
	checkUnread();
}, 2000);

$(document).ready(function(){
	$("#usersList div.convoWithUsers").click(function(){	
		var otherId = $(this).children().eq(0).val();
		var otherUsername = $(this).children().eq(2).children().eq(0).text();
		var otherImg = $(this).children().eq(1).attr('src');
		loadMessages(otherId, otherUsername, otherImg);
	});	
	
	$("#newMessageBtn").click(function(){
		var newMsg = $("#newMessage").val();
		var toUserId = $(this).val();
		var img = config.rootUrl + "user/" + $("#userId").val() + "/photo";
		var data = {
				"from": $("#username").text(),
				"to": $("#newMsgUsername").val(),
				"text": newMsg
		}
		if(newMsg){
			sendMessage(newMsg, toUserId, img, data);
			$("#newMessage").val("");
		}
	});
	
	$("#searchUser input").keyup(function(){
		$("#searchUser div.dropDownUserList-content").remove();
		var username = $(this).val();
		$.ajax({
			headers: {"X-CSRF-TOKEN": config.csrf.value},
			type : "GET",
			url : config.rootUrl + "user/searchUsers/"+ username,
			success: data => { 
				$("#searchUser input").addClass('dropDownUserList');
				var searchList = $('<div></div>');
				searchList.addClass('dropDownUserList-content');
				$("#searchUser").append(searchList);
				for(var i = 0; i < data.length; i++){
					var user = data[i];
					var img = config.rootUrl + 'user/'+ user.id + '/photo';
					var userResult = $('<div><input type="hidden" value="'+ user.id +'"><img src="'+ img +'" width="50px" length="50px">' +user.username+'</div>');
					userResult.addClass('userResult');
					userResult.click(function(){
						searchList.remove();
						loadMessages($(this).children().eq(0).val(), $(this).text(), $(this).children().eq(1).attr('src'));
					});
					searchList.append(userResult);
				}
			},
			error: e => console.log(e)
		});
	});
});