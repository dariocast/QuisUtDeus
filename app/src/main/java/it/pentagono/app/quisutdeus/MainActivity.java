package it.pentagono.app.quisutdeus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<Incontro> incontri;
    IncontroAdapter adapter;
    ListView lv_lista;

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm!=null?cm.getActiveNetworkInfo():null;
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Controlla la connessione Internet!")
                    .setTitle("Internet assente")
                    .setCancelable(true)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            recreate();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            populateView();
        }

    }

    public void populateView() {
        lv_lista = findViewById(R.id.lv_lista);

        new Getter().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.info_message)
                    .setIcon(R.mipmap.quisutdeus_logo)
                    .setTitle(R.string.action_info)
                    .setCancelable(false)
                    .setNeutralButton("Indietro", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();
        }

        return super.onOptionsItemSelected(item);
    }
    class Getter extends AsyncTask<Void, Void, ArrayList<Incontro>> {
        private ProgressDialog dialog;

        static final String url= "http://dariocast.altervista.org/quisutdeus/lista_json.php";

        public Getter() {
            this.dialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Caricamento...");
            this.dialog.show();
        }

        @Override
        protected ArrayList<Incontro> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("content-type","application/json")
                    .get()
                    .url(url)
                    .build();

            Response response = null;
            JSONArray jsonArray = null;
            ArrayList<Incontro> toReturn = null;
            try {
                response = client.newCall(request).execute();
                jsonArray = new JSONArray(response.body().string().toString());
                toReturn =  new ArrayList<>();

                for(int i = 0; i < jsonArray.length(); i++) {
                    toReturn.add(new Incontro(jsonArray.getJSONObject(i)));
                }
            } catch (JSONException e) {
                Log.e("ERROR","Quis Ut Deus - Background, Error while creating json");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("ERROR","Quis Ut Deus - Background, Error while getting body from request");
                e.printStackTrace();
            }

            return toReturn;
        }

        @Override
        protected void onPostExecute(ArrayList<Incontro> incontros) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (incontros==null) {
                dialog.setMessage("Errore nel caricamento");
                dialog.show();
            } else {
                incontri = incontros;
                adapter = new IncontroAdapter(MainActivity.this,R.layout.incontro_list_item,incontri);
                lv_lista.setAdapter(adapter);
                lv_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Incontro item = (Incontro) lv_lista.getItemAtPosition(i);
                        Intent mp3Intent = new Intent();
                        mp3Intent.setClass(MainActivity.this,StreamingMp3Player.class);
                        mp3Intent.putExtra("url",item.url);
                        mp3Intent.putExtra("incontro",item.getAsBundle());
                        startActivity(mp3Intent);
                    }
                });
            }
        }
    }
}

