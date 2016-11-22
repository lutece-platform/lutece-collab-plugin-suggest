ALTER TABLE suggest_suggest ADD COLUMN show_category_block smallint default 0;
ALTER TABLE suggest_suggest ADD COLUMN show_top_score_block smallint default 0;
ALTER TABLE suggest_suggest ADD COLUMN show_top_comment_block smallint default 0;
ALTER TABLE suggest_suggest ADD COLUMN active_suggest_submit_paginator smallint default 0;
ALTER TABLE suggest_suggest ADD COLUMN number_suggest_submit_per_page int;
ALTER TABLE suggest_suggest ADD COLUMN role varchar(50) default NULL;

ALTER TABLE suggest_vote_type ADD COLUMN template_file_name varchar(255) default NULL;

DROP TABLE IF EXISTS suggest_regular_expression;

ALTER TABLE suggest_comment_submit ADD COLUMN lutece_user_key varchar(100) default NULL;

ALTER TABLE suggest_entry_verify_by DROP FOREIGN KEY contain3_fk2;

ALTER TABLE suggest_entry_verify_by ADD CONSTRAINT fk_verify_by_entry FOREIGN KEY (id_entry) REFERENCES suggest_entry (id_entry);

DROP TABLE IF EXISTS suggest_vote;
CREATE TABLE suggest_vote (
	id_suggest_submit int default 0 NOT NULL,
	lutece_user_key varchar(100) default NULL
);

DROP TABLE IF EXISTS suggest_export_format;
CREATE TABLE suggest_export_format (
	id_export int default 0 NOT NULL,
	title varchar(255) default NULL,
	description varchar(255) default NULL,
	extension varchar(255) default NULL,
	xsl_file long varbinary,
	PRIMARY KEY (id_export)
);
