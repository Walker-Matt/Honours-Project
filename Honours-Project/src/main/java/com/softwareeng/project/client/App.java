package com.softwareeng.project.client;

import com.softwareeng.project.client.views.*;
import com.softwareeng.project.commands.ClientCommand;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class App extends Application {

    /* CONSTANTS */
    private static final String TITLE = "Quests Of The Round Table";
    public static final int MIN_WIDTH = 800;
    public static final int MIN_HEIGHT = (int) (MIN_WIDTH * 0.66);


    private Thread clientThread;

    protected BufferedReader in;
    protected PrintWriter out;

    private Stage primaryStage;

    private View activeView;

    private LoginView loginView;
    @FXML
    private AnchorPane rootLayout;

//    GameController gameCon;

    public App() throws IOException {
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        initRootLayout();
        loginView = new LoginView();
        showLoginView();

//        String ip = loginView.getIp();
//        int port = Integer.parseInt(loginView.getPort());
        loginView.addBtnLoginAction(event -> {
            try {
                clientThread = new Thread(new Client(this, loginView.getIp(), loginView.getPort()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientThread.start(); 
        });

//        showMainMenuView();
//        showPlayerSelectionView();



//        System.out.println(clientThread.getState().toString());


    }

    /**
     *
     */
    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/fxml/RootLayout.fxml"));
            rootLayout = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            primaryStage.setTitle(TITLE);
            primaryStage.setMaximized(true);
            // primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public View getActiveView() {
        return activeView;
    }

    /**
     *
     */
    public synchronized void showMainMenuView() {
        showMainMenuView("Awaiting response from the server...");
    }

    /**
     * @param message
     */
    public synchronized void showMainMenuView(String message) {
        MainMenuView mainView = new MainMenuView(message);
        activeView = mainView;
        VBox mainViewLayout = mainView.getLayout();

        rootLayout.getChildren().add(mainViewLayout);

        mainViewLayout.prefWidthProperty().bind(rootLayout.widthProperty());
        mainViewLayout.prefHeightProperty().bind(rootLayout.heightProperty());

        mainView.setMainApp(this);
    }

    /**
     *
     */
    public synchronized void showPlayerSelectionView() {
        PlayerSelectionView playerSelectionView = new PlayerSelectionView();
        activeView = playerSelectionView;

        VBox playerViewLayout = playerSelectionView.getLayout();

        rootLayout.getChildren().add(playerViewLayout);

        playerViewLayout.prefWidthProperty().bind(rootLayout.widthProperty());
        playerViewLayout.prefHeightProperty().bind(rootLayout.heightProperty());

        playerSelectionView.add2PLayerButtonAction(e -> {
            sendCommandToServer(ClientCommand.NUMBER_OF_PLAYERS, "2");
            showWaitingView();
        });

        playerSelectionView.add3PLayerButtonAction(e -> {
            sendCommandToServer(ClientCommand.NUMBER_OF_PLAYERS, "3");
            showWaitingView();
        });

        playerSelectionView.add4PLayerButtonAction(e -> {
            sendCommandToServer(ClientCommand.NUMBER_OF_PLAYERS, "4");
            showWaitingView();
        });

        playerSelectionView.setMainApp(this);
    }

    /**
     * @param numOfPlayers
     */
    public synchronized void showGameView(int numOfPlayers, UIController UICon) {
        try {
        	if(!(activeView instanceof GameView)) {
	            // Load person overview.
	            FXMLLoader loader = new FXMLLoader();
	            loader.setLocation(App.class.getResource("/fxml/GameView.fxml"));
	            BorderPane gameViewLayout = (BorderPane) loader.load();

	            rootLayout.getChildren().add(gameViewLayout);


	            gameViewLayout.prefWidthProperty().bind(rootLayout.widthProperty());
	            gameViewLayout.prefHeightProperty().bind(rootLayout.heightProperty());

	            // Give the controller access to the main app.
	            GameView gameView = loader.getController();
	            activeView = gameView;

	            gameView.setMainApp(this);

	            UICon.initGameView(gameView, numOfPlayers);
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public synchronized void showWaitingView() {
        WaitingForOthersView waitingForOthersView = new WaitingForOthersView();
        activeView = waitingForOthersView;

        VBox waitingViewLayout = waitingForOthersView.getLayout();

        rootLayout.getChildren().add(waitingViewLayout);

        waitingViewLayout.prefWidthProperty().bind(rootLayout.widthProperty());
        waitingViewLayout.prefHeightProperty().bind(rootLayout.heightProperty());

        waitingForOthersView.setMainApp(this);
    }

    public synchronized void showLoginView() {

        activeView = loginView;


        VBox loginViewLayout = loginView.getLayout();

        rootLayout.getChildren().add(loginViewLayout);

        loginViewLayout.prefWidthProperty().bind(rootLayout.widthProperty());
        loginViewLayout.prefHeightProperty().bind(rootLayout.heightProperty());

        loginView.setMainApp(this);
    }

    /**
     *
     * @param numOfPlayers
     */
//    public void initGame(int numOfPlayers) {
//        gameCon = new GameController(UICon);
//        gameCon.initGame(numOfPlayers, 5); //CHANGE THIS TO CHANGE THE SCENARIO INITIALIZED
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                startGame();
//            }
//        }).start();
//    }
//
//    public void startGame() {
//        gameCon.startGame();
//    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * @param cmd
     * @param args
     */
    public void sendCommandToServer(ClientCommand cmd, String args) {
        this.out.println(cmd + " " + args);
    }



}

