/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;
import java.util.Arrays;   

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	private void playGame() {
		usedCategories = new boolean[nPlayers][N_CATEGORIES];
		Scorecard = new int[N_CATEGORIES][nPlayers];
		for (round=1; round<=N_SCORING_CATEGORIES; round++) {
			playRound();
		}
		final_calculations();		
	}
	
	private void playRound() {
		for (playerNumber=1; playerNumber<=nPlayers; playerNumber++) {
			dice = new int[N_DICE];
			String playerName = playerNames[playerNumber-1];
			display.printMessage(playerName+"'s turn. Click \"Roll Dice\" button to roll the dice. You have 3 rolls left");
			rollsLeft = 3;
			firstRoll();
			second_and_thirdRoll();
			second_and_thirdRoll();
			updateScore();	
		}
	}
	
	private void final_calculations(){
		int max_score = 0;
		int winner = 0;
		for (playerNumber=1; playerNumber<=nPlayers; playerNumber++) {
			int upperScore = calcUpperScore();
			Scorecard[UPPER_SCORE-1][playerNumber-1] = upperScore;
			display.updateScorecard(UPPER_SCORE, playerNumber, upperScore);
			
			int upperBonus = calcupperBonus(upperScore);
			Scorecard[UPPER_BONUS-1][playerNumber-1] = upperBonus;
			display.updateScorecard(UPPER_BONUS, playerNumber, upperBonus);
			
			int lowerScore = calcLowerScore();
			Scorecard[LOWER_SCORE-1][playerNumber-1] = lowerScore;
			display.updateScorecard(LOWER_SCORE, playerNumber, lowerScore);
			
			Scorecard[TOTAL-1][playerNumber-1] = upperScore+upperBonus+lowerScore;
			int tot = upperScore+upperBonus+lowerScore;
			display.updateScorecard(TOTAL, playerNumber, tot);	
			if (tot>=max_score){
				max_score = tot;
				winner = playerNumber;
			}
		}	
		String playerName = playerNames[winner-1];
		display.printMessage("Congratulations, "+playerName+", you\'re the winner with a total score of "+max_score);
		
		
	}
	
	private void firstRoll() {
		display.waitForPlayerToClickRoll(playerNumber);
		rollDice();
		display.displayDice(dice);
		rollsLeft = rollsLeft-1;
	}
	
	private void second_and_thirdRoll() {
		if (rollsLeft==2) {
			display.printMessage("Select the dice you wish to re-roll and click \"Roll Again\". You have "+rollsLeft+" rolls left.");
		}else {
			display.printMessage("Select the dice you wish to re-roll and click \"Roll Again\". You have "+rollsLeft+" roll left.");
		}
		
		display.waitForPlayerToSelectDice();
		updateDice();
		display.displayDice(dice);
		rollsLeft = rollsLeft-1;
	}	
	
	private void updateScore() {
		display.printMessage("Select a category for this roll.");
		boolean wrongCategory = true;
		while (wrongCategory) {
			int category = display.waitForPlayerToSelectCategory();
			if (usedCategories[playerNumber-1][category-1]==false) {
				wrongCategory=false;
				usedCategories[playerNumber-1][category-1]=true;
				int score = calculateScore(category);
				Scorecard[category-1][playerNumber-1] = score;
				Scorecard[TOTAL-1][playerNumber-1] = scoreTotal();
				int tot = scoreTotal();
				display.updateScorecard(category, playerNumber, score);
				display.updateScorecard(TOTAL, playerNumber, tot);
			}
		}
	}
	
	
	
	
	private void rollDice() {
		for (int die=0; die<N_DICE; die++) {
			dice[die]  = rgen.nextInt(6)+1;
		}
	}
	
	private void updateDice() {
		for (int die=0; die<N_DICE; die++) {
			if(display.isDieSelected(die)) dice[die]  = rgen.nextInt(6)+1;
		}
	}
	
	
	private int scoreTotal() {
		int scoreSum = 0;
		for (int i=0; i<N_CATEGORIES-1; i++) {
			scoreSum+=Scorecard[i][playerNumber-1];
		}
		return scoreSum;
	}
	
	private int calcUpperScore(){
		int scoreSum = 0;
		for (int i=ONES; i<=SIXES; i++) {
			scoreSum+=Scorecard[i-1][playerNumber-1];
		}
		return scoreSum;
	}
	
	private int calcupperBonus(int upperScore) {
		if (upperScore>=63) {
			return 35;
		}else {
			return 0;
		}
		
	}
	
	private int calcLowerScore(){
		int scoreSum = 0;
		for (int i=THREE_OF_A_KIND; i<=CHANCE; i++) {
			scoreSum+=Scorecard[i-1][playerNumber-1];
		}
		return scoreSum;
	}
	
	
	private int calculateScore(int category) {
//		boolean p = YahtzeeMagicStub.checkCategory(dice, category);
		boolean p = checkCategory(dice, category);
		if (category==ONES && p) {
			return SumInts(ONES);
			
		}else if (category==TWOS && p) {
			return SumInts(TWOS); 
			
		}else if (category==THREES && p) {
			return SumInts(THREES);
			
		}else if (category==FOURS && p) {
			return SumInts(FOURS);
			
		}else if (category==FIVES && p) {
			return SumInts(FIVES); 
			 
		}else if (category==SIXES && p) {
			return SumInts(SIXES); 
			 
		}else if (category==THREE_OF_A_KIND && p) {
			return SumDice();
			 
		}else if (category==FOUR_OF_A_KIND && p) {
			return SumDice();
			 
		}else if (category==FULL_HOUSE && p) {
			return 25;
			 
		}else if (category==SMALL_STRAIGHT && p) {
			return 30;
			 
		}else if (category==LARGE_STRAIGHT && p) {
			return 40;
			 
		}else if (category==YAHTZEE && p) {
			return 50;
			
		}else if (category==CHANCE && p) {
			return SumDice();
			
		}else {
			return 0;
		}
	}
	
	
	private int SumDice(){
		int Sum = 0;
		for (int i=0; i<N_DICE; i++) {
			Sum+=dice[i];
		}
		return Sum;
	}

	private int SumInts(int num){
		int Sum = 0;
		for (int i=0; i<N_DICE; i++) {
			if (dice[i]==num) Sum+=dice[i];			
		}
		return Sum;
	}
	
	

	private boolean checkCategory(int[] dice, int category) {
		boolean result = false;
		int diffs;
		Arrays.sort(dice);
		
		switch (category) {
	      case ONES:	    	  
	    	  result = true;
	    	  break;
	      case TWOS:
	    	  result = true;
	    	  break;
	      case THREES:
	    	  result = true;
	    	  break;
	      case FOURS:
	    	  result = true;
	    	  break;
	      case FIVES:
	    	  result = true;
	    	  break;
	      case SIXES:
	    	  result = true;
	    	  break;
	      case THREE_OF_A_KIND:	  
	    	  
	    	  diffs = 0;
	    	  for (int j=1; j<N_DICE; j++) {
	    		  if ((dice[j]-dice[0])==0) diffs+=1;
	    	  }
	    	  if (diffs>=2) {
	    		  result = true;
	    	  }else {
	    		  diffs = 0;
		    	  for (int j=2; j<N_DICE; j++) {
		    		  if ((dice[j]-dice[1])==0) diffs+=1;
		    	  }
		    	  if (diffs>=2) {
		    		  result = true;
		    	  }else {
		    		  diffs = 0;
			    	  for (int j=3; j<N_DICE; j++) {
			    		  if ((dice[j]-dice[2])==0) diffs+=1;
			    	  }
			    	  if (diffs==2) {
			    		  result = true;
			    	  }
		    	  }
	    	  }
	    	  
	    	  break;
	      case FOUR_OF_A_KIND:
	    	  diffs = 0;
	    	  for (int j=1; j<N_DICE; j++) {
	    		  if ((dice[j]-dice[0])==0) diffs+=1;
	    	  }
	    	  if (diffs>=3) {
	    		  result = true;
	    	  }else {
			  diffs = 0;
		    	  for (int j=2; j<N_DICE; j++) {
		    		  if ((dice[j]-dice[1])==0) diffs+=1;
		    	  }
		    	  if (diffs==3) result = true;
	    	  }
	    	  
	    	  break;
	      case FULL_HOUSE:
	    	  if (dice[0]==dice[1] && dice[1]!=dice[2] && dice[2]==dice[3] && dice[3]==dice[4]) result = true;
	    	  if (dice[0]==dice[1] && dice[1]==dice[2] && dice[2]!=dice[3] && dice[3]==dice[4]) result = true;
	    	  break;
	      case SMALL_STRAIGHT:
	    	  
	    	  diffs = 0;
	    	  for (int i=0; i<N_DICE-1; i++) {
	    		  if ((dice[i+1]-dice[i])==1) diffs+=1;	    		  
	    	  }
	    	  if(diffs==4) result = true;
	    	  
	    	  if(diffs==3) {
	    		  boolean diffZero = false;
	    		  for (int k=0; k<N_DICE-1; k++) {
	    			  if ((dice[k+1]-dice[k])==0) diffZero = true;
	    		  }
	    		  if (diffZero==true) {
	    			  result = true;
	    		  }else {
	    			  if ( (dice[1]-dice[0])==2 || (dice[4]-dice[3])==2 ) result = true;	    			  
	    		  }
	    		  
	    	  }
	    	  
	    	  break;
	    	  
	      case LARGE_STRAIGHT:
	    	  diffs = 0;
	    	  for (int i=0; i<N_DICE-1; i++) {
	    		  if ((dice[i+1]-dice[i])==1) diffs+=1;	    		  
	    	  }
	    	  if(diffs==4) result = true;
	    	  break;
	      case YAHTZEE:
	    	  diffs = 0;
	    	  for (int i=0; i<N_DICE-1; i++) {
	    		  if ((dice[i+1]-dice[i])==0) diffs+=1;	    		  
	    	  }
	    	  if(diffs==4) result = true;
	    	  break;
	      case CHANCE:
	    	  result = true;
	    	  break;
	         
	   }
		
		
		return result;
	}
	
		
/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();

	private int round;
	private int playerNumber;
	private int[] dice;
	private boolean[][] usedCategories;
	private int[][] Scorecard;
	private int rollsLeft;
}
