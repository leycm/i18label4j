/*
 * This file is part of the linguae Library.
 *
 * Licensed under the GNU Lesser General Public License v3.0 (LGPL-3.0)
 * You should have received a copy of the license in LICENSE.LGPL
 * If not, see https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Copyright (c) leycm <leycm@proton.me>
 * Copyright (c) maintainers
 */
package com.example.template;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommonExampleProvider implements ExampleProvider {

    @Contract(value = " -> new", pure = true)
    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, String> exampleMap = new ConcurrentHashMap<>();
        private String exampleValue = "";

        private Builder() { }

        public Builder exampleValue(final @NonNull String exampleValue) {
            this.exampleValue = exampleValue;
            return this;
        }

        public Builder withExampleEntry(final @NonNull String key, final @Nullable String value) {
            if (value == null) {
                exampleMap.remove(key);
                return this;
            }

            exampleMap.put(key, value);
            return this;
        }

        public CommonExampleProvider build(final @NonNull String noDefaultValue) {
            return new CommonExampleProvider(exampleValue, exampleMap, noDefaultValue);
        }

    }

    private final Map<String, String> exampleMap;
    private final String exampleValue;
    private final String noDefaultValue;

    private CommonExampleProvider(final @NonNull String exampleValue,
                                  final @NonNull Map<String, String> exampleMap,
                                  final @NonNull String noDefaultValue) {

        this.exampleValue = exampleValue;
        this.exampleMap = exampleMap;
        this.noDefaultValue = noDefaultValue;
    }

    @Override
    public @NonNull String getExampleValue() {
        return exampleValue;
    }

    @Override
    public @NonNull String getNoDefaultValue() {
        return noDefaultValue;
    }

    @Override
    public @Nullable String getExampleValue(final @NonNull String key) {
        return exampleMap.get(key);
    }

}
