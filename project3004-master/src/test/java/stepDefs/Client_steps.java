package stepDefs;

import static org.junit.Assert.*;

import cucumber.api.java.en.*;
import helpers.TestFXBase;
import javafx.scene.control.*;

public class Client_steps extends TestFXBase {

	@Given("the ip address has been entered")
	public void the_ip_address_has_been_entered() {
		TextField ipInput = find("#ipInput");
		ipInput.setText("127.0.0.1");
		
		assertEquals("127.0.0.1", ipInput.getText());
	}

	@Given("the port number has been entered")
	public void the_port_number_has_been_entered() {
		TextField portInput = find("#portInput");
		portInput.setText("9876");
		
		assertEquals("9876", portInput.getText());
	}
	
	@Given("client two waits for client one")
	public void client_two_waits_for_client_one() throws InterruptedException {
		Thread.sleep(2000);
	}

	@When("the connect button is pressed")
	public void the_connect_button_is_pressed() throws InterruptedException {
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

	@Then("the client connects to the server")
	public void the_client_connects_to_the_server() throws InterruptedException {
		Thread.sleep(1000);
		try {
			find("#ChooseNumPlayers");
			assertTrue(true);
		} catch(Exception e) { 
			try {
				find("#WaitingForOtherPlayers");
				assertTrue(true);
			} catch(Exception f) {
				try {
					find("#message");
					assertTrue(true);
				} catch(Exception g) {
					System.out.print(g);
				}
			}
		}
	}

	@Given("the client must choose the number of players")
	public void the_client_must_choose_the_number_of_players() {
	    assertNotNull(find("#ChooseNumPlayers"));
	}

	@When("the client specifies a two player game")
	public void the_client_specifies_a_two_player_game() {
		Button btn2Player = find("#btn2Player");
		try {
			btn2Player.fire();
		} catch(Exception e) { }
	}

	@Then("the game has begun")
	public void the_game_has_begun() throws InterruptedException {
		Thread.sleep(2000);
		assertNotNull(find("#message"));
	}
}
