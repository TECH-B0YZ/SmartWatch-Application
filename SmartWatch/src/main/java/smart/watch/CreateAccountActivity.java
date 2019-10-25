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

        et3=(EditText)findViewById(R.id.create_email);
        email=et3.getText().toString();

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

        if(usernameValidate() && passwordValidate() && isValidEmail(email)) {
            switch (v.getId()) {
                case R.id.create_btn:
                    Intent create_intent = new Intent(this, LoginActivity.class);
                    startActivity(create_intent);
                    break;
                case R.id.profilepic_btn:
                    Toast.makeText(this, "Select your profile picture!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public boolean usernameValidate(){
        et1=(EditText)findViewById(R.id.create_username);
        String user = et1.getText().toString();

        if(user.isEmpty()){
            et1.setError("This field can't be empty");
            return false;
        }
        else if(user.length() > 10){
            et1.setError("Field is too long");
            return false;
        }
        else if(user.length() < 3){
            et1.setError("Field is too short");
            return false;
        }
        else {
            et1.setError(null);
            return true;
        }
    }

    public boolean passwordValidate(){
        et2=(EditText)findViewById(R.id.create_password);
        String pass = et2.getText().toString();

        if(pass.isEmpty()){
            et2.setError("This field can't be empty");
            return false;
        }
        else if(pass.length() < 3){
            et2.setError("Field is too short");
            return false;
        }
        else {
            et2.setError(null);
            return true;
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
