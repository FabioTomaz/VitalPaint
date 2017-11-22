package com.icm.projeto.vitalpaint.Data;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Silva on 14/11/2017.
 */
//Contém os dados do utilizador logado para acesso rápido dentro do projeto
public class UserData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String NAME;
    private String SHORTBIO;
    private String EMAIL;
    private int nMatchPlayed;
    private int nVictories;
    private int nLosses;
    private int nDraws;

    public int getnLosses() {
        return nLosses;
    }

    public void setnLosses(int nLosses) {
        this.nLosses = nLosses;
    }

    public int getnDraws() {
        return nDraws;
    }

    public void setnDraws(int nDraws) {
        this.nDraws = nDraws;
    }

    private List<UserData> friends;
    private List<Location> locationsPlayed;

    public List<UserData> getFriends() {
        return friends;
    }

    public void setFriends(List<UserData> friends) {
        this.friends = friends;
    }

    public UserData(String name, String email){
        this.NAME = name;
        this.EMAIL = email;
        this.SHORTBIO = "Enter a Short Bio";
        this.nMatchPlayed = 0;
        this.nVictories = 0;
        locationsPlayed = new ArrayList<>();
        friends = new ArrayList<>();
    }

    public UserData(String name, String SHORTBIO, String email, int nMatchPlayed, int nVictories){
        this.NAME = name;
        this.SHORTBIO = SHORTBIO;
        this.EMAIL = email;
        this.nMatchPlayed = nMatchPlayed;
        this.nVictories = nVictories;
        locationsPlayed = new ArrayList<>();//pode ser-nos útil manter a ordem de inserção, mas não queremos elementos duplicados
    }

    @Override
    public String toString() {
        return "UserData{" +
                "NAME='" + NAME + '\'' +
                ", EMAIL='" + EMAIL + '\'' +
                '}';
    }

    public String getSHORTBIO() {
        return SHORTBIO;
    }

    public void setSHORTBIO(String SHORTBIO) {
        this.SHORTBIO = SHORTBIO;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public int getnMatchPlayed() {
        return nMatchPlayed;
    }

    public void setnMatchPlayed(int nMatchPlayed) {
        this.nMatchPlayed = nMatchPlayed;
    }

    public int getnVictories() {
        return nVictories;
    }

    public void setnVictories(int nVictories) {
        this.nVictories = nVictories;
    }

    public List<Location> getLocationsPlayed() {
        return locationsPlayed;
    }

    public void setLocationsPlayed(List<Location> locationsPlayed) {
        this.locationsPlayed = locationsPlayed;
    }

    public UserData(){

    }
}
