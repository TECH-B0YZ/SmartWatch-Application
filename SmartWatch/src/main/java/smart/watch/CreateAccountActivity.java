package smart.watch;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener  {

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
        switch (v.getId()) {
            case R.id.create_btn:
                Intent create_intent = new Intent(this,LoginActivity.class);
                startActivity(create_intent);
                break;
            case R.id.profilepic_btn:
                Toast.makeText(this,"Select your profile picture!",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
