
--
-- Dumping data for table `digglike_digg`
--
INSERT INTO digglike_digg (id_digg, title, unavailability_message, workgroup, id_vote_type, number_vote_required, number_day_required, active_digg_submit_authentification, active_vote_authentification, active_comment_authentification, disable_new_digg_submit, authorized_comment, disable_new_comment, id_mailing_list_digg_submit, id_mailing_list_comment, active_captcha, active, date_creation, libelle_validate_button, active_digg_proposition_state, libelle_contribution, number_digg_submit_in_top_score, number_digg_submit_in_top_comment, limit_number_vote, number_digg_submit_caracters_shown, show_category_block, show_top_score_block, show_top_comment_block, active_digg_submit_paginator, number_digg_submit_per_page, role) VALUES (1,'Boîte à idées','Ce digg est indisponible','all',2,-1,-1,0,0,0,0,1,0,-1,-1,0,1,'2009-05-13 22:24:07','Valider',0,'Vos propositions d''amélioration',10,10,0,500,1,1,1,0,-1,'none');


--
-- Dumping data for table `digglike_digg_submit`
--
INSERT INTO digglike_digg_submit (id_digg_submit, id_digg, id_state, user_login, date_response, vote_number, score_number, id_category, digg_submit_value, digg_submit_title, comment_enable_number, digg_submit_value_show_in_the_list, reported, lutece_user_key) VALUES (1,1,3,NULL,'2009-05-13 22:39:40',13,26,NULL,'	<div class=\"response-element-title\"> \r\n 		Ajouter des statistiques de consultations\r\n	</div>	\r\n\r\n','Ajouter des statistiques de consultations',0,'	<div class=\"response-element-title\"> \r\n 		Ajouter des statistiques de consultations\r\n	</div>	\r\n\r\n',0,NULL);

--
-- Dumping data for table `digglike_entry`
--
INSERT INTO digglike_entry (id_entry, id_digg, id_type, title, help_message, entry_comment, mandatory, pos, default_value, height, width, max_size_enter, show_in_digg_submit_list) VALUES (1,1,3,'Votre proposition','Saisir votre proposition','',1,1,'',5,57,-1,1);


--
-- Dumping data for table `digglike_response`
--
INSERT INTO digglike_response (id_response, id_digg_submit, response_value, id_entry) VALUES (1,1,'Ajouter des statistiques de consultations',1);

