/*
* Author: TECHB0YS
* Project: SmartWatch
*
 */
package smart.watch;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button register_button = findViewById(R.id.register_btn);
        register_button.setOnClickListener(this);

        Button login_button = findViewById(R.id.login_btn);
        login_button.setOnClickListener(this);

    }

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

                user_login_id.putCharSequence(getString(R.string.username_java),username.getText());
                user_login_id.putCharSequence(getString(R.string.password_java),password.getText());

                user_info.putExtras(user_login_id);
                startActivity(user_info);
                break;
        }
    }

    @Override
    public void onBackPressed(){
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
                    public void onClick(DialogInterface dialog, int id) { dialog.cancel();}
                });
        alertDialogBuilder.show();
    }
}
