/*
 * Author: TECHB0YS
 * Project: SmartWatch
 *
 */
package smart.watch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "email";

    Context context = this;
    EditText emailEditText, passwordEditText;

    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    String email, password;

    private static final String TAG = "LoginActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button register_button = findViewById(R.id.register_btn);
        register_button.setOnClickListener(this);

        Button login_button = findViewById(R.id.login_btn);
        login_button.setOnClickListener(this);

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);

        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        dialog = new ProgressDialog(LoginActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn:
                Intent register_intent = new Intent(this, CreateAccountActivity.class);
                startActivity(register_intent);
                break;

            case R.id.login_btn:
                final String userEmail = emailEditText.getText().toString().trim();
                final String userPassword = passwordEditText.getText().toString().trim();

                if (userEmail.isEmpty() && !userPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, getString(R.string.unEmpty), Toast.LENGTH_SHORT).show();
                } else if (userPassword.isEmpty() && !userEmail.isEmpty()) {
                    Toast.makeText(LoginActivity.this, getString(R.string.passEmpty), Toast.LENGTH_SHORT).show();
                } else if (userPassword.isEmpty() && userEmail.isEmpty()) {
                    Toast.makeText(LoginActivity.this, getString(R.string.fieldEmpty), Toast.LENGTH_SHORT).show();
                }

                try {
                    DocumentReference docRef = db.collection("Login Data").document(userEmail);
                    docRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        final String email_db = documentSnapshot.getString(KEY_EMAIL);
                                        String password_db = documentSnapshot.getString(KEY_PASSWORD);

                                        if (!password_db.equals(userPassword)) {
                                            Toast.makeText(LoginActivity.this, getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show();
                                        }

                                        if ((email_db.equals(userEmail) && password_db.equals(userPassword))) {
                                            dialog.setMessage(getString(R.string.authentication));
                                            dialog.show();

                                            new CountDownTimer(3000, 1000) {

                                                public void onTick(long millisUntilFinished) {
                                                    // You don't need anything here
                                                }

                                                public void onFinish() {
                                                    saveData(email_db);

                                                    dialog.dismiss();

                                                    Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                                    startActivity(loginIntent);
                                                }
                                            }.start();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.no_user), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
                    break;
                } catch (IllegalArgumentException e) {
                    Log.d(TAG, e.toString());
                }
        }
    }

    public void saveData(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(EMAIL, email);
        editor.apply();
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(getString(R.string.warning));
        alertDialogBuilder
                .setMessage(getString(R.string.err_yes))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.show();
    }
}
