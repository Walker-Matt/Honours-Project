package com.softwareeng.project.client.views;


import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class WaitingForOthersView extends View {

    private static final int LAYOUT_SPACING = 20;
    private static final int LAYOUT_PADDING = 20;


    private VBox layout;

    public WaitingForOthersView() {
        super();
        layout = createLayout(LAYOUT_SPACING, LAYOUT_PADDING);


        ObservableList<Node> list = layout.getChildren();

        Label lblMsg = new Label("Waiting for other players to join");
        lblMsg.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        lblMsg.setTextFill(Color.LIGHTGOLDENRODYELLOW);
        list.add(lblMsg);
    }

    public VBox getLayout() {
        return layout;
    }

}
