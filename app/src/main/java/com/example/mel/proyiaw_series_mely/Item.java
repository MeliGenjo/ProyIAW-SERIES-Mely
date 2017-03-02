package com.example.mel.proyiaw_series_mely;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by tb_laota on 9/21/2015.
 */
public class Item implements Comparable<Item> {



    private int id;
    private String title,image;
    private int year;
    private double rate;
    private ArrayList<String> genre;

    public int getId() {return id;   }

    public void setId(int id) { this.id = id;  }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

    public Item(){}

    // ************ comparadores para ordenar **********************

    //ordenar por nombre de la serie
    @Override
    public int compareTo(Item another) {
        return title.compareTo(another.getTitle());
    }

    //ordenar por año de lanzamiento
    private static final Comparator<Item> compAnio = new Comparator<Item>() {
        @Override
        public int compare(Item it1, Item it2) {
            int a1 =it1.getYear();
            int a2=it2.getYear();
            if (a1<a2)
                return 1;
            else
            if (a1>a2)
                return -1;
            else
                return 0;
        }
    };

    public static Comparator<Item> comparatorAnio(){
        return compAnio;
    }

    // ordenar por puntaje
    private static final Comparator<Item> compPtje = new Comparator<Item>() {
        @Override
        public int compare(Item it1, Item it2) {
            double a1 =it1.getRate();
            double a2=it2.getRate();
            if (a1<a2)
                return 1;
            else
            if (a1>a2)
                return -1;
            else
                return 0;
        }
    };
    public static Comparator<Item> comparatorPtje(){
        return compPtje;
    }

    //  ordenar por género, se ordena solo por el primer elemento del array genero
    private static final Comparator<Item> compGenero = new Comparator<Item>() {
        @Override
        public int compare(Item it1, Item it2) {
            String genero1 = " ";
            String genero2 = " ";
            if (it1.getGenre().size()>0)
                genero1= it1.getGenre().get(0);
            if (it2.getGenre().size()>0)
                genero2 = it2.getGenre().get(0);

            return genero1.compareTo(genero2);

        }
    };

    public static Comparator<Item> comparatorGenero(){
        return compGenero;
    }

    //ordeno primero series genero drama
    //
    private static final Comparator<Item> compGeneroDrama = new Comparator<Item>() {
        @Override
        public int compare(Item it1, Item it2) {
            String genero1 = " ";
            String genero2 = " ";
            if (it1.getGenre().size()>0)
                genero1= it1.getGenre().get(0);
            if (it2.getGenre().size()>0)
                genero2 = it2.getGenre().get(0);

            return genero1.compareTo(genero2);
        }
    };

    public static Comparator<Item> comparatorGeneroDrama(){
        return compGeneroDrama;
    }
}
