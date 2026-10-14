-- liquibase formatted sql
-- changeset suggest:update_db_suggest-227-228.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name = 'suggest_tag_submit'
DROP TABLE suggest_tag_submit;