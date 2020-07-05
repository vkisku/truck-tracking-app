package com.vikash.truck.tracking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ml.md.R;

import static android.content.ContentValues.TAG;

/**
 * Fragment representing the login screen for Shrine.
 */
public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.shr_login_fragment, container, false);
        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_text_input);
        final TextInputEditText email = view.findViewById(R.id.username_edit_text);
        final TextInputEditText password = view.findViewById(R.id.password_edit_text);
//        email.setText("admin@admin.com");
//        password.setText("password");
        MaterialButton nextButton = view.findViewById(R.id.next_button);
        mAuth=FirebaseAuth.getInstance();;
        // Set an error if the password is less than 8 characters.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
//                                    FirebaseUser user = mAuth.getCurrentUser();
                                   passwordTextInput.setError(null); // Clear the error
                                   Intent intent = new Intent(getActivity(), MainActivity.class);
                                   startActivity(intent);
                                } else {
                                    passwordTextInput.setError(getString(R.string.shr_error_password));
                                }
                            }
                        });
            }
        });
        // Clear the error once more than 8 characters are typed.
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (isUserValid(email.getText().toString(),password.getText().toString())) {
                    passwordTextInput.setError(null); //Clear the error
                }
                return false;
            }
        });

        return view;
    }
    private boolean isUserValid(String email,String password) {
        return true;
//        return text != null && text.length() >= 8;
    }
    // "isPasswordValid" from "Navigate to the next Fragment" section method goes here

}
