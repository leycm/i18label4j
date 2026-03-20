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
package de.leycm.i18label4j.mapping;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class MappingRule {

    /** Double Curly style: {@code {{variable}}} (Vue, Handlebars, Jinja) */
    public static final @NonNull MappingRule DOUBLE_CURLY = new MappingRule("{{", "}}");

    /** Dollar Curly style: {@code ${variable}} (ES6, Kotlin, Bash-Strings) */
    public static final @NonNull MappingRule DOLLAR_CURLY = new MappingRule("${", "}");

    /** Section style: {@code §{variable}} (Custom/Legacy) */
    public static final @NonNull MappingRule SECTION_CURLY = new MappingRule("§{", "}");

    /** Tag style: {@code <variable>} (XML/HTML-ish) */
    public static final @NonNull MappingRule TAG = new MappingRule("<", ">");

    /** Curly style: {@code {variable}} (MessageFormat, Python f-strings) */
    public static final @NonNull MappingRule CURLY = new MappingRule("{", "}");

    /** Percent style: {@code %variable%} (Windows Environment, Batch) */
    public static final @NonNull MappingRule PERCENT = new MappingRule("%", "%");

    /** Bracket style: {@code [variable]} (BBCode, Wiki-Syntax) */
    public static final @NonNull MappingRule BRACKET = new MappingRule("[", "]");

    /** Shell style: {@code $variable} (Unix Shell, PHP) */
    public static final @NonNull MappingRule SHELL = new MappingRule("$", "");

    /** Format String style: {@code %variable} (C-style, String.format) */
    public static final @NonNull MappingRule FORMAT_STRING = new MappingRule("%", "");

    /** MiniMessage style: {@code <var:variable>} (Adventure/Kyori) */
    public static final @NonNull MappingRule MINI_MESSAGE = new MappingRule("<var:", ">");

    /** Minecraft Legacy style: {@code §:variable} */
    public static final @NonNull MappingRule MINECRAFT_LEGACY = new MappingRule("§:", "");

    private static final String REGEX_META = "\\.^$*+?()[]{}|";
    private static final String KEY_REGEX = "([A-Za-z0-9_.\\-]+)";
    private static final String ESCAPED_PREFIX = "\u0001P";
    private static final String ESCAPED_SUFFIX = "\u0001S";
    private static final int INPUT_LIMIT = 100_000_000;
    private static final int MAX_MATCHES = 10_000;

    private final String prefix;
    private final String suffix;
    private final Pattern pattern;

    private final @Nullable String escapedPrefixLiteral;
    private final @Nullable String escapedSuffixLiteral;

    private static @NonNull String regexEscape(final @NonNull String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (REGEX_META.indexOf(c) >= 0) sb.append('\\');
            sb.append(c);
        }
        return sb.toString();
    }

    public MappingRule(final @NonNull String prefix, final @NonNull String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;


        this.escapedPrefixLiteral = prefix.isEmpty() ? null : "\\" + prefix;
        this.escapedSuffixLiteral = suffix.isEmpty() ? null : "\\" + suffix;

        if (suffix.isEmpty()) {
            this.pattern = Pattern.compile(regexEscape(prefix) + KEY_REGEX, Pattern.UNICODE_CASE);
        } else {
            this.pattern = Pattern.compile(regexEscape(prefix) + KEY_REGEX + regexEscape(suffix));
        }
    }

    public @NonNull String apply(final @NonNull String input, final @NonNull Set<Mapping> mappings) {
        if (input.length() > INPUT_LIMIT) throw new IllegalArgumentException("Input too large");
        if (mappings.isEmpty() || input.isEmpty()) return input;

        int firstPrefix = input.indexOf(prefix);
        if (firstPrefix < 0) return input;

        final Map<String, String> lookup = buildLookup(mappings);

        boolean hasEscape = !prefix.isEmpty() && input.indexOf('\\') >= 0
                && input.indexOf('\\' + prefix.charAt(0)) >= 0;

        String working = hasEscape ? protectEscapes(input) : input;

        Matcher matcher = pattern.matcher(working);

        StringBuilder sb = null;
        int lastEnd = 0;
        int matchCount = 0;

        while (matcher.find()) {
            if (++matchCount > MAX_MATCHES) break;

            String key = matcher.group(1);
            String replacement = lookup.get(key);
            if (replacement == null) continue;

            if (sb == null) {
                sb = new StringBuilder(working.length() + 32);
            }

            sb.append(working, lastEnd, matcher.start());
            sb.append(replacement);
            lastEnd = matcher.end();
        }

        if (sb == null) return input;

        sb.append(working, lastEnd, working.length());

        String result = sb.toString();
        return hasEscape ? restoreEscapes(result) : result;
    }

    private @NonNull Map<String, String> buildLookup(final @NonNull Set<Mapping> mappings) {
        int size = mappings.size();
        if (size == 1) {
            Mapping m = mappings.iterator().next();
            return Map.of(m.key(), m.valueAsString());
        }
        Map<String, String> map = new HashMap<>((int) (size / 0.75f) + 1);
        for (Mapping m : mappings) map.put(m.key(), m.valueAsString());
        return map;
    }

    private @NonNull String protectEscapes(@NonNull String s) {
        if (escapedPrefixLiteral != null) s = s.replace(escapedPrefixLiteral, ESCAPED_PREFIX);
        if (escapedSuffixLiteral != null) s = s.replace(escapedSuffixLiteral, ESCAPED_SUFFIX);
        return s;
    }

    private @NonNull String restoreEscapes(@NonNull String s) {
        if (escapedPrefixLiteral != null) s = s.replace(ESCAPED_PREFIX, prefix);
        if (escapedSuffixLiteral != null) s = s.replace(ESCAPED_SUFFIX, suffix);
        return s;
    }

    @Override
    public String toString() {
        return MappingRule.class.getSimpleName() + "@" + prefix + "variable" + suffix;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MappingRule that = (MappingRule) obj;
        return prefix.equals(that.prefix) && suffix.equals(that.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, suffix);
    }

}
