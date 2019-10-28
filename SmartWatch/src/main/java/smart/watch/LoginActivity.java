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

    Context context = this;
    EditText userNameEditText, passwordEditText;

    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";

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

        userNameEditText = findViewById(R.id.login_username);
        passwordEditText = findViewById(R.id.login_password);

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
                final String userName = userNameEditText.getText().toString();
                final String userPassword = passwordEditText.getText().toString();

                DocumentReference docRef = db.collection("Login Data").document(userName);
                docRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String name = documentSnapshot.getString(KEY_NAME);
                                    String password = documentSnapshot.getString(KEY_PASSWORD);

                                    assert name != null;
                                    assert password != null;
                                    if (name.equals(userName) && password.equals(userPassword)) {
                                        dialog.setTitle("Logging in");
                                        dialog.setMessage("Checking user credentials...");
                                        dialog.show();

                                        new CountDownTimer(3000, 1000) {

                                            public void onTick(long millisUntilFinished) {
                                                // You don't need anything here
                                            }

                                            public void onFinish() {
                                                dialog.dismiss();
                                                Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(loginIntent);
                                            }
                                        }.start();
                                    }

                                } else {
                                    Toast.makeText(LoginActivity.this, "User Not Found!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            }
                        });
                break;
        }
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
