package com.example.kronosprojeto.ui.Details;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.LogAtribuicaoTarefaDto;
import com.example.kronosprojeto.dto.ReportRequestDto;
import com.example.kronosprojeto.dto.TaskDetailsDto;
import com.example.kronosprojeto.dto.TaskStatusDto;
import com.example.kronosprojeto.service.ReportService;
import com.example.kronosprojeto.service.TaskService;
import com.example.kronosprojeto.utils.ToastHelper;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment {

    private static final String TAG = "DetailsFragment";

    private TaskService taskService;
    private FrameLayout loadingOverlay;
    private ConstraintLayout constraintLayout;
    private FlexboxLayout step1Layout;
    private FlexboxLayout step2Layout;

    private long idTarefa;
    private String problem;
    private String description;
    private String currentStatus; // <-- nova variável para status atual

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView chamado");
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView back = view.findViewById(R.id.imgBack);
        idTarefa = -1;

        if (getArguments() != null) {
            idTarefa = getArguments().getLong("idTarefa", -1);
            Log.d(TAG, "Id da tarefa recebido: " + idTarefa);
        }

        taskService = RetrofitClientSQL.createService(TaskService.class);

        TextView txtTituloTarefa = view.findViewById(R.id.txtTituloTarefa);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        TextView txtInicialDate = view.findViewById(R.id.txtInicialDate);
        TextView txtSituation = view.findViewById(R.id.txtSituation);
        TextView txtSection = view.findViewById(R.id.txtSection);
        TextView txtRapporteur = view.findViewById(R.id.txtRapporteur);
        TextView txtAdditionalContacts = view.findViewById(R.id.txtAdditionalContacts);

        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        constraintLayout = view.findViewById(R.id.constraint_layout);

        constraintLayout.setVisibility(View.GONE);
        loadingOverlay.setVisibility(View.VISIBLE);

        MaterialButton btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(v -> showUpdateBottomSheet());

        View btnReport = view.findViewById(R.id.imgReport);
        btnReport.setOnClickListener(v -> showReportBottomSheet());

        ImageView imgHistorico = view.findViewById(R.id.imgHistorico);
        imgHistorico.setOnClickListener(s -> {
            NavController navController = NavHostFragment.findNavController(DetailsFragment.this);
            navController.navigate(R.id.action_details_to_assignmentHistoryFragment);
        });

        back.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(DetailsFragment.this);
            navController.navigate(R.id.action_details_to_HomeFragment);
        });

        loadTaskDetails(txtTituloTarefa, txtDescription, txtInicialDate, txtSituation, txtSection, txtRapporteur, txtAdditionalContacts);
    }

    private void loadTaskDetails(
            TextView txtTituloTarefa,
            TextView txtDescription,
            TextView txtInicialDate,
            TextView txtSituation,
            TextView txtSection,
            TextView txtRapporteur,
            TextView txtAdditionalContacts
    ) {
        SharedPreferences prefs = requireContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt", null);

        if (idTarefa != -1 && token != null) {
            Call<TaskDetailsDto> call = taskService.getTaskById(idTarefa, "Bearer " + token);
            call.enqueue(new Callback<TaskDetailsDto>() {
                @Override
                public void onResponse(Call<TaskDetailsDto> call, Response<TaskDetailsDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        TaskDetailsDto task = response.body();

                        txtTituloTarefa.setText(task.getNome());
                        txtDescription.setText(task.getDescricao() != null ? task.getDescricao() : "-");
                        txtInicialDate.setText(task.getDataAtribuicao() != null ? task.getDataAtribuicao() : "-");

                        currentStatus = task.getStatus(); // <-- guarda status atual
                        txtSituation.setText(currentStatus != null ? currentStatus : "-");

                        txtRapporteur.setText(task.getUsuarioRelator().getNome() != null ? task.getUsuarioRelator().getNome() : "-");
                        txtAdditionalContacts.setText(task.getUsuarioRelator().getEmail() != null ? task.getUsuarioRelator().getEmail() : "-");
                        txtSection.setText(task.getUsuarioRelator().getSetor().getNome() != null ? task.getUsuarioRelator().getSetor().getNome() : "-");

                        constraintLayout.setVisibility(View.VISIBLE);
                        loadingOverlay.setVisibility(View.GONE);
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
            Log.e(TAG, "idTarefa inválido ou token nulo: " + idTarefa + ", " + token);
        }
    }

    private void showUpdateBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog);
        View sheetViewChoice = getLayoutInflater().inflate(R.layout.bottom_sheet_choice, null);
        bottomSheetDialog.setContentView(sheetViewChoice);
        setBottomSheetBehavior(bottomSheetDialog);

        TextView txtDescriptionDetails = sheetViewChoice.findViewById(R.id.txtDescriptionDetails);
        TextView txtFinishTask = sheetViewChoice.findViewById(R.id.txtFinishTask);

        txtDescriptionDetails.setOnClickListener(v -> {
            if ("Pendente".equals(currentStatus)) {
                View sheetStart = getLayoutInflater().inflate(R.layout.bottom_sheet_start, null);
                MaterialButton txtStartTask = sheetStart.findViewById(R.id.txtStartTask);

                txtStartTask.setOnClickListener(v2 -> {
                    updateTaskStatus("Em Andamento");
                    currentStatus = "Em Andamento"; // Atualiza o status local
                    bottomSheetDialog.dismiss();
                });

                bottomSheetDialog.setContentView(sheetStart);
                setBottomSheetBehavior(bottomSheetDialog);
            } else {
                View sheetDetails = getLayoutInflater().inflate(R.layout.bottom_sheet_details, null);
                MaterialButton btnConfirm = sheetDetails.findViewById(R.id.btnConfirm);
                TextInputEditText txtDescriptionDetails2 = sheetDetails.findViewById(R.id.txtDescriptionDetails);

                btnConfirm.setOnClickListener(v1 -> {
                    String observacao = txtDescriptionDetails2.getText() != null ?
                            txtDescriptionDetails2.getText().toString().trim() : "";

                    if (observacao.isEmpty()) {
                        ToastHelper.showFeedbackToast(requireActivity(), "error", "Campo vazio", "Por favor, descreva o que foi feito no dia.");
                        return;
                    }

                    SharedPreferences prefs = requireContext().getSharedPreferences("app", Context.MODE_PRIVATE);
                    String token = prefs.getString("jwt", null);
                    Long idUsuario = Long.parseLong(prefs.getString("id", "0"));

                    if (token == null || idUsuario == 0 || idTarefa == -1) {
                        ToastHelper.showFeedbackToast(requireActivity(), "error", "Erro", "Não foi possível obter os dados do usuário.");
                        return;
                    }

                    // Data de hoje
                    String dataHoje = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            .format(new java.util.Date());

                    // Cria DTO
                    LogAtribuicaoTarefaDto dto = new LogAtribuicaoTarefaDto(idTarefa, idUsuario, observacao, dataHoje);

                    // Chama a API
                    TaskService logService = RetrofitClientSQL.createService(TaskService.class);
                    logService.adicionarLog("Bearer " + token, dto).enqueue(new retrofit2.Callback<LogAtribuicaoTarefaDto>() {
                        @Override
                        public void onResponse(Call<LogAtribuicaoTarefaDto> call, retrofit2.Response<LogAtribuicaoTarefaDto> response) {
                            if (response.isSuccessful()) {
                                ToastHelper.showFeedbackToast(requireActivity(), "successo", "Log salvo", "Observação registrada com sucesso!");
                            } else {
                                ToastHelper.showFeedbackToast(requireActivity(), "error", "Erro", "Falha ao salvar log: " + response.code());
                            }
                            bottomSheetDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<LogAtribuicaoTarefaDto> call, Throwable t) {
                            ToastHelper.showFeedbackToast(requireActivity(), "error", "Erro de conexão", t.getMessage());
                            bottomSheetDialog.dismiss();
                        }
                    });
                });

                bottomSheetDialog.setContentView(sheetDetails);
                setBottomSheetBehavior(bottomSheetDialog);
            }

        });

        txtFinishTask.setOnClickListener(v -> {
            updateTaskStatus("Concluída");
            currentStatus = "Concluída"; // Atualiza o status local
            bottomSheetDialog.dismiss();
        });
    }

    private void showReportBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_input_report, null);

        if (sheetView == null) return;

        step1Layout = sheetView.findViewById(R.id.step1);
        step2Layout = sheetView.findViewById(R.id.step2);

        MaterialButton btnConfirmReport = sheetView.findViewById(R.id.btnConfirmReport);
        TextView notUndestandOption = sheetView.findViewById(R.id.notUnderstandOption);
        TextView notBelongsToMeOption = sheetView.findViewById(R.id.notBelongsToMeOption);
        TextView notValidInfoOption = sheetView.findViewById(R.id.notValidInfoOption);
        TextView otherCauseOption = sheetView.findViewById(R.id.otherCauseOption);
        TextView problemChose = sheetView.findViewById(R.id.txtProblemChose);
        TextInputEditText descriptionInput = sheetView.findViewById(R.id.txtDescriptionInput);

        notUndestandOption.setOnClickListener(v1 -> showStep2("Não compreendi a tarefa", problemChose));
        notBelongsToMeOption.setOnClickListener(v1 -> showStep2("Essa tarefa não me pertence", problemChose));
        notValidInfoOption.setOnClickListener(v1 -> showStep2("Informações não válidas ou não condizem", problemChose));
        otherCauseOption.setOnClickListener(v1 -> showStep2("Outro Motivo", problemChose));

        btnConfirmReport.setOnClickListener(v1 -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("app", Context.MODE_PRIVATE);
            String token = prefs.getString("jwt", null);
            long idUsuario = Long.parseLong(prefs.getString("id", "1"));
            description = descriptionInput.getText().toString();

            ReportService reportService = RetrofitClientSQL.createService(ReportService.class);
            ReportRequestDto reportRequestDto = new ReportRequestDto(idTarefa, idUsuario, description, problem, "Pendente");

            reportService.insertReport("Bearer " + token, reportRequestDto).enqueue(new Callback<ReportRequestDto>() {
                @Override
                public void onResponse(Call<ReportRequestDto> call, Response<ReportRequestDto> response) {
                    if (response.isSuccessful()) {
                        ToastHelper.showFeedbackToast(requireActivity(), "successo", "Relatório enviado", "Seu problema foi registrado com sucesso!");
                    } else {
                        ToastHelper.showFeedbackToast(requireActivity(), "error", "Erro", "Falha ao registrar: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ReportRequestDto> call, Throwable t) {
                    ToastHelper.showFeedbackToast(requireActivity(), "error", "Erro de conexão", t.getMessage());
                }
            });

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(sheetView);
        setBottomSheetBehavior(bottomSheetDialog);
    }

    private void showStep2(String selectedProblem, TextView problemChose) {
        problem = selectedProblem;
        step1Layout.setVisibility(View.GONE);
        step2Layout.setVisibility(View.VISIBLE);
        problemChose.setText("Problema escolhido: " + problem);
    }

    private void updateTaskStatus(String newStatus) {
        SharedPreferences prefs = requireContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt", null);

        if (token == null) return;

        TaskStatusDto dto = new TaskStatusDto(idTarefa, newStatus);
        taskService.updateTask(dto, "Bearer " + token).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    ToastHelper.showFeedbackToast(requireActivity(), "successo", "Tarefa atualizada", "Status atualizado para: " + newStatus);
                } else {
                    ToastHelper.showFeedbackToast(requireActivity(), "error", "Erro", "Falha ao atualizar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                ToastHelper.showFeedbackToast(requireActivity(), "error", "Erro de conexão", t.getMessage());
            }
        });
    }

    private void setBottomSheetBehavior(BottomSheetDialog dialog) {
        View container = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        if (container != null) {
            container.setBackgroundResource(android.R.color.transparent);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(container);
            behavior.setSkipCollapsed(true);
            behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        dialog.show();
    }
}
