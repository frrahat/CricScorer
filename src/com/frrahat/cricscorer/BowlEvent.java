package com.frrahat.cricscorer;

/**
 * @author Rahat
 * @since Dec 13, 2016
 */
public class BowlEvent {
	public static enum EventType{Run, Extra, Wicket, RunOut, DeletePrev}
	
	EventType eventType;
	int runs;
	String str;

	
	public BowlEvent(EventType eventType, int runs, String str){
		this.eventType=eventType;
		this.runs=runs;
		this.str=str;
	}
}
