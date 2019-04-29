package com.softwareeng.project.server.Models;

public class TournamentCardModel extends CardModel {

    public int bonusShields;

    public TournamentCardModel(String name, int bonus) {
        super(name);
        bonusShields = bonus;
    }


}
