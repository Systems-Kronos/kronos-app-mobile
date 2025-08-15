package com.example.kronosprojeto.decorator;

import android.content.Context;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.example.kronosprojeto.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;
import java.util.Set;

public class BlackBackgroundDecorator implements DayViewDecorator {

    private final Context context;
    private final Set<CalendarDay> datas = new HashSet<>();

    public BlackBackgroundDecorator(Context context) {
        this.context = context;
    }

    // Seleção única
    public void setSelectedDay(CalendarDay day) {
        datas.clear(); // Remove qualquer dia anterior
        if (day != null) {
            datas.add(day); // Adiciona o novo
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return datas.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_dia_preenchimento_preto));
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
    }
}
