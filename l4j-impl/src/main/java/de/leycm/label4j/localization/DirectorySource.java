/*
 * This file is part of label4j - https://github.com/leycm/label4j.
 * Copyright (C) 2026 Lennard [leycm] <leycm@proton.me>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.leycm.label4j.localization;

import de.leycm.label4j.locale.Locales;
import de.leycm.label4j.parsing.FlatFileParser;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectorySource implements LocalizationSource {

    private final @NonNull Path baseDirectory;
    private final @NonNull FlatFileParser parser;
    private final Map<Locale, Map<String, Localization>> cache = new ConcurrentHashMap<>();

    @Contract(value = "_, _ -> new", pure = true)
    public static @NonNull DirectorySource from(
            final @NonNull String directory,
            final @NonNull FlatFileParser parser
    ) {
        return from(URI.create(directory), parser);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NonNull DirectorySource from(
            final @NonNull URI directory,
            final @NonNull FlatFileParser parser
    ) {
        return from(Path.of(directory), parser);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NonNull DirectorySource from(
            final @NonNull Path directory,
            final @NonNull FlatFileParser parser
    ) {
        return new DirectorySource(directory, parser);
    }

    DirectorySource(
            final @NonNull Path baseDirectory,
            final @NonNull FlatFileParser parser
    ) {
        this.baseDirectory = baseDirectory;
        this.parser = parser;
    }

    @Override
    public @NonNull Set<Locale> getLocalizations() {
        try (Stream<Path> stream = Files.list(baseDirectory)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .map(name -> {
                        try {
                            return Locales.filenameToLocale(name);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
        } catch (IOException e) {
            return Set.of();
        }
    }

    @Override
    public boolean containsLocalization(@NonNull Locale locale) {
        final Path localeDir = baseDirectory.resolve(Locales.localeToFilename(locale));
        return Files.isDirectory(localeDir);
    }

    @Override
    public @Unmodifiable @NonNull Map<String, Localization> getLocalization(
            final @NonNull Locale locale
    ) {
        return cache.computeIfAbsent(locale, loc -> {
            try {
                return loadLocalization(loc);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private @NonNull @UnmodifiableView Map<String, Localization> loadLocalization(
            final @NonNull Locale locale
    ) throws Exception {
        final Path localeDir = baseDirectory.resolve(Locales.localeToFilename(locale));
        if (!Files.isDirectory(localeDir)) {
            throw new IOException("Locale directory not found: " + localeDir);
        }

        final Map<String, Localization> result = new HashMap<>();
        final String ending = parser.getEnding();

        try (Stream<Path> files = Files.walk(localeDir)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(ending))
                    .forEach(file -> {
                        try {
                            final Path relativePath = localeDir.relativize(file);
                            String keyPrefix = relativePath.toString();

                            if (keyPrefix.endsWith(ending)) {
                                keyPrefix = keyPrefix.substring(0, keyPrefix.length() - ending.length());
                            }

                            keyPrefix = keyPrefix.replace(file.getFileSystem().getSeparator(), ".");

                            final Map<String, String> parsed = parser.parse(file);
                            for (Map.Entry<String, String> entry : parsed.entrySet()) {
                                final String fullKey = keyPrefix.isEmpty()
                                        ? entry.getKey()
                                        : keyPrefix + "." + entry.getKey();
                                result.put(fullKey,
                                        Localization.of(fullKey, locale, entry.getValue()));
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse " + file, e);
                        }
                    });
        }

        return Collections.unmodifiableMap(result);
    }
}