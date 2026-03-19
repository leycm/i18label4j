/*
 * This file is part of the i18label4j Library.
 *
 * Licensed under the GNU Lesser General Public License v3.0 (LGPL-3.0)
 * You should have received a copy of the license in LICENSE.LGPL
 * If not, see https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Copyright 2026 (c) leycm <leycm@proton.me>
 * Copyright 2026 (c) maintainers
 */
package de.leycm.i18label4j;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public record LocalizedResult(@Nullable String localized) {

    public @NonNull String or(final @NonNull String defaultValue) {
        return localized != null ? localized : defaultValue;
    }

}
