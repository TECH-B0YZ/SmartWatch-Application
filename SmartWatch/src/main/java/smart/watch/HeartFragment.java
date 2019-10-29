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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class HeartFragment extends Fragment{

    private View layoutHB;
    private ImageView heartImage, heartBeat, heartBeat1;
    private Handler handlerAnimationCIMG;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_heart, container, false);

        this.handlerAnimationCIMG = new Handler();
        this.layoutHB = root.findViewById(R.id.layoutBeat);
        this.heartImage =  root.findViewById(R.id.heart_image);
        this.heartBeat =  root.findViewById(R.id.heart_beat);
        this.heartBeat1 =  root.findViewById(R.id.heart_beat1);

        Glide.with(getActivity().getBaseContext()).load(R.drawable.heart_ph)
                .apply(new RequestOptions().circleCrop()).into(heartImage);

        root.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startTask();

            }
        });
        root.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopTask();

            }
        });


        return root;
    }

    private void startTask(){
        this.runnableAnim.run();
        this.layoutHB.setVisibility(View.VISIBLE);
    }

    private void stopTask(){
        this.handlerAnimationCIMG.removeCallbacks(runnableAnim);
        this.layoutHB.setVisibility(View.GONE);
    }


    private Runnable runnableAnim = new Runnable(){

        @Override
        public void run() {
            heartBeat.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    heartBeat.setScaleX(1f);
                    heartBeat.setScaleY(1f);
                    heartBeat.setAlpha(1f);
                }
            });

            heartBeat1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(700).withEndAction(new Runnable() {
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


}
