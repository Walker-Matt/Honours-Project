package core;

import com.softwareeng.project.server.Models.AIPlayerModel;
import com.softwareeng.project.server.Models.FoeCardModel;

import junit.framework.TestCase;

//Not Done
public class AIStrategy1Test extends TestCase {
	public void testgetCardstoPlayForTournament() {
		
	}
	public void testDoISponsorAQuest () {
		//Scenario 1
		AIPlayerModel aiPlayer1 = new AIPlayerModel(1,"Mo",1);
				
				
		FoeCardModel fc1 = new FoeCardModel("Robber Knight", 15);
		FoeCardModel fc2 = new FoeCardModel("Dragon", 50, 70);
				
		aiPlayer1.hand.playerHand.add((FoeCardModel)fc1);
		aiPlayer1.hand.playerHand.add((FoeCardModel)fc2);

		//function calls don't match recent variations
		//Actual value
//		boolean result = aiPlayer1.doISponsorAQuest();
		//The foe cards scenario
//		assertEquals(true,result);
	}

	public void testgetCardsToPlayForQuest() {
		
	}
	
	public void testnextBid() {
		
	}

	public void testdiscardAfterWinningTest() {
		
	}

	public void testfoesLessThan25 () {

	}
}
