DROP TABLE IF EXISTS suggest_vote_type_vote_button;
DROP TABLE IF EXISTS suggest_tag_submit;
DROP TABLE IF EXISTS suggest_response;
DROP TABLE IF EXISTS suggest_entry_verify_by;
DROP TABLE IF EXISTS suggest_entry;
DROP TABLE IF EXISTS suggest_suggest_submit CASCADE;
DROP TABLE IF EXISTS suggest_suggest_category CASCADE;
DROP TABLE IF EXISTS suggest_comment_submit CASCADE;
DROP TABLE IF EXISTS suggest_suggest CASCADE;
DROP TABLE IF EXISTS suggest_vote_type CASCADE;
DROP TABLE IF EXISTS suggest_vote_button CASCADE;
DROP TABLE IF EXISTS suggest_vote CASCADE;
DROP TABLE IF EXISTS suggest_export_format CASCADE;
DROP TABLE IF EXISTS suggest_entry_type CASCADE;
DROP TABLE IF EXISTS suggest_suggest_submit_state CASCADE;
DROP TABLE IF EXISTS suggest_default_message CASCADE;
DROP TABLE IF EXISTS suggest_category CASCADE;
DROP TABLE IF EXISTS suggest_action CASCADE;
DROP TABLE IF EXISTS suggest_image CASCADE;
DROP TABLE IF EXISTS suggest_suggest_submit_type CASCADE;
DROP TABLE IF EXISTS suggest_suggest_suggest_submit_type CASCADE;
DROP TABLE IF EXISTS suggest_rss_cf CASCADE; 
DROP TABLE IF EXISTS suggest_entry_attr_additional CASCADE;
DROP TABLE IF EXISTS suggest_video CASCADE;
DROP TABLE IF EXISTS suggest_suggest_user_info;
DROP TABLE IF EXISTS suggest_suggest_attribute CASCADE;
DROP TABLE IF EXISTS suggest_reported_message CASCADE;
--
-- Table structure for table suggest_action
--
CREATE TABLE suggest_action (
	id_action int default 0 NOT NULL,
	name_key varchar(100) default NULL,
	description_key varchar(100) default NULL,
	action_url varchar(255) default NULL,
	icon_url varchar(255) default NULL,
	action_permission varchar(255) default NULL,
	suggest_state smallint default 0,
	PRIMARY KEY (id_action)
);

--
-- Table structure for table suggest_category
--
CREATE TABLE suggest_category (
	id_category int NOT NULL,
	title varchar(100) NOT NULL,
	color varchar(10),
	PRIMARY KEY (id_category)
);
--
-- Table structure for table suggest_image
--
CREATE TABLE suggest_image (
	id_resource_image  int NOT NULL,
	image_content LONG VARBINARY DEFAULT NULL,
	image_mime_type varchar(100) DEFAULT NULL,
	PRIMARY KEY (id_resource_image)
);
--
-- Table structure for table suggest_default_message
--
CREATE TABLE suggest_default_message (
	unavailability_message long varchar,
	libelle_validate_button varchar(255),
	libelle_contribution varchar(255),
	number_suggest_submit_in_top_score int,
	number_suggest_submit_in_top_comment int,
	number_suggest_submit_caracters_shown int,
	notification_new_comment_title long varchar,
	notification_new_comment_body long varchar,
	notification_new_suggest_submit_title long varchar,
	notification_new_suggest_submit_body long varchar
);

--
-- Table structure for table suggest_suggest_submit_state
--
CREATE TABLE suggest_suggest_submit_state (
	id_state smallint default 0 NOT NULL,
	title varchar(255),
	number smallint default 0 NOT NULL,
	PRIMARY KEY (id_state)
);

--
-- Table structure for table suggest_entry_type
--
CREATE TABLE suggest_entry_type (
	id_type int default 0 NOT NULL,
	title varchar(255),
	class_name varchar(255),
	PRIMARY KEY (id_type)
);

--
-- Table structure for table suggest_export_format
--
CREATE TABLE suggest_export_format (
	id_export int default 0 NOT NULL,
	title varchar(255) default NULL,
	description varchar(255) default NULL,
	extension varchar(255) default NULL,
	xsl_file long varbinary,
	PRIMARY KEY (id_export)
);

--
-- Table structure for table suggest_vote
--
CREATE TABLE suggest_vote (
	id_suggest_submit int default 0 NOT NULL,
	lutece_user_key varchar(100) default NULL
);

--
-- Table structure for table suggest_vote_button
--
CREATE TABLE suggest_vote_button (
	id_vote_button int NOT NULL,
	title varchar(100) NOT NULL,
	vote_button_value int default NULL,
	icon_content long varbinary default NULL,
	icon_mime_type varchar(100) default NULL,
	PRIMARY KEY (id_vote_button)
);

--
-- Table structure for table suggest_vote_type
--
CREATE TABLE suggest_vote_type (
	id_vote_type int default 0 NOT NULL,
	title varchar(255),
	template_file_name varchar(255) default NULL,
	PRIMARY KEY (id_vote_type)
);

--
-- Table structure for table suggest_suggest
--
CREATE TABLE suggest_suggest (
	id_suggest int default 0 NOT NULL,
	title long varchar,
	unavailability_message long varchar,
	workgroup varchar(255),
	id_vote_type int default 0 NOT NULL,
	number_vote_required int,
	number_day_required int,
	active_suggest_submit_authentification smallint default 0,
	active_vote_authentification smallint default 0,
	active_comment_authentification smallint default 0,
	disable_new_suggest_submit smallint default 0,
	authorized_comment smallint default 0,
	disable_new_comment smallint default 0,
	id_mailing_list_suggest_submit int default NULL,
	active_captcha smallint default 0,
	active smallint default 0,
	date_creation timestamp NULL,
	libelle_validate_button varchar(255),
	active_suggest_proposition_state smallint default 0,
	libelle_contribution varchar(255) NOT NULL,
	number_suggest_submit_in_top_score int,
	number_suggest_submit_in_top_comment int,
	limit_number_vote smallint default NULL,
	number_suggest_submit_caracters_shown int,
	show_category_block smallint default 0,
	show_top_score_block smallint default 0,
	show_top_comment_block smallint default 0,
	active_suggest_submit_paginator smallint default 0,
	number_suggest_submit_per_page int,
	role varchar(50) default NULL,
	enable_new_suggest_submit_mail smallint default 0,
	header long varchar,
	sort_field int default 0,
	sort_asc smallint default 0,
	code_theme varchar(25)default NULL,
	confirmation_message LONG VARCHAR DEFAULT NULL,
	active_editor_bbcode smallint default 0,
	default_suggest smallint default 0,
	id_default_sort int,
	notification_new_comment_sender varchar(255),
	notification_new_comment_title long varchar,
	notification_new_comment_body long varchar,
	notification_new_suggest_submit_sender varchar(255),
	notification_new_suggest_submit_title long varchar,
	notification_new_suggest_submit_body long varchar,
	PRIMARY KEY (id_suggest)
);

CREATE INDEX index_suggest_suggest ON suggest_suggest (id_vote_type);

ALTER TABLE suggest_suggest ADD CONSTRAINT fk_suggest_suggest FOREIGN KEY (id_vote_type)
	REFERENCES suggest_vote_type (id_vote_type);

--
-- Table structure for table suggest_suggest_submit
--
CREATE TABLE suggest_suggest_submit (
	id_suggest_submit int default 0 NOT NULL,
	id_suggest int default 0 NOT NULL,
	id_state int default 0 NOT NULL,
	user_login varchar(100) default NULL,
	date_response timestamp NULL,
	vote_number int default NULL,
	score_number int default NULL,
	id_category int default NULL,
	suggest_submit_value long varchar,
	suggest_submit_title long varchar,
	comment_enable_number int default NULL,
	suggest_submit_value_show_in_the_list long varchar,
	reported smallint default 0,
	lutece_user_key varchar(100) default NULL,
	suggest_submit_list_order int default 0,
	suggest_submit_type int default 0,
	number_view int default 0,
	disable_vote smallint default 0,
	is_pinned smallint default 0,
	disable_comment smallint default 0,
	id_image_resource int default NULL,
	comment_number int default 0,
	PRIMARY KEY (id_suggest_submit)
);

CREATE INDEX index_suggest_suggest_submit_suggest ON suggest_suggest_submit (id_suggest);
CREATE INDEX index_suggest_suggest_submit_category ON suggest_suggest_submit (id_category);

ALTER TABLE suggest_suggest_submit ADD CONSTRAINT fk_suggest_suggest_submit_suggest FOREIGN KEY (id_suggest)
	REFERENCES suggest_suggest (id_suggest);
ALTER TABLE suggest_suggest_submit ADD CONSTRAINT fk_suggest_suggest_submit_category FOREIGN KEY (id_category)
	REFERENCES suggest_category (id_category);

--
-- Table structure for table suggest_comment_submit
--
CREATE TABLE suggest_comment_submit (
	id_comment_submit int default 0 NOT NULL,
	id_suggest_submit int default NULL,
	id_parent_comment int default 0,
	date_comment timestamp NULL,
	comment_value long varchar,
	active smallint default 0,
	lutece_user_key varchar(100) default NULL,
	official_answer smallint default 0,
	date_modify timestamp NULL,
	PRIMARY KEY (id_comment_submit)
);

CREATE INDEX index_suggest_comment_submit ON suggest_comment_submit (id_suggest_submit);
CREATE INDEX index_suggest_id_parent_comment ON suggest_comment_submit (id_parent_comment);


ALTER TABLE suggest_comment_submit ADD CONSTRAINT fk_suggest_comment_submit FOREIGN KEY (id_suggest_submit)
	REFERENCES suggest_suggest_submit (id_suggest_submit);

--
-- Table structure for table suggest_suggest_category
--
CREATE TABLE suggest_suggest_category (
	id_suggest int default 0 NOT NULL,
	id_category int default 0 NOT NULL,
	PRIMARY KEY (id_suggest,id_category)
);

CREATE INDEX index_suggest_suggest_category_suggest ON suggest_suggest_category (id_suggest);
CREATE INDEX index_suggest_suggest_category_category ON suggest_suggest_category (id_category);

ALTER TABLE suggest_suggest_category ADD CONSTRAINT fk_suggest_suggest_category_suggest FOREIGN KEY (id_suggest)
	REFERENCES suggest_suggest (id_suggest);
ALTER TABLE suggest_suggest_category ADD CONSTRAINT fk_suggest_suggest_category_category FOREIGN KEY (id_category)
	REFERENCES suggest_category (id_category);

--
-- Table structure for table suggest_entry
--
CREATE TABLE suggest_entry (
	id_entry int default 0 NOT NULL,
	id_suggest int default 0 NOT NULL,
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
	show_in_suggest_submit_list smallint default 0,
	PRIMARY KEY (id_entry)
);

CREATE INDEX index_suggest_entry_suggest ON suggest_entry (id_suggest);
CREATE INDEX index_suggest_entry_type ON suggest_entry (id_type);

ALTER TABLE suggest_entry ADD CONSTRAINT fk_suggest_entry_suggest FOREIGN KEY (id_suggest)
	REFERENCES suggest_suggest (id_suggest);
ALTER TABLE suggest_entry ADD CONSTRAINT fk_suggest_entry_type FOREIGN KEY (id_type)
	REFERENCES suggest_entry_type (id_type);

--
-- Table structure for table suggest_entry_verify_by
--
CREATE TABLE suggest_entry_verify_by (
	id_entry int default 0 NOT NULL,
	id_expression int default 0 NOT NULL,
	PRIMARY KEY (id_entry,id_expression)
);

CREATE INDEX index_suggest_verify_by_entry ON suggest_entry_verify_by (id_entry);

ALTER TABLE suggest_entry_verify_by ADD CONSTRAINT fk_suggest_verify_by_entry FOREIGN KEY (id_entry)
	REFERENCES suggest_entry (id_entry);

--
-- Table structure for table suggest_response
--
CREATE TABLE suggest_response (
	id_response int default 0 NOT NULL,
	id_suggest_submit int default NULL,
	response_value long varchar,
	id_entry int default NULL,
	id_resource_image int DEFAULT NULL,
	PRIMARY KEY (id_response)
);

CREATE INDEX index_suggest_response_entry ON suggest_response (id_entry);
CREATE INDEX index_suggest_response_suggest ON suggest_response (id_suggest_submit);

ALTER TABLE suggest_response ADD CONSTRAINT fk_suggest_response_entry FOREIGN KEY (id_entry)
	REFERENCES suggest_entry (id_entry);
ALTER TABLE suggest_response ADD CONSTRAINT fk_suggest_response_suggest FOREIGN KEY (id_suggest_submit)
	REFERENCES suggest_suggest_submit (id_suggest_submit);

--
-- Table structure for table suggest_vote_type_vote_button
--
CREATE TABLE suggest_vote_type_vote_button (
	id_vote_type int default 0 NOT NULL,
	id_vote_button int default 0 NOT NULL,
	vote_button_order int default NULL,
	PRIMARY KEY (id_vote_type,id_vote_button)
);

CREATE INDEX index_suggest_vote_type ON suggest_vote_type_vote_button (id_vote_type);
CREATE INDEX index_suggest_vote_button ON suggest_vote_type_vote_button (id_vote_button);

ALTER TABLE suggest_vote_type_vote_button ADD CONSTRAINT fk_suggest_vote_type FOREIGN KEY (id_vote_type)
	REFERENCES suggest_vote_type (id_vote_type);
ALTER TABLE suggest_vote_type_vote_button ADD CONSTRAINT fk_suggest_vote_button FOREIGN KEY (id_vote_button)
	REFERENCES suggest_vote_button (id_vote_button);

--
-- Table structure for table suggest_rss_cf
--
CREATE TABLE suggest_rss_cf (
	id_rss int default 0 NOT NULL,
	id_suggest int default 0 NOT NULL,
	is_submit_rss smallint default 0 NOT NULL,	
	id_suggest_submit int default 0 NOT NULL,
	PRIMARY KEY (id_rss)
);

--
-- Table structure for table suggest_suggest_submit_type
--
CREATE TABLE suggest_suggest_submit_type (
	id_type int default 0 NOT NULL,
	name long varchar,
	color varchar(10),
	id_resource_image int DEFAULT NULL,
	parameterizable smallint default 0,
	id_xsl int default 0 NOT NULL,
	PRIMARY KEY (id_type)
);


--
-- Table structure for table suggest_suggest_suggest_submit_type
--
CREATE TABLE suggest_suggest_suggest_submit_type (
	id_suggest int default 0 NOT NULL,
	id_type int default 0 NOT NULL,
	PRIMARY KEY (id_suggest,id_type)
);



ALTER TABLE suggest_suggest_suggest_submit_type ADD CONSTRAINT fk_suggest_suggest_suggest_submit_type FOREIGN KEY (id_suggest)
	REFERENCES suggest_suggest (id_suggest);

ALTER TABLE suggest_suggest_suggest_submit_type ADD CONSTRAINT fk_suggest_suggest_submit_type FOREIGN KEY (id_type)
	REFERENCES suggest_suggest_submit_type (id_type);

	
	
--
-- Table structure for table suggest_entry_attr_additional
--
CREATE TABLE suggest_entry_attr_additional (
	id_entry int default 0 NOT NULL,
	attr_name varchar(100) NOT NULL,
	attr_value varchar(100) NOT NULL
);

--
-- Table structure for table suggest_video
--

CREATE TABLE suggest_video (
	id_suggest_submit int NOT NULL,
	video_content long varbinary DEFAULT NULL,
	video_mime_type varchar(100) DEFAULT NULL,
	credits varchar(100) DEFAULT NULL,
	PRIMARY KEY (id_suggest_submit)
);

--
-- Table structure for table suggest_suggest_user_info
--

CREATE TABLE suggest_suggest_user_info (
  lutece_user_key varchar(255) NOT NULL,
  first_name varchar(255) DEFAULT NULL,
  last_name varchar(255)  DEFAULT NULL,
  business_mail varchar(255) DEFAULT NULL,
  home_mail varchar(255) DEFAULT NULL,
  login varchar(255)  DEFAULT NULL,
  PRIMARY KEY (lutece_user_key)
) ;	

--
-- Table structure for table suggest_suggest_attribute		
--
CREATE TABLE suggest_suggest_attribute (
	id_suggest INT DEFAULT 0 NOT NULL,
	attribute_key varchar(255) NOT NULL,
	attribute_value long varchar DEFAULT NULL,
	PRIMARY KEY (id_suggest, attribute_key)
);

--
-- Table structure for table suggest_reported_message		
--
CREATE TABLE suggest_reported_message (
	id_reported_message int default 0 NOT NULL,
	id_suggest_submit int default NULL,
	date_reported timestamp NULL,
	reported_value long varchar,
	PRIMARY KEY (id_reported_message)
	);

 
CREATE INDEX index_suggest_reported_message ON suggest_reported_message (id_suggest_submit);
ALTER TABLE suggest_reported_message ADD CONSTRAINT fk_suggest_reported_message FOREIGN KEY (id_suggest_submit)
	REFERENCES suggest_suggest_submit (id_suggest_submit);
	

