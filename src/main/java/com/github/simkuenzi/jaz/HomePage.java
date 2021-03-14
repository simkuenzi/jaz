package com.github.simkuenzi.jaz;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

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

    private int year() {
        String yearParam = context.queryParam("year");
        return yearParam != null ? Integer.parseInt(yearParam) : LocalDate.now().getYear();
    }

    private HolidayResolution holidayResolution() {
        return new FeiertageApiDe(year());
    }
}
