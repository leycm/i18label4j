package de.leycm.i18label4j.label;

import de.leycm.i18label4j.Label;
import de.leycm.i18label4j.LabelProvider;
import de.leycm.i18label4j.mapping.Mapping;
import lombok.NonNull;

import java.util.*;

public record LiteralLabel(
        @NonNull LabelProvider provider,
        @NonNull Set<Mapping> mappings,
        @NonNull String literal
) implements Label {

    public LiteralLabel { }

    public LiteralLabel(@NonNull LabelProvider provider,
                        @NonNull String literal) {
        this(provider, new HashSet<>(), literal);
    }

    @Override
    public @NonNull Set<Mapping> mappings() {
        return Collections.unmodifiableSet(mappings);
    }

    @Override
    public @NonNull Label mapTo(@NonNull Mapping mapping) {
        mappings.add(mapping);
        return this;
    }

    @Override
    public @NonNull String in(@NonNull Locale locale) {
        return literal;
    }

    @Override
    public @NonNull String toString() {
        try {return serialize();
        } catch (Throwable e) {
            return Objects.toString(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LiteralLabel that = (LiteralLabel) obj;
        return provider.equals(that.provider()) &&
                literal.equals(that.literal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, literal);
    }
}
