package com.softwareeng.project.client;


import com.softwareeng.project.client.views.GameView;
import com.softwareeng.project.commands.ClientCommand;
import com.softwareeng.project.commands.ServerCommand;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client extends Task<Void>/* implements Runnable*/ {

    private Socket socket;
    private BufferedReader in;
    public PrintWriter out;
    private App app;
    UIController UICon;

    public Client(App app, String ip, int port) throws IOException {
        this.app = app;
        
        if(ip.equals("") && port == 0) {
            socket = new Socket("localhost", 9876);
        } else {
            socket = new Socket(ip, port);
        }
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        app.in = in;
        out = new PrintWriter(socket.getOutputStream(), true);
        app.out = out;

        UICon = new UIController();
    }


    /**
     * @return
     * @throws Exception
     */
    @Override
    protected Void call() throws Exception {
        // Process all messages from server, according to the protocol.
        String response;
        while ((response = in.readLine()) != null) {
            Scanner scanner = new Scanner(response);
            String cmd = scanner.next();
            if (cmd.equals(ServerCommand.SHOW_PLAYER_SELECTION_VIEW.toString())) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        app.showPlayerSelectionView();
                    }
                });

            } else if (cmd.equals(ServerCommand.WAIT_FOR_SETUP.toString())) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        app.showWaitingView();
                    }
                });


            } else if (cmd.equals(ServerCommand.SHOW_GAMEVIEW.toString())) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //javaFX doesn't let you rerun a view that is already running
                        if (app.getActiveView() instanceof GameView) {
                            //do nothing
                        } else {
                            int numOfPlayers = scanner.nextInt();
                            app.showGameView(numOfPlayers, UICon);

                        }

                    }
                });
            } else if (cmd.equals(ServerCommand.PROMPT_FLIP_STORY.toString())) {
                UICon.waitUntilCardIsClicked();
                out.println(ClientCommand.STORY_DECK_CLICKED);
            } else if (cmd.equals(ServerCommand.SET_MESSAGE_BOX.toString())) {
                String message = scanner.nextLine();
                UICon.setMessageBoxText(message);
            } else if (cmd.equals(ServerCommand.FLIP_STORY_CARD.toString())) {
                int cardId = scanner.nextInt();
                String url = scanner.nextLine();
                UICon.flipStoryDeck(cardId, url);
            } else if (cmd.equals(ServerCommand.PROMPT_YES_NO.toString())) {
                int playerId = scanner.nextInt();
                boolean result = UICon.prompt(playerId);
                out.println(ClientCommand.PROMPT_YES_NO_REPLY + " " + result);
            } else if (cmd.equals(ServerCommand.PROMPT_PLAY_CARDS.toString())) {
                int playerId = scanner.nextInt();
                ArrayList<Integer> result = UICon.promptPlayCards(playerId);
                out.println(ClientCommand.PROMPT_PLAY_CARDS_REPLY + " " + result.toString());
            } else if (cmd.equals(ServerCommand.REMOVE_CARD_FROM_HAND.toString())) {
                int playerId = scanner.nextInt();
                int cardId = scanner.nextInt();
                UICon.removeSelectedCardFromPlayer(playerId, cardId);
            } else if (cmd.equals(ServerCommand.ADD_CARD_TO_PLAY_AREA.toString())) {
                int playerId = scanner.nextInt();
                int cardId = scanner.nextInt();
                String url = scanner.nextLine();
                UICon.addCardToPlayArea(playerId, cardId, url);
            } else if (cmd.equals(ServerCommand.ADD_CARD_TO_HAND.toString())) {
                int playerId = scanner.nextInt();
                int currId = scanner.nextInt();
                int cardId = scanner.nextInt();
                String url = scanner.nextLine();
                UICon.addCardToPlayer(playerId, currId, cardId, url);
            } else if (cmd.equals(ServerCommand.REMOVE_CARD_FROM_PLAY_AREA.toString())) {
                int playerId = scanner.nextInt();
                int cardId = scanner.nextInt();
                UICon.removeCardFromPlayArea(playerId, cardId);
            } else if (cmd.equals(ServerCommand.REMOVE_ALL_CARDS_FROM_PLAY_AREA.toString())) {
                int playerId = scanner.nextInt();
                UICon.clearPlayArea(playerId);
            } else if (cmd.equals(ServerCommand.PROMPT_DISCARD_CARDS.toString())) {
                int playerId = scanner.nextInt();
                ArrayList<Integer> result = UICon.promptDiscardCards(playerId);
                out.println(ClientCommand.PROMPT_DISCARD_REPLY + " " + result.toString());
            } else if (cmd.equals(ServerCommand.PROMPT_BID_CARDS.toString())) {
                int playerId = scanner.nextInt();
                int bids = UICon.promptBidCards(playerId);
                out.println(ClientCommand.PROMPT_BIDS_REPLY + " " + bids);
            } else if (cmd.equals(ServerCommand.PROMPT_TEST_DISCARD.toString())) {
                int playerId = scanner.nextInt();
                ArrayList<Integer> result = UICon.promptDiscardCardsNoAllys(playerId);
                out.println(ClientCommand.PROMPT_TEST_DISCARD_REPLY + " " + result.toString());
            } else if (cmd.equals(ServerCommand.ADD_SHIELDS.toString())) {
                int playerId = scanner.nextInt();
                int amount = scanner.nextInt();
                UICon.addShields(playerId, amount);
            } else if (cmd.equals(ServerCommand.REMOVE_SHIELDS.toString())) {
                int playerId = scanner.nextInt();
                int amount = scanner.nextInt();
                UICon.removeShields(playerId, amount);
            } else if (cmd.equals(ServerCommand.SET_SHIELDS.toString())) {
                int playerId = scanner.nextInt();
                int amount = scanner.nextInt();
                UICon.setShields(playerId, amount);
            } else if (cmd.equals(ServerCommand.CHANGE_PLAYER_RANK.toString())) {
                int playerId = scanner.nextInt();
                int newRankId = scanner.nextInt();
                int currentRankId = scanner.nextInt();
                String newRankUrl = scanner.nextLine();
                UICon.changePlayerRank(playerId, newRankId, currentRankId, newRankUrl);
            } else if (cmd.equals(ServerCommand.REMOVE_FLIPPED_STORY_CARD.toString())) {
                UICon.removeStoryDeckCard();
            } else if (cmd.equals(ServerCommand.SHOW_END_TURN_BUTTON.toString())) {
                int playerId = scanner.nextInt();
                boolean result = UICon.showEndTurnAndWait(playerId);
                out.println(ClientCommand.PROMPT_END_TURN + " " + result);
            } else if (cmd.equals(ServerCommand.ADD_CARD_TO_ASSETS.toString())) {
                int playerId = scanner.nextInt();
                int cardId = scanner.nextInt();
                String url = scanner.nextLine();
                UICon.addCardToAssets(playerId, cardId, url);
            } else if (cmd.equals(ServerCommand.FILL_DECK.toString())) {
                UICon.fillDecks();
            }
        }
        return null;
    }

}
