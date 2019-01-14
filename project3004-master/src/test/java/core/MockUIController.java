package core;

import java.util.ArrayList;

import com.softwareeng.project.client.App;
import com.softwareeng.project.client.UIController;
import com.softwareeng.project.server.Models.CardModel;
import com.softwareeng.project.server.Models.PlayerModel;
import com.softwareeng.project.client.views.GameView;
import com.softwareeng.project.client.views.MainMenuView;
import com.softwareeng.project.client.views.PlayerSelectionView;

public class MockUIController extends UIController {

	    public MockUIController() {
	    	super(true);
	    }


	    public void initMainMenuView(MainMenuView v, App a) {

	    }

	    public void initPlayerSelectionView(PlayerSelectionView v, App a) {

	    }

	    public void initGameView(GameView v, int numOfPlayers) {

	    }

	    public void addCardToPlayer(int playerId, int currentPlayerId, int cardId, String imgUrl) {

	    }

	    public void removeCardFromPlayer(int playerId, int cardId) {

	    }

	    public void removeSelectedCardFromPlayer(int playerId, int cardId) {

	    }

	    public void addCardToPlayArea(int playerId, int cardId, String imgUrl) {

	    }

	    public void removeCardFromPlayArea(int playerId, int cardId) {

	    }

	    public void clearPlayArea(int playerId) {

	    }

	    public void flipStoryDeck(CardModel currentCard) {

	    }

	    public void removeStoryDeckCard() {

	    }

	    public void changePlayerRank(int playerId, int newCardId, int oldCardId, String newRankUrl) {
	    
	    }

	    public void setShields(int playerId, int number) {

	    }

	    public void addShields(int playerId, int number) {

	    }

	    public void removeShields(int playerId, int number) {

	    }

	    
	    public boolean waitUntilCardIsClicked() throws InterruptedException {
	    	return true;
	    }

	    public boolean showEndTurnAndWait(int id) throws InterruptedException {
	    	return true;
	    }

	    public void setMessageBoxText(String message) {

	    }

	    public boolean prompt(int playerId) throws InterruptedException {
	    	return true;
	    }

	    public synchronized void setPlayerHandSelect(int playerId, boolean onOrOff) {

	    }

	    public ArrayList<Integer> promptPlayCards(int playerId) throws InterruptedException {
	    	return new ArrayList<>();
	    }

	    public ArrayList<Integer> promptDiscardCards(int playerId) throws InterruptedException {
	    	return new ArrayList<>();
	    }

	    public int promptBidCards(int playerId) throws InterruptedException {
	    	return 0;
	    }

	    public void readyForTurn(int id) throws InterruptedException {


	    }
	    
	    public void initGameViewWithCardsAndShields(ArrayList<PlayerModel> players, int currentPlayerId) {

	    	
	    }


	   
}
