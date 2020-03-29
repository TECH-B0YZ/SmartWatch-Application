/*
 * Author: TECHB0YS
 * Project: SmartWatch
 */

package smart.watch;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hookedonplay.decoviewlib.DecoView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class PedometerFragment extends Fragment implements SensorEventListener {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "email";
    private static final String TAG = "PedometerFragment";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_BEATS ="heart rate";

    private String email_sharedPrefs;
    private TextView tv_steps,tv_beats, tv_goal;
    private EditText et;
    private Handler mHandler = new Handler();
    private boolean running = false;
    private float goal = 500;

    private Runnable mTimer1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference SensorData = db.collection("Login Data");
    private Map<String, Object> note = new HashMap<>();


    private final String DEVICE_ADDRESS="A4:CF:12:9A:CB:CE";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Button startButton,stopButton;
    private boolean deviceConnected=false;
    private byte[] buffer;
    private boolean stopThread;


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_pedometer, container, false);


        tv_steps = root.findViewById(R.id.steps);
        tv_beats=root.findViewById(R.id.steps2);
        // Start the timer
        mHandler.post(runnable);

        startButton = (Button) root.findViewById(R.id.buttonStart);
        //Starts the bluetooth connection.
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BTinit()) //This will initilize bluetooth which will start looking for the ESP32 device.
                {
                    if(BTconnect()) //If connected, the beginListenForData will start working and a toast will appear.
                    {
                        setUiEnabled(true);
                        deviceConnected=true;
                        beginListenForData();
                        Toast.makeText(getActivity(),"ESP32 connected,",Toast.LENGTH_SHORT).show();


                    }

                }
            }
        });



        stopButton = (Button) root.findViewById(R.id.buttonStop);
        //Used to stop the bluetooth connection.
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread = true;
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //setUiEnabled(false);
                deviceConnected=false;
                Toast.makeText(getActivity(),"ESP32 disconnected",Toast.LENGTH_SHORT).show();
            }
        });


        //Consider dropping this
        Button btn1 = root.findViewById(R.id.goal_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                try {
                    et = root.findViewById(R.id.goal_et);
                    goal = Float.parseFloat(et.getText().toString());
                    tv_goal = root.findViewById(R.id.steps_goal);
                    tv_goal.setText(String.format("/%d", (int) goal));
                } catch (IllegalArgumentException e) {
                    Log.d(TAG, e.toString());
                }
            }
        });



        return root;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;

        mTimer1 = new Runnable() {
            @Override
            public void run() {
                //mSeries1.resetData(generateData());
                mHandler.postDelayed(this, 300);
            }
        };
        mHandler.postDelayed(mTimer1, 300);

    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
        running = false;
    }


    public String loadEmail() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        email_sharedPrefs = sharedPreferences.getString(EMAIL, "");
        return email_sharedPrefs;
    }


    private void setUiEnabled(boolean bool)
    {
        //startButton.setEnabled(!bool);
        //sendButton.setEnabled(bool);
        //stopButton.setEnabled(bool);
        //textView.setEnabled(bool);

    }

    private boolean BTinit()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getActivity(),"Device doesnt Support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(getActivity(),"Please Pair the Device first",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
        return found;
    }

    private boolean BTconnect()
    {
        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return connected;
    }

    private void beginListenForData()
    {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try
                    {
                        int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            final int read = inputStream.read(rawBytes);
                            final String string=new String(rawBytes);
                            handler.post(new Runnable() {
                                public void run()
                                {

                                    //This will break up the string into two.
                                    String[] numbers = string.split(":");

                                    //This will set the strings on the screen.
                                    tv_steps.setText(numbers[0]);
                                    tv_beats.setText(numbers[1]);


                                    //These will convert the index arrays into strings.(Used for database purposes.)
                                    final String stepSend = String.valueOf(numbers[0]);
                                    final String beatSend = String.valueOf(numbers[1]);

                                    //These next two are used for debugging.(Can be seen in logcat.)
                                    Log.i(TAG,"Step: "+stepSend);
                                    Log.i(TAG,"H.Beats: "+beatSend);


                                    //Setting up Database.
                                    loadEmail();
                                    SensorData.document(loadEmail()).update(note).
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    note.put(KEY_STEPS, stepSend); // This will send steps to database
                                                    note.put(KEY_BEATS,beatSend);  // This will send heartbeats to database.
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                }
                            });



                        }


                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }
}