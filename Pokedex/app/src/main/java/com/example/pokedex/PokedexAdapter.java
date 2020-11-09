package com.example.pokedex;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// PokedexAdapter class represents all data in recyclerview in activity_main
// add 'implements filterable' to makes class filterable through search
public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> implements Filterable {  // extends adapter & takes viewholder
    public static class PokedexViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout containerView;  // for layout in pokedex_row
        public TextView textView;  // for textview in pokedex_row

        PokedexViewHolder(View view) {  // constructor
            super(view);
            containerView = view.findViewById(R.id.pokedex_row);  // object for pokedex_row view
            textView = view.findViewById(R.id.pokedex_row_text_view);   // object for pokedex_row_text_view view

            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pokemon current = (Pokemon) containerView.getTag();  // cast to Pokemon object that we get from setTag()
                    Intent intent = new Intent(v.getContext(), PokemonActivity.class);  // instantiate intent with context and class of intent activity
                    intent.putExtra("url", current.getUrl());
                    // intent.putExtra("caught", // use save state here to get caught bool with current.getName());
                    intent.putExtra("name", current.getName());   // pass relevant data
                    //intent.putExtra("number", current.getNumber());

                    v.getContext().startActivity(intent);   // start activity with intent on view context(
                }
            });
        }
    }

    private class PokemonFilter extends Filter {
        @Override   // filter pokemon list here
        protected FilterResults performFiltering(CharSequence constraint) {
            //Log.d(null, constraint.toString());

            FilterResults results = new FilterResults();
            if (constraint.length() > 0) {
                List<Pokemon> filteredPokemon = new ArrayList<>();
                for (int i = 0; i < pokemon.size(); i++) {
                    if (pokemon.get(i).getName().toLowerCase().contains(constraint.toString().toLowerCase().subSequence(0, constraint.length()))) {
                        filteredPokemon.add(pokemon.get(i));
                    }
                }

                results.values = filteredPokemon; // you need to create this variable!
                results.count = filteredPokemon.size();
            } else {
                results.values = pokemon; // you need to create this variable!
                results.count = pokemon.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (List<Pokemon>) results.values;
            notifyDataSetChanged();
        }
    }

//    private List<Pokemon> pokemon = Arrays.asList(
//            new Pokemon("Bulbasaur", 1),
//            new Pokemon ("Ivysaur", 2),
//            new Pokemon("Venusaur", 3)
//    );
    public List<Pokemon> pokemon = new ArrayList<>();
    public List<Pokemon> filtered = new ArrayList<>();
    private RequestQueue requestQueue;

    PokedexAdapter(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        loadPokemon();
    }

    @Override
    public Filter getFilter() {
        return new PokemonFilter();
    }

    public void loadPokemon() {
        String url = "https://pokeapi.co/api/v2/pokemon?limit=151";

        // define request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject result = results.getJSONObject(i);
                            String name = result.getString("name");
                            pokemon.add(new Pokemon(
                                    name.substring(0, 1).toUpperCase() + name.substring(1), // result.getString("name"),
                                    result.getString("url")
                            ));
                        }
                        filtered = pokemon;

                        notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("cs50", "Json error", e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("cs50", "Pokemon list error");
                }
            }
        );

        // make request
        requestQueue.add(request);
    }

    @NonNull
    @Override
    public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {  // create new view holder
        // convert pokedex_row from xml to java object in memory - inflate layout to view
        // basically setting view to pokedex_row layout(xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokedex_row, parent, false);

        return new PokedexViewHolder(view);
    }

    // when data in position of adapter comes on screen from off screen - to set values of properties created in pokedex_row
    // presumably for efficiency reasons
    @Override
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {  // position indicates row positiong in adapter
        Pokemon current = filtered.get(position); // Pokemon current = pokemon.get(position);
        holder.textView.setText(current.getName());   // sets text

        // passes pokemon object to viewholder containerview for the onClickListener function
        holder.containerView.setTag(current);
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }
}
