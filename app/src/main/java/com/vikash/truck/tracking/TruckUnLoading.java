package com.vikash.truck.tracking;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.md.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TruckUnLoading extends AppCompatActivity{
    private String StruckNo,StruckDriverName;
    private TextInputEditText truckNo,truckDriverName,datetimepicker;
    FirebaseFirestore db;
    MaterialDatePicker.Builder<?> builder;
    private MaterialDatePicker<?> picker;
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.truck_loading);
        truckNo = (TextInputEditText) findViewById(R.id.truck_no);
        truckDriverName = (TextInputEditText) findViewById(R.id.truck_driver_name);
        StruckNo = getIntent().getStringExtra("truckNo");
        StruckDriverName = getIntent().getStringExtra("truckDriverName");
        datetimepicker=(TextInputEditText) findViewById(R.id.truck_loading_datetimepicker);
        truckNo.setText(StruckNo);
        builder = MaterialDatePicker.Builder.datePicker();
        picker=builder.build();
        db = FirebaseFirestore.getInstance();
        // Create a reference to the cities collection
        CollectionReference truckRef = db.collection("truck");

// Create a query against the collection.
        Query query = truckRef.whereEqualTo("truck_no", StruckNo);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("DATABASE", document.getId() + " => " + document.getData());
                        truckDriverName.setText(document.getString("truck_driver").toString());
                    }
                } else {
                    Log.w("Database", "Error getting documents.", task.getException());
                }
            }});
        datetimepicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    picker.show(getSupportFragmentManager(), picker.toString());
                }
                catch (Exception e){
                    Log.e("PICKER",e.toString());
                }
            }
        });
        picker.addOnPositiveButtonClickListener(
                selection -> {
                    datetimepicker.setText(picker.getHeaderText());
                });
        picker.addOnNegativeButtonClickListener(
                dialog -> {
                    datetimepicker.setText(null);
                });
        picker.addOnCancelListener(
                dialog -> {
                    datetimepicker.setText(null);
                });
    }
}
