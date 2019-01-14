package com.softwareeng.project.server.Models;

public class RankCardModel extends BattleCardModel {
    public RankCardModel(String n) {
        super(n);

        switch (n) {
            case "Squire":
                battlePoints = 5;
                break;
            case "Knight":
                battlePoints = 10;
                break;
            case "Champion Knight":
                battlePoints = 20;
                break;
            case "Knight of the Round Table":
                //TODO: call endgame sequence
                break;
            default:
                battlePoints = 5;
        }
    }

    public static int shieldsRequiredForNextUpgrade(String rankName) {
        int shields = 0;

        switch (rankName) {
            case "Squire":
                shields = 5;
                break;
            case "Knight":
                shields = 7;
                break;
            case "Champion Knight":
                shields = 10;
                break;
        }

        return shields;
    }
}
