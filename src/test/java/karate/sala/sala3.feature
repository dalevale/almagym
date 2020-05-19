Feature: Create new room

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

Scenario: Deleting a room
	Given path 'salas/del/1'
	And request {}
	And header X-CSRF-TOKEN = csrf
	When method post
	Then status 200
	And match response == "exito"
