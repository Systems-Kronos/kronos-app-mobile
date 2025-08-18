package com.example.kronosprojeto.decorator;


import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.kronosprojeto.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;

public class GreenBorderDecorator implements DayViewDecorator {
    private final Collection<CalendarDay> datas;
    private final Context context;

    public GreenBorderDecorator(Context context, Collection<CalendarDay> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return datas.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_dia_borda_verde));
    }
}

