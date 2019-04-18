package com.softwareeng.project.server.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.softwareeng.project.commands.ServerCommand;
import com.softwareeng.project.server.Models.*;
import com.softwareeng.project.server.ClientHandler;

public class QuestController {
    final static Logger log = Logger.getLogger(QuestController.class);

    ArrayList<ArrayList<CardModel>> stages;
    CardController cardCon;
    HandController handCon;
    DeckController deckCon = new DeckController();
    PlayerController playerCon;
    ArrayList<ClientHandler> clients;


    public QuestController(ArrayList<ClientHandler> cl) {
        clients = cl;
        playerCon = new PlayerController(clients);
        handCon = new HandController();
        cardCon = new CardController(clients);
    }

    public int initQuest(QuestCardModel card, PlayerModel currPlayer, ArrayList<PlayerModel> players) {

        int sponsorId = setUpSponsor(card.cardName, currPlayer, players, card.numOfStages);

        if (sponsorId == -1) {
            return sponsorId;
        }

        sponsorSetUpStages(card.cardName, players, sponsorId, card.numOfStages);

        decideParticipatingPlayers(sponsorId, players, card.numOfStages);
        return sponsorId;
    }

    public HashMap<Integer, Integer> playQuest(int sponsorId, int originalPlayerId, QuestCardModel card,
                                               ArrayList<PlayerModel> players, AdventureDeckModel adventureDeck, AdventureDeckModel discardDeck) {
        HashMap<Integer, Integer> questResult = new HashMap<>();

        int totalStageCards = 0;

        for (int i = 0; i < stages.size(); i++) {
            totalStageCards += stages.get(i).size();
        }

        // to participating players
        handOutOneCard(players, originalPlayerId, adventureDeck, discardDeck);

        for (int i = 0; i < card.numOfStages; i++) {
            ArrayList<CardModel> cardsForStage = stages.get(i);
            log.info("Stage " + (i + 1));

            if (cardsForStage.size() == 1 && cardsForStage.get(0) instanceof TestCardModel) {
                // Card is a testCard

                playQuestTest(sponsorId, i, players, cardsForStage.get(0));
            } else {
                // its a foe card + possible weapon cards

                playQuestFoe(sponsorId, i, players, card, cardsForStage, adventureDeck, discardDeck);
            }
        }

        // discard Amour
        for (PlayerModel p : players) {
            discardAmour(p, discardDeck);
        }

        String winnerNames = "";
        // Get the players still active aka the winners
        for (PlayerModel p : players) {
            if (p.isActive) {
                winnerNames += p.playerName + ", ";
                questResult.put(p.playerId, card.numOfStages);
            } else if (p.playerId == sponsorId) {
                // draw number of cards as per rules
                int numCardsToDraw = totalStageCards + card.numOfStages;

                for (int i = 0; i < numCardsToDraw; i++) {
                    CardModel c = handCon.drawACardAndShow(p, originalPlayerId, adventureDeck);
                    sendMessageToAllAddCard(p.playerId, c);
                }

                playerCon.tooManyCards(p, players, discardDeck);
            }
        }


        if (winnerNames != "") {
            sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, winnerNames + " won this quest");
            log.info(winnerNames + " won this quest");
        }


        sleep(1);

        sendMessageToAll(ServerCommand.REMOVE_ALL_CARDS_FROM_PLAY_AREA, "" + sponsorId);

        return questResult;
    }

    //modified for testing purposes
    private int setUpSponsor(String cardName, PlayerModel currPlayer, ArrayList<PlayerModel> players, int numStages) {
        stages = new ArrayList<>();
        int sponsorId = -1;
        PlayerModel tempPlayer = currPlayer;

        for (int i = 0; i < players.size(); i++) {
            if (tempPlayer instanceof AIPlayerModel) {
                AIPlayerModel tempAI = (AIPlayerModel) tempPlayer;

//                if (tempAI.doISponsorAQuest(numStages, players, currPlayer)) {
//                    // AI will sponsor
//                    sponsorId = tempAI.playerId;
//
//                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, tempAI.playerName + " accepted to sponsor");
//                    log.info(tempAI.playerName + " accepted to sponsor");
//                    break;
//                } else {
                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, tempAI.playerName + " declined to sponsor");
                    log.info(tempAI.playerName + " declined to sponsor");
//                }

                sleep(2);
            } else {
                boolean promptResult = false;

                promptResult = promptYesNo(tempPlayer.playerId, "Would you like to sponsor this quest?");

                if (promptResult) {
                    sponsorId = tempPlayer.playerId; // temp
                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, tempPlayer.playerName + " accepted to sponsor");

                    log.info(tempPlayer.playerName + " accepted to sponsor");
                    break;
                } else {
                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, tempPlayer.playerName + " declined to sponsor");

                    log.info(tempPlayer.playerName + " declined to sponsor");

                    tempPlayer = playerCon.nextPlayer(players, tempPlayer);
                }
            }
        }

        // nobody wants to sponsor
        if (sponsorId == -1) {
            sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, "Nobody sponsored this quest :(");
        }

        return sponsorId;
    }

    private void sponsorSetUpStages(String cardName, ArrayList<PlayerModel> players, int sponsorId, int numStages) {
        boolean testCardUsed = false;
        PlayerModel sponsor = playerCon.getPlayerUsingId(players, sponsorId);

        for (int i = 0; i < numStages; i++) {
            ArrayList<CardModel> stageCards = new ArrayList<>();
            ArrayList<CardModel> cardsPlayed = new ArrayList<>();
            ArrayList<Integer> selectedCardIds = new ArrayList<>();

            if (sponsor instanceof AIPlayerModel) {
                cardsPlayed = ((AIPlayerModel) sponsor).getCardsToPlayForQuest(numStages, i + 1);
                
                for (CardModel cardModel : cardsPlayed) {
					selectedCardIds.add(cardModel.cardId);
				}
                
                handCon.addClickCardToSelectedHand(sponsor, selectedCardIds);
            } else {
                //prompt for player to select cards

                boolean cardsOk = false;

                while (!cardsOk) {
                    clients.get(sponsorId).out.println(ServerCommand.SET_MESSAGE_BOX + " " + "Please select the cards for Stage " + (i + 1));

                    selectedCardIds.clear();
                    cardsPlayed.clear();

                    //function that will call the client and get the ids
                    selectedCardIds = getCardsToPlay(sponsorId);

                    cardsPlayed = playerCon.getCardsUsingIds(sponsor, selectedCardIds);

                    handCon.addClickCardToSelectedHand(sponsor, selectedCardIds);

                    if (cardsPlayed.size() == 1 && cardsPlayed.get(0) instanceof TestCardModel) {
                        testCardUsed = true;
                        cardsOk = true;
                    } else if (!validateStageCards(cardName, cardsPlayed, testCardUsed, i, players)) {
                        // invalid cards played
                        // remove cards from selected hand
                        // reprompt
                        handCon.removeClickCardFromSelectedHand(sponsor, selectedCardIds);

                        clients.get(sponsorId).out.println(ServerCommand.SET_MESSAGE_BOX + " " + "Invalid cards played!");

                        sleep(1);
                    } else {
                        cardsOk = true;
                    }
                }
            }

            String cardsLog = "Sponsor played for Stage " + (i + 1) + ": ";
            for (CardModel card : cardsPlayed) {
                sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_HAND, sponsorId + " " + card.cardId);

                cardsLog += card.cardName + ",";
            }

            stageCards.addAll(cardsPlayed);

            log.info(cardsLog);

            stages.add(stageCards);
        }
    }

    public void decideParticipatingPlayers(int sponsorId, ArrayList<PlayerModel> players, int numStages) {

        // error handling, if there is no sponsor the state
        if (-1 == sponsorId)
            return;

        //Double check index here
        PlayerModel currPlayer = players.get(sponsorId);
        for (int i = 0; i < players.size(); i++) {
            currPlayer = playerCon.nextPlayer(players, currPlayer);
            if (currPlayer.playerId != sponsorId) {
                if (currPlayer instanceof AIPlayerModel) {
                    currPlayer.isActive = ((AIPlayerModel) currPlayer).doIParticipateInQuest(numStages);

                    if (currPlayer.isActive) {
                        sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " is participating");
                        log.info(currPlayer.playerName + " is participating");
                    } else {
                        sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " is not participating");
                        log.info(currPlayer.playerName + " is not participating");
                    }
                    sleep(2);
                } else {

                    boolean promptResult = false;

                    promptResult = promptYesNo(currPlayer.playerId, "Would you like to participate in this quest?");

                    if (promptResult) {
                        log.info(currPlayer.playerName + " is participating");
                        currPlayer.isActive = true;
                    } else {
                        log.info(currPlayer.playerName + " is not participating");
                        currPlayer.isActive = false;
                    }
                }
            } else {
                // set the sponsor to false so he doesn't get a turn
                currPlayer.isActive = false;
            }
        }
    }

    public void playQuestTest(int sponsorId, int stageNum, ArrayList<PlayerModel> players, CardModel card) {
        TestCardModel testCard = (TestCardModel) card;

        log.info(testCard.cardName + " played");

        int minimumBid = 0;
        boolean biddingEnd = false;
        int biddingRound = 0;

        if (testCard.minimumBid == 0) {
            minimumBid = 3;
        } else {
            minimumBid = testCard.minimumBid;
        }

        PlayerModel currPlayer = players.get(sponsorId);

        //flip card right away
        sendMessageToAll(ServerCommand.ADD_CARD_TO_PLAY_AREA, sponsorId + " " + testCard.cardId + " " + testCard.imageUrl);

        while (!biddingEnd) {
            biddingRound++;
            for (int q = 0; q < players.size(); q++) {

                currPlayer = playerCon.nextPlayer(players, currPlayer);

                if (currPlayer.isActive) {
                    // prompt bid from player
                    // allow drop outs
                    int bids;

                    if (currPlayer instanceof AIPlayerModel) {
                        bids = ((AIPlayerModel) currPlayer).nextBid(biddingRound);
                    } else {
                        clients.get(currPlayer.playerId).out.println(ServerCommand.SET_MESSAGE_BOX + " " + "Please bid for Stage " + (stageNum + 1) + ". Atleast " + minimumBid + " bids required");


                        bids = getBids(currPlayer.playerId);
                    }

                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " bid " + bids + " bids");
                    log.info(currPlayer.playerName + " bid " + bids + " bids");

                    sleep(2);

                    if (bids >= minimumBid) {
                        sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " bid at or above the minimum(" + minimumBid + ")");
                        log.info(currPlayer.playerName + " bid above the minimum(" + minimumBid  + ")");

                        // player advances
                        // force the next player to bid higher
                        minimumBid = bids + 1;
                        sleep(2);

                        if (numberOfActivePlayers(players) == 1) {
                            biddingEnd = true;
                            break;
                        }

                    } else {
                        // player loses
                        //  animate loss
                        sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " bid below the minimum(" + (minimumBid - 1) + ")");
                        log.info(currPlayer.playerName + " bid below the minimum(" + (minimumBid - 1) + ")");

                        sleep(1);

                        currPlayer.isActive = false;

                        if (numberOfActivePlayers(players) == 1) {
                            biddingEnd = true;
                            break;
                        }
                    }
                }
            }
        }

        promptPlayerToDiscardRequiredCards(minimumBid, players, testCard, stageNum, biddingRound);
        
        for (CardModel c : stages.get(stageNum)) {
            sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_PLAY_AREA, sponsorId + " " + c.cardId);
        }
    }

    public void playQuestFoe(int sponsorId, int stageNum, ArrayList<PlayerModel> players, QuestCardModel card,
                             ArrayList<CardModel> cardsForStage, AdventureDeckModel adventureDeck, AdventureDeckModel discardDeck) {
        // Added parameter here (players)
        int stageBattlePoints = getStageBattlePoints(card.cardName, cardsForStage, players);

        PlayerModel currPlayer = players.get(sponsorId);
        for (int q = 0; q < players.size(); q++) {
            currPlayer = playerCon.nextPlayer(players, currPlayer);
            if (currPlayer.isActive) {
                ArrayList<CardModel> cardsSelected = new ArrayList<>();
                ArrayList<Integer> selectedCardIds = new ArrayList<>();

                if (currPlayer instanceof AIPlayerModel) {
                    selectedCardIds = ((AIPlayerModel) currPlayer).getNonSponsorCardsToPlayQuest(stageNum, card);
                    
                    cardsSelected = playerCon.getCardsUsingIds(currPlayer, selectedCardIds);
                    handCon.addClickCardToSelectedHand(currPlayer, selectedCardIds);
                } else {
                    while (true) {
                        clients.get(currPlayer.playerId).out.println(ServerCommand.SET_MESSAGE_BOX + " " + "Please select your cards to fight Stage " + (stageNum + 1));

                        selectedCardIds.clear();
                        cardsSelected.clear();

                        //function that will call the client and get the ids
                        selectedCardIds = getCardsToPlay(currPlayer.playerId);

                        cardsSelected = playerCon.getCardsUsingIds(currPlayer, selectedCardIds);

                        // PLAYERS CANT PLAY FOE OR DUPLICATE WEAPONS
                        // TODO MORE THAN 1 AMOUR
                        if (validatePlayerCards(cardsSelected)) {
                            handCon.addClickCardToSelectedHand(currPlayer, selectedCardIds);
                            break;
                        }

                    }
                }

                String playerCardsPlayed = currPlayer.playerName + " played against Stage " + (stageNum + 1) + ": ";
                for (CardModel c : cardsSelected) {
                    sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_HAND, currPlayer.playerId + " " + c.cardId);
                    sendMessageToAll(ServerCommand.ADD_CARD_TO_PLAY_AREA, currPlayer.playerId + " " + c.cardId + " " + c.imageUrl);
                    playerCardsPlayed += c.cardName + ",";
                }

                log.info(playerCardsPlayed);
            }
        }

        for (CardModel c : cardsForStage) {
            sendMessageToAll(ServerCommand.ADD_CARD_TO_PLAY_AREA, sponsorId + " " + c.cardId + " " + c.imageUrl);
        }

        sleep(2);

        getStageResults(players, sponsorId, stageBattlePoints, card, adventureDeck, discardDeck);

        for (CardModel c : stages.get(stageNum)) {
            sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_PLAY_AREA, sponsorId + " " + c.cardId);
        }

    }

    public void promptPlayerToDiscardRequiredCards(int minimumBid, ArrayList<PlayerModel> players, TestCardModel
            card, int stageNum, int roundNumber) {
        // player wins, must now discard cards
        int requiredDiscards = minimumBid - 1;

        for (PlayerModel p : players) {
            if (p.isActive) {
                PlayerModel winner = p;
                ArrayList<Integer> listOfIds = new ArrayList<>();
                ArrayList<CardModel> cards = new ArrayList<>();
                
                sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, p.playerName + " has won. Please discard " + requiredDiscards + " cards.");
                log.info(p.playerName + " has won. Please discard " + requiredDiscards + " cards.");
                
                if (winner instanceof AIPlayerModel) {
                    listOfIds = ((AIPlayerModel) winner).discardAfterWinningTest(roundNumber);
                    
                    cards = playerCon.getCardsUsingIds(winner, listOfIds);

                    for (Integer id : listOfIds) {
                        handCon.removeClickCardFromPlayerHand(winner, id);
					}
                    
                    String cardsDiscarded = winner.playerName + " discarded: ";
                    for (CardModel c : cards) {
                        sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_HAND, winner.playerId + " " + c.cardId);
                        cardsDiscarded += c.cardName + ",";
                    }

                    log.info(cardsDiscarded);
                } else {
                    while (true) {

                        listOfIds = getCardsToDiscard(winner.playerId);

                        cards = playerCon.getCardsUsingIds(winner, listOfIds);          

                        if (getPlayerStageBids(cards, card.cardName, winner, players) >= requiredDiscards) {
                            for (Integer id : listOfIds) {
                                handCon.removeClickCardFromPlayerHand(winner, id);
        					}
                        	
                            String cardsDiscarded = winner.playerName + " discarded: ";
                            for (CardModel c : cards) {
                                sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_HAND, winner.playerId + " " + c.cardId);
                                cardsDiscarded += c.cardName + ",";
                            }

                            log.info(cardsDiscarded);

                            break;
                        } else {
                            handCon.removeClickCardFromSelectedHand(winner, listOfIds);

                            clients.get(winner.playerId).out.println(ServerCommand.SET_MESSAGE_BOX + " " + "Not enough cards discarded");

                            sleep(2);
                        }
                    }
                }
            }
        }
    }

    public void getStageResults(ArrayList<PlayerModel> players, int sponsorId, int stageBattlePoints,
                                QuestCardModel card, AdventureDeckModel adventureDeck, AdventureDeckModel discardDeck) {
        PlayerModel currPlayer = players.get(sponsorId);
        for (int q = 0; q < players.size(); q++) {
            currPlayer = playerCon.nextPlayer(players, currPlayer);
            if (currPlayer.isActive) {
                // Added new parameter here (players)
                int playerPoints = getPlayerStageBattlePoints(card, currPlayer, players);

                log.info(currPlayer.playerName + " played a total off " + playerPoints
                        + " battlepoints against a stage with " + stageBattlePoints + " battlepoints");

                if (playerPoints >= stageBattlePoints) {
                    // player advances
                    // add a card to the players hand
                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " beat this stage");
                    log.info(currPlayer.playerName + " beat this stage");

                    sleep(2);

                    // discard the players weapon cards
                    discardWeaponCards(currPlayer, discardDeck);

                    CardModel c = handCon.drawACardAndShow(currPlayer, -1, adventureDeck);
                    sendMessageToAllAddCard(currPlayer.playerId, c);
                    //check card count
                    playerCon.tooManyCards(currPlayer, players, discardDeck);

                } else {
                    // player loses
                    currPlayer.isActive = false;

                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, currPlayer.playerName + " lost this stage");
                    log.info(currPlayer.playerName + " lost this stage");

                    sleep(1);

                    // discard the players weapon cards
                    discardWeaponCards(currPlayer, discardDeck);

                    if (numberOfActivePlayers(players) == 0) {
                        log.info("All players are out. There are no winners");
                        break;
                    }
                }
            }
        }
    }

    // Added new parameter
    private int getStageBattlePoints(String cardName, ArrayList<CardModel> stageCards, ArrayList<PlayerModel> players) {
        int points = 0;
        ArrayList<CardModel> cardsInPlay = getCardsInPlay(players);

        for (CardModel c : stageCards) {
            int p = cardCon.getBattlePoints(cardName, c, cardsInPlay);
            points += p;

            log.info("Stage -- " + c.cardName + " : " + p + " battlepoints");
        }

        return points;
    }

    // Gets the players battle points including the rank battle points addition
    public int getPlayerStageBattlePoints(QuestCardModel card, PlayerModel
            player, ArrayList<PlayerModel> players) {
        int points = 0;
        ArrayList<CardModel> cardsInPlay = getCardsInPlay(players);

        for (CardModel c : player.hand.selectedCards) {
            int p = cardCon.getBattlePoints(card.cardName, c, cardsInPlay);
            points += p;

            log.info(player.playerName + " -- " + c.cardName + " : " + p + " battlepoints");
        }

        points += player.rank.battlePoints;
        log.info(player.playerName + " -- rank : " + player.rank.battlePoints + " battlepoints");

        return points;
    }

    private boolean validateStageCards(String cardName, ArrayList<CardModel> cards, boolean testCardUsed, int stageNum, ArrayList<PlayerModel> players) {
        boolean foeFound = false;
        HashSet<CardModel> uniqueSet = new HashSet<>();

        if (cards.size() == 0)
            return true;

        for (CardModel c : cards) {
            uniqueSet.add(c);
        }

        if (uniqueSet.size() != cards.size()) { // check for duplicates
            return false;
        }

        for (CardModel c : cards) {
            if (c instanceof AllyCardModel) {
                return false;
            } else if (c instanceof TestCardModel && cards.size() > 1) {
                return false;
            } else if (c instanceof TestCardModel && testCardUsed) {
                return false;
            } else if (c instanceof FoeCardModel) {
                if (foeFound == true) {
                    return false; // only 1 foe per stage
                }

                foeFound = true;
            }
        }

        if (!foeFound)
            return false;

        if (stageNum > 0) {
            // make sure it is greater than before
            int previousRoundPoints = getStageBattlePoints(cardName, stages.get(stageNum - 1), players);
            if (previousRoundPoints == -1) {
                if (stageNum > 2) { // check 2 behind to be higher than stage before test
                    previousRoundPoints = getStageBattlePoints(cardName, stages.get(stageNum - 2), players);

                    if (previousRoundPoints < getStageBattlePoints(cardName, cards, players)) {
                        return true;
                    } else {
                        return false;
                    }

                } else {
                    // test card
                    return true;
                }
            } else {
                if (previousRoundPoints < getStageBattlePoints(cardName, cards, players)) {
                    return true;
                } else {
                    // battlepoints does not increase
                    return false;
                }
            }
        }

        return true;
    }

    private boolean validatePlayerCards(ArrayList<CardModel> cards) {
        HashSet<CardModel> uniqueSet = new HashSet<>();

        if (cards.size() == 0)
            return true;

        for (CardModel c : cards) {
            if (c instanceof FoeCardModel || c instanceof TestCardModel) {
            	log.info("Invalid Cards. Foe Cards and Tests Cards not allowed.");
                return false;
            }
            uniqueSet.add(c);
        }

        if (uniqueSet.size() != cards.size()) { // check for duplicates
        	log.info("Invalid Cards. Duplicates played.");
            return false;
        }

        return true;
    }

    private void discardWeaponCards(PlayerModel player, AdventureDeckModel discardDeck) {
        // remove all weapon cards
        ArrayList<CardModel> cards = player.hand.selectedCards;
        int size = cards.size();
        int counter = 0;

        for (int i = 0; i < size; i++) {
            CardModel c = cards.get(counter);
            if (!(c instanceof AllyCardModel)) {
                handCon.discardFromSelected(player, c, discardDeck);
                sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_PLAY_AREA, player.playerId + " " + c.cardId);
            } else {
                counter++;
            }
        }
    }

    public int getPlayerStageBids(ArrayList<CardModel> selectedCards, String questName, PlayerModel player, ArrayList<PlayerModel> players) {
        int totalBids = 0;
        ArrayList<CardModel> cardsInPlay = getCardsInPlay(players);

        for (int i = 0; i < selectedCards.size(); i++) {
            CardModel card = selectedCards.get(i);
            if (card instanceof AllyCardModel) {
                AllyCardModel c = (AllyCardModel) card;
                c = c.activateSpecial(c, questName, cardsInPlay);
                totalBids += c.cardBid;
            } else {
                totalBids++;
            }
        }

        return totalBids;
    }

    public int numberOfActivePlayers(ArrayList<PlayerModel> players) {
        int counter = 0;

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).isActive)
                counter++;
        }

        return counter;
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
                sendMessageToAll(ServerCommand.REMOVE_CARD_FROM_PLAY_AREA, player.playerId + " " + c.cardId);
            } else {
                counter++;
            }
        }
    }

    public void handOutOneCard(ArrayList<PlayerModel> players, int originalPlayerId, AdventureDeckModel
            adventureDeck,
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

    public int getBids(int playerId) {
        int bids = -1;

        clients.get(playerId).out.println(ServerCommand.PROMPT_BID_CARDS + " " + playerId);

        while (bids == -1) {
            bids = clients.get(playerId).checkForMessageInt();
        }

        return bids;
    }

    public void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
