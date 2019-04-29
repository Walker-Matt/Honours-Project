package com.softwareeng.project.server.Models;
import java.util.ArrayList;

import com.softwareeng.project.server.Models.AllyCardModel;

public class HandModel {
    public ArrayList<CardModel> playerHand; //adventure deck
    public ArrayList<CardModel> selectedCards;

    public HandModel() {
        playerHand = new ArrayList<>();
        selectedCards = new ArrayList<>();
    }

    public ArrayList<WeaponCardModel> uniqueWeapons() {
        ArrayList<WeaponCardModel> weapons = new ArrayList<WeaponCardModel>();

        for (CardModel c : playerHand) {
            if (c instanceof WeaponCardModel) {
                if (!weapons.contains(c)) {
                    weapons.add((WeaponCardModel) c);
                }
            }
        }

        return weapons;
    }

    public ArrayList<WeaponCardModel> weapons() {
        ArrayList<WeaponCardModel> weapons = new ArrayList<WeaponCardModel>();

        for (CardModel c : playerHand) {
            if (c instanceof WeaponCardModel) {
                weapons.add((WeaponCardModel) c);
            }
        }
        return weapons;
    }

    public ArrayList<AllyCardModel> allies() {
        ArrayList<AllyCardModel> allies = new ArrayList<AllyCardModel>();

        for (CardModel c : playerHand) {
            if (c instanceof AllyCardModel) {
                allies.add((AllyCardModel) c);
            }
        }

        return allies;
    }


    public ArrayList<FoeCardModel> foes() {
        ArrayList<FoeCardModel> foes = new ArrayList<FoeCardModel>();

        for (CardModel c : playerHand) {
            if (c instanceof FoeCardModel) {
                foes.add((FoeCardModel) c);
            }
        }

        return foes;
    }

    public TestCardModel getTest() {
        for (CardModel c : playerHand) {
            if (c instanceof TestCardModel) {
                return (TestCardModel) c;
            }
        }
        return null;
    }
}
