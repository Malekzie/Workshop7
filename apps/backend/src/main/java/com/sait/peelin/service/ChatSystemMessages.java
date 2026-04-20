package com.sait.peelin.service;

/**
 * Canonical copy for system/audit messages posted into chat threads.
 * Kept in one place so operator-facing phrasing can be tuned without touching business logic.
 */
final class ChatSystemMessages {

    private ChatSystemMessages() {}

    static final String NO_STAFF_ONLINE =
            "No staff online right now, we'll respond as soon as possible.";

    static final String CUSTOMER_TRANSFER_NOTICE =
            "You're being connected with another team member. They'll be right with you.";

    static String assignedTo(String staffName) {
        return "Assigned to " + staffName;
    }

    static String transferredBetween(String fromName, String toName) {
        return "Transferred from " + fromName + " to " + toName;
    }

    static String reopenedForAudit(String actorName, String previousAssigneeName) {
        String suffix = previousAssigneeName != null
                ? " (previously handled by " + previousAssigneeName + ")"
                : "";
        return "Reopened for audit by " + actorName + suffix;
    }
}
