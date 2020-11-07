package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

public class PokemonActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private String url;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);  // set view to activity_pokemon layout(xml)

        // intent is the object on which intended action is to happen
        url = getIntent().getStringExtra("url");
//        String name = getIntent().getStringExtra("name");
//        int number = getIntent().getIntExtra("number", 0);

        // get view element
        nameTextView = findViewById(R.id.pokemon_name);   // get name view from activity_pokemon
        numberTextView = findViewById(R.id.pokemon_number);  // get number view from activity_number
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);

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
                            nameTextView.setText(response.getString("name"));
                            numberTextView.setText(String.format("#%03d", response.getInt("id")));

                            JSONArray typeEntries = response.getJSONArray("types");
                            for (int i = 0; i < typeEntries.length(); i++) {
                                JSONObject typeEntry = typeEntries.getJSONObject(i);
                                int slot = typeEntry.getInt("slot");
                                String type = typeEntry.getJSONObject("type").getString("name");

                                if (slot == 1) {
                                    type1TextView.setText(type);
                                } else if (slot == 2) {
                                    type2TextView.setText(type);
                                }
                            }

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
    }
}