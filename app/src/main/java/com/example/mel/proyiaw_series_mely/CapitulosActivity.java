package com.example.mel.proyiaw_series_mely;


import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.layout_width;
import static android.R.color.holo_purple;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.graphics.Color.WHITE;
import static com.example.mel.proyiaw_series_mely.R.id.center;


/**
 * Created by Gimena on 28/02/2017.
 */

/**formato capitulo nro/nombre/visto/-codigo*/
public class CapitulosActivity extends AppCompatActivity {

    public static final String TAG = "CAPITULOS";
    public String[][] episodio;
    public String idSerie, titulo;
    public int cantidadCapVistos;
    public int ultimaTemp;
    public ArrayList<String> lista = new ArrayList<String>();
    public LinearLayout episodios;
    private ProgressDialog dialog;
    private Adapter adapter;
    private Boolean elegirFavoritos;
    private List<Item> array = new ArrayList<Item>();
    private JsonArrayRequest jsonArrayRequest; private ListView listView;
    private Boolean favorito;
    private String vengoDe;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capitulos);
        idSerie = getIntent().getStringExtra("idserie");
        titulo=getIntent().getStringExtra("titulo");
        vengoDe= getIntent().getStringExtra("vengoDe");
        favorito= getIntent().getBooleanExtra("esFavorito",false);
Log.d(TAG,"favorito: "+favorito);
        if (favorito)//controlo que se venga de la pantalla de favoritos para permitir edicion de capitulos
            elegirFavoritos=true;
        else
            elegirFavoritos=false;
        episodios = (LinearLayout) findViewById(R.id.linearLayoutEpisodios);
        if (elegirFavoritos) {
            lista=obtenerCapitulosVistos(idSerie);
        }
        String url = "http://api.tvmaze.com/shows/" + idSerie + "/episodes";
        adapter = new Adapter(this, array);
        obtenerCapitulosSerie(idSerie, url);
        AppController.getmInstance().addToRequesQueue(jsonArrayRequest);
    }

    private void obtenerCapitulosSerie(final String idSerie, String url) {

        jsonArrayRequest=new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<String> capitulosVistos= obtenerCapitulosVistos(idSerie);
                cantidadCapVistos=capitulosVistos.size();
                    hideDialog();
                //parsing json
                int cant=response.length();
                int cantTemp =0;
                try {
                    JSONObject cantT = response.getJSONObject(cant-1);
                    cantTemp = cantT.getInt("season");
                    } catch (JSONException e) {
                    e.printStackTrace();
                }
                episodio= new String[cantTemp+1][cant+1];
                for(int i=0;i<response.length();i++){
                    try{

                        JSONObject obj=response.getJSONObject(i);
                        Integer codigo = (Integer)obj.getInt("id");
                        String nombre =  (String) obj.getString("name");
                        Integer episodioJ= (Integer) obj.getInt("number");
                        Integer temporada = (Integer) obj.getInt("season");
                        String visto;
                        if (capitulosVistos.size()!=0){ //si es vacio, no vi ninguno
                            boolean existe=existeCapitulo(capitulosVistos,String.valueOf(codigo));
                            if (existe){
                                visto="v";
                                capitulosVistos.remove(String.valueOf(codigo));
                            }else{
                                visto="f";
                            }
                        }else{
                            visto="f";
                        }
                        episodio[temporada][episodioJ] = episodioJ+" "+nombre+"-"+codigo+visto;
                             if(ultimaTemp<temporada){
                            ultimaTemp = temporada;
                        }

                    }catch(JSONException ex){
                        ex.printStackTrace();
                    }
                }
                     fillCountryTable(ultimaTemp,cant);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    private boolean existeCapitulo(String idSerie,String codigo) {
        AdminSQLiteOpenHelper BD = new AdminSQLiteOpenHelper(getApplicationContext(), null, null, 1);
       return BD.existeCapitulo(idSerie,codigo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buscar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ) {

            case R.id.volverHome:
                irPantallaPrincipal();
                return true;

            case R.id.back:
                retrocederPantalla();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void retrocederPantalla() {
        Intent intent = new Intent(this, verItemSerieActivity.class);

        intent.putExtra("titulo",titulo);
        intent.putExtra("esfavorita",favorito);
        intent.putExtra("vengoDe",vengoDe);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void irPantallaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void hideDialog(){
        if(dialog !=null){
            dialog.dismiss();
            dialog=null;
        }
    }

    /*armo la pantalla dinamicamente con lso datos obtenidos de la API*/
    void fillCountryTable(int cantTemporadas,int cantCapitulos) {
        Button b1;
        CheckBox t1;
        TextView tituloS=new TextView(this);
        TextView tv1;
        TextView porcentajeF;
        int cantTemp=cantTemporadas;
        DecimalFormat decimales = new DecimalFormat("0.00");
        double cv= cantidadCapVistos;
        double cc= cantCapitulos;
        double porcentaje =(cv*100)/cc;
        Log.d(TAG, " cantidadCapvistos "+cantidadCapVistos+" cantCapitulos "+cantCapitulos+" porcentaje  "+ decimales.format(porcentaje));
        porcentajeF =new TextView(this);
        porcentajeF.setTextSize(15);
        porcentajeF.setText(" Porcentaje visto de la serie:"+decimales.format(porcentaje)+"%");
        tituloS.setText(titulo);
        tituloS.setTextSize(30);
        tituloS.setGravity(center);
        tituloS.setTextColor(Color.parseColor("#684CDA"));
        episodios.addView(tituloS, new TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //Converting to dip unit
        int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 1, getResources().getDisplayMetrics());
      //  cantTemp = episodio.length; //obtengo cantidad de temporadas
        episodios.addView(porcentajeF, new TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //agrego los capitulos y temporadas y los eventos asociados a los mismos

        for (int nroTemp = 1; nroTemp <  cantTemp+1; nroTemp++) {
            b1 = new Button(this);
            b1.setId((nroTemp+0));
            b1.setText("Temporada " + (nroTemp));
            b1.setTextSize(15);
            b1.setGravity(Gravity.CENTER);

            b1.setWidth(layout_width);
            b1.setTypeface(null, 1);
            b1.setTextColor(Color.parseColor("#684CDA"));
            b1.setCompoundDrawablesWithIntrinsicBounds(
                    0,     //left
                    0,      //tops
                    R.drawable.list_row_bg,  //right
                    0);
            b1.setPadding(0,2,0,0);
            episodios.addView(b1, new TableLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            for (int nroEp = 1; nroEp < episodio[nroTemp].length; nroEp++) {
                 if (episodio[nroTemp][nroEp] == null) {
                          break;
                }


                if (elegirFavoritos) {
                    boolean visto = false;
                    t1 = new CheckBox(this);
                    int n = (episodio[nroTemp][nroEp]).length();
                    t1.setId((nroTemp + 0));
                    String fuevisto = (episodio[nroTemp][nroEp]).substring(n - 1);
                    if (fuevisto.equals("v")) {
                        visto = true;
                    }


                    t1.setTypeface(null, 1);
                    t1.setTextSize(15);
                    t1.setGravity(Gravity.LEFT);
                    t1.setPadding(20, 10, 10, 10);
                    t1.setVisibility(t1.GONE);
                    int index = (episodio[nroTemp][nroEp]).indexOf('-');
                    String codigo = (episodio[nroTemp][nroEp]).substring((index + 1), (n - 1));
                    String texto = (episodio[nroTemp][nroEp]).substring(0, (index));
                    t1.setTag(codigo);
                    t1.setText(texto);
                    t1.setChecked(visto);
                    episodios.addView(t1, new TableLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    t1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            actualizarEstado(compoundButton.getText(), b, (String) compoundButton.getTag());
                            Log.d(TAG, "check clicleado: " + compoundButton.getText() + " valor " + b);
                        }
                    });
                }
            else{
                    tv1 = new TextView(this);
                    int n = (episodio[nroTemp][nroEp]).length();
                    tv1.setId((nroTemp + 0));
                    tv1.setTypeface(null, 1);
                    tv1.setTextSize(15);
                    tv1.setGravity(Gravity.LEFT);
                    tv1.setPadding(20, 10, 10, 10);
                    tv1.setVisibility(tv1.GONE);
                    int index = (episodio[nroTemp][nroEp]).indexOf('-');
                    String codigo = (episodio[nroTemp][nroEp]).substring((index + 1), (n - 1));
                    String texto = (episodio[nroTemp][nroEp]).substring(0, (index));
                    tv1.setText(texto);
                    episodios.addView(tv1, new TableLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    }
            }


            b1.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Button button = (Button) v;
                    Log.d(TAG, "idBoton apretado  " + v.getId());
                    LinearLayout parent = (LinearLayout) v.getParent();
                    Log.d(TAG, "cantidad dependencias buton  " + parent.getChildCount());
                    /**
                     * It text color is VIOLETA
                     *         open the accordion of selected tab
                     *         close the accordion of remaining tab
                     * else
                     *         if text color is white
                     *         close the accordion of selected tab
                     */
                    if (button.getCurrentTextColor() == Color.parseColor("#684CDA")) { //se abre acordeon
                        for (int j = 0; j < parent.getChildCount(); j++) {
                              if (v.getId() == parent.getChildAt(j).getId())//si la temporada coincide con el id temporada del episodio lo muestro
                            {
                                button.setTextColor(WHITE);
// Change visibility
                                       parent.getChildAt(j).setVisibility(parent.getChildAt(j).VISIBLE);

// Chnage icon
                                button.setCompoundDrawablesWithIntrinsicBounds(
                                        0,     //left
                                        0,      //top
                                        R.drawable.list_row_bg,  //right
                                        0);     //bottom
                            }
                        }
                    } else { //cierro elemento
                         for (int j = 0; j < parent.getChildCount(); j++) {
                            if (v.getId() == parent.getChildAt(j).getId() && parent.getChildAt(j).getVisibility() == View.VISIBLE) {
                                button.setTextColor(Color.parseColor("#684CDA"));
                                if (button != parent.getChildAt(j)) {
                                    parent.getChildAt(j).setVisibility(parent.getChildAt(j).GONE);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void actualizarEstado(CharSequence text, boolean marcado, String codigo) {
        boolean existe = false;
        AdminSQLiteOpenHelper BD = new AdminSQLiteOpenHelper(getApplicationContext(), null, null, 1);
        if (lista.size() > 0) {
            for (int i = 0; i < lista.size(); i++) {
                int n = lista.get(i).length();
                if ((lista.get(i)).equals(codigo)) {
                    existe = true;
                    break;
                }
            }
        } else {
            Log.d(TAG, "lista vacia");
        }

        if (!existe && marcado) {

            if (BD.agregarCapitulo(idSerie, codigo)) {
              } else {
                Log.d(TAG, "fallo guardado");
            }

        } else {
            if (existe && !marcado) {

                if (BD.eliminarCapitulo(idSerie, codigo)) {
                    Log.d(TAG, "eliminado");
                } else {
                    Log.d(TAG, "fallo eliminado");
                }

            }
        }

    }

    private ArrayList<String> obtenerCapitulosVistos(String idSerie) {
        AdminSQLiteOpenHelper BD = new AdminSQLiteOpenHelper(getApplicationContext(), null, null, 1);
        return BD.obtenerCapitulosVistos(idSerie);
    }

    //===========================================================controla si existe en la lista el capitulo============================
    private boolean existeCapitulo(ArrayList<String> listaCapV, String idCapitulo){
        for (String idCapLista : listaCapV) {
            if (idCapLista.equals(idCapitulo)){
                return true;
            }
        }
        return false;


    }

  }