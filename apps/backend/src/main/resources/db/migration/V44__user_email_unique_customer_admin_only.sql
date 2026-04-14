-- Allow the same normalized sign-in email on an employee row and a new customer row
-- (employee work email / login email matches customer registration for auto-linking).
-- Uniqueness is still enforced among customers and admins.

DROP INDEX IF EXISTS uq_user_email_normalized;

DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT c.conname AS cname
        FROM pg_constraint c
                 JOIN pg_class t ON c.conrelid = t.oid
        WHERE t.relname = 'user'
          AND c.contype = 'u'
          AND pg_get_constraintdef(c.oid) LIKE '%user_email%'
        LOOP
            EXECUTE format('ALTER TABLE "user" DROP CONSTRAINT IF EXISTS %I', r.cname);
        END LOOP;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uq_user_email_normalized_customer_admin
    ON "user" (LOWER(BTRIM(user_email)))
    WHERE user_role IN ('customer', 'admin');
