package com.softwareeng.project.server.Controllers;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.softwareeng.project.commands.ServerCommand;
import com.softwareeng.project.server.Models.AIPlayerModel;
import com.softwareeng.project.server.Models.AdventureDeckModel;
import com.softwareeng.project.server.Models.AllyCardModel;
import com.softwareeng.project.server.Models.BattleCardModel;
import com.softwareeng.project.server.Models.CardModel;
import com.softwareeng.project.server.Models.EventCardModel;
import com.softwareeng.project.server.Models.FoeCardModel;
import com.softwareeng.project.server.Models.PlayerModel;
import com.softwareeng.project.server.ClientHandler;


public class EventController {
    final static Logger log = Logger.getLogger(EventController.class);

    HandController handCon;
    DeckController deckCon = new DeckController();
    PlayerController playerCon;
    ArrayList<ClientHandler> clients;

    public EventController(ArrayList<ClientHandler> c) {
        clients = c;
        handCon = new HandController();
        playerCon = new PlayerController(clients);
    }

    public boolean startEvent(ArrayList<PlayerModel> players, PlayerModel curPlayer, EventCardModel card, AdventureDeckModel discardDeck, AdventureDeckModel advDeck) {

        //Chivalrous Deed Event
        if ("Chivalrous Deed" == card.cardName) {
            int lowestShields = lowestNumShields(players);

            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).rank.battlePoints == minRank(players)) {
                    if (players.get(i).shields == lowestShields) {
                        players.get(i).shields = players.get(i).shields + 3;

                        sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, "3 shields added to " + players.get(i).playerName);
                        log.info("3 shields added to " + players.get(i).playerName);

                        sendMessageToAll(ServerCommand.ADD_SHIELDS, i + " " + 3);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        //Plague Event
        else if ("Plague" == card.cardName) {
            int shieldsLost = 0;

            if (curPlayer.shields >= 2) {
                curPlayer.shields = (curPlayer.shields) - 2;
                shieldsLost = 2;
            } else if (curPlayer.shields == 1) {
                curPlayer.shields = (curPlayer.shields) - 1;
                shieldsLost = 1;
            }

            sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, curPlayer.playerName + " lost " + shieldsLost + " shield(s)");
            log.info(curPlayer.playerName + " lost " + shieldsLost + " shield(s)");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sendMessageToAll(ServerCommand.REMOVE_SHIELDS, curPlayer.playerId + " " + shieldsLost);
        }

        //Pox Event
        else if ("Pox" == card.cardName) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i) != curPlayer) {
                    if (players.get(i).shields >= 1) {
                        players.get(i).shields = (players.get(i).shields) - 1;

                        sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, players.get(i).playerName + " lost 1 shield");
                        log.info(players.get(i).playerName + " lost 1 shield");

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        sendMessageToAll(ServerCommand.REMOVE_SHIELDS, i + " " + 1);
                    } else {
                        continue;
                    }
                }
            }
        }
        //Court Called to Camelot Event
        else if ("Court Called to Camelot" == card.cardName) {
            for (int i = 0; i < players.size(); i++) {
                int size = players.get(i).hand.selectedCards.size();

                sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, players.get(i).playerName + " lost all their allies in play");
                log.info(players.get(i).playerName + " lost all their allies in play");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < size; j++) {
                    CardModel c = players.get(i).hand.selectedCards.remove(0);
                    discardDeck.getCards().add(c);

                    log.info(players.get(i).playerName + " lost " + c.cardName);

                    sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_PLAY_AREA, i + " " + c.cardId);
                }
            }
        } else if ("King's Recognition" == card.cardName) {
            sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, "The next quest winners wil receive 2 bonus shields");
            log.info("The next quest winners wil receive 2 bonus shields");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        } else if ("Queen's Favor" == card.cardName) {
            int minRank = minRank(players);
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).rank.battlePoints == minRank) {
                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, players.get(i).playerName + " receives 2 adventure cards");
                    log.info(players.get(i).playerName + " receives 2 adventure cards");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    CardModel c = handCon.drawACardAndShow(players.get(i), curPlayer.playerId, advDeck);
                    sendMessageToAllAddCard(i, c);
                    c = handCon.drawACardAndShow(players.get(i), curPlayer.playerId, advDeck);
                    sendMessageToAllAddCard(i, c);
                    playerCon.tooManyCards(players.get(i), players, discardDeck);
                }
            }

        } else if ("King's Call to Arms" == card.cardName) {
            int maxRank = maxRank(players);

            for (int i = 0; i < players.size(); i++) {
                boolean foundWeapon = false;

                if (players.get(i).rank.battlePoints == maxRank) {

                    int size = players.get(i).hand.playerHand.size();
                    int counter = 0;
                    //Check if the player has a weapon card
                    for (int x = 0; x < size; x++) {
                        CardModel c = players.get(i).hand.playerHand.get(counter);
                        if (c instanceof BattleCardModel && !(c instanceof AllyCardModel) && !(c instanceof FoeCardModel)) {
                            foundWeapon = true;

                            log.info(players.get(i).playerName + " must discard 1 weapon card");

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            ArrayList<Integer> selectedCardIds = new ArrayList<>();

                            if(players.get(i) instanceof AIPlayerModel) {
                            	//do nothing
                            } else {
                            	while (true) {
                                    selectedCardIds.clear();

                                    selectedCardIds = getCardsToDiscard(i);

                                    ArrayList<CardModel> listOfCards = playerCon.getCardsUsingIds(players.get(i), selectedCardIds);

                                    if (listOfCards.size() == 1 && ((listOfCards.get(0) instanceof BattleCardModel) && !(listOfCards.get(0) instanceof AllyCardModel) && !(listOfCards.get(0) instanceof FoeCardModel))) {
                                        log.info(players.get(i).playerName + " discards " + listOfCards.get(0).cardName);
                                        break;
                                    } else {
                                        sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, "Invalid cards played!");

                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } 

                            playerCon.addClickCardToDiscard(players.get(i), selectedCardIds, discardDeck);

                            for (int ids : selectedCardIds) {
                                sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_HAND, i + " " + ids);
                            }
                            break;
                        } else {
                            counter++;
                        }
                    }

                    if (!foundWeapon) {
                        //discard 2 foe
                        log.info(players.get(i).playerName + " must discard 2 foe card");

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ArrayList<Integer> selectedCardIds = new ArrayList<>();

                        while (true) {
                            selectedCardIds.clear();

                            selectedCardIds = getCardsToDiscard(i);

                            ArrayList<CardModel> listOfCards = playerCon.getCardsUsingIds(players.get(i), selectedCardIds);

                            if (listOfCards.size() == 2 && ((listOfCards.get(0) instanceof FoeCardModel) && (listOfCards.get(1) instanceof FoeCardModel))) {
                                log.info(players.get(i) + " discards " + listOfCards.get(0).cardName + " and " + listOfCards.get(1).cardName);
                                break;
                            } else {
                                sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, "Invalid cards played!");

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        playerCon.addClickCardToDiscard(players.get(i), selectedCardIds, discardDeck);

                        for (int ids : selectedCardIds) {
                            //UICon.removeSelectedCardFromPlayer(i, ids);
                            sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_HAND, i + " " + ids);
                        }
                    }
                }
            }
        }
        //Prosperity Throughout the Realm event card
        else if ("Prosperity Throughout the Realm" == card.cardName) {
            for (int i = 0; i < players.size(); i++) {
                sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, "All players draw 2 cards");
                log.info("All players drew 2 cards");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                CardModel c = handCon.drawACardAndShow(players.get(i), curPlayer.playerId, advDeck);
                sendMessageToAllAddCard(i, c);
                c = handCon.drawACardAndShow(players.get(i), curPlayer.playerId, advDeck);
                sendMessageToAllAddCard(i, c);
                playerCon.tooManyCards(players.get(i), players, discardDeck);
            }
        }

        return false;
    }

    //Must be tested
    //Function that finds the lowest rank's battlePoints
    public int minRank(ArrayList<PlayerModel> players) {

        ArrayList<Integer> ranked = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            ranked.add(players.get(i).rank.battlePoints);
        }
        int lowestRank = Collections.min(ranked);
        return lowestRank;
    }

    //Finds the highestRank battlepoints
    public int maxRank(ArrayList<PlayerModel> players) {

        ArrayList<Integer> ranked = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            ranked.add(players.get(i).rank.battlePoints);
        }
        int highestRank = Collections.max(ranked);
        return highestRank;
    }

    //Finds the lowest number of shields that a player has from all the players in the game
    public int lowestNumShields(ArrayList<PlayerModel> players) {
        ArrayList<Integer> shieldsList = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            shieldsList.add(players.get(i).shields);
        }
        int lowestShields = Collections.min(shieldsList);
        return lowestShields;
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

    public ArrayList<Integer> getCardsToDiscard(int playerID) {
        ArrayList<Integer> cardIds = new ArrayList<Integer>();
        clients.get(playerID).out.println(ServerCommand.PROMPT_TEST_DISCARD + " " + playerID);

        String cards = "";

        while (cards == "") {
            cards = clients.get(playerID).checkForMessageString();
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

}



