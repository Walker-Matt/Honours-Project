package com.softwareeng.project.client.views;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class LoginView extends View {


    private static final int LAYOUT_SPACING = 20;
    private static final int LAYOUT_PADDING = 20;


    private VBox layout;
    private Label lblIp;
    private Label lblPort;
    final TextField txtIp;
    final TextField txtPort;
    private Button btnConnect;
     Label lblMessage;

    public LoginView() {
        super();
        layout = createLayout(LAYOUT_SPACING, LAYOUT_PADDING);

        layout.setAlignment(Pos.BOTTOM_CENTER);

        ObservableList<Node> list = layout.getChildren();
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10, 50, 50, 50));

        //Adding HBox
        HBox hb = new HBox();
        hb.setPadding(new Insets(20, 20, 20, 30));

        //Adding GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        //Implementing Nodes for GridPane
        lblIp = new Label("IP of Server");
        txtIp = new TextField();
        lblPort = new Label("Port:");
        txtPort = new TextField();
        btnConnect = new Button("Connect");
        lblMessage = new Label();

        //Adding Nodes to GridPane layout
        gridPane.add(lblIp, 0, 0);
        gridPane.add(txtIp, 1, 0);
        gridPane.add(lblPort, 0, 1);
        gridPane.add(txtPort, 1, 1);
        gridPane.add(btnConnect, 2, 1);
        gridPane.add(lblMessage, 1, 2);


        //Reflection for gridPane
        setReflection(gridPane);


        //Adding text and DropShadow effect to it
        Text text = new Text("Quests of The Round Table");
        text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        text.setEffect(dropShadowEffect());

        //Adding text to HBox
        hb.getChildren().add(text);

        //Add ID's to Nodes
        bp.setId("bp");
        gridPane.setId("root");
        btnConnect.setId("btnConnect");
        text.setId("text");
        lblIp.setId("ip");
        lblPort.setId("port");
        bp.setTop(hb);
        bp.setCenter(gridPane);

        layout.getStylesheets().add("/css/login.css");

        list.add(bp);
    }

    public VBox getLayout() {
        return layout;
    }

    public String getIp() {
        return txtIp.getText();
    }

    public int getPort() {
        int port = 0;
        try {
        	if(!txtPort.getText().equals(""))
        		port = Integer.parseInt(txtPort.getText());
            return port;
        }catch (NumberFormatException e){
            e.printStackTrace();
            lblMessage.setText("Numbers Only!");
            lblMessage.setTextFill(Color.RED);
        }
        //does code reach here?
        return port;
    }

    public Label getLblMessage() {
        return lblMessage;
    }

    private void setReflection(GridPane gridPane) {
        Reflection r = new Reflection();
        r.setFraction(0.7f);
        gridPane.setEffect(r);
    }


    private Effect dropShadowEffect() {
        //DropShadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);
        return dropShadow;
    }

    public void addBtnLoginAction(EventHandler<ActionEvent> event) {
        btnConnect.setOnAction(event);
    }


}
