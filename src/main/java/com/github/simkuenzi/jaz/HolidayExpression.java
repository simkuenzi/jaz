package com.github.simkuenzi.jaz;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HolidayExpression {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @JsonProperty
    private String text;

    public Parsed parse(HolidayResolution holidayResolution) throws Exception {
        Optional<LocalDate> resolved = holidayResolution.resolve(text);
        return resolved.map(d -> (Parsed) new ValidHoliday(d)).orElse(new InvalidHoliday());
    }

    public interface Parsed {
        @JsonProperty
        LocalDate getDate();
        @JsonProperty
        String getText();
        @JsonProperty
        boolean isValid();
    }

    private class ValidHoliday implements Parsed {
        private final LocalDate date;

        private ValidHoliday(LocalDate date) {
            this.date = date;
        }

        @Override
        public LocalDate getDate() {
            return date;
        }

        @Override
        public String getText() {
            return String.format("%s (%s)", text, dateFormat.format(date));
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }

    private static class InvalidHoliday implements Parsed {
        @Override
        public LocalDate getDate() {
            return null;
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public boolean isValid() {
            return false;
        }
    }
}
