package com.model;

public class NotePlay {
	private int duration;
	private int note;
	private int counter;

	public NotePlay() {
		super();
		this.duration = 0;
		this.note = 0;
		this.counter = 0;
	}

	// counter will index until hits duration
	public NotePlay(int duration, int note) {
		super();
		this.duration = duration;
		this.note = note;
		this.counter = 0;
	}

	public void setValues(int duration, int note) {
		this.duration = duration;
		this.note = note;
	}

	public int getCounter() {
		return counter;
	}

	public void incCounter() {
		this.counter++;
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
