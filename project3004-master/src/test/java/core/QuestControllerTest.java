package core;

import java.util.ArrayList;

import com.softwareeng.project.server.Controllers.QuestController;
import com.softwareeng.project.server.Models.AdventureDeckModel;
import com.softwareeng.project.server.Models.AllyCardModel;
import com.softwareeng.project.server.Models.BattleCardModel;
import com.softwareeng.project.server.Models.PlayerModel;
import com.softwareeng.project.server.Models.QuestCardModel;

import junit.framework.TestCase;

public class QuestControllerTest extends TestCase {

	MockUIController ui = new MockUIController();
	
//	QuestController qc = new QuestController(ui);
	public void testdiscardAmour() {
		AdventureDeckModel disDeck = new AdventureDeckModel();
		PlayerModel player1 = new PlayerModel(1,"Mo");
		
		AllyCardModel amour1 = new AllyCardModel("Amour", 10, 1);
		BattleCardModel b2 = new BattleCardModel("Horse", 10);
		
		player1.hand.selectedCards.add(amour1);
		player1.hand.selectedCards.add(b2);
		
//		qc.discardAmour(player1, disDeck);
		assertEquals(1,player1.hand.selectedCards.size());
		
	}
	public void testNumActivePlayers() {
		ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");
		PlayerModel p3 = new PlayerModel(3,"Siraj");
		PlayerModel p4 = new PlayerModel(4,"Abhinav");
		
		p1.isActive = true;
		p2.isActive = true;
		p3.isActive = true;
		p4.isActive = true;
		
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		
//		assertEquals(4,qc.numberOfActivePlayers(players));
		
	}
	public void testGetPlayerStageBids() {
		AllyCardModel arthur = new AllyCardModel("King Arthur", 10, 2);
		QuestCardModel quest = new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight");
		PlayerModel p1 = new PlayerModel(1,"Mo");
		p1.hand.selectedCards.add(arthur);

		//ask Mo to fix this
//		assertEquals(2,qc.getPlayerStageBids(quest.cardName, p1));
	}
	public void testgetPlayerStageBattlePoints() {
		ArrayList<PlayerModel> players = new ArrayList<PlayerModel>();
		PlayerModel p1 = new PlayerModel(1,"Mo");
		PlayerModel p2 = new PlayerModel(2,"Ahmad");
		PlayerModel p3 = new PlayerModel(3,"Siraj");
		PlayerModel p4 = new PlayerModel(4,"Abhinav");
		
		QuestCardModel quest = new QuestCardModel("Journey through the Enchanted Forest", 3, "Evil Knight");
		BattleCardModel b2 = new BattleCardModel("Horse", 10);
		
		p1.hand.selectedCards.add(b2);
		p1.rank.battlePoints = 20;

		//ask Mo to fix this
//		int battlepoints=qc.getPlayerStageBattlePoints(quest,p1);
		
//		assertEquals(30,battlepoints);
		
		
		
		
	}
}
