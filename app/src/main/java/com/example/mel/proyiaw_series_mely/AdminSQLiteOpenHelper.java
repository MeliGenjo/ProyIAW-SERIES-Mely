package com.example.mel.proyiaw_series_mely;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    //Constructor
    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, "DBAppSeries", factory, version);
    }

    //Creamos las tablas
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table usuario (id integer primary key autoincrement, idFace integer, nombreApellido text, tema text)");
    }

    //Borrar las tablas y crear las nuevas tablas
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionPosterior) {
        db.execSQL("drop table if exists usuario");
        db.execSQL("create table usuario (id integer primary key autoincrement, idFace integer, nombreApellido text, tema text)");
    }

    public ArrayList<String> llenarLista() {

        ArrayList<String> lista = new ArrayList<>();

        SQLiteDatabase bd = this.getWritableDatabase();
        String q = "SELECT * FROM usuario";
        Cursor registros = bd.rawQuery(q,null);

        if (registros.moveToFirst()){
            //copio todos los usuarios a la lista
            do{
                lista.add(registros.getString(2));
            } while (registros.moveToNext());

        }
        return lista;
    }

    public String altaUsuario(String idF, String nom) {

        SQLiteDatabase bd = this.getWritableDatabase();
        String msj;

        //primero lo busco en la BD
        Cursor fila = bd.rawQuery("select id,nombreApellido,tema  from usuario where idFace=" + idF, null); //devuelve 0 o 1 fila

        if (fila.moveToFirst()) {  //si devolvió 1 fila, vamos al primero (que es el unico)
            //ya esta registrado en la BD
            msj = "Bievenido nuevamente :)";
        }
        else {
            //si no esta en la BD lo inserto
            String tema = "tema1";  //le estoy mandado cualquier tema, ojo aca!
            ContentValues registro = new ContentValues();  //es una clase para guardar datos

            registro.put("idFace", idF);
            registro.put("nombreApellido", nom);
            registro.put("tema", tema);

            bd.insert("usuario", null, registro);
            bd.close();

            msj = " Hola " + nom + " - Bievenido!";
        }
        return msj;
    }

}