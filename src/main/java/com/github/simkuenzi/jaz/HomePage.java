package com.github.simkuenzi.jaz;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomePage {
    private final Context context;
    private final ObjectMapper objectMapper;

    public HomePage(Context context, ObjectMapper objectMapper) {
        this.context = context;
        this.objectMapper = objectMapper;
    }

    public void show(Map<String, Object> vars) throws Exception {
        vars.put("year", year());
        vars.put("holidays", holidayResolution().validExpressions());
        vars.put("exceptionDays", exceptionDays());
        vars.put("holiday", holiday());
        vars.put("holidayExceptions", objectMapper.writeValueAsString(holidayExceptions()));
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

    public HolidayExpression.Parsed resolveHoliday() throws Exception {
        return objectMapper
                .readValue(context.body(), HolidayExpression.class)
                .parse(holidayResolution());
    }

    private int exceptionDays() {
        return Integer.parseInt(Objects.requireNonNull(context.formParam("exceptionDays", "0")));
    }

    private String holiday() {
        return Objects.requireNonNull(context.formParam("holiday", ""));
    }

    private HolidayExceptions holidayExceptions() throws Exception {
        return objectMapper.readValue(context.formParam("holidayExceptions", "{ \"list\": [] }"), HolidayExceptions.class);
    }

    private int hours() {
        return Integer.parseInt(Objects.requireNonNull(context.formParam("hours", "8")));
    }

    private boolean monday() {
        return weekdayField("monday", true);
    }

    private boolean tuesday() {
        return weekdayField("tuesday", true);
    }

    private boolean wednesday() {
        return weekdayField("wednesday", true);
    }

    private boolean thursday() {
        return weekdayField("thursday", true);
    }

    private boolean friday() {
        return weekdayField("friday", false);
    }

    private boolean saturday() {
        return weekdayField("saturday", false);
    }

    private boolean sunday() {
        return weekdayField("sunday", false);
    }

    private boolean weekdayField(String name, boolean defaultValue) {
        if (context.method().toLowerCase(Locale.ROOT).equals("post")) {
            return context.formParam(name) != null;
        }
        return defaultValue;
    }

    private int year() {
        String yearParam = context.queryParam("year");
        return yearParam != null ? Integer.parseInt(yearParam) : LocalDate.now().getYear();
    }

    private int jazHours() throws Exception {
        LocalDate firstDay = LocalDate.now().withYear(year()).with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDay = LocalDate.now().withYear(year()).with(TemporalAdjusters.lastDayOfYear());
        List<LocalDate> holidays = holidayExceptions().getList().stream().map(
                HolidayExceptions.HolidayException::getDate).collect(Collectors.toList());

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
            if (workWeekday && !holidays.contains(date)) {
                days++;
            }
        }

        return (days - exceptionDays()) * hours();
    }

    private HolidayResolution holidayResolution() {
        return new FeiertageApiDe(year());
    }
}
