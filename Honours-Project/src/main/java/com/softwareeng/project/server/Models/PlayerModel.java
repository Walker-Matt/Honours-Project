package com.softwareeng.project.server.Models;

public class PlayerModel {
    public int playerId;
    public String playerName;
    public int shields;
    public RankCardModel rank;
    public HandModel hand;
    public boolean isActive;

    public PlayerModel(int id, String name) { //create a new player
        playerId = id;
        playerName = name;
        shields = 0; //each player starts with 0 shields
        rank = new RankCardModel("Squire");
        hand = new HandModel();
        isActive = true;
    }
}
