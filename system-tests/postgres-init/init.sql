-- Create schemas for each connector
CREATE SCHEMA IF NOT EXISTS provider;
CREATE SCHEMA IF NOT EXISTS consumer;
CREATE SCHEMA IF NOT EXISTS company3;
CREATE SCHEMA IF NOT EXISTS federated_catalog_consumer;

-- Grant all privileges on schemas to user
GRANT ALL ON SCHEMA provider TO "user";
GRANT ALL ON SCHEMA consumer TO "user";
GRANT ALL ON SCHEMA company3 TO "user";
GRANT ALL ON SCHEMA federated_catalog_consumer TO "user";

-- Grant all privileges on all tables in schemas (for future tables)
ALTER DEFAULT PRIVILEGES IN SCHEMA provider GRANT ALL ON TABLES TO "user";
ALTER DEFAULT PRIVILEGES IN SCHEMA consumer GRANT ALL ON TABLES TO "user";
ALTER DEFAULT PRIVILEGES IN SCHEMA company3 GRANT ALL ON TABLES TO "user";
ALTER DEFAULT PRIVILEGES IN SCHEMA federated_catalog_consumer GRANT ALL ON TABLES TO "user";

-- Grant all privileges on all sequences in schemas (for future sequences)
ALTER DEFAULT PRIVILEGES IN SCHEMA provider GRANT ALL ON SEQUENCES TO "user";
ALTER DEFAULT PRIVILEGES IN SCHEMA consumer GRANT ALL ON SEQUENCES TO "user";
ALTER DEFAULT PRIVILEGES IN SCHEMA company3 GRANT ALL ON SEQUENCES TO "user";
ALTER DEFAULT PRIVILEGES IN SCHEMA federated_catalog_consumer GRANT ALL ON SEQUENCES TO "user";