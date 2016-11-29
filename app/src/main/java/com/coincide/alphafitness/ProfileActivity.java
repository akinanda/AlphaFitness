package com.coincide.alphafitness;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coincide.alphafitness.ProfileDialogFragment.onSubmitListener;

public class ProfileActivity extends AppCompatActivity implements onSubmitListener {

    ImageView iv_edit;
    TextView tv_name, tv_gender, tv_weight;
    TextView tv_avg_distance,tv_all_distance,tv_avg_time,tv_all_time,tv_all_calories,tv_avg_calories,tv_all_workouts,tv_avg_workouts;

    public static final String MyPREFERENCES = "ProfilePrefs" ;
    public static final String sp_name = "nameKey";
    public static final String sp_gender = "genderKey";
    public static final String sp_weight = "weightKey";

    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Always cast your custom Toolbar here, and set it as the ActionBar.
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        TextView tv_tb_center = (TextView) tb.findViewById(R.id.tv_tb_center);
        tv_tb_center.setText("ALPHA FITNESS");

     /*   ImageButton imgbtn_cart = (ImageButton) tb.findViewById(R.id.imgbtn_cart);
        imgbtn_cart.setVisibility(View.GONE);*/

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(false); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowCustomEnabled(false); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_weight = (TextView) findViewById(R.id.tv_weight);

        tv_avg_distance = (TextView) findViewById(R.id.tv_avg_distance);
        tv_all_distance = (TextView) findViewById(R.id.tv_all_distance);
        tv_avg_time = (TextView) findViewById(R.id.tv_avg_time);
        tv_all_time = (TextView) findViewById(R.id.tv_all_time);
        tv_all_calories = (TextView) findViewById(R.id.tv_all_calories);
        tv_avg_calories = (TextView) findViewById(R.id.tv_avg_calories);
        tv_avg_workouts = (TextView) findViewById(R.id.tv_avg_workouts);
        tv_all_workouts = (TextView) findViewById(R.id.tv_all_workouts);

        Intent iget=getIntent();

        tv_avg_distance.setText(iget.getStringExtra("avg_distance")+" km");
        tv_all_distance.setText(iget.getStringExtra("all_distance")+" km");
        tv_avg_time.setText(iget.getStringExtra("tot_workTime"));
        tv_all_time.setText(iget.getStringExtra("tot_workTime"));
        tv_all_calories.setText(iget.getStringExtra("all_calories")+ " Cal");
        tv_avg_calories.setText(iget.getStringExtra("avg_calories")+ " Cal");
        tv_all_workouts.setText(iget.getStringExtra("tot_workouts")+ " times");
        tv_avg_workouts.setText(iget.getStringExtra("avg_workouts")+ " times");

         sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        tv_name.setText(sharedpreferences.getString(sp_name,"Enter Name"));
        tv_gender.setText(sharedpreferences.getString(sp_gender,"Male"));
        tv_weight.setText(sharedpreferences.getString(sp_weight,"50"));

        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileDialogFragment fragment1 = new ProfileDialogFragment();
                fragment1.mListener = ProfileActivity.this;
                fragment1.txt_name = tv_name.getText().toString();
                fragment1.txt_gender = tv_gender.getText().toString();
                fragment1.txt_weight = tv_weight.getText().toString();
                fragment1.show(getFragmentManager(), "");
            }
        });

    }


    @Override
    public void setOnSubmitListener(String name, String gender, String weight) {
        tv_name.setText(name);
        tv_gender.setText(gender);
        tv_weight.setText(weight);


        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(sp_name, name);
        editor.putString(sp_gender, gender);
        editor.putString(sp_weight, weight);
        editor.commit();
    }
}
