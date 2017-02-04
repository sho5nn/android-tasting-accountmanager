package com.github.sho5nn.tasting.accountmanager;

import android.accounts.Account;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


public class AllAccountListAdapter extends ArrayAdapter<Account> {

    private LayoutInflater inflater;

    public AllAccountListAdapter(Context context, Account[] allAccounts) {
        super(context, 0, allAccounts);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        AccountListAdapterBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.list_account, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        binding.setAccount(getItem(position));

        return binding.getRoot();
    }
}
