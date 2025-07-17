package com.example.kronosprojeto;

import android.os.Bundle;
import android.view.View;

import com.example.kronosprojeto.ui.Home.Tarefa;
import com.example.kronosprojeto.ui.Home.TerefaAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.appcompat.widget.Toolbar;
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        View customView = getLayoutInflater().inflate(R.layout.toolbar_custom, null);
        toolbar.addView(customView);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.HomeFragment, R.id.CalendarioFragment, R.id.PerfilFragment, R.id.ChatFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }

}