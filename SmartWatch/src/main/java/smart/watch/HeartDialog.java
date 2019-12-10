/*
 * Author: TECHB0YS
 * Project: SmartWatch
 */
package smart.watch;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
                .setTitle(getString(R.string.set_alarm))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
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
            e.getMessage();
        }
    }

    public interface HeartDialogListener {
        void applyTexts(String alert);
    }
}
