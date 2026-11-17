-- liquibase formatted sql
-- changeset suggest:update_db_suggest-5.0.2-5.0.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'suggest_suggest_submit' AND column_name = 'number_unique_view'
ALTER TABLE suggest_suggest_submit ADD COLUMN number_unique_view int DEFAULT 0 NOT NULL;

-- changeset suggest:update_db_suggest-5.0.2-5.0.3-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name = 'suggest_suggest_submit_view'
CREATE TABLE suggest_suggest_submit_view (
	id_suggest_submit int NOT NULL,
	viewer_key varchar(64) NOT NULL,
	date_view timestamp NULL,
	PRIMARY KEY (id_suggest_submit, viewer_key)
	);
