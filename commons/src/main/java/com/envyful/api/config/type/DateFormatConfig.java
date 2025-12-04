package com.envyful.api.config.type;

import com.envyful.api.text.Placeholder;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 *
 * Represents a configurable date format.
 * <br>
 * The format is the format that the date will be formatted to when using the
 * {@link DateFormatConfig#format(Instant)} method.
 *
 */
@ConfigSerializable
public class DateFormatConfig {

    private String format = "dd/MM/yyyy HH:mm:ss";
    private transient DateFormat dateFormat;

    public DateFormatConfig() {}

    public DateFormatConfig(String format) {
        this.format = format;
    }

    public DateFormat getFormat() {
        if (this.dateFormat == null) {
            this.dateFormat = new SimpleDateFormat(this.format);
        }

        return this.dateFormat;
    }

    public String format(Instant instant) {
        return this.format(new Date(instant.toEpochMilli()));
    }

    public String format(Date date) {
        return this.getFormat().format(date);
    }

    public String format(long time) {
        return this.format(new Date(time));
    }

    public Placeholder wrap(Instant date) {
        return this.wrap("%date%", date);
    }

    public Placeholder wrap(Date date) {
        return this.wrap("%date%", date);
    }

    public Placeholder wrap(long date) {
        return this.wrap("%date%", date);
    }

    public Placeholder wrap(String key, Instant date) {
        return Placeholder.simple(key, this.format(date));
    }

    public Placeholder wrap(String key, Date date) {
        return Placeholder.simple(key, this.format(date));
    }

    public Placeholder wrap(String key, long date) {
        return Placeholder.simple(key, this.format(date));
    }
}
