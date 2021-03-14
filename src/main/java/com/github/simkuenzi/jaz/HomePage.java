package com.github.simkuenzi.jaz;

import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class HomePage {
    private final Context context;
    private final Holidays holidays;

    public HomePage(Context context) {
        this.context = context;
        holidays = new CachedHolidays(
                new AdaptedHolidays(
                        new FeiertageApiDe(year()),
                        Arrays.asList(
                                new Holiday("Berchtoldstag", LocalDate.of(year(), 1, 2)),
                                new Holiday("Nationalfeiertag", LocalDate.of(year(), 8, 1))),
                        Collections.singletonList(LocalDate.of(year(), 10, 3))
                ));
    }

    public void show(Map<String, Object> vars) {
        vars.put("year", year());
        vars.put("holidayItems", holidayItems());
        vars.put("selectedHolidays", selectedHolidays());
        vars.put("companyHolidaysBegin", companyHolidaysBegin());
        vars.put("companyHolidaysEnd", companyHolidaysEnd());
        vars.put("companyHolidays", companyHolidays());
        vars.put("exceptionDays", exceptionDays());
        vars.put("hours", hours());
        vars.put("monday", monday());
        vars.put("tuesday", tuesday());
        vars.put("wednesday", wednesday());
        vars.put("thursday", thursday());
        vars.put("friday", friday());
        vars.put("saturday", saturday());
        vars.put("sunday", sunday());
        vars.put("jazHours", jazHours());
    }

    private int exceptionDays() {
        return Integer.parseInt(Objects.requireNonNull(context.formParam("exceptionDays", "0")));
    }

    private List<SelectItem<LocalDate>> holidayItems() {
        return holidays.get().stream().map(hd -> hd.asItem(selectedHolidays())).collect(Collectors.toList());
    }

    private List<LocalDate> selectedHolidays() {
        return context.method().equalsIgnoreCase("post") ?
                Objects.requireNonNull(context.formParams("holidays")).stream()
                        .map(s -> LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE))
                        .collect(Collectors.toList()) :
                holidays.get().stream().map(Holiday::asDate).collect(Collectors.toList());
    }

    private LocalDate companyHolidaysBegin() {
        return LocalDate.of(year(), 12, 27);
    }

    private LocalDate companyHolidaysEnd() {
        return LocalDate.of(year(), 12, 31);
    }

    private boolean companyHolidays() {
        return checkboxField("companyHolidays", true);
    }

    private int hours() {
        return Integer.parseInt(Objects.requireNonNull(context.formParam("hours", "8")));
    }

    private boolean monday() {
        return checkboxField("monday", true);
    }

    private boolean tuesday() {
        return checkboxField("tuesday", true);
    }

    private boolean wednesday() {
        return checkboxField("wednesday", true);
    }

    private boolean thursday() {
        return checkboxField("thursday", true);
    }

    private boolean friday() {
        return checkboxField("friday", false);
    }

    private boolean saturday() {
        return checkboxField("saturday", false);
    }

    private boolean sunday() {
        return checkboxField("sunday", false);
    }

    private boolean checkboxField(String name, boolean defaultValue) {
        if (context.method().equalsIgnoreCase("post")) {
            return context.formParam(name) != null;
        }
        return defaultValue;
    }

    private int year() {
        String yearParam = context.queryParam("year");
        return yearParam != null ? Integer.parseInt(yearParam) : LocalDate.now().getYear();
    }

    private int jazHours() {
        LocalDate firstDay = LocalDate.now().withYear(year()).with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDay = LocalDate.now().withYear(year()).with(TemporalAdjusters.lastDayOfYear());

        int days = 0;
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            boolean workWeekday = switch (date.getDayOfWeek()) {
                case MONDAY -> monday();
                case TUESDAY -> tuesday();
                case WEDNESDAY -> wednesday();
                case THURSDAY -> thursday();
                case FRIDAY -> friday();
                case SATURDAY -> saturday();
                case SUNDAY -> sunday();
            };
            if (workWeekday && !selectedHolidays().contains(date) &&
                    (companyHolidaysBegin().isAfter(date) || companyHolidaysEnd().isBefore(date))) {
                days++;
            }
        }

        return (days - exceptionDays()) * hours();
    }
}
