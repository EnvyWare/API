package com.envyful.api.text.parse;

import com.envyful.api.text.ParseResult;
import com.envyful.api.text.Placeholder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class ComposedPlaceholder implements Placeholder {

    protected final List<Placeholder> placeholders;

    protected ComposedPlaceholder(List<Placeholder> placeholders) {
        this.placeholders = List.copyOf(placeholders);
    }

    public static Placeholder of(List<Placeholder> placeholders) {
        return new ComposedPlaceholder(placeholders);
    }

    public static Placeholder of(Placeholder... placeholders) {
        return new ComposedPlaceholder(List.of(placeholders));
    }

    @Override
    public @NonNull ParseResult replace(@NonNull ParseResult line) {
        for (Placeholder placeholder : this.placeholders) {
            line = placeholder.replace(line);
        }

        return line;
    }
}
