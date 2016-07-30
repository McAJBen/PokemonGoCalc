package com.mcajben.pokemongocalculator;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

class IVDatabase { // TODO find better way to store

    // level st at df

    static int pokemonDex;
    static int maxLevel;
    static boolean poweredUp = false;
    private static final ArrayList<int[]> list = new ArrayList<>();

    public static void add(int level, int stamina, int attack, int defence) {
        list.add( new int[]{level, stamina, attack, defence});
    }

    public static void levelUp() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i)[0]++;
            if (list.get(i)[0] >= maxLevel) {
                list.remove(i);
                i--;
            }
        }
    }

    public static void removeNotAtLevel() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i)[0] + 1 != maxLevel) {
                list.remove(i);
                i--;
            }
        }
    }

    public static int size() {
        return list.size();
    }

    public static int getHP(int index, int[] stats) {
        int[] ivs = list.get(index);
        double cpm = Variables.getCPM(getLvl(index));
        return Math.max((int) (cpm * (stats[0] + ivs[1])), 10);
    }

    public static int getCP(int index, int[] stats) {
        int[] ivs = list.get(index);
        return Variables.toCP(stats, ivs[1], ivs[2], ivs[3], Math.pow(Variables.getCPM(ivs[0]), 2) / 10);
    }

    public static int getNextLevelCP(int index, int[] stats) {
        int[] ivs = list.get(index);
        return Variables.toCP(stats, ivs[1], ivs[2], ivs[3], Math.pow(Variables.getCPM(ivs[0] + 1), 2) / 10);
    }

    public static int getLvl(int index) {
        return list.get(index)[0];
    }

    public static int[] get(int index) {
        return list.get(index);
    }

    public static void reset() {
        pokemonDex = -1;
        maxLevel = 80;
        poweredUp = false;
        list.clear();
    }

    public static void remove(int i) {
        list.remove(i);
    }


    public static String getPokemonName(View v) {
        String[] pokemonNames = v.getResources().getStringArray(R.array.pokemonNames);
        return pokemonNames[pokemonDex - 1];
    }

    public static String getCP() {
        int beginCP = getCP(0, Variables.getBaseIV(pokemonDex));
        int endCP = getCP(list.size() - 1, Variables.getBaseIV(pokemonDex));
        if (beginCP == endCP) {
            return beginCP + "";
        }
        else {
            return getCP(list.size() / 2, Variables.getBaseIV(pokemonDex)) + "?";
        }

    }
}
