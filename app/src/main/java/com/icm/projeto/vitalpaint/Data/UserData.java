package com.icm.projeto.vitalpaint.Data;

import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Silva on 14/11/2017.
 */
//Contém os dados do utilizador logado para acesso rápido dentro do projeto
public class UserData implements Serializable{
    public static UserData loggedUser;
    private String NAME;
    private String USERNAME;
    private String EMAIL;
    private int nMatchPlayed;
    private int nVictories;
    private List<String> locationsPlayed;
    public Bitmap profilePic;
    public Bitmap headerPic;

    public UserData(String name, String userName, String email){
        this.NAME = name;
        this.USERNAME = userName;
        this.EMAIL = email;
        this.nMatchPlayed = 0;
        this.nVictories = 0;
        locationsPlayed = new ArrayList<>();
    }

    public UserData(String name, String userName, String email, int nMatchPlayed, int nVictories){
        this.NAME = name;
        this.USERNAME = userName;
        this.EMAIL = email;
        this.nMatchPlayed = nMatchPlayed;
        this.nVictories = nVictories;
        locationsPlayed = new ArrayList<>();//pode ser-nos útil manter a ordem de inserção, mas não queremos elementos duplicados
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
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

    public List<String> getLocationsPlayed() {
        return locationsPlayed;
    }

    public void setLocationsPlayed(List<String> locationsPlayed) {
        this.locationsPlayed = locationsPlayed;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public Bitmap getHeaderPic() {
        return headerPic;
    }

    public void setHeaderPic(Bitmap headerPic) {
        this.headerPic = headerPic;
    }

    public UserData() {
    }

    @Override
    public String toString() {
        return "UserData{" +
                "NAME='" + NAME + '\'' +
                ", USERNAME='" + USERNAME + '\'' +
                ", EMAIL='" + EMAIL + '\'' +
                '}';
    }
}
