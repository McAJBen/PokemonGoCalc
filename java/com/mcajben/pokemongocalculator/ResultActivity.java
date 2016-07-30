package com.mcajben.pokemongocalculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int[] preEvStats;
        int[] postEvStats;
        boolean hasEvolution;
        {
            preEvStats = Variables.getBaseIV(IVDatabase.pokemonDex);
            int postEvDex = Variables.getEvolutionDex(IVDatabase.pokemonDex);
            if (postEvDex == 0) {
                Button evolveButton = (Button)findViewById(R.id.EvolveButton);
                evolveButton.setEnabled(false);
                LinearLayout EvolveLayout = (LinearLayout)findViewById(R.id.EvolveLayout);
                EvolveLayout.setVisibility(View.GONE);
                postEvStats = null;
                hasEvolution = false;
            }
            else {
                postEvStats = Variables.getBaseIV(postEvDex);
                hasEvolution = true;
            }
        }
        int EvCPMin = Integer.MAX_VALUE;
        int EvCPMax = Integer.MIN_VALUE;
        int EvHPMin = Integer.MAX_VALUE;
        int EvHPMax = Integer.MIN_VALUE;
        int averageEvCP = 0;
        int averageEvHP = 0;
        int EvCount = 0;

        int PuCPMin = Integer.MAX_VALUE;
        int PuCPMax = Integer.MIN_VALUE;
        int PuHPMin = Integer.MAX_VALUE;
        int PuHPMax = Integer.MIN_VALUE;
        int averagePuCP = 0;
        int averagePuHP = 0;
        int PuCount = 0;

        int minLevel = 80;
        int minPerfect = 45;
        int maxPerfect = 0;
        int averagePerfect = 0;
        int perfectCount = 0;

        TextView lvlText = (TextView)findViewById(R.id.LevelLabel),
                stText = (TextView)findViewById(R.id.StaminaLabel),
                atText = (TextView)findViewById(R.id.AttackLabel),
                dfText = (TextView)findViewById(R.id.DefenceLabel),
                pfText = (TextView)findViewById(R.id.PerfectLabel);
        lvlText.setText("");
        stText.setText("");
        atText.setText("");
        dfText.setText("");
        pfText.setText("");

        for (int i = 0; i < IVDatabase.size(); i++) {

            if (hasEvolution) {
                EvCount++;

                int postCP = IVDatabase.getCP(i, postEvStats);
                averageEvCP += postCP;
                if (EvCPMin > postCP) {
                    EvCPMin = postCP;
                }
                if (EvCPMax < postCP) {
                    EvCPMax = postCP;
                }

                int postHP = IVDatabase.getHP(i, postEvStats);
                averageEvHP += postHP;
                if (EvHPMin > postHP) {
                    EvHPMin = postHP;
                }
                if (EvHPMax < postHP) {
                    EvHPMax = postHP;
                }
            }
            int[] ivs = IVDatabase.get(i);
            int lvl = ivs[0];
            if (lvl < minLevel) {
                minLevel = lvl;
            }
            if (lvl < IVDatabase.maxLevel) {
                PuCount++;

                int nextLvlCP = IVDatabase.getNextLevelCP(i, preEvStats);
                averagePuCP += nextLvlCP;
                if (PuCPMin > nextLvlCP) {
                    PuCPMin = nextLvlCP;
                }
                if (PuCPMax < nextLvlCP) {
                    PuCPMax = nextLvlCP;
                }

                int nextLevelHP = IVDatabase.getHP(i, preEvStats);
                averagePuHP += nextLevelHP;
                if (PuHPMin > nextLevelHP) {
                    PuHPMin = nextLevelHP;
                }
                if (PuHPMax < nextLevelHP) {
                    PuHPMax = nextLevelHP;
                }
            }

            int perfect = ivs[1] + ivs[2] + ivs[3];

            perfectCount++;
            averagePerfect += perfect;
            if (minPerfect > perfect) {
                minPerfect = perfect;
            }
            if (maxPerfect < perfect) {
                maxPerfect = perfect;
            }

            if (i < 1000) {
                if (i == 0) {
                    lvlText.append(String.format(Locale.ENGLISH, "%.1f", (ivs[0] + 1) / 2.0));
                    stText.append(String.format(Locale.ENGLISH, "%d", ivs[1]));
                    atText.append(String.format(Locale.ENGLISH, "%d", ivs[2]));
                    dfText.append(String.format(Locale.ENGLISH, "%d", ivs[3]));
                    pfText.append(String.format(Locale.ENGLISH, "%.1f%%", perfect / 0.45));
                }
                else {
                    lvlText.append(String.format(Locale.ENGLISH, "%n%.1f", (ivs[0] + 1) / 2.0));
                    stText.append(String.format(Locale.ENGLISH, "%n%d", ivs[1]));
                    atText.append(String.format(Locale.ENGLISH, "%n%d", ivs[2]));
                    dfText.append(String.format(Locale.ENGLISH, "%n%d", ivs[3]));
                    pfText.append(String.format(Locale.ENGLISH, "%n%.1f%%", perfect / 0.45));
                }
            }
        }
        if (minLevel + 1 >= IVDatabase.maxLevel) {
            Button powerUpButton = (Button)findViewById(R.id.PowerUpButton);
            powerUpButton.setEnabled(false);
        }
        TextView totalIVsText = (TextView)findViewById(R.id.TotalIVsLbl);
        if (IVDatabase.size() >= 1000) {
            totalIVsText.setText(String.format(Locale.ENGLISH, "First 1000 of %d", IVDatabase.size()));
            Button saveButton = (Button)findViewById(R.id.SaveButton);
            saveButton.setEnabled(false);
        }
        else {
            totalIVsText.setText(String.format(Locale.ENGLISH, "%d", IVDatabase.size()));
            if (IVDatabase.size() == 0) {
                Button saveButton = (Button)findViewById(R.id.SaveButton);
                saveButton.setEnabled(false);
            }
        }
        if (perfectCount > 0) {
            TextView perfectText = (TextView)findViewById(R.id.PerfectLbl);
            perfectText.setText(String.format(Locale.ENGLISH, "%.1f%% to %.1f%% Average:%.2f%%", minPerfect / 0.45, maxPerfect / 0.45, averagePerfect / perfectCount / .45));
        }

        if (PuCount > 0) {
            TextView PuCPMinText = (TextView)findViewById(R.id.PuCPMin),
                    PuCPAverageText = (TextView)findViewById(R.id.PuCPAverage),
                    PuCPMaxText = (TextView)findViewById(R.id.PuCPMax),
                    PuHPMinText = (TextView)findViewById(R.id.PuHPMin),
                    PuHPAverageText = (TextView)findViewById(R.id.PuHPAverage),
                    PuHPMaxText = (TextView)findViewById(R.id.PuHPMax);

            PuCPMinText.setText(String.format(Locale.ENGLISH, "%d", PuCPMin));
            PuCPMaxText.setText(String.format(Locale.ENGLISH, "%d", PuCPMax));
            PuCPAverageText.setText(String.format(Locale.ENGLISH, "%d", averagePuCP / PuCount));
            PuHPMinText.setText(String.format(Locale.ENGLISH, "%d", PuHPMin));
            PuHPMaxText.setText(String.format(Locale.ENGLISH, "%d", PuHPMax));
            PuHPAverageText.setText(String.format(Locale.ENGLISH, "%d", averagePuHP / PuCount));
        }
        else {
            LinearLayout PowerUpLayout = (LinearLayout)findViewById(R.id.PowerUpLayout);
            PowerUpLayout.setVisibility(View.GONE);
        }

        if (EvCount > 0) {
            TextView EvCPMinText = (TextView)findViewById(R.id.EvCPMin),
                    EvCPAverageText = (TextView)findViewById(R.id.EvCPAverage),
                    EvCPMaxText = (TextView)findViewById(R.id.EvCPMax),
                    EvHPMinText = (TextView)findViewById(R.id.EvHPMin),
                    EvHPAverageText = (TextView)findViewById(R.id.EvHPAverage),
                    EvHPMaxText = (TextView)findViewById(R.id.EvHPMax);

            EvCPMinText.setText(String.format(Locale.ENGLISH, "%d", EvCPMin));
            EvCPMaxText.setText(String.format(Locale.ENGLISH, "%d", EvCPMax));
            EvCPAverageText.setText(String.format(Locale.ENGLISH, "%d", averageEvCP / EvCount));
            EvHPMinText.setText(String.format(Locale.ENGLISH, "%d", EvHPMin));
            EvHPMaxText.setText(String.format(Locale.ENGLISH, "%d", EvHPMax));
            EvHPAverageText.setText(String.format(Locale.ENGLISH, "%d", averageEvHP / EvCount));
        }
        else {
            LinearLayout EvolveLayout = (LinearLayout)findViewById(R.id.EvolveLayout);
            EvolveLayout.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("unused")
    public void onEvolve(View v) {

        IVDatabase.pokemonDex = Variables.getEvolutionDex(IVDatabase.pokemonDex);

        Intent intent = new Intent(this, MoreInfoActivity.class);

        intent.putExtra("DisplaySD", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void onPowerUp(View v) {

        IVDatabase.levelUp();
        IVDatabase.poweredUp = true;

        Intent intent = new Intent(this, MoreInfoActivity.class);

        intent.putExtra("DisplaySD", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void onCantPowerUp(View v) {

        IVDatabase.removeNotAtLevel();

        Intent intent = new Intent(this, ResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void onSave(View v) {

        Intent intent = new Intent(this, SaveActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        onBackPressed();
    }
}
