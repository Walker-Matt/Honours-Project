package helpers;

import com.softwareeng.project.server.Server;

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
			case "GameController":
				return type.cast(server.getGameController());
			}
		} catch(NullPointerException e) {
			String message = "The " + objectName + " instance you tried to access was found to be null. "
					+ "\nPerhaps you are trying to access the instance before it has been initialized?";
			throw new NullInstanceException(message);
		}
		return null;
	}
	
	public <T> T to(int index, Class<T> type) throws NullInstanceException {
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
}
