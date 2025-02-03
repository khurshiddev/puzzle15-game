package com.uzb.puzzle_15_complete;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LocalStorage {
    private static LocalStorage instance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    List<Integer> ratingList = new ArrayList<>();

    public LocalStorage(Context context) {
        this.preferences = context.getSharedPreferences("RatingTop", MODE_PRIVATE);

        this.editor = preferences.edit();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new LocalStorage(context);
        }
    }

    public static LocalStorage getInstance() {
        return instance;
    }

    public void setHistory(int score) {
        setArray(ratingList);
        ratingList.add(score);
        int len = Math.min(ratingList.size(), 3);
        StringBuilder st = new StringBuilder();
        ratingList.sort(Comparator.comparingInt(o -> o));
        for (int i = 1; i <= len; i++) {
            st.append(i).append(". Steps: ").append(ratingList.get(i - 1)).append("\n");
        }
        editor.putString("rating", st.toString()).apply();

    }

    private void setArray(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (Integer i : list) {
            sb.append(i).append("#");
        }
        editor.putString("mass", sb.toString()).apply();
    }

    public void getArray() {
        String st = preferences.getString("mass", "");
        if (st.isEmpty()) {
            ratingList = new ArrayList<>();
        } else {
            String[] stmass = st.split("#");
            List<Integer> list = new ArrayList<>();
            for (String s : stmass) {
                list.add(Integer.parseInt(s));
            }
            ratingList = list;
        }
    }





    public String getHistory() {

        return preferences.getString("rating", "");
    }
}
