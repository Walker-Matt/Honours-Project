package com.softwareeng.project.client;

import java.util.ArrayList;

import com.softwareeng.project.client.views.GameView;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


public class UIController {

    private GameView gameView;
    public App mainApp;
    public boolean deckClicked = false;
    public boolean turnEnded = false;
    public boolean cardsPlayed = false;
    public boolean turnReady = false;
    public boolean cardsDiscarded = false;
    public int bids = 0;
    public String promptResult = ""; //true for yes false for no
    public ArrayList<Integer> selectedCardIds;
    private EventHandler<MouseEvent> mouseEvent = e -> {
        ImageView clicked = (ImageView) e.getSource();
        int idOfClickedCard = (int) clicked.getUserData(); //user data is card id
        System.out.println("Adventure card is card clicked with user data: " + clicked.getUserData());

        if (null == clicked.getEffect()) {
            clicked.setEffect(new DropShadow(40, Color.RED));
            selectedCardIds.add(idOfClickedCard);
        } else {
            clicked.setEffect(null);
            int index = selectedCardIds.indexOf(idOfClickedCard);

            if (index != -1)
                selectedCardIds.remove(index);
        }
    };

    public UIController() {
        selectedCardIds = new ArrayList<Integer>();
    }

    public UIController(boolean mockCreated) {
    }

    public void initGameView(GameView v, int numOfPlayers) {
        gameView = v;

        gameView.setup(0, 1, 2, 3);

        gameView.addStoryCardAction(e -> {
            deckClicked = true;
        });
    }

    public void addCardToPlayer(int playerId, int currentPlayerId, int cardId, String imgUrl) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.addCardToHand(playerId, currentPlayerId, cardId, imgUrl, mouseEvent);
            }
        });
    }

    public void removeCardFromPlayer(int playerId, int cardId) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.removeCardFromHand(playerId, cardId);
            }
        });
    }

    public void removeSelectedCardFromPlayer(int playerId, int cardId) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.removeSelectedCardFromHand(playerId, cardId);
            }
        });
    }

    public void addCardToPlayArea(int playerId, int cardId, String imgUrl) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.addCardToPlayArea(playerId, cardId, imgUrl);
            }
        });
    }

    public void removeCardFromPlayArea(int playerId, int cardId) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.removeCardFromPlayArea(playerId, cardId);
            }
        });
    }

    public void clearPlayArea(int playerId) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.removeAllCardsFromPlayArea(playerId);
            }
        });
    }

    public void flipStoryDeck(int cardId, String cardUrl) {
        //MAKE SURE ANY CALL TO UI IS WRAPPED IN THIS
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.flipStoryCard(cardId, cardUrl);
            }
        });
    }

    public void removeStoryDeckCard() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.removeFlippedStoryCard();
            }
        });
    }

    public void changePlayerRank(int playerId, int newCardId, int oldCardId, String newRankUrl) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameView.removeCardFromAssets(playerId, oldCardId);
                gameView.addCardToAssets(playerId, newCardId, "rank/" + newRankUrl);
            }
        });
    }

    public void setShields(int playerId, int number) {
        //MAKE SURE ANY CALL TO UI IS WRAPPED IN THIS
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.addShields(playerId, number);
            }
        });
    }

    public void addShields(int playerId, int number) {
        //MAKE SURE ANY CALL TO UI IS WRAPPED IN THIS
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.addShields(playerId, number);
            }
        });
    }

    public void removeShields(int playerId, int number) {
        //MAKE SURE ANY CALL TO UI IS WRAPPED IN THIS
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.removeShield(playerId, number);
            }
        });
    }

    public boolean waitUntilCardIsClicked() throws InterruptedException {
        while (!deckClicked) {
            //sit there and wait for it to be clicked
            Thread.sleep(1000);
        }

        //reset it so that it can be clicked next time
        deckClicked = false;

        return true;
    }

    public boolean showEndTurnAndWait(int id) throws InterruptedException {
        Group button = gameView.showEndTurnButton(e -> {
            turnEnded = true;
        }, id);

        while (!turnEnded) {
            //sit there and wait for it to be clicked
            Thread.sleep(1000);
        }


        //reset it so that it can be clicked next time
        turnEnded = false;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.hideButton(button, id);
            }
        });

        return true;
    }

    public void setMessageBoxText(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.setMessageBox(message);
            }
        });
    }

    public boolean prompt(int playerId) throws InterruptedException {
        boolean result = false;

        Group yesButton = gameView.promptYes(e -> {
            promptResult = "yes";
        }, playerId);
        Group noButton = gameView.promptNo(e -> {
            promptResult = "no";
        }, playerId);

        while (promptResult == "") {
            //sit there and wait for it to be clicked
            Thread.sleep(1000);
        }

        if (promptResult == "yes") {
            result = true;
        } else if (promptResult == "no") {
            result = false;
        }


        //reset it so that it can be clicked next time
        promptResult = "";
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.hideButton(yesButton, playerId);
                gameView.hideButton(noButton, playerId);
            }
        });

        return result;
    }

    public synchronized void setPlayerHandSelect(int playerId, boolean onOrOff) {
        if (onOrOff) {
            gameView.addHandCardAction(e -> {
                ImageView clicked = (ImageView) e.getSource();
                int idOfClickedCard = (int) clicked.getUserData(); //user data is card id
                System.out.println("Adventure card is card clicked with user data: " + clicked.getUserData());

                if (null == clicked.getEffect()) {
                    clicked.setEffect(new DropShadow(40, Color.RED));
                    selectedCardIds.add(idOfClickedCard);
                } else {
                    clicked.setEffect(null);
                    selectedCardIds.remove(selectedCardIds.indexOf(idOfClickedCard));
                }

            }, playerId);
        } else {
            gameView.removeHandCardAction(null, playerId);
        }
    }

    public ArrayList<Integer> promptPlayCards(int playerId) throws InterruptedException {
        selectedCardIds.clear();//reset the array to be safe

        //setPlayerHandSelect(playerId, true);//turn on ability to select cards

        Group button = gameView.promptPlayCards(e -> {
            cardsPlayed = true;
        }, playerId);

        while (!cardsPlayed) {
            //sit there and wait for it to be clicked
            Thread.sleep(1000);
        }

        //reset it so that it can be clicked next time
        cardsPlayed = false;

        //setPlayerHandSelect(playerId, false);//turn off ability to select cards

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.hideButton(button, playerId);
            }
        });

        return selectedCardIds;
    }

    public ArrayList<Integer> promptDiscardCards(int playerId) throws InterruptedException {
        selectedCardIds.clear();//reset the array to be safe

        Group button = gameView.promptDiscardCards(e -> {
            cardsDiscarded = true;
        }, playerId);

        Group button2 = gameView.promptPlayAlly(e -> {
            selectedCardIds.add(0, -100); //indicator
            cardsDiscarded = true;
        }, playerId);

        while (!cardsDiscarded) {
            //sit there and wait for it to be clicked
            Thread.sleep(1000);
        }

        //reset it so that it can be clicked next time
        cardsDiscarded = false;


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.hideButton(button, playerId);
                gameView.hideButton(button2, playerId);
            }
        });

        return selectedCardIds;
    }

    public ArrayList<Integer> promptDiscardCardsNoAllys(int playerId) throws InterruptedException {
        selectedCardIds.clear();//reset the array to be safe

        Group button = gameView.promptDiscardCards(e -> {
            cardsDiscarded = true;
        }, playerId);


        while (!cardsDiscarded) {
            //sit there and wait for it to be clicked
            Thread.sleep(1000);
        }

        //reset it so that it can be clicked next time
        cardsDiscarded = false;


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.hideButton(button, playerId);
            }
        });

        return selectedCardIds;
    }

    public int promptBidCards(int playerId) throws InterruptedException {

        Group button = gameView.promptBidCards(e -> {
            cardsPlayed = true;
        }, playerId);

        Group button2 = gameView.promptIncreaseBids(e -> {
            bids++;
            setMessageBoxText("Increased to " + bids + " bids");
        }, playerId);

        Group button3 = gameView.promptDecreaseBids(e -> {
            bids--;
            setMessageBoxText("Decreased to " + bids + " bids");
        }, playerId);

        while (!cardsPlayed) {
            //sit there and wait for it to be clicked
            Thread.sleep(1000);
        }

        //reset it so that it can be clicked next time
        cardsPlayed = false;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.hideButton(button, playerId);
                gameView.hideButton(button2, playerId);
                gameView.hideButton(button3, playerId);
            }
        });

        return bids;
    }

    public void readyForTurn(int id) throws InterruptedException {
        Group button = gameView.promptTurnReady(e -> {
            turnReady = true;
        }, id);

        while (!turnReady) {
            //sit there and wait for it to be clicked
            Thread.sleep(1000);
        }

        //reset it so that it can be clicked next time
        turnReady = false;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.hideButton(button, id);
            }
        });

    }

    public void addCardToAssets(int playerId, int cardId, String url) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.addCardToAssets(playerId, cardId, "rank/" + url);
            }
        });
    }

    public void fillDecks() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //THIS IS MY LINE
                gameView.fillAdventureDeck();
                gameView.fillStoryDeck();
            }
        });
    }

    public void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}