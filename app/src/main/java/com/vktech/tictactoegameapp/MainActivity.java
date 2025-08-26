package com.vktech.tictactoegameapp;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons = new Button[3][3];
    private TextView textViewStatus;
    private TextView textViewPlayer1Score;
    private TextView textViewPlayer2Score;
    private Button buttonReset;
    private Button buttonResetScores;

    private boolean player1Turn = true;
    private int roundCount;
    private int player1Score;
    private int player2Score;
    private String player1Symbol = "X";
    private String player2Symbol = "O";
    private int[][] winningLine = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewStatus = findViewById(R.id.text_view_status);
        textViewPlayer1Score = findViewById(R.id.text_view_player1_score);
        textViewPlayer2Score = findViewById(R.id.text_view_player2_score);
        buttonReset = findViewById(R.id.button_reset);
        buttonResetScores = findViewById(R.id.button_reset_scores);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }

        buttonReset.setOnClickListener(v -> resetBoard());
        buttonResetScores.setOnClickListener(v -> resetScores());

        updateScores();
        // The initial symbol choice dialog is called from onCreate
        showSymbolChoiceDialog();
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        if (player1Turn) {
            ((Button) v).setText(player1Symbol);
        } else {
            ((Button) v).setText(player2Symbol);
        }

        roundCount++;

        if (checkForWin()) {
            // Note: The check is now on whose turn it *was*, so we check player1Turn directly.
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            // Switch turns only if the game is still ongoing
            player1Turn = !player1Turn;
            updateStatusText();
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                winningLine = new int[][]{{i, 0}, {i, 1}, {i, 2}};
                return true;
            }
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                winningLine = new int[][]{{0, i}, {1, i}, {2, i}};
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            winningLine = new int[][]{{0, 0}, {1, 1}, {2, 2}};
            return true;
        }

        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            winningLine = new int[][]{{0, 2}, {1, 1}, {2, 0}};
            return true;
        }

        winningLine = null;
        return false;
    }

    private void player1Wins() {
        Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT).show();
        player1Score++;
        updateScores();
        updateStatusText("Player 1 wins!");
        animateWinningButtons();
        disableButtons();
    }

    private void player2Wins() {
        Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show();
        player2Score++;
        updateScores();
        updateStatusText("Player 2 wins!");
        animateWinningButtons();
        disableButtons();
    }

    private void draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        updateStatusText("Draw!");
        disableButtons();
    }

    private void updateScores() {
        textViewPlayer1Score.setText(String.valueOf(player1Score));
        textViewPlayer2Score.setText(String.valueOf(player2Score));
    }

    private void updateStatusText() {
        if (player1Turn) {
            textViewStatus.setText("Player 1's turn (" + player1Symbol + ")");
        } else {
            textViewStatus.setText("Player 2's turn (" + player2Symbol + ")");
        }
    }

    private void updateStatusText(String message) {
        textViewStatus.setText(message);
    }

    private void disableButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private void resetBoard() {
        roundCount = 0;
        player1Turn = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                int color = ContextCompat.getColor(this, R.color.textColorDefault);
                buttons[i][j].setTextColor(color);
            }
        }
        // **CHANGE**: Call the symbol choice dialog every time the board is reset.
        showSymbolChoiceDialog();
    }

    private void resetScores() {
        player1Score = 0;
        player2Score = 0;
        updateScores();
        resetBoard(); // This will now also trigger the symbol choice dialog
    }

    private void animateWinningButtons() {
        if (winningLine != null) {
            for (int[] pos : winningLine) {
                Button winningButton = buttons[pos[0]][pos[1]];
                int color = ContextCompat.getColor(this, R.color.textColorAccent);
                winningButton.setTextColor(color);

                ObjectAnimator bounceAnim = ObjectAnimator.ofPropertyValuesHolder(winningButton,
                        PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f, 1f),
                        PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f, 1f));
                bounceAnim.setDuration(500);
                bounceAnim.setRepeatCount(1);
                bounceAnim.setRepeatMode(ObjectAnimator.REVERSE);
                bounceAnim.start();
            }
        }
    }

    private void showSymbolChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Your Symbol");
        builder.setMessage("Player 1, would you like to be X or O?");

        builder.setPositiveButton("X", (dialog, which) -> {
            player1Symbol = "X";
            player2Symbol = "O";
            updateStatusText();
            dialog.dismiss();
        });

        builder.setNegativeButton("O", (dialog, which) -> {
            player1Symbol = "O";
            player2Symbol = "X";
            updateStatusText();
            dialog.dismiss();
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
