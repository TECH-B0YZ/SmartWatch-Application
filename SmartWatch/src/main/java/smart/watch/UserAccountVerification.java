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

public class UserAccountVerification extends AppCompatActivity implements View.OnClickListener {
    private static final String KEY_EMAIL = "email";
    private static final String KEY_SECURITY_QUESTION = "pet name";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SAVE_EMAIL = "email";
    private static final String TAG = "UserAccountVerification";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText email, security_answer;
    ProgressDialog dialog;
    Button submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_account_verification);

        email = findViewById(R.id.login_email_uav);
        security_answer = findViewById(R.id.security_question);

        submit_btn = findViewById(R.id.submit_request);
        submit_btn.setOnClickListener(this);

        dialog = new ProgressDialog(UserAccountVerification.this);

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
            case R.id.submit_request:
                final String email_f = email.getText().toString().trim();
                final String security_f = security_answer.getText().toString().trim();

                if (email_f.isEmpty() && !security_f.isEmpty()) {
                    Toast.makeText(this, getString(R.string.unEmpty), Toast.LENGTH_SHORT).show();
                } else if (security_f.isEmpty() && !email_f.isEmpty()) {
                    Toast.makeText(this, getString(R.string.answEmpty), Toast.LENGTH_SHORT).show();
                } else if (security_f.isEmpty() && email_f.isEmpty()) {
                    Toast.makeText(this, getString(R.string.fieldEmpty2), Toast.LENGTH_SHORT).show();
                } else if (!email_f.isEmpty() && !security_f.isEmpty()) {
                    try {
                        DocumentReference docRef = db.collection("Login Data").document(email_f);
                        docRef.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            final String email_db = documentSnapshot.getString(KEY_EMAIL);
                                            final String security_db = documentSnapshot.getString(KEY_SECURITY_QUESTION);

                                            if (!security_db.equalsIgnoreCase(security_f)) {
                                                Toast.makeText(UserAccountVerification.this, getString(R.string.incorrect_answer), Toast.LENGTH_SHORT).show();
                                            }

                                            if ((email_db.equals(email_f) && security_db.equalsIgnoreCase(security_f))) {
                                                dialog.setMessage(getString(R.string.authentication));
                                                dialog.show();

                                                new CountDownTimer(3000, 1000) {

                                                    public void onTick(long millisUntilFinished) {
                                                        // You don't need anything here
                                                    }

                                                    public void onFinish() {
                                                        saveData(email_f);
                                                        dialog.dismiss();

                                                        Intent loginIntent = new Intent(UserAccountVerification.this, ChangePassword.class);
                                                        startActivity(loginIntent);
                                                    }
                                                }.start();
                                            }
                                        } else {
                                            Toast.makeText(UserAccountVerification.this, getString(R.string.no_user), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserAccountVerification.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
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

    public void saveData(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(SAVE_EMAIL, email);
        editor.apply();
    }
}

