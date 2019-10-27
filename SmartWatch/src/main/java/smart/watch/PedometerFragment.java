/*
 * Author: TECHB0YS
 * Project: SmartWatch
 *
 */

package smart.watch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.Objects;

public class PedometerFragment extends Fragment implements SensorEventListener {

    private TextView tv_steps,tv_goal;
    private SensorManager sensorManager;
    private boolean running = false;
    private DecoView mDecoView;
    private int mSeries1Index;
    private Handler mHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            update();
            mHandler.postDelayed(this, 1000);
        }
    };
    private float newPosition;
    private EditText et;
    private float goal =200;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_pedometer, container, false);
        tv_steps = root.findViewById(R.id.steps);
        sensorManager = (SensorManager)
                Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);
        mDecoView = root.findViewById(R.id.dynamicArcView);
        createDataSeries1();
        // Start the timer
        mHandler.post(runnable);

        Button btn1 = root.findViewById(R.id.goal_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                et = root.findViewById(R.id.goal_et);
                goal = Float.valueOf(et.getText().toString());
                tv_goal = root.findViewById(R.id.steps_goal);
                tv_goal.setText(String.format("/%d", (int) goal));
                createDataSeries1();
                mHandler.post(runnable);

            }
        });

        Button btn2 = root.findViewById(R.id.graph_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here we will use the button to view a graph that will display the growth
                // of steps over a period of time
            }
        });

        return root;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            newPosition = event.values[0];
            tv_steps.setText(String.valueOf(event.values[0]));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor,
                    SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(getContext(), getString(R.string.sensor_not_found),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void createDataSeries1() {
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 0, 255, 0))
                .setRange(0, goal, 0)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_OUTER,
                        Color.parseColor("#22000000"),0.4f))
                .setInterpolator(new OvershootInterpolator())
                .setShowPointWhenEmpty(false)
                .setCapRounded(false)
                .setInset(new PointF(32f, 32f))
                .setDrawAsPoint(false)
                .setSpinClockwise(true)
                .setSpinDuration(6000)
                .setChartStyle(SeriesItem.ChartStyle.STYLE_DONUT)
                .build();
        mSeries1Index = mDecoView.addSeries(seriesItem1);
    }

    private void update() {
        mDecoView.addEvent(new
                DecoEvent.Builder(newPosition).setIndex(mSeries1Index).build());
    }

}