package com.frrahat.cricscorer;

import java.io.Serializable;

/**
 * @author Rahat
 * @since Dec 13, 2016
 */
public class Bowler implements Serializable{
	String name;
	private int number;
	
	private int runs;
	private int balls;
	private int wickets;
	
	public Bowler(String name, int number){
		this.name=name;
		this.number=number;
		
		this.runs=0;
		this.wickets=0;
		this.balls=0;
	}
	
	public void addContribution(int runs, int balls, boolean takenWicket){
		this.runs+=runs;
		this.balls+=balls;
		
		if(takenWicket){
			this.wickets++;
		}
	}
	
	public void delContribution(int runs, int balls, boolean takenWicket){
		this.runs-=runs;
		this.balls-=balls;
		
		if(takenWicket){
			this.wickets--;
		}
	}

	@Override
	public String toString() {
		return "Bowler "+number+". " + name + " : " + runs
				+ "-" + (balls/6) + "."+ (balls%6)+ "-" + wickets;
	}
	
	
}
