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

Given path 'salas/add'
And request { id: 0, name: 'Sala Ejemplo', maxSize: 5, descrip: 'Description' }
And header X-CSRF-TOKEN = csrf
And header Accept = 'application/json'
When method post
Then status 200
* def id = response

Scenario: Get newly created room
	Given path 'salas/'
	When method get
    Then status 200
    * string response = response    
    * def str = "div#salaIteracion div.iteration div.table" + id + " div.sala-left div.row div.salaName"
    * def html = util.selectHtml(response, str);
    * print html
    And match html contains 'Sala Ejemplo'
    
