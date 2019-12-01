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

public class HomeFragment extends Fragment {
    TextView tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        DataProvider myActivity= (DataProvider) getActivity();
        tv=root.findViewById(R.id.homeFuserName);
        tv.setText(Objects.requireNonNull(myActivity).getUsername());

        return root;
    }
}
