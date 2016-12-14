package com.frrahat.cricscorer;

import java.io.Serializable;

/**
 * @author Rahat
 * @since Dec 13, 2016
 */
public class Batsman implements Serializable{
	String name;
	int number;
	int runs;
	int balls;
	int fours;
	int sixes;
	boolean isLive;
	
	public Batsman(String name, int number){
		this.name=name;
		this.number=number;
		
		this.runs=0;
		this.balls=0;
		this.fours=0;
		this.sixes=0;
		this.isLive=true;
	}
	
	public void addContribution(int runs){
		this.runs+=runs;
		this.balls++;
		
		if(runs==4){
			this.fours++;
		}
		else if(runs==6){
			this.sixes++;
		}
	}
	
	public void sendToPavillion(boolean consumedABall){
		if(consumedABall)
			balls++;
		this.isLive=false;
	}

	@Override
	public String toString() {
		return "Batsman "+ number+". "+name + (isLive?"*":" ")+": "
				+ runs + "-" + balls + "-" + fours + "-"
				+ sixes;
	}
	
	
}
