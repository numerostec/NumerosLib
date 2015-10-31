package me.numeros.numeroslib;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Abraham on 21/08/2015.
 */
public class DateWatcher implements TextWatcher {
    private String current = "";
    private String format = "ddmmaaaa";

    private Calendar cal;
    private EditText date;

    public DateWatcher(EditText editText, Calendar calendar) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        date = editText;
        cal = calendar;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(current)) {
            String clean = s.toString().replaceAll("[^\\d.]", "");
            String cleanC = current.replaceAll("[^\\d.]", "");

            int cl = clean.length();
            int sel = cl;
            for (int i = 2; i <= cl && i < 6; i += 2) {
                sel++;
            }
            //Fix for pressing delete next to a forward slash
            if (clean.equals(cleanC)) sel--;

            if (clean.length() < 8) {
                clean = clean + format.substring(clean.length());
            } else {
                //This part makes sure that when we finish entering numbers
                //the date is correct, fixing it otherways
                int day = Integer.parseInt(clean.substring(0, 2));
                int mon = Integer.parseInt(clean.substring(2, 4));
                int year = Integer.parseInt(clean.substring(4, 8));

                if (mon > 12) mon = 12;
                cal.set(Calendar.MONTH, mon - 1);
                day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                clean = String.format("%02d%02d%02d", day, mon, year);
            }

            clean = getFormat(clean);
            current = clean;
            date.setText(current);
            date.setSelection(sel < current.length() ? sel : current.length());
        }
    }

    public String getFormat() {
        return getFormat(format);
    }

    private String getFormat(String clean) {
        return String.format("%s/%s/%s", clean.substring(0, 2),
                clean.substring(2, 4),
                clean.substring(4, 8));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
