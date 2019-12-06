/*
 * Author: TECHB0YS
 * Project: SmartWatch
 */
package smart.watch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    public static final String SHARED_PREFS = "sharedPrefs";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ChangePassword";
    public static final String SAVE_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private DocumentReference noteRef;
    Button changePasswordButton;
    EditText pass, repass;
    ProgressDialog dialog;
    String email_sharedPrefs, path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        pass = findViewById(R.id.newPassword);
        repass = findViewById(R.id.reNewPassword);

        changePasswordButton = findViewById(R.id.change_password_btn);
        changePasswordButton.setOnClickListener(this);

        path = "Login Data/" + loadEmail();
        noteRef = db.document(path);

        dialog = new ProgressDialog(ChangePassword.this);

        if (isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private boolean isTablet() {
        return (this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_password_btn:
                final String password = pass.getText().toString().trim();
                final String repassword = repass.getText().toString().trim();

                if (password.isEmpty() && !repassword.isEmpty()) {
                    Toast.makeText(this, getString(R.string.passEmpty), Toast.LENGTH_SHORT).show();
                } else if (repassword.isEmpty() && !password.isEmpty()) {
                    Toast.makeText(this, getString(R.string.passEmpty2), Toast.LENGTH_SHORT).show();
                } else if (repassword.isEmpty() && password.isEmpty()) {
                    Toast.makeText(this, getString(R.string.fieldEmpty3), Toast.LENGTH_SHORT).show();
                } else if (!password.isEmpty() && !repassword.isEmpty() && password.equals(repassword)) {

                    try {
                        DocumentReference docRef = db.collection("Login Data").document(loadEmail());
                        docRef.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Map<String, Object> note = new HashMap<>();
                                            note.put(KEY_PASSWORD, password);
                                            noteRef.set(note, SetOptions.merge());

                                            dialog.setMessage(getString(R.string.changing_password));
                                            dialog.show();

                                            new CountDownTimer(3000, 1000) {

                                                public void onTick(long millisUntilFinished) {
                                                    // You don't need anything here
                                                }

                                                public void onFinish() {
                                                    dialog.dismiss();
                                                    Intent loginIntent = new Intent(ChangePassword.this, LoginActivity.class);
                                                    startActivity(loginIntent);
                                                }
                                            }.start();

                                        } else {
                                            Toast.makeText(ChangePassword.this, getString(R.string.no_user), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ChangePassword.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    }
                                });
                        break;
                    } catch (IllegalArgumentException e) {
                        Log.d(TAG, e.toString());
                    }
                }
        }
    }

    public String loadEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        email_sharedPrefs = sharedPreferences.getString(SAVE_EMAIL, "");

        return email_sharedPrefs;
    }
}
