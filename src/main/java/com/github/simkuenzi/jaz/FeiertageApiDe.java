package com.github.simkuenzi.jaz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;

public class FeiertageApiDe implements HolidayResolution {
    private final int year;

    public FeiertageApiDe(int year) {
        this.year = year;
    }

    @Override
    public Iterator<String> validExpressions() throws Exception {
        return new ObjectMapper().readTree(loadHolidays(year)).fieldNames();
    }

    @Override
    public Optional<LocalDate> resolve(String expression) throws Exception {
        JsonNode holidays = new ObjectMapper().readTree(loadHolidays(year));
        if (holidays.has(expression)) {
            String dateText = holidays.get(expression).get("datum").asText();
            return Optional.of(LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE));
        }
        return Optional.empty();
    }

    private String loadHolidays(int year) throws java.io.IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(String.format("https://feiertage-api.de/api/?jahr=%d&nur_land=NATIONAL", year));
        HttpRequest httpRequest = HttpRequest.newBuilder(url).build();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
    }
}
