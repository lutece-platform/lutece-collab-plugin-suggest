-- DATA migration from DIGGLIKE tables to SUGGEST
-- >> REQUIRE the execution of the SUGGEST create_db SQL script first

INSERT INTO  suggest_vote_type  SELECT * FROM  digglike_vote_type     ;

insert `suggest_suggest` (
	`suggest_suggest`.`id_suggest`,
    `suggest_suggest`.`title`,
    `suggest_suggest`.`unavailability_message`,
    `suggest_suggest`.`workgroup`,
    `suggest_suggest`.`id_vote_type`,
    `suggest_suggest`.`number_vote_required`,
    `suggest_suggest`.`number_day_required`,
    `suggest_suggest`.`active_suggest_submit_authentification`,
    `suggest_suggest`.`active_vote_authentification`,
    `suggest_suggest`.`active_comment_authentification`,
    `suggest_suggest`.`disable_new_suggest_submit`,
    `suggest_suggest`.`authorized_comment`,
    `suggest_suggest`.`disable_new_comment`,
    `suggest_suggest`.`id_mailing_list_suggest_submit`,
    `suggest_suggest`.`active_captcha`,
    `suggest_suggest`.`active`,
    `suggest_suggest`.`date_creation`,
    `suggest_suggest`.`libelle_validate_button`,
    `suggest_suggest`.`active_suggest_proposition_state`,
    `suggest_suggest`.`libelle_contribution`,
    `suggest_suggest`.`number_suggest_submit_in_top_score`,
    `suggest_suggest`.`number_suggest_submit_in_top_comment`,
    `suggest_suggest`.`limit_number_vote`,
    `suggest_suggest`.`number_suggest_submit_caracters_shown`,
    `suggest_suggest`.`show_category_block`,
    `suggest_suggest`.`show_top_score_block`,
    `suggest_suggest`.`show_top_comment_block`,
    `suggest_suggest`.`active_suggest_submit_paginator`,
    `suggest_suggest`.`number_suggest_submit_per_page`,
    `suggest_suggest`.`role`,
    `suggest_suggest`.`enable_new_suggest_submit_mail`,
    `suggest_suggest`.`header`,
    `suggest_suggest`.`sort_field`,
    `suggest_suggest`.`sort_asc`,
    `suggest_suggest`.`code_theme`,
    `suggest_suggest`.`confirmation_message`,
    `suggest_suggest`.`active_editor_bbcode`,
    `suggest_suggest`.`default_suggest`,
    `suggest_suggest`.`id_default_sort`,
    `suggest_suggest`.`notification_new_comment_sender`,
    `suggest_suggest`.`notification_new_comment_title`,
    `suggest_suggest`.`notification_new_comment_body`,
    `suggest_suggest`.`notification_new_suggest_submit_sender`,
    `suggest_suggest`.`notification_new_suggest_submit_title`,
    `suggest_suggest`.`notification_new_suggest_submit_body`)
SELECT `digglike_digg`.`id_digg`,
    `digglike_digg`.`title`,
    `digglike_digg`.`unavailability_message`,
    `digglike_digg`.`workgroup`,
    `digglike_digg`.`id_vote_type`,
    `digglike_digg`.`number_vote_required`,
    `digglike_digg`.`number_day_required`,
    `digglike_digg`.`active_digg_submit_authentification`,
    `digglike_digg`.`active_vote_authentification`,
    `digglike_digg`.`active_comment_authentification`,
    `digglike_digg`.`disable_new_digg_submit`,
    `digglike_digg`.`authorized_comment`,
    `digglike_digg`.`disable_new_comment`,
    `digglike_digg`.`id_mailing_list_digg_submit`,
    -- `digglike_digg`.`id_mailing_list_comment`,
    `digglike_digg`.`active_captcha`,
    `digglike_digg`.`active`,
    `digglike_digg`.`date_creation`,
    `digglike_digg`.`libelle_validate_button`,
    `digglike_digg`.`active_digg_proposition_state`,
    `digglike_digg`.`libelle_contribution`,
    `digglike_digg`.`number_digg_submit_in_top_score`,
    `digglike_digg`.`number_digg_submit_in_top_comment`,
    `digglike_digg`.`limit_number_vote`,
    `digglike_digg`.`number_digg_submit_caracters_shown`,
    `digglike_digg`.`show_category_block`,
    `digglike_digg`.`show_top_score_block`,
    `digglike_digg`.`show_top_comment_block`,
    `digglike_digg`.`active_digg_submit_paginator`,
    `digglike_digg`.`number_digg_submit_per_page`,
    `digglike_digg`.`role`,
    `digglike_digg`.`enable_new_digg_submit_mail`,
    -- `digglike_digg`.`id_mailing_list_new_digg_submit`,
    `digglike_digg`.`header`,
    `digglike_digg`.`sort_field`,
    `digglike_digg`.`sort_asc`,
    `digglike_digg`.`code_theme`,
    `digglike_digg`.`confirmation_message`,
    `digglike_digg`.`active_editor_bbcode`,
    `digglike_digg`.`default_digg`,
    `digglike_digg`.`id_default_sort`,
    `digglike_digg`.`notification_new_comment_sender`,
    `digglike_digg`.`notification_new_comment_title`,
    `digglike_digg`.`notification_new_comment_body`,
    `digglike_digg`.`notification_new_digg_submit_sender`,
    `digglike_digg`.`notification_new_digg_submit_title`,
    `digglike_digg`.`notification_new_digg_submit_body`
FROM `digglike_digg`;



insert into suggest_vote_type_vote_button   select * from digglike_vote_type_vote_button    ;
-- INSERT INTO  suggest_tag_submit SELECT * FROM  digglike_tag_submit     ;
INSERT INTO  suggest_entry_verify_by SELECT * FROM  digglike_entry_verify_by     ;

INSERT INTO  suggest_category  SELECT * FROM  digglike_category     ;
INSERT INTO  suggest_suggest_category  SELECT * FROM  digglike_digg_category     ;

INSERT INTO  suggest_suggest_suggest_submit_type  SELECT * FROM  digglike_digg_digg_submit_type     ;
INSERT INTO  suggest_suggest_submit_type  SELECT * FROM  digglike_digg_submit_type     ;
INSERT INTO  suggest_suggest_submit_state  SELECT * FROM  digglike_digg_submit_state     ;
INSERT INTO  suggest_suggest_submit  SELECT * FROM  digglike_digg_submit     ;

-- ! it could be necessary to clean the database before migration
-- delete  from digglike_comment_submit where id_digg_submit not in (select id_digg_submit from digglike_digg_submit);
INSERT INTO  suggest_comment_submit  SELECT * FROM  digglike_comment_submit     ;

INSERT INTO  suggest_vote_button  SELECT * FROM  digglike_vote_button     ;
INSERT INTO  suggest_vote SELECT * FROM  digglike_vote     ;
INSERT INTO  suggest_export_format  SELECT * FROM  digglike_export_format     ;

INSERT INTO  suggest_entry_type  SELECT * FROM  digglike_entry_type     ;
INSERT INTO  suggest_entry_attr_additional  SELECT * FROM  digglike_entry_attr_additional     ;
INSERT INTO  suggest_entry SELECT * FROM  digglike_entry      ;
INSERT INTO  suggest_response SELECT * FROM  digglike_response     ;

INSERT INTO  suggest_default_message  SELECT * FROM  digglike_default_message     ;
INSERT INTO  suggest_action  SELECT * FROM  digglike_action    ;
INSERT INTO  suggest_image  SELECT * FROM  digglike_image     ;

INSERT INTO  suggest_rss_cf  SELECT * FROM  digglike_rss_cf     ; 

INSERT INTO  suggest_video  SELECT * FROM  digglike_video     ;
INSERT INTO  suggest_suggest_user_info SELECT * FROM  digglike_digg_user_info     ;
INSERT INTO  suggest_suggest_attribute  SELECT * FROM  digglike_digg_attribute     ;
INSERT INTO  suggest_reported_message  SELECT * FROM  digglike_reported_message     ;


-- SUGGEST ACTIONS
update suggest_action set name_key = replace(name_key, 'digglike', 'suggest' ) where name_key like '%digglike%' ;
update suggest_action set name_key = replace(name_key, 'Digg', 'Suggest' )     where name_key like '%Digg%' ;

update suggest_action set description_key = replace(description_key, 'digglike', 'suggest' ) where description_key like '%digglike%' ;
update suggest_action set description_key = replace(description_key, 'Digg', 'Suggest' )     where description_key like '%Digg%' ;

update suggest_action set action_url = replace(action_url, 'digglike', 'suggest' ) where action_url like '%digglike%' ;
update suggest_action set action_url = replace(action_url, 'Digg', 'Suggest' )     where action_url like '%Digg%' ;

update suggest_action set action_permission = replace(action_permission, 'DIGG', 'SUGGEST' )     where action_permission like '%DIGG%' ;

-- ENTRY TYPES
update suggest_entry_type set class_name = replace(class_name, 'digglike', 'suggest' ) where class_name like '%digglike%' ;

-- INDEXER
update core_indexer_action set indexer_name  = 'SuggestIndexer' where indexer_name='DigglikeIndexer';

-- TEMPLATES
update suggest_vote_type set template_file_name = replace ( template_file_name, "digg", "suggest" );

-- DELETE RIGHTS
delete from core_admin_role_resource where role_key = 'digg_manager';
delete from core_admin_role  where role_key  = 'digg_manager';
delete from core_user_role   where role_key  = 'digg_manager';
delete from core_user_right  where id_right  = 'DIGGLIKE_MANAGEMENT';
delete from core_admin_right where id_right  = 'DIGGLIKE_MANAGEMENT';

-- DROP OLD TABLES
DROP TABLE IF EXISTS digglike_vote_type_vote_button;
DROP TABLE IF EXISTS digglike_tag_submit;
DROP TABLE IF EXISTS digglike_response;
DROP TABLE IF EXISTS digglike_entry_verify_by;
DROP TABLE IF EXISTS digglike_entry;
DROP TABLE IF EXISTS digglike_digg_submit CASCADE;
DROP TABLE IF EXISTS digglike_digg_category CASCADE;
DROP TABLE IF EXISTS digglike_comment_submit CASCADE;
DROP TABLE IF EXISTS digglike_digglike CASCADE;
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
DROP TABLE IF EXISTS digglike_digg_digg_submit_type CASCADE;
DROP TABLE IF EXISTS digglike_rss_cf CASCADE; 
DROP TABLE IF EXISTS digglike_entry_attr_additional CASCADE;
DROP TABLE IF EXISTS digglike_video CASCADE;
DROP TABLE IF EXISTS digglike_digg_user_info;
DROP TABLE IF EXISTS digglike_digg_attribute CASCADE;
DROP TABLE IF EXISTS digglike_reported_message CASCADE;

