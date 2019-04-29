Feature: Bind to player instances

	Scenario: Start the server
		Given the server is created
		When the server is started
		Then the server is running
		
	Scenario Outline: Players are dealt their cards
		Given player <player> has joined
		When the cards have been dealt
		Then player <player> has <cards> cards
		
		Examples:
			| player | cards |
			|    1   |   8   |
			|    2   |   8   |
			
	Scenario Outline: Player draws story card
		Given it is player <player> turn
		When they draw from the story deck
		Then the story card <storyCardName> is drawn
		
		Examples:
			| player |   storyCardName   |
			|    1   | 'Chivalrous Deed' |
			|    2   |    'Boar Hunt'    |
		
#	Scenario Outline: Player plays a card
#		Given it is player <player> turn
#		And player <player> has <cardName>
#		When player <player> plays that card
#		Then player <player> has <cards> cards left
#		
#		Examples:
#			| player |   cardName  | cards |
#			|    2   |   "Saxons"  |   7   |
#			|    2   |   "Boar"    |   6   |
#			|    1   | "Battle-ax" |   7   |
#			|    1   |   "Lance"   |   6   |
			
#	Scenario Outline: Player ranks up
#		Given player <player> has rank <currentRank>
#		When they gain more shields
#		Then they gain the rank <nextRank>
#		
#		Examples:
#			| player | currentRank |      nextRank     |
#			|    1   |   "Squire"  |      "Knight"     |
#			|    1   |   "Knight"  | "Champion Knight" |
		
	#This is since the server will shutdown otherwise
	#And the clients cannot continue without a server
	Scenario: Run for some amount of time
		Given clients exist
		When server waits for 2 minutes and 40 seconds
		Then the server can shutdown