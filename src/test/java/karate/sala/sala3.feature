Feature: Deleting a room

Background:
* url baseUrl
* call read('../login/login1.feature')
* def util = Java.type('karate.KarateTests')
 
	Given path 'login'
	When method get
	Then status 200
	* string response = response    
	* def csrf = util.selectAttribute(response, "input[name=_csrf]", "value");
	* print csrf
 
	Given path 'salas/add'
	And request { id: 0, name: 'Sala Auxiliar Para borrar', maxSize: 5, descrip: 'Description' }
	And header X-CSRF-TOKEN = csrf
	And header Accept = 'application/json'
	When method post
	Then status 200
	* def id = response

Scenario: Deleting a room
	Given path 'salas/del/'+id
	And request {}
	And header X-CSRF-TOKEN = csrf
	When method post
	Then status 200
	And match response == "exito"
