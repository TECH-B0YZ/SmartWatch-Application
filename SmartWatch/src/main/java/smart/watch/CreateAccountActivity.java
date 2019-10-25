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
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class CreateAccountActivity extends AppCompatActivity{
    EditText et1, et2, et3, et4;
    String email, pass, repass, user;

    ImageButton image_button;

    int REQUEST_CODE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void onClickValidation(View v) {

        if (usernameValidate() && passwordValidate() && isValidEmail() && rePasswordValidate()) {
                    Intent create_intent = new Intent(this, LoginActivity.class);
                    startActivity(create_intent);
        }else
            Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
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
        et1 = findViewById(R.id.create_username);
        user = et1.getText().toString();

        if (user.isEmpty()) {
            et1.setError(getString(R.string.error_empty));
            return false;
        } else if (user.length() > 10) {
            et1.setError(getString(R.string.too_long));
            return false;
        } else if (user.length() < 3) {
            et1.setError(getString(R.string.too_short));
            return false;
        } else {
            et1.setError(null);
            return true;
        }
    }

    public boolean passwordValidate() {
        et2 = findViewById(R.id.create_password);
        pass = et2.getText().toString();

        if (pass.isEmpty()) {
            et2.setError(getString(R.string.error_empty));
            return false;
        } else if (pass.length() < 3) {
            et2.setError(getString(R.string.too_short));
            return false;
        } else {
            et2.setError(null);
            return true;
        }
    }

    public boolean isValidEmail() {
        et3 = findViewById(R.id.create_email);
        email = et3.getText().toString().trim();

        if (email.isEmpty()) {
            et3.setError(getString(R.string.error_empty));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et3.setError(getString(R.string.proper_email));
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
        } else {
            et4.setError(null);
            return true;
        }
    }

    public void onClickPic(View v){
        image_button = findViewById(R.id.profilepic_btn);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
    }


//    public boolean picValidation(){
//        image_button = findViewById(R.id.profilepic_btn);
//
//        if(!image_button.isSelected()){
//            Toast.makeText(getApplicationContext(),"No pic is selected",Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        else
//            return true;
//    }
}
