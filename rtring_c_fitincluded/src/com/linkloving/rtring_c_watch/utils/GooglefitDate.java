package com.linkloving.rtring_c_watch.utils;

public class GooglefitDate {
	private long start_time ;
	private long end_time ;
	private int step;
	private float distance;
	
	public long getStart_time() {
		return start_time;
	}
	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}
	public long getEnd_time() {
		return end_time;
	}
	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	@Override
	public String toString() {
		return "GooglefitDate [start_time=" + start_time + ", end_time=" + end_time + ", step=" + step + ", distance="
				+ distance + "]";
	}
	
	
	
}
