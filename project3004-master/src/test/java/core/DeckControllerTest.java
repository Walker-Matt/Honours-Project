package core;

import static org.junit.Assert.assertThat;

import com.softwareeng.project.server.Controllers.DeckController;
import com.softwareeng.project.server.Models.AllyCardModel;
import com.softwareeng.project.server.Models.BattleCardModel;
import com.softwareeng.project.server.Models.GameModel;

import junit.framework.TestCase;

public class DeckControllerTest extends TestCase {
	
	public void testdrawFromDeck() {
		GameModel gameModel = new GameModel();
		DeckController deckCon = new DeckController();
		
		AllyCardModel sir = new AllyCardModel ("Sir Gawain", 10, 0);
		AllyCardModel king = new AllyCardModel("King Pellinore", 10, 0);
		deckCon.addCard(gameModel.adventureDeck,sir, 1);
		deckCon.addCard(gameModel.adventureDeck, king, 1);
		
		//deck pulls from top correctly
		assertEquals(king,deckCon.drawFromDeck(gameModel.adventureDeck));
		assertEquals(sir,deckCon.drawFromDeck(gameModel.adventureDeck));
	}
	
	//May need more tests, but good for now
	//****Ran multiple times to make sure it eventually works, meaning the shuffle is working******
	public void testShuffleandAddCard() {
		GameModel gameModel = new GameModel();
		DeckController deckCon = new DeckController();
		
		//AdventureDeckModel origDeck = new AdventureDeckModel();
		
		BattleCardModel horse = new BattleCardModel("Horse", 10);
		BattleCardModel dagger =new BattleCardModel("Dagger", 5);
		BattleCardModel excalibur= new BattleCardModel("Excalibur", 30);
		BattleCardModel lance = new BattleCardModel("Lance", 20);
		BattleCardModel battle = new BattleCardModel("Battle-ax", 15);
		
		//Make cards for test
		deckCon.addCard(gameModel.adventureDeck, horse, 1);
		deckCon.addCard(gameModel.adventureDeck, dagger, 1);
		deckCon.addCard(gameModel.adventureDeck, excalibur, 1);
		deckCon.addCard(gameModel.adventureDeck, lance, 1);
		deckCon.addCard(gameModel.adventureDeck, battle, 1);
		
		//testAddCard
		assertEquals(true,gameModel.adventureDeck.cards.size() == 5);
		
		deckCon.shuffle(gameModel.adventureDeck);
		
		assertEquals(false,gameModel.adventureDeck.cards.get(0) == horse);
		assertEquals(false,gameModel.adventureDeck.cards.get(1) == dagger);
		assertEquals(false,gameModel.adventureDeck.cards.get(4) == battle);
		
	}
	
	
}
