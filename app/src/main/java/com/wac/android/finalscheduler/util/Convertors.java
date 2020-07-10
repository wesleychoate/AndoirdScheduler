package com.wac.android.finalscheduler.util;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.util.UUID;

public class Convertors {

    @TypeConverter
    public static LocalDate toLocalDate(Long value) {
        return LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long toLong(LocalDate value) {
        return value.toEpochDay();
    }

    @TypeConverter
    public static UUID toUUID(String uuid) { return (uuid != null ? UUID.fromString(uuid) : null); }

    @TypeConverter
    public static String fromUUID(UUID uuid) { return (uuid != null ? uuid.toString() : null); }
}
