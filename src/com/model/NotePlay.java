package com.model;

public class NotePlay {
	private int duration;
	private int note;
	private int counter;

	//counter will index until hits duration
	public NotePlay(int duration, int note) {
		super();
		this.duration = duration;
		this.note = note;
		this.counter = 0;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int c) {
		this.counter = c;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getNote() {
		return note;
	}

	public void setNote(int note) {
		this.note = note;
	}
}
