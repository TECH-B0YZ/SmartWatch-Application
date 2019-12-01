/*
 * Author: TECHB0YS
 * Project: SmartWatch
 *
 */

package smart.watch;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Random;

public class HeartFragment extends DialogFragment implements HeartDialog.HeartDialogListener {

    private View layoutHB;
    private ImageView heartImage, heartBeat, heartBeat1;
    private Handler handlerAnimationCIMG;
    private TextView textViewAlert;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_heart, container, false);

        final TextView hb = (TextView)root.findViewById(R.id.textView3);
        textViewAlert = (TextView) root.findViewById(R.id.textView5);
        final Random random = new Random();

        this.handlerAnimationCIMG = new Handler();
        this.layoutHB = root.findViewById(R.id.layoutBeat);
        this.heartImage = root.findViewById(R.id.heart_image);
        this.heartBeat = root.findViewById(R.id.heart_beat);
        this.heartBeat1 = root.findViewById(R.id.heart_beat1);

        Glide.with(getActivity().getBaseContext()).load(R.drawable.ic_heart)
                .apply(new RequestOptions().circleCrop()).into(heartImage);

        root.findViewById(R.id.hr_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startTask();

                int num = random.nextInt(25) + 60;

                hb.setText(String.valueOf(num));


            }
        });
        root.findViewById(R.id.hr_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopTask();
                hb.setText("0");

            }
        });

        root.findViewById(R.id.hr_set_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDialog();


            }
        });

        return root;
    }

    private void startTask() {
        this.runnableAnim.run();
        this.layoutHB.setVisibility(View.VISIBLE);
    }

    private void stopTask() {
        this.handlerAnimationCIMG.removeCallbacks(runnableAnim);
        this.layoutHB.setVisibility(View.GONE);
        this.layoutHB.setVisibility(View.VISIBLE);
    }


    private Runnable runnableAnim = new Runnable() {

        @Override
        public void run() {
            heartBeat.animate().scaleX(1.25f).scaleY(1.25f).alpha(0f).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    heartBeat.setScaleX(1f);
                    heartBeat.setScaleY(1f);
                    heartBeat.setAlpha(1f);
                }
            });

            heartBeat1.animate().scaleX(1.25f).scaleY(1.25f).alpha(0f).setDuration(700).withEndAction(new Runnable() {
                @Override
                public void run() {
                    heartBeat1.setScaleX(1f);
                    heartBeat1.setScaleY(1f);
                    heartBeat1.setAlpha(1f);
                }
            });

            handlerAnimationCIMG.postDelayed(runnableAnim, 1500);
        }
    };

    public void openDialog() {
        HeartDialog exampleDialog = new HeartDialog();
        exampleDialog.show(getActivity().getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String username) {
        textViewAlert.setText(username);

    }


}
