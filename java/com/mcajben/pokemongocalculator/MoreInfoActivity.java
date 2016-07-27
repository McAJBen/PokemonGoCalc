package com.mcajben.pokemongocalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MoreInfoActivity extends AppCompatActivity {

    private EditText cp;
    private EditText hp;
    private Spinner stardustSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        Intent intent = getIntent();

        cp = (EditText)findViewById(R.id.CPField);
        hp = (EditText)findViewById(R.id.HPField);
        stardustSpinner = (Spinner)findViewById(R.id.stardustSpinner);
        TextView stardustLbl = (TextView)findViewById(R.id.StardustLbl);

        boolean displayStardust = intent.getBooleanExtra("DisplaySD", true);
        if (!displayStardust) {
            stardustSpinner.setVisibility(View.GONE);
            if (stardustLbl != null) {
                stardustLbl.setVisibility(View.GONE);
            }
        }
    }

    @SuppressWarnings("unused")
    public void onSubmitMore(View v) {

        int[] stats = Variables.getBaseIV(IVDatabase.pokemonDex);

        boolean knowCP = true;
        boolean knowHP = true;
        boolean knowSD = true;

        int curCP = 0;
        int curHP = 0;

        int minLevel;

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
            IVDatabase.maxLevel = minLevel + 3;
        }

        for (int i = 0; i < IVDatabase.size(); i++) {
            if (knowSD) {
                int lvl = IVDatabase.getLvl(i);
                if (minLevel > lvl || lvl >= IVDatabase.maxLevel) {
                    IVDatabase.remove(i);
                    i--;
                    continue;
                }
            }

            if (knowHP) {
                int testHP = IVDatabase.getHP(i, stats);
                if (testHP != curHP) {
                    IVDatabase.remove(i);
                    i--;
                    continue;
                }
            }

            if (knowCP) {
                int testCP = IVDatabase.getCP(i, stats);
                if (testCP != curCP) {
                    IVDatabase.remove(i);
                    i--;
                }
            }
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
