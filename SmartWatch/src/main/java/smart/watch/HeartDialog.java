package smart.watch;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class HeartDialog extends AppCompatDialogFragment {

    private EditText editTextAlert;

    private HeartDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        editTextAlert = view.findViewById(R.id.edit_alert);

        builder.setView(view)
                .setTitle("Set Alarm")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String alert = editTextAlert.getText().toString();
                            listener.applyTexts(alert);
                        } catch (NullPointerException e) {
                            e.getMessage();
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (HeartDialogListener) context;
        } catch (ClassCastException e) {

        }
    }

    public interface HeartDialogListener {
        void applyTexts(String username);
    }
}
