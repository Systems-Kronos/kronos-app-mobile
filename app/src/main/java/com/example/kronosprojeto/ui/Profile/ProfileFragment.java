package com.example.kronosprojeto.ui.Profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kronosprojeto.R;
import com.example.kronosprojeto.adapter.TaskAdapter;
import com.example.kronosprojeto.databinding.FragmentProfileBinding;
import com.example.kronosprojeto.model.Task;

import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.databinding.FragmentNotificationsBinding;
import com.example.kronosprojeto.databinding.FragmentProfileBinding;
import com.example.kronosprojeto.dto.UserResponseDto;
import com.example.kronosprojeto.service.UserService;
import com.example.kronosprojeto.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView nameTextView, emailView,sectionView;
    private UserViewModel userViewModel;




    public ProfileFragment() {
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView profileImg = binding.profileImg;
        nameTextView = binding.usernameText;
        emailView = binding.emailText;
        sectionView = binding.setorText;



        Glide.with(this)
                .load(R.drawable.profile_mock)
                .circleCrop()
                .into(profileImg);


        List<Task> tarefas = new ArrayList<>();
        tarefas.add(new Task("Matar boi", new Date(), 3, "Matadouro", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Frigorífico", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // observa mudanças no LiveData
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                nameTextView.setText(user.getNome());
                emailView.setText(user.getEmail());
                sectionView.setText(user.getSetor().getNome());
            }
        });



        RecyclerView recyclerView = binding.userTasks;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TaskAdapter(getContext(), tarefas));

        return root;
    }
}