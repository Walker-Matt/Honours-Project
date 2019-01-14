package com.softwareeng.project.client.views;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PlayerSelectionView extends View {

    private static final int LAYOUT_SPACING = 20;
    private static final int LAYOUT_PADDING = 20;


    private VBox layout;
    private Button btn2Player;
    private Button btn3Player;
    private Button btn4Player;
    private Button btnExit;

    public PlayerSelectionView() {
        super();
        layout = createLayout(LAYOUT_SPACING, LAYOUT_PADDING);

        initPlayerSelectionButtons();
    }

    public VBox getLayout() {

        return layout;

    }


    private void initPlayerSelectionButtons() {
        ObservableList<Node> list = layout.getChildren();

        Label lblMsg = new Label("Choose number of players");
        lblMsg.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        lblMsg.setTextFill(Color.LIGHTGOLDENRODYELLOW);
        list.add(lblMsg);

        btn2Player = createBtn("2 players");
        list.add(btn2Player);

        btn3Player = createBtn("3 players");
        list.add(btn3Player);

        btn4Player = createBtn("4 players");
        list.add(btn4Player);

        btnExit = createBtn("EXIT");
        list.add(btnExit);

    }

    public void add2PLayerButtonAction(EventHandler<ActionEvent> event) {
        btn2Player.setOnAction(event);
    }

    public void add3PLayerButtonAction(EventHandler<ActionEvent> event) {
        btn3Player.setOnAction(event);
    }

    public void add4PLayerButtonAction(EventHandler<ActionEvent> event) {
        btn4Player.setOnAction(event);
    }

    public void addExitButtonAction(EventHandler<ActionEvent> event) {
        btnExit.setOnAction(event);
    }


}
