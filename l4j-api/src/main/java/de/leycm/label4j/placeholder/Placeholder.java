package de.leycm.label4j.placeholder;

import de.leycm.init4j.instance.Instanceable;
import de.leycm.label4j.LabelProvider;

import lombok.NonNull;

import java.util.Objects;
import java.util.function.Supplier;

public record Placeholder(
        @NonNull String key,
        @NonNull Supplier<Object> value
) implements Comparable<Placeholder> {

    public Placeholder {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Placeholder key must not be empty");
        }

        for (char c : key.toCharArray()) {
            if (PlaceholderRule.isKeyChar(c, false)) {
                throw new IllegalArgumentException("Placeholder key contains illegal character: " + c);
            }
        }
    }

    public @NonNull Object get() {
        return value.get();
    }

    public @NonNull String getAsString() {
        return String.valueOf(value.get());
    }

    @Override
    public int compareTo(final @NonNull Placeholder other) {
        return this.key.compareTo(other.key);
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Placeholder that = (Placeholder) object;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    @Override
    public @NonNull String toString() {
        if (!Instanceable.hasInstance(LabelProvider.class)) {
            return toString(PlaceholderRule.DOLLAR_CURLY);
        }
        // todo: replace with return toString(LabelProvider.getInstance().placeholderRule());
        return "";
    }

    public @NonNull String toString(final @NonNull PlaceholderRule rule) {
        return rule.prefix() + key + rule.suffix().orElse("");
    }
}
