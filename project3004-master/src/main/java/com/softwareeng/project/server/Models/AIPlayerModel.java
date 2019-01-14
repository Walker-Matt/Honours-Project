package com.softwareeng.project.server.Models;

import com.softwareeng.project.server.Controllers.strategy.AIStrategy;
import com.softwareeng.project.server.Controllers.strategy.Strategy1;
import com.softwareeng.project.server.Controllers.strategy.Strategy2;

import java.util.ArrayList;
import java.util.Random;

public class AIPlayerModel extends PlayerModel {

    public AIStrategy aiStrategy;
    int strategyNum;

    public AIPlayerModel(int id, String name) {
        super(id, name);
        aiStrategy = new Strategy2(this);;
    }

    //For testing purposes
    public AIPlayerModel(int id, String name, int strategy) {
        super(id, name);

        switch (strategy) {
            case 1:
                aiStrategy = new Strategy1(this);
                break;
            case 2:
                aiStrategy = new Strategy2(this);
                break;
            default:
                aiStrategy = new Strategy2(this);
                break;
        }
    }

    //returns a random instance of one of the strategy
    private AIStrategy getRandomStrategy() {
        Random random = new Random();
        int randomNum = random.nextInt(2) + 1;

        switch (randomNum) {

            case 1:
                return new Strategy1(this);
            case 2:
                return new Strategy2(this);
            default:
                return new Strategy2(this);

        }

    }

    public boolean doISponsorAQuest(int numStages, ArrayList<PlayerModel> players, PlayerModel currPlayer) {
        return this.aiStrategy.doISponsorAQuest(this.hand, numStages, players, currPlayer);
    }

    public boolean doIParticipateInTournament(ArrayList<PlayerModel> players, TournamentCardModel card) {
        return this.aiStrategy.doIParticipateInTournament(players, card);
    }

    public ArrayList<CardModel> getCardsToPlayForTournament(ArrayList<PlayerModel> players, TournamentCardModel tourCard) {
        return this.aiStrategy.getCardsToPlayForTournament(this.hand, players, tourCard);
    }

    public ArrayList<CardModel> getCardsToPlayForQuest(int numStages, int currStage) {
        return this.aiStrategy.getCardsToPlayForQuest(this.hand, numStages, currStage);
    }

    public boolean doIParticipateInQuest(int numStages) {
        return this.aiStrategy.doIParticipateInQuest(this.hand, numStages);
    }

    public int nextBid(int roundNumber) {
        return this.aiStrategy.nextBid(this.hand, roundNumber);
    }

    public ArrayList<Integer> discardAfterWinningTest(int roundNumber) {
        return this.aiStrategy.discardAfterWinningTest(this.hand, roundNumber);
    }

    public ArrayList<Integer> discardToManyCards() {
        return this.aiStrategy.discardToManyCards(this.hand);
    }

    public ArrayList<Integer> getNonSponsorCardsToPlayQuest(int stageNum, QuestCardModel qCard) {
        return this.aiStrategy.getNonSponsorCardsToPlayQuest(this.hand, stageNum, qCard);
    }
}
