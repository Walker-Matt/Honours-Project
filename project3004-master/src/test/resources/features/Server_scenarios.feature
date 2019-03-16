Feature: Bind to player instances

	Scenario: Start the server
		Given the server is created
		When the server is started
		Then the server is running

	Scenario Outline: Bind to a player instance
		Given client <instance> is connected
		When player <instance> exists
		Then player <instance> is bound
		
		Examples:
			| instance |
			|    1     |
			|    2     |