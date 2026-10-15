-- liquibase formatted sql
-- changeset suggest:update_db_suggest-5.0.0-5.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'suggest_vote' AND column_name = 'vote_value'
ALTER TABLE suggest_vote ADD COLUMN vote_value int DEFAULT NULL;
