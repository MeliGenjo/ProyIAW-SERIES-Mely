package com.example.mel.proyiaw_series_mely;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static java.net.Proxy.Type.HTTP;

/**
 * Created by Gimena on 26/02/2017.
 */

public class envioNotificaciones {
   public final static Uri urlSerie = Uri.parse("http://api.tvmaze.com/");

    public void controlNotificaciones(MainActivity mainActivity, String $usuario){

    AdminSQLiteOpenHelper sql= new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);
        //obtengo las series asociadas al usuario
       ArrayList<String> listSeries= sql.obtenerSeries($usuario);
     if (listSeries!=null) {
  /**      for (String idSerie : listSeries) { //recorro las series pra ver si hay notificaciones que mandar
         String subtitulo = "";
         String NuevaTemporada = nuevasTemporadas(idSerie); //controlo nueva temporada
         if (NuevaTemporada != null) {
             subtitulo = NuevaTemporada;
         } else { //sino hay  temporada nueva controlo nuevo capitulo
             String NuevoCapitulo = nuevosCapitulos(idSerie);
             if (NuevoCapitulo != null) {
                 subtitulo = NuevoCapitulo;
             }
         }
         if (subtitulo != "") {

                envioNotificaciones(mainActivity,subtitulo);

         }
     }*/
 }
    }
    public String nuevosCapitulos(Integer $idSerie)
    {
        http://api.tvmaze.com/shows/1/episodebynumber?season=1&number=1
        return null;
    }

    public String nuevasTemporadas(Integer $idSerie)
    {
        http://api.tvmaze.com/shows/1/seasons
        return null;
    }

    private void envioNotificaciones(MainActivity mainActivity,String id) {
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr =(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        int icono = R.mipmap.ic_launcher;
        Intent intent = new Intent(mainActivity, SeriesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity, 0,intent, 0);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(pendingIntent)
                .setSmallIcon(icono)
                .setContentTitle("Titulo")
                .setContentText("Hola que tal?")
                .setVibrate(new long[] {100, 250, 100, 500})
                .setAutoCancel(true);
        mNotifyMgr.notify(1, mBuilder.build());


    }
}
