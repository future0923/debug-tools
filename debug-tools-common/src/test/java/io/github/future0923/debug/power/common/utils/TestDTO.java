package io.github.future0923.debug.power.common.utils;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author future0923
 */
@Data
public class TestDTO {

    private LocalDateTime localDateTime;

    private LocalDate localDate;

    private LocalTime localTime;

    private Date date;

    private Calendar calendar;
}
