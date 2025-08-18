package com.example.kronosprojeto.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kronosprojeto.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

public class Details extends Fragment {

    private static final String TAG = "DetailsFragment";

    public Details() {
    }

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
        Log.d(TAG, "onViewCreated chamado");

        MaterialButton btnUpdate = view.findViewById(R.id.btnUpdate);

        if (btnUpdate == null) {
            Log.e(TAG, "ERRO: btnUpdate não encontrado no layout!");
            Toast.makeText(requireContext(), "Erro: botão Update não encontrado!", Toast.LENGTH_LONG).show();
            return;
        } else {
            Log.d(TAG, "btnUpdate encontrado com sucesso.");
        }

        btnUpdate.setOnClickListener(v -> {
            Log.d(TAG, "Botão Update clicado");

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_details, null);

            if (sheetView == null) {
                Log.e(TAG, "ERRO: Layout bottom_sheet_details não foi inflado!");
                Toast.makeText(requireContext(), "Erro: bottom sheet não carregou!", Toast.LENGTH_LONG).show();
                return;
            } else {
                Log.d(TAG, "Layout bottom_sheet_details inflado com sucesso.");
            }

            MaterialButton btnConfirm = sheetView.findViewById(R.id.btnConfirm);
            if (btnConfirm == null) {
                Log.e(TAG, "ERRO: btnConfirm não encontrado no bottom sheet!");
                Toast.makeText(requireContext(), "Erro: botão Confirm não encontrado!", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "btnConfirm encontrado.");
                btnConfirm.setOnClickListener(view1 -> {
                    Log.d(TAG, "Botão Confirm clicado");
                    Toast.makeText(requireContext(), "Confirmado!", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                });
            }

            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();
            Log.d(TAG, "BottomSheetDialog exibido.");
        });
    }
}
