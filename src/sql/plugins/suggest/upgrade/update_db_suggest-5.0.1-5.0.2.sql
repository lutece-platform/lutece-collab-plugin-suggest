-- liquibase formatted sql
-- changeset suggest:update_db_suggest-5.0.1-5.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name = 'suggest_suggest_submit_complement'
CREATE TABLE suggest_suggest_submit_complement (
	id_complement int AUTO_INCREMENT NOT NULL,
	id_suggest_submit int NOT NULL,
	date_complement timestamp NULL,
	complement_value long varchar,
	PRIMARY KEY (id_complement)
	);
CREATE INDEX index_suggest_submit_complement ON suggest_suggest_submit_complement (id_suggest_submit);
ALTER TABLE suggest_suggest_submit_complement ADD CONSTRAINT fk_suggest_submit_complement FOREIGN KEY (id_suggest_submit)
	REFERENCES suggest_suggest_submit (id_suggest_submit);
