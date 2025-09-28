package com.example.kronosprojeto.decorator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class BlackBackgroundDecorator implements DayViewDecorator {

    private CalendarDay selectedDay;

    public BlackBackgroundDecorator(Context context) {
    }

    public void setSelectedDay(CalendarDay day) {
        this.selectedDay = day;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return selectedDay != null && day.equals(selectedDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.BLACK); // fundo preto
        view.setBackgroundDrawable(drawable);
    }
}
