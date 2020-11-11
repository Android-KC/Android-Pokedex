package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private Button catchButton;
    private ImageView imageView;
    private TextView pokemonDescription;
    private String url;
    private String name;
    private RequestQueue requestQueue;

    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            }
            catch (IOException e) {
                Log.e("cs50", "Download sprite error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // load the bitmap into the ImageView
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);  // set view to activity_pokemon layout(xml)

        // intent is the object on which intended action is to happen
        url = getIntent().getStringExtra("url");
        name = getIntent().getStringExtra("name");
//        int number = getIntent().getIntExtra("number", 0);

        // get view element
        nameTextView = findViewById(R.id.pokemon_name);   // get name view from activity_pokemon
        numberTextView = findViewById(R.id.pokemon_number);  // get number view from activity_number
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);
        catchButton = findViewById(R.id.catch_pokemon);
        imageView = findViewById(R.id.pokemon_image);
        pokemonDescription = findViewById(R.id.pokemon_description);

        if (getPreferences(Context.MODE_PRIVATE).getBoolean(name, false)) {
            catchButton.setText("Release");
        } else {
            catchButton.setText("Catch");
        }

        // request queue
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        load();

        // set element data
//        nameTextView.setText(name);
//        numberTextView.setText(String.format("#%03d", number));  // numberTextView.setText(Integer.toString(number));
    }

    public void load() {
        //String url = "https://pokeapi.co/api/v2/pokemon?limit=151";
        type1TextView.setText("");
        type2TextView.setText("");

        // define request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        nameTextView.setText(response.getString("name").substring(0, 1).toUpperCase() + response.getString("name").substring(1));
                        numberTextView.setText(String.format("#%03d", response.getInt("id")));

                        JSONArray typeEntries = response.getJSONArray("types");
                        for (int i = 0; i < typeEntries.length(); i++) {
                            JSONObject typeEntry = typeEntries.getJSONObject(i);
                            int slot = typeEntry.getInt("slot");
                            String type = typeEntry.getJSONObject("type").getString("name").substring(0, 1).toUpperCase()
                                    + typeEntry.getJSONObject("type").getString("name").substring(1);

                            if (slot == 1) {
                                type1TextView.setText(type);
                            } else if (slot == 2) {
                                type2TextView.setText(type);
                            }
                        }

                        JSONObject sprites = response.getJSONObject("sprites");
                        String front_default = sprites.getString("front_default");
                        new DownloadSpriteTask().execute(front_default);

                    } catch (JSONException e) {
                        Log.e("cs50", "Pokemon Json error", e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("cs50", "Pokemon details error");
                }
            }
        );
        requestQueue.add(request);

        String descriptionUrl = "https://pokeapi.co/api/v2/pokemon-species/" + name.toLowerCase();
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, descriptionUrl, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray descriptions = response.getJSONArray("flavor_text_entries");
                        for (int i = 0; i < descriptions.length(); i++) {
                            JSONObject defaultEntry = descriptions.getJSONObject(i);
                            if (defaultEntry.getJSONObject("language").getString("name").equals("en")) {
                                String description = defaultEntry.getString("flavor_text");
                                pokemonDescription.setText(description);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("cs50", "Pokemon Species Json error", e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("cs50", "Pokemon description error");
                }
            }
        );

        requestQueue.add(request2);
    }

    public void toggleCatch(View view) {
        // change button text to 'Catch' or 'Release'
        // also change state of pokemon caught in sharedpreferences
        boolean caught = getPreferences(Context.MODE_PRIVATE).getBoolean(nameTextView.getText().toString(), false);
        if (caught) {
            getPreferences(Context.MODE_PRIVATE).edit().putBoolean(nameTextView.getText().toString(), false).commit();
            catchButton.setText("Catch");
        } else {
            getPreferences(Context.MODE_PRIVATE).edit().putBoolean(nameTextView.getText().toString(), true).commit();
            catchButton.setText("Release");
        }
    }
}