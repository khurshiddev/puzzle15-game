package com.uzb.puzzle_15_complete;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.uzb.puzzle_15_complete.models.MyCoordinate;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppCompatButton[][] buttons;
    private List<Integer> numbers;
    private TextView scoreText;
    private Chronometer chronometer;
    private ImageButton imageButton;
    private Button restartButton;
    private Button result;
    private View levelLayout;
    private LinearLayout container;
    private MediaPlayer sound1;
    private MediaPlayer sound2;
    static final int N = 4;
    int emptyX = 0;
    int emptyY = 0;
    int LEVEL = 4;
    int count;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        findViewById(R.id.btnRating).setOnClickListener(v -> {
//
//        });
//localStorage = LocalStorage.getInstance();
//loadViews();
//if(getIntent().getBooleanExtra(""))
        buttons = new AppCompatButton[4][4];

        container = findViewById(R.id.container); // Bu qatorni qo'shish kerak!
        container.setVisibility(GONE);
        localStorage = LocalStorage.getInstance();

        chronometer = findViewById(R.id.timeChronometer);
        scoreText = findViewById(R.id.tvScore);
        setLevel(4);
    }

@Override
protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString("SCORE", scoreText.getText().toString());
//    outState.putInt("score", Integer.parseInt(scoreTv.getText().toString()));
    outState.putLong("time", chronometer.getBase());
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
            sb.append(buttons[i][j].getText());
            sb.append("#");
        }
    }
    outState.putString("mat", sb.toString());
}

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scoreText.setText(savedInstanceState.getString("SCORE"));
        chronometer.setBase(savedInstanceState.getLong("time"));
        chronometer.start();
        String[] s = savedInstanceState.getString("mat", "").split("#");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (s[i * 4 + j].equals("0")) {
                    buttons[i][j].setText("0");
                    buttons[i][j].setVisibility(INVISIBLE);
                    emptyX = i;
                    emptyY = j;
                } else {
                    buttons[i][j].setVisibility(VISIBLE);
                    buttons[i][j].setText(s[i * 4 + j]);
                }
            }
        }
    }


    private void setLevel(int level) {
        LEVEL = level;
        container.setVisibility(VISIBLE);
        if (scoreText == null) {
            scoreText = findViewById(R.id.tvScore);
        }

        // Ensure chronometer is initialized
        if (chronometer == null) {
            chronometer = findViewById(R.id.timeChronometer);
        }
        loadViews(); // barcha viewlarni topib olinadi
        loadData(); // barcha dastur boshlanishida kerak bo'ladigan datalarni yuklab olinadi
        loadDataToView();// datalarni endi viewga yuklaymiz
        setActions();// barcha clickable hususiyati bor viewlarga click methodini yozamiz

    }


    private void setActions() {
        imageButton.setOnClickListener(v -> {
            finish();
            sound1.start();
        });
        restartButton.setOnClickListener(v -> {
            restart();
            sound1.start();

        });
        chronometer.start();
        // GamButton Actions
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                buttons[i][j].setTag(new MyCoordinate(i, j));
                buttons[i][j].setOnClickListener(view -> {
                    AppCompatButton temp = (AppCompatButton) view;
                    MyCoordinate myCoordinate = (MyCoordinate) temp.getTag();
                    checkCanMove(myCoordinate.x, myCoordinate.y);
                });
            }
        }

    }

    private void loadViews() {

        imageButton = findViewById(R.id.btnBack);
        scoreText = findViewById(R.id.tvScore);
        chronometer = findViewById(R.id.timeChronometer);
        restartButton = findViewById(R.id.btnRestart);
        sound1 = MediaPlayer.create(this, R.raw.sound_1);
        sound2 = MediaPlayer.create(this, R.raw.sound_2);

//        container.removeAllViews();// buni bir ko'ramiz

        buttons = new AppCompatButton[4][4];
        for (int i = 0; i < 4; i++) {
            LinearLayout row = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_row, container, false);
            for (int j = 0; j < 4; j++) {
                AppCompatButton button = (AppCompatButton) LayoutInflater.from(this).inflate(R.layout.item_button, row, false);
                row.addView(button);
                buttons[i][j] = button;
            }
            container.addView(row);
        }
    }

    private void loadData() {// hamma datalarni shu yerda yuklab olamiz
        count = 0;
        numbers = new ArrayList<>(16);
        for (int i = 1; i < LEVEL * LEVEL; i++) {
            numbers.add(i);
        }
        setShuffleData();
        numbers.add(0); // bu har doim bo'sh katak past o'ng tomonda chiqishi uchun
    }

    private void loadDataToView() {

        scoreText.setText(String.valueOf(count));
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                if (numbers.get(i * LEVEL + j) == 0) {
                    buttons[i][j].setVisibility(INVISIBLE);
                    emptyX = i;
                    emptyY = j;
                } else {
                    buttons[i][j].setVisibility(VISIBLE);

                }
                buttons[i][j].setText(String.valueOf(numbers.get((i * LEVEL + j))));
            }
        }
        chronometer.setBase(SystemClock.elapsedRealtime());
    }


    private void setShuffleData() {
        Collections.shuffle(numbers);
    }

    private void restart() {
        loadData();
        loadDataToView();
    }


    private void checkCanMove(int x, int y) {
        if ((Math.abs(emptyX - x) == 1 && emptyY == y) || (Math.abs(emptyY - y) == 1 && emptyX == x)) {
            buttons[emptyX][emptyY].setVisibility(VISIBLE);
            buttons[emptyX][emptyY].setText(buttons[x][y].getText());
            buttons[x][y].setText("0");
            buttons[x][y].setVisibility(INVISIBLE);
            emptyX = x;
            emptyY = y;
            count++;
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound_1);
            SoftReference<MediaPlayer> softReference = new SoftReference<>(mediaPlayer);
            softReference.get().start();
            scoreText.setText(String.valueOf(count));
            if (emptyX == LEVEL - 1 && emptyY == LEVEL - 1) {
                if (gameOver()) {
                    Toast.makeText(MainActivity.this, "Win", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean gameOver() {
        for (int i = 0; i < LEVEL; i++) {
            for (int j = 0; j < LEVEL; j++) {
                if ((i * LEVEL + j) == LEVEL * LEVEL - 1) {
                    localStorage.setHistory(count);
                    return true;
                }
                if (Integer.parseInt(buttons[i][j].getText().toString()) != (i * LEVEL + j + 1)) {
                    return false;
                }
            }
        }
        if (check()) {
            Toast.makeText(MainActivity.this, "Win", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    private boolean check() {
        int[][] puzzle = new int[LEVEL][LEVEL];
        for (int i = 0; i < LEVEL; i++) {
            for (int j = 0; j < LEVEL; j++) {
                puzzle[i][j] = numbers.get(i * 4 + j);
            }

        }
        return isSolvable(puzzle);

    }

    static boolean isSolvable(int[][] puzzle) {
        int[] arr = new int[N * N];
        int k = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                arr[k++] = puzzle[i][j];

            }

        }
        int invCount = getInvCount(arr);

        if (N % 2 == 1) {
            return invCount % 2 == 0;
        } else {
            int pos = findXPosition(puzzle);
            if (pos % 2 == 1) {
                return invCount % 2 == 0;
            } else {
                return invCount % 2 == 1;
            }
        }
    }

    static int getInvCount(int[] arr) {
        int inv_count = 0;
        for (int i = 0; i < N * N - 1; i++) {
            for (int j = i + 1; j < N * N; j++) {
                if (arr[j] != 0 && arr[i] != 0 && arr[i] > arr[j]) {
                    inv_count++;
                }

            }

        }
        return inv_count;
    }

    static int findXPosition(int[][] puzzle) {
        for (int i = N - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (puzzle[i][j] == 0) {
                    return i - 1;
                }

            }

        }
        return -1;
    }


}


