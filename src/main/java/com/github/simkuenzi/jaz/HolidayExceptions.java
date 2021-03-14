package com.github.simkuenzi.jaz;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public class HolidayExceptions {
    @JsonProperty
    private List<HolidayException> list;

    public List<HolidayException> getList() {
        return list;
    }

    static class HolidayException  {
        @JsonProperty
        private LocalDate date;
        @JsonProperty
        private String text;
        @JsonProperty
        private boolean valid;

        public LocalDate getDate() {
            return date;
        }

        public String getText() {
            return text;
        }

        public boolean isValid() {
            return valid;
        }
    }
}
