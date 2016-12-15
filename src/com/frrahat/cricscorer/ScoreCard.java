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
	
	//int lastBallBatsMan=-1;
	int currentBatsmanNum=0;	
	int currentBowler=0;
	
	String overStrings[];
	int currentOver;
	
	boolean isNewOverNext;
	
	
	public ScoreCard(String batsmanName, String bowlername){
		this.totalScore=0;
		this.extras=0;
		this.wickets=0;
		this.ballsInOver=0;
		
		batsmans=new Batsman[MAX_PLAYER];
		bowlers=new Bowler[MAX_OVERS];
		
		batsmans[0]=new Batsman(batsmanName,0);		
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
			
			batsmans[currentBatsmanNum].addContribution(bowlEvent.runs);
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
			batsmans[currentBatsmanNum].sendToPavillion(true);
			bowlers[currentBowler].addContribution(bowlEvent.runs, 1, true);	
			currentBatsmanNum=wickets;
		}
		else if(bowlEvent.eventType==BowlEvent.EventType.DeletePrev){
			String s=overStrings[currentOver].trim();
			if(s.length()==0)
				return;
			String parts[]=s.split(" ");
			if(parts.length==0){
				return;
			}
			
			String lastBallString=parts[parts.length-1];
			overStrings[currentOver]="";
			for(int i=0;i<parts.length-1;i++){
				overStrings[currentOver]+=" "+parts[i];
			}
			
			if(lastBallString.endsWith("wd") || lastBallString.endsWith("nb")){
				int len=lastBallString.length();
				if(len>2){
					int extraRuns=Integer.parseInt(lastBallString.substring(0,len-2));
					
					this.extras-=extraRuns;
					this.totalScore-=extraRuns;
					
					bowlers[currentBowler].delContribution(extraRuns, 0, false);
				}
				this.extras-=2;
				this.totalScore-=2;
				
				bowlers[currentBowler].delContribution(2, 0, false);
			}
			else if(lastBallString.equals("W")){
				batsmans[currentBatsmanNum]=null;
				wickets--;
				currentBatsmanNum=wickets;
				batsmans[currentBatsmanNum].bringBackToLife(true);
				bowlers[currentBowler].delContribution(0, 1, true);
			}
			else{
				int runs=Integer.parseInt(lastBallString);
				
				totalScore-=runs;
				ballsInOver--;
				batsmans[currentBatsmanNum].delContribution(runs);
				
				bowlers[currentBowler].delContribution(runs, 1, false);
			}
			return;
		}
		
		overStrings[currentOver]+=" "+bowlEvent.str;

	}
	
	public void startNewOver(){		
		currentOver++;
		ballsInOver=0;
		
		overStrings[currentOver]="";
		
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
	
	public Batsman getCurrentBatsman(){
		return batsmans[currentBatsmanNum];
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
	
	public void setNextBatsman(String name){
		batsmans[wickets+1]=new Batsman(name, wickets+1);
	}
	
	public void setCurrentBowler(String name){
		bowlers[currentOver].name=name;
	}
	
	@Override
	public String toString() {
		String s=toScoreString()+"\n\n";
		s+="Batsman's Name : R-B-F-S\n";
		for(int i=0;i<wickets+1;i++){
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
	
	public void plusAdditionalRuns(int extraRuns){
		String parts[]=overStrings[currentOver].split(" ");
		if(parts.length==0){
			return;
		}
		
		String lastBallString=parts[parts.length-1];
		overStrings[currentOver]="";
		for(int i=0;i<parts.length-1;i++){
			overStrings[currentOver]+=" "+parts[i];
		}
		overStrings[currentOver]+=" "+Integer.toString(extraRuns)+lastBallString;
		
		totalScore+=extraRuns;
		extras+=extraRuns;
		batsmans[currentBatsmanNum].addContribution(extraRuns);
		bowlers[currentBowler].addContribution(extraRuns, 0, false);
	}
}
