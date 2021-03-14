package com.github.simkuenzi.jaz;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class Holiday {
    private final String name;
    private final LocalDate date;

    public Holiday(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }

    public SelectItem<LocalDate> asItem(Collection<LocalDate> selectedValues) {
        return new SelectItem<>() {
            @Override
            public String getText() {
                return Holiday.this.getText();
            }

            @Override
            public LocalDate getValue() {
                return date;
            }

            @Override
            public boolean isSelected() {
                return selectedValues.contains(date);
            }
        };
    }

    private String getText() {
        return String.format("%s am %s", name, date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }
}
