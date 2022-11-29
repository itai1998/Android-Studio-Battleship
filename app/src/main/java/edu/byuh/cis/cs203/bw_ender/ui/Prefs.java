package edu.byuh.cis.cs203.bw_ender.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import edu.byuh.cis.cs203.bw_ender.R;

public class Prefs extends PreferenceFragmentCompat {

    private static final String PLAY_SOUNDTRACK = "PLAY_SOUNDTRACK";
    private static final String MISSILE_NUMBERS = "MISSILE_NUMBERS";
    private static final String DEPTH_CHARGE_NUMBER = "DEPTH_CHARGE_NUMBER";
    private static final String PLANE_NUMBERS = "PLANE_NUMBER";
    private static final String SUB_NUMBERS = "SUB_NUMBERS";
    private static final String SUB_SPEED_PREF = "SUB_SPEED_PREF";
    private static final String PLANE_SPEED_PREF = "PLANE_SPEED_PREF";


    /**
     * Create a setting activity view and its functions
     */
    @Override
    public void onCreatePreferences(Bundle b, String s) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);


        //TODO add preference widgets here
        //var musicPref = new CheckBoxPreference(context);
        var musicPref = new SwitchPreference(context);
        musicPref.setTitle(R.string.music_pref);
        musicPref.setSummaryOff(R.string.music_summaryoff);
        musicPref.setSummaryOn(R.string.music_summaryon);
        musicPref.setKey(PLAY_SOUNDTRACK);

        var missilePref = new CheckBoxPreference(context);
        missilePref.setTitle(R.string.missile_pref);
        missilePref.setSummaryOff(R.string.missile_summaryoff);
        missilePref.setSummaryOn(R.string.missile_summaryon);
        missilePref.setKey(MISSILE_NUMBERS);

        var depthPref = new CheckBoxPreference(context);
        depthPref.setTitle(R.string.depth_pref);
        depthPref.setSummaryOff(R.string.depth_summaryoff);
        depthPref.setSummaryOn(R.string.depth_summaryon);
        depthPref.setKey(DEPTH_CHARGE_NUMBER);

        var planeNumbersPref = new ListPreference(context);
        planeNumbersPref.setTitle(R.string.plane_numbers_pref);
        planeNumbersPref.setSummary(R.string.plane_numbers_summary);
        planeNumbersPref.setKey(PLANE_NUMBERS);
        planeNumbersPref.setEntries(R.array.plane_numbers);
        planeNumbersPref.setEntryValues(new String[] {"10", "9", "8", "7", "6", "5", "4", "3", "2", "1"});

        var subNumbersPref = new ListPreference(context);
        subNumbersPref.setTitle(R.string.sub_numbers_pref);
        subNumbersPref.setSummary(R.string.sub_numbers_summary);
        subNumbersPref.setKey(SUB_NUMBERS);
        subNumbersPref.setEntries(R.array.sub_numbers);
        subNumbersPref.setEntryValues(new String[] {"10", "9", "8", "7", "6", "5", "4", "3", "2", "1"});

        var subSpeedPref = new ListPreference(context);
        subSpeedPref.setTitle(R.string.sub_moving_pref);
        subSpeedPref.setSummary(R.string.sub_moving_summary);
        subSpeedPref.setKey(SUB_SPEED_PREF);
        subSpeedPref.setEntries(R.array.sub_speed_labels);
        subSpeedPref.setEntryValues(new String[] {"10", "5", "1"});

        var planeSpeedPref = new ListPreference(context);
        planeSpeedPref.setTitle(R.string.plane_moving_pref);
        planeSpeedPref.setSummary(R.string.plane_moving_summary);
        planeSpeedPref.setKey(PLANE_SPEED_PREF);
        planeSpeedPref.setEntries(R.array.plane_speed_labels);
        planeSpeedPref.setEntryValues(new String[] {"10", "5", "1"});


        screen.addPreference(musicPref);
        screen.addPreference(missilePref);
        screen.addPreference(depthPref);
        screen.addPreference(planeNumbersPref);
        screen.addPreference(subNumbersPref);
        screen.addPreference(subSpeedPref);
        screen.addPreference(planeSpeedPref);


        setPreferenceScreen(screen);
    }

    /**
     * @return boolean and see if the users want to turn the sound on
     */
    public static boolean soundFX(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(PLAY_SOUNDTRACK, true);
    }

    /**
     * @return boolean to see if the users allow more than one missile on the screen
     */
    public static boolean missileNumber(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(MISSILE_NUMBERS, true);
    }

    /**
     * @return boolean to see if the users allow more than one depthCharge on the screen
     */
    public static boolean depthNumber(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(DEPTH_CHARGE_NUMBER, true);
    }

    /**
     * @return int to see how many airplanes that the users want to show on the screen
     */
    public static int planeNumbers(Context c){
        String tmp = PreferenceManager.getDefaultSharedPreferences(c).getString(PLANE_NUMBERS, "3");
        return Integer.parseInt(tmp);
    }

    /**
     * @return int to see how many submarine that the users want to show on the screen
     */
    public static int subNumbers(Context c){
        String tmp = PreferenceManager.getDefaultSharedPreferences(c).getString(SUB_NUMBERS, "3");
        return Integer.parseInt(tmp);
    }

    /**
     * @return int to see how fast the user want the submarines move
     */
    public static int subSpeed(Context c){
        String tmp = PreferenceManager.getDefaultSharedPreferences(c).getString(SUB_SPEED_PREF, "1");
        return Integer.parseInt(tmp);
    }

    /**
     * @return int to see how fast the user want the airplanes move
     */
    public static int planeSpeed(Context c){
        String tmp = PreferenceManager.getDefaultSharedPreferences(c).getString(PLANE_SPEED_PREF, "1");
        return Integer.parseInt(tmp);
    }


}