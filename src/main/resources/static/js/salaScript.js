//Script de vista salas solamente los botones de salas
$(document).ready(
	function() {
		$(".listActions>.photoSala").click(function() {
			var id= $($(this).parent().parent().parent()
					.parent().parent()
					.children().eq(0)
					.children()).val();
			var table = "#salaIteracion div div.table"+id;
			$("#photoSalaSbmit").val(id);
			$("#salaPhotoName").text($(table+" .salaName").text());
			var src = $("#salaIteracion div div.table"+id+" div div img.salaImg").attr("src");
			$("#changePhotoModal div div form img.salaImg").attr("src", src);
			$("#changePhotoModal").modal("toggle");
		});

		$(".listActions>.editSala").click(function() {
			var id = $($(this).parent().parent().parent()
					.parent().parent()
					.children().eq(0)
					.children()).val();
			var table = "#salaIteracion div div.table"+id;
			var salaName = $("#salaIteracion div div.table"+ id +" div.salaName").text();
			$("#editModalSala").modal("toggle");
			$("#editSalaSbmit").val(id);
			$("#editModalSalaName input").val(salaName);
			$("#editModalCapacidad input").val($(table+" .salaCapacidad").text());
			$("#editModalDesc textarea").val($(table+" .salaDesc").text());
			
		});

		$(".listActions>.removeSala.deletable").click(function() {
			var id= $($(this).parent().parent().parent()
					.parent().parent()
					.children().eq(0)
					.children()).val();
			$("#removeModal .modal-title").text("¿Estás seguro?");
			$("#removeModal .modal-body p").text("¿Estás seguro?");
			$("#removeSalaSbmit").show();
			$("#removeSalaSbmit").val(id);
			$("#removeModal").modal("toggle");
		});	

		$(".listActions>.removeSala.undeletable").click(function() {
			$("#removeModal .modal-title").text("Acción invalida!");
			$("#removeModal .modal-body p").text("No se puede borrar! Hay clases en esta sala!");
			$("#removeSalaSbmit").hide();	
			$("#removeModal").modal("toggle");
		});	

		$(".listActions>.addSala").click(function() {
			$("#addModal").modal("toggle");
		});	

		$("#salaPhotoInput").on("change", function previewFile() {
			const preview = $("#changePhotoModal div div form img.salaImg");
			const file = document.getElementById('salaPhotoInput').files[0];
			const reader = new FileReader();
			reader.addEventListener("load", 
				function () {
					preview.attr("src", reader.result);
			    	preview.show();
				}, false);
			if (file) {
				reader.readAsDataURL(file);
			}
		});
	
		$("#photoSalaSbmit").click((e) => {   
			console.log(e.source);
			var id = $("#photoSalaSbmit").val();
			var table = "#salaIteracion div div.table"+id;
			var file = document.getElementById('uploadPhotoForm');
			var fd = new FormData(file);
			$.ajax({
				headers: {"X-CSRF-TOKEN": config.csrf.value},
			   type : "POST",
			   data : fd,
			   url : config.rootUrl + "salas/changephoto/" + id,
			   data: fd,
			   enctype: 'multipart/form-data',
			   processData: false,
			   contentType: false,
			   cache: false,
			   success: data => { 
				   $("#changePhotoModal").modal("toggle");
			   	$(table+" div div img").attr("src", $("#changePhotoModal div div form img.salaImg").attr("src"));
			   },
			   error: e => console.log(e)
			});
		});

		$("#editSalaSbmit").click((e) => {     
			var sala = {
				"id" : $("#editSalaSbmit").val(),
				"name" : $("#editModalName input").val(), 
			    "maxSize": $("#editModalCapacidad input").val(),
			    "descrip": $("#editModalDesc textarea").val()
			};
			$.ajax({ 
				headers: {"X-CSRF-TOKEN": config.csrf.value},
			    type: "POST",
			    contentType: "application/json",
			    url: "/salas/edit", 
			    data: JSON.stringify(sala), 
			    success : data => { 
			    	var id= $("#editSalaSbmit").val();
			    	var table = "#salaIteracion div div.table"+id;
					$("#editModalSala").modal("toggle");
					$(table+" .salaName").text($("#editModalSalaName").val());
					$(table+" .salaCapacidad").text($("#editModalCapacidad input").val());
					$(table+" .salaDesc").text($("#editModalNumPlazas input").val());
		    	},
  		        error : e => {
  		        	alert(e);
  		        }
			});
		});
 
		$("#removeSalaSbmit").click((e) => {   
			console.log(e.source);
			var id = $("#removeSalaSbmit").val();
			var table = "#salaIteracion div div.table"+id;
			$.ajax({
				headers: {"X-CSRF-TOKEN": config.csrf.value},
			    type : "GET",
			    url : config.rootUrl + "salas/del/" + id,
			    success: data => { 
				$(table).hide();
				$("#removeModal").modal("toggle");
	        },
	        error: e => console.log(e)
			});
		});
		
		$("#addSalaSbmit").click((e) => {     
			var sala = {
				"name" : $("#addModalName input").val(), 
				"maxSize": $("#addModalCapacidad input").val(),
				"descrip": $("#addModalDesc textarea").val()
			};
			$.ajax({ 
				headers: {"X-CSRF-TOKEN": config.csrf.value},
			    type: "POST",
			    contentType: "application/json",
			    url: "/salas/add", 
			    data: JSON.stringify(sala), 
			    success : data => {
		    		$("#addModal").modal("toggle");
	    			var newTable = $('<div class="table'+data+' row sala p-3 rounded-top" style="background-color: #f7f4f4;">'+
						'<div class="col-3 sala-left">'+
							'<div><input class="salaId" type="hidden" value="'+data+'" /></div>'+
							'<div class="row"><div class="mt-0 h3 salaName" >'+$("#addModalName input").val()+'</div></div>'+
							'<div class="row"><img class="salaImg"style="width: 180px; height: 110px; margin: auto;" src="/img/default-sala.jpg"></div>'+
							'<div class="row mt-1"><p>Capacidad:&nbsp</p><p class="salaCapacidad">'+$("#addModalCapacidad input").val()+'</p></div>'+
							'<div class="row mt-1"><p class="salaDesc">'+$("#addModalDesc textarea").val()+'</p></div>'+
							'<table>'+
								'<tr><td class="listActions">'+
									'<button type="button" class="btn photoSala btn-info btn-default btn-xs ml-2">'+
									'<span class="glyphicon glyphicon-pencil" aria-hidden="true">&#127924</span></button>'+
									'<button type="button" class="btn editSala btn-success btn-default btn-xs ml-2">'+
									'<span class="glyphicon glyphicon-pencil" aria-hidden="true">&#9999</span></button>'+
									'<button type="button" class="btn removeSala btn-danger deletable btn-default btn-xs ml-2">'+
									'<span class="glyphicon glyphicon-remove" aria-hidden="true">&#10006</span></button>'+
								'</td></tr></table></div>'+
						'<div class="col-9 sala-right">'+
							'<h5>En esta sala están las siguientes máquinas:</h5>'+
							'<div class="row ">'+
								'<div class="listActions añadir d-flex justify-content-center mb-4">'+
								'<button type="button" class="btn addEquipo btn-primary btn-lg">&#10133Añadir Equipo</button>'+
						'</div></div></div></div>');
					$("#salaIteracion").append(newTable);
					},
	  		        error : e => {
	  		        	alert(e);
	  		        }
		      });
	  	});
});

