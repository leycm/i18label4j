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

import com.moandjiezana.toml.Toml;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

final class TomlParser implements FileParser {

    // local toml4j instance - thread-safe
    private final Toml toml = new Toml();
    private final @NonNull String extension;

    TomlParser(@NonNull String extension) {
        this.extension = extension;
    }

    @Override
    public @NonNull String getExtension() {
        return extension;
    }

    @Override
    public @NonNull Map<String, String> parse(final @NonNull Path file) throws IllegalArgumentException {
        try {
            final String content = FileParser.readFile(file);
            final Map<String, Object> raw = toml.read(content).toMap();
            return FileParser.flattenRaw(raw);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read file: " + file, e);
        }
    }
}