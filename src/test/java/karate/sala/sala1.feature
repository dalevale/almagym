Feature: Fetch sala html

Background:
* url baseUrl
* call read('../login/login1.feature')
* def util = Java.type('karate.KarateTests')

Scenario: Get HTML page 'sala'
    Given path 'salas/'
    When method get
    Then status 200
    * string response = response    
    * def h1s = util.selectHtml(response, "div.row.h2");
    * print h1s
    And match h1s contains 'Salas'
