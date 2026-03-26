-- =============================================
-- V1__baseline.sql (Flyway)
-- Copy of THREADED-PROJECT/unified_postgres.sql — edit that file, then re-copy here
-- =============================================
-- Unified PostgreSQL schema for Bakery Ecommerce
-- Merges: desktop (MariaDB), mobile (SQLite), web (Postgres)
-- =============================================

-- ===================
-- EXTENSIONS
-- ===================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ===================
-- ENUM TYPES
-- ===================

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
        CREATE TYPE user_role AS ENUM ('admin', 'employee', 'customer');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'order_status') THEN
        CREATE TYPE order_status AS ENUM (
            'placed',
            'pending_payment',
            'paid',
            'preparing',
            'ready',
            'scheduled',
            'picked_up',
            'delivered',
            'completed',
            'cancelled'
        );
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'order_method') THEN
        CREATE TYPE order_method AS ENUM ('pickup', 'delivery');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'preference_type') THEN
        CREATE TYPE preference_type AS ENUM ('like', 'dislike', 'avoid', 'allergic');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'payment_status') THEN
        CREATE TYPE payment_status AS ENUM ('pending', 'authorized', 'paid', 'failed', 'refunded');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'payment_method') THEN
        CREATE TYPE payment_method AS ENUM ('cash', 'credit_card', 'debit_card', 'online');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'review_status') THEN
        CREATE TYPE review_status AS ENUM ('pending', 'approved', 'rejected');
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'bakery_status') THEN
        CREATE TYPE bakery_status AS ENUM ('open', 'closed', 'maintenance');
    END IF;
END $$;

-- ===================
-- TABLES
-- ===================

-- ---------------------
-- address
-- ---------------------
CREATE TABLE IF NOT EXISTS address (
    address_id          INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    address_line1       VARCHAR(120) NOT NULL,
    address_line2       VARCHAR(120),
    address_city        VARCHAR(120) NOT NULL,
    address_province    VARCHAR(80)  NOT NULL,
    address_postal_code VARCHAR(10)  NOT NULL
);

-- ---------------------
-- "user"
-- ---------------------
CREATE TABLE IF NOT EXISTS "user" (
    user_id            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid               UUID         DEFAULT gen_random_uuid() NOT NULL UNIQUE,
    username           VARCHAR(50)  NOT NULL UNIQUE,
    user_email         VARCHAR(254) NOT NULL UNIQUE,
    user_password_hash VARCHAR(255) NOT NULL,
    user_role          user_role    NOT NULL,
    is_active          BOOLEAN      DEFAULT TRUE NOT NULL,
    user_created_at    TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

-- ---------------------
-- reward_tier
-- ---------------------
CREATE TABLE IF NOT EXISTS reward_tier (
    reward_tier_id            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reward_tier_name          VARCHAR(30)   NOT NULL UNIQUE,
    reward_tier_min_points    INTEGER       NOT NULL,
    reward_tier_max_points    INTEGER,
    reward_tier_discount_rate NUMERIC(5, 2)
);

-- ---------------------
-- bakery
-- ---------------------
CREATE TABLE IF NOT EXISTS bakery (
    bakery_id    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    address_id   INTEGER        NOT NULL REFERENCES address,
    bakery_name  VARCHAR(100)   NOT NULL,
    bakery_phone VARCHAR(20)    NOT NULL,
    bakery_email VARCHAR(254)   NOT NULL UNIQUE,
    status       bakery_status  DEFAULT 'open' NOT NULL,
    latitude     NUMERIC(9, 6),
    longitude    NUMERIC(9, 6)
);

-- ---------------------
-- bakery_hours
-- ---------------------
CREATE TABLE IF NOT EXISTS bakery_hours (
    bakery_hours_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    bakery_id       INTEGER               NOT NULL REFERENCES bakery,
    day_of_week     SMALLINT              NOT NULL
        CONSTRAINT bakery_hours_day_of_week_check CHECK (day_of_week >= 1 AND day_of_week <= 7),
    open_time       TIME,
    close_time      TIME,
    is_closed       BOOLEAN DEFAULT FALSE NOT NULL
);

-- ---------------------
-- customer
-- ---------------------
CREATE TABLE IF NOT EXISTS customer (
    customer_id                 INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                        UUID    DEFAULT gen_random_uuid() NOT NULL UNIQUE,
    user_id                     INTEGER REFERENCES "user",
    address_id                  INTEGER NOT NULL REFERENCES address,
    reward_tier_id              INTEGER NOT NULL REFERENCES reward_tier,
    customer_first_name         VARCHAR(50)  NOT NULL,
    customer_middle_initial     VARCHAR(2),
    customer_last_name          VARCHAR(50)  NOT NULL,
    customer_phone              VARCHAR(20)  NOT NULL,
    customer_business_phone     VARCHAR(20),
    customer_email              VARCHAR(254) NOT NULL,
    customer_reward_balance     INTEGER DEFAULT 0 NOT NULL,
    customer_tier_assigned_date DATE,
    guest_expiry_date           DATE,
    profile_photo_path          VARCHAR(500),
    photo_approval_pending      BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT chk_customer_identity CHECK (user_id IS NOT NULL OR customer_email IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS idx_customer_user ON customer (user_id);
CREATE INDEX IF NOT EXISTS idx_customer_guest_expiry ON customer (guest_expiry_date) WHERE guest_expiry_date IS NOT NULL;

-- ---------------------
-- employee
-- ---------------------
CREATE TABLE IF NOT EXISTS employee (
    employee_id             INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                    UUID    DEFAULT gen_random_uuid() NOT NULL UNIQUE,
    user_id                 INTEGER NOT NULL REFERENCES "user",
    address_id              INTEGER NOT NULL REFERENCES address,
    bakery_id               INTEGER NOT NULL REFERENCES bakery,
    employee_first_name     VARCHAR(50)  NOT NULL,
    employee_middle_initial CHAR(2),
    employee_last_name      VARCHAR(50)  NOT NULL,
    employee_position       VARCHAR(40)  NOT NULL,
    employee_phone          VARCHAR(20)  NOT NULL,
    employee_business_phone VARCHAR(20),
    employee_work_email     VARCHAR(254) NOT NULL,
    profile_photo_path      VARCHAR(500),
    photo_approval_pending  BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_employee_user ON employee (user_id);

-- ---------------------
-- supplier
-- ---------------------
CREATE TABLE IF NOT EXISTS supplier (
    supplier_id    INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    address_id     INTEGER      NOT NULL REFERENCES address,
    supplier_name  VARCHAR(120) NOT NULL,
    supplier_phone VARCHAR(20),
    supplier_email VARCHAR(254)
);

-- ---------------------
-- inventory
-- ---------------------
CREATE TABLE IF NOT EXISTS inventory (
    inventory_id               INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    bakery_id                  INTEGER        NOT NULL REFERENCES bakery,
    supplier_id                INTEGER        NOT NULL REFERENCES supplier,
    inventory_item_name        VARCHAR(120)   NOT NULL,
    inventory_item_type        VARCHAR(40)    NOT NULL,
    inventory_quantity_on_hand NUMERIC(12, 3) NOT NULL,
    inventory_unit_of_measure  VARCHAR(20)    NOT NULL
);

-- ---------------------
-- tag
-- ---------------------
CREATE TABLE IF NOT EXISTS tag (
    tag_id   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tag_name VARCHAR(50) NOT NULL UNIQUE
);

-- ---------------------
-- product
-- ---------------------
CREATE TABLE IF NOT EXISTS product (
    product_id          INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_name        VARCHAR(120)   NOT NULL,
    product_description VARCHAR(1000),
    product_base_price  NUMERIC(10, 2) NOT NULL,
    product_image_url   VARCHAR(500)
);

-- ---------------------
-- product_tag
-- ---------------------
CREATE TABLE IF NOT EXISTS product_tag (
    product_id INTEGER NOT NULL REFERENCES product,
    tag_id     INTEGER NOT NULL REFERENCES tag,
    PRIMARY KEY (product_id, tag_id)
);

-- ---------------------
-- batch
-- ---------------------
CREATE TABLE IF NOT EXISTS batch (
    batch_id                INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    bakery_id               INTEGER NOT NULL REFERENCES bakery,
    product_id              INTEGER NOT NULL REFERENCES product,
    employee_id             INTEGER NOT NULL REFERENCES employee,
    batch_production_date   TIMESTAMP WITH TIME ZONE NOT NULL,
    batch_expiry_date       TIMESTAMP WITH TIME ZONE,
    batch_quantity_produced INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_batch_product ON batch (product_id);
CREATE INDEX IF NOT EXISTS idx_batch_bakery ON batch (bakery_id);

-- ---------------------
-- batch_inventory
-- ---------------------
CREATE TABLE IF NOT EXISTS batch_inventory (
    batch_id                INTEGER        NOT NULL REFERENCES batch,
    inventory_id            INTEGER        NOT NULL REFERENCES inventory,
    quantity_used           NUMERIC(12, 3) NOT NULL,
    unit_of_measure_at_time VARCHAR(20)    NOT NULL,
    usage_recorded_date     TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    PRIMARY KEY (batch_id, inventory_id)
);

-- ---------------------
-- "order"
-- ---------------------
CREATE TABLE IF NOT EXISTS "order" (
    order_id                 INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                     UUID           DEFAULT gen_random_uuid() NOT NULL UNIQUE,
    order_number             VARCHAR(20)    NOT NULL UNIQUE,
    customer_id              INTEGER        REFERENCES customer,
    bakery_id                INTEGER        NOT NULL REFERENCES bakery,
    address_id               INTEGER        REFERENCES address,
    guest_name               VARCHAR(100),
    guest_email              VARCHAR(254),
    guest_phone              VARCHAR(20),
    order_placed_datetime    TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    order_scheduled_datetime TIMESTAMP WITH TIME ZONE,
    order_delivered_datetime TIMESTAMP WITH TIME ZONE,
    order_method             order_method   NOT NULL,
    order_comment            VARCHAR(500),
    order_total              NUMERIC(10, 2) NOT NULL,
    order_discount           NUMERIC(10, 2) DEFAULT 0 NOT NULL,
    order_status             order_status   DEFAULT 'placed' NOT NULL,
    CONSTRAINT chk_order_customer_or_guest
        CHECK (customer_id IS NOT NULL OR (guest_name IS NOT NULL AND guest_email IS NOT NULL)),
    CONSTRAINT chk_order_delivery_address
        CHECK (order_method = 'pickup' OR address_id IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS idx_order_customer ON "order" (customer_id);
CREATE INDEX IF NOT EXISTS idx_order_bakery ON "order" (bakery_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON "order" (order_status);
CREATE INDEX IF NOT EXISTS idx_order_number ON "order" (order_number);

-- ---------------------
-- order_item
-- ---------------------
CREATE TABLE IF NOT EXISTS order_item (
    order_item_id                 INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id                      INTEGER        NOT NULL REFERENCES "order",
    product_id                    INTEGER        NOT NULL REFERENCES product,
    batch_id                      INTEGER        REFERENCES batch,
    order_item_quantity           INTEGER        NOT NULL,
    order_item_unit_price_at_time NUMERIC(10, 2) NOT NULL,
    order_item_line_total         NUMERIC(10, 2) NOT NULL
);

-- ---------------------
-- payment
-- ---------------------
CREATE TABLE IF NOT EXISTS payment (
    payment_id             INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                   UUID           DEFAULT gen_random_uuid() NOT NULL UNIQUE,
    order_id               INTEGER        NOT NULL REFERENCES "order",
    payment_amount         NUMERIC(10, 2) NOT NULL,
    payment_method         payment_method NOT NULL,
    payment_transaction_id VARCHAR(100),
    payment_status         payment_status DEFAULT 'pending' NOT NULL,
    payment_paid_at        TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_payment_order ON payment (order_id);

-- ---------------------
-- reward
-- ---------------------
CREATE TABLE IF NOT EXISTS reward (
    reward_id               INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                    UUID    DEFAULT gen_random_uuid() NOT NULL UNIQUE,
    customer_id             INTEGER NOT NULL REFERENCES customer,
    order_id                INTEGER NOT NULL REFERENCES "order",
    reward_points_earned    INTEGER NOT NULL,
    reward_transaction_date TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_reward_customer ON reward (customer_id);

-- ---------------------
-- review
-- ---------------------
CREATE TABLE IF NOT EXISTS review (
    review_id             INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid                  UUID          DEFAULT gen_random_uuid() NOT NULL UNIQUE,
    customer_id           INTEGER       NOT NULL REFERENCES customer,
    product_id            INTEGER       NOT NULL REFERENCES product,
    employee_id           INTEGER       REFERENCES employee,
    review_rating         SMALLINT      NOT NULL
        CONSTRAINT review_rating_check CHECK (review_rating >= 1 AND review_rating <= 5),
    review_comment        VARCHAR(2000),
    review_submitted_date TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    review_status         review_status DEFAULT 'pending' NOT NULL,
    review_approval_date  TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_review_product ON review (product_id);
CREATE INDEX IF NOT EXISTS idx_review_customer ON review (customer_id);

-- ---------------------
-- customer_preference
-- ---------------------
CREATE TABLE IF NOT EXISTS customer_preference (
    customer_id         INTEGER         NOT NULL REFERENCES customer,
    tag_id              INTEGER         NOT NULL REFERENCES tag,
    preference_type     preference_type NOT NULL,
    preference_strength SMALLINT
        CONSTRAINT customer_preference_strength_check CHECK (preference_strength >= 1 AND preference_strength <= 10),
    PRIMARY KEY (customer_id, tag_id)
);

-- ---------------------
-- message (formal notifications - per design doc)
-- ---------------------
CREATE TABLE IF NOT EXISTS message (
    message_id            INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender_id             INTEGER       NOT NULL REFERENCES "user",
    receiver_id           INTEGER       NOT NULL REFERENCES "user",
    message_subject       VARCHAR(255)  NOT NULL,
    message_content       VARCHAR(2000) NOT NULL,
    message_sent_datetime TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    message_is_read       BOOLEAN       DEFAULT FALSE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_message_sender ON message (sender_id);
CREATE INDEX IF NOT EXISTS idx_message_receiver ON message (receiver_id);

-- ---------------------
-- chat_thread (real-time chat - from mobile)
-- ---------------------
CREATE TABLE IF NOT EXISTS chat_thread (
    thread_id         INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_user_id  INTEGER NOT NULL REFERENCES "user",
    employee_user_id  INTEGER REFERENCES "user",
    status            VARCHAR(20) DEFAULT 'open' NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_chat_thread_customer ON chat_thread (customer_user_id);
CREATE INDEX IF NOT EXISTS idx_chat_thread_employee ON chat_thread (employee_user_id);

-- ---------------------
-- chat_message (real-time chat - from mobile)
-- ---------------------
CREATE TABLE IF NOT EXISTS chat_message (
    message_id     INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    thread_id      INTEGER NOT NULL REFERENCES chat_thread ON DELETE CASCADE,
    sender_user_id INTEGER NOT NULL REFERENCES "user",
    message_text   VARCHAR(2000),
    sent_at        TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    is_read        BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_chat_message_thread ON chat_message (thread_id);
CREATE INDEX IF NOT EXISTS idx_chat_message_sender ON chat_message (sender_user_id);

-- ===================
-- SEED DATA
-- ===================

-- ---------------------
-- address
-- Note: provinces corrected (Vancouver -> BC, Toronto -> ON, Montreal -> QC, Ottawa -> ON)
-- ---------------------
INSERT INTO address (address_id, address_line1, address_line2, address_city, address_province, address_postal_code)
OVERRIDING SYSTEM VALUE VALUES
    (1,  '1208 4 Ave SW',              'Suite 210',  'Calgary',    'AB', 'T2P 0H3'),
    (2,  '33 10 St NW',                NULL,         'Calgary',    'AB', 'T2N 1V4'),
    (3,  '455 7 Ave SE',               NULL,         'Calgary',    'AB', 'T2G 0J8'),
    (4,  '9805 12 Ave SW',             NULL,         'Calgary',    'AB', 'T2W 1K1'),
    (5,  '2100 16 Ave NW',             'Unit 14',    'Calgary',    'AB', 'T2M 0M5'),
    (6,  '8715 Macleod Trail SE',      'Unit 120',   'Calgary',    'AB', 'T2H 0M3'),
    (7,  '101 9 Ave SW',               'Floor 6',    'Calgary',    'AB', 'T2P 1J9'),
    (8,  '560 2 St SW',                NULL,         'Calgary',    'AB', 'T2P 0S6'),
    (9,  '1180 7 St SW',               NULL,         'Calgary',    'AB', 'T2R 1A5'),
    (10, '4020 4 St NW',               NULL,         'Calgary',    'AB', 'T2K 1A2'),
    (11, '12 100 St NW',               NULL,         'Edmonton',   'AB', 'T5J 1L6'),
    (12, '815 104 Ave NW',             'Unit 5',     'Edmonton',   'AB', 'T5H 0L1'),
    (13, '10425 Jasper Ave',           'Suite 300',  'Edmonton',   'AB', 'T5J 1Z7'),
    (14, '8900 99 St NW',              NULL,         'Edmonton',   'AB', 'T6E 3T9'),
    (15, '150 109 St NW',              'Unit 2',     'Edmonton',   'AB', 'T5J 2X6'),
    (16, '200 Granville St',           'Unit 110',   'Vancouver',  'BC', 'V6C 1S4'),
    (17, '845 Burrard St',             NULL,         'Vancouver',  'BC', 'V6Z 2K6'),
    (18, '1155 W Georgia St',          'Suite 900',  'Vancouver',  'BC', 'V6E 4T6'),
    (19, '777 Hornby St',              NULL,         'Vancouver',  'BC', 'V6Z 1S4'),
    (20, '10 King St W',               'Floor 12',   'Toronto',    'ON', 'M5H 1A1'),
    (21, '220 Bloor St W',             'Unit 7',     'Toronto',    'ON', 'M5S 1T8'),
    (22, '30 Wellington St W',         NULL,         'Toronto',    'ON', 'M5L 1E2'),
    (23, '1555 Rue Sainte-Catherine O','Suite 400',  'Montreal',   'QC', 'H3G 1P2'),
    (24, '1000 Rue De La Gauchetire O',NULL,         'Montreal',   'QC', 'H3B 4W5'),
    (25, '300 Prince of Wales Dr',     'Unit 18',    'Ottawa',     'ON', 'K2C 3T2'),
    (26, '99 Bank St',                 'Suite 500',  'Ottawa',     'ON', 'K1P 5N2'),
    (27, '75 Queen St',                NULL,         'Ottawa',     'ON', 'K1P 1N2'),
    (28, '2500 5 Ave NE',              NULL,         'Calgary',    'AB', 'T2A 6K6'),
    (29, '4400 4 Ave SE',              NULL,         'Calgary',    'AB', 'T2G 4X3'),
    (30, '601 12 Ave SW',              'Unit 3',     'Calgary',    'AB', 'T2R 1H7'),
    (31, '920 17 Ave SW',              NULL,         'Calgary',    'AB', 'T2T 0A8'),
    (32, '350 8 Ave SE',               NULL,         'Calgary',    'AB', 'T2G 0K6'),
    (33, '65 97 St NW',                NULL,         'Edmonton',   'AB', 'T5K 1L5'),
    (34, '730 7 Ave SW',               'Suite 240',  'Calgary',    'AB', 'T2P 0Z9'),
    (35, '888 3 St SW',                'Unit 105',   'Calgary',    'AB', 'T2P 5C5'),
    (36, '145 5 Ave SE',               NULL,         'Calgary',    'AB', 'T2G 2X1'),
    (37, '940 6 Ave SW',               'Suite 180',  'Calgary',    'AB', 'T2P 3T1'),
    (38, '222 4 Ave SE',               NULL,         'Calgary',    'AB', 'T2G 4X7'),
    (39, '1300 1 St SE',               'Unit 12',    'Calgary',    'AB', 'T2G 0G8'),
    (40, '410 10 St NW',               NULL,         'Calgary',    'AB', 'T2N 1V7'),
    (41, '5800 2 St SW',               'Unit 8',     'Calgary',    'AB', 'T2H 0H2');

SELECT setval(pg_get_serial_sequence('address', 'address_id'), 41);

-- ---------------------
-- reward_tier (standardized - 4 tiers from desktop with discount rates)
-- ---------------------
INSERT INTO reward_tier (reward_tier_id, reward_tier_name, reward_tier_min_points, reward_tier_max_points, reward_tier_discount_rate)
OVERRIDING SYSTEM VALUE VALUES
    (1, 'Bronze',   0,      99999,  0.00),
    (2, 'Silver',   100000, 249999, 5.00),
    (3, 'Gold',     250000, 499999, 10.00),
    (4, 'Platinum', 500000, NULL,   15.00);

SELECT setval(pg_get_serial_sequence('reward_tier', 'reward_tier_id'), 4);

-- ---------------------
-- "user"
-- BCrypt (cost 10) password hashes for seed logins (dev/demo only):
--   admin -> Admin123! | employee -> Emp123! | customer -> Cust123!
-- ---------------------
INSERT INTO "user" (user_id, username, user_email, user_password_hash, user_role, is_active, user_created_at)
OVERRIDING SYSTEM VALUE VALUES
    (1,  'alicia.nguyen',    'alicia.nguyen@northharbourmail.ca',    '$2b$10$Czhwxa6ZkyeIWilrXXTx0e5F9VM/44GvBAwsdhKqVpT1dK9d41mFq', 'admin',    TRUE, '2025-08-22 12:00:00+00'),
    (2,  'mason.clark',      'mason.clark@northharbourmail.ca',      '$2b$10$gu3mm6AREUrvkzLmFUxD0ul6C6khAJzN8a8EoWGbf6IbfXA1qmb2G', 'employee', TRUE, '2025-09-16 12:00:00+00'),
    (3,  'sophia.patel',     'sophia.patel@northharbourmail.ca',     '$2b$10$gu3mm6AREUrvkzLmFUxD0ul6C6khAJzN8a8EoWGbf6IbfXA1qmb2G', 'employee', TRUE, '2025-09-21 12:00:00+00'),
    (4,  'ethan.wright',     'ethan.wright@northharbourmail.ca',     '$2b$10$gu3mm6AREUrvkzLmFUxD0ul6C6khAJzN8a8EoWGbf6IbfXA1qmb2G', 'employee', TRUE, '2025-09-29 12:00:00+00'),
    (5,  'isabella.chen',    'isabella.chen@northharbourmail.ca',    '$2b$10$gu3mm6AREUrvkzLmFUxD0ul6C6khAJzN8a8EoWGbf6IbfXA1qmb2G', 'employee', TRUE, '2025-10-01 12:00:00+00'),
    (6,  'noah.martin',      'noah.martin@northharbourmail.ca',      '$2b$10$gu3mm6AREUrvkzLmFUxD0ul6C6khAJzN8a8EoWGbf6IbfXA1qmb2G', 'employee', TRUE, '2025-10-05 12:00:00+00'),
    (7,  'ava.roberts',      'ava.roberts@northharbourmail.ca',      '$2b$10$gu3mm6AREUrvkzLmFUxD0ul6C6khAJzN8a8EoWGbf6IbfXA1qmb2G', 'employee', TRUE, '2025-10-11 12:00:00+00'),
    (8,  'logan.scott',      'logan.scott@northharbourmail.ca',      '$2b$10$gu3mm6AREUrvkzLmFUxD0ul6C6khAJzN8a8EoWGbf6IbfXA1qmb2G', 'employee', TRUE, '2025-10-15 12:00:00+00'),
    (9,  'mia.kim',          'mia.kim@northharbourmail.ca',          '$2b$10$gu3mm6AREUrvkzLmFUxD0ul6C6khAJzN8a8EoWGbf6IbfXA1qmb2G', 'employee', TRUE, '2025-10-19 12:00:00+00'),
    (10, 'jackson.hall',     'jackson.hall@northharbourmail.ca',     '$2b$10$gu3mm6AREUrvkzLmFUxD0ul6C6khAJzN8a8EoWGbf6IbfXA1qmb2G', 'employee', TRUE, '2025-10-21 12:00:00+00'),
    (11, 'olivia.brown',     'olivia.brown@northharbourmail.ca',     '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-10-23 12:00:00+00'),
    (12, 'liam.thompson',    'liam.thompson@northharbourmail.ca',    '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-10-26 12:00:00+00'),
    (13, 'emma.wilson',      'emma.wilson@northharbourmail.ca',      '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-10-31 12:00:00+00'),
    (14, 'benjamin.lee',     'benjamin.lee@northharbourmail.ca',     '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-11-05 12:00:00+00'),
    (15, 'amelia.johnson',   'amelia.johnson@northharbourmail.ca',   '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-11-08 12:00:00+00'),
    (16, 'lucas.anderson',   'lucas.anderson@northharbourmail.ca',   '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-11-10 12:00:00+00'),
    (17, 'charlotte.miller', 'charlotte.miller@northharbourmail.ca', '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-11-12 12:00:00+00'),
    (18, 'henry.davis',      'henry.davis@northharbourmail.ca',      '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-11-15 12:00:00+00'),
    (19, 'evelyn.moore',     'evelyn.moore@northharbourmail.ca',     '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-11-20 12:00:00+00'),
    (20, 'daniel.taylor',    'daniel.taylor@northharbourmail.ca',    '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-11-22 12:00:00+00'),
    (21, 'harper.jackson',   'harper.jackson@northharbourmail.ca',   '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-11-26 12:00:00+00'),
    (22, 'sebastian.white',  'sebastian.white@northharbourmail.ca',  '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-11-28 12:00:00+00'),
    (23, 'nora.harris',      'nora.harris@northharbourmail.ca',      '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-11-30 12:00:00+00'),
    (24, 'wyatt.martinez',   'wyatt.martinez@northharbourmail.ca',   '$2b$10$F3FB0nifNo5PcRbaX4g9su/En1NAY3iDmAjOd2VEM1JMP8LqYtCw.', 'customer', TRUE, '2025-12-02 12:00:00+00');

SELECT setval(pg_get_serial_sequence('"user"', 'user_id'), 24);

-- ---------------------
-- bakery (with latitude/longitude from mobile, status column)
-- ---------------------
INSERT INTO bakery (bakery_id, address_id, bakery_name, bakery_phone, bakery_email, status, latitude, longitude)
OVERRIDING SYSTEM VALUE VALUES
    (1, 1,  'North Harbour Bakery - Downtown',         '(403) 555-2101', 'downtown@northharbourbakery.ca',  'open', 51.044700, -114.071900),
    (2, 11, 'North Harbour Bakery - Edmonton Central',  '(780) 555-4302', 'edmonton@northharbourbakery.ca', 'open', 53.546100, -113.493800),
    (3, 20, 'North Harbour Bakery - Toronto Financial', '(416) 555-9012', 'toronto@northharbourbakery.ca',  'open', 43.653200, -79.383200);

SELECT setval(pg_get_serial_sequence('bakery', 'bakery_id'), 3);

-- ---------------------
-- bakery_hours (from desktop - varied hours per location)
-- ---------------------
INSERT INTO bakery_hours (bakery_hours_id, bakery_id, day_of_week, open_time, close_time, is_closed)
OVERRIDING SYSTEM VALUE VALUES
    (1,  1, 1, '07:30', '18:00', FALSE),
    (2,  1, 2, '07:30', '18:00', FALSE),
    (3,  1, 3, '07:30', '18:00', FALSE),
    (4,  1, 4, '07:30', '18:00', FALSE),
    (5,  1, 5, '07:30', '18:00', FALSE),
    (6,  1, 6, '08:30', '16:30', FALSE),
    (7,  1, 7, NULL,    NULL,    TRUE),
    (8,  2, 1, '08:00', '17:30', FALSE),
    (9,  2, 2, '08:00', '17:30', FALSE),
    (10, 2, 3, '08:00', '17:30', FALSE),
    (11, 2, 4, '08:00', '17:30', FALSE),
    (12, 2, 5, '08:00', '17:30', FALSE),
    (13, 2, 6, '09:00', '16:00', FALSE),
    (14, 2, 7, NULL,    NULL,    TRUE),
    (15, 3, 1, '07:00', '18:30', FALSE),
    (16, 3, 2, '07:00', '18:30', FALSE),
    (17, 3, 3, '07:00', '18:30', FALSE),
    (18, 3, 4, '07:00', '18:30', FALSE),
    (19, 3, 5, '07:00', '18:30', FALSE),
    (20, 3, 6, '08:00', '17:00', FALSE),
    (21, 3, 7, '09:00', '14:00', FALSE);

SELECT setval(pg_get_serial_sequence('bakery_hours', 'bakery_hours_id'), 21);

-- ---------------------
-- tag
-- ---------------------
INSERT INTO tag (tag_id, tag_name)
OVERRIDING SYSTEM VALUE VALUES
    (1,  'Bread'),
    (2,  'Cake'),
    (3,  'Pastry'),
    (4,  'Cookie'),
    (5,  'Gluten-Free'),
    (6,  'Dairy-Free'),
    (7,  'Seasonal'),
    (8,  'Vegan'),
    (9,  'Breakfast'),
    (10, 'Dessert'),
    (11, 'Nut-Free'),
    (12, 'Whole Grain');

SELECT setval(pg_get_serial_sequence('tag', 'tag_id'), 12);

-- ---------------------
-- product (with product_image_url column, NULL for now)
-- ---------------------
INSERT INTO product (product_id, product_name, product_description, product_base_price, product_image_url)
OVERRIDING SYSTEM VALUE VALUES
    (1,  'Sourdough Loaf',            'Naturally leavened sourdough bread',                    6.49,  NULL),
    (2,  'Multigrain Sandwich Bread',  'Whole grain sandwich loaf',                            5.99,  NULL),
    (3,  'Baguette',                   'Classic French-style baguette',                        3.49,  NULL),
    (4,  'Cinnamon Roll',             'Soft roll with cinnamon filling and glaze',             4.25,  NULL),
    (5,  'Butter Croissant',          'Flaky butter croissant',                                3.95,  NULL),
    (6,  'Blueberry Muffin',          'Muffin with blueberries',                               3.25,  NULL),
    (7,  'Banana Bread Slice',        'Moist banana bread slice',                              2.95,  NULL),
    (8,  'Chocolate Chip Cookie',     'Cookie with chocolate chips',                           2.25,  NULL),
    (9,  'Oatmeal Raisin Cookie',     'Oatmeal cookie with raisins',                          2.25,  NULL),
    (10, 'Vanilla Cupcake',           'Vanilla cupcake with buttercream',                      3.50,  NULL),
    (11, 'Chocolate Cupcake',         'Chocolate cupcake with buttercream',                    3.50,  NULL),
    (12, 'Carrot Cake Slice',         'Carrot cake slice with cream cheese icing',             6.95,  NULL),
    (13, 'Chocolate Layer Cake',      'Chocolate cake with ganache',                           29.99, NULL),
    (14, 'Cheesecake Slice',          'Classic cheesecake slice',                              7.25,  NULL),
    (15, 'Apple Turnover',            'Puff pastry turnover with apple filling',               4.10,  NULL),
    (16, 'Spinach Feta Danish',       'Danish pastry with spinach and feta',                   4.75,  NULL),
    (17, 'Lemon Tart',               'Tart with lemon curd filling',                          6.50,  NULL),
    (18, 'Brownie',                   'Fudgy chocolate brownie',                               3.75,  NULL),
    (19, 'Vegan Chocolate Brownie',   'Dairy-free brownie',                                    4.25,  NULL),
    (20, 'Gluten-Free Banana Muffin', 'Gluten-free banana muffin',                            3.95,  NULL),
    (21, 'Seasonal Pumpkin Muffin',   'Pumpkin spice muffin',                                  3.75,  NULL),
    (22, 'Strawberry Shortcake Cup',  'Layered shortcake with strawberries',                   6.95,  NULL),
    (23, 'Almond Biscotti',           'Twice-baked almond biscotti',                           2.75,  NULL),
    (24, 'Whole Wheat Scone',         'Scone made with whole wheat flour',                     3.25,  NULL),
    (25, 'Raspberry Danish',          'Danish pastry with raspberry filling',                   4.75,  NULL),
    (26, 'Chocolate Eclair',          'Choux pastry with cream and chocolate topping',         5.25,  NULL);

SELECT setval(pg_get_serial_sequence('product', 'product_id'), 26);

-- ---------------------
-- product_tag
-- ---------------------
INSERT INTO product_tag (product_id, tag_id) VALUES
    (1, 1),
    (2, 1), (2, 12),
    (3, 1),
    (4, 3), (4, 9),
    (5, 3), (5, 9),
    (6, 3), (6, 9),
    (7, 9),
    (8, 4), (8, 10),
    (9, 4), (9, 10),
    (10, 2), (10, 10),
    (11, 2), (11, 10),
    (12, 2), (12, 10),
    (13, 2), (13, 10),
    (14, 2), (14, 10),
    (15, 3), (15, 10),
    (16, 3),
    (17, 10),
    (18, 10),
    (19, 6), (19, 8),
    (20, 5),
    (21, 7),
    (22, 10),
    (23, 4),
    (24, 12),
    (25, 3),
    (26, 3);

-- ---------------------
-- supplier
-- ---------------------
INSERT INTO supplier (supplier_id, address_id, supplier_name, supplier_phone, supplier_email)
OVERRIDING SYSTEM VALUE VALUES
    (1, 29, 'Prairie Wholesale Ingredients',  '(403) 555-7001', 'orders@prairiewholesale.ca'),
    (2, 30, 'Summit Packaging Supply',        '(403) 555-7002', 'support@summitpackaging.ca'),
    (3, 33, 'Riverbend Dairy Co.',            '(780) 555-7003', 'sales@riverbenddairy.ca'),
    (4, 16, 'Coastal Produce Distributors',   '(604) 555-7004', 'info@coastalproduce.ca'),
    (5, 24, 'St. Lawrence Dry Goods',         '(514) 555-7005', 'service@stlawrencedrygoods.ca');

SELECT setval(pg_get_serial_sequence('supplier', 'supplier_id'), 5);

-- ---------------------
-- inventory
-- ---------------------
INSERT INTO inventory (inventory_id, bakery_id, supplier_id, inventory_item_name, inventory_item_type, inventory_quantity_on_hand, inventory_unit_of_measure)
OVERRIDING SYSTEM VALUE VALUES
    (1,  1, 1, 'All-purpose flour',       'Ingredient', 450.000, 'kg'),
    (2,  1, 1, 'Granulated sugar',        'Ingredient', 220.000, 'kg'),
    (3,  1, 3, 'Unsalted butter',         'Ingredient', 180.000, 'kg'),
    (4,  1, 3, 'Whole milk',              'Ingredient', 600.000, 'L'),
    (5,  1, 4, 'Fresh lemons',            'Ingredient', 95.000,  'kg'),
    (6,  1, 2, 'Bakery boxes (10 inch)',  'Packaging',  800.000, 'count'),
    (7,  1, 2, 'Pastry bags',             'Packaging',  1200.000,'count'),
    (8,  1, 5, 'Baking cocoa',            'Ingredient', 80.000,  'kg'),
    (9,  2, 1, 'All-purpose flour',       'Ingredient', 380.000, 'kg'),
    (10, 2, 1, 'Granulated sugar',        'Ingredient', 210.000, 'kg'),
    (11, 2, 3, 'Unsalted butter',         'Ingredient', 165.000, 'kg'),
    (12, 2, 3, 'Whole milk',              'Ingredient', 520.000, 'L'),
    (13, 2, 4, 'Fresh berries (mixed)',   'Ingredient', 70.000,  'kg'),
    (14, 2, 2, 'Bakery boxes (10 inch)',  'Packaging',  650.000, 'count'),
    (15, 2, 2, 'Cupcake liners',          'Packaging',  5000.000,'count'),
    (16, 2, 5, 'Vanilla extract',         'Ingredient', 18.000,  'L'),
    (17, 3, 1, 'All-purpose flour',       'Ingredient', 520.000, 'kg'),
    (18, 3, 1, 'Granulated sugar',        'Ingredient', 260.000, 'kg'),
    (19, 3, 3, 'Unsalted butter',         'Ingredient', 210.000, 'kg'),
    (20, 3, 3, 'Whole milk',              'Ingredient', 720.000, 'L'),
    (21, 3, 4, 'Apples (fresh)',          'Ingredient', 120.000, 'kg'),
    (22, 3, 2, 'Bakery boxes (10 inch)',  'Packaging',  900.000, 'count'),
    (23, 3, 2, 'Paper bags',             'Packaging',  3000.000,'count'),
    (24, 3, 5, 'Baking powder',           'Ingredient', 65.000,  'kg');

SELECT setval(pg_get_serial_sequence('inventory', 'inventory_id'), 24);

-- ---------------------
-- employee (now includes bakery_id as required by design doc)
-- Employees 1-4: bakery 1 (Downtown Calgary)
-- Employees 5-7: bakery 2 (Edmonton)
-- Employees 8-9: bakery 3 (Toronto)
-- ---------------------
INSERT INTO employee (employee_id, user_id, address_id, bakery_id, employee_first_name, employee_middle_initial, employee_last_name, employee_position, employee_phone, employee_business_phone, employee_work_email)
OVERRIDING SYSTEM VALUE VALUES
    (1, 2,  2,  1, 'Mason',   NULL, 'Clark',   'Baker',            '(403) 555-3101', '(403) 555-4101', 'mason.clark@northharbourbakery.ca'),
    (2, 3,  3,  1, 'Sophia',  'R',  'Patel',   'Baker',            '(403) 555-3102', '(403) 555-4102', 'sophia.patel@northharbourbakery.ca'),
    (3, 4,  4,  1, 'Ethan',   NULL, 'Wright',  'Shift Lead',       '(403) 555-3103', '(403) 555-4103', 'ethan.wright@northharbourbakery.ca'),
    (4, 5,  5,  1, 'Isabella','M',  'Chen',    'Baker',            '(403) 555-3104', '(403) 555-4104', 'isabella.chen@northharbourbakery.ca'),
    (5, 6,  6,  2, 'Noah',    NULL, 'Martin',  'Baker',            '(403) 555-3105', '(403) 555-4105', 'noah.martin@northharbourbakery.ca'),
    (6, 7,  7,  2, 'Ava',     NULL, 'Roberts', 'Customer Support', '(403) 555-3106', '(403) 555-4106', 'ava.roberts@northharbourbakery.ca'),
    (7, 8,  8,  2, 'Logan',   'J',  'Scott',   'Quality Control',  '(403) 555-3107', '(403) 555-4107', 'logan.scott@northharbourbakery.ca'),
    (8, 9,  9,  3, 'Mia',     NULL, 'Kim',     'Baker',            '(403) 555-3108', '(403) 555-4108', 'mia.kim@northharbourbakery.ca'),
    (9, 10, 10, 3, 'Jackson', NULL, 'Hall',    'Baker',            '(403) 555-3109', '(403) 555-4109', 'jackson.hall@northharbourbakery.ca');

SELECT setval(pg_get_serial_sequence('employee', 'employee_id'), 9);

-- ---------------------
-- customer
-- ---------------------
INSERT INTO customer (customer_id, user_id, address_id, reward_tier_id, customer_first_name, customer_middle_initial, customer_last_name, customer_phone, customer_business_phone, customer_email, customer_reward_balance, customer_tier_assigned_date)
OVERRIDING SYSTEM VALUE VALUES
    (1,  11, 21, 1, 'Olivia',    NULL, 'Brown',    '(416) 555-1201', NULL, 'olivia.brown@northharbourmail.ca',     120000,  '2025-11-20'),
    (2,  12, 22, 1, 'Liam',      NULL, 'Thompson', '(416) 555-1202', NULL, 'liam.thompson@northharbourmail.ca',    240000,  '2025-11-22'),
    (3,  13, 23, 2, 'Emma',      'J',  'Wilson',   '(514) 555-1203', NULL, 'emma.wilson@northharbourmail.ca',      520000,  '2025-11-24'),
    (4,  14, 25, 1, 'Benjamin',  NULL, 'Lee',      '(613) 555-1204', NULL, 'benjamin.lee@northharbourmail.ca',     80000,   '2025-11-26'),
    (5,  15, 26, 2, 'Amelia',    NULL, 'Johnson',  '(613) 555-1205', NULL, 'amelia.johnson@northharbourmail.ca',   740000,  '2025-11-28'),
    (6,  16, 27, 1, 'Lucas',     'A',  'Anderson', '(613) 555-1206', NULL, 'lucas.anderson@northharbourmail.ca',   60000,   '2025-11-30'),
    (7,  17, 31, 1, 'Charlotte', NULL, 'Miller',   '(403) 555-1207', NULL, 'charlotte.miller@northharbourmail.ca', 210000,  '2025-12-02'),
    (8,  18, 32, 3, 'Henry',     NULL, 'Davis',    '(403) 555-1208', NULL, 'henry.davis@northharbourmail.ca',      1120000, '2025-12-04'),
    (9,  19, 34, 2, 'Evelyn',    NULL, 'Moore',    '(403) 555-1209', NULL, 'evelyn.moore@northharbourmail.ca',     680000,  '2025-12-06'),
    (10, 20, 35, 1, 'Daniel',    NULL, 'Taylor',   '(403) 555-1210', NULL, 'daniel.taylor@northharbourmail.ca',    140000,  '2025-12-08'),
    (11, 21, 36, 2, 'Harper',    NULL, 'Jackson',  '(403) 555-1211', NULL, 'harper.jackson@northharbourmail.ca',   810000,  '2025-12-10'),
    (12, 22, 37, 1, 'Sebastian', NULL, 'White',    '(403) 555-1212', NULL, 'sebastian.white@northharbourmail.ca',  95000,   '2025-12-11'),
    (13, 23, 38, 1, 'Nora',      NULL, 'Harris',   '(403) 555-1213', NULL, 'nora.harris@northharbourmail.ca',      260000,  '2025-12-12'),
    (14, 24, 39, 1, 'Wyatt',     NULL, 'Martinez', '(403) 555-1214', NULL, 'wyatt.martinez@northharbourmail.ca',   180000,  '2025-12-13');

SELECT setval(pg_get_serial_sequence('customer', 'customer_id'), 14);

-- ---------------------
-- batch
-- ---------------------
INSERT INTO batch (batch_id, bakery_id, product_id, employee_id, batch_production_date, batch_expiry_date, batch_quantity_produced)
OVERRIDING SYSTEM VALUE VALUES
    (1,  1, 1,  1, '2025-12-14 12:00:00+00', '2025-12-19 12:00:00+00', 60),
    (2,  1, 3,  2, '2025-12-17 12:00:00+00', '2025-12-22 12:00:00+00', 90),
    (3,  1, 5,  3, '2025-12-18 12:00:00+00', '2025-12-22 12:00:00+00', 120),
    (4,  1, 8,  4, '2025-12-16 12:00:00+00', '2025-12-26 12:00:00+00', 200),
    (5,  1, 13, 3, '2025-12-19 12:00:00+00', '2025-12-23 12:00:00+00', 12),
    (6,  1, 21, 2, '2025-12-20 00:00:00+00', '2025-12-25 12:00:00+00', 80),
    (7,  2, 2,  5, '2025-12-15 12:00:00+00', '2025-12-22 12:00:00+00', 55),
    (8,  2, 6,  6, '2025-12-18 12:00:00+00', '2025-12-23 12:00:00+00', 140),
    (9,  2, 10, 7, '2025-12-18 12:00:00+00', '2025-12-24 12:00:00+00', 110),
    (10, 2, 14, 8, '2025-12-19 12:00:00+00', '2025-12-23 12:00:00+00', 40),
    (11, 2, 18, 9, '2025-12-20 00:00:00+00', '2025-12-26 12:00:00+00', 90),
    (12, 3, 4,  6, '2025-12-17 12:00:00+00', '2025-12-22 12:00:00+00', 70),
    (13, 3, 7,  7, '2025-12-14 12:00:00+00', '2025-12-21 12:00:00+00', 120),
    (14, 3, 12, 8, '2025-12-18 12:00:00+00', '2025-12-25 12:00:00+00', 30),
    (15, 3, 15, 9, '2025-12-19 12:00:00+00', '2025-12-23 12:00:00+00', 75),
    (16, 3, 16, 5, '2025-12-20 00:00:00+00', '2025-12-24 12:00:00+00', 65),
    (17, 3, 17, 4, '2025-12-20 00:00:00+00', '2025-12-24 12:00:00+00', 40),
    (18, 3, 26, 2, '2025-12-19 12:00:00+00', '2025-12-22 12:00:00+00', 50);

SELECT setval(pg_get_serial_sequence('batch', 'batch_id'), 18);

-- ---------------------
-- batch_inventory
-- ---------------------
INSERT INTO batch_inventory (batch_id, inventory_id, quantity_used, unit_of_measure_at_time, usage_recorded_date) VALUES
    (1,  1,  18.500, 'kg',    '2025-12-14 12:00:00+00'),
    (1,  3,  6.000,  'kg',    '2025-12-14 12:00:00+00'),
    (2,  1,  22.000, 'kg',    '2025-12-17 12:00:00+00'),
    (2,  4,  18.000, 'L',     '2025-12-17 12:00:00+00'),
    (3,  1,  20.000, 'kg',    '2025-12-18 12:00:00+00'),
    (3,  3,  9.000,  'kg',    '2025-12-18 12:00:00+00'),
    (4,  1,  14.500, 'kg',    '2025-12-16 12:00:00+00'),
    (4,  2,  7.500,  'kg',    '2025-12-16 12:00:00+00'),
    (4,  8,  3.200,  'kg',    '2025-12-16 12:00:00+00'),
    (5,  1,  25.000, 'kg',    '2025-12-19 12:00:00+00'),
    (5,  2,  12.000, 'kg',    '2025-12-19 12:00:00+00'),
    (5,  8,  4.500,  'kg',    '2025-12-19 12:00:00+00'),
    (6,  1,  16.000, 'kg',    '2025-12-20 12:00:00+00'),
    (6,  2,  9.000,  'kg',    '2025-12-20 12:00:00+00'),
    (7,  9,  16.500, 'kg',    '2025-12-15 12:00:00+00'),
    (7,  11, 5.500,  'kg',    '2025-12-15 12:00:00+00'),
    (8,  9,  18.000, 'kg',    '2025-12-18 12:00:00+00'),
    (8,  10, 8.000,  'kg',    '2025-12-18 12:00:00+00'),
    (9,  9,  12.000, 'kg',    '2025-12-18 12:00:00+00'),
    (9,  10, 6.000,  'kg',    '2025-12-18 12:00:00+00'),
    (10, 9,  10.500, 'kg',    '2025-12-19 12:00:00+00'),
    (10, 10, 4.500,  'kg',    '2025-12-19 12:00:00+00'),
    (11, 9,  11.000, 'kg',    '2025-12-20 12:00:00+00'),
    (11, 10, 5.000,  'kg',    '2025-12-20 12:00:00+00'),
    (12, 17, 13.000, 'kg',    '2025-12-17 12:00:00+00'),
    (12, 19, 6.500,  'kg',    '2025-12-17 12:00:00+00'),
    (13, 17, 9.000,  'kg',    '2025-12-14 12:00:00+00'),
    (13, 20, 4.000,  'kg',    '2025-12-14 12:00:00+00'),
    (14, 17, 7.000,  'kg',    '2025-12-18 12:00:00+00'),
    (14, 18, 3.500,  'kg',    '2025-12-18 12:00:00+00'),
    (15, 21, 8.000,  'kg',    '2025-12-19 12:00:00+00'),
    (15, 22, 900.000,'count', '2025-12-19 12:00:00+00'),
    (16, 21, 6.500,  'kg',    '2025-12-20 12:00:00+00'),
    (16, 23, 1200.000,'count','2025-12-20 12:00:00+00'),
    (17, 20, 3.000,  'kg',    '2025-12-20 12:00:00+00'),
    (17, 21, 4.800,  'kg',    '2025-12-20 12:00:00+00'),
    (18, 17, 5.500,  'kg',    '2025-12-19 12:00:00+00'),
    (18, 18, 2.200,  'kg',    '2025-12-19 12:00:00+00');

-- ---------------------
-- "order" (with order_number and guest checkout fields from web)
-- ---------------------
INSERT INTO "order" (order_id, order_number, customer_id, bakery_id, address_id, order_placed_datetime, order_scheduled_datetime, order_delivered_datetime, order_method, order_comment, order_total, order_discount, order_status)
OVERRIDING SYSTEM VALUE VALUES
    (1,  'ORD-0001', 1,  3, 21,   '2025-12-08 12:00:00+00', '2025-12-09 12:00:00+00', '2025-12-09 12:00:00+00', 'delivery', 'Ring buzzer upon arrival',       26.95,  0.00, 'completed'),
    (2,  'ORD-0002', 2,  3, NULL, '2025-12-10 12:00:00+00', '2025-12-10 12:00:00+00', '2025-12-10 12:00:00+00', 'pickup',   NULL,                              12.98,  0.00, 'completed'),
    (3,  'ORD-0003', 3,  2, 23,   '2025-12-11 12:00:00+00', '2025-12-12 12:00:00+00', '2025-12-12 12:00:00+00', 'delivery', 'Leave with concierge',           34.20,  2.00, 'completed'),
    (4,  'ORD-0004', 4,  2, NULL, '2025-12-12 12:00:00+00', '2025-12-12 12:00:00+00', '2025-12-12 12:00:00+00', 'pickup',   NULL,                              9.75,   0.00, 'completed'),
    (5,  'ORD-0005', 5,  1, 26,   '2025-12-13 12:00:00+00', '2025-12-14 12:00:00+00', '2025-12-14 12:00:00+00', 'delivery', 'Call on arrival',                41.90,  4.00, 'completed'),
    (6,  'ORD-0006', 6,  1, NULL, '2025-12-14 12:00:00+00', '2025-12-14 12:00:00+00', '2025-12-14 12:00:00+00', 'pickup',   NULL,                              18.20,  0.00, 'completed'),
    (7,  'ORD-0007', 7,  1, 31,   '2025-12-15 12:00:00+00', '2025-12-15 12:00:00+00', NULL,                     'delivery', 'Please ensure items are sealed', 22.45,  0.00, 'scheduled'),
    (8,  'ORD-0008', 8,  1, NULL, '2025-12-15 12:00:00+00', '2025-12-15 12:00:00+00', '2025-12-15 12:00:00+00', 'pickup',   NULL,                              7.25,   0.00, 'completed'),
    (9,  'ORD-0009', 9,  3, 34,   '2025-12-16 12:00:00+00', '2025-12-17 12:00:00+00', '2025-12-17 12:00:00+00', 'delivery', NULL,                              58.48,  5.00, 'completed'),
    (10, 'ORD-0010', 10, 3, NULL, '2025-12-17 12:00:00+00', '2025-12-17 12:00:00+00', NULL,                     'pickup',   NULL,                              6.49,   0.00, 'placed'),
    (11, 'ORD-0011', 11, 2, 36,   '2025-12-17 12:00:00+00', '2025-12-18 12:00:00+00', '2025-12-18 12:00:00+00', 'delivery', 'Front desk drop-off',            27.70,  0.00, 'completed'),
    (12, 'ORD-0012', 12, 2, NULL, '2025-12-18 12:00:00+00', '2025-12-18 12:00:00+00', '2025-12-18 12:00:00+00', 'pickup',   NULL,                              14.50,  0.00, 'completed'),
    (13, 'ORD-0013', 13, 1, 38,   '2025-12-18 12:00:00+00', '2025-12-19 12:00:00+00', '2025-12-19 12:00:00+00', 'delivery', NULL,                              19.95,  0.00, 'cancelled'),
    (14, 'ORD-0014', 14, 1, NULL, '2025-12-19 12:00:00+00', '2025-12-19 12:00:00+00', NULL,                     'pickup',   NULL,                              29.99,  0.00, 'cancelled'),
    (15, 'ORD-0015', 8,  1, 41,   '2026-03-25 15:31:54+00', '2026-04-03 18:00:00+00', NULL,                     'pickup',   NULL,                              17.45,  0.00, 'ready');

SELECT setval(pg_get_serial_sequence('"order"', 'order_id'), 15);

-- ---------------------
-- order_item
-- ---------------------
INSERT INTO order_item (order_item_id, order_id, product_id, batch_id, order_item_quantity, order_item_unit_price_at_time, order_item_line_total)
OVERRIDING SYSTEM VALUE VALUES
    (1,  1,  14, 10, 1, 7.25,  7.25),
    (2,  1,  8,  4,  2, 2.25,  4.50),
    (3,  1,  5,  3,  2, 3.95,  7.90),
    (4,  2,  1,  2,  2, 6.49,  12.98),
    (5,  3,  13, 5,  1, 29.99, 29.99),
    (6,  3,  8,  4,  1, 2.25,  2.25),
    (7,  3,  21, 6,  1, 3.75,  3.75),
    (8,  4,  18, 11, 1, 3.75,  3.75),
    (9,  4,  6,  8,  2, 3.00,  6.00),
    (10, 5,  12, 14, 2, 6.95,  13.90),
    (11, 5,  26, 18, 1, 5.25,  5.25),
    (12, 5,  5,  3,  2, 3.95,  7.90),
    (13, 5,  8,  4,  1, 2.25,  2.25),
    (14, 5,  17, 17, 1, 6.50,  6.50),
    (15, 6,  15, 15, 1, 4.10,  4.10),
    (16, 6,  16, 16, 1, 4.75,  4.75),
    (17, 6,  18, 11, 1, 3.75,  3.75),
    (18, 6,  8,  4,  2, 2.25,  4.50),
    (19, 6,  6,  8,  1, 3.10,  3.10),
    (20, 7,  10, 9,  2, 3.50,  7.00),
    (21, 7,  5,  3,  1, 3.95,  3.95),
    (22, 7,  1,  2,  1, 6.49,  6.49),
    (23, 7,  8,  4,  2, 2.25,  4.50),
    (24, 8,  14, 10, 1, 7.25,  7.25),
    (25, 9,  13, 5,  1, 29.99, 29.99),
    (26, 9,  12, 14, 1, 6.95,  6.95),
    (27, 9,  17, 17, 2, 6.50,  13.00),
    (28, 9,  26, 18, 1, 5.25,  5.25),
    (29, 9,  8,  4,  2, 2.25,  4.50),
    (30, 10, 1,  2,  1, 6.49,  6.49),
    (31, 11, 4,  12, 1, 4.25,  4.25),
    (32, 11, 5,  3,  1, 3.95,  3.95),
    (33, 11, 18, 11, 2, 3.75,  7.50),
    (34, 11, 14, 10, 1, 7.25,  7.25),
    (35, 11, 8,  4,  2, 2.25,  4.50),
    (36, 12, 6,  8,  2, 3.25,  6.50),
    (37, 12, 8,  4,  2, 2.25,  4.50),
    (38, 12, 9,  4,  1, 2.25,  2.25),
    (39, 12, 24, 7,  1, 3.25,  3.25),
    (40, 13, 21, 6,  2, 3.75,  7.50),
    (41, 13, 5,  3,  1, 3.95,  3.95),
    (42, 13, 16, 16, 1, 4.75,  4.75),
    (43, 13, 8,  4,  1, 2.25,  2.25),
    (44, 13, 6,  8,  1, 1.50,  1.50),
    (45, 14, 13, 5,  1, 29.99, 29.99),
    (46, 15, 3,  NULL,5, 3.49,  17.45);

SELECT setval(pg_get_serial_sequence('order_item', 'order_item_id'), 46);

-- ---------------------
-- payment (payment_method mapped: 'Credit Card' -> 'credit_card', 'Debit' -> 'debit_card')
-- ---------------------
INSERT INTO payment (payment_id, order_id, payment_amount, payment_method, payment_transaction_id, payment_status, payment_paid_at)
OVERRIDING SYSTEM VALUE VALUES
    (1,  1,  26.95, 'credit_card', 'TRX-98314501', 'paid',       '2025-12-09 12:00:00+00'),
    (2,  2,  12.98, 'debit_card',  'TRX-98314502', 'paid',       '2025-12-10 12:00:00+00'),
    (3,  3,  32.20, 'credit_card', 'TRX-98314503', 'paid',       '2025-12-12 12:00:00+00'),
    (4,  4,  9.75,  'credit_card', 'TRX-98314504', 'paid',       '2025-12-12 12:00:00+00'),
    (5,  5,  37.90, 'credit_card', 'TRX-98314505', 'paid',       '2025-12-14 12:00:00+00'),
    (6,  6,  18.20, 'debit_card',  'TRX-98314506', 'paid',       '2025-12-14 12:00:00+00'),
    (7,  7,  22.45, 'credit_card', 'TRX-98314507', 'authorized', NULL),
    (8,  8,  7.25,  'credit_card', 'TRX-98314508', 'paid',       '2025-12-15 12:00:00+00'),
    (9,  9,  53.48, 'credit_card', 'TRX-98314509', 'paid',       '2025-12-17 12:00:00+00'),
    (10, 10, 6.49,  'debit_card',  'TRX-98314510', 'pending',    NULL),
    (11, 11, 27.70, 'credit_card', 'TRX-98314511', 'paid',       '2025-12-18 12:00:00+00'),
    (12, 12, 14.50, 'credit_card', 'TRX-98314512', 'paid',       '2025-12-18 12:00:00+00'),
    (13, 13, 19.95, 'debit_card',  'TRX-98314513', 'paid',       '2025-12-19 12:00:00+00'),
    (14, 14, 29.99, 'credit_card', 'TRX-98314514', 'pending',    NULL);

SELECT setval(pg_get_serial_sequence('payment', 'payment_id'), 14);

-- ---------------------
-- reward
-- ---------------------
INSERT INTO reward (reward_id, customer_id, order_id, reward_points_earned, reward_transaction_date)
OVERRIDING SYSTEM VALUE VALUES
    (1,  1,  1,  26950, '2025-12-09 12:00:00+00'),
    (2,  2,  2,  12980, '2025-12-10 12:00:00+00'),
    (3,  3,  3,  32200, '2025-12-12 12:00:00+00'),
    (4,  4,  4,  9750,  '2025-12-12 12:00:00+00'),
    (5,  5,  5,  37900, '2025-12-14 12:00:00+00'),
    (6,  6,  6,  18200, '2025-12-14 12:00:00+00'),
    (7,  8,  8,  7250,  '2025-12-15 12:00:00+00'),
    (8,  9,  9,  53480, '2025-12-17 12:00:00+00'),
    (9,  11, 11, 27700, '2025-12-18 12:00:00+00'),
    (10, 12, 12, 14500, '2025-12-18 12:00:00+00'),
    (11, 13, 13, 19950, '2025-12-19 12:00:00+00');

SELECT setval(pg_get_serial_sequence('reward', 'reward_id'), 11);

-- ---------------------
-- review
-- ---------------------
INSERT INTO review (review_id, customer_id, product_id, employee_id, review_rating, review_comment, review_submitted_date, review_status, review_approval_date)
OVERRIDING SYSTEM VALUE VALUES
    (1,  1,  5,  7,    5, 'Fresh and flaky, exactly what I hoped for.',     '2025-12-11 12:00:00+00', 'approved', '2025-12-12 12:00:00+00'),
    (2,  2,  1,  7,    4, 'Good loaf with a nice crust.',                  '2025-12-12 12:00:00+00', 'approved', '2025-12-13 12:00:00+00'),
    (3,  3,  13, 7,    5, 'Excellent cake, rich and not overly sweet.',     '2025-12-13 12:00:00+00', 'approved', '2025-12-14 12:00:00+00'),
    (4,  4,  6,  NULL, 4, 'Muffin was soft and well-balanced.',             '2025-12-13 12:00:00+00', 'pending',  NULL),
    (5,  5,  12, 7,    5, 'Great flavour and texture.',                     '2025-12-14 12:00:00+00', 'approved', '2025-12-15 12:00:00+00'),
    (6,  6,  16, 7,    3, 'Filling was good, pastry slightly dry.',         '2025-12-14 12:00:00+00', 'approved', '2025-12-15 12:00:00+00'),
    (7,  7,  10, NULL, 4, 'Cupcake was moist and frosting was smooth.',     '2025-12-15 12:00:00+00', 'pending',  NULL),
    (8,  8,  14, 7,    5, 'Very creamy slice and good crust.',              '2025-12-15 12:00:00+00', 'approved', '2025-12-16 12:00:00+00'),
    (9,  9,  17, 7,    4, 'Bright flavour and a nice finish.',              '2025-12-16 12:00:00+00', 'approved', '2025-12-17 12:00:00+00'),
    (10, 10, 3,  NULL, 4, 'Crisp outside and soft inside.',                 '2025-12-17 12:00:00+00', 'pending',  NULL),
    (11, 11, 18, 7,    5, 'Perfect brownie, very fudgy.',                   '2025-12-18 12:00:00+00', 'approved', '2025-12-18 12:00:00+00'),
    (12, 12, 8,  7,    4, 'Classic cookie, good texture.',                  '2025-12-18 12:00:00+00', 'approved', '2025-12-19 12:00:00+00'),
    (13, 13, 21, NULL, 4, 'Nice seasonal option, would buy again.',         '2025-12-19 12:00:00+00', 'pending',  NULL),
    (14, 14, 13, NULL, 5, 'Great for an occasion, everyone enjoyed it.',    '2025-12-19 12:00:00+00', 'pending',  NULL);

SELECT setval(pg_get_serial_sequence('review', 'review_id'), 14);

-- ---------------------
-- customer_preference
-- ---------------------
INSERT INTO customer_preference (customer_id, tag_id, preference_type, preference_strength) VALUES
    (1,  1,  'like',     7),
    (1,  10, 'like',     6),
    (2,  5,  'dislike',  8),
    (2,  9,  'like',     6),
    (3,  2,  'like',     8),
    (3,  10, 'like',     7),
    (4,  4,  'like',     6),
    (4,  11, 'allergic', 10),
    (5,  3,  'like',     7),
    (5,  7,  'like',     6),
    (6,  6,  'dislike',  8),
    (6,  8,  'like',     6),
    (7,  5,  'allergic', 10),
    (7,  9,  'like',     6),
    (8,  2,  'like',     7),
    (8,  10, 'like',     8),
    (9,  7,  'like',     7),
    (9,  9,  'like',     6),
    (10, 4,  'like',     6),
    (10, 11, 'allergic', 10),
    (11, 3,  'dislike',  4),
    (11, 9,  'like',     6),
    (12, 6,  'dislike',  8),
    (12, 10, 'like',     7),
    (13, 5,  'allergic', 10),
    (13, 7,  'like',     6),
    (14, 2,  'like',     7),
    (14, 10, 'like',     6);
