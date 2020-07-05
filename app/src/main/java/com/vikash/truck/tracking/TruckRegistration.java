package com.vikash.truck.tracking;

import android.content.Intent;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.md.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class TruckRegistration extends AppCompatActivity {
    private String StruckNo,StruckDriverName;
    private TextInputEditText truckNo,truckDriverName;
    private TextInputLayout truckNoLayout;
    private MaterialButton register;
    private FirebaseFirestore db;
    private Truck truck;
    private CollectionReference truckRef;
    private Query query;
    private boolean truckFlag=false;
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.truck_registration);
        truckNo = (TextInputEditText) findViewById(R.id.truck_no);
        truckNoLayout=(TextInputLayout)findViewById(R.id.truck_no_layout);
        truckDriverName = (TextInputEditText) findViewById(R.id.truck_driver_name);
        register=findViewById(R.id.truck_register_btn);
        truckFlag=false;
        db = FirebaseFirestore.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                truckFlag=true;
                // Create a new user with a first and last name
                truck = new Truck(truckNo.getText().toString(), truckDriverName.getText().toString());

                truckRef = db.collection("truck");
                query = truckRef.whereEqualTo("truck_no", truckNo.getText().toString().toLowerCase());
                register.setEnabled(false);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<String> trucks = new ArrayList<>();
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                truckNoLayout.setError("Truck Already Registered");
                                Snackbar.make(getWindow().getDecorView(), "Truck Already Registered", Snackbar.LENGTH_LONG).show();
                            }
                            else {
                                //Truck Registration
                                truckRef.add(truck.toMap()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        truckNo.setText(null);
                                        truckDriverName.setText(null);
                                        Snackbar.make(getWindow().getDecorView(), "Truck Registered Successfully", Snackbar.LENGTH_LONG).show();
                                        register.setEnabled(false);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        register.setEnabled(true);
                                    }
                                });
                            }
                        }

                    }
                });

            }
        });
        truckNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                truckNoLayout.setError(null); //Clear the error
                register.setEnabled(true);
                return false;
            }
        });
    }
}
@IgnoreExtraProperties
class Truck{
    private String collectionName="truck";
    private String truckNo;
    private String truckDriverName;
    public Truck(){
    }
    public Truck(String truckNo,String truckDriverName){
        this.truckNo=truckNo;
        this.truckDriverName=truckDriverName;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("truck_no", truckNo.toLowerCase());
        result.put("truck_driver", truckDriverName);
        return result;
    }
}
