package com.github.simkuenzi.jaz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FeiertageApiDe implements Holidays {
    private final int year;

    public FeiertageApiDe(int year) {
        this.year = year;
    }

    @Override
    public List<Holiday> get() {
        List<Holiday> holidays = new ArrayList<>();
        JsonNode json;
        try {
            json = new ObjectMapper().readTree(loadHolidays(year));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Iterator<String> holidayNames = json.fieldNames();
        while (holidayNames.hasNext()) {
            String holidayName = holidayNames.next();
            String dateText = json.get(holidayName).get("datum").asText();
            LocalDate date = LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
            holidays.add(new Holiday(holidayName, date));
        }

        return holidays;
    }

    private String loadHolidays(int year) throws java.io.IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create(String.format("https://feiertage-api.de/api/?jahr=%d&nur_land=NATIONAL", year));
        HttpRequest httpRequest = HttpRequest.newBuilder(url).build();
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
    }
}
