package com.softwareeng.project.server.Controllers.strategy;

import com.softwareeng.project.server.Models.CardModel;
import com.softwareeng.project.server.Models.HandModel;
import com.softwareeng.project.server.Models.PlayerModel;
import com.softwareeng.project.server.Models.QuestCardModel;
import com.softwareeng.project.server.Models.TournamentCardModel;

import java.util.ArrayList;

public abstract interface AIStrategy {

    boolean doIParticipateInTournament(ArrayList<PlayerModel> players,TournamentCardModel card);

    ArrayList<CardModel> getCardsToPlayForTournament(HandModel hand,ArrayList<PlayerModel> players, TournamentCardModel tourCard);

    boolean doISponsorAQuest(HandModel hand, int numStages,ArrayList<PlayerModel> players,PlayerModel currPlayer);

    ArrayList<CardModel> getCardsToPlayForQuest(HandModel handModel, int numStages, int currStage);

    boolean doIParticipateInQuest(HandModel handModel, int numStages);

    int nextBid(HandModel hand, int roundNumber);

    ArrayList<Integer> discardAfterWinningTest(HandModel handModel, int roundNumber);
    
    ArrayList<Integer> discardToManyCards (HandModel hand);
    
    ArrayList<Integer> getNonSponsorCardsToPlayQuest(HandModel hand, int stageNum,QuestCardModel qCard);   
}
