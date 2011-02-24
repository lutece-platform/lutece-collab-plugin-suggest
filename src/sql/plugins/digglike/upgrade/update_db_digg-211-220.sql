ALTER TABLE digglike_digg ADD COLUMN enable_new_digg_submit_mail smallint default 0;
ALTER TABLE digglike_digg ADD COLUMN id_mailing_list_new_digg_submit int default NULL;
-- used to have a header under the digg title--
ALTER TABLE digglike_digg ADD COLUMN header long varchar;

ALTER TABLE digglike_comment_submit ADD COLUMN official_answer smallint default 0;

INSERT INTO digglike_digg_submit_state (id_state,title,number) VALUES (2,'En attente',2);
INSERT INTO digglike_entry_type (id_type,title,class_name) VALUES (4,'Image','fr.paris.lutece.plugins.digglike.business.EntryTypeImage');

DROP TABLE IF EXISTS digglike_image;
CREATE TABLE digglike_image (
	id_digg_submit int NOT NULL,
	image_content longblob DEFAULT NULL,
	image_mime_type varchar(100) DEFAULT NULL,
	PRIMARY KEY (id_digg_submit)
);