package com.github.sho5nn.tasting.accountmanager;

import android.accounts.Account;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;


public class AllAccountListDialogFragment extends AppCompatDialogFragment {

    public static final String TAG = AllAccountListDialogFragment.class.getSimpleName();
    public static final String ARGS_ACCOUNTS = "accounts";

    public static AllAccountListDialogFragment createInstance(Account[] accounts) {
        Bundle args = new Bundle();
        args.putParcelableArray(ARGS_ACCOUNTS, accounts);

        AllAccountListDialogFragment dialog = new AllAccountListDialogFragment();
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();

        Account[] accounts = (Account[]) getArguments().getParcelableArray(ARGS_ACCOUNTS);

        return new AlertDialog.Builder(context)
                .setCancelable(true)
                .setAdapter(new AllAccountListAdapter(context, accounts), null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }
}
