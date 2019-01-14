package com.softwareeng.project.server.Controllers.strategy;

import com.softwareeng.project.server.Controllers.PlayerController;
import com.softwareeng.project.server.Models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

public class Strategy2 extends Strategy {
	final static Logger log = Logger.getLogger(Strategy2.class);

   
	

    public Strategy2(AIPlayerModel player) {
        super(player);

    }
    class bpComparator implements Comparator<CardModel>{
		@Override
		public int compare(CardModel o1, CardModel o2) {
			return ((BattleCardModel) o1).battlePoints - ((BattleCardModel) o2).battlePoints;
		}
    	
    }
    @Override
    public boolean doIParticipateInTournament(ArrayList<PlayerModel> players,TournamentCardModel card) {
        return true;
    }
    
    @Override
    public ArrayList<CardModel> getCardsToPlayForTournament(HandModel hand,ArrayList<PlayerModel> players,TournamentCardModel tourCard) {

    	ArrayList<CardModel> cards = new ArrayList<>();
        
    	ArrayList<WeaponCardModel> weapons = hand.uniqueWeapons();
        Collections.sort(weapons, new bpComparator());
        Collections.reverse(weapons);
        
        List<AllyCardModel> allies = hand.allies();
        Collections.sort(allies, new bpComparator());
        Collections.reverse(allies);
        
        ArrayList<String> cardsInPlay = new ArrayList<>();
        
        int totalBattlePoints = 0;
        
        //I play as few WEAPON cards to get 50 or my best possible Battle points
        for (WeaponCardModel cardInHand : weapons) {
            cards.add(cardInHand);
            totalBattlePoints += cardInHand.battlePoints;
            if (totalBattlePoints >= 50) {
            	log.info(" AIPlayer plays as few WEAPON cards to get 50 or my best possible Battle points");
                return cards;
            }
        }

        //If above fails, I play the allies I could to reach that level of BP (50)
        for (AllyCardModel cardInHand : allies) {
        	log.info("AIPlayer plays the allies ");
            cards.add(cardInHand);
            totalBattlePoints += cardInHand.battlePoints;
                return cards;
        }
        return cards;
    }

    @Override
    public boolean doISponsorAQuest(HandModel hand, int numStages,ArrayList<PlayerModel> players,PlayerModel currPlayer) {
    	// Check if someone can rank up
    	for (PlayerModel player : players) {
    		if(player == currPlayer) {
    			continue;
    		}
    		
    		if (RankCardModel.shieldsRequiredForNextUpgrade(player.rank.cardName) - numStages - player.shields <= 0) {
    			log.info(player.playerName + " can rank up by winning this quest");
                return false;
            }
        }
    	List<FoeCardModel> foes = hand.foes();

        //one less foe to worry about
        if (hand.getTest() != null) {
            numStages--;
        }

        if (foes.size() < numStages) {
        	log.info("AIPlayer: not enough foe cards, does not participate");
            return false;
        }

        foes.sort(new Comparator<FoeCardModel>() {
            @Override
            public int compare(FoeCardModel o1, FoeCardModel o2) {
                return o1.battlePoints - o2.battlePoints;
            }
        });


        int increasing = 1;
        int currBattlePts = foes.get(0).battlePoints; //TODO: provide API that gives upper battle power depending on current quest...
        boolean firstCheck = true;;
        for (FoeCardModel f : foes) {
        	if(firstCheck == true) {
        		firstCheck = false;
        		increasing++;
        		currBattlePts = f.battlePoints;
        		continue;
        	}
            if (currBattlePts < f.battlePoints) {
                increasing++;
            }
        }

        boolean sponsoring = increasing >= numStages;
        if(sponsoring == true) {
        log.info("AIPlayer has enough increasing foes and no player can rank up by winning Quest, therefore will sponsor");
        }else {
        	log.info("AIPlayer does not have enough increasing foes and no player can rank up by winning Quest, therefore will  not sponsor");
        }
        return sponsoring;
    }

    @Override
    public boolean doIParticipateInQuest(HandModel hand, int numStages) {
        //I can increment by 10 at each stage
        boolean c1 = false;
        List<Integer> allFoes = new ArrayList<>();
        for (CardModel cardInHand : hand.playerHand) {
            if (cardInHand instanceof FoeCardModel) {
                allFoes.add(((FoeCardModel) cardInHand).battlePoints);
            }
        }
          
        if (allFoes.size() >= 2) {
            Collections.sort(allFoes); //sorts in increasing order
            Collections.reverse(allFoes); //sorts in decreasing order

            
            
            //if the difference between the two max is greater than or equal to 10
            
            c1 = (allFoes.get(0) - allFoes.get(1)) >= 10;
            if(c1) {
            	log.info("AIPlayer: Cant increment by 10 each stage");
            }
            else {
            	log.info("AIPlayer: The difference between the two max is NOT greater than or equal to 10");
            }
        }
        
        // I have atleast 2 foes of less than 25 points
        List<Integer> allFoes2 = new ArrayList<>();
        for (CardModel cardInHand : hand.playerHand) {
            if (cardInHand instanceof FoeCardModel) {
                if (((FoeCardModel) cardInHand).battlePoints >= 25)
                    allFoes2.add(((FoeCardModel) cardInHand).battlePoints);
            }
        }
        boolean c2 = allFoes2.size() >= 2;
        if(c2) {
        	log.info("AIPlayer: has atleast 2 foes of less than 25 points");
        }
        //then I participate
        if(c1 && c2) {
        	log.info("AIPlayer: can increment by 10 each round and has atleast 2 foes of less than 25 points, therefore participates");
        }
        return c1 && c2;
    }

    //should be called only if sponsored
    @Override
    public ArrayList<CardModel> getCardsToPlayForQuest(HandModel handModel, int numStages, int currStage) {
    	
    	ArrayList<String> cardsPlayed = new ArrayList<>();
    	ArrayList<CardModel> cardsToReturn = new ArrayList<>();
    	ArrayList<CardModel> incFoes = new ArrayList<>();
    	
    	if(currStage == numStages) {
    		log.info("AIPlayer: last stage, plays cards to be atleast 40 if possible");
    	for(CardModel c : handModel.playerHand) {
    		if(cardsPlayed.contains(c.cardName)){
    			continue;
    		}
    		cardsToReturn.add(c);
    		cardsPlayed.add(c.cardName);
    	}
    	}
    	else if(numStages-1 == currStage) {
    		for(CardModel c : handModel.playerHand) {
    			if (c instanceof TestCardModel) {
    				log.info("AIPlayer: second last stage, plays test card");
    				cardsToReturn.add(c);
    			}
    		}
    		if(cardsToReturn.isEmpty()) {
    			log.info("AIPlayer: second last stage, cant play test card and continues normally");
    			for(CardModel c : handModel.playerHand) {
        			if(c instanceof FoeCardModel) {
        				incFoes.add(c);
        			}
        		}
        		Collections.sort(incFoes, new bpComparator());
        		cardsToReturn.add(incFoes.get(0));
    		}
    		
    	}else {
    		log.info("AIPlayer: plays increasing foes from firs to second last non-test stage");
    		for(CardModel c : handModel.playerHand) {
    			if(c instanceof FoeCardModel) {
    				incFoes.add(c);
    			}
    		}
    		Collections.sort(incFoes, new bpComparator());
    		cardsToReturn.add(incFoes.get(0));
    	}
        return cardsToReturn;
        
    }
    
    @Override
    public int nextBid(HandModel hand, int roundNumber) {

        if (roundNumber == 1) {
            //bid the number of foes less than 25
        	log.info("AIPlayer: round number is 1, bids the number of foes less than 25");
            return foesLessThan(hand, 25).size();
        } else if (roundNumber == 2) {
        	log.info("AIPlayer: round number 2, bids the number of foes less than 25 + number of duplicates in hand.");
            //bid the number of foes less than 25 + number of duplicates in hand.
            return foesLessThan(hand, 25).size() + duplicateFoes(hand).size();
        } else {
            return 0;
        }
    }

    @Override
    public ArrayList<Integer> discardAfterWinningTest(HandModel handModel, int roundNumber) {
        if (roundNumber == 1) {
            List<CardModel> returnList = new ArrayList<>();
            for (CardModel cardInHand : handModel.playerHand) {
                if (cardInHand instanceof FoeCardModel) {
                    FoeCardModel temp = (FoeCardModel) cardInHand;
                    if (temp.battlePoints < 25) {
                        returnList.add(temp);
                    }
                }
            }
            return (ArrayList)returnList;
        } else if (roundNumber == 2) {
            //todo: add foes of round 1 to the return list
            //todo
            //discard duplicate foes
            return (ArrayList)duplicateFoes(handModel);
        } else {
            return null;
        }
    }
    @Override
    //Discards first card from left to right
    public ArrayList<Integer> discardToManyCards(HandModel hand){
    	
    	ArrayList<CardModel> playerCards = hand.playerHand;
    	ArrayList<Integer> cardsToDiscardIds = new ArrayList<>();
    	CardModel discardCard = playerCards.get(0);
		cardsToDiscardIds.add(discardCard.cardId);
    	return cardsToDiscardIds;
    }

	@Override
	public ArrayList<Integer> getNonSponsorCardsToPlayQuest(HandModel hand, int stageNum, QuestCardModel qCard) {
		ArrayList<Integer> cardIds = new ArrayList<>();
		ArrayList<String> cardsPlayed = new ArrayList<>();
		boolean wasAlly = false;
		
		ArrayList<WeaponCardModel> weapons = hand.uniqueWeapons();
        Collections.sort(weapons, new bpComparator());
        Collections.reverse(weapons);
        
        List<AllyCardModel> allies = hand.allies();
        Collections.sort(allies, new bpComparator());
        Collections.reverse(allies);
        
		if(qCard.numOfStages == stageNum) {
			log.info("AIPlayer: Last stage, playing strongest valid combination");
			for (CardModel card : weapons) {
				if(!cardsPlayed.contains(card));
				cardIds.add(card.cardId);
				cardsPlayed.add(card.cardName);
			}
			
			
			for (CardModel card : allies) {
				if(!cardsPlayed.contains(card));
				cardIds.add(card.cardId);
				cardsPlayed.add(card.cardName);
			}
			
			return cardIds;
			
		}
		//Check for Amour first
		log.info("AIPlayer: not last stage, therefore playing an increment of +10 using amour first, then ally, then weapons" );
		for(CardModel c : hand.playerHand) {
			if(cardsPlayed.contains(c.cardName)) {
				continue;
			}
			if(c.cardName == "Amour") {
				cardsPlayed.add(c.cardName);
				cardIds.add(c.cardId);
				
			}
		}
		//Check for foe 20
		
		for(CardModel c : hand.playerHand) {
			if(cardsPlayed.contains(c.cardName)) {
				continue;
			}
			if(c instanceof AllyCardModel && ((BattleCardModel) c).battlePoints == 20) {
				cardsPlayed.add(c.cardName);
				cardIds.add(c.cardId);
				wasAlly = true;
			}
		}
		
		if(wasAlly == true) {
			for(CardModel c : hand.playerHand) {
				if(cardsPlayed.contains(c.cardName)) {
					continue;
				}
				if(c instanceof WeaponCardModel && ((BattleCardModel) c).battlePoints == 30) {
					cardsPlayed.add(c.cardName);
					cardIds.add(c.cardId);
					
				}
			}
		}else {
			for(CardModel c : hand.playerHand) {
				if(cardsPlayed.contains(c.cardName)) {
					continue;
				}
				if(c instanceof WeaponCardModel && ((BattleCardModel) c).battlePoints == 20) {
					cardsPlayed.add(c.cardName);
					cardIds.add(c.cardId);
				}
			}
		}
		
		return cardIds;
	}

}
