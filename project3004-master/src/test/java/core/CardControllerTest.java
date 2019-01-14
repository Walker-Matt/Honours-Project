package core;

import com.softwareeng.project.server.Controllers.CardController;
import com.softwareeng.project.server.Models.AllyCardModel;
import com.softwareeng.project.server.Models.BattleCardModel;
import com.softwareeng.project.server.Models.FoeCardModel;

import junit.framework.TestCase;

public class CardControllerTest extends TestCase {
	MockUIController mockUI = new MockUIController();
//doesn't match current signature for CardController constructor
	//	CardController cc = new CardController(mockUI);
	
	//Weapons are in good shape
	public void testgetBattlePointsWeapon() {
		//Weapon 1
		BattleCardModel bc = new BattleCardModel("Excalibur", 30);
//		assertEquals(30,cc.getBattlePoints("QuestCardName",bc));
		
		//Weapon 2
		BattleCardModel bc2 = new BattleCardModel("Horse", 10);
//		assertEquals(10,cc.getBattlePoints("QuestCardName",bc2));
	}
	
	//Something Wrong returning 0 instead of desired 30
	//Ally getting points does not work at the moment 
	public void testgetBattlePointsAlly() {
		//Ally
		AllyCardModel ac1 = new AllyCardModel("Sir Gawain", 10, 0);
//		assertEquals(30,cc.getBattlePoints("QuestCardName", ac1));
	}
	
	//Foes are in good shape
	public void testgetBattlePointsFoe() {
		//Foe 1
		//Name not in quest
		FoeCardModel fc1 = new FoeCardModel("Dragon", 50, 70);
//		assertEquals(50,cc.getBattlePoints("QuestCardName",fc1));
		//Name in quest
//		assertEquals(70,cc.getBattlePoints("Slay the Dragon",fc1));
		
		
		//Extra Test
		//Name not in quest
		FoeCardModel fc2 = new FoeCardModel("Green Knight", 25, 40);
//		assertEquals(25,cc.getBattlePoints("Slay the Dragon",fc2));
		//Name in quest
//		assertEquals(40,cc.getBattlePoints("Test of the Green Knight",fc2));
	}
}
