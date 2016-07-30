package com.mcajben.pokemongocalculator;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SaveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
    }

    public void onDefault(View v) {
        EditText nickname = (EditText) findViewById(R.id.NicknameField);
        nickname.setText(String.format(Locale.ENGLISH, "%s-%s", IVDatabase.getCP(), IVDatabase.getPokemonName(v)));
    }

    public void onSave(View v) {
        String nickname;
        {
            EditText nicknameField = (EditText) findViewById(R.id.NicknameField);
            if (nicknameField.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Not a valid Nickname", Toast.LENGTH_SHORT).show();
                return; // Escape
            }
            nickname = nicknameField.getText().toString();
        }

        SharedPreferences mPrefs = getSharedPreferences("saves", 0);
        SharedPreferences.Editor edit = mPrefs.edit();
        Set<String> names = mPrefs.getStringSet("nickNames", null);
        Set<String> tables = mPrefs.getStringSet("tableNames", null);
        Set<String> pokemonDex = mPrefs.getStringSet("PokemonDex", null);
        int tableInt = mPrefs.getInt("NextTableName", 0);
        edit.putInt("NextTableName", tableInt + 1);

        if (names == null) {
            names = new LinkedHashSet<String>();
            tables = new LinkedHashSet<String>();
            pokemonDex = new LinkedHashSet<String>();
        }

        names.add(nickname);
        String tableName = toTableName(tableInt);
        tables.add(tableName);
        pokemonDex.add("" + IVDatabase.pokemonDex);
        edit.putStringSet("nickNames", names);
        edit.putStringSet("tableNames", tables);
        edit.putStringSet("PokemonDex", pokemonDex);
        edit.commit();

        DatabaseOperations DB = new DatabaseOperations(this);
        DB.createTable(tableName);

        onBackPressed();
    }

    private String toTableName(int num) {

        String ret = "";

        for (int i = 6; i >= 0; i--) {
            double div = Math.pow(26, i);
            int min = (int)(num / div);

            ret += toLetter(min);
            num -= min * div;
        }

        return ret;
    }

    private char toLetter(int num) {
        return (char) (num + 65);
    }
}
