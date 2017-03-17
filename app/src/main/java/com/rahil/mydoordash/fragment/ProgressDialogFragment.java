package com.rahil.mydoordash.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.rahil.mydoordash.R;


public class ProgressDialogFragment extends DialogFragment {

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private String progressMessage;
    private ProgressDialogActionListener progressDialogActionListener;

    public ProgressDialogFragment() {
    }

    public static ProgressDialogFragment newInstance(String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            progressDialogActionListener = (ProgressDialogActionListener) activity;
        } catch (ClassCastException exception) {
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implement "
                    + ProgressDialogActionListener.class.getSimpleName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);

        Bundle arguments = getArguments();
        progressMessage = arguments != null && arguments.containsKey(EXTRA_MESSAGE)
                ? arguments.getString(EXTRA_MESSAGE)
                : getString(R.string.reading_data);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(progressMessage);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, android.view.KeyEvent event) {

                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    if (progressDialogActionListener != null) {
                        progressDialogActionListener.onStopClicked();
                    }
                    dialog.dismiss();
                    return true;
                } else
                    return false;
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.stop), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (progressDialogActionListener != null) {
                    progressDialogActionListener.onStopClicked();
                }
                dialogInterface.dismiss();
            }
        });

        return dialog;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }

        super.onDestroyView();
    }

    public interface ProgressDialogActionListener {
        void onStopClicked();
    }
}
