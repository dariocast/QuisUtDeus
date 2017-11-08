package it.pentagono.app.quisutdeus;

import org.json.JSONException;
import org.json.JSONObject;

public class Incontro {
    String titolo;
    String data;
    String luogo;
    String occasione;
    String momento;
    String mediaType;
    String url;

    public Incontro(String titolo, String data, String luogo, String occasione, String momento, String mediaType, String url) {
        this.titolo = titolo;
        this.data = data;
        this.luogo = luogo;
        this.occasione = occasione;
        this.momento = momento;
        this.mediaType = mediaType;
        this.url = url;
    }

    public Incontro(JSONObject incontro) throws JSONException {
        this(
                incontro.getString("titolo"),
                incontro.getString("data"),
                incontro.getString("luogo"),
                incontro.getString("occasione"),
                incontro.getString("momento"),
                incontro.getString("mediaType"),
                incontro.getString("url")
        );
    }

    public JSONObject getAsJSONObject() throws JSONException {
        JSONObject incontro = new JSONObject();
        incontro.put("titolo",this.titolo);
        incontro.put("data",this.data);
        incontro.put("luogo",this.luogo);
        incontro.put("occasione",this.occasione);
        incontro.put("momento",this.momento);
        incontro.put("mediaType",this.mediaType);
        incontro.put("url",this.url);

        return incontro;
    }
}