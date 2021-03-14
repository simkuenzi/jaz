package com.github.simkuenzi.jaz;

import java.util.List;

public class CachedHolidays implements Holidays {
    private Holidays basedOn;
    private List<Holiday> cached;

    public CachedHolidays(Holidays basedOn) {
        this.basedOn = basedOn;
    }

    @Override
    public List<Holiday> get() {
        if (cached == null) {
            cached = basedOn.get();
        }
        return cached;
    }
}
