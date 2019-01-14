package com.softwareeng.project.server.Models;

public class BattleCardModel extends CardModel {
    public int battlePoints;

    public BattleCardModel(String n) {
        super(n);

        battlePoints = 0;
    }

    public BattleCardModel(String n, int b) {
        super(n);

        battlePoints = b;
    }


}
