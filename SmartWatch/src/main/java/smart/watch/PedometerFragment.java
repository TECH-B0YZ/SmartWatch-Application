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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
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
import com.hookedonplay.decoviewlib.charts.EdgeDetail;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class PedometerFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "PedometerFragment";
    private TextView tv_steps, tv_goal;
    private EditText et;
    private SensorManager sensorManager;
    private DecoView mDecoView;
    private Handler mHandler = new Handler();
    private boolean running = false;
    private float goal = 500;
    private int mSeries1Index;
    private float newPosition;
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeries1;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            update();
            mHandler.postDelayed(this, 1000);
        }
    };
    private double mLastRandom = 2;
    private Random mRand = new Random();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference SensorData = db.collection("Login Data");
    private Map<String, Object> note = new HashMap<>();

    private static final String KEY_STEPS = "Steps";

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
                try {
                    et = root.findViewById(R.id.goal_et);
                    goal = Float.valueOf(et.getText().toString());
                    tv_goal = root.findViewById(R.id.steps_goal);
                    tv_goal.setText(String.format("/%d", (int) goal));
                } catch (IllegalArgumentException e) {
                    Log.d(TAG, e.toString());
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();



        GraphView graph = (GraphView) root.findViewById(R.id.graph);
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint((Date)calendar.getTime(), -2),
                new DataPoint((Date)calendar.getTime(), 1),
                new DataPoint((Date)calendar.getTime(), 3)
        });
        graph.addSeries(series);
        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        graph.getGridLabelRenderer().reloadStyles();
        series.setShape(PointsGraphSeries.Shape.POINT);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d3.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

//        GraphView graph = root.findViewById(R.id.graph);
//        mSeries1 = new LineGraphSeries<>(generateData());
//        mSeries1.setColor(Color.GREEN);
//        graph.getGridLabelRenderer().setGridColor(Color.BLACK);
//        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
//        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
//        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
//        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
//        graph.getGridLabelRenderer().reloadStyles();
//        graph.addSeries(mSeries1);

        return root;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            newPosition = event.values[0];
            tv_steps.setText(String.valueOf(event.values[0]));

            note.put(KEY_STEPS,newPosition);
            SensorData.document("Monado").update(note).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), getString(R.string.no_connection),Toast.LENGTH_SHORT).show();
                        }
                    });
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void createDataSeries1() {
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 0, 255, 0))
                .setRange(0, goal, 0)
                .setInitialVisibility(false)
                .setLineWidth(32f)
                .addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_OUTER,
                        Color.parseColor("#22000000"), 0.4f))
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

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i = 0; i < count; i++) {
            double x = i;
            double f = mRand.nextDouble() * 0.15 + 0.3;
            double y = Math.sin(i * f + 2) + mRand.nextDouble() * 0.3;
            //double y = newPosition;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    private double getRandom() {
        return mLastRandom += mRand.nextDouble() * 0.5 - 0.25;
    }
}