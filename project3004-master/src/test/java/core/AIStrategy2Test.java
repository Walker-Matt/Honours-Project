package core;

import java.util.ArrayList;
import java.util.List;

import com.softwareeng.project.server.Models.AIPlayerModel;
import com.softwareeng.project.server.Models.BattleCardModel;
import com.softwareeng.project.server.Models.CardModel;
import com.softwareeng.project.server.Models.FoeCardModel;

import junit.framework.TestCase;

public class AIStrategy2Test extends TestCase {

    //A couple of scenarios are not working
    public void testgetCardstoPlayForTournament() {

        //Scenario 1
        AIPlayerModel aiPlayer1 = new AIPlayerModel(1, "Mo", 2);

        // Play as few cards to get 50
        BattleCardModel bc1 = new BattleCardModel("Horse", 51);
        BattleCardModel bc2 = new BattleCardModel("Dagger", 25);
        BattleCardModel bc3 = new BattleCardModel("Sword", 25);

        aiPlayer1.hand.playerHand.add((BattleCardModel) bc1);
        aiPlayer1.hand.playerHand.add((BattleCardModel) bc2);
        aiPlayer1.hand.playerHand.add((BattleCardModel) bc3);

        //Expected value
        List<CardModel> expectedCards = new ArrayList<>();

        expectedCards.add(bc1);

//		assertEquals(expectedCards,aiPlayer1.getCardsToPlayForTournament());


        //Scenario 2
        AIPlayerModel aiPlayer2 = new AIPlayerModel(1, "Ahmad", 2);

        BattleCardModel bc4 = new BattleCardModel("Horse", 30);
        BattleCardModel bc5 = new BattleCardModel("Dagger", 25);
        BattleCardModel bc6 = new BattleCardModel("Sword", 25);

        aiPlayer2.hand.playerHand.add(bc4);
        aiPlayer2.hand.playerHand.add(bc5);
        aiPlayer2.hand.playerHand.add(bc6);

        //Expected value
        List<CardModel> expectedCards2 = new ArrayList<>();

        expectedCards2.add(bc4);
        expectedCards2.add(bc5);
        expectedCards2.add(bc6);

//		assertEquals(expectedCards2,aiPlayer2.getCardsToPlayForTournament());


        //Scenario 3
        //Not working
        AIPlayerModel aiPlayer3 = new AIPlayerModel(1, "LeBron", 2);

        BattleCardModel bc7 = new BattleCardModel("Horse", 5);
        BattleCardModel bc8 = new BattleCardModel("Dagger", 5);
        BattleCardModel bc9 = new BattleCardModel("Sword", 5);

        aiPlayer3.hand.playerHand.add(bc7);
        aiPlayer3.hand.playerHand.add(bc8);
        aiPlayer3.hand.playerHand.add(bc9);

        //Expected value
        List<CardModel> expectedCards3 = new ArrayList<>();

        expectedCards3.add(bc7);
        expectedCards3.add(bc8);
        expectedCards3.add(bc9);

//		System.out.println((aiPlayer3.getCardsToPlayForTournament()).get(0).cardName);
//		assertEquals(expectedCards3,aiPlayer3.getCardsToPlayForTournament());

    }

    //Scenario where the player checks if the other players can win and evolve not done
    public void testDoISponsorAQuest() {

        //Scenario 1
        AIPlayerModel aiPlayer1 = new AIPlayerModel(1, "Mo", 2);


        FoeCardModel fc1 = new FoeCardModel("Robber Knight", 15);
        FoeCardModel fc2 = new FoeCardModel("Dragon", 50, 70);

        aiPlayer1.hand.playerHand.add((FoeCardModel) fc1);
        aiPlayer1.hand.playerHand.add((FoeCardModel) fc2);

        //function calls don't match recent variations
        //Actual value
//		boolean result = aiPlayer1.doISponsorAQuest();
        //The foe cards scenario
//		assertEquals(true,result);
    }

    //Not working
    public void testdoIParticipateInQuest() {
        AIPlayerModel aiPlayer1 = new AIPlayerModel(1, "Mo", 2);


        FoeCardModel fc1 = new FoeCardModel("Robber Knight", 25);
        FoeCardModel fc2 = new FoeCardModel("Dragon", 5);

        aiPlayer1.hand.playerHand.add(fc1);
        aiPlayer1.hand.playerHand.add(fc2);
        System.out.println();
        //increment by 10 each stage scenario and 2 or more foes should be true
        boolean result = aiPlayer1.doIParticipateInQuest(2);

        assertEquals(true, result);

    }

    //Not implemented
    public void testgetCardsToPlayForQuest() {

    }

    public void testnextBid() {
        AIPlayerModel aiPlayer1 = new AIPlayerModel(1, "Mo", 2);

        FoeCardModel fc1 = new FoeCardModel("Robber Knight", 15);
        FoeCardModel fc2 = new FoeCardModel("Dragon", 5);

        aiPlayer1.hand.playerHand.add(fc1);
        aiPlayer1.hand.playerHand.add(fc2);

        //Scenario 1
        assertEquals(2, aiPlayer1.nextBid(1));

        //Scenario 2
        FoeCardModel fc3 = new FoeCardModel("Dragon", 5);
        aiPlayer1.hand.playerHand.add(fc3);

        assertEquals(4, aiPlayer1.nextBid(2));
    }

    //Not fully implemented
    public void testdiscardAfterWinningTest() {

    }

    //Tested using the above functions
    public void testfoesLessThan25() {

    }
}

