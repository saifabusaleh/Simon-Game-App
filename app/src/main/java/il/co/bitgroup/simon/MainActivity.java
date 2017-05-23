package il.co.bitgroup.simon;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    View leftTop;
    View leftBottom;
    View rightTop;
    View rightBottom;
    TextView textView;
    final int MAX_LENGTH = 1000;
    int array_of_moves[] = new int[MAX_LENGTH];
    public int numberOfElmentsInMovesArray = 0, k = 0, numberOfClicksEachStage = 0, x, sadMusic, highScore = 0, hardness;
    public SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    Random r = new Random();
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sadMusic = sp.load(this, R.raw.sad, 1);
        leftTop = findViewById(R.id.leftTop);
        leftBottom = findViewById(R.id.leftBottom);
        rightTop = findViewById(R.id.rightTop);
        rightBottom = findViewById(R.id.rightBottom);
        textView = (TextView) findViewById(R.id.textView2);

        leftTop.setOnTouchListener(onTouch);
        leftBottom.setOnTouchListener(onTouch);
        rightBottom.setOnTouchListener(onTouch);
        rightTop.setOnTouchListener(onTouch);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String a[] = {"Easy", "Medium", "Hard", "Geek"};
        builder.setTitle("Select the level that you want play the game")

                .setItems(a, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        hardness = which;
                        textView.setText("Currect score: " + numberOfElmentsInMovesArray + "           High score: " + highScore);
                        Toast.makeText(MainActivity.this, "Welcome to Simon V1 hardness: " + hardness + ", Created by Saif", Toast.LENGTH_LONG).show();
                        //on initial start, click the playGame function after delay
                        final Runnable r = new Runnable() {
                            public void run() {
                                playGame();
                            }
                        };
                        handler.postDelayed(r, 3000);
                    }
                });
        AlertDialog myDialog = builder.create();
        myDialog.show();

    }

    View.OnTouchListener onTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.leftTop:
                        x = 1;
                        break;
                    case R.id.rightTop:
                        x = 2;
                        break;
                    case R.id.rightBottom:
                        x = 4;
                        break;
                    case R.id.leftBottom:
                        x = 3;
                        break;
                }
                if (array_of_moves[numberOfClicksEachStage] != x) { // on wrong click
                    sp.play(sadMusic, 1, 1, 1, 0, 1f);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("Game over, you reached level: " + (numberOfElmentsInMovesArray - 1) + ", Do you want to play agian?");
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //Start over
                                    clear();
                                    textView.setText("Currect score: " + numberOfElmentsInMovesArray + "           High score: " + highScore);
                                    playGame();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    return true;
                }
                //on success
                playSound(v.getId());
                xorMyColor(v);
                numberOfClicksEachStage++;
                if (numberOfElmentsInMovesArray == numberOfClicksEachStage) { //if 4 boxes shown, then activate  function
                    //playGame only after 4 clicks have been made by the user

                    numberOfClicksEachStage = 0;
                    if (numberOfElmentsInMovesArray > highScore) {
                        highScore = numberOfElmentsInMovesArray;
                    }
                    textView.setText("Currect score: " + numberOfElmentsInMovesArray + "           High score: " + highScore);
                    final Runnable r = new Runnable() {
                        public void run() {
                            playGame();
                        }
                    };
                    handler.postDelayed(r, 2000 - 500 * hardness);
                }
            }
            return true;
        }
    };

    private void playSound(int id) {
        //function that play sound according to sound ID
        int audioRes = 0;
        switch (id) {
            case R.id.leftTop:
                audioRes = R.raw.doo;
                break;
            case R.id.rightTop:
                audioRes = R.raw.re;
                break;
            case R.id.rightBottom:
                audioRes = R.raw.mi;
                break;
            case R.id.leftBottom:
                audioRes = R.raw.fa;
                break;
        }
        MediaPlayer p = MediaPlayer.create(this, audioRes);
        p.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        p.start();
    }

    private void xorMyColor(final View v) {
        //function that changes the background color and get it back after 500 milliseconds
        v.getBackground().setAlpha(51);
        final Runnable r = new Runnable() {
            public void run() {
                v.getBackground().setAlpha(255);
            }
        };
        handler.postDelayed(r, 300);
    }

    public void playGame() {
        appendValueToArray();
        numberOfElmentsInMovesArray++;
        for (k = 0; k < numberOfElmentsInMovesArray; k++) {
            click(k);
        }
    }

    public void click(final int click_index) {
        //Function that clicks one place randomally on the view
        final Runnable r = new Runnable() {
            public void run() {
                if (array_of_moves[click_index] == 1) {
                    playSound(R.id.leftTop);
                    xorMyColor(leftTop);
                } else if (array_of_moves[click_index] == 2) {
                    playSound(R.id.rightTop);
                    xorMyColor(rightTop);
                } else if (array_of_moves[click_index] == 3) {
                    playSound(R.id.leftBottom);
                    xorMyColor(leftBottom);
                } else {
                    playSound(R.id.rightBottom);
                    xorMyColor(rightBottom);
                }
            }
        };

        handler.postDelayed(r, (2000 - 500 * hardness) * click_index);
    }


    private int generateRandomNumber() {
        return r.nextInt(4) + 1; // generate random number between 1 and 4
    }

    private void appendValueToArray() {  // add random number to the first free position in the array
        for (int i = 0; i < MAX_LENGTH; i++) {
            if (array_of_moves[i] == 0) {
                array_of_moves[i] = generateRandomNumber();
                break;
            }
        }
    }

    private void clear() {//reset the game to initial state
        for (int i = 0; i < MAX_LENGTH; i++) {
            array_of_moves[i] = 0;
        }
        numberOfClicksEachStage = 0;
        numberOfElmentsInMovesArray = 0;
    }

}

