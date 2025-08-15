package com.example.kronosprojeto.decorator;


import android.content.Context;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.example.kronosprojeto.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class GrayBorderDecorator implements DayViewDecorator {
    private final Context context;

    public GrayBorderDecorator(Context context) {
        this.context = context;
    }


    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(day.getYear(), day.getMonth() - 1, day.getDay());

        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return (weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_dia_borda_cinza));
    }
}

