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
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.md.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class TruckLoading extends AppCompatActivity{
    private String StruckNo,StruckDriverName;
    private TextInputEditText truckNo,truckDriverName,datetimepicker,emptyWeight,ladenWeight;
    private FirebaseFirestore db;
    private MaterialDatePicker.Builder<?> builder;
    private MaterialDatePicker<?> picker;
    private MaterialButton submit_btn;
    private TruckLoadingData truck;
    private CollectionReference truckRef,truck_loadingRef;
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.truck_loading);
        truckNo = (TextInputEditText) findViewById(R.id.truck_no);
        truckDriverName = (TextInputEditText) findViewById(R.id.truck_driver_name);
        StruckNo = getIntent().getStringExtra("truckNo");
        StruckDriverName = getIntent().getStringExtra("truckDriverName");
        datetimepicker=(TextInputEditText) findViewById(R.id.truck_loading_datetimepicker);
        submit_btn=findViewById(R.id.truck_loading_submit_btn);
        emptyWeight=(TextInputEditText)findViewById(R.id.truck_empty_weight);
        ladenWeight=(TextInputEditText)findViewById(R.id.truck_laden_weight);
        truckNo.setText(StruckNo);
        builder = MaterialDatePicker.Builder.datePicker();
        picker=builder.build();
        db = FirebaseFirestore.getInstance();
        // Create a reference to the cities collection
        truckRef = db.collection("truck");
        truck_loadingRef = db.collection("truck_loading");

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
                    datetimepicker.setText(picker.getHeaderText());
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
                truck_loadingRef.add(Loading.toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        truckNo.setText(null);
                        truckDriverName.setText(null);
                        Snackbar.make(getWindow().getDecorView(), "Truck Loading info Added Successfully", Snackbar.LENGTH_LONG).show();
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
    class TruckLoadingData{
    private String truckNo;
    private String emptyWeight;
    private String ladenWeight;
    private String date;
    private String trip;
    public TruckLoadingData(){

    }
    public TruckLoadingData(String truckno,String emptyWeight,String ladenWeight,String date,String trip){
        this.truckNo=truckno;
        this.emptyWeight=emptyWeight;
        this.ladenWeight=ladenWeight;
        this.date=date;
        this.trip=trip;
    }
        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("truck_no", truckNo.toLowerCase());
            result.put("empty_weight", emptyWeight);
            result.put("laden_weight", ladenWeight);
            result.put("date", date);
            result.put("trip", trip);
            return result;
        }
    }
