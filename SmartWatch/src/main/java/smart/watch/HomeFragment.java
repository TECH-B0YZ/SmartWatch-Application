/*
 * Author: TECHB0YS
 * Project: SmartWatch
 *
 */

package smart.watch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    public static final String SHARED_PREFS = "sharedPrefs";
    private static final String KEY_FNAME = "first name";
    private static final String KEY_LNAME = "last name";
    private static final String KEY_STEPS = "steps";
    public static final String EMAIL = "email";
    private static final String TAG = "HomeFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView tv_name, tv_steps;
    String email_sharedPrefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        tv_name = root.findViewById(R.id.homeFuserName);
        tv_steps = root.findViewById(R.id.steps_home);

        loadEmail();
        retrieveData();

        return root;
    }

    public String loadEmail() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        email_sharedPrefs = sharedPreferences.getString(EMAIL, "");

        return email_sharedPrefs;
    }

    public void retrieveData() {
        try {
            DocumentReference docRef = db.collection("Login Data").document(loadEmail());
            docRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                final String fname = documentSnapshot.getString(KEY_FNAME);
                                final String lname = documentSnapshot.getString(KEY_LNAME);
                                final String steps = documentSnapshot.getString(KEY_STEPS);
                                try {
                                    if (steps != null) {
                                        tv_name.setText(fname + " " + lname);
                                        tv_steps.setText(steps);
                                    } else {
                                        tv_steps.setText("0");
                                    }
                                } catch (NullPointerException e) {
                                    Log.d(TAG, e.toString());
                                }

                            } else {
                                //data not found feedback
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    });
        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.toString());
        }
    }
}
