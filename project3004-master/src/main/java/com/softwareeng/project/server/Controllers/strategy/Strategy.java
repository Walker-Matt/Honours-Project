package com.softwareeng.project.server.Controllers.strategy;

import com.softwareeng.project.server.Models.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Strategy implements AIStrategy {

    AIPlayerModel player;

    public Strategy(AIPlayerModel player) {
        this.player = player;
    }

    protected ArrayList<Integer> foesLessThan(HandModel hand, int num) {

        ArrayList<CardModel> list = new ArrayList<CardModel>();
        ArrayList<Integer> listOfIds = new ArrayList<>();
        
        int foesLessThan25 = 0;
        for (CardModel cardInHand : hand.playerHand) {
            if (cardInHand instanceof FoeCardModel) {
                FoeCardModel foe = (FoeCardModel) cardInHand;
                if (foe.battlePoints < num)
                    list.add(cardInHand);
            }
        }
        for (CardModel c : list) {
         int curCardId = c.cardId; 
        	listOfIds.add(curCardId);
        }
        
        return listOfIds;
    }

    protected List<CardModel> duplicateFoes(HandModel handModel) {
        List<CardModel> returnList = new ArrayList<>();
        List<String> uniqueFoes = new ArrayList<>();
        for (CardModel cardInHand : handModel.playerHand) {
            FoeCardModel temp = (FoeCardModel) cardInHand;
            if (!uniqueFoes.contains(temp.cardName)) {
                uniqueFoes.add(temp.cardName);
            } else {
                returnList.add(cardInHand);
            }
        }
        return returnList;
    }
}
