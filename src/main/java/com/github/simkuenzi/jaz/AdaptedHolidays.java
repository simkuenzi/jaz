package com.github.simkuenzi.jaz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AdaptedHolidays implements Holidays {
    private final Holidays basedOn;
    private final List<Holiday> additional;
    private final List<LocalDate> ignored;

    public AdaptedHolidays(Holidays basedOn, List<Holiday> additional, List<LocalDate> ignored) {
        this.basedOn = basedOn;
        this.additional = additional;
        this.ignored = ignored;
    }

    @Override
    public List<Holiday> get() {
        List<Holiday> holidays = new ArrayList<>();
        holidays.addAll(basedOn.get().stream().filter(hd -> !hd.containedBy(ignored)).collect(Collectors.toList()));
        holidays.addAll(additional);
        Collections.sort(holidays);
        return holidays;
    }
}
