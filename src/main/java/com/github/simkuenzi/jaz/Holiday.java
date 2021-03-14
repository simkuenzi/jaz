package com.github.simkuenzi.jaz;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public class Holiday implements Comparable<Holiday> {
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

    public LocalDate asDate() {
        return date;
    }

    public boolean containedBy(List<LocalDate> l) {
        return l.contains(date);
    }

    @Override
    public int compareTo(@NotNull Holiday o) {
        return date.compareTo(o.date);
    }

    private String getText() {
        return String.format("%s am %s", name, date.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")));
    }
}
