package com.mcajben.pokemongocalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PokemonInfoActivity extends AppCompatActivity {

    private Spinner stardustSpinner;

    private AutoCompleteTextView pokemon;
    private EditText trainerLevel;
    private EditText cp;
    private EditText hp;

    private CheckBox upGradedCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_info);

        trainerLevel = (EditText)findViewById(R.id.TrainerField);
        cp = (EditText)findViewById(R.id.CPField);
        hp = (EditText)findViewById(R.id.HPField);
        stardustSpinner = (Spinner)findViewById(R.id.stardustSpinner);
        upGradedCheck = (CheckBox)findViewById(R.id.UpgradedCheck);

        pokemon = (AutoCompleteTextView)findViewById(R.id.Pokemon);
        String[] pokemonNames = getResources().getStringArray(R.array.pokemonNames);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pokemonNames);
        pokemon.setAdapter(adapter);

    }

    @SuppressWarnings("unused")
    public void onSubmit(View v) {

        IVDatabase.reset();
        {
            String[] pokemonNames = getResources().getStringArray(R.array.pokemonNames);
            for (int i = 0; i < pokemonNames.length; i++) {
                if (pokemon.getText().toString().equals(pokemonNames[i])) {
                    IVDatabase.pokemonDex = i + 1;
                    break;
                }
            }
        }
        if (IVDatabase.pokemonDex == -1) {
            Toast.makeText(getApplicationContext(), "Not a valid Pokemon", Toast.LENGTH_SHORT).show();
            return; // Escape
        }
        int[] preEvStats = Variables.getBaseIV(IVDatabase.pokemonDex);

        boolean knowCP = true;
        boolean knowHP = true;
        boolean knowSD = true;


        int curCP = 0;
        int curHP = 0;
        int minLevel;
        int maxLevel;

        try {
            IVDatabase.maxLevel = Math.min(Integer.parseInt(trainerLevel.getText().toString()) * 2 + 3, 80);
        } catch (NumberFormatException e) {
            IVDatabase.maxLevel = 80;
        }
        maxLevel = IVDatabase.maxLevel;
        try {
            curCP = Integer.parseInt(cp.getText().toString());
        } catch (NumberFormatException e) {
            knowCP = false;
        }
        try {
            curHP = Integer.parseInt(hp.getText().toString());
        } catch (NumberFormatException e) {
            knowHP = false;
        }
        minLevel = stardustSpinner.getSelectedItemPosition();
        if (minLevel == 0) {
            knowSD = false;
            minLevel = 1;
        }
        else {
            minLevel *= 4;
            minLevel -= 3;
            maxLevel = minLevel + 4;
        }
        if (upGradedCheck.isChecked()) {
            IVDatabase.poweredUp = true;
        }

        for (int lvl = minLevel; lvl < maxLevel && (lvl < minLevel + 4 || !knowSD); lvl++) {
            if (IVDatabase.poweredUp || lvl % 2 == 1) {
                double cpm = Variables.getCPM(lvl);
                double cpm2 = Math.pow(cpm, 2) / 10;
                for (int st = 0; st < 16; st++) {
                    int thisHP = Math.max((int) (cpm * (st + preEvStats[0])), 10);
                    if (!knowHP || thisHP == curHP) {
                        for (int at = 0; at < 16; at++) {
                            for (int df = 0; df < 16; df++) {
                                if (!knowCP || Variables.toCP(preEvStats, st, at, df, cpm2) == curCP) {
                                    IVDatabase.add(lvl, st, at, df);
                                }
                            }
                        }
                    }
                }
            }
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
