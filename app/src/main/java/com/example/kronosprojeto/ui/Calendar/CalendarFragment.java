package com.example.kronosprojeto.ui.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientCloudinary;
import com.example.kronosprojeto.config.RetrofitClientNoSQL;
import com.example.kronosprojeto.databinding.FragmentCalendarBinding;
import com.example.kronosprojeto.decorator.BlackBackgroundDecorator;
import com.example.kronosprojeto.decorator.GrayBorderDecorator;
import com.example.kronosprojeto.decorator.GreenBorderDecorator;
import com.example.kronosprojeto.decorator.OrangeBorderDecorator;
import com.example.kronosprojeto.dto.UploadResultDto;
import com.example.kronosprojeto.model.Calendar;
import com.example.kronosprojeto.service.CalendarService;
import com.example.kronosprojeto.service.CloudinaryService;
import com.example.kronosprojeto.utils.ToastHelper;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    Activity activity;
    private String uploadedImageUrl;
    private List<Calendar> calenderByUser = new ArrayList<>();
    TextView selectedDayTxt, selectedDayQuestionTxt;
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

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        uploadImageToCloudinary(imageUri);
                    }
                }
            });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        activity = getActivity();

        MaterialCalendarView calendarView = binding.calendarView;

        calendarView.state().edit()
                .setFirstDayOfWeek(DayOfWeek.SUNDAY)
                .commit();

        blackBackgroundDecorator = new BlackBackgroundDecorator(getContext());

        calendarView.setWeekDayFormatter(dayOfWeek -> {
            int index = dayOfWeek.getValue() % 7;
            return DIAS_ABREV[index];
        });

        ImageView infoImage = binding.infoBorders;
        infoImage.setOnClickListener(v -> {
            View legendaView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_legenda, null);

            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setView(legendaView)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            dialog.show();

            if (dialog.getWindow() != null) {
                Window window = dialog.getWindow();

                // Define a posição
                window.setGravity(Gravity.TOP | Gravity.END);

                // Define margens da borda superior e direita
                WindowManager.LayoutParams params = window.getAttributes();
                params.y = 100; // distância do topo
                params.x = 50;  // distância da direita
                window.setAttributes(params);

                // 💡 Define o tamanho máximo da janela
                // Usando  wrap_content para não ocupar a tela inteira
                window.setLayout(
                        (int) (getResources().getDisplayMetrics().widthPixels * 0.45),
                        WindowManager.LayoutParams.WRAP_CONTENT
                );
            }
        });

        selectedDayQuestionTxt = binding.selectedDayQuestionTxt;
        selectedDayTxt = binding.selectedDayTxt;
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            blackBackgroundDecorator.setSelectedDay(date);
            widget.invalidateDecorators();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR"));
            String dataFormatada = date.getDate().format(formatter);

            selectDay = date;

            if (date.getDate().isEqual( LocalDate.now())){
                selectedDayTxt.setText("Hoje");
                selectedDayQuestionTxt.setText("Você estará presente?");


            }else{
                selectedDayTxt.setText("Dia: "+dataFormatada);
                selectedDayQuestionTxt.setText("Você estará ausente?");
            }



            EditText editTextArea = root.findViewById(R.id.editTextArea);
            editTextArea.setText("");

            LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
            String localDateStr = localDate.toString();

            if (isWeekend(localDate)) {
                if (isAdded())  ToastHelper.showFeedbackToast(activity,"info","Selecione um dia válido:","Finais de semana não podem ser selecionados");

                return;
            }

            Calendar calendarDay = null;
            for (Calendar c : calenderByUser) {
                String saveDate = c.getDay() != null && c.getDay().length() >= 10 ? c.getDay().substring(0, 10) : "";
                if (saveDate.equals(localDateStr)) {
                    calendarDay = c;
                    if (c.getObservation() != null) {
                        editTextArea.setText(c.getObservation());
                    }
                    break;
                }
            }

            if (calendarDay != null && calendarDay.getAttest() != null && !calendarDay.getAttest().isEmpty()) {
                binding.imageAtestado.setVisibility(View.VISIBLE);
                Log.d("CALENDAR_DEBUG", "URL do atestado para " + localDateStr + ": " + calendarDay.getAttest());
                String url = calendarDay.getAttest();
                if (url != null && url.startsWith("http://")) {
                    url = url.replace("http://", "https://");
                }
                Glide.with(requireContext())
                        .load(url)
                        .into(binding.imageAtestado);

                uploadedImageUrl = calendarDay.getAttest();
            } else {
                binding.imageAtestado.setVisibility(View.GONE);
                binding.imageAtestado.setImageDrawable(null);
                uploadedImageUrl = null;
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
                if (isAdded()) ToastHelper.showFeedbackToast(activity,"info","Selecione um dia válido:","Você precisa selecionar um dia primeiro");
                return;
            }

            LocalDate selectedDay = LocalDate.of(selectDay.getYear(), selectDay.getMonth(), selectDay.getDay());

            if (isWeekend(selectedDay)) {
                if (isAdded()) ToastHelper.showFeedbackToast(activity,"info","Selecione um dia válido:","Você não pode marcar falta em finais de semana");
                return;
            }
            LocalDate today = LocalDate.now();

            if (selectedDay.isBefore(today)) {
                if (isAdded())  ToastHelper.showFeedbackToast(activity,"info","Ação não permitida:","Você só pode marcar uma falta no dia atual ou agenda-la para dias futuros");
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
                if (isAdded()) ToastHelper.showFeedbackToast(activity,"info","Selecione um dia válido:","Você precisa selecionar um dia primeiro");
                return;
            }

            LocalDate today = LocalDate.now();
            LocalDate selectedDay = LocalDate.of(selectDay.getYear(), selectDay.getMonth(), selectDay.getDay());

            if (!selectedDay.isEqual(today)) {
                if (isAdded()) ToastHelper.showFeedbackToast(activity,"info","Selecione um dia válido:","Você só pode registrar presença no dia atual");
                return;
            }

            if (isWeekend(selectedDay)) {
                if (isAdded()) ToastHelper.showFeedbackToast(activity,"info","Selecione um dia válido:","Você não pode marcar falta em finais de semana");
                return;
            }

            selectCalendarDay("presente", root);
            btnPresenceSelect.setBackgroundResource(R.drawable.border_yellow);
            btnAbscense.setBackgroundResource(R.drawable.border_normal);
        });

        btnSend.setOnClickListener(v -> sendUpdate());
        TextView txtAnexo = root.findViewById(R.id.txtAnexo);
        txtAnexo.setOnClickListener(v -> abrirGaleria());

        return root;
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), bytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "atestado.jpg", requestFile);

            RequestBody preset = RequestBody.create(MultipartBody.FORM, "kronos-upload");

            CloudinaryService service = RetrofitClientCloudinary.createService(CloudinaryService.class);

            Call<UploadResultDto> call = service.uploadImage("dblwo3rra", body, preset);

            Toast.makeText(getContext(), "Enviando imagem...", Toast.LENGTH_SHORT).show();

            call.enqueue(new Callback<UploadResultDto>() {
                @Override
                public void onResponse(@NonNull Call<UploadResultDto> call, @NonNull Response<UploadResultDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String uploadedImageUrl = response.body().getUrl();
                        Toast.makeText(getContext(), "Imagem enviada com sucesso!", Toast.LENGTH_SHORT).show();

                        binding.imageAtestado.setVisibility(View.VISIBLE);
                        binding.imageAtestado.setImageURI(imageUri);

                        CalendarFragment.this.uploadedImageUrl = uploadedImageUrl;

                    } else {
                        Toast.makeText(getContext(), "Erro ao enviar imagem!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UploadResultDto> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Falha no upload: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro ao processar imagem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    private void selectCalendarDay(String acao, View root) {
        if (selectDay == null) {
            if (isAdded()) ToastHelper.showFeedbackToast(activity,"info","Preencha as informações:","Verifique se selecionou um dia e preencheu os campos");
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
        if (selectDay == null) {
            if (isAdded())
                ToastHelper.showFeedbackToast(activity,"info","Preencha as informações:","Verifique se selecionou um dia e preencheu os campos corretamente");
            return;
        }

        LocalDate selectedDay = LocalDate.of(selectDay.getYear(), selectDay.getMonth(), selectDay.getDay());
        if (isWeekend(selectedDay)) {
            if (isAdded())
                ToastHelper.showFeedbackToast(activity,"info","Selecione um dia válido:","Nenhuma ação é permitida nos finais de semana");
            return;
        }

        if (selectCalendar != null) {
            selectCalendar.setPresence("falta".equals(actionSelect) ? false : true);

            EditText editTextArea = getView().findViewById(R.id.editTextArea);
            String observation = editTextArea.getText().toString();
            selectCalendar.setObservation(observation);

            if (uploadedImageUrl != null && !uploadedImageUrl.isEmpty()) {
                selectCalendar.setAttest(uploadedImageUrl);
            }

            if (selectCalendar.getId() == null) {
                createCalender(selectCalendar);
            } else {
                updateCalender(selectCalendar.getId(), selectCalendar);
            }
        } else {
            if (isAdded())
                ToastHelper.showFeedbackToast(activity,"info","Preencha as informações:","Verifique se selecionou um dia e preencheu os campos corretamente");
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
                    binding.calendarView.post(() -> {
                        CalendarDay todayDay = CalendarDay.today();
                        selectDay = todayDay;

                        blackBackgroundDecorator.setSelectedDay(todayDay);
                        binding.calendarView.setDateSelected(todayDay, true);
                        binding.calendarView.invalidateDecorators();

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR"));
                        String dataFormatada = todayDay.getDate().format(formatter);
                        selectedDayTxt.setText("Hoje");
                        selectedDayQuestionTxt.setText("Você estará presente?");

                        binding.calendarView.setCurrentDate(todayDay);
                    });

                } else {
                    ToastHelper.showFeedbackToast(activity,"error","Erro inesperado:","Não foi possível concluir a ação");
                    Log.e("CALENDAR_DEBUG", "Erro HTtP: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Calendar>> call, Throwable t) {
                Log.e("CALENDAR_DEBUG", "Falha na chamada da API", t);
                if (isAdded() && getContext() != null) {
                    ToastHelper.showFeedbackToast(activity,"error","Erro inesperado:","Não foi possível carregar os dias");
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
                    if (isAdded())  ToastHelper.showFeedbackToast(activity,"success","Ação concluída!:","Sua falta ou presença foi salva");

                    calenderByUser(idUsuario);
                }
            }

            @Override
            public void onFailure(Call<Calendar> call, Throwable t) {
                t.printStackTrace();
                if (isAdded())  ToastHelper.showFeedbackToast(activity,"error","Erro inesperado:","Não foi possível carregar os dias");
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
        if (orange == null || greenFromDb == null) {
            Log.w("CALENDAR_DEBUG", "Listas ainda não carregadas. Ignorando updateGreenDaysForMonth.");
            return;
        }
        List<CalendarDay> greenVisual = new ArrayList<>();

        LocalDate firstDayOfMonth = LocalDate.of(month.getYear(), month.getMonth(), 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        LocalDate today = LocalDate.now();
        LocalDate cursor = firstDayOfMonth;

        while (!cursor.isAfter(lastDayOfMonth)) {
            if (!cursor.isBefore(today)) {
                cursor = cursor.plusDays(1);
                continue;
            }

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

        if (greenDecorator != null) binding.calendarView.removeDecorator(greenDecorator);
        greenDecorator = new GreenBorderDecorator(getContext(), greenVisual);
        binding.calendarView.addDecorator(greenDecorator);
        binding.calendarView.invalidateDecorators();
    }

}
