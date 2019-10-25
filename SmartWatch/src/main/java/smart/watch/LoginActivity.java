package smart.watch;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button register_button = findViewById(R.id.register_btn);
        register_button.setOnClickListener(this);

        Button login_button = findViewById(R.id.login_btn);
        login_button.setOnClickListener(this);

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn:
                Intent register_intent = new Intent(this,CreateAccountActivity.class);
                startActivity(register_intent);
                break;

            case R.id.login_btn:
                Intent user_info = new Intent(this, HomeActivity.class);
                Bundle user_login_id = new Bundle();

                EditText username = findViewById(R.id.login_username);
                EditText password = findViewById(R.id.login_password);

                user_login_id.putCharSequence("username",username.getText());
                user_login_id.putCharSequence("password",password.getText());

                user_info.putExtras(user_login_id);
                startActivity(user_info);
                break;
        }
    }
}
