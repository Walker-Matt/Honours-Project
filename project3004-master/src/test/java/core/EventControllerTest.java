package core;

import java.util.ArrayList;

import com.softwareeng.project.server.Controllers.DeckController;
import com.softwareeng.project.server.Controllers.EventController;
import com.softwareeng.project.server.Models.AdventureDeckModel;
import com.softwareeng.project.server.Models.AllyCardModel;
import com.softwareeng.project.server.Models.BattleCardModel;
import com.softwareeng.project.server.Models.EventCardModel;
import com.softwareeng.project.server.Models.PlayerModel;

import junit.framework.TestCase;

public class EventControllerTest extends TestCase {
	
	//Needed for every test case
	MockUIController ui = new MockUIController();
//doesn't match current constructor for EventController
	//	EventController ec = new EventController(ui);
	DeckController dc = new DeckController();
	
	
	public void testlowestNumbShields() {
		//Players for test
		ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
		
		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");
		PlayerModel p3 = new PlayerModel(3,"Siraj");
		PlayerModel p4 = new PlayerModel(4,"Abhinav");
		
		p1.shields = 100;
		p2.shields = 75;
		p3.shields = 50;
		p4.shields = 25;
		
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

		//ec commented out from above conflict so the following line also has to be commented out
//		assertEquals(25,ec.lowestNumShields(players));
		
		p1.shields = 100;
		p2.shields = 75;
		p3.shields = 22;
		p4.shields = 50;

//		ec commented out from above conflict so the following line also has to be commented out
//		assertEquals(22,ec.lowestNumShields(players));
		
		p1.shields = 100;
		p2.shields = 0;
		p3.shields = 0;
		p4.shields = 0;
		//ec commented out from above conflict so the following line also has to be commented out
//		assertEquals(0,ec.lowestNumShields(players));
		
	}
	
	public void testMaxRankMinRank() {
		
		ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
		
		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");
		PlayerModel p3 = new PlayerModel(3,"Siraj");
		PlayerModel p4 = new PlayerModel(4,"Abhinav");
		
		p1.rank.battlePoints = 20;
		p2.rank.battlePoints = 10;
		p3.rank.battlePoints = 5;
		p4.rank.battlePoints = 5;
		
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);

//		ec commented out from above conflict so the following line also has to be commented out
//		assertEquals(20,ec.maxRank(players));
//		assertEquals(5,ec.minRank(players));
		
	}
	public void testChivalrousDeed() {

		ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
		
		
		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");
		PlayerModel p3 = new PlayerModel(3,"Siraj");
		PlayerModel p4 = new PlayerModel(4,"Abhinav");
		
		p1.rank.battlePoints = 20;
		p2.rank.battlePoints = 10;
		p3.rank.battlePoints = 5;
		p4.rank.battlePoints = 5;
		
		
		p1.shields = 15;
		p2.shields = 15;
		p3.shields = 2;
		p4.shields = 1;
		
		
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		
		
		EventCardModel em = new EventCardModel("Chivalrous Deed");
		AdventureDeckModel advDeck = new AdventureDeckModel();
		AdventureDeckModel disDeck = new AdventureDeckModel();
		boolean kingrec = false;



//		ec commented out from above conflict so the following line also has to be commented out
//		ec.startEvent(players,p1,em,disDeck,advDeck);
		
		assertEquals(15,p1.shields);
		assertEquals(15,p2.shields);
		assertEquals(2,p3.shields);
		assertEquals(4,p4.shields);
	}
	public void testPlague() {
		ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
		
		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");
		PlayerModel p3 = new PlayerModel(3,"Siraj");
		PlayerModel p4 = new PlayerModel(4,"Abhinav");
		
		p1.rank.battlePoints = 20;
		p2.rank.battlePoints = 10;
		p3.rank.battlePoints = 5;
		p4.rank.battlePoints = 5;
		
		
		p1.shields = 15;
		p2.shields = 15;
		p3.shields = 2;
		p4.shields = 1;
		
		
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		
		
		EventCardModel em = new EventCardModel("Plague");
		AdventureDeckModel advDeck = new AdventureDeckModel();
		AdventureDeckModel disDeck = new AdventureDeckModel();
		boolean kingrec = false;
		
		
		
		//Normal situation
		//ec commented out from above conflict so the following line also has to be commented out
//		ec.startEvent(players,p2,em,disDeck,advDeck);
		
		assertEquals(15,p1.shields);
		assertEquals(13,p2.shields);
		assertEquals(2,p3.shields);
		assertEquals(1,p4.shields);
		
		
		//If the drawer doesn't have enough cards
		
		//Resetting shields
		p1.shields = 15;
		p2.shields = 15;
		p3.shields = 2;
		p4.shields = 1;

		//ec commented out from above conflict so the following line also has to be commented out
//		ec.startEvent(players,p4,em,disDeck,advDeck);
		
		assertEquals(15,p1.shields);
		assertEquals(15,p2.shields);
		assertEquals(2,p3.shields);
		assertEquals(0,p4.shields);
		
		//If the drawer has 0 cards
		
		//Resetting shields
		p1.shields = 15;
		p2.shields = 15;
		p3.shields = 2;
		p4.shields = 0;

//		ec commented out from above conflict so the following line also has to be commented out
//		ec.startEvent(players,p4,em,disDeck,advDeck);
				
		assertEquals(15,p1.shields);
		assertEquals(15,p2.shields);
		assertEquals(2,p3.shields);
		assertEquals(0,p4.shields);
		
	}
	public void testPox() {
		ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
		
		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");
		PlayerModel p3 = new PlayerModel(3,"Siraj");
		PlayerModel p4 = new PlayerModel(4,"Abhinav");
		
		p1.rank.battlePoints = 20;
		p2.rank.battlePoints = 10;
		p3.rank.battlePoints = 5;
		p4.rank.battlePoints = 5;
		
		
		p1.shields = 15;
		p2.shields = 15;
		p3.shields = 2;
		p4.shields = 1;
		
		
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		
		
		EventCardModel em = new EventCardModel("Pox");
		AdventureDeckModel advDeck = new AdventureDeckModel();
		AdventureDeckModel disDeck = new AdventureDeckModel();
		boolean kingrec = false;
		
		
		//Regular situation
//		ec commented out from above conflict so the following line also has to be commented out
//		ec.startEvent(players, p1, em, disDeck, advDeck);
		
		assertEquals(15,p1.shields);
		assertEquals(14,p2.shields);
		assertEquals(1,p3.shields);
		assertEquals(0,p4.shields);
		
		//Edge case when 1 player doesn't have 1 sheild
		p1.shields = 15;
		p2.shields = 15;
		p3.shields = 2;
		p4.shields = 0;

//		ec commented out from above conflict so the following line also has to be commented out
//		ec.startEvent(players, p1, em, disDeck, advDeck);
		
		assertEquals(15,p1.shields);
		assertEquals(14,p2.shields);
		assertEquals(1,p3.shields);
		assertEquals(0,p4.shields);
		
	}
	public void testCourtCalledtoCamelot() {
		
		ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
		
		AllyCardModel a1 = new AllyCardModel("Sir Gawain", 10, 0);
		BattleCardModel b1 = new BattleCardModel("Horse", 10);
		
		
		
		AllyCardModel a2 = new AllyCardModel("King Pellinore", 10, 0);
		BattleCardModel b2 = new BattleCardModel("Horse", 10);
		BattleCardModel b3 = new BattleCardModel("Horse", 10);
		
		
		
		//Testing using 2 players
		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");
		
		p1.hand.selectedCards.add(a1);
		p1.hand.selectedCards.add(b1);
		
		p2.hand.selectedCards.add(a2);
		p2.hand.selectedCards.add(b2);
		p2.hand.selectedCards.add(b3);
		
		players.add(p1);
		players.add(p2);
		
		EventCardModel em = new EventCardModel("Court Called to Camelot");
		AdventureDeckModel advDeck = new AdventureDeckModel();
		AdventureDeckModel disDeck = new AdventureDeckModel();
		boolean kingrec = false;
		
//		ec commented out from above conflict so the following line also has to be commented out
//		ec.startEvent(players, p1, em, disDeck, advDeck);
		
		assertEquals(1,p1.hand.selectedCards.size());
		assertEquals(2,p2.hand.selectedCards.size());
	}
	public void testQueensFavor() {
		ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
		
		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");
		PlayerModel p3 = new PlayerModel(3,"Siraj");
		PlayerModel p4 = new PlayerModel(4,"Abhinav");
		
		p1.rank.battlePoints = 20;
		p2.rank.battlePoints = 10;
		p3.rank.battlePoints = 5;
		p4.rank.battlePoints = 5;
		
		
		p1.shields = 15;
		p2.shields = 15;
		p3.shields = 2;
		p4.shields = 1;
		
		
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		
		AllyCardModel a1 = new AllyCardModel("Sir Gawain", 10, 0);
		BattleCardModel b1 = new BattleCardModel("Horse", 10);
		AllyCardModel a2 = new AllyCardModel("King Pellinore", 10, 0);
		BattleCardModel b2 = new BattleCardModel("Horse", 10);
		BattleCardModel b3 = new BattleCardModel("Horse", 10);
		
		
		EventCardModel em = new EventCardModel("Queen's Favor");
		
		AdventureDeckModel advDeck = new AdventureDeckModel();
		
		dc.addCard(advDeck, a2, 1);
		dc.addCard(advDeck, a1, 1);
		dc.addCard(advDeck, b1, 1);
		dc.addCard(advDeck, b2, 1);
		dc.addCard(advDeck, b3, 1);
		
		AdventureDeckModel disDeck = new AdventureDeckModel();
		boolean kingrec = false;

//		ec commented out from above conflict so the following line also has to be commented out
//		ec.startEvent(players, p1, em, disDeck, advDeck);
		
		assertEquals(2,p3.hand.playerHand.size());
		assertEquals(2,p4.hand.playerHand.size());
	}
	
	//The code may need to be redone when the prompt player is working
	public void testKingsCalltoArms() {
		/*
		//Player setup
		ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
		
		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");
		PlayerModel p3 = new PlayerModel(3,"Siraj");
		PlayerModel p4 = new PlayerModel(4,"Abhinav");
		
		p1.rank.battlePoints = 20;
		p2.rank.battlePoints = 10;
		p3.rank.battlePoints = 5;
		p4.rank.battlePoints = 5;
		
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		
		EventCardModel em = new EventCardModel("Queen's Favor");
		
		AdventureDeckModel advDeck = new AdventureDeckModel();
		
		FoeCardModel fc1 = new FoeCardModel("Robber Knight", 15);
		FoeCardModel fc2 = new FoeCardModel("Saxons", 10, 20);
		
		
		
		AdventureDeckModel disDeck = new AdventureDeckModel();
		boolean kingrec = false;
		
		*/
	}
}
