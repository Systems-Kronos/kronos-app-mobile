package com.example.kronosprojeto.ui.Details;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kronosprojeto.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

public class Details extends Fragment {

    private static final String TAG = "DetailsFragment";

    public Details() {}

    public static Details newInstance() {
        return new Details();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView chamado");
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnUpdate = view.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(v -> {
            Log.d(TAG, "Botão Update clicado");

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog);
            View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_details, null);

            if (sheetView == null) {
                Log.e(TAG, "ERRO: Layout bottom_sheet_details não foi inflado!");
                return;
            }

            MaterialButton btnConfirm = sheetView.findViewById(R.id.btnConfirm);
            if (btnConfirm != null) {
                btnConfirm.setOnClickListener(view1 -> {
                    Log.d(TAG, "Botão Confirm clicado");
                    bottomSheetDialog.dismiss();
                });
            }

            bottomSheetDialog.setContentView(sheetView);

            View container = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (container != null) {
                container.setBackgroundResource(android.R.color.transparent);
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(container);
                behavior.setSkipCollapsed(true);
                behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            bottomSheetDialog.show();
            Log.d(TAG, "BottomSheetDialog exibido.");
        });

        View btnReport = view.findViewById(R.id.imgReport);
        btnReport.setOnClickListener(v -> {
            Log.d(TAG, "Botão Report clicado");

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog);
            View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_input_report, null);

            if (sheetView == null) {
                Log.e(TAG, "ERRO: Layout bottom_sheet_input_report não foi inflado!");
                return;
            }

            MaterialButton btnConfirmReport = sheetView.findViewById(R.id.btnConfirmReport);
            if (btnConfirmReport != null) {
                btnConfirmReport.setOnClickListener(view1 -> {
                    Log.d(TAG, "Botão Confirm Report clicado");
                    bottomSheetDialog.dismiss();
                });
            }

            bottomSheetDialog.setContentView(sheetView);

            View container = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (container != null) {
                container.setBackgroundResource(android.R.color.transparent);
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(container);
                behavior.setSkipCollapsed(true);
                behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            bottomSheetDialog.show();
        });

        ImageView imgHistorico = view.findViewById(R.id.imgHistorico);
        imgHistorico.setOnClickListener(s -> {
            NavController navController = NavHostFragment.findNavController(Details.this);
            navController.navigate(R.id.action_details_to_assignmentHistoryFragment);
        });
    }
}
