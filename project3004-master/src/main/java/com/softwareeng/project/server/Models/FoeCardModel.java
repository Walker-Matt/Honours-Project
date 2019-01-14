package com.softwareeng.project.server.Models;

public class FoeCardModel extends BattleCardModel {
    public int upperBattlePoints;

    public FoeCardModel(String name, int lowerPoints) {
        super(name, lowerPoints);
        upperBattlePoints = -1; //not given the ability to have higher points
    }

    public FoeCardModel(String name, int lowerPoints, int higherPoints) {
        super(name, lowerPoints);
        upperBattlePoints = higherPoints;
    }
}
