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

-- changeset suggest:update_db_suggest-5.0.1-5.0.2-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name = 'suggest_suggest_submit_attachment'
CREATE TABLE suggest_suggest_submit_attachment (
	id_attachment int AUTO_INCREMENT NOT NULL,
	id_suggest_submit int NOT NULL,
	id_entry int NOT NULL,
	file_key varchar(255) NOT NULL,
	file_name varchar(255) NOT NULL,
	mime_type varchar(255) DEFAULT NULL,
	file_size bigint DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_attachment)
	);
CREATE INDEX index_suggest_submit_attachment ON suggest_suggest_submit_attachment (id_suggest_submit);
ALTER TABLE suggest_suggest_submit_attachment ADD CONSTRAINT fk_suggest_submit_attachment FOREIGN KEY (id_suggest_submit)
	REFERENCES suggest_suggest_submit (id_suggest_submit);

-- changeset suggest:update_db_suggest-5.0.1-5.0.2-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM suggest_entry_type WHERE id_type = 7
INSERT INTO suggest_entry_type ( id_type, title, class_name ) VALUES ( 7, 'File', 'fr.paris.lutece.plugins.suggest.business.EntryTypeFile' );

-- changeset suggest:update_db_suggest-5.0.1-5.0.2-rev3.sql dbms:mariadb,mysql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'suggest_entry_attr_additional' AND column_name = 'attr_value' AND character_maximum_length < 1000
ALTER TABLE suggest_entry_attr_additional MODIFY attr_value varchar(1000) NOT NULL;

-- changeset suggest:update_db_suggest-5.0.1-5.0.2-rev4.sql
UPDATE suggest_suggest_submit SET
  comment_number = ( SELECT COUNT(*) FROM suggest_comment_submit c WHERE c.id_suggest_submit = suggest_suggest_submit.id_suggest_submit AND ( c.official_answer = 0 OR c.id_parent_comment > 0 ) ),
  comment_enable_number = ( SELECT COUNT(*) FROM suggest_comment_submit c WHERE c.id_suggest_submit = suggest_suggest_submit.id_suggest_submit AND c.active = 1 AND ( c.official_answer = 0 OR c.id_parent_comment > 0 ) );

-- changeset suggest:update_db_suggest-5.0.1-5.0.2-rev5.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = database() AND table_name = 'suggest_vote' AND index_name = 'index_suggest_vote_submit_user'
CREATE INDEX index_suggest_vote_submit_user ON suggest_vote (id_suggest_submit, lutece_user_key);

-- changeset suggest:update_db_suggest-5.0.1-5.0.2-rev6.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = database() AND table_name = 'suggest_suggest_submit' AND index_name = 'index_suggest_suggest_submit_user'
CREATE INDEX index_suggest_suggest_submit_user ON suggest_suggest_submit (lutece_user_key, id_suggest, id_state);

-- changeset suggest:update_db_suggest-5.0.1-5.0.2-rev7.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = database() AND table_name = 'suggest_suggest_user_info' AND index_name = 'index_suggest_user_info_department'
CREATE INDEX index_suggest_user_info_department ON suggest_suggest_user_info (department, lutece_user_key);
