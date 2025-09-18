package com.example.kronosprojeto.ui.Calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.kronosprojeto.config.RetrofitCalenderNoSQL;
import com.example.kronosprojeto.databinding.FragmentCalendarBinding;
import com.example.kronosprojeto.decorator.BlackBackgroundDecorator;
import com.example.kronosprojeto.decorator.GrayBorderDecorator;
import com.example.kronosprojeto.decorator.GreenBorderDecorator;
import com.example.kronosprojeto.decorator.OrangeBorderDecorator;
import com.example.kronosprojeto.service.CalenderService;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends Fragment {

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
        // Cria uma única instância do decorator de seleção preta
        BlackBackgroundDecorator blackBackgroundDecorator = new BlackBackgroundDecorator(getContext());

        // Aqui que escolhemos o dia que vai ficar marcado
        // Datas para bordas
        List<CalendarDay> bordaLaranja = Arrays.asList(
                CalendarDay.from(2025, 8, 15),
                CalendarDay.from(2025, 8, 18)
        );

        List<CalendarDay> bordaVerde = Arrays.asList(
                CalendarDay.from(2025, 8, 20),
                CalendarDay.from(2025, 8, 22)
        );


        // Adiciona todos os decoradores no início
        calendarView.addDecorator(new OrangeBorderDecorator(getContext(), bordaLaranja));
        calendarView.addDecorator(new GreenBorderDecorator(getContext(), bordaVerde));
        calendarView.addDecorator(new GrayBorderDecorator(getContext()));
        calendarView.addDecorator(blackBackgroundDecorator);

        calendarView.setWeekDayFormatter(new WeekDayFormatter() {
            @Override
            public CharSequence format(DayOfWeek dayOfWeek) {
                // dayOfWeek.getValue() -> 1 (Segunda) a 7 (Domingo)
                int index = dayOfWeek.getValue() % 7; // transforma 7 (Domingo) em 0
                return DIAS_ABREV[index];
            }
        });
        // Ao clicar, atualiza apenas o dia selecionado
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            blackBackgroundDecorator.setSelectedDay(date);
            widget.invalidateDecorators();
        });

        return root;
    }

    private void calender() {
        CalenderService calendarService = RetrofitCalenderNoSQL.createService(CalenderService.class);

        calendarService.buscarPorUsuario(10).enqueue(new Callback<List<com.example.kronosprojeto.model.Calendar>>() {
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

                        if (Boolean.TRUE.equals(event.getPresenca())) {
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
                Toast.makeText(getContext(), "Erro ao buscar eventos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
