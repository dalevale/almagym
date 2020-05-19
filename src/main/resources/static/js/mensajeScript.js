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

function showMessages(){
	var numRows = $("#datatable tbody tr").length;
	for(var i = 0; i < numRows; i++){
		if($($("#datatable tbody tr").get(i).children).eq(3).text() == "")
			$($("#datatable tbody tr").get(i)).css("font-weight" , "bold");
	}
	$("#datatable tr *:nth-child(6)").hide();
}

function readMessage(){
	$("#datatable tbody tr").each(function(index) {
		$(this).click(function(){
			var id = $(this).children().eq(6).text();
			if($(this).children().eq(3).text() == ""){
				$.ajax({
					headers: {"X-CSRF-TOKEN": config.csrf.value},
				    type : "GET",
				    url : config.rootUrl + "messages/setread/" + id,
				    success: data => { 
			    	$(this).css("font-weight" , "normal");
					var date = formatDate(data);
					$(this).children().eq(3).text(date);
		        },
		        error: e => console.log(e)
				});
			}
			$(checkUnread);
			$("#messageInbox div span.messageFrom").text($(this).children().eq(0).text());
			$("#messageInbox div p.messageText").text($(this).children().eq(5).text());
			$("#messageInbox div span.messageSubj").text($(this).children().eq(4).text());
			$("#messageInbox").css("display","block");
			
		});
	});
}
$(showMessages);
$(readMessage);
$(checkUnread);