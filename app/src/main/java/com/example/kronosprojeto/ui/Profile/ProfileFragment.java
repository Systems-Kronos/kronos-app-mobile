package com.example.kronosprojeto.ui.Profile;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
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
import com.example.kronosprojeto.adapter.TaskAdapter;
import com.example.kronosprojeto.config.CloudinaryManager;
import com.example.kronosprojeto.config.RetrofitClientCloudinary;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.databinding.FragmentProfileBinding;
import com.example.kronosprojeto.dto.UploadResultDto;
import com.example.kronosprojeto.dto.UserResponseDto;
import com.example.kronosprojeto.model.Task;
import com.example.kronosprojeto.service.CloudinaryService;
import com.example.kronosprojeto.service.TaskService;
import com.example.kronosprojeto.service.UserService;
import com.example.kronosprojeto.utils.ToastHelper;
import com.example.kronosprojeto.viewmodel.UserViewModel;

import java.io.File;
import java.io.FileOutputStream;
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
    private TextView nameTextView, emailView, sectionView;
    private UserViewModel userViewModel;
    private ImageView pencilImage;
    private CloudinaryService cloudinaryService;
    private UserService userService;
    private View banner;
    RecyclerView recyclerView;
    TaskAdapter adapter;
    TextView concluidasTxt;
    TextView realocadasTxt;
    TextView atribuidasTxt;
    FrameLayout loadingOverlay;
    Activity activity;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Uri cameraImageUri;

    public ProfileFragment() {}

    @Override
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
        CloudinaryManager.init(requireContext());

        ImageView profileImg = binding.profileImg;
        nameTextView = binding.usernameText;
        emailView = binding.emailText;
        sectionView = binding.setorText;
        pencilImage = binding.pencilIcon;
        banner = binding.viewBanner;
        cloudinaryService = RetrofitClientCloudinary.createService(CloudinaryService.class);
        loadingOverlay = binding.loadingOverlay;

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        processarImagemSelecionada(imageUri);
                    }
                });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (cameraImageUri != null) {
                            processarImagemSelecionada(cameraImageUri);
                        }
                    }
                });

        pencilImage.setOnClickListener(v -> openChoseImage());

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                nameTextView.setText(user.getNome());
                emailView.setText(user.getEmail());
                sectionView.setText(user.getSetor().getNome());
                loadingOverlay.setVisibility(View.VISIBLE);

                if (user.getFoto() == null || user.getFoto().isEmpty()) {
                    Glide.with(this)
                            .load(R.drawable.profile_mock)
                            .circleCrop()
                            .into(profileImg);
                    loadingOverlay.setVisibility(View.GONE);
                } else {
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
        adapter = new TaskAdapter(getContext(), tarefas, "profile");
        recyclerView.setAdapter(adapter);

        concluidasTxt = binding.concluidasTxt;
        realocadasTxt = binding.realocadasTxt;
        atribuidasTxt = binding.atribuidasTxt;

        String usuarioIdStr = prefs.getString("id", "0");
        Long usuarioId = Long.parseLong(usuarioIdStr);
        chargeUserTasks(token, usuarioId, "1", "4");

        return root;
    }

    private void openChoseImage() {
        String[] options = {"Escolher da Galeria", "Tirar Foto"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selecionar Imagem")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        imagePickerLauncher.launch(intent);
                    } else {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = new File(requireContext().getCacheDir(), "foto_temp.jpg");
                        cameraImageUri = FileProvider.getUriForFile(
                                requireContext(),
                                requireContext().getPackageName() + ".fileprovider",
                                photoFile
                        );
                        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, cameraImageUri);
                        cameraLauncher.launch(cameraIntent);
                    }
                })
                .show();
    }

    private void processarImagemSelecionada(Uri imageUri) {
        ImageView profileImg = binding.profileImg;

        Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .into(profileImg);

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (originalBitmap == null) {
                ToastHelper.showFeedbackToast(activity, "error", "ERRO:", "Não foi possível processar a imagem");
                return;
            }

            Bitmap rotatedBitmap = corrigirRotacao(requireContext(), imageUri, originalBitmap);
            Palette.from(rotatedBitmap).generate(palette -> {
                int corPredominante = palette.getDominantColor(Color.GRAY);
                banner.setBackgroundColor(corPredominante);
            });

            File finalFile = new File(requireContext().getCacheDir(), "upload_final.jpg");
            try (FileOutputStream out = new FileOutputStream(finalFile)) {
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // qualidade total
            }

            com.cloudinary.android.MediaManager.get()
                    .upload(finalFile.getAbsolutePath())
                    .unsigned("kronos-upload")
                    .callback(new com.cloudinary.android.callback.UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            loadingOverlay.setVisibility(View.VISIBLE);
                            Log.d("Cloudinary", "Iniciando upload...");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            double progress = (double) bytes / totalBytes * 100;
                            Log.d("Cloudinary", "Progresso: " + String.format("%.2f", progress) + "%");
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            loadingOverlay.setVisibility(View.GONE);
                            String imageUrl = resultData.get("secure_url").toString();
                            Log.d("Cloudinary", "✅ Upload completo: " + imageUrl);
                            ToastHelper.showFeedbackToast(activity, "success", "SUCESSO:", "Imagem enviada!");
                            updateUserPhoto(imageUrl);
                        }

                        @Override
                        public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                            loadingOverlay.setVisibility(View.GONE);
                            ToastHelper.showFeedbackToast(activity, "error", "ERRO:", "Falha ao enviar imagem");
                            Log.e("Cloudinary", "Erro: " + error.getDescription());
                        }

                        @Override
                        public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                            loadingOverlay.setVisibility(View.GONE);
                            Log.w("Cloudinary", "Reagendado: " + error.getDescription());
                        }
                    })
                    .dispatch();

        } catch (IOException e) {
            e.printStackTrace();
            ToastHelper.showFeedbackToast(activity, "error", "ERRO:", "Falha ao processar imagem");
        }
    }

    private Bitmap corrigirRotacao(Context context, Uri imageUri, Bitmap bitmap) {
        try {
            InputStream input = context.getContentResolver().openInputStream(imageUri);
            androidx.exifinterface.media.ExifInterface exif = new androidx.exifinterface.media.ExifInterface(input);
            int orientation = exif.getAttributeInt(
                    androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
            );
            input.close();

            Matrix matrix = new Matrix();
            switch (orientation) {
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap;
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private void updateUserPhoto(String imageUrl) {
        UserResponseDto userResponseDto = userViewModel.getUser().getValue();
        if (userResponseDto == null) return;

        userResponseDto.setFoto(imageUrl);

        if (!isAdded()) return;

        Activity activity = getActivity();
        if (activity == null) return;

        SharedPreferences sharedPreferences = activity.getSharedPreferences("app", MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            Log.e("ProfileFragment", "Token JWT não encontrado nas SharedPreferences");
            return;
        }

        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("foto", imageUrl);

        userService = RetrofitClientSQL.createService(UserService.class);

        Call<String> callUpdate = userService.updateUser(
                "Bearer " + token,
                updateFields,
                String.valueOf(userResponseDto.getId())
        );

        callUpdate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("ProfileFragment", "Foto atualizada com sucesso!");

                    // Atualiza o SharedPreferences localmente
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("foto", imageUrl);
                    editor.apply();

                    // Atualiza a imagem na interface
                    if (isAdded() && getView() != null) {
                        ImageView imageView = getView().findViewById(R.id.profile_img);
                        if (imageView != null) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile_mock)
                                    .circleCrop()
                                    .into(imageView);
                        }
                    }

                    ToastHelper.showFeedbackToast(activity, "success", "IMAGEM SALVA:", "Sua foto de perfil foi atualizada");

                } else {
                    Log.e("ProfileFragment", "Erro ao atualizar foto: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("ProfileFragment", "Falha na requisição de atualização de foto", t);
            }
        });
    }

    private void chargeUserTasks(String token, Long usuarioId, String tipoTarefa, String status) {
        TaskService service = RetrofitClientSQL.createService(TaskService.class);
        Call<List<Task>> call = service.getTasksByUserID(usuarioId, "Bearer " + token, tipoTarefa, status);

        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Task> tarefas = response.body();

                    int concluidas = 0;
                    int realocadas = 0;
                    int atribuidas = 0;

                    for (Task tarefa : tarefas) {
                        if ("Concluída".equalsIgnoreCase(tarefa.getStatus()) ||
                                "Concluido".equalsIgnoreCase(tarefa.getStatus())
                        || "Concluído".equalsIgnoreCase(tarefa.getStatus())
                        || "Concluida".equalsIgnoreCase(tarefa.getStatus())){
                            concluidas++;
                        }

                        if ("Realocada".equalsIgnoreCase(tarefa.getOrigemTarefa())) {
                            realocadas++;
                        }

                        if ("Original".equalsIgnoreCase(tarefa.getOrigemTarefa())) {
                            atribuidas++;
                        }
                    }

                    concluidasTxt.setText(String.valueOf(concluidas));
                    realocadasTxt.setText(String.valueOf(realocadas));
                    atribuidasTxt.setText(String.valueOf(atribuidas));

                    adapter.updateList(tarefas);
                } else {
                    ToastHelper.showFeedbackToast(activity, "error", "ERRO:", "Falha ao carregar tarefas");
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                ToastHelper.showFeedbackToast(activity, "error", "ERRO:", "Erro de conexão ao carregar tarefas");
            }
        });
    }
}
