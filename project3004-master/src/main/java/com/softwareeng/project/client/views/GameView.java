package com.softwareeng.project.client.views;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameView extends View {
    final static Logger log = Logger.getLogger(GameView.class);

    /****************************************************************
     *********************FXML INJECTIONS****************************
     ***************************************************************/
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private BorderPane board;

    @FXML
    private HBox bottomPlayer;

    @FXML
    private BorderPane boardCenter;

    @FXML
    private HBox bottomPlayerAssets;

    @FXML
    private BorderPane playAreas;

    @FXML
    private HBox bottomPlayerPlayArea;

    @FXML
    private HBox decksArea;

    @FXML
    private VBox leftPlayerPlayArea;

    @FXML
    private VBox rightPlayerPlayArea;

    @FXML
    private HBox topPlayerPlayArea;

    @FXML
    private VBox leftPlayerAssets;

    @FXML
    private VBox rightPlayerAssets;

    @FXML
    private HBox topPlayerAssets;

    @FXML
    private VBox leftPlayer;

    @FXML
    private VBox rightPlayer;

    @FXML
    private HBox topPlayer;

    @FXML
    private HBox messageBox;

    /*****************************************************************
     * END OF FXML INJECTION AND CONTAINER DECLARATIONS
     ******************************************************************/

    /*******************************************************************
     * *************************PRIVATE STATIC FINALS*******************
     ******************************************************************/
    private static final int CARD_PREF_WIDTH = 100;
    private static final int CARD_MIN_HEIGHT = 120;
    private static final String STORY_CARD_BACK_PATH = "back/adventure_back.jpg";
    private static final String ADVENTURE_CARD_BACK_PATH = "back/storyBack.jpg";


    /***********************************************************
     *************************MAPS******************************
     **********************************************************/
    /**
     * hands of players
     */
    private Map<Integer, ObservableList<Node>> playerHands;

    /**
     * shields belonging to players
     * We will show A shield image at all times and use playerShieldCounters to display the amount on that image
     * NO PUBLIC ACCESS
     */
    private Map<Integer, Node> playerShields;

    /**
     * the area where a player can set up Quests, or play foe/weapon cards
     */
    private Map<Integer, ObservableList<Node>> playerPlayAreas;

    /**
     * area where amour/allies are played
     */
    private Map<Integer, ObservableList<Node>> assetAreas;

    /**
     * counter for shields
     */
    private Map<Integer, Integer> playerShieldCounters;

    /**
     * these are the rank cards belonging to the players
     */
    private Map<Integer, ObservableList<Node>> playerRankCards;

    /**
     *
     */
    private Map<Integer, PlayerPosition> playerPositions;

    /*****************************************************
     ******************END OF MAPS**********************
     ***************************************************/

    /*******************************************
     ***************OBSERVABLE LISTS************
     *******************************************/
    /**
     * game adventure deck
     */
    private ObservableList<Node> deckAdventure;

    /**
     * game story deck
     */
    private ObservableList<Node> deckStory;

    /*********************************
     * END OF OBSERVABLE LISTS********
     *********************************/

    /**
     * CONSTRUCTOR
     * NOTE: the constructor does NOT have access to @FXML fields referring to components defined in the .fxml file, while initialize() does have access to them.
     */
    public GameView() {
    }


    /**
     * called after @FXML annotated fields are populated
     */
    @FXML
    public void initialize() {
        //reduced spacing to give nice hand of cards look
        bottomPlayer.setSpacing(-25);
        leftPlayer.setSpacing(-50);
        topPlayer.setSpacing(-25);
        rightPlayer.setSpacing(-50);

        //reduced spacing to make vertical assets area look nice
        leftPlayerAssets.setSpacing(-15);
        rightPlayerAssets.setSpacing(-15);

        //reduce spacing between cards
        leftPlayerPlayArea.setSpacing(-40);
        rightPlayerPlayArea.setSpacing(-40);

        //for better look
        bottomPlayerAssets.setSpacing(5);
        topPlayerAssets.setSpacing(5);

        //put space between story and adventure deck
        decksArea.setSpacing(10);

        //set right and left player play area spacing to normal
        rightPlayerPlayArea.setSpacing(-10);
        leftPlayerPlayArea.setSpacing(-10);
        
        bottomPlayer.setId("bottomPlayerHand");
        bottomPlayerPlayArea.setId("bottomPlayerPlayArea");
        
        leftPlayer.setId("leftPlayerHand");
        leftPlayerPlayArea.setId("leftPlayerPlayArea");
    }


    /******************************************************
     ********************GUI MAPPING***********************
     *****************************************************/
    /**
     * receives player ids and maps out game areas to each player
     *
     * @param playerIDs
     */
    private void initPlayerMaps(ArrayList<Integer> playerIDs) {

        //bottom for player1 and top for player2 in a 2 player game
        if (playerIDs.size() == 2) {
            playerHands.put(playerIDs.get(0), bottomPlayer.getChildren());
            playerPlayAreas.put(playerIDs.get(0), bottomPlayerPlayArea.getChildren());
            playerShieldCounters.put(playerIDs.get(0), 0);
            assetAreas.put(playerIDs.get(0), bottomPlayerAssets.getChildren());


            playerHands.put(playerIDs.get(1), topPlayer.getChildren());
            playerPlayAreas.put(playerIDs.get(1), topPlayerPlayArea.getChildren());
            playerShieldCounters.put(playerIDs.get(1), 0);
            assetAreas.put(playerIDs.get(1), topPlayerAssets.getChildren());

        }

        //CLOCKWISE
        if (playerIDs.size() >= 3) {
            playerHands.put(playerIDs.get(0), bottomPlayer.getChildren());
            playerPlayAreas.put(playerIDs.get(0), bottomPlayerPlayArea.getChildren());
            playerShieldCounters.put(playerIDs.get(0), 0);
            assetAreas.put(playerIDs.get(0), bottomPlayerAssets.getChildren());

            playerHands.put(playerIDs.get(1), leftPlayer.getChildren());
            playerPlayAreas.put(playerIDs.get(1), leftPlayerPlayArea.getChildren());
            playerShieldCounters.put(playerIDs.get(1), 0);
            assetAreas.put(playerIDs.get(1), leftPlayerAssets.getChildren());


            playerHands.put(playerIDs.get(2), topPlayer.getChildren());
            playerPlayAreas.put(playerIDs.get(2), topPlayerPlayArea.getChildren());
            playerShieldCounters.put(playerIDs.get(2), 0);
            assetAreas.put(playerIDs.get(2), topPlayerAssets.getChildren());


        }
        if (playerIDs.size() == 4) {
            playerHands.put(playerIDs.get(3), rightPlayer.getChildren());
            playerPlayAreas.put(playerIDs.get(3), rightPlayerPlayArea.getChildren());
            playerShieldCounters.put(playerIDs.get(3), 0);
            assetAreas.put(playerIDs.get(3), rightPlayerAssets.getChildren());
        }
    }

    /**
     * @param playerIDs
     */
    private void init(ArrayList<Integer> playerIDs) {
        playerShieldCounters = new HashMap<>();
        playerHands = new HashMap<>();
        playerPlayAreas = new HashMap<>();
        assetAreas = new HashMap<>();
        playerShields = new HashMap<>();

        initPlayerMaps(playerIDs);
        initCentre();
        initMessage();
    }


    /**
     * set up 2 player game
     *
     * @param bottomPlayerID
     * @param topPlayerID
     */
    public void setup(int bottomPlayerID, int topPlayerID) {
        ArrayList<Integer> playerIDs = new ArrayList<>();

        playerIDs.add(bottomPlayerID);
        playerIDs.add(topPlayerID);

        init(playerIDs);

        playerPositions = new HashMap<>();
        playerPositions.put(bottomPlayerID, PlayerPosition.BOTTOM);
        playerPositions.put(topPlayerID, PlayerPosition.TOP);

        StackPane bottomShield = stackShieldCountOnShield(bottomPlayerID, "yellow_shield.jpg");
        playerShields.put(bottomPlayerID, bottomShield);
        bottomPlayerAssets.getChildren().add(playerShields.get(bottomPlayerID));

        StackPane topShield = stackShieldCountOnShield(topPlayerID, "blue_shield.jpg");
        playerShields.put(topPlayerID, topShield);
        playerShields.get(topPlayerID).setRotate(180);
        topPlayerAssets.getChildren().add(playerShields.get(topPlayerID));
    }

    /**
     * setup 3 player game
     *
     * @param bottomPlayerID
     * @param leftPlayerID
     * @param topPlayerID
     */
    public void setup(int bottomPlayerID, int leftPlayerID, int topPlayerID) {
        ArrayList<Integer> playerIDs = new ArrayList<>();

        playerIDs.add(bottomPlayerID);
        playerIDs.add(leftPlayerID);
        playerIDs.add(topPlayerID);

        init(playerIDs);

        playerPositions = new HashMap<>();
        playerPositions.put(bottomPlayerID, PlayerPosition.BOTTOM);
        playerPositions.put(leftPlayerID, PlayerPosition.LEFT);
        playerPositions.put(topPlayerID, PlayerPosition.TOP);

        StackPane bottomShield = stackShieldCountOnShield(bottomPlayerID, "yellow_shield.jpg");
        playerShields.put(bottomPlayerID, bottomShield);
        bottomPlayerAssets.getChildren().add(playerShields.get(bottomPlayerID));

        StackPane leftShield = stackShieldCountOnShield(leftPlayerID, "red_shield.jpg");
        playerShields.put(leftPlayerID, leftShield);
        playerShields.get(leftPlayerID).setRotate(90);
        leftPlayerAssets.getChildren().add(playerShields.get(leftPlayerID));

        StackPane topShield = stackShieldCountOnShield(topPlayerID, "blue_shield.jpg");
        playerShields.put(topPlayerID, topShield);
        playerShields.get(topPlayerID).setRotate(180);
        topPlayerAssets.getChildren().add(playerShields.get(topPlayerID));
    }

    /**
     * set up 4 player game
     *
     * @param bottomPlayerID
     * @param leftPlayerID
     * @param topPlayerID
     * @param rightPlayerID
     */
    public void setup(int bottomPlayerID, int leftPlayerID, int topPlayerID, int rightPlayerID) {
        ArrayList<Integer> playerIDs = new ArrayList<>();
        playerIDs.add(bottomPlayerID);
        playerIDs.add(leftPlayerID);
        playerIDs.add(topPlayerID);
        playerIDs.add(rightPlayerID);

        init(playerIDs);

        playerPositions = new HashMap<>();
        playerPositions.put(bottomPlayerID, PlayerPosition.BOTTOM);
        playerPositions.put(leftPlayerID, PlayerPosition.LEFT);
        playerPositions.put(topPlayerID, PlayerPosition.TOP);
        playerPositions.put(rightPlayerID, PlayerPosition.RIGHT);

        StackPane bottomShield = stackShieldCountOnShield(bottomPlayerID, "yellow_shield.jpg");
        playerShields.put(bottomPlayerID, bottomShield);
        bottomPlayerAssets.getChildren().add(playerShields.get(bottomPlayerID));

        StackPane leftShield = stackShieldCountOnShield(leftPlayerID, "red_shield.jpg");
        playerShields.put(leftPlayerID, leftShield);
        playerShields.get(leftPlayerID).setRotate(90);
        leftPlayerAssets.getChildren().add(playerShields.get(leftPlayerID));

        StackPane topShield = stackShieldCountOnShield(topPlayerID, "blue_shield.jpg");
        playerShields.put(topPlayerID, topShield);
        topPlayerAssets.getChildren().add(playerShields.get(topPlayerID));
        playerShields.get(topPlayerID).setRotate(180);

        StackPane rightShield = stackShieldCountOnShield(rightPlayerID, "green_shield.jpg");
        playerShields.put(rightPlayerID, rightShield);
        playerShields.get(rightPlayerID).setRotate(270);
        rightPlayerAssets.getChildren().add(playerShields.get(rightPlayerID));


    }

    /*******************************
     * GUI MAPPING ENDS************
     *******************************/


    /************************************************************
     ********** MODIFIERS TO BE USED BY CONTROLLERS **************
     *************************************************************/

    /**
     * set the text for messageBox
     *
     * @param message
     */
    public void setMessageBox(String message) {
        ((Label) messageBox.getChildren().get(0)).setText(message);
    }


    /***********************************************
     *************************HAND*********
     ************************************************/
    /**
     * to add a card to a specific hand
     *
     * @param playerId
     * @param cardId
     * @param imgUrl
     */
    public void addCardToHand(int playerId, int currentPlayerId, int cardId, String imgUrl, EventHandler<MouseEvent> mouseEvent) {
        if (playerId == currentPlayerId) { //if current player show
            addCardToMap(playerHands, playerId, cardId, imgUrl, mouseEvent);
        } else { //if not current player hide
            addCardToMap(playerHands, playerId, cardId, "back/adventure_back.jpg", mouseEvent);
        }
    }

    //siraj's overloaded method's for testing purposes only

    /**
     * @param playerId
     * @param cardId
     * @param imgUrl
     */
    public void addCardToHand(int playerId, int cardId, String imgUrl) {
        addCardToMap(playerHands, playerId, cardId, imgUrl);
    }


    /**
     * to remove a card from a specific hand
     *
     * @param playerId
     * @param cardId
     */
    public void removeCardFromHand(int playerId, int cardId) {
        removeNodeFromObservableList(playerHands.get(playerId), cardId);
    }

    /**
     * @param playerId
     * @param cardId
     */
    public void removeSelectedCardFromHand(int playerId, int cardId) {
        removeSelectedNodeFromObservableList(playerHands.get(playerId), cardId);
    }

    /**
     * When a card in hand is clicked, we should
     * 1) highlight the UI
     * 2) Add this card to this player's selected list
     */
    public synchronized void addHandCardAction(EventHandler<MouseEvent> storyClicked, int playerId) {
        for (Node node : playerHands.get(playerId)) {
            node.setOnMouseClicked(storyClicked);
        }
    }

    /**
     * @param storyClicked
     * @param playerId
     */
    public void removeHandCardAction(EventHandler<MouseEvent> storyClicked, int playerId) {
        for (Node node : playerHands.get(playerId)) {
            node.setEffect(null);
            node.setOnMouseClicked(null);
        }
    }

    /**
     * @param playerId
     * @param cardId
     * @param imgUrl
     */
    public void changeCardImage(int playerId, int cardId, String imgUrl) {
        ImageView card = createCard(cardId, imgUrl);

        for (Node node : playerHands.get(playerId)) {
            if (Integer.parseInt(node.getId()) == cardId) {
                node = card;
            }
        }
    }

    /***********************************************
     ******************DECKS*************************
     ************************************************/

    /**
     * @param storyClicked
     */
    public void addStoryCardAction(EventHandler<MouseEvent> storyClicked) {
        decksArea.addEventHandler(MouseEvent.MOUSE_CLICKED, storyClicked);
        decksArea.setId("decksArea");
    }

    /**
     *
     */
    public void emptyAdventureDeck() {
        deckAdventure.removeAll();
    }

    /**
     *
     */
    public void emptyStoryDeck() {
        deckStory.removeAll();
    }

    /**
     * to flip a story card
     *
     * @param cardId
     * @param imgUrl
     */
    public void flipStoryCard(int cardId, String imgUrl) {
        addCardToObservableList(deckStory, cardId, imgUrl);
    }

    /**
     * to remove the top card that is flipped from story deck
     */
    public void removeFlippedStoryCard() {
        deckStory.remove(3);
    }

    /**
     * shows that the adventure deck has at least one card upside down
     */
    public void fillAdventureDeck() {
        addCardToObservableList(deckAdventure, -1, ADVENTURE_CARD_BACK_PATH);
    }

    /**
     * shows that the story deck has at least one card upside down
     */
    public void fillStoryDeck() {
        addCardToObservableList(deckStory, -1, STORY_CARD_BACK_PATH);
    }


    /***************************************************
     ***********************ASSETS**********************
     * *************************************************
     * /

     /**
     * adds card to the asset area of a specific player
     * @param playerId
     * @param cardId
     * @param imgUrl
     */
    public void addCardToAssets(int playerId, int cardId, String imgUrl) {
        addCardToMap(assetAreas, playerId, cardId, imgUrl, null);
    }

    /**
     * @param playerId
     * @param cardId
     */
    public void removeCardFromAssets(int playerId, int cardId) {
        removeNodeFromObservableList(assetAreas.get(playerId), cardId);
    }


    /*********************************************************
     *********************PLAY AREAS************************
     *********************************************************/


    /**
     * @param playerId
     * @param cardId
     * @param imgUrl
     */
    public void addCardToPlayArea(int playerId, int cardId, String imgUrl) {
        addCardToMap(playerPlayAreas, playerId, cardId, imgUrl, null);
    }

    /**
     * @param playerId
     * @param cardId
     */
    public void removeCardFromPlayArea(int playerId, int cardId) {
        removeNodeFromObservableList(playerPlayAreas.get(playerId), cardId);
    }

    /**
     * @param playerId
     */
    public void removeAllCardsFromPlayArea(int playerId) {
        for (Node node : playerPlayAreas.get(playerId)) {
            removeNodeFromObservableList(playerPlayAreas.get(playerId), (int) node.getUserData());
        }
    }


    /***************************************************************
     *******************************Rank****************************
     ****************************************************************/
    public void addCardToRankArea(int playerId, int cardId, String imgUrl) {
        addCardToMap(playerRankCards, playerId, cardId, "rank/" + imgUrl, null);
    }


    /**********************************************************************
     ***************************SHIELDS************************************
     *********************************************************************/

    /**
     * adds numOfShields specific player's shields
     *
     * @param playerId
     * @param numOfShields
     */
    public void addShields(int playerId, int numOfShields) {
        playerShieldCounters.put(playerId, playerShieldCounters.get(playerId) + numOfShields);
        setShieldCountLabel(playerId);
    }

    /**
     * removes numOfShields from specific player's shields
     *
     * @param playerId
     */
    public void removeShield(int playerId, int numOfShields) {
        playerShieldCounters.put(playerId, playerShieldCounters.get(playerId) - numOfShields);
        setShieldCountLabel(playerId);
    }


    /***********************************************************
     *******************PROMPTS**************************
     *************************************************************/

    /**
     * @param button
     * @param id
     */

    public void hideButton(Group button, int id) {
        playerPlayAreas.get(id).remove(button);
    }

    /**
     * prompts specific player for end turn
     *
     * @param event
     * @param id
     * @return Group
     */
    public Group showEndTurnButton(EventHandler<ActionEvent> event, int id) {
        return playerPromptButtonCreator("End Turn", event, id);
    }


    /**
     * @param event
     * @param id
     * @return
     */
    public Group promptYes(EventHandler<ActionEvent> event, int id) {

        return playerPromptButtonCreator("Yes", event, id);
    }

    /**
     * @param event
     * @param id
     * @return
     */
    public Group promptNo(EventHandler<ActionEvent> event, int id) {
        return playerPromptButtonCreator("No", event, id);
    }

    /**
     * @param event
     * @param id
     * @return
     */
    public Group promptPlayCards(EventHandler<ActionEvent> event, int id) {
        return playerPromptButtonCreator("Play Cards", event, id);
    }

    /**
     * @param event
     * @param id
     * @return
     */
    public Group promptTurnReady(EventHandler<ActionEvent> event, int id) {
        return playerPromptButtonCreator("Ready", event, id);
    }

    /**
     * @param event
     * @param id
     * @return
     */
    public Group promptDiscardCards(EventHandler<ActionEvent> event, int id) {
        return playerPromptButtonCreator("Discard", event, id);
    }

    /**
     * @param event
     * @param id
     * @return
     */
    public Group promptBidCards(EventHandler<ActionEvent> event, int id) {
        return playerPromptButtonCreator("Bid Cards", event, id);
    }

    /**
     * @param event
     * @param id
     * @return
     */
    public Group promptIncreaseBids(EventHandler<ActionEvent> event, int id) {
        return playerPromptButtonCreator("+1 bid", event, id);
    }

    /**
     * @param event
     * @param id
     * @return
     */
    public Group promptDecreaseBids(EventHandler<ActionEvent> event, int id) {
        return playerPromptButtonCreator("-1 bid", event, id);
    }

    /**
     * @param event
     * @param id
     * @return
     */
    public Group promptPlayAlly(EventHandler<ActionEvent> event, int id) {
        return playerPromptButtonCreator("Play Ally/Amour", event, id);

    }

    /*********************************************************************************
     ************* HELPERS********************
     *********************************************************************************/

    private void initMessage() {
        Label text = new Label("Click on the story deck to get started!");
        text.setId("message");
        text.setWrapText(true);

        messageBox.getChildren().add(text);
        messageBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");
        messageBox.setPadding(new Insets(20));

    }

    private StackPane stackShieldCountOnShield(int playerId, String imgUrl) {
        StackPane shieldCountBox = new StackPane();
        shieldCountBox.getChildren().add(createCard(-1, imgUrl));
        Label shieldCount = new Label(playerShieldCounters.get(playerId).toString());
        shieldCount.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        shieldCountBox.getChildren().add(shieldCount);
        shieldCountBox.setAlignment(Pos.CENTER);
        shieldCountBox.setMinWidth(10);
        return shieldCountBox;
    }

    /**
     * to reset value of shield by player id
     */
    private void setShieldCountLabel(int playerId) {
        ((Label) ((StackPane) playerShields.get(playerId)).getChildren().get(1)).setText(playerShieldCounters.get(playerId).toString());
    }

    /**
     * @param resource
     * @return Image
     */
    private Image getImage(String resource) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(resource);
        File file = null;
        if (url != null) {
            file = new File(url.getFile());
        }
        Image thisImage = null;
        try {
            if (file != null) {
                thisImage = new Image(new FileInputStream(file));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return thisImage;
    }


    /**
     * init centre of the game view
     * specifically placement of story deck and adventure deck
     */
    private void initCentre() {
        deckAdventure = decksArea.getChildren();

        deckStory = decksArea.getChildren();
        
    }

    /**
     * to create a card
     *
     * @param path
     * @return ImageView
     */
    private ImageView createCard(int cardId, String path) {
        path = path.replace(" ", "");
        ImageView imv = new ImageView(path);
        Image type = getImage(path);
        imv.setImage(type);
        imv.setFitWidth(CARD_PREF_WIDTH);
        imv.setFitHeight(CARD_MIN_HEIGHT);
        imv.setUserData(cardId);
        imv.setId(String.valueOf(cardId) + path);
        return imv;
    }


    /**
     * to add a card to an ObservableList
     *
     * @param list
     * @param cardId
     * @param imgUrl
     */
    private void addCardToObservableList(ObservableList<Node> list, int cardId, String imgUrl) {

        list.add(createCard(cardId, imgUrl));

    }

    /**
     * to remove a Node from a specific ObservableList
     *
     * @param list
     * @param cardId
     */
    private void removeNodeFromObservableList(ObservableList<Node> list, int cardId) {
        Node nodeToRemove = null;

        for (Node node : list) {
            if (node instanceof ImageView) {
                if (node.getId().contains(String.valueOf(cardId))) {
                    nodeToRemove = node;
                    break; //doesn't cause problem
                }
            }
        }
        list.remove(nodeToRemove);
    }

    private void removeSelectedNodeFromObservableList(ObservableList<Node> list, int cardId) {
        Node nodeToRemove = null;

        for (Node node : list) {
            if (node.getId().contains(String.valueOf(cardId))) {
                if (node.getEffect() == null) {
                    continue;
                }
                nodeToRemove = node;
                break; //doesn't cause problem
            }
        }

        if (nodeToRemove == null) {
            removeNodeFromObservableList(list, cardId);
        } else {
            list.remove(nodeToRemove);
        }
    }


    /**
     * @param map
     * @param playerId
     * @param cardId
     * @param imgUrl
     */
    private void addCardToMap(Map<Integer, ObservableList<Node>> map, int playerId, int cardId, String imgUrl, EventHandler<MouseEvent> mouseEvent) {
        //rotation is hard coded for now
        PlayerPosition pos = playerPositions.get(playerId);

        imgUrl = imgUrl.replace(" ", "");

        ImageView imv = createCard(cardId, imgUrl);
        if (pos == PlayerPosition.BOTTOM) {
            //DO NOTHING
        } else if (pos == PlayerPosition.LEFT) {
            imv.setRotate(90);
        } else if (pos == PlayerPosition.RIGHT) {
            imv.setRotate(270);
        } else {
            imv.setRotate(180);
        }
        imv.setOnMouseClicked(mouseEvent);

        map.get(playerId).add(imv);
    }

    //Siraj's overloaded method for testing purpose
    private void addCardToMap(Map<Integer, ObservableList<Node>> map, int playerId, int cardId, String imgUrl) {
        //rotation is hard coded for now
        PlayerPosition pos = playerPositions.get(playerId);
        ImageView imv = createCard(cardId, imgUrl);
        if (pos == PlayerPosition.BOTTOM) {
            //DO NOTHING
        } else if (pos == PlayerPosition.LEFT) {
            imv.setRotate(90);
        } else if (pos == PlayerPosition.RIGHT) {
            imv.setRotate(270);
        } else {
            imv.setRotate(180);
        }
        //imv.setOnMouseClicked(mouseEvent);

        map.get(playerId).add(imv);
    }

    private Group playerPromptButtonCreator(String buttonText, EventHandler<ActionEvent> event, int id) {
        Button button = createBtn(buttonText);
        button.setMinWidth(BTN_MAX_WIDTH);
        button.setMinHeight(BTN_MAX_HEIGHT);
        button.setOnAction(event);
        button.setId(buttonText);

        if (playerPositions.get(id) == PlayerPosition.LEFT) {
            button.setRotate(90);
            //this line is no longer needed
            //leftPlayerPlayArea.setSpacing(30);
        }
        if (playerPositions.get(id) == PlayerPosition.RIGHT) {
            button.setRotate(270);
            //this line is no longer needed
            //   rightPlayerPlayArea.setSpacing(30);
        }

        Group grp = new Group(button);
        Platform.runLater(new Runnable() { //and create the button if it doesnt exist if it doesnt
            @Override
            public void run() {
                //THIS IS MY LINE
                playerPlayAreas.get(id).add(grp);
            }
        });
        return grp;
    }

    private enum PlayerPosition {
        BOTTOM,
        LEFT,
        TOP,
        RIGHT
    }
}

