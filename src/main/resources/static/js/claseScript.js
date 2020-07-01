$(document).ready(function(){
	
	function viewInfo(root){
		$("#infoModalName p").text($($(root).parent().parent().children()[1]).text());
		$("#infoModalSala p").text($($(root).parent().parent().children()[2]).text());
		$("#infoModalProfesor p").text($($(root).parent().parent().children()[3]).text());
		$("#infoModalNumPlazas p").text($($(root).parent().parent().children()[4]).text());
		
		var fechaIni = $($(root).parent().parent().children()[5]).text();
		var fechaFin = $($(root).parent().parent().children()[6]).text();
		var HoraIni = $($(root).parent().parent().children()[7]).text();
		var HoraFin = $($(root).parent().parent().children()[8]).text();
		var day = new Date(fechaIni).getDay();
		var weekday = new Array(7);
		weekday[0] = "Sunday";
		weekday[1] = "Monday";
		weekday[2] = "Tuesday";
		weekday[3] = "Wednesday";
		weekday[4] = "Thursday";
		weekday[5] = "Friday";
		weekday[6] = "Saturday";
		var n = weekday[day];
		$("#infoModalFechaIni p").text(fechaIni);
		$("#infoModalFechaFin p").text(fechaFin);
		$("#infoModalDia p").text($($(root).parent().parent().children()[7]).text());

		$("#infoModalHoraIni p").text(HoraIni);
		$("#infoModalHoraFin p").text(HoraFin);
		$("#infoModalDia p").text(n);
		$("#infoModal").modal("toggle");
	}

    function editInfo(root){
    	 $("#editModalName input").val($($(root).parent().parent().children()[1]).text());
         var r = $("#editModalRoomSelect option[value='"+$(root).data("roomid")+"']").index();
         $("#editModalRoomSelect").prop("selectedIndex",$("#editModalRoomSelect option[value='"+$(root).data("roomid")+"']").index())
         $("#editModalNumPlazas input").val($($(root).parent().parent().children()[4]).text());
         var p = $("#editModalProfSelect option[value='"+$(root).data("profid")+"']").index();
         $("#editModalProfSelect").prop("selectedIndex",$("#editModalProfSelect option[value='"+$(root).data("profid")+"']").index())
          
         var fechaIni = $($(root).parent().parent().children()[5]).text();
         var fechaFin = $($(root).parent().parent().children()[6]).text();
         
         function formatDate(d){
         	  var date = new Date(d),
               yr = date.getFullYear(),
               month   = date.getMonth() > 8 ? (date.getMonth() + 1) : '0' + (date.getMonth() + 1)  ,
     		  day = date.getDate()  < 10 ? '0' + date.getDate()  : date.getDate(),
               newDate = yr + '-' + month + '-' + day;
     		  return newDate;
         }
         
         $("#editModalFechaIni input").val(formatDate(fechaIni));
         $("#editModalFechaFin input").val(formatDate(fechaFin));
         $("#editModalHoraIni input").val($($(root).parent().parent().children()[7]).text());
         $("#editModalHoraFin input").val($($(root).parent().parent().children()[8]).text());
         $("#editBtn").val($(root).parent().parent().children().first().text());
         $("#editModal").modal("toggle");
    }
    

    function removeLesson(root){
    	  $("#removeModal").modal("toggle");
          $("#yesRemove").val($(root).parent().parent().children().first().text());
    }
    
    $(".listActions>.btn-success").click(function() {
    	editInfo(this);
    });
    
    $(".listActions>.btn-primary").click(function() {
        $("#addModal").modal("toggle");
    });
        
    $(".listActions>.btn-info").click(function() {
		viewInfo(this);
    });
    
    $(".listActions>.btn-danger").click(function() {
    	removeLesson(this);
    })
    
    $("#addModalBtn").click(function() {
    	 $("#addClassModal").modal("toggle");
    });
    
    $("#yesRemove").click((e) => {   
		console.log(e.source);
		var id = $("#yesRemove").val();
		$.ajax({
			headers: {"X-CSRF-TOKEN": config.csrf.value},
			type : "POST",
			url : config.rootUrl + "clases/removeLesson/" + id,
			success: data => { 
			$("#dtBasicExample tbody tr.table"+id).hide();
			$("#removeModal").modal("toggle");
			},
			error: e => console.log(e)
		});
	});
    
	$("#editBtn").click(function() {   
		function formatDateToServer(d, t){
			var date = new Date(d),
			yr = date.getFullYear(),
			month = date.getMonth() > 8 ? (date.getMonth() + 1) : '0' + (date.getMonth() + 1)  ,
			day = date.getDate()  < 10 ? '0' + date.getDate()  : date.getDate(),
			newDate = yr + '-' + month + '-' + day + 'T' + t;
			return newDate;
		}
		function formatDateToClient(d){
			var date = new Date(d),
			yr = date.getFullYear(),
			month = date.getMonth() > 8 ? (date.getMonth() + 1) : '0' + (date.getMonth() + 1)  ,
			day = date.getDate()  < 10 ? '0' + date.getDate()  : date.getDate(),
			newDate = month + '-' + day + '-' + yr;
			return newDate;
		}
	       var dateIni = $("#editModalFechaIni input").val();
	       var dateFin = $("#editModalFechaFin input").val();
	       var horaIni = $("#editModalHoraIni input").val();
	       var horaFin = $("#editModalHoraFin input").val();
	       id = $(this).val();
	       var clase = {
	       "id" : id,
	       "name" : $("#editModalName input").val(), 
	       "roomId": $("#editModalRoomSelect").val(),
	       "profeId": $("#editModalProfSelect").val(),
	       "totalStudents": $("#editModalNumPlazas input").val(),
	       "dateIni": formatDateToServer(dateIni, horaIni),
	       "dateFin": formatDateToServer(dateFin, horaFin) 
	      };
	 
	      $.ajax({ 
  		      headers: {"X-CSRF-TOKEN": config.csrf.value},
  		       type: "POST",
  		       contentType: "application/json",
  		       url: "/clases/editLesson", 
  		       data: JSON.stringify(clase), 
  		       success : function(data) { 
  		    	$("#editModal").modal("toggle");
  		    	$($("#dtBasicExample tbody tr td.d-none:contains('"+id+"')").parent().children()[1]).text($("#editModalName input").val()); // nombre
				$($("#dtBasicExample tbody tr td.d-none:contains('"+id+"')").parent().children()[2]).text($("#editModalSala option:selected").text()); // sala
				$($("#dtBasicExample tbody tr td.d-none:contains('"+id+"')").parent().children()[3]).text($("#editModalProfesor option:selected").text()); // numero
				$($("#dtBasicExample tbody tr td.d-none:contains('"+id+"')").parent().children()[4]).text($("#editModalNumPlazas input").val()); // numero
				$($("#dtBasicExample tbody tr td.d-none:contains('"+id+"')").parent().children()[5]).text(formatDateToClient(dateIni)); // fecha
				$($("#dtBasicExample tbody tr td.d-none:contains('"+id+"')").parent().children()[6]).text(formatDateToClient(dateFin)); // fecha
				$($("#dtBasicExample tbody tr td.d-none:contains('"+id+"')").parent().children()[7]).text($("#editModalHoraIni input").val()); // fecha
				$($("#dtBasicExample tbody tr td.d-none:contains('"+id+"')").parent().children()[8]).text($("#editModalHoraFin input").val()); // fecha
				},
  		       error : function(e) {
  		        	alert(e);
  		       }
	      });
	});
    
	$("#addClassBtn").click(function() {
		function formatDateToServer(d, t){
			var date = new Date(d),
			yr = date.getFullYear(),
			month = date.getMonth() > 8 ? (date.getMonth() + 1) : '0' + (date.getMonth() + 1)  ,
			day = date.getDate()  < 10 ? '0' + date.getDate()  : date.getDate(),
			newDate = yr + '-' + month + '-' + day + 'T' + t;
			return newDate;
		}
		function formatDateToClient(d){
			var date = new Date(d),
			yr = date.getFullYear(),
			month = date.getMonth() > 8 ? (date.getMonth() + 1) : '0' + (date.getMonth() + 1)  ,
			day = date.getDate()  < 10 ? '0' + date.getDate()  : date.getDate(),
			newDate = month + '-' + day + '-' + yr;
			return newDate;
		}
		var fechaIni = $("#addClassModalFechaIni input").val();
		var fechaFin = $("#addClassModalFechaFin input").val()
    	var horaIni = $("#addClassModalHoraIni input").val();
    	var horaFin = $("#addClassModalHoraFin input").val();
    	var totalStudents = $("#addClassModalNumPlazas input").val();
    	var roomCapacity = $("#addClassModalSala option:selected").attr("capacity");
    	if(totalStudents && totalStudents <= roomCapacity){
    		var clase = { 
    				"name" : $("#addClassModalName input").val(),
    		        "roomId": $("#addClassModalSala option:selected").val(),
    		        "profeId": $("#addClassModalProf option:selected").val(),
    		        "totalStudents": $("#addClassModalNumPlazas input").val(),
    		        "dateIni": formatDateToServer(fechaIni, horaIni),
    		        "dateFin": formatDateToServer(fechaFin, horaFin)
    		    };
    		 
    			$.ajax({
    				headers: {"X-CSRF-TOKEN": config.csrf.value},
    				type: "POST",
    				contentType: "application/json",
    				url: "/clases/addLesson", 
    				data: JSON.stringify(clase), 
    				success: data => { 
    					console.log("exito");
    		   		    $("#dtBasicExample tbody").append('<tr>'+
    	   		    		'<td class="d-none" value="'+data+'">Nada</td>'+
    	   		    		'<td>'+ $("#addClassModalName input").val()+'</td>'+
    	   		    		'<td>'+$("#addClassModalSala option:selected").text()+'</td>'+
    	   		    		'<td>'+$("#addModalProfSelect option:selected").text()+'</td>'+
    	   		    		'<td>'+$("#addClassModalNumPlazas input").val()+'</td>'+
    	   		    		'<td>'+formatDateToClient(fechaIni)+'</td>'+
    	   		    		'<td>'+formatDateToClient(fechaFin)+'</td>'+
    	   		    		'<td class="d-none">'+horaIni+'</td>'+
    	   		    		'<td class="d-none">'+horaFin+'</td>'+		
    	   		    		'<td class="listActions text-center">'+
    	   		    		'<button type="button" class="btn btn-info btn-default btn-xs" style="min-width: 50px;">'+
    	   		    		'<span class="glyphicon glyphicon-info-sign" aria-hidden="true">&#10069</span></button>'+
    	   		    		'<button type="button" class="btn btn-success btn-default btn-xs ml-2 "  style="min-width: 50px;">'+
    	   		    		'<span class="glyphicon glyphicon-pencil" aria-hidden="true">&#9999</span></button>'+
    	   		    		'<button  type="button" class="btn btn-danger btn-default btn-xs ml-2"  style="min-width: 50px;">'+
    	   		    		'<span class="glyphicon glyphicon-remove" aria-hidden="true">&#10006</span>'+
    						   '</button></td></tr>');
    						   $(".listActions>.btn-info").click(function() {
    							   viewInfo(this);
    						   });
    						   $(".listActions>.btn-success").click(function() {
    						    	editInfo(this);
    						   });	
    						   $(".listActions>.btn-danger").click(function() {
    						    	removeLesson(this);
    						    });
    						   				    
    	  		    	$("#addClassModal").modal("hide");
    	  		       },
    	  		       error: e => console.log(e)
    	  		 });
    	}
		
    })
});