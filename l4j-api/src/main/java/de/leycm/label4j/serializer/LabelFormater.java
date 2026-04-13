package de.leycm.label4j.serializer;

import de.leycm.label4j.excaption.FormatException;

import lombok.NonNull;

@FunctionalInterface
public interface LabelFormater<T> {

    @NonNull T format(@NonNull String input) throws FormatException;
}
