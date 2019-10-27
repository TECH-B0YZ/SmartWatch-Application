/*
 * Author: TECHB0YS
 * Project: SmartWatch
 *
 */

package smart.watch;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class HomeFragment  extends Fragment {

    private BluetoothAdapter bluetoothadapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        bluetoothadapter = BluetoothAdapter.getDefaultAdapter();


        ImageButton btnIMG = root.findViewById(R.id.btn_img);
        Button btnON = root.findViewById(R.id.btn_on);
        Button btnOFF = root.findViewById(R.id.btn_off);

        btnON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothEnable();
            }
        });

        btnOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDisable();
            }
        });

        btnIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings");
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return root;
    }

    private void BluetoothEnable(){

        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        startActivityForResult(bluetoothIntent, 1);

        Toast.makeText(getContext(),"Bluetooth is ON",Toast.LENGTH_SHORT).show();;

    }

    private void BluetoothDisable(){

        bluetoothadapter.disable();

        Toast.makeText(getContext(),"Bluetooth is OFF",Toast.LENGTH_SHORT).show();

    }

}
