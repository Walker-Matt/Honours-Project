package com.softwareeng.project.client.views;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class MainMenuView extends View {
	private static final int LAYOUT_SPACING = 100;
    private static final int LAYOUT_PADDING = 150;

    private VBox layout;
    private Button startBtn;
    private Label lblMessage;

    public MainMenuView(String message) {
        super();

        layout = createLayout(LAYOUT_SPACING, LAYOUT_PADDING);
        Label label1 = new Label("Quests Of The Round Table");
        label1.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        label1.setTextFill(Color.LIGHTGOLDENRODYELLOW);

        lblMessage = new Label(message);
        lblMessage.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        lblMessage.setTextFill(Color.LIGHTGOLDENRODYELLOW);
//
//        startBtn = createBtn("Start Game");
//
//
//        GridPane gridPane1 = new GridPane();
//        GridPane gridPane2 = new GridPane();
//
//
//        gridPane1.addRow(0, label1);
//        gridPane2.addRow(0, startBtn);
//        gridPane1.setAlignment(Pos.CENTER);
//        gridPane2.setAlignment(Pos.CENTER);
//
//        layout.getChildren().addAll(gridPane1, gridPane2);
        layout.getChildren().add(lblMessage);
        lblMessage.setAlignment(Pos.CENTER);

    }

    public void addStartButtonAction(EventHandler<ActionEvent> event) {
        startBtn.setOnAction(event);
    }

    public void setMessage(String message) {
        lblMessage.setText(message);
    }

    public VBox getLayout() {

        return layout;
    }
}
