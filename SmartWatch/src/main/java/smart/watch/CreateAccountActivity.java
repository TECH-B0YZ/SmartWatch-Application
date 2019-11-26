/*
 * Author: TECHB0YS
 * Project: SmartWatch
 *
 */

package smart.watch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CreateAccountActivity extends AppCompatActivity {
    EditText et1, et2, et3, et4;
    String email, pass, repass, user;
    ImageButton image_button;
    Button createUser;

    private Toolbar mTopToolbar;

    int REQUEST_CODE = 1;

    private static final String TAG = "CreateAccountActivity";

    private static String name;
    ProgressDialog dialog;
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private Timer timer = new Timer();
    private final long DELAY = 1000;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mTopToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new ProgressDialog(CreateAccountActivity.this);

        et1 = findViewById(R.id.create_username);
        et2 = findViewById(R.id.create_password);
        et3 = findViewById(R.id.create_email);

        createUser = findViewById(R.id.create_btn);

        et1.addTextChangedListener(new TextWatcher() {
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
                            userNameCheck();
                        }

                    }, DELAY);
                }
            }
        });
    }

    public void onClickValidation(View v) {
        if (createUser.isEnabled()) {
            if (usernameValidate() && passwordValidate() && isValidEmail() && rePasswordValidate()) {

                String userName = et1.getText().toString();
                String email = et3.getText().toString();
                String password = et2.getText().toString();

                CollectionReference loginData = db.collection("Login Data");
                Map<String, Object> note = new HashMap<>();
                note.put(KEY_NAME, userName);
                note.put(KEY_EMAIL, email);
                note.put(KEY_PASSWORD, password);

                loginData.document(userName).set(note)
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
            et1.setError(getString(R.string.username_taken));
    }

//    public void onClickPic(View v) {
//        image_button = findViewById(R.id.profilepic_btn);
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, getString(R.string.picture_selection)), REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null
//                && data.getData() != null) {
//            Uri uri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                Bitmap resize = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
//                image_button.setImageBitmap(resize);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public boolean usernameValidate() {
        user = et1.getText().toString();

        if (user.isEmpty()) {
            et1.setError(getString(R.string.error_empty));
            return false;
        } else if (user.length() > 25) {
            et1.setError(getString(R.string.too_long));
            return false;
        } else if (user.length() < 3) {
            et1.setError(getString(R.string.too_short));
            return false;
        } else if (user.contains(" ")) {
            et1.setError(getString(R.string.space));
            return false;
        } else {
            et1.setError(null);
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

    public void userNameCheck() {
        String userName = et1.getText().toString();

        DocumentReference docRef = db.collection("Login Data").document(userName);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            et1.setError(getString(R.string.username_taken));
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
