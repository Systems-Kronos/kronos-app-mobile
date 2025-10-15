package com.example.kronosprojeto.ui.Profile;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.kronosprojeto.R;

import com.example.kronosprojeto.config.RetrofitClientCloudinary;
import com.example.kronosprojeto.adapter.TaskAdapter;
import com.example.kronosprojeto.databinding.FragmentProfileBinding;
import com.example.kronosprojeto.model.Task;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.UploadResultDto;
import com.example.kronosprojeto.dto.UserResponseDto;

import com.example.kronosprojeto.service.CloudinaryService;

import com.example.kronosprojeto.service.TaskService;
import com.example.kronosprojeto.service.UserService;
import com.example.kronosprojeto.utils.ToastHelper;
import com.example.kronosprojeto.viewmodel.UserViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView nameTextView, emailView,sectionView;
    private UserViewModel userViewModel;
    private ImageView pencilImage;
    private CloudinaryService cloudinaryService;
    private UserService usuarioService;
    private View banner;
    RecyclerView recyclerView;
    TaskAdapter adapter;
    TextView concluidasTxt;
    TextView realocadasTxt;
    TextView atribuidasTxt;
    FrameLayout loadingOverlay;
    Activity activity;

    private ActivityResultLauncher<Intent> imagePickerLauncher;


    public ProfileFragment() {
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SharedPreferences prefs = getContext().getSharedPreferences("app", MODE_PRIVATE);

        String token = prefs.getString("jwt", null);
        activity = getActivity();

        ImageView profileImg = binding.profileImg;
        nameTextView = binding.usernameText;
        emailView = binding.emailText;
        sectionView = binding.setorText;
        pencilImage = binding.pencilIcon;
        banner = binding.viewBanner;
        cloudinaryService = RetrofitClientCloudinary.createService(CloudinaryService.class);

        loadingOverlay= binding.loadingOverlay;


        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        profileImg.setImageURI(imageUri);
                        Glide.with(this)
                                .load(imageUri)
                                .circleCrop()
                                .into(profileImg);

                        try {
                            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                            byte[] bytes = new byte[inputStream.available()];
                            inputStream.read(bytes);
                            inputStream.close();

                            InputStream inputStreamBitmap = requireContext().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStreamBitmap);
                            inputStreamBitmap.close();

                            if (bitmap != null) {
                                Palette.from(bitmap).generate(palette -> {
                                    int corPredominante = palette.getDominantColor(Color.GRAY);
                                    banner.setBackgroundColor(corPredominante);
                                });
                            }

                            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), bytes);
                            MultipartBody.Part body = MultipartBody.Part.createFormData(
                                    "file",
                                    "imagem.jpg",
                                    requestFile
                            );

                            RequestBody preset = RequestBody.create(MultipartBody.FORM, "kronos-upload");

                            Call<UploadResultDto> call = cloudinaryService.uploadImage("dblwo3rra", body, preset);
                            call.enqueue(new Callback<UploadResultDto>() {
                                @Override
                                public void onResponse(Call<UploadResultDto> call, Response<UploadResultDto> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        String imageUrl = response.body().secure_url;
                                        Log.d("Cloudinary", "URL: " + imageUrl);


                                        UserResponseDto userResponseDto = userViewModel.getUser().getValue();
                                        userResponseDto.setFoto(imageUrl);

                                        Map<String, Object> updateFields = new HashMap<>();
                                        updateFields.put("foto", imageUrl);
                                        usuarioService = RetrofitClientSQL.createService(UserService.class);
                                        Call<String> callUpdate = usuarioService.updateUser(
                                                "Bearer " + token,
                                                updateFields, String.valueOf(userResponseDto.getId())

                                        );

                                        callUpdate.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {


                                                if (response.isSuccessful()) {
                                                    ToastHelper.showFeedbackToast(activity,"sucesso","SUCESSO:","Informações salvas!");

                                                    Log.d("UpdateUsuario", "Usuário atualizado com sucesso!");


                                                } else {
                                                    Log.e("UpdateUsuario", "Erro: " + response.code());
                                                    ToastHelper.showFeedbackToast(activity,"error","ERROR:","Não foi possível concluir a operação");


                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                Log.e("UpdateUsuario", "Falha: " + t.getMessage());
                                                ToastHelper.showFeedbackToast(activity,"error","ERROR:","Não foi possível concluir a operação");

                                            }
                                        });

                                    } else {
                                        Log.e("Cloudinary", "Erro no upload: " + response.errorBody());
                                        ToastHelper.showFeedbackToast(activity,"error","ERROR:","Não foi possível concluir a operação");

                                    }
                                }

                                @Override
                                public void onFailure(Call<UploadResultDto> call, Throwable t) {
                                    Log.e("Cloudinary", "Falhou: " + t.getMessage());
                                    ToastHelper.showFeedbackToast(activity,"error","ERROR:","Não foi possível concluir a operação");

                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                });

        pencilImage.setOnClickListener(v -> abrirGaleria());

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                nameTextView.setText(user.getNome());
                emailView.setText(user.getEmail());
                sectionView.setText(user.getSetor().getNome());
                loadingOverlay.setVisibility(View.VISIBLE);

                if (user.getFoto() == null){
                    Glide.with(this)
                            .load(R.drawable.profile_mock)
                            .circleCrop()
                            .into(profileImg);

                    loadingOverlay.setVisibility(View.GONE);



                }else {
                    Glide.with(this)
                            .load(userViewModel.getUser().getValue().getFoto())
                            .circleCrop()
                            .into(profileImg);

                    Glide.with(this)
                            .asBitmap()
                            .load(userViewModel.getUser().getValue().getFoto())
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource,
                                                            @Nullable Transition<? super Bitmap> transition) {
                                    Palette.from(resource).generate(palette -> {
                                        int corPredominante = palette.getDominantColor(Color.GRAY);
                                        banner.setBackgroundColor(corPredominante);
                                    });
                                    loadingOverlay.setVisibility(View.GONE);

                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {}
                            });
                }
            }
        });

        List<Task> tarefas = new ArrayList<>();



        recyclerView = binding.userTasks;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter(getContext(), tarefas,"profile");

        recyclerView.setAdapter(adapter);

        concluidasTxt = binding.concluidasTxt;
        realocadasTxt = binding.realocadasTxt;
        atribuidasTxt = binding.atribuidasTxt;

        String usuarioIdStr = prefs.getString("id", "0");
        Long usuarioId = Long.parseLong(usuarioIdStr);
        carregarTarefasUsuario(token,usuarioId, "1", "4");
        return root;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void carregarTarefasUsuario(String token, Long usuarioId, String tipoTarefa, String status) {

        TaskService service = RetrofitClientSQL.createService(TaskService.class);
        Call<List<Task>> call = service.getTasksByUserID(usuarioId,"Bearer "+ token, tipoTarefa, status);

        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Task> tarefas = response.body();

                    int concluidas = 0;
                    int realocadas = 0;
                    int atribuidas = 0;

                    Log.d("DEBUG_TASKS", "Quantidade de tarefas recebidas: " + tarefas.size());
                    for (Task tarefa : tarefas) {
                        Log.d("DEBUG_TASKS", "Tarefa: " + tarefa.getNome()
                                + ", Gravidade: " + tarefa.getGravidade()
                                + ", Origem: " + tarefa.getOrigemTarefa()
                                + ", Data Atribuicao: " + tarefa.getDataAtribuicao()
                                + ", Status: " + tarefa.getStatus());
                        if ("Concluída".equalsIgnoreCase(tarefa.getStatus()) ||
                                "Concluida".equalsIgnoreCase(tarefa.getStatus()) ||
                                "Concluído".equalsIgnoreCase(tarefa.getStatus()) ||
                                "Concluido".equalsIgnoreCase(tarefa.getStatus())
                        ) {
                            concluidas++;
                        }

                        if ("Realocada".equalsIgnoreCase(tarefa.getOrigemTarefa())) {
                            realocadas++;
                        }

                        if ("Original".equalsIgnoreCase(tarefa.getOrigemTarefa())) {
                            atribuidas++;
                        }

                        concluidasTxt.setText(String.valueOf(concluidas));
                        realocadasTxt.setText(String.valueOf(realocadas));
                        atribuidasTxt.setText(String.valueOf(atribuidas));


                    }
                    adapter.updateList(tarefas);
                } else {
                    Log.d("DEBUG_TASKS", "Resposta não foi bem sucedida. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e("DEBUG_TASKS", "Erro ao buscar tarefas", t);
                ToastHelper.showFeedbackToast(activity,"error","ERRO:","Não foi possível carregar as tarefas");

            }
        });
    }
}