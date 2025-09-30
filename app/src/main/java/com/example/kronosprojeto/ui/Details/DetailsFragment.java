package com.example.kronosprojeto.ui.Details;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
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
import com.example.kronosprojeto.config.RetrofitClientCloudinary;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.ReportRequestDto;
import com.example.kronosprojeto.dto.TaskDetailsDto;
import com.example.kronosprojeto.model.Task;
import com.example.kronosprojeto.service.CloudinaryService;
import com.example.kronosprojeto.service.ReportService;
import com.example.kronosprojeto.service.TaskService;
import com.example.kronosprojeto.service.UserService;
import com.example.kronosprojeto.ui.Home.HomeViewModel;
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
    FrameLayout loadingOverlay;
    ConstraintLayout constraintLayout;
    FlexboxLayout step1Layout;
    FlexboxLayout step2Layout;
    String problem;
    String description;
    long idTarefa;
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

        ImageView back = view.findViewById(R.id.imgBack);

        idTarefa = -1; // valor padrão caso não venha nada
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
        loadingOverlay= view.findViewById(R.id.loadingOverlay);
        constraintLayout = view.findViewById(R.id.constraint_layout);

        constraintLayout.setVisibility(View.GONE);
        loadingOverlay.setVisibility(View.VISIBLE);

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
            step1Layout = sheetView.findViewById(R.id.step1);
            step2Layout = sheetView.findViewById(R.id.step2);

            MaterialButton btnConfirmReport = sheetView.findViewById(R.id.btnConfirmReport);
            TextView notUndestandOption, notBelongsToMeOption, notValidInfoOption, otherCauseOption, problemChose;
            TextInputEditText descriptionInput = sheetView.findViewById(R.id.txtDescriptionInput);
            problemChose = sheetView.findViewById(R.id.txtProblemChose);
            notUndestandOption = sheetView.findViewById(R.id.notUnderstandOption);
            notBelongsToMeOption = sheetView.findViewById(R.id.notBelongsToMeOption);
            notValidInfoOption = sheetView.findViewById(R.id.notValidInfoOption);
            otherCauseOption = sheetView.findViewById(R.id.otherCauseOption);

            notUndestandOption.setOnClickListener(v1 -> {
                step1Layout.setVisibility(View.GONE);
                problem = "Não compreendi a tarefa";
                step2Layout.setVisibility(View.VISIBLE);
                problemChose.setText("Problema escolhido: "+problem);
            });
            notBelongsToMeOption.setOnClickListener(v1 -> {
                step1Layout.setVisibility(View.GONE);
                problem = "Essa tarefa não me pertence";
                step2Layout.setVisibility(View.VISIBLE);
                problemChose.setText("Problema escolhido: "+problem);

            });

            notValidInfoOption.setOnClickListener(v1 -> {
                step1Layout.setVisibility(View.GONE);
                problem = "Informações não válidas ou não condizem";
                step2Layout.setVisibility(View.VISIBLE);
                problemChose.setText("Problema escolhido: "+problem);

            });
            otherCauseOption.setOnClickListener(v1 -> {
                step1Layout.setVisibility(View.GONE);
                problem = "Outro Motivo";
                step2Layout.setVisibility(View.VISIBLE);
                problemChose.setText("Problema escolhido: "+problem);

            });
            if (btnConfirmReport != null) {
                btnConfirmReport.setOnClickListener(view1 -> {
                    Log.d(TAG, "Informações não válidas ou não condizem");
                    Log.e("INPUT FIELD", descriptionInput.getText().toString());
                    SharedPreferences prefs = requireContext().getSharedPreferences("app", Context.MODE_PRIVATE);
                    String token = prefs.getString("jwt", null);
                    String idStr = prefs.getString("id", "1");
                    long idUsuario = Long.parseLong(idStr);
                    description =  descriptionInput.getText().toString();
                    ReportService reportService = RetrofitClientSQL.createService(ReportService.class);
                    ReportRequestDto reportRequestDto = new ReportRequestDto(idTarefa,idUsuario,description,problem, "Pendente");
                    Call<ReportRequestDto> call = reportService.insertReport("Bearer "+token,reportRequestDto);

                    call.enqueue(new Callback<ReportRequestDto>() {
                        @Override
                        public void onResponse(Call<ReportRequestDto> call, Response<ReportRequestDto> response) {
                            if (response.isSuccessful()) {

                                ToastHelper.showFeedbackToast(
                                        requireActivity(),
                                        "successo",
                                        "Relatório enviado",
                                        "Seu problema foi registrado com sucesso!"
                                );
                            } else {

                                ToastHelper.showFeedbackToast(
                                        requireActivity(),
                                        "error",
                                        "Erro",
                                        "Falha ao registrar: " + response.code()
                                );
                            }
                        }

                        @Override
                        public void onFailure(Call<ReportRequestDto> call, Throwable t) {
                            // ❌ Erro de rede
                            ToastHelper.showFeedbackToast(
                                    requireActivity(),
                                    "error",
                                    "Erro de conexão",
                                    t.getMessage()
                            );
                        }
                    });







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

        back.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(DetailsFragment.this);
            navController.navigate(R.id.action_details_to_HomeFragment);
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
            Log.e(TAG, "idTarefa inválido ou token nulo");
            Log.e(TAG, "idTarefa inválido ou token nulo: " + idTarefa + ", " + token);



        }    }
}
