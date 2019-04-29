Feature: Player two plays Quest

	Scenario: Connect to the game
		Given I wait for player one to setup the game
		When I click on the connect button
		Then I connect to the game
		And the game starts
		
	Scenario: Draw a story card
		Given I wait for my turn
		When I draw from the story deck
		Then the card 'Boar Hunt' is drawn
		
	Scenario: Sponsor a quest
		Given I am asked if I want to sponsor the quest
		When I choose yes
		Then I am asked to setup stage 1
		And I play the foe 'Saxons'
		Then I am asked to setup stage 2
		And I play the foe 'Boar'
		Then I end my turn
		
	Scenario: Sponsor a quest
		Given I wait for my turn
		And I am asked if I want to sponsor the quest
		When I choose yes
		Then I am asked to setup stage 1
		And I play the foe 'Thieves'
		Then I am asked to setup stage 2
		And I play the foe 'Saxon Knight'
		Then I am asked to setup stage 3
		And I play the foe 'Evil Knight'
		Then I wait for my turn
		And I discard 11 extra cards
		
	Scenario: Draw a story card
		Given I wait for my turn
		When I draw from the story deck
		Then the card 'Defend the Queen\'s Honor' is drawn
		
	Scenario: Sponsor a quest
		Given I am asked if I want to sponsor the quest
		When I choose yes
		Then I am asked to setup stage 1
		And I play the foe 'Saxons'
		Then I am asked to setup stage 2
		And I play the foe 'Robber Knight'
		Then I am asked to setup stage 3
		And I play the foe 'Black Knight'
		Then I am asked to setup stage 4
		And I play nothing
		Then I end my turn
		And I wait 10 seconds