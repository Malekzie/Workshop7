-- Tag internal staff-only system messages so customer-facing REST/WS excludes them.
ALTER TABLE chat_message
    ADD COLUMN is_staff_only BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_chat_message_thread_staff_only
    ON chat_message (thread_id, is_staff_only);
