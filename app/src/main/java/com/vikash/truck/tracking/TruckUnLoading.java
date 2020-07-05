package com.vikash.truck.tracking;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.md.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class TruckUnLoading extends AppCompatActivity{
    private String StruckNo,StruckDriverName;
    private TextInputEditText truckNo,truckDriverName,datetimepicker,emptyWeight,ladenWeight;
    private FirebaseFirestore db;
    private MaterialDatePicker.Builder<?> builder;
    private MaterialButton submit_btn;
    private MaterialDatePicker<?> picker;
    private CollectionReference truckRef,truck_unloadingRef;

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
        submit_btn=findViewById(R.id.truck_loading_submit_btn);
        emptyWeight=(TextInputEditText)findViewById(R.id.truck_empty_weight);
        ladenWeight=(TextInputEditText)findViewById(R.id.truck_laden_weight);
        builder = MaterialDatePicker.Builder.datePicker();
        picker=builder.build();
        db = FirebaseFirestore.getInstance();
        // Create a reference to the cities collection
        truckRef = db.collection("truck");
        truck_unloadingRef = db.collection("truck_loading");

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

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TruckLoadingData Loading=new TruckLoadingData(
                        truckNo.getText().toString().toLowerCase(),
                        emptyWeight.getText().toString().toLowerCase(),
                        ladenWeight.getText().toString().toLowerCase(),
                        datetimepicker.getText().toString().toLowerCase(),
                        "0"
                );
                truck_unloadingRef.add(Loading.toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        truckNo.setText(null);
                        truckDriverName.setText(null);
                        Snackbar.make(getWindow().getDecorView(), "Truck UnLoading info Added Successfully", Snackbar.LENGTH_LONG).show();
                        submit_btn.setEnabled(false);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        submit_btn.setEnabled(true);
                    }
                });
            }
        });
    }
}
