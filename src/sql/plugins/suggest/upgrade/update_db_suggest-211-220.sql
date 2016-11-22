ALTER TABLE suggest_suggest ADD COLUMN enable_new_suggest_submit_mail smallint default 0;
ALTER TABLE suggest_suggest ADD COLUMN id_mailing_list_new_suggest_submit int default NULL;
-- used to have a header under the suggest title--
ALTER TABLE suggest_suggest ADD COLUMN header long varchar;

ALTER TABLE suggest_comment_submit ADD COLUMN official_answer smallint default 0;

INSERT INTO suggest_suggest_submit_state (id_state,title,number) VALUES (2,'En attente',2);
INSERT INTO suggest_entry_type (id_type,title,class_name) VALUES (4,'Image','fr.paris.lutece.plugins.suggest.business.EntryTypeImage');

DROP TABLE IF EXISTS suggest_image;
CREATE TABLE suggest_image (
	id_suggest_submit int NOT NULL,
	image_content longblob DEFAULT NULL,
	image_mime_type varchar(100) DEFAULT NULL,
	PRIMARY KEY (id_suggest_submit)
);