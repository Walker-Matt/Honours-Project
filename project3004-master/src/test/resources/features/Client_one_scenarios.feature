Feature: Client one hosts and plays a game

	Scenario: Successful connection to server
		Given the ip address has been entered
		And the port number has been entered
		When the connect button is pressed
		Then the client connects to the server
	
	Scenario: Begin a two player game
		Given the client must choose the number of players
		When the client specifies a two player game
		Then the game has begun