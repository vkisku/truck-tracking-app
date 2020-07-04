package com.vikash.truck.tracking;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.md.R;

public class TruckLoading extends AppCompatActivity {
    private String StruckNo,StruckDriverName;
    private TextInputEditText truckNo,truckDriverName;
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.truck_loading);
        truckNo = (TextInputEditText) findViewById(R.id.truck_no);
        truckDriverName = (TextInputEditText) findViewById(R.id.truck_driver_name);
        StruckNo = getIntent().getStringExtra("truckNo");
        StruckDriverName = getIntent().getStringExtra("truckDriverName");
        truckNo.setText(StruckNo);
        truckDriverName.setText(StruckDriverName);
    }
}
