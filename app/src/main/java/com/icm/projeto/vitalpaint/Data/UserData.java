package com.icm.projeto.vitalpaint.Data;

import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bruno Silva on 14/11/2017.
 */
//Contém os dados do utilizador logado para acesso rápido dentro do projeto
public class UserData {
    public static String NAME;
    public static String USERNAME;
    public static String EMAIL;
    public static int nMatchPlayed;
    public static int nVictories;
    public static List<String> locationsPlayed;

    public static void setProfilePic(Bitmap profilePic) {
        UserData.profilePic = profilePic;
    }

    public static void setHeaderPic(Bitmap headerPic) {
        UserData.headerPic = headerPic;
    }

    public static Bitmap profilePic;
    public static Bitmap headerPic;
    private DatabaseReference dbData;

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

    public static void setNAME(String NAME) {
        UserData.NAME = NAME;
    }

    public static void setUSERNAME(String USERNAME) {
        UserData.USERNAME = USERNAME;
    }

    public static void setEMAIL(String EMAIL) {
        UserData.EMAIL = EMAIL;
    }

    public static void setnMatchPlayed(int nMatchPlayed) {
        UserData.nMatchPlayed = nMatchPlayed;
    }

    public static void setnVictories(int nVictories) {
        UserData.nVictories = nVictories;
    }

    public static void setLocationsPlayed(List<String> locationsPlayed) {
        UserData.locationsPlayed = locationsPlayed;
    }
}
