package com.example.mel.proyiaw_series_mely;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String APP_TAG = "SERIES" ;
    //Constructor
    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, "DBAppSeries", factory, version);
    }

    //Creamos las tablas
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table usuario (id integer primary key autoincrement, idFace integer, nombreApellido text, tema text)");
        db.execSQL("create table usuario_serie (id integer primary key autoincrement, idSerie text,idUsuario text)");
        db.execSQL("create table serie_capitulo (id integer primary key autoincrement, idSerie text,idCapitulo text)");
    }

    //Borrar las tablas y crear las nuevas tablas
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionPosterior) {
        try {
        db.execSQL("drop table if exists usuario");
        db.execSQL("create table usuario (id integer primary key autoincrement, idFace integer, nombreApellido text, tema text);");
        db.execSQL("drop table if exists usuario_serie");
        db.execSQL("create table usuario_serie (id integer primary key autoincrement, idSerie text,idUsuario text);");
        db.execSQL("drop table if exists serie_capitulo");
        db.execSQL("create table serie_capitulo (id integer primary key autoincrement, idSerie text,idCapitulo text);");

        }catch(SQLiteException e){
            String  error =e.getMessage();
            Log.d(APP_TAG,error);}
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

    public ArrayList<String> obtenerSeries(String usuario) {
        ArrayList<String> listaSeries = new ArrayList<>();
        SQLiteDatabase bd = this.getWritableDatabase();
        String q = "select idSerie from usuario_serie where idUsuario="+usuario;
        Cursor registros = bd.rawQuery(q,null);
            if (registros.moveToFirst()) {
                //Log.e("INFO ", registros.getColumnName(0));
                //copio todos los usuarios a la lista
                do {
                    listaSeries.add(registros.getString(0));  ///// -------------- no entiendo por que en 0!! tendria que ser 1 pero no es asi :/ [mely]

                } while (registros.moveToNext());
            }
        return listaSeries;
    }

    public boolean guardarFavoritas(String idUsuario,String idSerie){
        SQLiteDatabase bd = this.getWritableDatabase();
        try {
            bd.execSQL("INSERT INTO usuario_serie(idSerie ,idUsuario) values (\""+idSerie+"\","+idUsuario+")");
        }catch(SQLiteException e){
           String  error =e.getMessage();
            Log.d(APP_TAG,error);
            return false;
        }
            return true;

    }
}