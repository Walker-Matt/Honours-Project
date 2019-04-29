/*
 * Ahmad
 * Game Manager
 * This file is responsible for managing the high level of the game.
 * It initializes the decks and the players and then runs a loop that
 * repeatedly draws a card until the game ends
 */

package com.softwareeng.project.server.Controllers;

import com.softwareeng.project.commands.ServerCommand;
import com.softwareeng.project.server.Models.*;
import com.softwareeng.project.server.ClientHandler;
import com.softwareeng.project.server.Controllers.strategy.Strategy1;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class GameController {
    final static Logger log = Logger.getLogger(GameController.class);
    public boolean kingsRecTrig = false;

    private GameModel gameModel = new GameModel();
    ArrayList<ClientHandler> clients;
    DeckController deckCon = new DeckController();
    HandController handCon;
    QuestController questCon;
    EventController eventCon;
    TournamentController tournamentCon;
    PlayerController playerCon;
    CardModel currentCard;
    
    public CardModel getCurrentCard() { return this.currentCard; }

    public ArrayList<ClientHandler> getClients() { return this.clients; }
    
    public GameModel getGameModel() { return this.gameModel; }

    public GameController(ArrayList<ClientHandler> clients) {
        this.clients = clients;

        questCon = new QuestController(clients);
        tournamentCon = new TournamentController(clients);
        eventCon = new EventController(clients);
        playerCon = new PlayerController(clients);
        handCon = new HandController();
    }

    public void initGame(int numberOfHumanPlayers, int scenario) {
        gameModel.players = initPlayers(numberOfHumanPlayers);

        gameModel.currentPlayer = gameModel.players.get(0);

        switch (scenario) {
            case -1:
                log.info("Initializing a regular game");

                initDeck();

                for (PlayerModel player : gameModel.getPlayers()) {
                    player.hand.playerHand = handCon.generateHand(12, gameModel.adventureDeck);
                }
                break;

            case 1:
                log.info("Initializing a game for scenario 1");

                initDeckScenario1();
                initPlayersScenario1();
                break;
            case 2:
                log.info("Initializing a game for scenario 2");

                initDeckScenario2();
                initPlayersScenario2();
                break;
            case 3: 
                log.info("Initializing a game for scenario 3");
                
                initDeckScenario3();
                initPlayersScenario3();
                break;
            case 4: 
                log.info("Initializing a game for scenario 4");
                log.info("PLEASE NOTE -> QUESTS ARE ALL 1 STAGE TO MAKE SIMPLER");
                
                initDeckScenario4();
                initPlayersScenario4();
                break;
            case 5:
                log.info("Initializing a game for scenario 5");
                log.info("PLEASE NOTE -> QUESTS ARE ALL 1 STAGE TO MAKE SIMPLER");

                initDeckScenario5();
                initPlayersScenario5();
                break;
            case 6:
                log.info("Initializing a game for scenario 5");
                log.info("PLEASE NOTE -> QUESTS ARE ALL 1 STAGE TO MAKE SIMPLER");

                initDeckScenario6();
                initPlayersScenario6();
                break;

            case 7:
                log.info("Initializing a game for AI Strategy 1 or 2 that shows they will NOT sponsor in this scenario");
                log.info("P1 has 4 shields meaning that P1 can for sure rank up if they win this quest");
                log.info("PLEASE NOTE -> QUESTS ARE ALL 1 STAGE TO MAKE SIMPLER");

                initDeckScenarioAISt1and2NoSpon();
                initAINoSponsorSt1and2Spon();
                break;
            case 8:
                log.info("Initializing a game for AI Strategy 1 or 2 that shows they will NOT sponsor in this scenario");
                log.info("The AI player does not have enough increasing foes");
                log.info("PLEASE NOTE -> QUESTS ARE ALL 1 STAGE TO MAKE SIMPLER");

                initDeckAISponsorSt1and2SponNoFoe();
                intiAISponsorSt1and2NoFoes();
                break;
        }

        //logging
        for (PlayerModel playerModel : gameModel.players) {
            String cards = playerModel.playerName + "'s hand: ";
            for (CardModel c : playerModel.hand.playerHand) {
                cards += c.cardName + ",";
            }

            log.info(cards);
        }

        for (int i = 0; i < gameModel.players.size(); i++) {
            PlayerModel p = gameModel.players.get(i);
            for (int q = 0; q < p.hand.playerHand.size(); q++) {
                sendMessageToAllAddCard(i, p.hand.playerHand.get(q));
            }

            sendMessageToAll(ServerCommand.ADD_CARD_TO_ASSETS, i + " " + p.rank.cardId + " " + p.rank.imageUrl);
        }

        sendMessageToAll(ServerCommand.FILL_DECK, "");


    }

    public void startGame() {
        ArrayList<PlayerModel> winners = new ArrayList<>();

        gameModel.currentPlayer = gameModel.players.get(0);

        while (!gameModel.gameEnd) {
            //returns true when the user clicks on a card
            boolean storyDeckClicked = false;

            for (ClientHandler cH : clients) {
                if (cH.getId() == (gameModel.currentPlayer.playerId + 1)) {
                    cH.out.println(ServerCommand.SET_MESSAGE_BOX + " " + "Click on the story deck to draw a card");
                } else {
                    cH.out.println(ServerCommand.SET_MESSAGE_BOX + " " + "Please wait your turn");
                }
            }

            log.info(gameModel.currentPlayer.playerName + "'s turn");


            if (gameModel.currentPlayer instanceof AIPlayerModel) {
                ////uiCon.sleep(1);

                storyDeckClicked = true;
            } else {
                clients.get(gameModel.currentPlayer.playerId).out.println(ServerCommand.PROMPT_FLIP_STORY);

                while (storyDeckClicked == false) {
                    storyDeckClicked = clients.get(gameModel.currentPlayer.playerId).checkForMessageBool();
                }
            }


            if (storyDeckClicked) {
                currentCard = deckCon.drawFromDeck(gameModel.storyDeck);

                sendMessageToAll(ServerCommand.FLIP_STORY_CARD, currentCard.cardId + " " + currentCard.imageUrl);

                //either Quest, Tournament or Event
                if (currentCard instanceof QuestCardModel) {
                    QuestCardModel questCard = (QuestCardModel) currentCard;

                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, questCard.cardName + " quest started!");
                    log.info(questCard.cardName + " quest started!");

                    //uiCon.sleep(1);

                    int sponsorId = questCon.initQuest(questCard, gameModel.currentPlayer, gameModel.players);
                    gameModel.setCurrentStory(questCard);
                    //sponsorId = -1 means there is no sponsor
                    if (-1 != sponsorId) {
                        HashMap<Integer, Integer> questResult = questCon.playQuest(sponsorId, gameModel.currentPlayer.playerId, questCard, gameModel.players, gameModel.adventureDeck, gameModel.discardAdventureDeck);


                        for (PlayerModel p : gameModel.players) {
                            if (questResult.containsKey(p.playerId)) {
                                RankCardModel currentRank = p.rank;

                                boolean isKnightOfRoundTable = false;
                                if (kingsRecTrig) {
                                    isKnightOfRoundTable = playerCon.awardShields(p, questResult.get(p.playerId) + 2);
                                    kingsRecTrig = false;
                                    log.info(p.playerName + " awarded " + (questResult.get(p.playerId) + 2) + " shields (KingsRecog)");
                                } else {
                                    isKnightOfRoundTable = playerCon.awardShields(p, questResult.get(p.playerId));
                                    log.info(p.playerName + " awarded " + questResult.get(p.playerId) + " shields");
                                }

                                playerCon.checkIfUpgradeNeeded(currentRank, p);

                                if (isKnightOfRoundTable) {
                                    winners.add(p);
                                    gameModel.gameEnd = true;

                                    log.info(p.playerName + " added to list of winners");
                                }
                            }
                        }
                    } else {
                        //no sponsor was chosen, so discard the card
                        deckCon.discardCard(currentCard, gameModel.discardStoryDeck);
                    }
                } else if (currentCard instanceof TournamentCardModel) {
                    TournamentCardModel tournamentCard = (TournamentCardModel) currentCard;
                    gameModel.setCurrentStory(tournamentCard);
                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, tournamentCard.cardName + " tournament started!");
                    log.info(tournamentCard.cardName + " tournament started!");

                    //uiCon.sleep(1);

                    HashMap<Integer, Integer> tournamentResult = tournamentCon.startTournament(tournamentCard, gameModel.players, gameModel.currentPlayer, gameModel.discardAdventureDeck, gameModel.adventureDeck);


                    for (PlayerModel p : gameModel.players) {
                        if (tournamentResult.containsKey(p.playerId)) {
                            boolean isKnightOfRoundTable = false;
                            RankCardModel currentRank = p.rank;

                            isKnightOfRoundTable = playerCon.awardShields(p, tournamentResult.get(p.playerId) + tournamentCard.bonusShields);

                            playerCon.checkIfUpgradeNeeded(currentRank, p);

                            if (isKnightOfRoundTable) {
                                winners.add(p);
                                gameModel.gameEnd = true;

                                log.info(p.playerName + " added to list of winners");
                            }
                        }
                    }
                } else if (currentCard instanceof EventCardModel) {
                    EventCardModel eventCard = (EventCardModel) currentCard;
                    gameModel.setCurrentStory(eventCard);
                    sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, eventCard.cardName + " event started!");
                    log.info(eventCard.cardName + " event started!");

                    //uiCon.sleep(1);

                    kingsRecTrig = eventCon.startEvent(gameModel.players, gameModel.currentPlayer, eventCard, gameModel.discardAdventureDeck, gameModel.adventureDeck);
                }

                boolean endTurn = false;

                if (gameModel.currentPlayer instanceof AIPlayerModel) {
                    endTurn = true;
                } else {
                    clients.get(gameModel.currentPlayer.playerId).out.println(ServerCommand.SHOW_END_TURN_BUTTON + " " + gameModel.currentPlayer.playerId);

                    while (endTurn == false) {
                        String result = clients.get(gameModel.currentPlayer.playerId).checkForMessageString();
                        if (result.equals("true")) {
                            endTurn = true;
                        } else if (result.equals("false")) {
                            break;
                        }
                    }
                }

                if (endTurn) {
                    sendMessageToAll(ServerCommand.REMOVE_FLIPPED_STORY_CARD, "");

                    playerCon.setAllActive(gameModel.players);

                    if (winners.size() == 0) {
                        gameModel.currentPlayer = playerCon.nextPlayer(gameModel.players, gameModel.currentPlayer);

                    } else if (winners.size() > 1) {
                        sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, "There are multiple winners! Tournament starting");
                        log.info("There are multiple winners! Tournament starting");

                        //uiCon.sleep(2);

                        //run tournament
                        TournamentCardModel endGameCard = new TournamentCardModel("At York", 0);

                        HashMap<Integer, Integer> tournamentResult = tournamentCon.startTournament(endGameCard, winners, winners.get(0), gameModel.discardAdventureDeck, gameModel.adventureDeck);


                        int size = winners.size();
                        for (int i = size - 1; i >= 0; i--) {
                            if (tournamentResult.containsKey(winners.get(i).playerId)) {
                                continue;
                            } else {
                                winners.remove(i);
                            }
                        }
                    }
                }
            }
        }

        String winnerNames = "";

        for (PlayerModel p : winners) {
            winnerNames += p.playerName;
        }

        sendMessageToAll(ServerCommand.SET_MESSAGE_BOX, winnerNames + " won the game!!!");
        log.info(winnerNames + " won the game");

        //uiCon.sleep(5);
    }

    public GameModel getModel() {
        return gameModel;
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

    private ArrayList<PlayerModel> initPlayers(int numHuman) {
        ArrayList<PlayerModel> players = new ArrayList<PlayerModel>(4);

        for (int i = 0; i < numHuman; i++) {
            players.add(new PlayerModel(i, "Player" + (i + 1)));
            log.info(players.get(i).playerName + " has been created");
        }

        for (int i = players.size(); i < 4; i++) {
            AIPlayerModel aiPlayer = new AIPlayerModel(i, "AIPlayer" + (i + 1));
            players.add(aiPlayer);

            //usage
            //aiPlayer.doISponsorAQuest();
//			aiPlayer.doIParticipateInTournament();
//			aiPlayer.discardAfterWinningTest();
//			aiPlayer.nextBid();
//			aiPlayer.getCardsToPlayForQuest();
//			aiPlayer.getCardsToPlayForTournament();
//			aiPlayer.doIParticipateInQuest();

            if(aiPlayer.aiStrategy instanceof Strategy1)
            	log.info(aiPlayer.playerName + " has been created with Strategy 1");
            else 
            	log.info(aiPlayer.playerName + " has been created with Strategy 2");
        }

        return players;
    }

    //modified for testing purposes
    private void initPlayersScenario1() {
        PlayerModel p1 = gameModel.players.get(0);

        p1.hand.playerHand = handCon.generateHand(0, gameModel.adventureDeck);
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Battle-ax"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Lance"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sword"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Lance"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Excalibur"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Battle-ax"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Lance"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Excalibur"));

        PlayerModel p2 = gameModel.players.get(1);

        p2.hand.playerHand = handCon.generateHand(0, gameModel.adventureDeck);
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Saxons"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Boar"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Thieves"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Saxon Knight"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Evil Knight"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Saxons"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Robber Knight"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Black Knight"));

        PlayerModel p3 = gameModel.players.get(2);

        p3.hand.playerHand = handCon.generateHand(0, gameModel.adventureDeck);

        PlayerModel p4 = gameModel.players.get(3);

        p4.hand.playerHand = handCon.generateHand(0, gameModel.adventureDeck);
    }

    private void initPlayersScenario2() {
        PlayerModel p1 = gameModel.players.get(0);

        p1.hand.playerHand = handCon.generateHand(8, gameModel.adventureDeck);
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Saxons"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Boar"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sword"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Dagger"));

        PlayerModel p2 = gameModel.players.get(1);

        p2.hand.playerHand = handCon.generateHand(12, gameModel.adventureDeck);

        PlayerModel p3 = gameModel.players.get(2);

        p3.hand.playerHand = handCon.generateHand(9, gameModel.adventureDeck);
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Horse"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Excalibur"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));

        PlayerModel p4 = gameModel.players.get(3);

        p4.hand.playerHand = handCon.generateHand(10, gameModel.adventureDeck);
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Battle-ax"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Lance"));
    }

    private void initPlayersScenario3() {
        PlayerModel p1 = gameModel.players.get(0);

        p1.hand.playerHand = handCon.generateHand(8, gameModel.adventureDeck);
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Saxons"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Boar"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sword"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Dagger"));

        PlayerModel p2 = gameModel.players.get(1);

        p2.hand.playerHand = handCon.generateHand(11, gameModel.adventureDeck);
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Test of Valor"));

        PlayerModel p3 = gameModel.players.get(2);

        p3.hand.playerHand = handCon.generateHand(9, gameModel.adventureDeck);
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Horse"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Excalibur"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));

        PlayerModel p4 = gameModel.players.get(3);

        p4.hand.playerHand = handCon.generateHand(10, gameModel.adventureDeck);
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Battle-ax"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Lance"));
    }
    
    private void initPlayersScenario4() {
        PlayerModel p1 = gameModel.players.get(0);

        p1.hand.playerHand = handCon.generateHand(11, gameModel.adventureDeck);
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Test of Morgan Le Fey"));

        PlayerModel p2 = gameModel.players.get(1);

        p2.hand.playerHand = handCon.generateHand(11, gameModel.adventureDeck);
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Test of the Questing Beast"));

        PlayerModel p3 = gameModel.players.get(2);

        p3.hand.playerHand = handCon.generateHand(9, gameModel.adventureDeck);
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Horse"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Excalibur"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));

        PlayerModel p4 = gameModel.players.get(3);

        p4.hand.playerHand = handCon.generateHand(10, gameModel.adventureDeck);
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Battle-ax"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Lance"));
    }
    
    private void initPlayersScenario5() {
        PlayerModel p1 = gameModel.players.get(0);

        p1.hand.playerHand = handCon.generateHand(8, gameModel.adventureDeck);
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Percival"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Gawain"));
        //Changed Mordred to Sir Tristan
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Tristan"));

        PlayerModel p2 = gameModel.players.get(1);

        p2.hand.playerHand = handCon.generateHand(8, gameModel.adventureDeck);
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Percival"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Gawain"));
        //Changed Mordred to Queen Isuelt
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Queen Iseult"));

        PlayerModel p3 = gameModel.players.get(2);

        p3.hand.playerHand = handCon.generateHand(8, gameModel.adventureDeck);
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Percival"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Gawain"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Mordred"));

        PlayerModel p4 = gameModel.players.get(3);

        p4.hand.playerHand = handCon.generateHand(8, gameModel.adventureDeck);
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Percival"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Gawain"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Mordred"));

        
        
        p1.shields = 2;
        p2.shields = 4;
        p2.rank = new RankCardModel("Knight");
        p3.shields = 1;
        p4.shields = 1;
        log.info("Players rigged with the following Shield count:");
        log.info(p1.playerName + ": " + p1.shields + " shields as a " + p1.rank.cardName);
        log.info(p2.playerName + ": " + p2.shields + " shields as a " + p2.rank.cardName);
        log.info(p3.playerName + ": " + p3.shields + " shields as a " + p3.rank.cardName);
        log.info(p4.playerName + ": " + p4.shields + " shields as a " + p4.rank.cardName);
        

    }

    private void initPlayersScenario6() {
        PlayerModel p1 = gameModel.players.get(0);

        p1.hand.playerHand = handCon.generateHand(12, gameModel.adventureDeck);


        PlayerModel p2 = gameModel.players.get(1);

        p2.hand.playerHand = handCon.generateHand(3, gameModel.adventureDeck);
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "King Pellinore"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Percival"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Gawain"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Tristan"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Queen Iseult"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Lancelot"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sir Galahad"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Queen Guinevere"));
        p2.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "King Arthur"));

        PlayerModel p3 = gameModel.players.get(2);

        p3.hand.playerHand = handCon.generateHand(12, gameModel.adventureDeck);


        PlayerModel p4 = gameModel.players.get(3);

        p4.hand.playerHand = handCon.generateHand(12, gameModel.adventureDeck);

    }

    
    //AI Player Scenarios Sponsoring
    private void initAISponsorSt1and2Spon() {

        PlayerModel p1 = gameModel.players.get(0);

        p1.hand.playerHand = handCon.generateHand(8, gameModel.adventureDeck);
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Saxons"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Boar"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sword"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Dagger"));

        PlayerModel p2 = gameModel.players.get(1);

        p2.hand.playerHand = handCon.generateHand(12, gameModel.adventureDeck);

        PlayerModel p3 = gameModel.players.get(2);

        p3.hand.playerHand = handCon.generateHand(9, gameModel.adventureDeck);
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Horse"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Excalibur"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));

        PlayerModel p4 = gameModel.players.get(3);

        p4.hand.playerHand = handCon.generateHand(7, gameModel.adventureDeck);

        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Thieves"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Boar"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Giant"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Dragon"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Mordred"));


    }

    private void initAINoSponsorSt1and2Spon() {
        PlayerModel p1 = gameModel.players.get(0);

        p1.hand.playerHand = handCon.generateHand(8, gameModel.adventureDeck);
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Saxons"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Boar"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sword"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Dagger"));

        PlayerModel p2 = gameModel.players.get(1);

        p2.hand.playerHand = handCon.generateHand(12, gameModel.adventureDeck);

        PlayerModel p3 = gameModel.players.get(2);

        p3.hand.playerHand = handCon.generateHand(9, gameModel.adventureDeck);
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Horse"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Excalibur"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));

        PlayerModel p4 = gameModel.players.get(3);

        p4.hand.playerHand = handCon.generateHand(10, gameModel.adventureDeck);
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Battle-ax"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Lance"));

        //Rigging
        p1.shields = 4;

    }

    private void intiAISponsorSt1and2NoFoes() {

        PlayerModel p1 = gameModel.players.get(0);

        p1.hand.playerHand = handCon.generateHand(8, gameModel.adventureDeck);
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Saxons"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Boar"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Sword"));
        p1.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Dagger"));

        PlayerModel p2 = gameModel.players.get(1);

        p2.hand.playerHand = handCon.generateHand(12, gameModel.adventureDeck);

        PlayerModel p3 = gameModel.players.get(2);

        p3.hand.playerHand = handCon.generateHand(9, gameModel.adventureDeck);
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Horse"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Excalibur"));
        p3.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));

        PlayerModel p4 = gameModel.players.get(3);

        //p4.hand.playerHand = handCon.generateHand(7, gameModel.adventureDeck);

        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Horse"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Horse"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Horse"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Dagger"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Dagger"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Excalibur"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Dagger"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Lance"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Lance"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Amour"));
        p4.hand.playerHand.add(deckCon.drawSpecificCardFromDeck(gameModel.adventureDeck, "Battle-ax"));
    }


    private void initDeck() {
        //Quest Cards
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Vanquish King Arthur's Enemies", 3), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Boar Hunt", 2, "Boar"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 4), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 4, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Rescue the Fair Maiden", 3, "Black Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Holy Grail", 5, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Test of the Green Knight", 4, "Green Knight"), 1);
        //Tournament Cards
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Camelot", 3), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Orkney", 2), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Tintagel", 1), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At York", 0), 1);
        //Event Cards
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Chivalrous Deed"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Pox"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Plague"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Recognition"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Queen's Favor"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Court Called to Camelot"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Prosperity Throughout the Realm"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Call to Arms"), 1);

        //Weapon Cards
        deckCon.addCard(gameModel.adventureDeck, new WeaponCardModel("Horse", 10), 11);
        deckCon.addCard(gameModel.adventureDeck, new WeaponCardModel("Sword", 10), 16);
        deckCon.addCard(gameModel.adventureDeck, new WeaponCardModel("Dagger", 5), 6);
        deckCon.addCard(gameModel.adventureDeck, new WeaponCardModel("Excalibur", 30), 2);
        deckCon.addCard(gameModel.adventureDeck, new WeaponCardModel("Lance", 20), 6);
        deckCon.addCard(gameModel.adventureDeck, new WeaponCardModel("Battle-ax", 15), 8);
        //Foe Cards
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Robber Knight", 15), 7);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxons", 10, 20), 5);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Boar", 5, 15), 4);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Thieves", 5), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Green Knight", 25, 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Black Knight", 25, 35), 3);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Evil Knight", 20, 30), 6);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxon Knight", 15, 25), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Dragon", 50, 70), 1);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Giant", 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Mordred", 30), 4);
        //Test Cards
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of the Questing Beast", 4), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Temptation"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Valor"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Morgan Le Fey", 3), 2);
        //Ally Cards
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Gawain", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Pellinore", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Percival", 5, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Tristan", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Arthur", 10, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Guinevere", 0, 3), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Merlin", 0, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Iseult", 0, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Lancelot", 15, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Galahad", 15, 0), 1);
        //Amour Card
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Amour", 10, 1), 8);

        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Squire"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Knight"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Champion Knight"), 4);

        deckCon.shuffle(gameModel.storyDeck);
        deckCon.shuffle(gameModel.adventureDeck); //shuffle the cards
    }

    //modified for testing purposes
    private void initDeckScenario1() {
        //Quest Cards
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Vanquish King Arthur's Enemies", 3), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Test of the Green Knight", 4, "Green Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Rescue the Fair Maiden", 3, "Black Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Holy Grail", 5, "All"), 1);
        //Tournament Cards
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Camelot", 3), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Orkney", 2), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Tintagel", 1), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At York", 0), 1);
        //Event Cards
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Pox"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Plague"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Queen's Favor"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Call to Arms"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Prosperity Throughout the Realm"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Recognition"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Court Called to Camelot"), 2);

        //RIGGING
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 4, "All"), 1); //THIRD QUEST
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight"), 1); //SECOND QUEST
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 4), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Boar Hunt", 2, "Boar"), 1); //FIRST QUEST
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Chivalrous Deed"), 1);

        //Weapon Cards
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Horse", 10), 11);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Sword", 10), 16);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Dagger", 5), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Excalibur", 30), 2);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Lance", 20), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Battle-ax", 15), 8);
        //Foe Cards
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Robber Knight", 15), 7);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxons", 10, 20), 5);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Boar", 5, 15), 4);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Thieves", 5), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Green Knight", 25, 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Black Knight", 25, 35), 3);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Evil Knight", 20, 30), 6);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxon Knight", 15, 25), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Dragon", 50, 70), 1);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Giant", 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Mordred", 30), 4);
        //Test Cards
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of the Questing Beast", 4), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Temptation"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Valor"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Morgan Le Fey", 3), 2);
        //Ally Cards
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Gawain", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Pellinore", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Percival", 5, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Tristan", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Arthur", 10, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Guinevere", 0, 3), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Merlin", 0, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Iseult", 0, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Lancelot", 15, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Galahad", 15, 0), 1);
        //Amour Card
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Amour", 10, 1), 8);

        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Squire"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Knight"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Champion Knight"), 4);

        deckCon.shuffle(gameModel.adventureDeck); //shuffle the cards
    }

    private void initDeckScenario2() {
        //Quest Cards
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Vanquish King Arthur's Enemies", 3), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 4), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 4, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Rescue the Fair Maiden", 3, "Black Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Holy Grail", 5, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Test of the Green Knight", 4, "Green Knight"), 1);
        //Tournament Cards
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Camelot", 3), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Orkney", 2), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At York", 0), 1);
        //Event Cards
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Pox"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Plague"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Recognition"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Queen's Favor"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Court Called to Camelot"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Call to Arms"), 1);


        //RIGGING
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Tintagel", 1), 1); //TOURNAMENT FIRSTCARD


        //Weapon Cards
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Horse", 10), 11);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Sword", 10), 16);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Dagger", 5), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Excalibur", 30), 2);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Lance", 20), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Battle-ax", 15), 8);
        //Foe Cards
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Robber Knight", 15), 7);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxons", 10, 20), 5);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Boar", 5, 15), 4);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Thieves", 5), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Green Knight", 25, 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Black Knight", 25, 35), 3);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Evil Knight", 20, 30), 6);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxon Knight", 15, 25), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Dragon", 50, 70), 1);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Giant", 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Mordred", 30), 4);
        //Test Cards
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of the Questing Beast", 4), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Temptation"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Valor"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Morgan Le Fey", 3), 2);
        //Ally Cards
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Gawain", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Pellinore", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Percival", 5, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Tristan", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Arthur", 10, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Guinevere", 0, 3), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Merlin", 0, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Iseult", 0, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Lancelot", 15, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Galahad", 15, 0), 1);
        //Amour Card
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Amour", 10, 1), 8);

        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Squire"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Knight"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Champion Knight"), 4);

        deckCon.shuffle(gameModel.adventureDeck); //shuffle the cards
    }

    private void initDeckScenario3() {
        //Quest Cards
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Vanquish King Arthur's Enemies", 3), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 4), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 4, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Rescue the Fair Maiden", 3, "Black Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Holy Grail", 5, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Test of the Green Knight", 4, "Green Knight"), 1);
        //Tournament Cards
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Camelot", 3), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Orkney", 2), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At York", 0), 1);
        //Event Cards
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Pox"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Plague"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Recognition"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Queen's Favor"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Court Called to Camelot"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Call to Arms"), 1);

        //RIGGING
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);

        //Weapon Cards
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Horse", 10), 11);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Sword", 10), 16);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Dagger", 5), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Excalibur", 30), 2);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Lance", 20), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Battle-ax", 15), 8);
        //Foe Cards
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Robber Knight", 15), 7);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxons", 10, 20), 5);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Boar", 5, 15), 4);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Thieves", 5), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Green Knight", 25, 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Black Knight", 25, 35), 3);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Evil Knight", 20, 30), 6);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxon Knight", 15, 25), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Dragon", 50, 70), 1);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Giant", 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Mordred", 30), 4);
        //Test Cards
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of the Questing Beast", 4), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Temptation"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Valor"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Morgan Le Fey", 3), 2);
        //Ally Cards
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Gawain", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Pellinore", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Percival", 5, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Tristan", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Arthur", 10, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Guinevere", 0, 3), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Merlin", 0, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Iseult", 0, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Lancelot", 15, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Galahad", 15, 0), 1);
        //Amour Card
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Amour", 10, 1), 8);

        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Squire"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Knight"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Champion Knight"), 4);

        deckCon.shuffle(gameModel.adventureDeck); //shuffle the cards
    }
    
    private void initDeckScenario4() {
        //Quest Cards
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Vanquish King Arthur's Enemies", 3), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 4), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 4, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Rescue the Fair Maiden", 3, "Black Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Holy Grail", 5, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Test of the Green Knight", 4, "Green Knight"), 1);
        //Tournament Cards
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Camelot", 3), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Orkney", 2), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At York", 0), 1);
        //Event Cards
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Pox"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Plague"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Recognition"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Queen's Favor"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Court Called to Camelot"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Call to Arms"), 1);

        //RIGGING
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 1), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 1, "Dragon"), 1);

        //Weapon Cards
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Horse", 10), 11);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Sword", 10), 16);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Dagger", 5), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Excalibur", 30), 2);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Lance", 20), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Battle-ax", 15), 8);
        //Foe Cards
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Robber Knight", 15), 7);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxons", 10, 20), 5);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Boar", 5, 15), 4);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Thieves", 5), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Green Knight", 25, 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Black Knight", 25, 35), 3);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Evil Knight", 20, 30), 6);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxon Knight", 15, 25), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Dragon", 50, 70), 1);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Giant", 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Mordred", 30), 4);
        //Test Cards
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of the Questing Beast", 4), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Temptation"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Valor"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Morgan Le Fey", 3), 2);
        //Ally Cards
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Gawain", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Pellinore", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Percival", 5, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Tristan", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Arthur", 10, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Guinevere", 0, 3), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Merlin", 0, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Iseult", 0, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Lancelot", 15, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Galahad", 15, 0), 1);
        //Amour Card
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Amour", 10, 1), 8);

        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Squire"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Knight"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Champion Knight"), 4);

        deckCon.shuffle(gameModel.adventureDeck); //shuffle the cards
    }
    
    private void initDeckScenario5() {
        //Quest Cards
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 4), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 4, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Rescue the Fair Maiden", 3, "Black Knight"), 1);


        //Tournament Cards
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Camelot", 3), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Orkney", 2), 1);

        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At York", 0), 1);
        //Event Cards


        //RIGGING
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Vanquish King Arthur's Enemies", 1), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Recognition"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Court Called to Camelot"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Queen's Favor"), 1);
        //deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Call to Arms"), 1);
        //deckCon.addCard(gameModel.storyDeck, new EventCardModel("Plague"), 1);
        //deckCon.addCard(gameModel.storyDeck, new EventCardModel("Pox"), 1);


        //Weapon Cards
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Horse", 10), 11);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Sword", 10), 16);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Dagger", 5), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Excalibur", 30), 2);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Lance", 20), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Battle-ax", 15), 8);
        //Foe Cards
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Robber Knight", 15), 7);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxons", 10, 20), 5);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Boar", 5, 15), 4);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Thieves", 5), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Green Knight", 25, 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Black Knight", 25, 35), 3);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Evil Knight", 20, 30), 6);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxon Knight", 15, 25), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Dragon", 50, 70), 1);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Giant", 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Mordred", 30), 8);
        //Test Cards
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of the Questing Beast", 4), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Temptation"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Valor"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Morgan Le Fey", 3), 2);
        //Ally Cards
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Gawain", 10, 0), 8);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Pellinore", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Percival", 5, 0), 8);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Tristan", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Arthur", 10, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Guinevere", 0, 3), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Merlin", 0, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Iseult", 0, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Lancelot", 15, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Galahad", 15, 0), 1);
        //Amour Card
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Amour", 10, 1), 8);

        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Squire"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Knight"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Champion Knight"), 4);

        deckCon.shuffle(gameModel.adventureDeck); //shuffle the cards
    }

    private void initDeckScenario6() {
        //Quest Cards
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 4), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 4, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Rescue the Fair Maiden", 3, "Black Knight"), 1);


        //Tournament Cards
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Camelot", 3), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Orkney", 2), 1);

        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At York", 0), 1);
        //Event Cards


        //RIGGING
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 1, "All"), 1);
        //deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Holy Grail", 1, "All"), 1);
        //deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 1), 1);
        //deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Test of the Green Knight", 1, "Green Knight"), 1);



        //Weapon Cards
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Horse", 10), 11);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Sword", 10), 16);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Dagger", 5), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Excalibur", 30), 2);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Lance", 20), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Battle-ax", 15), 8);
        //Foe Cards
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Robber Knight", 15), 7);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxons", 10, 20), 5);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Boar", 5, 15), 4);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Thieves", 5), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Green Knight", 25, 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Black Knight", 25, 35), 3);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Evil Knight", 20, 30), 6);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxon Knight", 15, 25), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Dragon", 50, 70), 1);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Giant", 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Mordred", 30), 8);
        //Test Cards
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of the Questing Beast", 4), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Temptation"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Valor"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Morgan Le Fey", 3), 2);
        //Ally Cards
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Gawain", 10, 0), 8);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Pellinore", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Percival", 5, 0), 8);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Tristan", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Arthur", 10, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Guinevere", 0, 3), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Merlin", 0, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Iseult", 0, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Lancelot", 15, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Galahad", 15, 0), 1);
        //Amour Card
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Amour", 10, 1), 8);

        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Squire"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Knight"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Champion Knight"), 4);

        deckCon.shuffle(gameModel.adventureDeck); //shuffle the cards
    }


    //AI Player Scenarios Deck initialization Sponsoring

    private void initDeckScenarioAISt1and2Spon() {
        //Quest Cards
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Vanquish King Arthur's Enemies", 3), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 4), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 4, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Rescue the Fair Maiden", 3, "Black Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Holy Grail", 5, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Test of the Green Knight", 4, "Green Knight"), 1);
        //Tournament Cards
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Camelot", 3), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Orkney", 2), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Tintagel", 1), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At York", 0), 1);
        //Event Cards
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Pox"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Plague"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Recognition"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Queen's Favor"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Court Called to Camelot"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Call to Arms"), 1);


        //RIGGING
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Boar Hunt", 2, "Boar"), 1); //BOAR HUNT FIRST CARD


        //Weapon Cards
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Horse", 10), 11);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Sword", 10), 16);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Dagger", 5), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Excalibur", 30), 2);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Lance", 20), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Battle-ax", 15), 8);
        //Foe Cards
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Robber Knight", 15), 7);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxons", 10, 20), 5);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Boar", 5, 15), 4);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Thieves", 5), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Green Knight", 25, 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Black Knight", 25, 35), 3);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Evil Knight", 20, 30), 6);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxon Knight", 15, 25), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Dragon", 50, 70), 1);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Giant", 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Mordred", 30), 4);
        //Test Cards
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of the Questing Beast", 4), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Temptation"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Valor"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Morgan Le Fey", 3), 2);
        //Ally Cards
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Gawain", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Pellinore", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Percival", 5, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Tristan", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Arthur", 10, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Guinevere", 0, 3), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Merlin", 0, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Iseult", 0, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Lancelot", 15, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Galahad", 15, 0), 1);
        //Amour Card
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Amour", 10, 1), 8);

        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Squire"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Knight"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Champion Knight"), 4);

        deckCon.shuffle(gameModel.adventureDeck); //shuffle the cards
    }

    private void initDeckScenarioAISt1and2NoSpon() {
        //Quest Cards
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Vanquish King Arthur's Enemies", 3), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 4), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 4, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Rescue the Fair Maiden", 3, "Black Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Holy Grail", 5, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Test of the Green Knight", 4, "Green Knight"), 1);
        //Tournament Cards
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Camelot", 3), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Orkney", 2), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Tintagel", 1), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At York", 0), 1);
        //Event Cards
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Pox"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Plague"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Recognition"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Queen's Favor"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Court Called to Camelot"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Call to Arms"), 1);


        //RIGGING
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Boar Hunt", 2, "Boar"), 1); //BOAR HUNT FIRST CARD


        //Weapon Cards
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Horse", 10), 11);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Sword", 10), 16);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Dagger", 5), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Excalibur", 30), 2);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Lance", 20), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Battle-ax", 15), 8);
        //Foe Cards
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Robber Knight", 15), 7);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxons", 10, 20), 5);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Boar", 5, 15), 4);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Thieves", 5), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Green Knight", 25, 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Black Knight", 25, 35), 3);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Evil Knight", 20, 30), 6);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxon Knight", 15, 25), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Dragon", 50, 70), 1);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Giant", 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Mordred", 30), 4);
        //Test Cards
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of the Questing Beast", 4), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Temptation"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Valor"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Morgan Le Fey", 3), 2);
        //Ally Cards
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Gawain", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Pellinore", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Percival", 5, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Tristan", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Arthur", 10, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Guinevere", 0, 3), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Merlin", 0, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Iseult", 0, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Lancelot", 15, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Galahad", 15, 0), 1);
        //Amour Card
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Amour", 10, 1), 8);

        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Squire"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Knight"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Champion Knight"), 4);

        deckCon.shuffle(gameModel.adventureDeck); //shuffle the cards
    }

    private void initDeckAISponsorSt1and2SponNoFoe() {
        //Quest Cards
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Vanquish King Arthur's Enemies", 3), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Questing Beast", 4), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Defend the Queen's Honor", 4, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Slay the Dragon", 3, "Dragon"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Rescue the Fair Maiden", 3, "Black Knight"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Search for the Holy Grail", 5, "All"), 1);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Test of the Green Knight", 4, "Green Knight"), 1);
        //Tournament Cards
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Camelot", 3), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Orkney", 2), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At Tintagel", 1), 1);
        deckCon.addCard(gameModel.storyDeck, new TournamentCardModel("At York", 0), 1);
        //Event Cards
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Pox"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Plague"), 1);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Recognition"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Queen's Favor"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("Court Called to Camelot"), 2);
        deckCon.addCard(gameModel.storyDeck, new EventCardModel("King's Call to Arms"), 1);


        //RIGGING
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Repel the Saxon Raiders", 2, "All Saxons"), 2);
        deckCon.addCard(gameModel.storyDeck, new QuestCardModel("Boar Hunt", 2, "Boar"), 1); //BOAR HUNT FIRST CARD


        //Weapon Cards
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Horse", 10), 11);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Sword", 10), 16);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Dagger", 5), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Excalibur", 30), 2);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Lance", 20), 6);
        deckCon.addCard(gameModel.adventureDeck, new BattleCardModel("Battle-ax", 15), 8);
        //Foe Cards
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Robber Knight", 15), 7);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxons", 10, 20), 5);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Boar", 5, 15), 4);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Thieves", 5), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Green Knight", 25, 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Black Knight", 25, 35), 3);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Evil Knight", 20, 30), 6);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Saxon Knight", 15, 25), 8);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Dragon", 50, 70), 1);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Giant", 40), 2);
        deckCon.addCard(gameModel.adventureDeck, new FoeCardModel("Mordred", 30), 4);
        //Test Cards
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of the Questing Beast", 4), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Temptation"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Valor"), 2);
        deckCon.addCard(gameModel.adventureDeck, new TestCardModel("Test of Morgan Le Fey", 3), 2);
        //Ally Cards
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Gawain", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Pellinore", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Percival", 5, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Tristan", 10, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("King Arthur", 10, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Guinevere", 0, 3), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Merlin", 0, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Queen Iseult", 0, 2), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Lancelot", 15, 0), 1);
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Sir Galahad", 15, 0), 1);
        //Amour Card
        deckCon.addCard(gameModel.adventureDeck, new AllyCardModel("Amour", 10, 1), 8);

        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Squire"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Knight"), 4);
        deckCon.addCard(gameModel.rankDeck, new RankCardModel("Champion Knight"), 4);

        deckCon.shuffle(gameModel.adventureDeck); //shuffle the cards
    }


}

