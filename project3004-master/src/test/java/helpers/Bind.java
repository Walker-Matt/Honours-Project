package helpers;

import com.softwareeng.project.server.Server;
import com.softwareeng.project.server.Models.CardModel;

public class Bind {
	private static Server server;

	public Bind(Server server) {
		Bind.server = server;
	}
	
	@SuppressWarnings("serial")
	public static class NullInstanceException extends Exception {
		public NullInstanceException(String message) {
			super(message);
		}
	}
	
	public <T> T to(Class<T> type) throws NullInstanceException {
		String[] fullName = type.getName().split("\\.");
		String objectName = fullName[fullName.length-1];
		try {
			switch(objectName) {
			case "GameModel":
				return type.cast(server.getGameController().getGameModel());
			case "CardModel":
				return type.cast(server.getGameController().getCurrentCard());
			case "AdventureDeckModel":
				return type.cast(server.getGameController().getGameModel().getAdventureDeck());
			case "StoryDeckModel":
				return type.cast(server.getGameController().getGameModel().getStoryDeck());
			}
		} catch(NullPointerException e) {
			String message = "The " + objectName + " instance you tried to access was found to be null. "
					+ "\nPerhaps you are trying to access the instance before it has been initialized?";
			throw new NullInstanceException(message);
		}
		return null;
	}
	
	public <T> T to(Class<T> type, int index) throws NullInstanceException {
		String[] fullName = type.getName().split("\\.");
		String objectName = fullName[fullName.length-1];
		index--;
		try {
			switch(objectName) {
			case "ClientHandler":
				return type.cast(server.getPlayerClientHandlers().get(index));
			case "PlayerModel":
				return type.cast(server.getGameController().getGameModel().getPlayers().get(index));
			}
		} catch(IndexOutOfBoundsException | NullPointerException e) {
			String message = "The " + objectName + " instance at index " + index + " you tried to access was found to be null. "
					+ "\nPerhaps you are trying to access the instance before it has been initialized?";
			throw new NullInstanceException(message);
		}
		return null;
	}
	
	public <T> T to(Class<T> type, int index, String name) throws NullInstanceException {
		String[] fullName = type.getName().split("\\.");
		String objectName = fullName[fullName.length-1];
		index--;
		try {
			switch(objectName) {
			case "CardModel":
				for(CardModel card : server.getGameController().getGameModel().getPlayers().get(index).hand.playerHand) {
					if(card.cardName == name) {
						return type.cast(card);
					}
				}
			}
		} catch(IndexOutOfBoundsException | NullPointerException e) {
			String message = "The " + objectName + " instance at index " + index + "," + name + " you tried to access was found to be null. "
					+ "\nPerhaps you are trying to access the instance before it has been initialized?";
			throw new NullInstanceException(message);
		}
		return null;
	}
}
