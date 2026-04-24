/*
 * Copyright (C) 2026 leycm <leycm@proton.me>
 *
 * This file is part of label4j.
 *
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
package de.leycm.label4j.parsing;

import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public interface FileParser {

    static @NonNull FileParser json(final @NonNull String extension) {
        return new JsonParser(extension);
    }

    static @NonNull FileParser json() {
        return new JsonParser("json");
    }

    static @NonNull FileParser yaml(final @NonNull String extension) {
        return new YamlParser(extension);
    }

    static @NonNull FileParser yaml() {
        return new YamlParser("yaml");
    }

    static @NonNull FileParser yml() {
        return new YamlParser("yml");
    }

    static @NonNull FileParser toml(final @NonNull String extension) {
        return new TomlParser(extension);
    }

    static @NonNull FileParser toml() {
        return new TomlParser("toml");
    }

    static @NonNull FileParser properties(final @NonNull String extension) {
        return new PropertyParser(extension);
    }

    static @NonNull FileParser properties() {
        return new PropertyParser("properties");
    }

    @NonNull String getExtension();

    default @NonNull String getEnding() {
        return "." + getExtension();
    }

    @NonNull Map<String, String> parse(@NonNull Path file) throws Exception;

    // ==== Helper Methods ====================================================

    // Converts a raw {@code Map<String, Object>} into a
    // {@code Map<String, String>} by calling {@link String#valueOf(Object)}
    // on each value.
    // {@code null} values in {@code raw} are preserved as
    // {@code null} in the result.
    static @NonNull Map<String, String> flattenRaw(
            final @NonNull Map<String, Object> raw) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            result.put(entry.getKey(),
                    entry.getValue() == null ? null : entry.getValue().toString());
        }
        return result;
    }

    // Helper to read file content
    static @NonNull String readFile(final @NonNull Path file) throws IOException {
        return Files.readString(file);
    }

}