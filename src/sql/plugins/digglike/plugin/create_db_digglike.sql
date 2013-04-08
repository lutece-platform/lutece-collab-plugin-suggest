DROP TABLE IF EXISTS digglike_vote_type_vote_button;
DROP TABLE IF EXISTS digglike_tag_submit;
DROP TABLE IF EXISTS digglike_response;
DROP TABLE IF EXISTS digglike_entry_verify_by;
DROP TABLE IF EXISTS digglike_entry;
DROP TABLE IF EXISTS digglike_digg_submit CASCADE;
DROP TABLE IF EXISTS digglike_digg_category CASCADE;
DROP TABLE IF EXISTS digglike_comment_submit CASCADE;
DROP TABLE IF EXISTS digglike_digg CASCADE;
DROP TABLE IF EXISTS digglike_vote_type CASCADE;
DROP TABLE IF EXISTS digglike_vote_button CASCADE;
DROP TABLE IF EXISTS digglike_vote CASCADE;
DROP TABLE IF EXISTS digglike_export_format CASCADE;
DROP TABLE IF EXISTS digglike_entry_type CASCADE;
DROP TABLE IF EXISTS digglike_digg_submit_state CASCADE;
DROP TABLE IF EXISTS digglike_default_message CASCADE;
DROP TABLE IF EXISTS digglike_category CASCADE;
DROP TABLE IF EXISTS digglike_action CASCADE;
DROP TABLE IF EXISTS digglike_image CASCADE;
DROP TABLE IF EXISTS digglike_digg_submit_type CASCADE;
DROP TABLE IF EXISTS digglike_rss_cf CASCADE; 
DROP TABLE IF EXISTS digglike_entry_attr_additional CASCADE;
DROP TABLE IF EXISTS digglike_video CASCADE;

--
-- Table structure for table digglike_action
--
CREATE TABLE digglike_action (
	id_action int default 0 NOT NULL,
	name_key varchar(100) default NULL,
	description_key varchar(100) default NULL,
	action_url varchar(255) default NULL,
	icon_url varchar(255) default NULL,
	action_permission varchar(255) default NULL,
	digg_state smallint default 0,
	PRIMARY KEY (id_action)
);

--
-- Table structure for table digglike_category
--
CREATE TABLE digglike_category (
	id_category int NOT NULL,
	title varchar(100) NOT NULL,
	color varchar(10),
	PRIMARY KEY (id_category)
);
--
-- Table structure for table digglike_image
--
CREATE TABLE digglike_image (
	id_digg_submit int NOT NULL,
	image_content LONG VARBINARY DEFAULT NULL,
	image_mime_type varchar(100) DEFAULT NULL,
	PRIMARY KEY (id_digg_submit)
);
--
-- Table structure for table digglike_default_message
--
CREATE TABLE digglike_default_message (
	unavailability_message long varchar,
	libelle_validate_button varchar(255),
	libelle_contribution varchar(255),
	number_digg_submit_in_top_score int,
	number_digg_submit_in_top_comment int,
	number_digg_submit_caracters_shown int
);

--
-- Table structure for table digglike_digg_submit_state
--
CREATE TABLE digglike_digg_submit_state (
	id_state smallint default 0 NOT NULL,
	title varchar(255),
	number smallint default 0 NOT NULL,
	PRIMARY KEY (id_state)
);

--
-- Table structure for table digglike_entry_type
--
CREATE TABLE digglike_entry_type (
	id_type int default 0 NOT NULL,
	title varchar(255),
	class_name varchar(255),
	PRIMARY KEY (id_type)
);

--
-- Table structure for table digglike_export_format
--
CREATE TABLE digglike_export_format (
	id_export int default 0 NOT NULL,
	title varchar(255) default NULL,
	description varchar(255) default NULL,
	extension varchar(255) default NULL,
	xsl_file long varbinary,
	PRIMARY KEY (id_export)
);

--
-- Table structure for table digglike_vote
--
CREATE TABLE digglike_vote (
	id_digg_submit int default 0 NOT NULL,
	lutece_user_key varchar(100) default NULL
);

--
-- Table structure for table digglike_vote_button
--
CREATE TABLE digglike_vote_button (
	id_vote_button int NOT NULL,
	title varchar(100) NOT NULL,
	vote_button_value int default NULL,
	icon_content long varbinary default NULL,
	icon_mime_type varchar(100) default NULL,
	PRIMARY KEY (id_vote_button)
);

--
-- Table structure for table digglike_vote_type
--
CREATE TABLE digglike_vote_type (
	id_vote_type int default 0 NOT NULL,
	title varchar(255),
	template_file_name varchar(255) default NULL,
	PRIMARY KEY (id_vote_type)
);

--
-- Table structure for table digglike_digg
--
CREATE TABLE digglike_digg (
	id_digg int default 0 NOT NULL,
	title long varchar,
	unavailability_message long varchar,
	workgroup varchar(255),
	id_vote_type int default 0 NOT NULL,
	number_vote_required int,
	number_day_required int,
	active_digg_submit_authentification smallint default 0,
	active_vote_authentification smallint default 0,
	active_comment_authentification smallint default 0,
	disable_new_digg_submit smallint default 0,
	authorized_comment smallint default 0,
	disable_new_comment smallint default 0,
	id_mailing_list_digg_submit int default NULL,
	id_mailing_list_comment int default NULL,
	active_captcha smallint default 0,
	active smallint default 0,
	date_creation timestamp NULL,
	libelle_validate_button varchar(255),
	active_digg_proposition_state smallint default 0,
	libelle_contribution varchar(255) NOT NULL,
	number_digg_submit_in_top_score int,
	number_digg_submit_in_top_comment int,
	limit_number_vote smallint default NULL,
	number_digg_submit_caracters_shown int,
	show_category_block smallint default 0,
	show_top_score_block smallint default 0,
	show_top_comment_block smallint default 0,
	active_digg_submit_paginator smallint default 0,
	number_digg_submit_per_page int,
	role varchar(50) default NULL,
	enable_new_digg_submit_mail smallint default 0,
	id_mailing_list_new_digg_submit int default NULL,
	header long varchar,
	sort_field int default 0,
	sort_asc smallint default 0,
	code_theme varchar(25)default NULL,
	confirmation_message LONG VARCHAR DEFAULT NULL,
	active_editor_bbcode smallint default 0,
	default_digg smallint default 0,
	id_default_sort int,
	active_digg_submit_type smallint default 0,
	PRIMARY KEY (id_digg)
);

CREATE INDEX index_digglike_digg ON digglike_digg (id_vote_type);

ALTER TABLE digglike_digg ADD CONSTRAINT fk_digglike_digg FOREIGN KEY (id_vote_type)
	REFERENCES digglike_vote_type (id_vote_type);

--
-- Table structure for table digglike_digg_submit
--
CREATE TABLE digglike_digg_submit (
	id_digg_submit int default 0 NOT NULL,
	id_digg int default 0 NOT NULL,
	id_state int default 0 NOT NULL,
	user_login varchar(100) default NULL,
	date_response timestamp NULL,
	vote_number int default NULL,
	score_number int default NULL,
	id_category int default NULL,
	digg_submit_value long varchar,
	digg_submit_title long varchar,
	comment_enable_number int default NULL,
	digg_submit_value_show_in_the_list long varchar,
	reported smallint default 0,
	lutece_user_key varchar(100) default NULL,
	digg_submit_list_order int default 0,
	digg_submit_type int default 0,
	number_view int default 0,
	PRIMARY KEY (id_digg_submit)
);

CREATE INDEX index_digglike_digg_submit_digg ON digglike_digg_submit (id_digg);
CREATE INDEX index_digglike_digg_submit_category ON digglike_digg_submit (id_category);

ALTER TABLE digglike_digg_submit ADD CONSTRAINT fk_digglike_digg_submit_digg FOREIGN KEY (id_digg)
	REFERENCES digglike_digg (id_digg);
ALTER TABLE digglike_digg_submit ADD CONSTRAINT fk_digglike_digg_submit_category FOREIGN KEY (id_category)
	REFERENCES digglike_category (id_category);

--
-- Table structure for table digglike_comment_submit
--
CREATE TABLE digglike_comment_submit (
	id_comment_submit int default 0 NOT NULL,
	id_digg_submit int default NULL,
	id_parent_comment int default 0,
	date_comment timestamp NULL,
	comment_value long varchar,
	active smallint default 0,
	lutece_user_key varchar(100) default NULL,
	official_answer smallint default 0,
	date_modify timestamp NULL,
	PRIMARY KEY (id_comment_submit)
);

CREATE INDEX index_digglike_comment_submit ON digglike_comment_submit (id_digg_submit);

ALTER TABLE digglike_comment_submit ADD CONSTRAINT fk_digglike_comment_submit FOREIGN KEY (id_digg_submit)
	REFERENCES digglike_digg_submit (id_digg_submit);

--
-- Table structure for table digglike_digg_category
--
CREATE TABLE digglike_digg_category (
	id_digg int default 0 NOT NULL,
	id_category int default 0 NOT NULL,
	PRIMARY KEY (id_digg,id_category)
);

CREATE INDEX index_digglike_digg_category_digg ON digglike_digg_category (id_digg);
CREATE INDEX index_digglike_digg_category_category ON digglike_digg_category (id_category);

ALTER TABLE digglike_digg_category ADD CONSTRAINT fk_digglike_digg_category_digg FOREIGN KEY (id_digg)
	REFERENCES digglike_digg (id_digg);
ALTER TABLE digglike_digg_category ADD CONSTRAINT fk_digglike_digg_category_category FOREIGN KEY (id_category)
	REFERENCES digglike_category (id_category);

--
-- Table structure for table digglike_entry
--
CREATE TABLE digglike_entry (
	id_entry int default 0 NOT NULL,
	id_digg int default 0 NOT NULL,
	id_type int default 0 NOT NULL,
	title long varchar,
	help_message long varchar,
	entry_comment long varchar,
	mandatory smallint default 0,
	pos int default NULL,
	default_value long varchar,
	height int default NULL,
	width int default NULL,
	max_size_enter int default NULL,
	show_in_digg_submit_list smallint default 0,
	PRIMARY KEY (id_entry)
);

CREATE INDEX index_digglike_entry_digg ON digglike_entry (id_digg);
CREATE INDEX index_digglike_entry_type ON digglike_entry (id_type);

ALTER TABLE digglike_entry ADD CONSTRAINT fk_digglike_entry_digg FOREIGN KEY (id_digg)
	REFERENCES digglike_digg (id_digg);
ALTER TABLE digglike_entry ADD CONSTRAINT fk_digglike_entry_type FOREIGN KEY (id_type)
	REFERENCES digglike_entry_type (id_type);

--
-- Table structure for table digglike_entry_verify_by
--
CREATE TABLE digglike_entry_verify_by (
	id_entry int default 0 NOT NULL,
	id_expression int default 0 NOT NULL,
	PRIMARY KEY (id_entry,id_expression)
);

CREATE INDEX index_digglike_verify_by_entry ON digglike_entry_verify_by (id_entry);

ALTER TABLE digglike_entry_verify_by ADD CONSTRAINT fk_digglike_verify_by_entry FOREIGN KEY (id_entry)
	REFERENCES digglike_entry (id_entry);

--
-- Table structure for table digglike_response
--
CREATE TABLE digglike_response (
	id_response int default 0 NOT NULL,
	id_digg_submit int default NULL,
	response_value long varchar,
	id_entry int default NULL,
	PRIMARY KEY (id_response)
);

CREATE INDEX index_digglike_response_entry ON digglike_response (id_entry);
CREATE INDEX index_digglike_response_digg ON digglike_response (id_digg_submit);

ALTER TABLE digglike_response ADD CONSTRAINT fk_digglike_response_entry FOREIGN KEY (id_entry)
	REFERENCES digglike_entry (id_entry);
ALTER TABLE digglike_response ADD CONSTRAINT fk_digglike_response_digg FOREIGN KEY (id_digg_submit)
	REFERENCES digglike_digg_submit (id_digg_submit);

--
-- Table structure for table digglike_tag_submit
--
CREATE TABLE digglike_tag_submit (
	id_tag_submit int default 0 NOT NULL,
	id_digg_submit int default 0 NOT NULL,
	tag_value long varchar,
	PRIMARY KEY (id_tag_submit)
);

CREATE INDEX index_digglike_tag_submit ON digglike_tag_submit (id_digg_submit);

ALTER TABLE digglike_tag_submit ADD CONSTRAINT fk_digglike_tag_submit FOREIGN KEY (id_digg_submit)
	REFERENCES digglike_digg_submit (id_digg_submit);

--
-- Table structure for table digglike_vote_type_vote_button
--
CREATE TABLE digglike_vote_type_vote_button (
	id_vote_type int default 0 NOT NULL,
	id_vote_button int default 0 NOT NULL,
	vote_button_order int default NULL,
	PRIMARY KEY (id_vote_type,id_vote_button)
);

CREATE INDEX index_digglike_vote_type ON digglike_vote_type_vote_button (id_vote_type);
CREATE INDEX index_digglike_vote_button ON digglike_vote_type_vote_button (id_vote_button);

ALTER TABLE digglike_vote_type_vote_button ADD CONSTRAINT fk_digglike_vote_type FOREIGN KEY (id_vote_type)
	REFERENCES digglike_vote_type (id_vote_type);
ALTER TABLE digglike_vote_type_vote_button ADD CONSTRAINT fk_digglike_vote_button FOREIGN KEY (id_vote_button)
	REFERENCES digglike_vote_button (id_vote_button);

--
-- Table structure for table digglike_rss_cf
--
CREATE TABLE digglike_rss_cf (
	id_rss int default 0 NOT NULL,
	id_digg int default 0 NOT NULL,
	is_submit_rss smallint default 0 NOT NULL,	
	id_digg_submit int default 0 NOT NULL,
	PRIMARY KEY (id_rss)
);

--
-- Table structure for table digglike_digg_submit_type
--
CREATE TABLE digglike_digg_submit_type (
	id_type int default 0 NOT NULL,
	name long varchar,
	color varchar(10),
	image_content LONG VARBINARY DEFAULT NULL,
	image_mime_type varchar(100) DEFAULT NULL,
	parameterizable smallint default 0,
	id_xsl int default 0 NOT NULL,
	image_url varchar(100) DEFAULT NULL,
	PRIMARY KEY (id_type)
);
	
	
--
-- Table structure for table digglike_entry_attr_additional
--
CREATE TABLE digglike_entry_attr_additional (
	id_entry int default 0 NOT NULL,
	attr_name varchar(100) NOT NULL,
	attr_value varchar(100) NOT NULL
);

--
-- Table structure for table digglike_video
--
DROP TABLE IF EXISTS digglike_video;
CREATE TABLE digglike_video (
	id_digg_submit int NOT NULL,
	video_content long varbinary DEFAULT NULL,
	video_mime_type varchar(100) DEFAULT NULL,
	credits varchar(100) DEFAULT NULL,
	PRIMARY KEY (id_digg_submit)
);