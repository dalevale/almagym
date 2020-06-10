Feature: Modify a room

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
	And request { id: 0, name: 'Sala Ejemplo modify', maxSize: 5, descrip: 'Description' }
	And header X-CSRF-TOKEN = csrf
	And header Accept = 'application/json'
	When method post
	Then status 200
	* def idCreated = response

Scenario: Modify created room
	Given path 'salas/edit' 
	And request { id: idCreated, name: 'Sala Ejemplo modified', maxSize: 5, descrip: 'Description' }
	And header X-CSRF-TOKEN = csrf
	And header Accept = 'application/json'
	When method post
	Then status 200
	And match response == "exito" 
    
