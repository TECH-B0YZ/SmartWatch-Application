/*
 * Author: TECHB0YS
 * Project: SmartWatch
 */

package smart.watch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CreateAccountActivity extends AppCompatActivity {
    EditText et2, et3, et4, et5, et6, et7;
    String email, pass, repass, fname, lname, pname;
    Button createUser;

    private Toolbar mTopToolbar;
    private static final String TAG = "CreateAccountActivity";

    ProgressDialog dialog;
    private static final String KEY_FNAME = "first name";
    private static final String KEY_LNAME = "last name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SECURITY_QUESTION = "pet name";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_HB = "heart rate";

    private Timer timer = new Timer();
    private final long DELAY = 1000;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        if (isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mTopToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new ProgressDialog(CreateAccountActivity.this);

        et2 = findViewById(R.id.create_password);
        et3 = findViewById(R.id.create_email);
        et5 = findViewById(R.id.create_fn);
        et6 = findViewById(R.id.create_ln);
        et7 = findViewById(R.id.security_question_answer);

        createUser = findViewById(R.id.create_btn);

        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                createUser.setEnabled(false);
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before,
                                      int count) {
                if (timer != null)
                    timer.cancel();
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() >= 3) {

                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            userEmailCheck();
                        }

                    }, DELAY);
                }
            }
        });
    }

    private boolean isTablet() {
        return (this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void onClickValidation(View v) {
        if (createUser.isEnabled()) {
            if (isValidEmail() && passwordValidate() && rePasswordValidate() && firstNameValidate() && lastNameValidate() && petNameValidate()) {

                String email = et3.getText().toString();
                String password = et2.getText().toString();
                String firstName = et5.getText().toString();
                String lastName = et6.getText().toString();
                String petName = et7.getText().toString();

                CollectionReference loginData = db.collection("Login Data");
                Map<String, Object> note = new HashMap<>();

                note.put(KEY_EMAIL, email);
                note.put(KEY_SECURITY_QUESTION, petName);
                note.put(KEY_PASSWORD, password);
                note.put(KEY_FNAME, firstName);
                note.put(KEY_LNAME, lastName);
                note.put(KEY_STEPS, "0");
                note.put(KEY_HB, "0");

                loginData.document(email).set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.setMessage(getString(R.string.account_creation));
                                dialog.show();

                                new CountDownTimer(3000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        // You don't need anything here
                                    }

                                    public void onFinish() {
                                        dialog.dismiss();
                                        Intent loginIntent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                        startActivity(loginIntent);
                                    }
                                }.start();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateAccountActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            }
                        });
            } else
                Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
        } else
            et2.setError(getString(R.string.username_taken));
    }

    public boolean firstNameValidate() {
        fname = et5.getText().toString();

        if (fname.isEmpty()) {
            et5.setError(getString(R.string.error_empty));
            return false;
        } else if (fname.length() < 3) {
            et5.setError(getString(R.string.too_short));
            return false;
        } else if (fname.contains(" ")) {
            et5.setError(getString(R.string.space));
            return false;
        } else {
            et5.setError(null);
            return true;
        }
    }

    public boolean lastNameValidate() {
        lname = et6.getText().toString();

        if (lname.isEmpty()) {
            et6.setError(getString(R.string.error_empty));
            return false;
        } else if (lname.length() < 3) {
            et6.setError(getString(R.string.too_short));
            return false;
        } else if (lname.contains(" ")) {
            et6.setError(getString(R.string.space));
            return false;
        } else {
            et6.setError(null);
            return true;
        }
    }

    public boolean petNameValidate() {
        pname = et7.getText().toString();

        if (pname.isEmpty()) {
            et7.setError(getString(R.string.error_empty));
            return false;
        } else if (pname.length() < 3) {
            et7.setError(getString(R.string.too_short));
            return false;
        } else if (pname.contains(" ")) {
            et7.setError(getString(R.string.space));
            return false;
        } else {
            et7.setError(null);
            return true;
        }
    }

    public boolean passwordValidate() {
        pass = et2.getText().toString();

        if (pass.isEmpty()) {
            et2.setError(getString(R.string.error_empty));
            return false;
        } else if (pass.length() < 3) {
            et2.setError(getString(R.string.too_short));
            return false;
        } else if (pass.contains(" ")) {
            et2.setError(getString(R.string.space));
            return false;
        } else {
            et2.setError(null);
            return true;
        }
    }

    public boolean isValidEmail() {
        email = et3.getText().toString().trim();

        if (email.isEmpty()) {
            et3.setError(getString(R.string.error_empty));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et3.setError(getString(R.string.proper_email));
            return false;
        } else if (email.contains(" ")) {
            et3.setError(getString(R.string.space));
            return false;
        } else {
            et3.setError(null);
            return true;
        }
    }

    public boolean rePasswordValidate() {
        et4 = findViewById(R.id.confirm_password);
        repass = et4.getText().toString();

        if (repass.isEmpty()) {
            et4.setError(getString(R.string.error_empty));
            return false;
        } else if (!repass.equals(pass)) {
            et4.setError(getString(R.string.not_equal));
            return false;
        } else if (repass.contains(" ")) {
            et4.setError(getString(R.string.space));
            return false;
        } else {
            et4.setError(null);
            return true;
        }
    }

    public void userEmailCheck() {
        String user_email = et3.getText().toString();

        DocumentReference docRef = db.collection("Login Data").document(user_email);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            et3.setError(getString(R.string.username_taken));
                            createUser.setEnabled(false);
                        } else {
                            createUser.setEnabled(true);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateAccountActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}
