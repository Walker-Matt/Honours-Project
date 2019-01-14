package com.softwareeng.project.server.Controllers;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.softwareeng.project.commands.ServerCommand;
import com.softwareeng.project.server.Models.AIPlayerModel;
import com.softwareeng.project.server.Models.AllyCardModel;
import com.softwareeng.project.server.Models.CardModel;
import com.softwareeng.project.server.Models.DeckModel;
import com.softwareeng.project.server.Models.PlayerModel;
import com.softwareeng.project.server.Models.RankCardModel;
import com.softwareeng.project.server.ClientHandler;

public class PlayerController {
    final static Logger log = Logger.getLogger(GameController.class);

    DeckController deckCon = new DeckController();
    HandController handCon;
    ArrayList<ClientHandler> clients;


    public PlayerController(ArrayList<ClientHandler> c) {
        clients = c;
        handCon = new HandController();
    }

    public PlayerModel nextPlayer(ArrayList<PlayerModel> players, PlayerModel currentPlayer) {
        int currPlayerIndex = players.indexOf(currentPlayer);
        if (currPlayerIndex == 3) { //makes sure that it loops properly
            currPlayerIndex = -1;
        }
        return players.get(currPlayerIndex + 1);
    }

    public PlayerModel getPlayerUsingId(ArrayList<PlayerModel> players, int id) {
        for (PlayerModel p : players) {
            if (p.playerId == id)
                return p;
        }

        return null;
    }

    public void setAllActive(ArrayList<PlayerModel> players) {
        for (PlayerModel p : players) {
            p.isActive = true;
        }
    }

    public int checkHandSize(PlayerModel p) {
        return p.hand.playerHand.size() - 12;
    }

    //AI Player get rid of any card that has 2 or more of them first from left to right of hand
    public void tooManyCards(PlayerModel p, ArrayList<PlayerModel> players, DeckModel discardDeck) {
        int cardsAboveMax = checkHandSize(p);
        while (cardsAboveMax > 0) {
            log.info(p.playerName + " has " + cardsAboveMax + " cards too many");
            ArrayList<Integer> selectedCardIds = new ArrayList<>();

            if (p instanceof AIPlayerModel) {
                selectedCardIds = ((AIPlayerModel) p).discardToManyCards();

                addClickCardToDiscard(p, selectedCardIds, discardDeck);

                for (int ids : selectedCardIds) {
                    sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_HAND, p.playerId + " " + ids, clients);
                }
            } else {
                clients.get(p.playerId).out.println(ServerCommand.SET_MESSAGE_BOX + " " + "You have " + cardsAboveMax + " cards too many");

                //function that will call the client and get the ids
                selectedCardIds = getCardsToDiscard(p.playerId, clients);

                if (selectedCardIds.get(0) == -100) { //allycards played
                    selectedCardIds.remove(0);
                    ArrayList<CardModel> cards = getCardsUsingIds(p, selectedCardIds);


                    boolean allyPlayed = true;

                    for (CardModel c : cards) {
                        if (!(c instanceof AllyCardModel)) {
                            allyPlayed = false;
                        }
                    }

                    if (allyPlayed) {
                        handCon.addClickCardToSelectedHand(p, selectedCardIds);

                        for (CardModel c : cards) {
                            sendMessageToAll(ServerCommand.ADD_CARD_TO_PLAY_AREA, p.playerId + " " + c.cardId + " " + c.imageUrl, clients);
                        }

                        for (int ids : selectedCardIds) {
                            sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_HAND, p.playerId + " " + ids, clients);
                        }
                    } else {
                        clients.get(p.playerId).out.println(ServerCommand.SET_MESSAGE_BOX + " " + "Invalid cards played");

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    addClickCardToDiscard(p, selectedCardIds, discardDeck);

                    for (int ids : selectedCardIds) {
                        sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_HAND, p.playerId + " " + ids, clients);
                    }
                }
            }

            cardsAboveMax = checkHandSize(p);
        }
    }

    public void addClickCardToDiscard(PlayerModel currentPlayer, ArrayList<Integer> listOfIds, DeckModel discardDeck) {
        for (Integer idOfClickedCard : listOfIds) {
            for (CardModel adventureCard : currentPlayer.hand.playerHand) {
                if (adventureCard.cardId == idOfClickedCard) {
                    deckCon.discardCard(adventureCard, discardDeck);
                    currentPlayer.hand.playerHand.remove(adventureCard);

                    log.info(currentPlayer.playerName + " discard " + adventureCard.cardName);

                    break;
                }
            }
        }
    }

    public ArrayList<CardModel> getCardsUsingIds(PlayerModel currentPlayer, ArrayList<Integer> listOfIds) {
        ArrayList<CardModel> result = new ArrayList<>();

        for (Integer idOfClickedCard : listOfIds) {
            for (CardModel adventureCard : currentPlayer.hand.playerHand) {
                if (adventureCard.cardId == idOfClickedCard) {
                    result.add(adventureCard);
                    break;
                }
            }
        }

        return result;
    }

    //Not tested
    public ArrayList<Integer> getIdsUsingCards(PlayerModel currentPlayer, ArrayList<CardModel> listOfCards) {
        ArrayList<Integer> result = new ArrayList<>();

        for (CardModel c : listOfCards) {
            int cardId = c.cardId;
            result.add(cardId);
        }
        return result;
    }


    public boolean awardShields(PlayerModel p, int shields) {
        //returns if the player has upgraded to a Knight of the Round Table  (aka game over)
        boolean gameEnd = false;
        int shieldsTillUpgrade = RankCardModel.shieldsRequiredForNextUpgrade(p.rank.cardName);

        p.shields += shields;

        if (p.shields >= shieldsTillUpgrade) {
            p.shields -= shieldsTillUpgrade;
            gameEnd = upgradeRank(p);
        }

        return gameEnd;
    }

    private boolean upgradeRank(PlayerModel p) {
        boolean endGame = false;
        switch (p.rank.cardName) {
            case "Squire":
                p.rank = new RankCardModel("Knight");
                break;
            case "Knight":
                p.rank = new RankCardModel("Champion Knight");
                break;
            case "Champion Knight":
                p.rank = new RankCardModel("Knight of the Round Table");
                endGame = true;
                break;
        }

        return endGame;
    }

    public void checkIfUpgradeNeeded(RankCardModel currentRank, PlayerModel p) {
        RankCardModel newRank = p.rank;

        for (ClientHandler cH : clients) {
            cH.out.println(ServerCommand.SET_SHIELDS + " " + p.playerId + " " + p.shields);
        }

        if (currentRank != newRank) { //the player's rank upgraded
            for (ClientHandler cH : clients) {
                cH.out.println(ServerCommand.CHANGE_PLAYER_RANK + " " + p.playerId + " " + newRank.cardId + " " + currentRank.cardId + " " + newRank.imageUrl);
            }

            log.info(p.playerName + " rank upgraded to a " + newRank.cardName);
        }
    }

    public ArrayList<Integer> getCardsToDiscard(int playerId, ArrayList<ClientHandler> clients) {
        ArrayList<Integer> cardIds = new ArrayList<Integer>();
        clients.get(playerId).out.println(ServerCommand.PROMPT_DISCARD_CARDS + " " + playerId);

        String cards = "";

        while (cards == "") {
            cards = clients.get(playerId).checkForMessageString();
        }

        cards = cards.replace("[", "");
        cards = cards.replace("]", "");
        cards = cards.replace(" ", "");

        String[] cardArray = cards.split(",");

        if (cardArray.length == 0) {
            return cardIds;
        }

        for (String string : cardArray) {
            cardIds.add(Integer.parseInt(string));
        }
        return cardIds;
    }

    public void sendMessageToAll(ServerCommand s, String message, ArrayList<ClientHandler> clients) {
        for (ClientHandler cH : clients) {
            cH.out.println(s + " " + message);
        }
    }
    
   
}


