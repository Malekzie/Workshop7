// Contributor(s): Samantha
// Main: Samantha - JPA entity for orders payments loyalty tax or Stripe idempotency.

package com.sait.peelin.model;

public enum OrderStatus {
    placed,
    pending_payment,
    paid,
    preparing,
    ready,
    scheduled,
    picked_up,
    delivered,
    completed,
    cancelled
}
