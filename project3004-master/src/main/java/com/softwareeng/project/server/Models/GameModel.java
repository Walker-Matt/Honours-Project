package com.softwareeng.project.server.Models;

import java.util.ArrayList;

public class GameModel {
    public AdventureDeckModel adventureDeck;
    public StoryDeckModel storyDeck;
    public AdventureDeckModel discardAdventureDeck;
    public StoryDeckModel discardStoryDeck;
    public RankDeckModel rankDeck;
    public boolean gameEnd;
    public ArrayList<PlayerModel> players;
    public PlayerModel currentPlayer;
    private CardModel currentStory;

    public GameModel() {
        adventureDeck = new AdventureDeckModel();
        storyDeck = new StoryDeckModel();
        discardAdventureDeck = new AdventureDeckModel();
        discardStoryDeck = new StoryDeckModel();
        rankDeck = new RankDeckModel();
        gameEnd = false;
        players = new ArrayList<>(4);
    }

    public ArrayList<PlayerModel> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<PlayerModel> players) {
        if (null != players)
            this.players = players;
    }

    //Pass in PlayerModel (i.e. self) if you do not want to include yourself in rank up check
    public boolean rankUpCheck(PlayerModel p, int shields) {
        for (PlayerModel player : players) {
            if (p == player) {
                continue;
            }

            if (RankCardModel.shieldsRequiredForNextUpgrade(player.rank.cardName) - player.shields - shields <= 0) {
                return true;
            }
        }

        return false;
    }

    public void setCurrentStory(CardModel card) {
        this.currentStory = card;
    }

    public CardModel getCurrentStory() {
        return currentStory;
    }

}
