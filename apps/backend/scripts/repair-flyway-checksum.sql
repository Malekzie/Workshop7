-- Run once on dev-database (IntelliJ / psql) with the app stopped or after deploy.
-- Aligns flyway_schema_history with the checksum of the current V1__baseline.sql in the repo.
-- Then set spring.flyway.validate-on-migrate back to true (or remove the property) in application-prod.yaml.

UPDATE public.flyway_schema_history
SET checksum = 801152755
WHERE version = '1';
