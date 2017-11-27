package com.icm.projeto.vitalpaint.Data;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.Date;

/**
 * Created by young on 27/11/2017.
 */

public class GamePlayed {
    public static enum RESULT{
        WON, LOST, DRAW
    }
    private RESULT gameResult;
    private DateTime startDate;
    private GameMode gameMode;
    private LocalTime time;

    public RESULT getGameResult() {
        return gameResult;
    }

    public void setGameResult(RESULT gameResult) {
        this.gameResult = gameResult;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public GamePlayed(RESULT gameResult, DateTime startDate, GameMode gameMode, LocalTime time) {
        this.gameResult = gameResult;
        this.startDate = startDate;
        this.gameMode = gameMode;
        this.time = time;
    }
}
