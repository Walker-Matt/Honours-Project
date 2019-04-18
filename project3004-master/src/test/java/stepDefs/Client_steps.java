package stepDefs;

import static org.junit.Assert.*;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.testfx.api.FxToolkit;

import com.softwareeng.project.server.Server;

import javafx.event.Event;
import javafx.event.EventHandler;
import cucumber.api.java.Before;
import cucumber.api.java.en.*;
import helpers.Bind;
import helpers.TestFXBase;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseButton;
import javafx.scene.image.ImageView;

public class Client_steps extends TestFXBase {
	
	//helper methods
	
	public MouseEvent click() {
	    return new MouseEvent(MouseEvent.MOUSE_CLICKED,
				   0, 0, 0, 0, MouseButton.PRIMARY, 1,
				   true, true, true, true, true, true, true, true, true, true, null);
	}
	
	public boolean pressButton(String name) {
		try {
			HBox bottomPlayerPlayArea = find("#bottomPlayerPlayArea");
			ObservableList<Node> bottomPlayArea = bottomPlayerPlayArea.getChildren();
			for(Node node : bottomPlayArea) {
				Group group = (Group)node;
				Button button = (Button)group.getChildren().get(0);
				if(button.getText().equals(name)) {
					button.fire();
					return true;
				}
			}
		} catch(Exception e) { }
		try {
			VBox leftPlayerPlayArea = find("#leftPlayerPlayArea");
			ObservableList<Node> leftPlayArea = leftPlayerPlayArea.getChildren();
			for(Node node : leftPlayArea) {
				Group group = (Group)node;
				Button button = (Button)group.getChildren().get(0);
				if(button.getText().equals(name)) {
					button.fire();
					return true;
				}
			}
		} catch(Exception e) { }
		return false;
	}
	
	public boolean selectCard(int player, String card) {
		if(player == 1) {
			try {
				HBox bottomPlayerHand = find("#bottomPlayerHand");
				ObservableList<Node> bottomHand = bottomPlayerHand.getChildren();
				for(Node c : bottomHand) {
					String cardName = c.getId().replace("_", " ").replaceAll("[0-9]", "");
					cardName = cardName.substring(0, cardName.length()-4);
					if(cardName.equals(card)) {
						c.fireEvent(click());
						return true;
					}
				}
			} catch(Exception e) { }
		} else if(player == 2) {
			try {
				VBox leftPlayerHand = find("#leftPlayerHand");
				ObservableList<Node> leftHand = leftPlayerHand.getChildren();
				for(Node c : leftHand) {
					String cardName = c.getId().replace("_", " ").replaceAll("[0-9]", "");
					cardName = cardName.substring(0, cardName.length()-4);
					if(cardName.equals(card)) {
						c.fireEvent(click());
						return true;
					}
				}
			} catch(Exception e) { }
		}
		return false;
	}
	
	public boolean selectCard(int player, int index) {
		if(player == 1) {
			try {
				HBox bottomPlayerHand = find("#bottomPlayerHand");
				ObservableList<Node> bottomHand = bottomPlayerHand.getChildren();
				bottomHand.get(index-1).fireEvent(click());
				return true;
			} catch(Exception e) { }
		} else if(player == 2) {
			try {
				VBox leftPlayerHand = find("#leftPlayerHand");
				ObservableList<Node> leftHand = leftPlayerHand.getChildren();
				leftHand.get(index-1).fireEvent(click());
				return true;
			} catch(Exception e) { }
		}
		return false;
	}
	
	//Shared steps
	
	@Given("I click on the connect button")
	public void i_click_on_the_connect_button() {
		Button connect = find("#btnConnect");
		Boolean connected = false;
		int failed = 0;
		while(!connected) {
			try {
				connect.fire();
				connected = true;
			} catch (Exception e) {
				connected = false;
				failed++;
				String fail = "Failed connection attempts: " + failed;
				System.out.print(fail);
			}
		}
	}
	
	@Then("I connect to the game")
	public void i_connect_to_the_game() throws InterruptedException {
		Thread.sleep(1000);
		try {
			find("#ChooseNumPlayers");
		} catch(Exception e) { 
			try {
				find("#WaitingForOtherPlayers");
			} catch(Exception f) {
				try {
					find("#message");
				} catch(Exception g) {
					System.out.print(g);
				}
			}
		}
	}
	
	@Then("the game starts")
	public void the_game_starts() throws InterruptedException {
		Thread.sleep(2000);
		assertNotNull(find("#message"));
	}
	
	@Given("I wait for my turn")
	public void i_wait_for_my_turn() throws InterruptedException {
		Label message = find("#message");
	    boolean myTurn = false;
	    while(!myTurn) {
	    	message = find("#message");
	    	String text = message.getText().toString();
	    	//Turn to draw a story card
	    	if(text.equals(" Click on the story deck to draw a card")) {
	    		myTurn = true;
	    	//Turn to participate in quest
	    	} else if(text.equals(" Would you like to participate in this quest?")) {
	    		myTurn = true;
	    	//Turn to sponsor a quest
	    	} else if(text.equals(" Would you like to sponsor this quest?")) {
	    		myTurn = true;
	    	//Turn to discard extra cards
	    	} else if(text.equals(" You have 1 cards too many")) {
	    		myTurn = true;
	    	}
	    	Thread.sleep(1000);
	    }
	}
	
	@When("I draw from the story deck")
	public void i_draw_from_the_story_deck() {
		HBox decksArea = find("#decksArea");
		decksArea.fireEvent(click());
	}
	
	@Then("the card {string} is drawn")
	public void the_card_is_drawn(String string) throws InterruptedException {
		Thread.sleep(1000);
		HBox decksArea = find("#decksArea");
		ObservableList<Node> deckStory = decksArea.getChildren();
		Node storyCard = deckStory.get(3);
		String cardName = storyCard.getId().replace("_", " ").replaceAll("[0-9]", "");
		cardName = cardName.substring(0, cardName.length()-4);
		assertEquals(string, cardName);
	}
	
	@Then("I end my turn")
	public void i_end_my_turn() throws InterruptedException {
		while(true) {
			if(!pressButton("End Turn")) {
				Thread.sleep(1000);
			} else {
				break;
			}
		}
	}
	
	@Then("I am asked if I want to sponsor the quest")
	public void i_am_asked_if_I_want_to_sponsor_the_quest() {
		Label message = find("#message");
		assertEquals(" Would you like to sponsor this quest?", message.getText().toString());
	}
	
	@When("I choose yes")
	public void i_choose_yes() throws InterruptedException {
	    pressButton("Yes");
	    Thread.sleep(1000);
	}
	
	@Then("I play nothing")
	public void i_play_nothing() {
	    pressButton("Play Cards");
	}
	
	@Then("I wait {int} seconds")
	public void i_wait_seconds(Integer int1) throws InterruptedException {
		Thread.sleep(int1*1000);
	}
	
	//Client One

	@Given("I have to choose the number of players")
	public void i_have_to_choose_the_number_of_players() {
		assertNotNull(find("#ChooseNumPlayers"));
	}

	@When("I choose a two player game")
	public void i_choose_a_two_player_game() {
		Button btn2Player = find("#btn2Player");
		try {
			btn2Player.fire();
		} catch(Exception e) { }
	}	

	@When("I am asked if I want to participate")
	public void i_am_asked_if_I_want_to_participate() {
		Label message = find("#message");
		assertEquals(" Would you like to participate in this quest?", message.getText().toString());
	}

	@Then("I am asked to play stage {int}")
	public void i_am_asked_to_play_stage(Integer int1) throws InterruptedException {
		Label message = find("#message");
		String compare = " Please select your cards to fight Stage " + int1.toString();
		assertEquals(compare, message.getText().toString());
	}
	
	@Then("I play the weapon {string}")
	public void i_play_the_weapon(String string) throws InterruptedException {
		selectCard(1, string);
	    pressButton("Play Cards");
	    Thread.sleep(8000);
	}

	@Then("I choose no")
	public void i_choose_no() throws InterruptedException {
		Thread.sleep(2000);
		pressButton("No");
		Thread.sleep(1000);
	}
	
	//Client Two
	
	@Given("I wait for player one to setup the game")
	public void i_wait_for_player_one_to_setup_the_game() throws InterruptedException {
	    Thread.sleep(1000);
	}

	@Then("I am asked to setup stage {int}")
	public void i_am_asked_to_setup_stage(Integer int1) throws InterruptedException {
		Thread.sleep(1000);
		Label message = find("#message");
		String compare = " Please select the cards for Stage " + int1.toString();
		assertEquals(compare, message.getText().toString());
	}
	
	@Then("I play the foe {string}")
	public void i_play_the_foe(String string) throws InterruptedException {
		selectCard(2, string);
	    pressButton("Play Cards");
	}

	@Then("I discard {int} extra cards")
	public void i_discard_extra_cards(Integer int1) throws InterruptedException {
		for(int i=0; i<int1; i++) {
			selectCard(2, i+4);
		}
		Thread.sleep(1000);
		pressButton("Discard");
		Thread.sleep(1000);
	}
}
