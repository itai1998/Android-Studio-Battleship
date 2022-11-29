package edu.byuh.cis.cs203.bw_ender.ui;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintSet;

import edu.byuh.cis.cs203.bw_ender.R;

public class SplashActivity extends Activity {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        iv = new ImageView(this);
        iv.setImageResource(R.drawable.splash);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(iv);
    }

    /**
     * Show the right activity according to where the users click on the screen
     */
    @Override
    public boolean onTouchEvent(MotionEvent m){
        var w = iv.getWidth();
        var h = iv.getHeight();
        if(m.getAction() == MotionEvent.ACTION_UP){
            var x = m.getX();
            var y = m.getY();
            if(x>w*0.333 && x<w*0.422 && y<h*0.592 && y>h*0.477){
                Intent tent = new Intent(this, MainActivity.class);
                startActivity(tent);
                finish();

            }else if(x>w*0.9 && y>h*0.97){
                Intent tent = new Intent(this, MyPrefsActivity.class);
                startActivity(tent);

            } else if(h>h*0.99 && x>w*0.843 && x< w*0.942){
                AlertDialog.Builder ab;
                ab = new AlertDialog.Builder(this);
                ab.setTitle(R.string.information_title)
                        .setMessage(R.string.information_message)
                        .setCancelable(false)
                        .setNegativeButton(R.string.information_ok, (d, a) -> this.finishActivity(0));
                AlertDialog box = ab.create();
                box.show();
            }


        }
        return true;
    }

}
