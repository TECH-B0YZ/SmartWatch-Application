/*
 * Author: TECHB0YS
 * Project: SmartWatch
 *
 */

package smart.watch;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.charts.SeriesLabel;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.util.Objects;

public class PedometerFragment extends Fragment implements SensorEventListener {

    private TextView tv_steps;
    private SensorManager sensorManager;
    private boolean running = false;
    private DecoView mDecoView;
    private int mSeries1Index;
    private final float mSeriesMax = 50f;
    private Handler mHandler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            update();
            mHandler.postDelayed(this, 1000);
        }
    };
    private float newPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pedometer, container, false);
        tv_steps = root.findViewById(R.id.steps);
        sensorManager = (SensorManager)
                Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);
        mDecoView = root.findViewById(R.id.dynamicArcView);
        createDataSeries1();
        // Start the timer
        mHandler.post(runnable);
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
            Toast.makeText(getContext(), "Sensor not found",
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
                .setRange(0, 200, 0)
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