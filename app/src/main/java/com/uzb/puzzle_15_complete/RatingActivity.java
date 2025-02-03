package com.uzb.puzzle_15_complete;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RatingActivity extends AppCompatActivity {
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        localStorage = LocalStorage.getInstance();
        TextView text = findViewById(R.id.ratingTv);
        text.setText(localStorage.getHistory());

        findViewById(R.id.ratingBackBtn).setOnClickListener(v->{
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
        });

    }
}