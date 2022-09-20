package com.example.pokedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.pokedex.models.Pokemon;
import com.example.pokedex.models.PokemonRespuesta;
import com.example.pokedex.pokeapi.PokeapiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "POKEDEX";
    private int offset;
    private Retrofit retrofit;

    private RecyclerView recyclerView;
    private ListaPokemonAdapter listaPokemonAdapter;

    private boolean aptoParaCargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclearView);
        listaPokemonAdapter = new ListaPokemonAdapter(this);
        recyclerView.setAdapter(listaPokemonAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy>0){
                    int visibleIteamCount = layoutManager.getChildCount();
                    int totalIteamCount = layoutManager.getItemCount();
                    int pastVisibleIteams = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if(aptoParaCargar){
                        if((visibleIteamCount +pastVisibleIteams)>=totalIteamCount){
                            Log.i(TAG,"Llegamos al final");

                            aptoParaCargar = false;
                            offset +=20;
                            obtenerDatos(offset);
                        }
                    }
                }

            }
        });
        retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        aptoParaCargar = true;
        offset = 0;
        obtenerDatos(offset);
    }

    private  void obtenerDatos(int offset){
        PokeapiService service = retrofit.create(PokeapiService.class);
        Call<PokemonRespuesta>pokemonRespuestaCall = service.obtenerListaPokemon(20,offset);

        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                aptoParaCargar = true;
                if(response.isSuccessful()){
                    PokemonRespuesta pokemonRespuesta = response.body();
                    ArrayList<Pokemon>listaPokemon = pokemonRespuesta.getResults();

                   listaPokemonAdapter.adicionarListaPokemon(listaPokemon);
                }else{
                    Log.e(TAG,"onResponse: "+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                aptoParaCargar = true;
                Log.e(TAG,"onFailere"+t.getMessage());
            }
        });
    }
}