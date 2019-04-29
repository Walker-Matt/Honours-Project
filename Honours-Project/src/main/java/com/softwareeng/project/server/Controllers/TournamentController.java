package com.softwareeng.project.server.Controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.softwareeng.project.commands.ServerCommand;
import com.softwareeng.project.server.Models.AIPlayerModel;
import com.softwareeng.project.server.Models.AdventureDeckModel;
import com.softwareeng.project.server.Models.AllyCardModel;
import com.softwareeng.project.server.Models.CardModel;
import com.softwareeng.project.server.Models.FoeCardModel;
import com.softwareeng.project.server.Models.PlayerModel;
import com.softwareeng.project.server.Models.TournamentCardModel;
import com.softwareeng.project.server.ClientHandler;


public class TournamentController {
	final static Logger log = Logger.getLogger(TournamentController.class);

	CardController cardCon;
	HandController handCon;
	DeckController deckCon = new DeckController();
	PlayerController playerCon;
	ArrayList<PlayerModel> parPlayers;
	ArrayList<PlayerModel> theWinners;
	ArrayList<ClientHandler> clients;

    public TournamentController(ArrayList<ClientHandler> c) {
        clients = c;

        parPlayers = new ArrayList<PlayerModel>();
        theWinners = new ArrayList<PlayerModel>();

        handCon = new HandController();
        playerCon = new PlayerController(clients);
        cardCon = new CardController(clients);
    }

	// This function is to be called in the game controller and no logic but
	// assigning the shields is out of this controller
	public HashMap<Integer, Integer> startTournament(TournamentCardModel card, ArrayList<PlayerModel> players,
			PlayerModel curPlayer, AdventureDeckModel discardDeck, AdventureDeckModel advDeck) {
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

		decideParticipatingPlayers(curPlayer, players,card);
		for (PlayerModel p : players) {
			if (p.isActive)
				parPlayers.add(p);
		}

        //Tournament can't run with 1 player
        if (parPlayers.size() <= 1) {
            //Double check if the ArrayList returned is empty in GameController**
            log.info("Not enough player participating so no good");
            return result;
        }

        //Players draw from adventure deck
        handOutOneCard(parPlayers, curPlayer.playerId, advDeck, discardDeck);

        //AT MOST 2 Rounds
        for (int i = 0; i < 2; i++) {
            playTournamentRound(players, curPlayer, i, card);

            //display players cards
            for (PlayerModel p : players) {
                for (CardModel c : p.hand.selectedCards) {
                	sendMessageToAll(ServerCommand.ADD_CARD_TO_PLAY_AREA, p.playerId + " " + c.cardId + " " + c.imageUrl);
                }
            }

            theWinners = getWinners(parPlayers, card.cardName, players);

			String winners = "";
			for (PlayerModel p : theWinners) {
				winners += p.playerName + ", ";
			}

			sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, winners + " won the tournament");
			log.info(winners + " won the tournament");

			sleep(2);

			// discard
			for (PlayerModel p : parPlayers) {
				discardWeaponCards(p, discardDeck);
			}


            if (theWinners.size() == 1) {
                break;
            }
        }

        //discard Amour
        for (PlayerModel p : players) {
            discardAmour(p, discardDeck);
        }

		for (PlayerModel p : theWinners) {
			result.put(p.playerId, parPlayers.size());
		}
		return result;
	}

	// Puts the participating players in a array list
	private void decideParticipatingPlayers(PlayerModel curPlayer, ArrayList<PlayerModel> players,TournamentCardModel card) {
		PlayerModel currPlayer = curPlayer;
		for (int i = 0; i < players.size(); i++) {
			if (currPlayer instanceof AIPlayerModel) {
				currPlayer.isActive = ((AIPlayerModel) currPlayer).doIParticipateInTournament(players,card);
				if (currPlayer.isActive) {
					log.info(currPlayer.playerName + " is participating");
					sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " is participating");
				} else {
					log.info(currPlayer.playerName + " is not participating");
					sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " is not participating");
				}
				sleep(2);
			} else {
				boolean promptResult = false;

				promptResult = promptYesNo(currPlayer.playerId, "Would you like to participate in this tournament?");

				if (promptResult) {
					log.info(currPlayer.playerName + " is participating");
					sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " is participating");
					currPlayer.isActive = true;
				} else {
					log.info(currPlayer.playerName + " is not participating");
					sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " is not participating");
					currPlayer.isActive = false;
				}
				sleep(2);
			}
			currPlayer = playerCon.nextPlayer(players, currPlayer);
		}
	}
    // AI has been set up in this function
    public void playTournamentRound(ArrayList<PlayerModel> players, PlayerModel curPlayer, int roundNum, TournamentCardModel card) {
        PlayerModel currPlayer = curPlayer;
        for (int q = 0; q < players.size(); q++) {
            if (currPlayer.isActive) {
                //playerCon.showCurrentPlayersCards("Player " + (currPlayer.playerId + 1) + "'s turn", players, currPlayer.playerId);

				ArrayList<Integer> selectedCardIds = new ArrayList<>();
				ArrayList<CardModel> cardsSelected = new ArrayList<>();

				while (true) {
					sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, "Please select your cards to use for Round " + (roundNum + 1));

					selectedCardIds.clear();
					cardsSelected.clear();

					if (currPlayer instanceof AIPlayerModel) {

						cardsSelected = ((AIPlayerModel) currPlayer).getCardsToPlayForTournament(players,card);
						for (CardModel cardModel : cardsSelected) {
							selectedCardIds.add(cardModel.cardId);
						}

						handCon.addClickCardToSelectedHand(currPlayer, selectedCardIds);
					} else {
						selectedCardIds = getCardsToPlay(currPlayer.playerId);
						cardsSelected = playerCon.getCardsUsingIds(currPlayer, selectedCardIds);

						handCon.addClickCardToSelectedHand(currPlayer, selectedCardIds);

						// PLAYERS CANT PLAY FOE OR DUPLICATE WEAPONS
						// TODO MORE THAN 1 AMOUR
						if (validatePlayerCards(cardsSelected)) {
							break;
						} else {
							// invalid cards played
							// remove cards from selected hand
							// reprompt
							handCon.removeClickCardFromSelectedHand(currPlayer, selectedCardIds);

							sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, "Invalid cards played!");

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}

				String cardsForTourn = currPlayer.playerName + " played ";
				for (CardModel c : cardsSelected) {
					sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_HAND, currPlayer.playerId + " " + c.cardId);
					cardsForTourn += c.cardName + ",";
				}

                log.info(cardsForTourn);
            }
            currPlayer = playerCon.nextPlayer(players, currPlayer);
        }
    }

    //Gets the players battle points including the rank battle points addition
    public int getPlayerBattlePoints(PlayerModel player, String cardName, ArrayList<PlayerModel> players) {
        int points = 0;
        ArrayList<CardModel> cardsInPlay = getCardsInPlay(players);
        for (CardModel c : player.hand.selectedCards) {
            points += cardCon.getBattlePoints(cardName, c, cardsInPlay);
        }
        points += player.rank.battlePoints;
        return points;
    }

    //Gets the winner according to battle points and returns the winners in a array list
    //Parameters might need to be changed according to the game rules
    public ArrayList<PlayerModel> getWinners(ArrayList<PlayerModel> parPlayers, String cardName, ArrayList<PlayerModel> players) {

		ArrayList<Integer> theBattlePoints = new ArrayList<Integer>();
		ArrayList<PlayerModel> theWinners = new ArrayList<PlayerModel>();

        for (PlayerModel p : parPlayers) {
            int points = getPlayerBattlePoints(p, cardName, players);

			theBattlePoints.add(points);
			log.info(p.playerName + " total points played : " + points);
		}

        int max = Collections.max(theBattlePoints);
        for (PlayerModel p : parPlayers) {
            if (getPlayerBattlePoints(p, cardName, players) == max) {
                theWinners.add(p);
            } else {
                p.isActive = false;
            }
        }
        return theWinners;
    }

	public void discardWeaponCards(PlayerModel player, AdventureDeckModel discardDeck) {
		// remove all weapon cards
		ArrayList<CardModel> cards = player.hand.selectedCards;
		int size = cards.size();

		for (int i = 0; i < size; i++) {
			CardModel c = cards.get(0);
			if (!(c instanceof AllyCardModel)) {
				handCon.discardFromSelected(player, c, discardDeck);
				//UICon.removeCardFromPlayArea(player.playerId, c.cardId);
				sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_PLAY_AREA, player.playerId + " " + c.cardId);
			}
		}
	}

	public void discardAmour(PlayerModel player, AdventureDeckModel discardDeck) {
		// remove all amour
		ArrayList<CardModel> cards = player.hand.selectedCards;
		int size = cards.size();
		int counter = 0;

		for (int i = 0; i < size; i++) {
			CardModel c = cards.get(counter);
			if (c.cardName == "Amour") {
				handCon.discardFromSelected(player, c, discardDeck);
				//UICon.removeCardFromPlayArea(player.playerId, c.cardId);
				sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_PLAY_AREA, player.playerId + " " + c.cardId);
			} else {
				counter++;
			}
		}
	}

	public boolean validatePlayerCards(ArrayList<CardModel> cards) {
		HashSet<CardModel> uniqueSet = new HashSet<>();

		if (cards.size() == 0)
			return true;

		for (CardModel c : cards) {
			if (c instanceof FoeCardModel) {
				return false;
			}
			uniqueSet.add(c);
		}

		if (uniqueSet.size() != cards.size()) { // check for duplicates
			return false;
		}

		return true;
	}

	public void handOutOneCard(ArrayList<PlayerModel> players, int originalPlayerId, AdventureDeckModel adventureDeck,
			AdventureDeckModel discardDeck) {
		// hand out one card to every player participating as per quest rules
		for (int i = 0; i < players.size(); i++) {
			PlayerModel p = players.get(i);

			if (p.isActive) {
				log.info(p.playerName + " receives a card for participating");

                CardModel c = handCon.drawACardAndShow(p, originalPlayerId, adventureDeck);
                sendMessageToAllAddCard(p.playerId, c);
                //check card count
                playerCon.tooManyCards(p, players, discardDeck);
            }
        }
    }

    //private utility function made here to make code cleaner
    private ArrayList<CardModel> getCardsInPlay(ArrayList<PlayerModel> players) {
        ArrayList<CardModel> cardsInPlay = new ArrayList<>();

        for (PlayerModel p : players) {
            for (CardModel c : p.hand.selectedCards) {
                cardsInPlay.add(c);
            }
        }
        return cardsInPlay;
    }

    public void sendMessageToAll(ServerCommand s, String message) {
        for (ClientHandler cH : clients) {
            cH.out.println(s + " " + message);
        }
    }

    public void sendMessageToAllAddCard(int playerId, CardModel card) {
        for (ClientHandler cH : clients) {
            int currPlayer = cH.getId() - 1;
            cH.out.println(ServerCommand.ADD_CARD_TO_HAND + " " + playerId + " " + currPlayer + " " + card.cardId + " " + card.imageUrl);
        }
    }

    public ArrayList<Integer> getCardsToPlay(int sponsorId) {
        ArrayList<Integer> cardIds = new ArrayList<Integer>();
        clients.get(sponsorId).out.println(ServerCommand.PROMPT_PLAY_CARDS + " " + sponsorId);

        String cards = "";

        while (cards == "") {
            cards = clients.get(sponsorId).checkForMessageString();
        }

        cards = cards.replace("[", "");
        cards = cards.replace("]", "");
        cards = cards.replace(" ", "");

        if (cards.equals(""))
            return cardIds;

        String[] cardArray = cards.split(",");

        for (String string : cardArray) {
            cardIds.add(Integer.parseInt(string));
        }
        return cardIds;
    }

    public ArrayList<Integer> getCardsToDiscard(int playerID) {
    	ArrayList<Integer> cardIds = new ArrayList<Integer>();
        clients.get(playerID).out.println(ServerCommand.PROMPT_TEST_DISCARD + " " + playerID);
        
        String cards = "";
        
        while(cards == "") {
        	cards = clients.get(playerID).checkForMessageString();
        }
        
        cards = cards.replace("[", "");
        cards = cards.replace("]", "");
        cards = cards.replace(" ", "");
        
        if(cards.equals(""))
        	return cardIds;
        
        String[] cardArray = cards.split(",");
        
        for (String string : cardArray) {
			cardIds.add(Integer.parseInt(string));
		}
        return cardIds;
    }
    
    public boolean promptYesNo(int i, String message) {
        clients.get(i).out.println(ServerCommand.SET_MESSAGE_BOX + " " + message);

        boolean promptResult = false;

        clients.get(i).out.println(ServerCommand.PROMPT_YES_NO + " " + i);

        while (promptResult == false) {
            String result = clients.get(i).checkForMessageString();
            if (result.equals("true")) {
                promptResult = true;
            } else if (result.equals("false")) {
                break;
            }
        }

        return promptResult;
    }
    
    public void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
