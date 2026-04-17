package com.sait.peelin.controller.v1;

import com.sait.peelin.model.StripeProcessedEvent;
import com.sait.peelin.repository.StripeProcessedEventRepository;
import com.sait.peelin.service.StripePaymentFulfillmentService;
import com.sait.peelin.service.StripePaymentFulfillmentService.PaymentIntentSnapshot;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    @Value("${stripe.publishable-key:}")
    private String publishableKey;

    private final StripePaymentFulfillmentService stripePaymentFulfillmentService;
    private final StripeProcessedEventRepository stripeProcessedEventRepository;

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> config() {
        return ResponseEntity.ok(Map.of("publishableKey", publishableKey != null ? publishableKey : ""));
    }

    @PostMapping("/webhook")
    @Transactional
    public ResponseEntity<String> handleWebhook(
            @RequestBody byte[] payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {

        final String payloadStr = new String(payload, java.nio.charset.StandardCharsets.UTF_8);
        Event event = parseEvent(payloadStr, sigHeader);
        if (event == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid webhook");
        }

        // Stripe retries on any non-2xx and may also re-deliver successfully-acked events.
        // Persisting the event id under a PK uniqueness constraint guarantees we only fulfill once.
        if (!claimEvent(event.getId())) {
            log.info("Stripe webhook event {} already processed; returning 200", event.getId());
            return ResponseEntity.ok("duplicate");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            handlePaymentIntentSucceeded(event, payloadStr);
        }

        return ResponseEntity.ok("received");
    }

    private void handlePaymentIntentSucceeded(Event event, String payloadStr) {
        PaymentIntentSnapshot snapshot = extractPaymentIntent(payloadStr);
        if (snapshot == null) {
            log.warn("payment_intent.succeeded event {}: could not read PaymentIntent payload", event.getId());
            return;
        }
        stripePaymentFulfillmentService.fulfillOrderByPaymentIntent(snapshot);
    }

    /**
     * Returns the parsed {@link Event} or {@code null} if the request must be rejected. Branches
     * are split into separate helpers so the entry-point method stays flat (CodeScene
     * "Bumpy Road").
     */
    private Event parseEvent(String payloadStr, String sigHeader) {
        if (StringUtils.hasText(webhookSecret)) {
            return parseSignedEvent(payloadStr, sigHeader);
        }
        return parseUnsignedEvent(payloadStr);
    }

    /**
     * Verifies the Stripe-Signature header against {@link #webhookSecret}. Returns {@code null}
     * when the header is missing, the signature doesn't verify, or the body fails to parse — any
     * such case lets the controller reply 400 without distinguishing them to the caller.
     */
    private Event parseSignedEvent(String payloadStr, String sigHeader) {
        if (!StringUtils.hasText(sigHeader)) {
            log.warn("Stripe webhook rejected: signature header missing");
            return null;
        }
        try {
            return Webhook.constructEvent(payloadStr, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Stripe webhook signature verification failed", e);
            return null;
        } catch (Exception e) {
            log.error("Failed to parse Stripe webhook event", e);
            return null;
        }
    }

    /**
     * Dev-only path: STRIPE_WEBHOOK_SECRET is required in prod by EnvValidator, so reaching this
     * branch means we're on a local profile without {@code stripe listen}.
     */
    private static Event parseUnsignedEvent(String payloadStr) {
        log.warn("Stripe webhook signature not verified (STRIPE_WEBHOOK_SECRET not set)");
        try {
            return Event.GSON.fromJson(payloadStr, Event.class);
        } catch (Exception e) {
            log.error("Failed to parse Stripe webhook event", e);
            return null;
        }
    }

    /**
     * Insert event id; return false if it was already there. Caller must be inside a @Transactional.
     */
    private boolean claimEvent(String eventId) {
        if (stripeProcessedEventRepository.existsById(eventId)) {
            return false;
        }
        try {
            StripeProcessedEvent row = new StripeProcessedEvent();
            row.setEventId(eventId);
            row.setProcessedAt(OffsetDateTime.now());
            stripeProcessedEventRepository.save(row);
            return true;
        } catch (DataIntegrityViolationException e) {
            // Lost a race with a concurrent delivery — treat as already processed.
            return false;
        }
    }

    /**
     * Stripe API versions newer than the bundled stripe-java model often leave
     * {@code Event.getDataObjectDeserializer().getObject()} empty; parse {@code data.object} fields
     * directly from the raw payload so we get id + amount + currency in one pass.
     */
    private static PaymentIntentSnapshot extractPaymentIntent(String payloadStr) {
        try {
            JsonObject root = JsonParser.parseString(payloadStr).getAsJsonObject();
            JsonObject data = root.getAsJsonObject("data");
            if (data == null) return null;
            JsonObject obj = data.getAsJsonObject("object");
            if (!isPaymentIntentObject(obj)) return null;
            String currency = obj.has("currency") ? obj.get("currency").getAsString() : null;
            return new PaymentIntentSnapshot(
                    obj.get("id").getAsString(),
                    obj.get("amount").getAsLong(),
                    currency);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isPaymentIntentObject(JsonObject obj) {
        if (obj == null) return false;
        if (!obj.has("object")) return false;
        if (!obj.has("id")) return false;
        if (!obj.has("amount")) return false;
        return "payment_intent".equals(obj.get("object").getAsString());
    }
}
