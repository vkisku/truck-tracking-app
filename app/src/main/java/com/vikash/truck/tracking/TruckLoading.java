package com.vikash.truck.tracking;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.md.R;

public class TruckLoading extends AppCompatActivity{
    private String StruckNo,StruckDriverName;
    private TextInputEditText truckNo,truckDriverName;
    FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.truck_loading);
        truckNo = (TextInputEditText) findViewById(R.id.truck_no);
        truckDriverName = (TextInputEditText) findViewById(R.id.truck_driver_name);
        StruckNo = getIntent().getStringExtra("truckNo");
        StruckDriverName = getIntent().getStringExtra("truckDriverName");
        truckNo.setText(StruckNo);
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
    }
}
