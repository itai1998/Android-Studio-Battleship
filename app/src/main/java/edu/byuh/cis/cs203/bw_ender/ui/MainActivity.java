package edu.byuh.cis.cs203.bw_ender.ui;

import android.app.Activity;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.byuh.cis.cs203.bw_ender.R;

public class MainActivity extends Activity {

    private GameView gv;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        gv = new GameView(this);
        setContentView(gv);
    }

    @Override
    protected void onResume(){
        super.onResume();
        gv.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        gv.pause();
    }

}