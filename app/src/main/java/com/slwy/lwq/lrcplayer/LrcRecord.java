package com.slwy.lwq.lrcplayer;

public class LrcRecord {
    private boolean display;
    private int startTime;
    private int stopTime;
    private String lrcText;

    public LrcRecord(boolean display, int startTime, int stopTime, String lrcText){
        this.display = display;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.lrcText = lrcText;
    }

    public boolean getDisplay() {
        return display;
    }
    public int getStartTime() {
        return startTime;
    }
    public int getStopTime() {
        return stopTime;
    }
    public String getLrcText() {
        return lrcText;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
    public void setStopTime(int stopTime) {
        this.stopTime = stopTime;
    }
    public void setLrcText(String lrcText) {
        this.lrcText = lrcText;
    }
}
