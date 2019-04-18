Feature: Player one plays Quest

	Scenario: Connect to the game
		Given I click on the connect button
		Then I connect to the game
	
	Scenario: Begin a two player game
		Given I have to choose the number of players
		When I choose a two player game
		Then the game starts
		
	Scenario: Draw a story card
		Given I wait for my turn
		When I draw from the story deck
		Then the card 'Chivalrous Deed' is drawn
		And I end my turn
		
	Scenario: Participate in a quest
		Given I wait for my turn
		When I am asked if I want to participate
		And I choose yes
		Then I am asked to play stage 1
		And I play the weapon 'Battle-ax'
		Then I am asked to play stage 2
		And I play the weapon 'Lance'
		
	Scenario: Draw a story card
		Given I wait for my turn
		When I draw from the story deck
		Then the card 'Journey through the Enchanted Forest' is drawn
		And I am asked if I want to sponsor the quest
		Then I choose no
		
	Scenario: Participate in a quest
		Given I wait for my turn
		When I am asked if I want to participate
		And I choose yes
		Then I am asked to play stage 1
		And I play the weapon 'Sword'
		Then I am asked to play stage 2
		And I play the weapon 'Lance'
		Then I am asked to play stage 3
		And I play the weapon 'Excalibur'
		And I end my turn
		
	Scenario: Participate in a quest
		Given I wait for my turn
		When I am asked if I want to participate
		And I choose yes
		Then I am asked to play stage 1
		And I play the weapon 'Battle-ax'
		Then I am asked to play stage 2
		And I play the weapon 'Lance'
		Then I am asked to play stage 3
		And I play the weapon 'Excalibur'
		Then I am asked to play stage 4
		And I play nothing
		And I wait 10 seconds