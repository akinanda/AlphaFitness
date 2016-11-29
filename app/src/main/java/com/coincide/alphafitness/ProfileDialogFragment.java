package com.coincide.alphafitness;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProfileDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    Button btn_save;
    EditText edt_name,edt_weight;
    onSubmitListener mListener;
    String txt_name = "";
    String txt_gender = "";
    String txt_weight = "";
    String txt_selectedGender = "";

    interface onSubmitListener {
        void setOnSubmitListener(String name,String gender,String weight);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.dialogfragment_profile);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        btn_save = (Button) dialog.findViewById(R.id.btn_save);
        edt_name = (EditText) dialog.findViewById(R.id.edt_name);
        edt_weight = (EditText) dialog.findViewById(R.id.edt_weight);

        if (txt_name.equalsIgnoreCase("Enter Name"))
        {
         edt_name.setText("");
        }else {
            edt_name.setText(txt_name);
        }

        edt_weight.setText(txt_weight);

        btn_save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.setOnSubmitListener(edt_name.getText().toString(),txt_selectedGender,edt_weight.getText().toString());
                dismiss();
            }
        });

        // Spinner element
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spin_gender);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);




        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Male");
        categories.add("Female");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        if(txt_gender.equalsIgnoreCase("Male")){
            spinner.setSelection(0);
        }else if(txt_gender.equalsIgnoreCase("Female")){
            spinner.setSelection(1);
        }else{
            spinner.setSelection(0);
        }


        return dialog;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
txt_selectedGender=item;
        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}