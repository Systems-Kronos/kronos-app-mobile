package com.example.kronosprojeto.ui.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitCalendarNoSQL;
import com.example.kronosprojeto.databinding.FragmentCalendarBinding;
import com.example.kronosprojeto.decorator.BlackBackgroundDecorator;
import com.example.kronosprojeto.decorator.GreenBorderDecorator;
import com.example.kronosprojeto.decorator.OrangeBorderDecorator;
import com.example.kronosprojeto.model.Calendar;
import com.example.kronosprojeto.service.CalendarService;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;

    private List<Calendar> calenderByUser = new ArrayList<>();

    private CalendarDay selectDay;

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
                int index = dayOfWeek.getValue() % 7; // transforma 7 (domingo) em 0
                return DIAS_ABREV[index];
            }
        });

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            blackBackgroundDecorator.setSelectedDay(date);
            widget.invalidateDecorators();
            selectDay = date;
        });

        SharedPreferences prefs = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        String idUsuario = prefs.getString("id", null);

        calender(idUsuario);

        Button btnAbsence = root.findViewById(R.id.absenceSelect);
        EditText editTextArea = root.findViewById(R.id.editTextArea);

        btnAbsence.setOnClickListener(v -> {

            String observation = editTextArea.getText().toString();

            LocalDate localDate = LocalDate.of(
                    selectDay.getYear(),
                    selectDay.getMonth(),
                    selectDay.getDay()
            );

            Calendar calendarioExistente = null;
            for (Calendar c : calenderByUser) {
                if (c.getDay().startsWith(localDate.toString())) {
                    calendarioExistente = c;
                    break;
                }
            }

            calendarioExistente.setPresence(false);
            calendarioExistente.setObservation(observation);

            updateCalender(calendarioExistente.getId(), calendarioExistente);
        });


        return root;
    }

    private void calender(String userId) {
        CalendarService calendarService = RetrofitCalendarNoSQL.createService(CalendarService.class);

        calendarService.searchUser(userId).enqueue(new Callback<List<Calendar>>() {
            @Override
            public void onResponse(Call<List<Calendar>> call,
                                   Response<List<Calendar>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    calenderByUser = response.body();

                    List<CalendarDay> verdes = new ArrayList<>();
                    List<CalendarDay> laranjas = new ArrayList<>();

                    for (Calendar event : calenderByUser) {
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
            public void onFailure(Call<List<Calendar>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Erro ao buscar calendários: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCalender(String idCalendario, Calendar calendar) {
        CalendarService calendarService = RetrofitCalendarNoSQL.createService(CalendarService.class);

        calendarService.updateReport(idCalendario, calendar).enqueue(new Callback<Calendar>() {
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

                }
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Erro na atualização: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
