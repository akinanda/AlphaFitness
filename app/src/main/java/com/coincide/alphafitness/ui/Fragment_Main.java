/*
 * Copyright 2014 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coincide.alphafitness.ui;

import android.Manifest;
import android.app.AlertDialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.coincide.alphafitness.BroadcastService;
import com.coincide.alphafitness.BuildConfig;
import com.coincide.alphafitness.Database;
import com.coincide.alphafitness.DirectionsJSONParser;
import com.coincide.alphafitness.GPSTracker;

import com.coincide.alphafitness.ProfileActivity;
import com.coincide.alphafitness.R;
import com.coincide.alphafitness.SensorListener;
import com.coincide.alphafitness.TimeFormatUtil;
import com.coincide.alphafitness.util.Logger;
import com.coincide.alphafitness.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;
import static com.coincide.alphafitness.ProfileActivity.MyPREFERENCES;
import static com.coincide.alphafitness.ProfileActivity.sp_weight;


public class Fragment_Main extends Fragment implements SensorEventListener,LocationListener {

//    private TextView stepsView, totalView, averageView;
    private TextView totalView;

//    private PieModel sliceGoal, sliceCurrent;
//    private PieChart pg;

    private int todayOffset, total_start, goal, since_boot, total_days;
    public final static NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
    private boolean showSteps = false;


    /*
    * MainActivity
    * */

    GoogleMap mGoogleMap;
    ArrayList<LatLng> mMarkerPoints;
    double mLatitude = 0;
    double mLongitude = 0;
    MarkerOptions fixedCentreoption;

    String Addrs;
    private static TextView tv_duration;

    private boolean isButtonStartPressed = false;


//    private Timer timer;

    private Button startButton;

//    private int currentTime = 0;
//    private int lapTime = 0;

/*    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private int mId = 1;*/

    ImageView iv_profile;
float avg_distance=0;
float all_distance=0;
float today_distance=0;
    private static double METRIC_WALKING_FACTOR = 0.708;
    private float avg_calories = 0;
    private float all_calories = 0;
    private float today_calories = 0;
    float mBodyWeight;

static int time_current;

TextView tv_avg,tv_max,tv_min;

    int tot_workouts;
    int tot_workTime;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);
        setRetainInstance(true);

        if (savedInstanceState != null) {
            // Restore last state
            isButtonStartPressed = savedInstanceState.getBoolean("isButtonStartPressed");

        } else {
            isButtonStartPressed=false;
        }


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
//        final View v = inflater.inflate(R.layout.fragment_overview, null);
         View v=null;


            v = inflater.inflate(R.layout.fragment_main, container, false);


//        stepsView = (TextView) v.findViewById(R.id.steps);
//        totalView = (TextView) v.findViewById(R.id.total);
        totalView = (TextView) v.findViewById(R.id.tv_distance);
        tv_avg = (TextView) v.findViewById(R.id.tv_avg);
        tv_max = (TextView) v.findViewById(R.id.tv_max);
        tv_min = (TextView) v.findViewById(R.id.tv_min);
//        averageView = (TextView) v.findViewById(R.id.average);

/*
        pg = (PieChart) v.findViewById(R.id.graph);

        // slice for the steps taken today
        sliceCurrent = new PieModel("", 0, Color.parseColor("#99CC00"));
        pg.addPieSlice(sliceCurrent);

        // slice for the "missing" steps until reaching the goal
        sliceGoal = new PieModel("", Fragment_Settings.DEFAULT_GOAL, Color.parseColor("#CC0000"));
        pg.addPieSlice(sliceGoal);

        pg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                showSteps = !showSteps;
                stepsDistanceChanged();
            }
        });

        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();
*/

        /*
        * MainActivity
        * */
        // Always cast your custom Toolbar here, and set it as the ActionBar.
        Toolbar tb = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tb);

        TextView tv_tb_center = (TextView) tb.findViewById(R.id.tv_tb_center);
        tv_tb_center.setText("ALPHA FITNESS");

     /*   ImageButton imgbtn_cart = (ImageButton) tb.findViewById(R.id.imgbtn_cart);
        imgbtn_cart.setVisibility(View.GONE);*/

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(false); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowCustomEnabled(false); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        tv_duration = (TextView) v.findViewById(R.id.tv_duration);

        startButton = (Button) v.findViewById(R.id.btn_start);
        if(isButtonStartPressed){
            startButton.setBackgroundResource(R.drawable.btn_stop_states);
            startButton.setText(R.string.btn_stop);
            try {
                SensorManager sm =
                        (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                        SensorManager.SENSOR_DELAY_UI, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            try {
                SensorManager sm =
                        (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                sm.unregisterListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onSWatchStart();
            }
        });
        iv_profile = (ImageView) v.findViewById(R.id.iv_profile);
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Database db = Database.getInstance(getActivity());
                tot_workouts=db.getTotWorkouts();
                tot_workTime=db.getWorkTime();
Log.e("Tot Work time",tot_workTime+"");
//                int seconds = (int) (tot_workTime / 1000) % 60;
//                int minutes = (int) ((tot_workTime / (1000 * 60)) % 60);

                long millis = tot_workTime*100;  // obtained from StopWatch
                long hours = (millis / 1000)  / 3600;
                long minutes = (millis / 1000)  / 60;
                long seconds = (millis / 1000) % 60;


                db.close();

                Intent i = new Intent(getActivity(), ProfileActivity.class);
                i.putExtra("avg_distance", avg_distance+"");
                i.putExtra("all_distance", all_distance+"");
                i.putExtra("avg_time", tv_duration.getText().toString());
                i.putExtra("all_time", tv_duration.getText().toString());
                i.putExtra("avg_calories", avg_calories+"");
                i.putExtra("all_calories", all_calories+"");
                i.putExtra("tot_workouts", tot_workouts+"");
                i.putExtra("avg_workouts", tot_workouts+"");

                i.putExtra("tot_workTime",hours+" hrs "+ minutes + " min " + seconds + " sec");

                startActivity(i);


            }
        });

// Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Initializing
            mMarkerPoints = new ArrayList<LatLng>();

            // Getting reference to SupportMapFragment of the activity_main
//            SupportMapFragment fm = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
            MapFragment fm = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment
            mGoogleMap = fm.getMap();

            // Enable MyLocation Button in the Map
            mGoogleMap.setMyLocationEnabled(true);


            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location ;
            if (provider != null) {


                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
                location = locationManager.getLastKnownLocation(provider);
                locationManager.requestLocationUpdates(provider, 20000, 0, this);
            }
            else{
                location = new Location("");
                location.setLatitude(0.0d);//your coords of course
                location.setLongitude(0.0d);
            }

            if (location != null) {
                onLocationChanged(location);
            }



            // Setting onclick event listener for the map
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {

                    // Already map contain destination location
                    if(mMarkerPoints.size()>1){

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        mMarkerPoints.clear();
                        mGoogleMap.clear();
                        LatLng startPoint = new LatLng(mLatitude, mLongitude);
                        drawMarker(startPoint);
                    }

                    drawMarker(point);

                    // Checks, whether start and end locations are captured
                    if(mMarkerPoints.size() >= 2){
                        LatLng origin = mMarkerPoints.get(0);
                        LatLng dest = mMarkerPoints.get(1);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
                }
            });
            fixedCentreoption = new MarkerOptions();

            mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition position) {
                    // TODO Auto-generated method stub
                    // Get the center of the Map.

                    mGoogleMap.clear();

                    LatLng centerOfMap = mGoogleMap.getCameraPosition().target;

                    // Update your Marker's position to the center of the Map.
                    fixedCentreoption.position(centerOfMap);
//	                mGoogleMap.addMarker(fixedCentreoption);
//	                drawMarker(centerOfMap);
                    LatLng origin = new LatLng(0.0d, 0.0d);;
                    if(mMarkerPoints.size()>0) {
                        origin = mMarkerPoints.get(0);
                    }
//	            	LatLng dest = mMarkerPoints.get(1);
                    LatLng dest = centerOfMap;
                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);


                    GPSTracker gpsTracker = new GPSTracker(getActivity().getApplicationContext());
//					String Addrs = gpsTracker.location();
                    Addrs = gpsTracker.locationBasedOnLatlng(centerOfMap);
//					Toast.makeText(getApplicationContext(), Addrs, Toast.LENGTH_LONG).show();

                }
            });
        }



        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mBodyWeight=Float.parseFloat(sharedpreferences.getString(sp_weight,"50"));


        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isButtonStartPressed", isButtonStartPressed);
    }

   @Override
    public void onResume() {
        super.onResume();
//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

        Database db = Database.getInstance(getActivity());

        if (BuildConfig.DEBUG) db.logState();
        // read todays offset
        todayOffset = db.getSteps(Util.getToday());

        SharedPreferences prefs =
                getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);

        goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);
        since_boot = db.getCurrentSteps(); // do not use the value from the sharedPreferences
        int pauseDifference = since_boot - prefs.getInt("pauseCount", since_boot);

        // register a sensorlistener to live update the UI if a step is taken
        if (!prefs.contains("pauseCount")) {
            SensorManager sm =
                    (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (sensor == null) {
                new AlertDialog.Builder(getActivity()).setTitle(R.string.no_sensor)
                        .setMessage(R.string.no_sensor_explain)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(final DialogInterface dialogInterface) {
                                getActivity().finish();
                            }
                        }).setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            } else {
                sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
            }
        }

        since_boot -= pauseDifference;

        total_start = db.getTotalWithoutToday();
        total_days = db.getDays();

        db.close();

       SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
       mBodyWeight=Float.parseFloat(sharedpreferences.getString(sp_weight,"0"));


       stepsDistanceChanged();


    }

    /**
     * C
     * all this method if the Fragment should update the "steps"/"km" text in
     * the pie graph as well as the pie and the bars graphs.
     */
    private void stepsDistanceChanged() {
        if (showSteps) {
//            ((TextView) getView().findViewById(R.id.unit)).setText(getString(R.string.steps));
            ((TextView) getView().findViewById(R.id.tv_distance_unit)).setText(getString(R.string.steps));
        } else {
            String unit = getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE)
                    .getString("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT);
            if (unit.equals("cm")) {
                unit = "km";
            } else {
                unit = "mi";
            }
            ((TextView) getView().findViewById(R.id.tv_distance_unit)).setText(unit);
        }

        updatePie();
        updateBars();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            SensorManager sm =
                    (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Database db = Database.getInstance(getActivity());
        db.saveCurrentSteps(since_boot);
        db.close();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem pause = menu.getItem(0);
        Drawable d;
        if (getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE)
                .contains("pauseCount")) { // currently paused
            pause.setTitle(R.string.resume);
            d = getResources().getDrawable(R.drawable.ic_resume);
        } else {
            pause.setTitle(R.string.pause);
            d = getResources().getDrawable(R.drawable.ic_pause);
        }
        d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        pause.setIcon(d);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_split_count:
                Dialog_Split.getDialog(getActivity(),
                        total_start + Math.max(todayOffset + since_boot, 0)).show();
                return true;
            case R.id.action_pause:
                SensorManager sm =
                        (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                Drawable d;
                if (getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE)
                        .contains("pauseCount")) { // currently paused -> now resumed
                    sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                            SensorManager.SENSOR_DELAY_UI, 0);
                    item.setTitle(R.string.pause);
                    d = getResources().getDrawable(R.drawable.ic_pause);
                } else {
                    sm.unregisterListener(this);
                    item.setTitle(R.string.resume);
                    d = getResources().getDrawable(R.drawable.ic_resume);
                }
                d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                item.setIcon(d);
                getActivity().startService(new Intent(getActivity(), SensorListener.class)
                        .putExtra("action", SensorListener.ACTION_PAUSE));
                return true;
            default:
                return ((Activity_Main) getActivity()).optionsItemSelected(item);
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // won't happen
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (BuildConfig.DEBUG)
            Logger.log("UI - sensorChanged | todayOffset: " + todayOffset + " since boot: " +
                    event.values[0]);
        if (event.values[0] > Integer.MAX_VALUE || event.values[0] == 0) {
            return;
        }
        if (todayOffset == Integer.MIN_VALUE) {
            // no values for today
            // we dont know when the reboot was, so set todays steps to 0 by
            // initializing them with -STEPS_SINCE_BOOT
            todayOffset = -(int) event.values[0];
            Database db = Database.getInstance(getActivity());
            db.insertNewDay(Util.getToday(), (int) event.values[0]);
            db.close();
        }
        since_boot = (int) event.values[0];
        updatePie();
    }

    /**
     * Updates the pie graph to show todays steps/distance as well as the
     * yesterday and total values. Should be called when switching from step
     * count to distance.
     */
    private void updatePie() {
        if (BuildConfig.DEBUG) Logger.log("UI - update steps: " + since_boot);
        // todayOffset might still be Integer.MIN_VALUE on first start
        int steps_today = Math.max(todayOffset + since_boot, 0);
/*        sliceCurrent.setValue(steps_today);
        if (goal - steps_today > 0) {
            // goal not reached yet
            if (pg.getData().size() == 1) {
                // can happen if the goal value was changed: old goal value was
                // reached but now there are some steps missing for the new goal
                pg.addPieSlice(sliceGoal);
            }
            sliceGoal.setValue(goal - steps_today);
        } else {
            // goal reached
            pg.clearChart();
            pg.addPieSlice(sliceCurrent);
        }
        pg.update();*/
        if (showSteps) {
//            stepsView.setText(formatter.format(steps_today));
            totalView.setText(formatter.format(total_start + steps_today));
//            averageView.setText(formatter.format((total_start + steps_today) / total_days));
        } else {
            // update only every 10 steps when displaying distance
            SharedPreferences prefs =
                    getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);
            float stepsize = prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_STEP_SIZE);
            float distance_today = steps_today * stepsize;
            float distance_total = (total_start + steps_today) * stepsize;
            if (prefs.getString("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT)
                    .equals("cm")) {
                distance_today /= 100000;
                distance_total /= 100000;
            } else {
                distance_today /= 5280;
                distance_total /= 5280;
            }
//            stepsView.setText(formatter.format(distance_today));
            totalView.setText(formatter.format(distance_today));
//            averageView.setText(formatter.format(distance_total / total_days));
//            avg_distance=formatter.format(distance_total / total_days);
            avg_distance=distance_total/7;
            all_distance=distance_total;

            all_calories = (float) (mBodyWeight * (METRIC_WALKING_FACTOR))*distance_total;
            avg_calories = (float)(mBodyWeight * (METRIC_WALKING_FACTOR))*distance_total/7;

                            // Distance:
//                            * mStepLength // centimeters
//                            / 100000.0; // centimeters/kilometer
today_distance=distance_today;
            today_calories = (float) (mBodyWeight * (METRIC_WALKING_FACTOR))*distance_total;

        }
    }

    /**
     * Updates the bar graph to show the steps/distance of the last week. Should
     * be called when switching from step count to distance.
     */
    private void updateBars() {
        SimpleDateFormat df = new SimpleDateFormat("E", Locale.getDefault());
        BarChart barChart = (BarChart) getView().findViewById(R.id.bargraph);
        if (barChart.getData().size() > 0) barChart.clearChart();
        int steps;
        float distance = 0, stepsize = Fragment_Settings.DEFAULT_STEP_SIZE;
        boolean stepsize_cm = true;
        if (!showSteps) {
            // load some more settings if distance is needed
            SharedPreferences prefs =
                    getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);
            stepsize = prefs.getFloat("stepsize_value", Fragment_Settings.DEFAULT_STEP_SIZE);
            stepsize_cm = prefs.getString("stepsize_unit", Fragment_Settings.DEFAULT_STEP_UNIT)
                    .equals("cm");
        }
        barChart.setShowDecimal(!showSteps); // show decimal in distance view only
        BarModel bm;
        Database db = Database.getInstance(getActivity());
//        List<Pair<Long, Integer>> last = db.getLastEntries(8);
        List<Pair<Float, Integer>> last = db.getWorkouts(8);
        db.close();
        for (int i = last.size() - 1; i > 0; i--) {
//            Pair<Long, Integer> current = last.get(i);
            Pair<Float, Integer> current = last.get(i);
            steps = current.second;
            if (steps > 0) {
//                bm = new BarModel(df.format(new Date(current.first)), 0,
//                        steps > goal ? Color.parseColor("#99CC00") : Color.parseColor("#0099cc"));
                bm=new BarModel("Calories Burnt",0,Color.parseColor("#0099cc"));

                /*if (showSteps) {
                    bm.setValue(steps);
                } else {
                    distance = steps * stepsize;
                    if (stepsize_cm) {
                        distance /= 100000;
                    } else {
                        distance /= 5280;
                    }
                    distance = Math.round(distance * 1000) / 1000f; // 3 decimals
                    bm.setValue(distance);
                }*/

                bm.setValue(current.first);
                barChart.addBar(bm);
            }
        }
       /* bm=new BarModel("Calories Burnt",0,Color.parseColor("#0099cc"));
        bm.setValue(2.5f);
        barChart.addBar(bm);

        bm=new BarModel("Calories Burnt",0,Color.parseColor("#0099cc"));
        bm.setValue(1.5f);
        barChart.addBar(bm);

        bm=new BarModel("Calories Burnt",0,Color.parseColor("#0099cc"));
        bm.setValue(1.0f);
        barChart.addBar(bm);
*/
        if (barChart.getData().size() > 0) {
            barChart.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
//                    Dialog_Statistics.getDialog(getActivity(), since_boot).show();
                }
            });
            barChart.startAnimation();
        } else {
            barChart.setVisibility(View.VISIBLE);
        }

//        Database db = Database.getInstance(getActivity());

        try {
            long avgDistance=db.getAVGDistance();
            long avgTime=db.getAVGTime();

            long millis = avgTime*100;  // obtained from StopWatch
            long minutes = (millis / 1000)  / 60;

            long avg=minutes/avgDistance;
            tv_avg.setText(TimeFormatUtil.toDisplayString((int) avg));

             avgDistance=db.getMAXDistance();
             avgTime=db.getMAXTime();

             millis = avgTime*100;  // obtained from StopWatch
             minutes = (millis / 1000)  / 60;

             avg=minutes/avgDistance;

            tv_max.setText(TimeFormatUtil.toDisplayString((int) avg));

             avgDistance=db.getMINDistance();
            avgTime=db.getMINTime();

             millis = avgTime*100;  // obtained from StopWatch
             minutes = (millis / 1000)  / 60;

             avg=minutes/avgDistance;

            tv_min.setText(TimeFormatUtil.toDisplayString((int) avg));

        }catch (Exception e){

        }



    }


    /*
    * MainActivity
    * */
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.e("URL", url);
        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            if(result!=null)
            {
                for(int i=0;i<result.size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(2);
                    lineOptions.color(Color.RED);

                }

                // Drawing polyline in the Google Map for the i-th route
                if(lineOptions!=null) {
                    mGoogleMap.addPolyline(lineOptions);
                }
            }
        }
    }




    private void drawMarker(LatLng point){
        mMarkerPoints.add(point);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if(mMarkerPoints.size()==1){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }else if(mMarkerPoints.size()==2){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mGoogleMap.addMarker(options);
    }

    @Override
    public void onLocationChanged(Location location) {
        // Draw the marker, if destination location is not set
        if(mMarkerPoints.size() < 2){

            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            LatLng point = new LatLng(mLatitude, mLongitude);

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

            drawMarker(point);

            // Getting URL to the Google Directions API
//            String url = getDirectionsUrl(mMarkerPoints.get(0), point);

//            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
//            downloadTask.execute(url);


        }

    }





    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    public void onSWatchStart() {
        if (isButtonStartPressed) {
            onSWatchStop();
//            startStopService();

            try {
                SensorManager sm =
                        (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                sm.unregisterListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

//            startStopService();
            getActivity().startService(new Intent(getActivity(), SensorListener.class));

            try {
                SensorManager sm =
                        (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                        SensorManager.SENSOR_DELAY_UI, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }






            isButtonStartPressed = true;

            startButton.setBackgroundResource(R.drawable.btn_stop_states);
            startButton.setText(R.string.btn_stop);

            getActivity().startService(new Intent(getActivity(), BroadcastService.class));

            /*lapButton.setBackgroundResource(R.drawable.btn_lap_states);
            lapButton.setText(R.string.btn_lap);
            lapButton.setEnabled(true);*/

//            setUpNotification();

            /*timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            currentTime += 1;
//                            lapTime += 1;

                           *//*manager = (NotificationManager)
                                    getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                            // update notification text
                            builder.setContentText(TimeFormatUtil.toDisplayString(currentTime));
                            manager.notify(mId, builder.build());*//*

                            // update ui
                            tv_duration.setText(TimeFormatUtil.toDisplayString(currentTime));
                        }
                    });
                }
            }, 0, 100);*/
        }
    }

    public void onSWatchStop() {
        startButton.setBackgroundResource(R.drawable.btn_start_states);
        startButton.setText(R.string.btn_start);
//        lapButton.setEnabled(true);
//        lapButton.setBackgroundResource(R.drawable.btn_lap_states);
//        lapButton.setText(R.string.btn_reset);

        isButtonStartPressed = false;
        getActivity().stopService(new Intent(getActivity(), BroadcastService.class));

        Database db = Database.getInstance(getActivity());
        db.insertWorkouts(Util.getToday(), since_boot,today_distance, time_current,today_calories);
        db.close();



//        timer.cancel();
//        manager.cancel(mId);
    }

    public void startStopService(){
        SensorManager sm =
                (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        if (getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE)
                .contains("pauseCount")) { // currently paused -> now resumed
            sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                    SensorManager.SENSOR_DELAY_UI, 0);


        } else {
            sm.unregisterListener(this);

        }

        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_UI, 0);


        getActivity().startService(new Intent(getActivity(), SensorListener.class));
        //.putExtra("action", SensorListener.ACTION_PAUSE));
    }


    public static class TimerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//			updateGUI(intent); // or whatever method used to update your GUI fields
            String action = intent.getAction();

            Log.i("Receiver", "Broadcast received: " + action);

            if (action.equals("package.action.string")) {
//				String state = intent.getExtras().getString("extra");
             /*   long millisUntilFinished = intent.getLongExtra("countdown", 0);
                Log.e("Nailcura UpdateGUI---", "Countdown seconds remaining: " + millisUntilFinished / 1000);

                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                try {
                    if (minutes == 0) {
                        txt_confirm.setText("You have " + seconds + " sec to confirm this appointment...");
                    } else {
                        txt_confirm.setText("You have " + minutes + " min " + seconds + " sec to confirm this appointment...");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (millisUntilFinished / 1000 == 1) {
//context.startActivity(new Intent(context,ServicesActivity.class));
                    Intent intentone = new Intent(context.getApplicationContext(), ServicesActivity.class);
                    intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentone);


                }*/
                int currentTime = intent.getIntExtra("currentTime", 0);
                time_current=currentTime;
                tv_duration.setText(TimeFormatUtil.toDisplayString(currentTime));

            }

			/*if(action.equals(BroadcastService.COUNTDOWN_BR)){
//				String state = intent.getExtras().getString("extra");
				long millisUntilFinished = intent.getLongExtra("countdown", 0);
				Log.e("Nailcura UpdateGUI---", "Countdown seconds remaining: " + millisUntilFinished / 1000);
				txt_confirm.setText("You have " + millisUntilFinished / 1000 + " to confirm  this appointment...");
			}*/

        }
    }






    /*public void setUpNotification() {
        builder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.ic_timelapse_white_18dp)
                        .setContentTitle("Stopwatch running")
                        .setContentText("00:00:00")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setOngoing(true);


        Intent resultIntent = new Intent(getActivity(), Activity_Main.class);

        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addParentStack(Stopwatch.class);
        //stackBuilder.addNextIntent(resultIntent);



        *//*PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        *//*

        PendingIntent resultPendingIntent = PendingIntent.getActivity(getActivity(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

    }
*/



}
