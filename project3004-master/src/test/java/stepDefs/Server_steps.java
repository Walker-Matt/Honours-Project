package stepDefs;

import cucumber.api.java.Before;
import cucumber.api.java.en.*;
import helpers.Bind;
import helpers.Bind.NullInstanceException;

import static org.junit.Assert.*;

import java.io.IOException;

import com.softwareeng.project.server.ClientHandler;
import com.softwareeng.project.server.Server;
import com.softwareeng.project.server.Controllers.GameController;
import com.softwareeng.project.server.Models.PlayerModel;

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
		GameController gameController = bind.to(GameController.class);
	    assertNotNull(gameController);
	}
	
	@Given("client {int} is connected")
	public void client_u_is_connected(Integer int1) throws NullInstanceException {
		ClientHandler client = bind.to(int1, ClientHandler.class);
	    assertNotNull(client);
	}

	@When("player {int} exists")
	public void player_u_exists(Integer int1) throws NullInstanceException {
		PlayerModel player = bind.to(int1, PlayerModel.class);
		assertNotNull(player);
	}

	@Then("player {int} is bound")
	public void player_u_is_bound(Integer int1) throws NullInstanceException {
		if(int1 == 1) {
			PlayerModel player1 = bind.to(int1, PlayerModel.class);
		} else {
			PlayerModel player2 = bind.to(int1, PlayerModel.class);
		}
	}
}
