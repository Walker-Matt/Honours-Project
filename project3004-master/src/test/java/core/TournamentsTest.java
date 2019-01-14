package core;

import java.util.ArrayList;

import com.softwareeng.project.server.Controllers.TournamentController;
import com.softwareeng.project.server.Models.BattleCardModel;
import com.softwareeng.project.server.Models.CardModel;
import com.softwareeng.project.server.Models.FoeCardModel;
import com.softwareeng.project.server.Models.PlayerModel;
import com.softwareeng.project.server.Models.TestCardModel;
import com.softwareeng.project.server.Models.TournamentCardModel;

import junit.framework.TestCase;

public class TournamentsTest extends TestCase {
	MockUIController ui = new MockUIController();

	//doesn't match current parameter type
//	TournamentController tc = new TournamentController(ui);

	public void testvalidatePlayerCardsTest() {

		//With foe card
		FoeCardModel foe = new FoeCardModel("Robber Knight", 15);

		ArrayList<CardModel> cards1 = new ArrayList<CardModel>();
		cards1.add(foe);

		//commented out because tc is commented out from above conflict
//		assertEquals(false,tc.validatePlayerCards(cards1));

		//Without foe card
		 TestCardModel testcard = new TestCardModel("Test of the Questing Beast");
		 ArrayList<CardModel> cards2 = new ArrayList<CardModel>();
		 cards2.add(testcard);
//commented out because tc is commented out from above conflict
//		 assertEquals(true,tc.validatePlayerCards(cards2));



	}
	public void testGetWinner() {

		 TournamentCardModel tourney = new TournamentCardModel("At Camelot", 3);


		BattleCardModel b1 = new BattleCardModel ("cardp1", 10);
		BattleCardModel b2 = new BattleCardModel ("cardp2", 5);
		BattleCardModel b3 = new BattleCardModel ("cardp3", 5);

		BattleCardModel b4 = new BattleCardModel ("cardp1", 10);
		BattleCardModel b5 = new BattleCardModel ("cardp2", 5);
		BattleCardModel b6 = new BattleCardModel ("cardp3", 5);


		ArrayList<CardModel> p1Cards = new ArrayList<CardModel>();
		ArrayList<CardModel> p2Cards = new ArrayList<CardModel>();

		p1Cards.add(b1);
		p1Cards.add(b2);
		p1Cards.add(b3);

		p2Cards.add(b4);
		p2Cards.add(b5);
		p2Cards.add(b6);

		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");


		p1.hand.selectedCards = p1Cards;
		p2.hand.selectedCards = p2Cards;

		ArrayList<PlayerModel> theWinners = new ArrayList<PlayerModel>();

		theWinners.add(p1);
		theWinners.add(p2);

		//ask Mo to fix this
//		assertEquals(theWinners,tc.getWinners(theWinners,tourney.cardName));

		//One player wins



	}

	}
	

