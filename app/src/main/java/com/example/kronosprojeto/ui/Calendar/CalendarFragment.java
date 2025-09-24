package com.example.kronosprojeto.ui.Calendar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitCalendarNoSQL;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.databinding.FragmentCalendarBinding;
import com.example.kronosprojeto.decorator.BlackBackgroundDecorator;
import com.example.kronosprojeto.decorator.GreenBorderDecorator;
import com.example.kronosprojeto.decorator.OrangeBorderDecorator;
import com.example.kronosprojeto.dto.UserResponseDto;
import com.example.kronosprojeto.model.Calendar;
import com.example.kronosprojeto.service.*;
import com.example.kronosprojeto.viewmodel.UserViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.kronosprojeto.service.CalendarService;

public class CalendarFragment extends Fragment {
    private UserViewModel userViewModel;

    private FragmentCalendarBinding binding;
    private static final String[] DIAS_ABREV = {"D", "S", "T", "Q", "Q", "S", "S"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        MaterialCalendarView calendarView = binding.calendarView;

        calendarView.state().edit()
                .setFirstDayOfWeek(DayOfWeek.SUNDAY)
                .commit();
        BlackBackgroundDecorator blackBackgroundDecorator = new BlackBackgroundDecorator(getContext());


        calendarView.setWeekDayFormatter(new WeekDayFormatter() {
            @Override
            public CharSequence format(DayOfWeek dayOfWeek) {
                // dayOfWeek.getValue() -> 1 (Segunda) a 7 (Domingo)
                int index = dayOfWeek.getValue() % 7; // transforma 7 (Domingo) em 0
                return DIAS_ABREV[index];
            }
        });

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            blackBackgroundDecorator.setSelectedDay(date);

            widget.invalidateDecorators();
        });

        // Aqui eu preencho os dados necessários para instanciar o Calendário quando tentam marcar a falta
        // CAso 1 que seria a questão da falta mesmo


        Button btnAbsence = calendarView.findViewById(R.id.absenceSelect);
        btnAbsence.setOnClickListener(v -> {

        });
        return  root;
    }


    private void calender(long userId) {
        CalendarService calendarService = RetrofitCalendarNoSQL.createService(CalendarService.class);

        calendarService.searchUser(userId).enqueue(new Callback<List<com.example.kronosprojeto.model.Calendar>>() {
            @Override
            public void onResponse(Call<List<com.example.kronosprojeto.model.Calendar>> call,
                                   Response<List<com.example.kronosprojeto.model.Calendar>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.kronosprojeto.model.Calendar> eventos = response.body();

                    List<CalendarDay> verdes = new ArrayList<>();
                    List<CalendarDay> laranjas = new ArrayList<>();

                    for (int i = 0; i < eventos.size(); i++) {
                        com.example.kronosprojeto.model.Calendar event = eventos.get(i);

                        LocalDate date = LocalDate.parse(event.getDay().substring(0, 10));

                        CalendarDay dia = CalendarDay.from(
                                date.getYear(),
                                date.getMonthValue(),
                                date.getDayOfMonth()
                        );

                        if (Boolean.TRUE.equals(event.getPresence())) {
                            verdes.add(dia);
                        } else {
                            laranjas.add(dia);
                        }
                    }

                    binding.calendarView.addDecorator(new GreenBorderDecorator(getContext(), verdes));
                    binding.calendarView.addDecorator(new OrangeBorderDecorator(getContext(), laranjas));
                }


            }

            @Override
            public void onFailure(Call<List<com.example.kronosprojeto.model.Calendar>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Erro tal tal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCalender(String id, Calendar calendar){
        CalendarService calendarService = RetrofitCalendarNoSQL.createService(CalendarService.class);

        calendarService.updateReport(id, calendar).enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Calendar calenderUpdated = response.body();

                    LocalDate date = LocalDate.parse(calenderUpdated.getDay().substring(0, 10));
                    CalendarDay dia = CalendarDay.from(
                            date.getYear(),
                            date.getMonthValue(),
                            date.getDayOfMonth()
                    );

                    if (calenderUpdated.getPresence()) {
                        binding.calendarView.addDecorator(
                                new GreenBorderDecorator(getContext(), List.of(dia))
                        );
                    } else {
                        binding.calendarView.addDecorator(
                                new OrangeBorderDecorator(getContext(), List.of(dia))
                        );
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {

            }
        });
    }

    private void searchUserID() {

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        SharedPreferences prefs = requireContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        String cpf = prefs.getString("cpf", null);
        String token = prefs.getString("token", null);

        UserService userService = RetrofitClientSQL.createService(UserService.class);
        userService.getUserByCPF(token, cpf).enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponseDto usuario = response.body();
                    long userId = usuario.getId();

                    calender(userId);
                } else {
                    Toast.makeText(getContext(), "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                Toast.makeText(getContext(), "Erro ao buscar usuário: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}