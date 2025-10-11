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
import com.example.kronosprojeto.config.RetrofitClientNoSQL;
import com.example.kronosprojeto.databinding.FragmentCalendarBinding;
import com.example.kronosprojeto.decorator.BlackBackgroundDecorator;
import com.example.kronosprojeto.decorator.GrayBorderDecorator;
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
    List<CalendarDay> greenVisual;
    List<CalendarDay> greenFromDb;
    GreenBorderDecorator greenDecorator;
    private BlackBackgroundDecorator blackBackgroundDecorator;
    private Button btnAbscense, btnPresenceSelect;
    List<CalendarDay> orange;
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

            if (isWeekend(localDate)) {
                if (isAdded()) Toast.makeText(requireContext(), "Final de semana não pode ser selecionado", Toast.LENGTH_SHORT).show();
                return;
            }

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
            if (selectDay == null) {
                if (isAdded()) Toast.makeText(requireContext(), "Selecione um dia primeiro", Toast.LENGTH_SHORT).show();
                return;
            }

            LocalDate selectedDay = LocalDate.of(selectDay.getYear(), selectDay.getMonth(), selectDay.getDay());

            if (isWeekend(selectedDay)) {
                if (isAdded()) Toast.makeText(requireContext(), "Não é possível marcar falta no final de semana", Toast.LENGTH_SHORT).show();
                return;
            }
            LocalDate today = LocalDate.now();

            if (selectedDay.isBefore(today)) {
                if (isAdded()) Toast.makeText(requireContext(), "Só é permitido marcar uma falta hoje ou programada para o futuro", Toast.LENGTH_SHORT).show();
                return;
            }

            selectCalendarDay("falta", root);
            btnAbscense.setBackgroundResource(R.drawable.border_yellow);
            btnPresenceSelect.setBackgroundResource(R.drawable.border_normal);
        });
        calendarView.setOnMonthChangedListener((widget, date) -> {
            updateGreenDaysForMonth(date);
        });

        btnPresenceSelect.setOnClickListener(v -> {
            if (selectDay == null) {
                if (isAdded()) Toast.makeText(requireContext(), "Selecione um dia primeiro", Toast.LENGTH_SHORT).show();
                return;
            }

            LocalDate today = LocalDate.now();
            LocalDate selectedDay = LocalDate.of(selectDay.getYear(), selectDay.getMonth(), selectDay.getDay());

            if (!selectedDay.isEqual(today)) {
                if (isAdded()) Toast.makeText(requireContext(), "Só é permitido marcar presença no dia de hoje.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isWeekend(selectedDay)) {
                if (isAdded()) Toast.makeText(requireContext(), "Não é possível marcar presença no final de semana", Toast.LENGTH_SHORT).show();
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
            if (isAdded()) Toast.makeText(requireContext(), "Você deve primeiro selecionar um dia no calendário", Toast.LENGTH_SHORT).show();
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

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private void sendUpdate() {
        // Hideko caso tenha dúvida esse isAdded serve meio que pra dizer
        // “Mostre o Toast somente se o fragment ainda estiver visível e associado a uma Activity" pra não dar mais aquele crash

        if (selectDay == null) {
            if (isAdded()) Toast.makeText(requireContext(), "Selecione um dia primeiro.", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate selectedDay = LocalDate.of(selectDay.getYear(), selectDay.getMonth(), selectDay.getDay());
        if (isWeekend(selectedDay)) {
            if (isAdded()) Toast.makeText(requireContext(), "Não é possível salvar marcações em finais de semana.", Toast.LENGTH_SHORT).show();
            return;
        }

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
            if (isAdded()) Toast.makeText(requireContext(), "Nenhum dado preparado. Clique primeiro em 'Marcar falta' ou 'Marcar presença'.", Toast.LENGTH_SHORT).show();
        }
    }

    private void calenderByUser(String userId) {
        if (userId == null) return;

        CalendarService calendarService = RetrofitClientNoSQL.createService(CalendarService.class);

        calendarService.searchUser(userId).enqueue(new Callback<List<Calendar>>() {
            @Override
            public void onResponse(Call<List<Calendar>> call, Response<List<Calendar>> response) {
                if (!isAdded()) {
                    Log.e("CALENDAR_DEBUG", "Fragment fechado");
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    calenderByUser = response.body();

                    greenFromDb = new ArrayList<>();
                    orange = new ArrayList<>();
                    greenVisual = new ArrayList<>();

                    LocalDate today = LocalDate.now();

                    for (Calendar event : calenderByUser) {
                        String dateStr = event.getDay() != null && event.getDay().length() >= 10
                                ? event.getDay().substring(0, 10)
                                : null;

                        if (dateStr == null) continue;

                        try {
                            LocalDate date = LocalDate.parse(dateStr);
                            CalendarDay day = CalendarDay.from(date);
                            Boolean presence = event.getPresence();

                            if (Boolean.FALSE.equals(presence)) {
                                orange.add(day);
                            } else if (Boolean.TRUE.equals(presence)) {
                                greenFromDb.add(day);
                            }

                        } catch (Exception e) {
                            Log.e("CALENDAR_DEBUG", "Erro ao converter data: " + dateStr, e);
                        }
                    }

                    LocalDate firstDayOfMonth = today.withDayOfMonth(1);
                    LocalDate cursor = firstDayOfMonth;
                    while (cursor.isBefore(today)) {
                        CalendarDay day = CalendarDay.from(cursor);
                        if (!orange.contains(day) && !greenFromDb.contains(day)) {
                            greenVisual.add(day);
                        }
                        cursor = cursor.plusDays(1);
                    }

                    List<CalendarDay> greenAll = new ArrayList<>();
                    greenAll.addAll(greenFromDb);
                    greenAll.addAll(greenVisual);

                    binding.calendarView.removeDecorators();
                    if (getContext() != null) {
                        binding.calendarView.addDecorator(new GrayBorderDecorator(getContext()));
                        binding.calendarView.addDecorator(new OrangeBorderDecorator(getContext(), orange));
                        binding.calendarView.addDecorator(new GreenBorderDecorator(getContext(), greenAll));
                        if (blackBackgroundDecorator != null) binding.calendarView.addDecorator(blackBackgroundDecorator);
                    }

                    binding.calendarView.invalidateDecorators();

                } else {
                    Log.e("CALENDAR_DEBUG", "Erro HTtP: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Calendar>> call, Throwable t) {
                Log.e("CALENDAR_DEBUG", "Falha na chamada da API", t);
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Erro ao buscar calendários: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createCalender(Calendar calendar) {
        CalendarService calendarService = RetrofitClientNoSQL.createService(CalendarService.class);

        calendarService.insertReport(calendar).enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    Calendar created = response.body();
                    calenderByUser.add(created);
                    selectCalendar = created;
                    if (isAdded()) Toast.makeText(requireContext(), "Dia salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    calenderByUser(idUsuario);
                }
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {
                t.printStackTrace();
                if (isAdded()) Toast.makeText(requireContext(), "Erro ao criar calendário: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCalender(String idCalendario, Calendar calendar) {
        CalendarService calendarService = RetrofitClientNoSQL.createService(CalendarService.class);

        calendarService.updateReport(idCalendario, calendar).enqueue(new Callback<Calendar>() {
            @Override
            public void onResponse(Call<Calendar> call, Response<Calendar> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    if (isAdded()) Toast.makeText(requireContext(), "Dia atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    calenderByUser(idUsuario);
                }
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {
                t.printStackTrace();
                if (isAdded()) Toast.makeText(requireContext(), "Erro na atualização: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void updateGreenDaysForMonth(CalendarDay month) {
        List<CalendarDay> greenVisual = new ArrayList<>();

        LocalDate firstDayOfMonth = LocalDate.of(month.getYear(), month.getMonth(), 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        LocalDate today = LocalDate.now();
        LocalDate cursor = firstDayOfMonth;

        while (!cursor.isAfter(lastDayOfMonth)) {
            // Só considera dias antes de hoje
            if (!cursor.isBefore(today)) {
                cursor = cursor.plusDays(1);
                continue;
            }

            // Ignora finais de semana
            DayOfWeek dayOfWeek = cursor.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                cursor = cursor.plusDays(1);
                continue;
            }

            CalendarDay day = CalendarDay.from(cursor);

            if (!orange.contains(day) && !greenFromDb.contains(day)) {
                greenVisual.add(day);
            }

            cursor = cursor.plusDays(1);
        }

        // Atualiza decorator
        if (greenDecorator != null) binding.calendarView.removeDecorator(greenDecorator);
        greenDecorator = new GreenBorderDecorator(getContext(), greenVisual);
        binding.calendarView.addDecorator(greenDecorator);
        binding.calendarView.invalidateDecorators();
    }

}
