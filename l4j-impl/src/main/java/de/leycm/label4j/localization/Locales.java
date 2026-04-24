package de.leycm.label4j.localization;

import lombok.NonNull;

import java.util.Locale;

public class Locales {
    static @NonNull String localeToFilename(final @NonNull Locale locale) {
        return locale.getLanguage().toLowerCase() + "_" + locale.getCountry().toLowerCase();
    }

    static @NonNull Locale filenameToLocale(final @NonNull String filename) {
        return Locale.forLanguageTag(filename.replace("_", "-"));
    }
}
