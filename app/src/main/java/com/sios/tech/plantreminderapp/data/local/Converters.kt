package com.sios.tech.plantreminderapp.data.local

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converters for Room database to handle Date objects.
 * 
 * This class provides conversion methods between [Date] and [Long] types
 * for Room database storage. Room cannot store complex objects directly,
 * so we need to convert Date objects to a format that Room can store (Long)
 * and vice versa.
 */
class Converters {
    /**
     * Converts a timestamp (stored in database) to a Date object.
     *
     * @param value The timestamp in milliseconds since January 1, 1970, 00:00:00 GMT.
     *             Can be null if no date was stored.
     * @return A [Date] object representing the timestamp, or null if the input was null.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converts a Date object to a timestamp for database storage.
     *
     * @param date The [Date] object to convert. Can be null.
     * @return The timestamp in milliseconds since January 1, 1970, 00:00:00 GMT,
     *         or null if the input date was null.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
