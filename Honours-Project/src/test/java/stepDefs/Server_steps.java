package stepDefs;

import cucumber.api.java.Before;
import cucumber.api.java.en.*;
import helpers.Bind;
import helpers.Bind.NullInstanceException;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import com.softwareeng.project.server.ClientHandler;
import com.softwareeng.project.server.Server;
import com.softwareeng.project.server.Models.*;

public class Server_steps {
	static Server server;
	Bind bind;
	
	@Before
	public void setup() {
		if(server != null) {
			bind = new Bind(Server_steps.server);
		}
	}
	
	@Given("the server is created")
	public void the_server_is_created() throws IOException {
		Server_steps.server = new Server();
	}

	@When("the server is started")
	public void the_server_is_started() {
		Thread thread = new Thread() {
			public void run() {
				try {
					Server_steps.server.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	@Then("the server is running")
	public void the_server_is_running() throws InterruptedException, NullInstanceException {
		Thread.sleep(5000); //wait for game to initialize
		bind = new Bind(Server_steps.server);
		GameModel game = bind.to(GameModel.class);
	    assertNotNull(game);
	}
	
	@Given("player {int} has joined")
	public void player_has_joined(Integer int1) throws NullInstanceException {
		PlayerModel player = bind.to(PlayerModel.class, int1);
		assertNotNull(player);
	}

	@When("the cards have been dealt")
	public void the_cards_have_been_dealt() throws NullInstanceException {
		AdventureDeckModel adventureDeck = bind.to(AdventureDeckModel.class);
		assertEquals(109, adventureDeck.getCards().size());
	}

	@Then("player {int} has {int} cards")
	public void player_has_cards(Integer int1, Integer int2) throws NullInstanceException {
		PlayerModel player = bind.to(PlayerModel.class, int1);
	    assertEquals(player.hand.playerHand.size(), int2.intValue());
	}

	@Given("it is player {int} turn")
	public void it_is_player_turn(Integer int1) throws NullInstanceException, InterruptedException {
		PlayerModel player = bind.to(PlayerModel.class, int1);
		GameModel game = bind.to(GameModel.class);
		while(true) {
			if(player.equals(game.currentPlayer)) {
				break;
			} else {
				Thread.sleep(1000);
			}
		}
	}

	@When("they draw from the story deck")
	public void they_draw_from_the_story_deck() throws NullInstanceException, InterruptedException {
		StoryDeckModel storyDeck = bind.to(StoryDeckModel.class);
		int currentSize = storyDeck.getCards().size();
		while(storyDeck.getCards().size() == currentSize) {
			Thread.sleep(1000);
		}
	}
	
	@Then("the story card {string} is drawn")
	public void the_story_card_is_drawn(String string) throws NullInstanceException {
		CardModel storyCard = bind.to(CardModel.class);
		assertEquals(string, storyCard.cardName);
	}
	
	@Given("player {int} has {string}")
	public void player_has(Integer int1, String string) throws NullInstanceException {
		PlayerModel player = bind.to(PlayerModel.class, int1);
		CardModel card = bind.to(CardModel.class, int1, string);
		System.out.print("\n" + card.cardName + "\n");
		assertTrue(player.hand.playerHand.contains(card));
	}

	@When("player {int} plays that card")
	public void player_plays_that_card(Integer int1) throws NullInstanceException, InterruptedException {
	    PlayerModel player = bind.to(PlayerModel.class, int1);
	    int currentSize = player.hand.playerHand.size();
	    while(player.hand.playerHand.size() == currentSize) {
	    	Thread.sleep(1000);
	    }
	}

	@Then("player {int} has {int} cards left")
	public void player_has_cards_left(Integer int1, Integer int2) throws NullInstanceException {
		PlayerModel player = bind.to(PlayerModel.class, int1);
		assertEquals(player.hand.playerHand.size(), int2.intValue());
	}
	
	@Given("player {int} has rank {string}")
	public void player_has_rank(Integer int1, String string) {
	    
	}

	@When("they gain more shields")
	public void they_gain_more_shields() {
	    
	}

	@Then("they gain the rank {string}")
	public void they_gain_the_rank(String string) {
	    
	}

	@Given("clients exist")
	public void clients_exist() throws NullInstanceException {
		ClientHandler client1 = bind.to(ClientHandler.class, 1);
		ClientHandler client2 = bind.to(ClientHandler.class, 2);
		assertNotNull(client1);
		assertNotNull(client2);
	}

	@When("server waits for {int} minutes and {int} seconds")
	public void server_waits_from_minutes_and_seconds(Integer int1, Integer int2) throws InterruptedException, NullInstanceException {
		Thread.sleep(int1*60*1000 + int2*1000);
//		int seconds = int1*60 + int2;
//		PlayerModel player2= bind.to(PlayerModel.class, 2);
//		while(seconds > 0) {
//			System.out.print("Player 2 currently has " + player2.hand.playerHand.size() + " cards.\n");
//			Thread.sleep(1000);
//			seconds--;
//		}
	}

	@Then("the server can shutdown")
	public void the_server_can_shutdown() {
	    //Server testing and server thread shuts down after this point
		//either by the test Scenarios finishing, or the game finishing
	}
}
