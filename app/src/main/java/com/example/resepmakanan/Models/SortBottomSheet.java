package com.example.resepmakanan.Models;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.resepmakanan.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SortBottomSheet extends BottomSheetDialogFragment {

    private OnSortSelected listener;
    private String currentSort = "az";

    public void setListener(OnSortSelected listener, String currentSort) {
        this.listener = listener;
        this.currentSort = currentSort;
    }

    public interface OnSortSelected {
        void onSortChosen(String sortType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saved) {
        View v = inflater.inflate(R.layout.bottom_sheet_sort, parent, false);

        RadioGroup rg = v.findViewById(R.id.rgSort);
        TextView tvClear = v.findViewById(R.id.tvClear);

        if (currentSort.equals("newest")) rg.check(R.id.rbNewest);
        else if (currentSort.equals("oldest")) rg.check(R.id.rbOldest);
        else rg.check(R.id.rbAz);

        tvClear.setOnClickListener(i -> {
            rg.clearCheck();
            listener.onSortChosen("az");
            dismiss();
        });

        v.findViewById(R.id.btnApplySort).setOnClickListener(i -> {
            int id = rg.getCheckedRadioButtonId();
            String sort = "az";

            if (id == R.id.rbNewest) sort = "newest";
            else if (id == R.id.rbOldest) sort = "oldest";

            listener.onSortChosen(sort);
            dismiss();
        });

        return v;
    }
}
