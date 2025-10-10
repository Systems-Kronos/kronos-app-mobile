package com.example.kronosprojeto.ui.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
    private Calendar selectCalendar;
    private String actionSelect;
    private String idUsuario;

    private BlackBackgroundDecorator blackBackgroundDecorator;

    private Button btnAbscense, btnPresenceSelect;

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

        blackBackgroundDecorator = new BlackBackgroundDecorator(getContext());

        calendarView.setWeekDayFormatter(dayOfWeek -> {
            int index = dayOfWeek.getValue() % 7;
            return DIAS_ABREV[index];
        });

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            blackBackgroundDecorator.setSelectedDay(date);
            widget.invalidateDecorators();

            selectDay = date;

            EditText editTextArea = root.findViewById(R.id.editTextArea);
            editTextArea.setText("");

            LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
            String localDateStr = localDate.toString();

            for (Calendar c : calenderByUser) {
                String saveDate = c.getDay() != null && c.getDay().length() >= 10 ? c.getDay().substring(0, 10) : "";
                if (saveDate.equals(localDateStr)) {
                    if (c.getObservation() != null) {
                        editTextArea.setText(c.getObservation());
                    }
                    break;
                }
            }
        });

        SharedPreferences prefs = getContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        idUsuario = prefs.getString("id", null);

        calenderByUser(idUsuario);

        btnAbscense = root.findViewById(R.id.absenceSelect);
        btnPresenceSelect = root.findViewById(R.id.presenceSelect);
        Button btnSend = root.findViewById(R.id.sendAbsenceButton);

        btnAbscense.setOnClickListener(v -> {
            selectCalendarDay("falta", root);
            btnAbscense.setBackgroundResource(R.drawable.border_yellow);
            btnPresenceSelect.setBackgroundResource(R.drawable.border_normal);
        });

        btnPresenceSelect.setOnClickListener(v -> {
            if (selectDay == null) {
                Toast.makeText(getContext(), "Selecione um dia primeiro", Toast.LENGTH_SHORT).show();
                return;
            }

            LocalDate hoje = LocalDate.now();
            LocalDate diaSelecionado = LocalDate.of(selectDay.getYear(), selectDay.getMonth(), selectDay.getDay());

            if (!diaSelecionado.isEqual(hoje)) {
                Toast.makeText(getContext(), "Só é permitido marcar presença no dia de hoje.", Toast.LENGTH_SHORT).show();
                return;
            }

            selectCalendarDay("presente", root);
            btnPresenceSelect.setBackgroundResource(R.drawable.border_yellow);
            btnAbscense.setBackgroundResource(R.drawable.border_normal);
        });

        btnSend.setOnClickListener(v -> sendUpdate());

        return root;
    }

    private void selectCalendarDay(String acao, View root) {
        if (selectDay == null) {
            Toast.makeText(getContext(), "Você deve primeiro selecionar um dia no calendário", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate localDate = LocalDate.of(selectDay.getYear(), selectDay.getMonth(), selectDay.getDay());
        String localDateStr = localDate.toString();

        Calendar existCalendar = null;
        for (Calendar c : calenderByUser) {
            String saveDate = c.getDay() != null && c.getDay().length() >= 10 ? c.getDay().substring(0, 10) : "";
            if (saveDate.equals(localDateStr)) {
                existCalendar = c;
                break;
            }
        }

        if (existCalendar == null) {
            existCalendar = new Calendar();
            if (idUsuario != null) {
                existCalendar.setUser(Long.parseLong(idUsuario));
            }
            existCalendar.setDay(localDateStr);
        }

        actionSelect = acao;
        selectCalendar = existCalendar;
    }

    private void sendUpdate() {
        if (selectCalendar != null) {
            selectCalendar.setPresence("falta".equals(actionSelect) ? false : true);

            EditText editTextArea = getView().findViewById(R.id.editTextArea);
            String observation = editTextArea.getText().toString();
            selectCalendar.setObservation(observation);

            if (selectCalendar.getId() == null) {
                createCalender(selectCalendar);
            } else {
                updateCalender(selectCalendar.getId(), selectCalendar);
            }
        } else {
            Toast.makeText(getContext(), "Nenhum dado preparado. Clique primeiro em 'Marcar falta' ou 'Marcar presença'.", Toast.LENGTH_SHORT).show();
        }
    }

    private void calenderByUser(String userId) {
        if (userId == null) return;

        CalendarService calendarService = RetrofitCalendarNoSQL.createService(CalendarService.class);

        calendarService.searchUser(userId).enqueue(new Callback<List<Calendar>>() {
            @Override
            public void onResponse(Call<List<Calendar>> call, Response<List<Calendar>> response) {
                if (binding == null || getActivity() == null) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    calenderByUser = response.body();

                    List<CalendarDay> verdes = new ArrayList<>();
                    List<CalendarDay> laranjas = new ArrayList<>();

                    LocalDate hoje = LocalDate.now();

                    for (Calendar event : calenderByUser) {
                        String dateStr = event.getDay() != null && event.getDay().length() >= 10
                                ? event.getDay().substring(0, 10)
                                : null;

                        if (dateStr == null) continue;

                        try {
                            LocalDate date = LocalDate.parse(dateStr);
                            CalendarDay dia = CalendarDay.from(date);

                            Boolean presence = event.getPresence();

                            if (Boolean.FALSE.equals(presence)) {
                                laranjas.add(dia);
                            } else if (Boolean.TRUE.equals(presence)) {
                                verdes.add(dia);
                            } else if (date.isBefore(hoje)){
                                verdes.add(dia);
                            }

                        } catch (Exception e) {
                            Log.e("CALENDAR_DEBUG", "Erro ao converter data: " + dateStr, e);
                        }
                    }

                    binding.calendarView.removeDecorators();
                    binding.calendarView.addDecorator(new GreenBorderDecorator(getContext(), verdes));
                    binding.calendarView.addDecorator(new OrangeBorderDecorator(getContext(), laranjas));

                    if (blackBackgroundDecorator != null) {
                        binding.calendarView.addDecorator(blackBackgroundDecorator);
                    }

                    binding.calendarView.invalidateDecorators();
                } else {
                    Log.e("CALENDAR_DEBUG", "Erro HTTP: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Calendar>> call, Throwable t) {
                Log.e("CALENDAR_DEBUG", "Falha na chamada da API", t);
                Toast.makeText(getContext(), "Erro ao buscar calendários: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void createCalender(Calendar calendar) {
        CalendarService calendarService = RetrofitCalendarNoSQL.createService(CalendarService.class);

        calendarService.insertReport(calendar).enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("teste2", "Entrou aqui");
                    Calendar created = response.body();
                    calenderByUser.add(created);
                    selectCalendar = created;
                    Toast.makeText(getContext(), "Dia salvo com sucesso!", Toast.LENGTH_SHORT).show();

                    calenderByUser(idUsuario);
                }
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Erro ao criar calendário: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCalender(String idCalendario, Calendar calendar) {
        CalendarService calendarService = RetrofitCalendarNoSQL.createService(CalendarService.class);

        calendarService.updateReport(idCalendario, calendar).enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Dia atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    calenderByUser(idUsuario);
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