$(document).ready(
	function() {
		$(".equipoListActions>.removeEquipo").click(function() {
			var id = $($(this).parent().parent().parent()
					.parent().parent()
					.children().eq(0)
					.children()).val();
			$("#removeEquipoSbmit").val(id);					
			$("#removeModalEquipo").modal("toggle");
		});	
		
		$(".equipoListActions>.editEquipo").click(function() {
					var id = $($(this).parent().parent().parent()
											.parent().parent()
											.children().eq(0)
											.children()).val();
					var table = "#equipoIteracion div div.table"+id;
					$("#editEquipoSbmit").val(id);
					$("#editEquipoModalRoom input").val($($(this).parent()
							.parent().parent().parent().parent().parent()
							.parent().parent().parent().children().eq(0)
							.children().eq(0).children()).val());
					$("#editEquipoModalName input").val($(table+" .equipoName").text());
					$("#editEquipoModalCantidad input").val($(table+" .equipoCantidad").text());
					$("#editModalEquipo").modal("toggle");
		});
	
		$(".equipoListActions>.addEquipo").click(function() {
			$("#addEquipoModalRoom input").val($($(this).parent().parent().parent()
					.children().eq(0).children().eq(0).children()).val());
			$("#addModalEquipo").modal("toggle");
		});	
		
		$(".equipoListActions>.photoEquipo").click(function() {
			var id= $($(this).parent().parent()
					.parent().parent().parent()
					.children().eq(0).children()).val();
			var table = "#equipoIteracion div div.table"+id;
			$("#photoEquipoSbmit").val(id);
			$("#equipoPhotoName").text($(table+" .equipoName").text());
			var src = $("#equipoIteracion div div.table"+id+" img.equipoImg").attr("src");
			$("#changePhotoModalEquipo div div form img.equipoImg").attr("src", src);
			$("#changePhotoModalEquipo").modal("toggle");
		});
		
		$("#editEquipoSbmit").click((e) => {     
	      	  var equipo = {
			       "id" : $("#editEquipoSbmit").val(),
			       "name" : $("#editEquipoModalName input").val(), 
			       "quantity": $("#editEquipoModalCantidad input").val(),
			       "room": $("#editEquipoModalRoom input").val()
			      };
		      $.ajax({ 
      		      headers: {"X-CSRF-TOKEN": config.csrf.value},
      		       type: "POST",
      		       contentType: "application/json",
      		       url: "/equipos/edit", 
      		       data: JSON.stringify(equipo), 
      		       success : data => { 
	      		    	var id= $("#editEquipoSbmit").val();
      		    		var table = "#equipoIteracion div div.table"+id;
      		    	$("#editModalEquipo").modal("toggle");
      		    	$(table+" .equipoName").text($("#editEquipoModalName input").val());
      		    	$(table+" .equipoCantidad").text($("#editEquipoModalCantidad input").val());
      		       },
      		       error : e => {
      		        	alert(e);
      		       }
		      });  
      	});
		
		$("#removeEquipoSbmit").click((e) => {   
			  console.log(e.source);
			  var id = $("#removeEquipoSbmit").val();
		 	  var table = "#equipoIteracion div div.table"+id;
		      $.ajax({
				   headers: {"X-CSRF-TOKEN": config.csrf.value},
	  		       type : "POST",
	  		       url : config.rootUrl + "equipos/del/" + id,
	  		       success: data => { 
	  		    	 $(table).hide();
	  		    	 $("#removeModalEquipo").modal("toggle");
	  		       },
	  		       error: e => console.log(e)
		      });
	    });
		
		$("#addEquipoSbmit").click((e) => {     
	      	  var equipo = {
			       "name" : $("#addEquipoModalName input").val(), 
			       "quantity": $("#addEquipoModalCantidad input").val(),
			       "room": $("#addEquipoModalRoom input").val()
			      };
		      $.ajax({ 
      		      headers: {"X-CSRF-TOKEN": config.csrf.value},
      		       type: "POST",
      		       contentType: "application/json",
      		       url: "/equipos/add", 
      		       data: JSON.stringify(equipo), 
      		       success : data => {
	      		    	$("#addModalEquipo").modal("toggle");
	      		    	var val = $("#addEquipoModalRoom input").val();
						var equipoTable = $("#salaIteracion div div.table"+ val + " div.sala-right div.row");
      		    		var newTable = '<div class="col">'+
      		    				'<div class="table'+data+' card p-3">'+
								'<div><input type="hidden" value="${equipment.id}" /></div>'+
								'<img class="card-img-top" style="width: 180px; height: 110px; margin: auto;" src="/img/default-sala.jpg">'+
								'<div class="card-body">'+
									'<h5 class="equipoName">'+$("#addEquipoModalName input").val()+'</h5>'+
									'<p>Cantidad:&nbsp</p>'+
									'<p class="equipoCantidad">'+$("#addEquipoModalCantidad input").val()+'</p></div>'+
								'<table><tr>'+
										'<!-- Botones para los modales Equipo-->'+
										'<td class="equipoListActions">'+
											'<button type="button"class="btn photoEquipo btn-info btn-default btn-xs ml-2">'+
												'<span class="glyphicon glyphicon-pencil" aria-hidden="true">&#127924</span></button>'+
											'<button type="button"class="btn editEquipo btn-success btn-default btn-xs ml-2">'+
												'<span class="glyphicon glyphicon-pencil" aria-hidden="true">&#9999</span></button>'+
											'<button type="button"class="btn removeEquipo btn-danger deletable btn-default btn-xs ml-2 removeEquipo">'+
												'<span class="glyphicon glyphicon-remove" aria-hidden="true">&#10006</span></button>'+
										'</td></tr></table></div></div></div>';
      		    		equipoTable.append(newTable);
      		       },
      		       error : e => {
      		        	alert(e);
      		       }
		      });   
		});
		
		$("#equipoPhotoInput").on("change", function previewFile() {
			  const preview = $("#changePhotoModalEquipo div div form img.equipoImg");
			  const file = document.getElementById('equipoPhotoInput').files[0];
			  const reader = new FileReader();
			  reader.addEventListener("load", function () {
			    preview.attr("src", reader.result);
		    	preview.show();
			  }, false);
			  if (file) {
			    reader.readAsDataURL(file);
			  }
		});
	    
		$("#photoEquipoSbmit").click((e) => {   
			console.log(e.source);
			var id = $("#photoEquipoSbmit").val();
	    	var table = "#equipoIteracion div div.table"+id;
	    	var file = document.getElementById('uploadPhotoFormEquipo');
	    	var fd = new FormData(file);
		    $.ajax({
		    	headers: {"X-CSRF-TOKEN": config.csrf.value},
	  		    type : "POST",
	  		    data : fd,
	  		    url : config.rootUrl + "equipos/changephoto/" + id,
	  		    data: fd,
	  		   	enctype: 'multipart/form-data',
	  		    processData: false,
	  		    contentType: false,
	  		    cache: false,
	  		    success: data => { 
	  		    	$("#changePhotoModalEquipo").modal("toggle");
	  		    	$(table+" img.equipoImg").attr("src", $("#changePhotoModalEquipo div div form img.equipoImg").attr("src"));
	  		    },
  		        error: e => console.log(e)
		    });
	    });
});