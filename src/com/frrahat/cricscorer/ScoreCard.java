package com.frrahat.cricscorer;

import java.io.Serializable;

/**
 * @author Rahat
 * @since Dec 13, 2016
 */
public class ScoreCard implements Serializable{
	public static final int MAX_PLAYER=20;
	public static final int MAX_OVERS=30;
	int totalScore;
	int extras;
	int wickets;
	int ballsInOver;
	
	Batsman batsmans[];
	Bowler bowlers[];
	
	int strikingBatsmanNum=0;
	int nonStrikingBatsmanNum=1;
	
	int currentBowler=0;
	
	String overStrings[];
	int currentOver;
	
	boolean isNewOverNext;
	
	
	public ScoreCard(String batsman1name, String batsman2name, String bowlername){
		this.totalScore=0;
		this.extras=0;
		this.wickets=0;
		this.ballsInOver=0;
		
		batsmans=new Batsman[MAX_PLAYER];
		bowlers=new Bowler[MAX_OVERS];
		
		batsmans[0]=new Batsman(batsman1name,0);
		batsmans[1]=new Batsman(batsman2name, 1);
		
		bowlers[0]=new Bowler(bowlername, 0);
		
		overStrings=new String[MAX_OVERS];
		currentOver=0;
		overStrings[currentOver]="";
		
		isNewOverNext=false;
	}
	
	public void addBowlEvent(BowlEvent bowlEvent){	
		if(isNewOverNext){
			startNewOver();
			isNewOverNext=false;
		}
		
		if(bowlEvent.eventType==BowlEvent.EventType.Run){	
			ballsInOver++;
			
			if(ballsInOver==6){
				isNewOverNext=true;
			}
			
			totalScore+=bowlEvent.runs;
			
			if(bowlEvent.runs%2==1){
				switcthBatsMan();
			}
			
			batsmans[strikingBatsmanNum].addContribution(bowlEvent.runs);
			bowlers[currentBowler].addContribution(bowlEvent.runs, 1, false);
		}
		else if(bowlEvent.eventType==BowlEvent.EventType.Extra){
			totalScore+=bowlEvent.runs;
			extras+=bowlEvent.runs;
			if("b".equals(bowlEvent.str)){
				ballsInOver++;
				if(ballsInOver==6){
					isNewOverNext=true;
				}
				bowlers[currentBowler].addContribution(bowlEvent.runs, 1, false);
			}else{
				bowlers[currentBowler].addContribution(bowlEvent.runs, 0, false);
			}
		}
		else if(bowlEvent.eventType==BowlEvent.EventType.Wicket){
			ballsInOver++;
			
			if(ballsInOver==6){
				isNewOverNext=true;
			}
			
			wickets++;
			batsmans[strikingBatsmanNum].sendToPavillion(true);
			bowlers[currentBowler].addContribution(bowlEvent.runs, 1, true);	
			strikingBatsmanNum=wickets+1;
		}
		
		else if(bowlEvent.eventType==BowlEvent.EventType.RunOut){ 
			/*ballsInOver++;
			if(ballsInOver==6){
				isNewOverNext=true;
			}
			wickets++;
			
			totalScore+=bowlEvent.runs;
			//TODO
			strikingBatsmanNum=wickets+1;*/
		}
		else if(bowlEvent.eventType==BowlEvent.EventType.Plus){
			/*int i=overStrings[currentOver].length()-1;
			while(i>=0 && overStrings[currentOver].charAt(i)!=' ')
				i--;
			if(i<overStrings[currentOver].length()-1){
				String lastBall=overStrings[currentOver].substring(i+1);
				overStrings[currentOver]=","+lastBall+",";
				int j=0;
				while(j<lastBall.length() && (lastBall.charAt(j)>='0' && lastBall.charAt(j)<='9'))
					j++;
				String prevRunString=lastBall.substring(0,j);
				
				if(lastBall.length()!=j){
					int prevRun=0;
					if(prevRunString.length()!=0){
						prevRun=Integer.parseInt(prevRunString);
					}
					totalScore++;
					lastBall=Integer.toString(prevRun+1)+lastBall.substring(j);
					
					overStrings[currentOver]=overStrings[currentOver].substring(0,i);
					overStrings[currentOver]+=" "+lastBall;
				}
			}*/
			//TODO
		}
		else if(bowlEvent.eventType==BowlEvent.EventType.DeletePrev){
			//TODO
		}
		
		
		overStrings[currentOver]+=" "+bowlEvent.str;

	}
	
	public void startNewOver(){		
		currentOver++;
		ballsInOver=0;
		
		overStrings[currentOver]="";
		
		switcthBatsMan();
		
		currentBowler++;
		bowlers[currentBowler]=new Bowler("", currentBowler);
	}
	
	public static String toOverString(int balls){
		return Integer.toString(balls/6)+"."+Integer.toString(balls%6);
	}
	
	public String toOverString(){
		if(ballsInOver==6)
			return Integer.toString(currentOver+1)+".0";
		return Integer.toString(currentOver)+"."+Integer.toString(ballsInOver);
	}
	
	public Batsman getStrikingBatsman(){
		return batsmans[strikingBatsmanNum];
	}
	
	public Batsman getNonStrikingBatsman() {
		return batsmans[nonStrikingBatsmanNum];
	}
	
	public Bowler getCurrentBowler(){
		return bowlers[currentBowler];
	}
	
	public String toScoreString(){
		return Integer.toString(totalScore)+"/"+Integer.toString(wickets)+", "
				+ toOverString()+" overs";
	}
	
	public String getCurrentOverstring(){
		return overStrings[currentOver];
	}
	
	public void deleteLastEvent(){
		//TODO
	}
	
	public void setNextBatsman(String name){
		batsmans[wickets+2]=new Batsman(name, wickets+2);
	}
	
	public void setCurrentBowler(String name){
		bowlers[currentOver].name=name;
	}
	
	@Override
	public String toString() {
		String s=toScoreString()+"\n\n";
		s+="Batsman's Name : R-B-F-S\n";
		for(int i=0;i<wickets+2;i++){
			s+=batsmans[i].toString()+"\n";
		}
		s+="\nExtras: "+Integer.toString(extras)+"\n";
		s+="\nBowler's Name : R-O-W\n";
		for(int i=0;i<=currentOver;i++){
			s+=bowlers[i].toString()+"\n";
		}
		s+="\n\nOver ball by ball:\n";
		for(int i=0;i<=currentOver;i++){
			s+=overStrings[i]+"\n";
		}
		return s;
	}
	
	public void switcthBatsMan(){
		int temp=strikingBatsmanNum;
		strikingBatsmanNum=nonStrikingBatsmanNum;
		nonStrikingBatsmanNum=temp;
	}

}
