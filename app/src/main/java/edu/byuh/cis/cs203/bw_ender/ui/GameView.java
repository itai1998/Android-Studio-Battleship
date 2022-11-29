package edu.byuh.cis.cs203.bw_ender.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import edu.byuh.cis.cs203.bw_ender.R;
import edu.byuh.cis.cs203.bw_ender.graphics.Airplane;
import edu.byuh.cis.cs203.bw_ender.graphics.Battleship;
import edu.byuh.cis.cs203.bw_ender.graphics.DepthCharge;
import edu.byuh.cis.cs203.bw_ender.misc.Direction;
import edu.byuh.cis.cs203.bw_ender.graphics.Missile;
import edu.byuh.cis.cs203.bw_ender.graphics.Sprite;
import edu.byuh.cis.cs203.bw_ender.graphics.Submarine;
import edu.byuh.cis.cs203.bw_ender.misc.TickListener;
import edu.byuh.cis.cs203.bw_ender.misc.Timer;

/**
 * It all happens here: the drawing, the tapping, the animation.
 */
public class GameView extends View implements TickListener {

    private Bitmap water;
    private Bitmap pop;
    private Battleship battleship;
    private List<Airplane> planes;
    private List<Submarine> subs;
    private boolean init;
    private Timer tim;
    private List<DepthCharge> bombs;
    private List<Missile> missiles;
    private float w,h;
    private Paint missilePaint;
    private boolean leftPop, rightPop;
    private int score;
    private Paint scorePaint;
    private int timeLeft;
    private int timerCounter;

    private MediaPlayer missileSoundLeft;
    private MediaPlayer missileSoundRight;
    private MediaPlayer depthSound;
    private MediaPlayer planeExplde;
    private MediaPlayer subExplode;
    private int missNumber = 0;
    private int bombNumber = 0;
    private int airNumber;
    private int submarineNumbers;



    /**
     * Constructor for our View subclass. Loads all the images
     * @param context a reference to our main Activity class
     */
    public GameView(Context context) {
        super(context);
        pop = BitmapFactory.decodeResource(getResources(),R.drawable.star);
        water = BitmapFactory.decodeResource(getResources(), R.drawable.water);
        bombs = new ArrayList<>();
        missiles = new ArrayList<>();
        missilePaint = new Paint();
        missilePaint.setColor(Color.DKGRAY);
        missilePaint.setStyle(Paint.Style.STROKE);
        planes = new ArrayList<>();
        subs = new ArrayList<>();
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setStyle(Paint.Style.FILL);
        scorePaint.setTextAlign(Paint.Align.LEFT);
        init = false;
        leftPop = false;
        rightPop = false;
        reset();

        missileSoundLeft = MediaPlayer.create(getContext(),R.raw.left_gun);
        missileSoundRight = MediaPlayer.create(getContext(),R.raw.right_gun);
        depthSound = MediaPlayer.create(getContext(),R.raw.depth_charge);
        planeExplde = MediaPlayer.create(getContext(),R.raw.plane_explode);
        subExplode = MediaPlayer.create(getContext(),R.raw.sub_explode);
        airNumber = Prefs.planeNumbers(context);
        submarineNumbers = Prefs.subNumbers(context);


    }

    private void reset() {
        timerCounter = 0;
        timeLeft = 180;
        score = 0;
    }

    /**
     * Scales, positions, and renders the scene
     * @param c the Canvas object, provided by system
     */
    @Override
    public void onDraw(Canvas c) {

        if (init == false) {
            init = true;
            w = getWidth();
            h = getHeight();
            scorePaint.setTextSize(h/15);

            //scale the water
            final int waterWidth = (int)(w/50);
            water = Bitmap.createScaledBitmap(water, waterWidth,waterWidth, true);

            //scale the "pop"
            final int popWidth = (int)(w*0.03f);
            pop = Bitmap.createScaledBitmap(pop, popWidth, popWidth, true);

            // call the Battleship Singleton class by its getter method instead of instantiate it
            battleship = Battleship.getInstance(getResources(), w);
            missilePaint.setStrokeWidth(w*0.0025f);

            //position sprites
            final float battleshipX = w/2; //center the ship
            final float battleshipY = h/2-battleship.getHeight()*0.04f; //put the ship above the water line
            battleship.setLocation(battleshipX, battleshipY);

            //DIRTY HACK: inform Airplane class of acceptable upper/lower limits of flight
            final float battleshipTop = battleship.getTop()+battleship.getHeight()*0.4f;
            Airplane.setSkyLimits(0, battleshipTop);

            //DIRTY HACK: inform Submarine class of acceptable upper/lower limits of depth
            Submarine.setWaterDepth(h/2 + waterWidth*2, h);

            //load and scale the enemies

            for(int i=0; i<airNumber; i++){
                planes.add(new Airplane(getContext(),getResources(), w));
            }

            for(int i=0; i<submarineNumbers; i++){
                subs.add(new Submarine(getContext(),getResources(), w));
            }

            //Once everything is in place, start the animation loop!
            tim = new Timer();
            //Using "method reference" syntax here, just for fun
            planes.forEach(tim::subscribe);
            subs.forEach(tim::subscribe);
            tim.subscribe(this);
        }

        //now draw everything
        c.drawColor(Color.WHITE);

        float waterX = 0;
        while (waterX < w) {
            c.drawBitmap(water, waterX, h/2, null);
            waterX += water.getWidth();
        }

        battleship.draw(c);
        planes.forEach(p -> p.draw(c));
        subs.forEach(s -> s.draw(c));
        missiles.forEach(m -> m.draw(c));
        bombs.forEach(d -> d.draw(c));

        if (leftPop) {
            final PointF popLocation = battleship.getLeftCannonPosition();
            c.drawBitmap(pop, popLocation.x-pop.getWidth(), popLocation.y-pop.getHeight(), null);
            leftPop = false;
        }
        if (rightPop) {
            final PointF popLocation = battleship.getRightCannonPosition();
            c.drawBitmap(pop, popLocation.x, popLocation.y-pop.getHeight(), null);
            rightPop = false;
        }
        c.drawText(getResources().getString(R.string.game_score) +" "+ score, 5, h*0.6f, scorePaint);
        final String seconds = String.format("%02d", timeLeft % 60);
        c.drawText(getResources().getString(R.string.game_time)+" " + (timeLeft/60) +":" + seconds, w*0.75f, h*0.6f, scorePaint);
        //c.drawText("MISSILES: " + missiles.size(), 5, h*0.7f, scorePaint);
        //c.drawText("DEPTHCHARGES: " + bombs.size(), 5, h*0.8f, scorePaint);
    }

    /**
     * launch depth charges and missiles based on the user's taps.
     * @param m an object encapsulating the (x,y) location of the user's tap
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            float x = m.getX();
            float y = m.getY();

            if(missiles.isEmpty()){
                missNumber=0;
            }

            if(bombs.isEmpty()){
                bombNumber=0;
            }
            //did the user tap the bottom half of the screen? Depth Charge!
            if(Prefs.depthNumber(getContext())){
                if (y > h/2 && bombNumber==0) {
                    var dc = new DepthCharge(getResources(), w);
                    dc.setCentroid(w/2, h/2);
                    bombs.add(dc);
                    tim.subscribe(dc);
                    bombNumber++;

                    if(Prefs.soundFX(getContext())){
                        depthSound.start();
                    }


                }
            }else if(y>h/2){
                    var dc = new DepthCharge(getResources(), w);
                    dc.setCentroid(w/2, h/2);
                    bombs.add(dc);
                    tim.subscribe(dc);
                    if(Prefs.soundFX(getContext())){
                        depthSound.start();
                    }
            }

                if(Prefs.missileNumber(getContext())){
                    Missile miss = null;
                    if (x < w/2 && y<h/2 && missNumber==0) {
                        miss = new Missile(Direction.LEFT_FACING, w, missilePaint);
                        miss.setBottomRight(battleship.getLeftCannonPosition());
                        leftPop = true;
                        missiles.add(miss);
                        tim.subscribe(miss);
                        missNumber++;

                        if(Prefs.soundFX(getContext())){
                            missileSoundLeft.start();
                        }

                    }else if(x>w/2 && y<h/2 &&missNumber==0){
                        miss = new Missile(Direction.RIGHT_FACING, w, missilePaint);
                        miss.setBottomLeft(battleship.getRightCannonPosition());
                        rightPop = true;
                        missiles.add(miss);
                        tim.subscribe(miss);
                        missNumber++;
                        if(Prefs.soundFX(getContext())){
                            missileSoundRight.start();
                        }
                    }else{
                        System.out.println("Cannot shoot");
                    }


                }else{
                    Missile miss = null;
                    if (x < w/2 && y<h/2) {
                        miss = new Missile(Direction.LEFT_FACING, w, missilePaint);
                        miss.setBottomRight(battleship.getLeftCannonPosition());
                        leftPop = true;
                        missiles.add(miss);
                        tim.subscribe(miss);
                        if(Prefs.soundFX(getContext())){
                            missileSoundLeft.start();
                        }

                    } else if(x>w/2 && y<h/2) {
                        miss = new Missile(Direction.RIGHT_FACING, w, missilePaint);
                        miss.setBottomLeft(battleship.getRightCannonPosition());
                        rightPop = true;
                        missiles.add(miss);
                        tim.subscribe(miss);
                        if(Prefs.soundFX(getContext())){
                            missileSoundRight.start();
                        }
                    }
                }

            //clean up depth charges that go off-screen
            List<Sprite> doomed = bombs.stream().filter(dc -> dc.getTop() > getHeight()).collect(Collectors.toList());
            doomed.forEach(tim::unsubscribe);
            bombs.removeAll(doomed);



            //clean up missiles that go off-screen
            doomed = missiles.stream().filter(miss -> miss.getBottom() < 0).collect(Collectors.toList());
            doomed.forEach(tim::unsubscribe);
            missiles.removeAll(doomed);


        }
        return true;
    }

    private void detectCollisions() {
        for (Submarine s : subs) {
            for (DepthCharge d : bombs) {
                if (d.overlaps(s)) {
                    s.explode();
                    score += s.getPointValue();
                    //hide the depth charge off-screen; it will get cleaned
                    //up at the next touch event.
                    d.setLocation(0,getHeight());
                    if(Prefs.soundFX(getContext())){
                        subExplode.start();
                    }

                }
            }
        }

        for (Airplane p : planes) {
            for (Missile m : missiles) {
                if (p.overlaps(m)) {
                    p.explode();
                    score += p.getPointValue();
                    //hide the missile charge off-screen; it will get cleaned
                    //up at the next touch event.
                    m.setLocation(0,-getHeight());
                    if(Prefs.soundFX(getContext())){
                        planeExplde.start();
                    }


                }
            }
        }
    }

    /**
     * Counting the timer
     */
    @Override
    public void tick() {
        invalidate();
        detectCollisions();
        timerCounter++;
        if (timerCounter >= 10) {
            timeLeft--;
            timerCounter = 0;
        }
        if (timeLeft <= 0) {
            endgame();
        }
    }

    private void endgame() {
        tim.pause();
        String message = "";
        int oldScore = 0;
        //attempt to load the old score
        //try with resource
        final String scoreFile = "highscore.txt";
        try(Scanner is = new Scanner(getContext().openFileInput(scoreFile))) {
            oldScore = is.nextInt();
        } catch (FileNotFoundException e) {
            System.out.println("The file cannot be found!!!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Something goes wrong!");
            e.printStackTrace();
        }

//
        if (oldScore < score) {
            message = getResources().getString(R.string.game_congratulations);
            //now, save the new score
            // try with resource
            try(OutputStream os = getContext().openFileOutput(scoreFile, Context.MODE_PRIVATE)) {
                os.write((""+score).getBytes());
            } catch (IOException e) {
                //do nothing. There's nothing we could do anyway.
            }
        } else {
            message = getResources().getString(R.string.game_again) + oldScore + ")" ;
        }

        //Now, prep the dialog box
        AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
        ab.setTitle("GAME OVER")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int w) {
                        tim.resume();
                        reset();
                    }
                })
                .setNegativeButton("No", (d, w) -> ((Activity)getContext()).finish());
        AlertDialog box = ab.create();
        box.show();
    }

    /**
     * Pause the timer
     */
    public void pause(){
        tim.pause();
    }

    /**
     * Re-start the timer
     */
    public void resume(){
        if(tim != null){
            tim.resume();
        }

    }


}


