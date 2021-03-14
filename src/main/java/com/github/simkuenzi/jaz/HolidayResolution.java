package com.github.simkuenzi.jaz;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

interface HolidayResolution {
    Iterator<String> validExpressions() throws Exception;

    Optional<LocalDate> resolve(String expression) throws Exception;
}
