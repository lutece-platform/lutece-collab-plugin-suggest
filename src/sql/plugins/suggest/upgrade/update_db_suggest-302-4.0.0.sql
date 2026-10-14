-- liquibase formatted sql
-- changeset suggest:update_db_suggest-302-4.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'suggest_suggest' AND column_name = 'id_workflow'
--
-- Add a new colum for a consultation's workflow's ID
--
ALTER TABLE suggest_suggest ADD id_workflow INT DEFAULT NULL AFTER workgroup;