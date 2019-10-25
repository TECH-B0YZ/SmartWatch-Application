/*
 * Author: TECHB0YS
 * Project: SmartWatch
 *
 */

package smart.watch;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener  {
    EditText et1,et2,et3;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Button create_button = findViewById(R.id.create_btn);
        create_button.setOnClickListener(this);

        ImageButton image_button = findViewById(R.id.profilepic_btn);
        image_button.setOnClickListener(this);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

        if(usernameValidate() && passwordValidate() && isValidEmail()) {
            switch (v.getId()) {
                case R.id.create_btn:
                    Intent create_intent = new Intent(this, LoginActivity.class);
                    startActivity(create_intent);
                    break;
                case R.id.profilepic_btn:
                    Toast.makeText(this, R.string.pfp_selection, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        else
            Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    public boolean usernameValidate(){
        et1=findViewById(R.id.create_username);
        String user = et1.getText().toString();

        if(user.isEmpty()){
            et1.setError(getString(R.string.error_empty));
            return false;
        }
        else if(user.length() > 10){
            et1.setError(getString(R.string.too_long));
            return false;
        }
        else if(user.length() < 3){
            et1.setError(getString(R.string.too_short));
            return false;
        }
        else {
            et1.setError(null);
            return true;
        }
    }

    public boolean passwordValidate(){
        et2=findViewById(R.id.create_password);
        String pass = et2.getText().toString();

        if(pass.isEmpty()){
            et2.setError(getString(R.string.error_empty));
            return false;
        }
        else if(pass.length() < 3){
            et2.setError(getString(R.string.too_short));
            return false;
        }
        else {
            et2.setError(null);
            return true;
        }
    }

    public boolean isValidEmail() {
        et3=findViewById(R.id.create_email);
        email= et3.getText().toString().trim();

        if(email.isEmpty()){
            et3.setError(getString(R.string.error_empty));
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et3.setError(getString(R.string.proper_email));
            return false;
        }
        else{
            et3.setError(null);
            return true;
        }
    }
}
