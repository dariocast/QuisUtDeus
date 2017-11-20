package it.pentagono.app.quisutdeus;

/**
 * Created on 13/11/17.
 */
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class StreamingMp3Player extends Activity implements OnClickListener, OnTouchListener, OnCompletionListener, OnBufferingUpdateListener {

    private ImageButton buttonPlayPause;
    private SeekBar seekBarProgress;
    private TextView tempo;

    private MediaPlayer mediaPlayer;
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class

    private final Handler handler = new Handler();
    String url;
    Incontro incontro;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp3);
        Intent incoming = getIntent();
        url = incoming.getStringExtra("url");
        incontro = Incontro.getFromBundle(incoming.getBundleExtra("incontro"));

        ((ImageView) findViewById(R.id.img_incontro)).setImageResource(incontro.momento.equals("celebrazione")? R.drawable.celebrazione : R.drawable.preghiera);
        ((TextView) findViewById(R.id.tv_titolo)).setText(incontro.titolo);
        ((TextView) findViewById(R.id.tv_data)).setText(incontro.data);
        ((TextView) findViewById(R.id.tv_luogo)).setText(incontro.luogo);
        ((TextView) findViewById(R.id.tv_occasione)).setText(incontro.occasione);

        buttonPlayPause = (ImageButton) findViewById(R.id.ButtonTestPlayPause);
        buttonPlayPause.setOnClickListener(this);

        seekBarProgress = (SeekBar) findViewById(R.id.SeekBarTestPlay);
        seekBarProgress.setMax(99); // It means 100% .0-99
        seekBarProgress.setOnTouchListener(this);

        tempo = (TextView) findViewById(R.id.tv_tempo);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
        tempo.setText(String.format("%02d.%02d minuti",
                TimeUnit.MILLISECONDS.toMinutes(mediaFileLengthInMilliseconds),
                TimeUnit.MILLISECONDS.toSeconds(mediaFileLengthInMilliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaFileLengthInMilliseconds))
        ));
    }

    /**
     * Method which updates the SeekBar primary progress by current song playing position
     */
    private void primarySeekBarProgressUpdater() {
        seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ButtonTestPlayPause) {

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                buttonPlayPause.setImageResource(R.drawable.button_pause);
            } else {
                mediaPlayer.pause();
                buttonPlayPause.setImageResource(R.drawable.button_play);
            }

            primarySeekBarProgressUpdater();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.SeekBarTestPlay) {
            /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
            if (mediaPlayer.isPlaying()) {
                SeekBar sb = (SeekBar) v;
                int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                mediaPlayer.seekTo(playPositionInMillisecconds);
            }
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /** MediaPlayer onCompletion event handler. Method which calls then song playing is complete*/
        buttonPlayPause.setImageResource(R.drawable.button_play);
        mp.seekTo(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            buttonPlayPause.setImageResource(R.drawable.button_play);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            buttonPlayPause.setImageResource(R.drawable.button_play);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        /** Method which updates the SeekBar secondary progress by current song loading from URL position*/
        seekBarProgress.setSecondaryProgress(percent);
    }

}