Feature: Client two plays a game

	Scenario: Successful connection to server
		Given the ip address has been entered
		And the port number has been entered
		And client two waits for client one
		When the connect button is pressed
		Then the client connects to the server
		And the game has begun