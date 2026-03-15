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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ExampleProvider {

    @NonNull String getExampleValue();

    @NonNull String getNoDefaultValue();

    @Nullable String getExampleValue(@NonNull String key);

}