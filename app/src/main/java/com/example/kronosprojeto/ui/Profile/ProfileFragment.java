package com.example.kronosprojeto.ui.Profile;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

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
import com.example.kronosprojeto.databinding.FragmentProfileBinding;
import com.example.kronosprojeto.dto.UploadResultDto;
import com.example.kronosprojeto.dto.UserResponseDto;

import com.example.kronosprojeto.service.AuthService;
import com.example.kronosprojeto.service.CloudinaryService;

import com.example.kronosprojeto.service.UserService;
import com.example.kronosprojeto.viewmodel.UserViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        ImageView profileImg = binding.profileImg;
        nameTextView = binding.usernameText;
        emailView = binding.emailText;
        sectionView = binding.setorText;
        pencilImage = binding.pencilIcon;
        banner = binding.viewBanner;
        cloudinaryService = RetrofitClientCloudinary.createService(CloudinaryService.class);



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

                                        usuarioService = RetrofitClientSQL.createService(UserService.class);
                                        Call<String> callUpdate = usuarioService.updateUser(
                                                "Bearer " + token,
                                                userResponseDto
                                        );

                                        callUpdate.enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (response.isSuccessful()) {
                                                    Log.d("UpdateUsuario", "Usuário atualizado com sucesso!");
                                                } else {
                                                    Log.e("UpdateUsuario", "Erro: " + response.code());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<String> call, Throwable t) {
                                                Log.e("UpdateUsuario", "Falha: " + t.getMessage());
                                            }
                                        });

                                    } else {
                                        Log.e("Cloudinary", "Erro no upload: " + response.errorBody());
                                    }
                                }

                                @Override
                                public void onFailure(Call<UploadResultDto> call, Throwable t) {
                                    Log.e("Cloudinary", "Falhou: " + t.getMessage());
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

                if (user.getFoto() == null){
                    Glide.with(this)
                            .load(R.drawable.profile_mock)
                            .circleCrop()
                            .into(profileImg);


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
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {}
                            });
                }
            }
        });

        List<Task> tarefas = new ArrayList<>();
        tarefas.add(new Task("Matar boi", new Date(), 3, "Matadouro", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Frigorífico", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));


        RecyclerView recyclerView = binding.userTasks;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TaskAdapter(getContext(), tarefas));

        return root;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
}
