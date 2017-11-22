package com.icm.projeto.vitalpaint.Data;

import java.io.Serializable;

/**
 * Created by Bruno Silva on 22/11/2017.
 */

public class GameDate implements Serializable{

    private int dia;
    private int mes;
    private int horas;
    private int minutos;

    public GameDate(int dia, int mes, int horas, int minutos) {
        this.dia = dia;
        this.mes = mes;
        this.horas = horas;
        this.minutos = minutos;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    @Override
    public String toString() {
        return dia+"/"+mes+", "+horas+":"+minutos;
    }
}