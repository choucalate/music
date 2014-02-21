package com.model;

import java.io.Serializable;
import java.util.Calendar;

public class RecNotes implements Serializable {
	private static final long serialVersionUID = 3;

	public long getCurrTime() {
		return currTime;
	}

	public void setCurrTime(long Calendar) {
		this.currTime = currTime;
	}

	public String getNoteToPlay() {
		return noteToPlay;
	}

	public void setNoteToPlay(String noteToPlay) {
		this.noteToPlay = noteToPlay;
	}

	private long currTime;
	private String noteToPlay;
	private int beat;
	private int noteshade;
	private boolean isBeat;
   
	public int getBeat() {
		return beat;
	}
	
	public boolean isBeat() {
		return isBeat;
	}

	public int getNoteshade() {
		return noteshade;
	}

	public void setNoteshade(int noteshade) {
		this.noteshade = noteshade;
	}

	public RecNotes(long l, String key, int noteshade) {
		this.currTime = l;
		this.noteToPlay = key;
		this.noteshade = noteshade;
		this.isBeat = false;
	}

	/**
	 * The constructor for a beat Recnote
	 */
	public RecNotes(long l, int beat) {
		this.isBeat = true;
		this.beat = beat;
		this.currTime = l;
	}
}
