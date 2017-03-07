package com.example.mel.proyiaw_series_mely;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static java.net.Proxy.Type.HTTP;

/**
 * Created by Gimena on 26/02/2017.
 */

public class envioNotificaciones {
     private JsonArrayRequest jsonArrayRequest1;
    private JsonArrayRequest jsonArrayRequest;
    public static final String TAG = "NOTIFICACIONES";
    private ProgressDialog dialog;
    private MainActivity mainActivity;
    public String idserie;


    public void controlNotificaciones(MainActivity mainActivity, String $usuario){
       this.mainActivity =mainActivity;
    AdminSQLiteOpenHelper sql= new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);
        //obtengo las series asociadas al usuario
       ArrayList<String> listSeries= sql.obtenerSeries($usuario);
     if (listSeries!=null) {
       for (String idSerie : listSeries) { //recorro las series pra ver si hay notificaciones que mandar
              nuevosCapitulos(idSerie);
              }
       }
    }
    public String nuevosCapitulos(String idSerie)
    {
        idserie=idSerie;
        String nombS=obtenerNombreSerie(idSerie);
        AppController.getmInstance().addToRequesQueue(jsonArrayRequest1);
        Log.d(TAG,"id: "+idSerie+" nombS "+nombS);
        //controlo si la fecha de estreno de la ultima temporada es hoy o 3 dias mas adelante
        for(int i=0; i<3;i++){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR,i);
            Date date = calendar.getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String fecha = df.format(date);
            String url="http://api.tvmaze.com/shows/"+idSerie+"/episodesbydate?date="+fecha;
            llamarJson(url,nombS);
            AppController.getmInstance().addToRequesQueue(jsonArrayRequest);
      } return null;
    }

    private String obtenerNombreSerie(String idSerie) {
        final String[] nombre = new String[1];
        String url="http://api.tvmaze.com/shows/"+idSerie;

        jsonArrayRequest1=new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //parsing json
                Log.d(TAG,"nombre serie  "+  nombre[0]);
                Log.d(TAG,"X ENTRAR AL FOR Ç");
                for(int i=0;i<response.length();i++){
                    Log.d(TAG,"ENTRO");
                    try{
                        JSONObject obj=response.getJSONObject(i);
                        nombre[0] =  (String) obj.getString("name");
                        Log.d(TAG,"nombre serie  "+  nombre[0]);
                    }catch(JSONException ex){
                        Log.d(TAG,"error nombre serie  ");
                        ex.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"ERROR en request");
            }
        });
        return nombre[0];
    }



    private String llamarJson(String url, final String nombS){
        final String nombreS=nombS;
        final String[] titulo = {""};
        final String[] subtitulo = {""};
        jsonArrayRequest=new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                hideDialog();
                //parsing json
                Log.d(TAG,"ENTRO JSON "+ response.length());
                for(int i=0;i<response.length();i++){
                    try{
                         titulo[0] ="";
                         subtitulo[0] ="";
                        JSONObject obj=response.getJSONObject(i);
                        String nombre =  (String) obj.getString("name");
                        Log.d(TAG,"no hay extreno" +nombre);
                        if (nombre.equals("Not Found")){
                            Log.d(TAG,"no hay extreno");
                        }else{
                            Log.d(TAG,"hay extreno");
                            Integer temporada=  (Integer) obj.getInt("season");
                            Integer nroCap=  (Integer) obj.getInt("number");
                            String subt= obj.getString("summary");
                            if (nombreS!=null){
                                titulo[0] =nombreS+" "+nroCap+"x"+temporada+" "+nombre;
                                subtitulo[0] =subt;
                            }else{
                                titulo[0] = "Próximo estreno!";
                                subtitulo[0] =nroCap+"x"+temporada+" "+nombre;
                            }
                            envioNotificaciones(mainActivity, titulo[0], subtitulo[0]);
                        }

                    }catch(JSONException ex){
                        ex.printStackTrace();
                    }
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"ERROR");
            }
        });
        return titulo[0];
     }
    public void hideDialog(){
        if(dialog !=null){
            dialog.dismiss();
            dialog=null;
        }
    }


    private void envioNotificaciones(MainActivity mainActivity,String titulo,String subtitulo) {
        NotificationCompat.Builder mBuilder;
        if (subtitulo==null){
            subtitulo=titulo;
        }
        NotificationManager mNotifyMgr =(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        int icono = R.mipmap.icono;
        Intent intent = new Intent(mainActivity, CapitulosActivity.class);
        intent.putExtra("idserie",idserie);
        intent.putExtra("esFavorito",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity, 0,intent, 0);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.notificacion)
                .setContentTitle(titulo)
                .setContentText(subtitulo)
                .setVibrate(new long[] {100, 250, 100, 500})
                .setAutoCancel(true);
        mNotifyMgr.notify(1, mBuilder.build());


    }
}
