package com.example.kronosprojeto.decorator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GreenBorderDecorator implements DayViewDecorator {

    private final Set<CalendarDay> dates = new HashSet<>();

    public GreenBorderDecorator(Context context, List<CalendarDay> dates) {
        this.dates.addAll(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(4, Color.GREEN); // borda verde
        drawable.setColor(Color.TRANSPARENT); // fundo transparente

        // CORREÇÃO: Usa setSelectionDrawable para evitar conflito com o background
        view.setSelectionDrawable(drawable);
    }
}