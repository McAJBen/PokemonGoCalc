package com.mcajben.pokemongocalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static android.view.View.*;

public class PokemonInfoActivity extends AppCompatActivity {

    private Spinner stardustSpinner;

    private AutoCompleteTextView pokemon;
    private EditText trainerLevel;
    private EditText cp;
    private EditText hp;

    private CheckBox upGradedCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //this.deleteDatabase("user_info");

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

        //setSaves();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setSaves();
    }

    private void setSaves() {
        GridLayout savedButtons = (GridLayout)findViewById(R.id.SavedButtonsLayout);
        savedButtons.removeAllViews();

        SharedPreferences mPrefs = getSharedPreferences("saves", 0);
        String[] names;
        String[] tables;
        String[] pokemonDex;
        try {
            Set<String> nSet = mPrefs.getStringSet("nickNames", null);
            names = nSet.toArray(new String[nSet.size()]);
            Set<String> tSet = mPrefs.getStringSet("tableNames", null);
            tables = tSet.toArray(new String[tSet.size()]);
            Set<String> pSet = mPrefs.getStringSet("PokemonDex", null);
            pokemonDex = pSet.toArray(new String[pSet.size()]);
        } catch (NullPointerException e) {
            return;
        }

        for (int i = 0; i < names.length; i++) {
            Button b = new Button(this);
            b.setOnClickListener(new LoadSave(tables[i],Integer.parseInt(pokemonDex[i]), this));
            b.setOnLongClickListener(new DeleteSave(tables[i], i, this));
            b.setText(names[i]);

            savedButtons.addView(b);
        }
    }

    private class DeleteSave implements OnLongClickListener {
        String table;
        int index;
        Context context;

        public DeleteSave(String s, int index, Context context) {
            table = s;
            this.index = index;
            this.context = context;
        }


        @Override
        public boolean onLongClick(View v) {


            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {

                        (new DatabaseOperations(context)).deleteTable(table);

                        String[] names;
                        String[] tables;
                        String[] pokemonDex;

                        SharedPreferences mPrefs = getSharedPreferences("saves", 0);
                        Set<String> nSet = mPrefs.getStringSet("nickNames", null);
                        names = nSet.toArray(new String[nSet.size()]);
                        Set<String> tSet = mPrefs.getStringSet("tableNames", null);
                        tables = tSet.toArray(new String[tSet.size()]);
                        Set<String> pSet = mPrefs.getStringSet("PokemonDex", null);
                        pokemonDex = pSet.toArray(new String[pSet.size()]);

                        nSet.remove(names[index]);
                        tSet.remove(tables[index]);
                        pSet.remove(pokemonDex[index]);

                        SharedPreferences.Editor edit = mPrefs.edit();

                        edit.remove("nickNames");
                        edit.remove("tableNames");
                        edit.remove("PokemonDex");

                        edit.commit();
                        edit = mPrefs.edit();

                        edit.putStringSet("nickNames", nSet);
                        edit.putStringSet("tableNames", tSet);
                        edit.putStringSet("PokemonDex", pSet);

                        edit.commit();

                        finish();
                        startActivity(getIntent());
                        return;

                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete this?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

            return true;
        }
    }

    private class LoadSave implements OnClickListener {

        String table;
        int pokemonDex;
        Context context;

        public LoadSave(String s, int dex, Context context) {
            table = s;
            pokemonDex = dex;
            this.context = context;
        }


        @Override
        public void onClick(View v) {
            IVDatabase.reset();

            Cursor Cur = (new DatabaseOperations(context)).getInformation(table);
            if (!Cur.moveToFirst()) {
                return;
            }
            do {
                IVDatabase.add(Cur.getInt(0), Cur.getInt(1), Cur.getInt(2), Cur.getInt(3));
            } while (Cur.moveToNext());
            IVDatabase.pokemonDex = pokemonDex;

            Intent intent = new Intent(context, ResultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
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
