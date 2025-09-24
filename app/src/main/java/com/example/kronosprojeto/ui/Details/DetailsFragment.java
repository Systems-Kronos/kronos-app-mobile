package com.example.kronosprojeto.ui.Details;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientCloudinary;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.TaskDetailsDto;
import com.example.kronosprojeto.model.Task;
import com.example.kronosprojeto.service.CloudinaryService;
import com.example.kronosprojeto.service.TaskService;
import com.example.kronosprojeto.service.UserService;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";
    private TaskService taskService;

    public DetailsFragment() {}

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
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


        long idTarefa = -1; // valor padrão caso não venha nada
        if (getArguments() != null) {
            idTarefa = getArguments().getLong("idTarefa", -1); // mesmo nome que o adapter
            Log.d("DetailsFragment", "Id da tarefa recebido: " + idTarefa);
        }
        taskService = RetrofitClientSQL.createService(TaskService.class);

        TextView txtTituloTarefa = view.findViewById(R.id.txtTituloTarefa);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        TextView txtInicialDate = view.findViewById(R.id.txtInicialDate);
        TextView txtSituation = view.findViewById(R.id.txtSituation);
        TextView txtSection = view.findViewById(R.id.txtSection);
        TextView txtRapporteur = view.findViewById(R.id.txtRapporteur);
        TextView txtAdditionalContacts = view.findViewById(R.id.txtAdditionalContacts);


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
            NavController navController = NavHostFragment.findNavController(DetailsFragment.this);
            navController.navigate(R.id.action_details_to_assignmentHistoryFragment);
        });
        SharedPreferences prefs = requireContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt", null);



        if (idTarefa != -1 && token != null) {
            Call<TaskDetailsDto> call = taskService.getTaskById(idTarefa, "Bearer " + token);
            call.enqueue(new Callback<TaskDetailsDto>() {
                @Override
                public void onResponse(Call<TaskDetailsDto> call, Response<TaskDetailsDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        TaskDetailsDto task = response.body();
                        Log.d(TAG, "Tarefa recebida: " + task.getNome());
                        TaskDetailsDto tarefa = response.body();
                        txtTituloTarefa.setText(tarefa.getNome());
                        txtDescription.setText(tarefa.getDescricao() != null ? tarefa.getDescricao() : "-");
                        txtInicialDate.setText(tarefa.getDataAtribuicao() != null ? tarefa.getDataAtribuicao() : "-");
                        txtSituation.setText(tarefa.getStatus() != null ? tarefa.getStatus() : "-");
                        txtRapporteur.setText(tarefa.getUsuarioRelator().getNome() != null ? tarefa.getUsuarioRelator().getNome()  : "-");
                        txtAdditionalContacts.setText(tarefa.getUsuarioRelator().getEmail() != null ? tarefa.getUsuarioRelator().getEmail()  : "-");
                        txtSection.setText(tarefa.getUsuarioRelator().getSetor().getNome() != null ? tarefa.getUsuarioRelator().getSetor().getNome()  : "-");




                    } else {
                        Log.e(TAG, "Erro na resposta: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<TaskDetailsDto> call, Throwable t) {
                    Log.e(TAG, "Falha ao buscar tarefa", t);
                }
            });
        } else {
            Log.e(TAG, "idTarefa inválido ou token nulo");
            Log.e(TAG, "idTarefa inválido ou token nulo: " + idTarefa + ", " + token);



        }    }
}
