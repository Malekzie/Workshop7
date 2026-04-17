package com.sait.peelin.config;

import io.sentry.Breadcrumb;
import io.sentry.EventProcessor;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.protocol.Message;
import io.sentry.protocol.Request;
import io.sentry.protocol.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Defense-in-depth for Sentry events: even with send-default-pii=false, custom code may still
 * attach request data / user email. Strip the highest-risk fields before transmission.
 *
 * <p>Logback's Sentry appender funnels {@code log.error}/{@code log.warn} through the same
 * {@link EventProcessor} pipeline as manual {@code Sentry.captureException} calls — so scrubbing
 * {@link SentryEvent#getMessage()} and {@link SentryEvent#getBreadcrumbs()} here covers both
 * paths without needing a separate Logback filter (B6).
 */
@Configuration
public class SentryScrubbingConfig {

    private static final String REDACTED = "[redacted]";

    private static final Pattern SENSITIVE_KEY = Pattern.compile(
            "(?i)^(authorization|cookie|set-cookie|x-api-key|x-auth-token|token|access_token|refresh_token|password|secret|stripe-signature)$");

    private static final Pattern SENSITIVE_QUERY_KEY = Pattern.compile(
            "(?i)(^|&)(token|access_token|refresh_token|code|state|id_token|password)=[^&]*");

    // Patterns applied to free-form log/breadcrumb message text.
    // Order matters: longer / more specific patterns run first so they aren't shadowed by the
    // looser email/UUID matchers.
    private static final List<Pattern> MESSAGE_REDACTORS = List.of(
            // JWTs (3 base64url segments)
            Pattern.compile("eyJ[A-Za-z0-9_-]+=*\\.[A-Za-z0-9._-]+=*\\.[A-Za-z0-9._-]+=*"),
            // Bearer / Basic auth values
            Pattern.compile("(?i)(bearer|basic)\\s+[A-Za-z0-9._\\-+/=]+"),
            // Generic 32+ char hex/base64 secrets (API keys, password reset tokens)
            Pattern.compile("(?<![A-Za-z0-9])[A-Za-z0-9_-]{32,}(?![A-Za-z0-9])"),
            // Email addresses
            Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"),
            // Credit-card-shaped sequences (13–19 digits with optional separators)
            Pattern.compile("(?<!\\d)(?:\\d[ -]?){13,19}(?!\\d)")
    );

    @Bean
    public EventProcessor sentryScrubbingProcessor() {
        return new EventProcessor() {
            @Override
            public SentryEvent process(SentryEvent event, Hint hint) {
                scrubRequest(event.getRequest());
                scrubUser(event.getUser());
                scrubBreadcrumbs(event.getBreadcrumbs());
                scrubLogMessage(event);
                return event;
            }
        };
    }

    /** Strip secrets from the formatted log line + breadcrumb captions that Logback ships. */
    private static void scrubLogMessage(SentryEvent event) {
        Message message = event.getMessage();
        if (message != null) {
            message.setFormatted(redactMessageText(message.getFormatted()));
            message.setMessage(redactMessageText(message.getMessage()));
            List<String> params = message.getParams();
            if (params != null && !params.isEmpty()) {
                List<String> scrubbed = new ArrayList<>(params.size());
                for (String p : params) scrubbed.add(redactMessageText(p));
                message.setParams(scrubbed);
            }
        }
    }

    static String redactMessageText(String value) {
        if (value == null || value.isEmpty()) return value;
        String out = value;
        for (Pattern p : MESSAGE_REDACTORS) {
            out = p.matcher(out).replaceAll(REDACTED);
        }
        return out;
    }

    private static void scrubRequest(Request request) {
        if (request == null) return;
        request.setCookies(null);
        scrubHeaders(request.getHeaders());
        request.setQueryString(redactQueryParams(request.getQueryString()));
        request.setUrl(redactQueryParams(request.getUrl()));
    }

    private static void scrubHeaders(Map<String, String> headers) {
        if (headers == null) return;
        headers.replaceAll((k, v) -> SENSITIVE_KEY.matcher(k).matches() ? REDACTED : v);
    }

    private static String redactQueryParams(String value) {
        if (value == null || value.isEmpty()) return value;
        return SENSITIVE_QUERY_KEY.matcher(value).replaceAll("$1$2=" + REDACTED);
    }

    private static void scrubUser(User user) {
        if (user == null) return;
        user.setIpAddress(null);
        user.setEmail(null);
    }

    // Sentry SDK drops most breadcrumb data already; this is belt-and-suspenders on custom fields.
    private static void scrubBreadcrumbs(List<Breadcrumb> breadcrumbs) {
        if (breadcrumbs == null) return;
        breadcrumbs.forEach(SentryScrubbingConfig::scrubBreadcrumbData);
    }

    private static void scrubBreadcrumbData(Breadcrumb breadcrumb) {
        breadcrumb.setMessage(redactMessageText(breadcrumb.getMessage()));
        Map<String, Object> data = breadcrumb.getData();
        if (data == null) return;
        data.replaceAll((k, v) -> {
            if (SENSITIVE_KEY.matcher(k.toLowerCase(Locale.ROOT)).matches()) return REDACTED;
            if (v instanceof String s) return redactMessageText(s);
            return v;
        });
    }
}
