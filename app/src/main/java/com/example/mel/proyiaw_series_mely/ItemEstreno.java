package com.example.mel.proyiaw_series_mely;

/**
 * Created by Caro on 05/03/2017.
 */

public class ItemEstreno {

    private String name;
    private String season;
    private String number_chap;
    private String url_image;

    public ItemEstreno(String n, String s, String n_c, String url){
        name=n;
        season=s;
        number_chap=n_c;
        url_image=url;
    }

    public void setName(String n){
        name=n;
    }

    public void setSeason(String s){
        season=s;
    }

    public void setUrl_image(String u){
        url_image=u;
    }

    public void setNumber_chap(String n_c){
        number_chap=n_c;
    }

    public String getName(){
        return name;
    }

    public String getSeason(){
        return season;
    }

    public String getNumber_chap(){
        return number_chap;
    }

    public String getUrl_image(){
        return url_image;
    }

}
