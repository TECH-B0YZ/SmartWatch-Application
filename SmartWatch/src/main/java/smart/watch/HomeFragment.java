/*
 * Author: TECHB0YS
 * Project: SmartWatch
 *
 */

package smart.watch;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class HomeFragment  extends Fragment {

    private String usern;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root;
        root = inflater.inflate(R.layout.fragment_home, container, false);

        Bundle bundle = this.getArguments();

        if(getArguments()!=null) {
            usern = bundle.getString("username");
        }
        TextView tv;
        tv = root.findViewById(R.id.greeting);
        tv.setText(usern);
        return root;
    }

}
