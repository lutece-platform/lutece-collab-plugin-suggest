ALTER TABLE digglike_digg ADD COLUMN show_category_block smallint default 0;
ALTER TABLE digglike_digg ADD COLUMN show_top_score_block smallint default 0;
ALTER TABLE digglike_digg ADD COLUMN show_top_comment_block smallint default 0;
ALTER TABLE digglike_digg ADD COLUMN active_digg_submit_paginator smallint default 0;
ALTER TABLE digglike_digg ADD COLUMN number_digg_submit_per_page int;
ALTER TABLE digglike_digg ADD COLUMN role varchar(50) default NULL;

ALTER TABLE digglike_vote_type ADD COLUMN template_file_name varchar(255) default NULL;

DROP TABLE IF EXISTS digglike_regular_expression;

ALTER TABLE digglike_comment_submit ADD COLUMN lutece_user_key varchar(100) default NULL;

ALTER TABLE digglike_entry_verify_by DROP FOREIGN KEY contain3_fk2;

ALTER TABLE digglike_entry_verify_by ADD CONSTRAINT fk_verify_by_entry FOREIGN KEY (id_entry) REFERENCES digglike_entry (id_entry);

DROP TABLE IF EXISTS digglike_vote;
CREATE TABLE digglike_vote (
	id_digg_submit int default 0 NOT NULL,
	lutece_user_key varchar(100) default NULL
);

DROP TABLE IF EXISTS digglike_export_format;
CREATE TABLE digglike_export_format (
	id_export int default 0 NOT NULL,
	title varchar(255) default NULL,
	description varchar(255) default NULL,
	extension varchar(255) default NULL,
	xsl_file long varbinary,
	PRIMARY KEY (id_export)
);
