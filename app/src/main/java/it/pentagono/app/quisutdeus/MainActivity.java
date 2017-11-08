package it.pentagono.app.quisutdeus;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<Incontro> incontri;
    IncontroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView lv_lista = findViewById(R.id.lv_lista);

        Incontro incontro = new Incontro("titolo","data","luogo","occasione",R.drawable.celebrazione,"mediaType","url");
        new Getter().execute();

        adapter = new IncontroAdapter(MainActivity.this,R.layout.incontro_list_item,incontri);

        lv_lista.setAdapter(adapter);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class Getter extends AsyncTask<Void, Void, ArrayList<Incontro>> {

        static final String url= "http://dariocast.altervista.org/quisutdeus/lista_json.php";

        @Override
        protected ArrayList<Incontro> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = null;
            JSONArray jsonArray = new JSONArray();
            ArrayList<Incontro> toReturn = null;
            try {
                response = client.newCall(request).execute();
                jsonArray = new JSONArray(response.body().string());
                toReturn =  new ArrayList<Incontro>();

                for(int i = 0; i < jsonArray.length(); i++) {
                    toReturn.add(new Incontro(jsonArray.getJSONObject(i)));
                }
            } catch (JSONException e) {
                Log.e("ERROR","Quis Ut Deus - Background, Error while creating json");
            } catch (IOException e) {
                Log.e("ERROR","Quis Ut Deus - Background, Error while getting body from request");
            }

            return toReturn;
        }

        @Override
        protected void onPostExecute(ArrayList<Incontro> listaIncontri) {
            incontri = listaIncontri;
        }
    }
}

