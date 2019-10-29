/*
 * Author: TECHB0YS
 * Project: SmartWatch
 *
 */

package smart.watch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    EditText et1, et2, et3, et4;
    String email, pass, repass, user;
    ImageButton image_button;

    int REQUEST_CODE = 1;

    private static final String TAG = "CreateAccountActivity";

    private static String name;
    private static String nameCheck;
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        et1 = findViewById(R.id.create_username);
        et2 = findViewById(R.id.create_password);
        et3 = findViewById(R.id.create_email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void onClickValidation(View v) {

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
                            Toast.makeText(CreateAccountActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                            Intent create_intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                            startActivity(create_intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateAccountActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    });
        } else
            Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    public void onClickPic(View v) {
        image_button = findViewById(R.id.profilepic_btn);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Bitmap resize = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                image_button.setImageBitmap(resize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
        } else if (user.equals(userNameCheck())) {
            et1.setError(getString(R.string.username_taken));
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

    public String userNameCheck() {
        String userName = et1.getText().toString();

        DocumentReference docRef = db.collection("Login Data").document(userName);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            name = documentSnapshot.getString(KEY_NAME);
                            nameCheck = name;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateAccountActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
        return nameCheck;
    }
}
