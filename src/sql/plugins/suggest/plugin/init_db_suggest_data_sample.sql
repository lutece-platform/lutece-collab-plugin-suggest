
--
-- Dumping data for table suggest_suggest_attribute
--

DELETE FROM suggest_suggest_attribute;
INSERT INTO suggest_suggest_attribute (id_suggest, attribute_key, attribute_value) VALUES
	(1, 'idImageResource', '-1'),
	(1, 'disableVote', 'false'),
	(1, 'notificationNewCommentBody', '<p>&nbsp;</p>\r\n<p>Hello</p>\r\n<p><br /> ${comments?size} New comments have been added : <br /> &lt;#list comments as comment_submit&gt;</p>\r\n<p>&lt;#if comment_submit.isOfficialAnswer()&gt; <span class="badge badge-warning"> Official response </span> &nbsp; ${comment_submit.dateComment?string("dd/MM/yyyy HH:mm:ss")}</p>\r\n<p>&lt;#if comment_submit.value?length &gt; 100&gt; ${comment_submit.value?substring(0,100)} &lt;#else&gt; ${comment_submit.value} <a class="btn btn-small btn-primary" href="${base_url}/jsp/site/Portal.jsp?page=suggest&amp;id_suggest=${comment_submit.suggestSubmit.suggest.idSuggest}&amp;id_suggest_submit=${comment_submit.suggestSubmit.idSuggestSubmit}&amp;action=view_suggest_submit"> Read More </a></p>'),
	(1, 'termsOfUse', ''),
	(1, 'enableMailNewReportedSubmit', 'false'),
	(1, 'numberCharCommentDisplayInSuggestSubmitList', '30'),
	(1, 'enableTermsOfUse', 'false'),
	(1, 'notificationNewCommentTitle', '<p>New comments have been posted</p>'),
	(1, 'notificationNewCommentSenderName', ''),
	(1, 'numberCommentDisplayInSuggestSubmitList', '3'),
	(1, 'enableMailNewCommentSubmit', 'false'),
	(1, 'description', '<p>&nbsp;Research shows that happiness at work is essential to organizational success, entirely possible to foster, and well worth the investment and effort. <b>What are your proposals?</b></p>'),
	(1, 'notificationNewSuggestSubmitTitle', '<p>New contributions have been created</p>'),
	(1, 'notificationNewSuggestSubmitBody', '<p>&nbsp;</p>\r\n<p>Hello</p>\r\n<p><br /> ${suggestSubmits?size} New contributions have been added : <br /> &lt;#list suggestSubmits as suggestSubmit&gt;</p>\r\n<p>${suggestSubmit.suggestSubmitTitle}</p>\r\n<p><a class="btn btn-small btn-primary" href="${base_url}/jsp/site/Portal.jsp?page=suggest&amp;id_suggest=${suggestSubmit.suggest.idSuggest}&amp;id_suggest_submit=${suggestSubmit.idSuggestSubmit}&amp;action=view_suggest_submit"> Read More </a></p>'),
	(1, 'displayCommentInSuggestSubmitList', 'false'),
	(1, 'notificationNewSuggestSubmitSenderName', ''),
	(1, 'enableReports', 'false'),
	(2, 'idImageResource', '-1'),
	(2, 'disableVote', 'false'),
	(2, 'notificationNewCommentBody', '<p>&nbsp;</p>\r\n<p>Hello</p>\r\n<p><br /> ${comments?size} New comments have been added : <br /> &lt;#list comments as comment_submit&gt;</p>\r\n<p>&lt;#if comment_submit.isOfficialAnswer()&gt; <span class="badge badge-warning"> Official response </span> &nbsp; ${comment_submit.dateComment?string("dd/MM/yyyy HH:mm:ss")}</p>\r\n<p>&lt;#if comment_submit.value?length &gt; 100&gt; ${comment_submit.value?substring(0,100)} &lt;#else&gt; ${comment_submit.value} <a class="btn btn-small btn-primary" href="${base_url}/jsp/site/Portal.jsp?page=suggest&amp;id_suggest=${comment_submit.suggestSubmit.suggest.idSuggest}&amp;id_suggest_submit=${comment_submit.suggestSubmit.idSuggestSubmit}&amp;action=view_suggest_submit"> Read More </a></p>'),
	(2, 'termsOfUse', ''),
	(2, 'enableMailNewReportedSubmit', 'false'),
	(2, 'numberCharCommentDisplayInSuggestSubmitList', '30'),
	(2, 'enableTermsOfUse', 'false'),
	(2, 'notificationNewCommentTitle', '<p>New comments have been posted</p>'),
	(2, 'notificationNewCommentSenderName', ''),
	(2, 'numberCommentDisplayInSuggestSubmitList', '3'),
	(2, 'enableMailNewCommentSubmit', 'false'),
	(2, 'description', '<p>&nbsp;Climate change is a global phenomenon that largely impacts urban life. At the same time, cities are a key contributor to climate change, as urban activities are major sources of greenhouse gas emissions. <b>What initiatives would you like to support?</b></p>'),
	(2, 'notificationNewSuggestSubmitTitle', '<p>New contributions have been created</p>'),
	(2, 'notificationNewSuggestSubmitBody', '<p>&nbsp;</p>\r\n<p>Hello</p>\r\n<p><br /> ${suggestSubmits?size} New contributions have been added : <br /> &lt;#list suggestSubmits as suggestSubmit&gt;</p>\r\n<p>${suggestSubmit.suggestSubmitTitle}</p>\r\n<p><a class="btn btn-small btn-primary" href="${base_url}/jsp/site/Portal.jsp?page=suggest&amp;id_suggest=${suggestSubmit.suggest.idSuggest}&amp;id_suggest_submit=${suggestSubmit.idSuggestSubmit}&amp;action=view_suggest_submit"> Read More </a></p>'),
	(2, 'displayCommentInSuggestSubmitList', 'false'),
	(2, 'notificationNewSuggestSubmitSenderName', ''),
	(2, 'enableReports', 'false');


--
-- Dumping data for table suggest_category
--

DELETE FROM suggest_category;
INSERT INTO suggest_category ( id_category, title, color) VALUES
	(1, 'Theme 1', NULL),
	(2, 'Theme 2', NULL),
	(3, 'Energy', '#ffffbb'),
	(4, 'Transportation', '#ffbbff'),
	(5, 'Environment', '#bbffff');

--
-- Dumping data for table suggest_suggest
--
DELETE FROM suggest_suggest;
INSERT INTO suggest_suggest (id_suggest, title, unavailability_message, workgroup, id_vote_type, number_vote_required, number_day_required, active_suggest_submit_authentification, active_vote_authentification, active_comment_authentification, disable_new_suggest_submit, authorized_comment, disable_new_comment, id_mailing_list_suggest_submit, active_captcha, active, date_creation, libelle_validate_button, active_suggest_proposition_state, libelle_contribution, number_suggest_submit_in_top_score, number_suggest_submit_in_top_comment, limit_number_vote, number_suggest_submit_caracters_shown, show_category_block, show_top_score_block, show_top_comment_block, active_suggest_submit_paginator, number_suggest_submit_per_page, role, enable_new_suggest_submit_mail, header, sort_field, sort_asc, code_theme, confirmation_message, active_editor_bbcode, default_suggest, id_default_sort, notification_new_comment_sender, notification_new_comment_title, notification_new_comment_body, notification_new_suggest_submit_sender, notification_new_suggest_submit_title, notification_new_suggest_submit_body) VALUES
	(1, 'Hapiness at work', '<p>This consultation is not available</p>', 'all', 1, -1, -1, 0, 0, 0, 0, 0, 0, -1, 0, 1, '2019-12-26 16:25:29', 'Post', 0, 'Hapiness at work', 10, 10, 0, 500, 1, 1, 1, 1, 20, NULL, 0, '', 0, 0, 'blue', '', 0, 0, -1, '', '<p>New comments have been posted</p>', '<p>&nbsp;</p>\r\n<p>Hello</p>\r\n<p><br /> ${comments?size} New comments have been added : <br /> &lt;#list comments as comment_submit&gt;</p>\r\n<p>&lt;#if comment_submit.isOfficialAnswer()&gt; <span class="badge badge-warning"> Official response </span> &nbsp; ${comment_submit.dateComment?string("dd/MM/yyyy HH:mm:ss")}</p>\r\n<p>&lt;#if comment_submit.value?length &gt; 100&gt; ${comment_submit.value?substring(0,100)} &lt;#else&gt; ${comment_submit.value} <a class="btn btn-small btn-primary" href="${base_url}/jsp/site/Portal.jsp?page=suggest&amp;id_suggest=${comment_submit.suggestSubmit.suggest.idSuggest}&amp;id_suggest_submit=${comment_submit.suggestSubmit.idSuggestSubmit}&amp;action=view_suggest_submit"> Read More </a> </p>', '', '<p>New contributions have been created</p>', '<p>&nbsp;</p>\r\n<p>Hello</p>\r\n<p><br /> ${suggestSubmits?size} New contributions have been added : <br /> &lt;#list suggestSubmits as suggestSubmit&gt;</p>\r\n<p>${suggestSubmit.suggestSubmitTitle}</p>\r\n<p><a class="btn btn-small btn-primary" href="${base_url}/jsp/site/Portal.jsp?page=suggest&amp;id_suggest=${suggestSubmit.suggest.idSuggest}&amp;id_suggest_submit=${suggestSubmit.idSuggestSubmit}&amp;action=view_suggest_submit"> Read More </a> </p>'),
	(2, 'What can my city do about climate change?', '<p>The consultation is closed</p>', 'all', 1, -1, -1, 0, 0, 0, 0, 0, 0, -1, 0, 1, '2019-12-27 14:42:50', 'Send', 0, 'What can my city do about climate change?', 10, 10, 0, 500, 1, 1, 1, 1, 20, NULL, 0, '', 0, 0, 'blue', '', 0, 0, -1, '', '<p>New comments have been posted</p>', '<p>&nbsp;</p>\r\n<p>Hello</p>\r\n<p><br /> ${comments?size} New comments have been added : <br /> &lt;#list comments as comment_submit&gt;</p>\r\n<p>&lt;#if comment_submit.isOfficialAnswer()&gt; <span class="badge badge-warning"> Official response </span> &nbsp; ${comment_submit.dateComment?string("dd/MM/yyyy HH:mm:ss")}</p>\r\n<p>&lt;#if comment_submit.value?length &gt; 100&gt; ${comment_submit.value?substring(0,100)} &lt;#else&gt; ${comment_submit.value} <a class="btn btn-small btn-primary" href="${base_url}/jsp/site/Portal.jsp?page=suggest&amp;id_suggest=${comment_submit.suggestSubmit.suggest.idSuggest}&amp;id_suggest_submit=${comment_submit.suggestSubmit.idSuggestSubmit}&amp;action=view_suggest_submit"> Read More </a></p>', '', '<p>New contributions have been created</p>', '<p>&nbsp;</p>\r\n<p>Hello</p>\r\n<p><br /> ${suggestSubmits?size} New contributions have been added : <br /> &lt;#list suggestSubmits as suggestSubmit&gt;</p>\r\n<p>${suggestSubmit.suggestSubmitTitle}</p>\r\n<p><a class="btn btn-small btn-primary" href="${base_url}/jsp/site/Portal.jsp?page=suggest&amp;id_suggest=${suggestSubmit.suggest.idSuggest}&amp;id_suggest_submit=${suggestSubmit.idSuggestSubmit}&amp;action=view_suggest_submit"> Read More </a></p>');

--
-- Dumping data for table suggest_suggest_category
--
DELETE FROM suggest_suggest_category;
INSERT INTO suggest_suggest_category (id_suggest, id_category) VALUES
	(2, 3),
	(2, 4),
	(2, 5);
	

--
-- Dumping data for table suggest_entry
--

DELETE FROM suggest_entry;
INSERT INTO suggest_entry (id_entry, id_suggest, id_type, title, help_message, entry_comment, mandatory, pos, default_value, height, width, max_size_enter, show_in_suggest_submit_list) VALUES
	(2, 1, 3, 'Description', '', '', 1, 1, '', 4, 80, 500, 1),
	(1, 1, 2, 'Title', '', '', 1, 2, '', -1, 80, -1, 1),
	(3, 2, 2, 'Title', '', '', 1, 4, '', -1, 100, -1, 1),
	(4, 2, 3, 'Description', '', '', 1, 3, '', 4, 100, 500, 1);
	
	
--
-- Dumping data for table suggest_suggest_submit
--
DELETE FROM suggest_suggest_submit;
INSERT INTO suggest_suggest_submit (id_suggest_submit, id_suggest, id_state, user_login, date_response, vote_number, score_number, id_category, suggest_submit_value, suggest_submit_title, comment_enable_number, suggest_submit_value_show_in_the_list, reported, lutece_user_key, suggest_submit_list_order, suggest_submit_type, number_view, disable_vote, is_pinned, disable_comment, id_image_resource, comment_number) 
VALUES (3, 1, 3, NULL, '2019-12-26 16:42:45', 0, 0, 1, '<p class="lead">\nHire Happy Personalities\n</p>	\nA happy work environment attracts good people and in turn, promotes a culture of productivity and accountability â€“ a stressful environment will most definitely destroy the company culture.\n', 'Hire Happy Personalities', 0, '<p class="lead">\nHire Happy Personalities\n</p>	\nA happy work environment attracts good people and in turn, promotes a culture of productivity and accountability â€“ a stressful environment will most definitely destroy the company culture.\n', 0, NULL, 2, NULL, 0, 0, 0, 0, NULL, 0),
	(4, 1, 3, NULL, '2019-12-26 16:44:24', 0, 0, 1, '<p class="lead">\nWellness Program\n</p>	\nA wellness program that allows people to set health goals, access tools (trackers and apps). It can reward them for completing healthy activities and improvement in health through an innovative app\n', 'Wellness Program', 0, '<p class="lead">\nWellness Program\n</p>	\nA wellness program that allows people to set health goals, access tools (trackers and apps). It can reward them for completing healthy activities and improvement in health through an innovative app\n', 0, NULL, 3, NULL, 0, 0, 0, 0, NULL, 0),
	(2, 1, 3, NULL, '2019-12-26 16:34:24', 2, 2, 1, '<p class="lead">\nAvoid micromanagement\n</p>	\nIf employees feel like they are constantly on their bossâ€™s radar, they are not going to perform the way they normally would, and they will begin to resent their job.\r\nIt doesnâ€™t help anyone if half the day is spent recording and reporting what tasks were checked off and which ones werenâ€™t, so, give the team the trust and creative freedom that they deserve by setting clear expectations and fair boundaries.\n', 'Avoid micromanagement', 0, '<p class="lead">\nAvoid micromanagement\n</p>	\nIf employees feel like they are constantly on their bossâ€™s radar, they are not going to perform the way they normally would, and they will begin to resent their job.\r\nIt doesnâ€™t help anyone if half the day is spent recording and reporting what tasks were checked off and which ones werenâ€™t, so, give the team the trust and creative freedom that they deserve by setting clear expectations and fair boundaries.\n', 0, NULL, 1, NULL, 0, 0, 0, 0, NULL, 0),
	(5, 1, 3, NULL, '2019-12-26 16:45:17', 1, 1, 1, '<p class="lead">\nManage Work/Life balance\n</p>	\nMake it known that work/life balance is a priority by offering work-from-home Fridays, unlimited vacation days, discounts on surrounding health and wellness programs, or childcare options. Offering incentives that improve a team memberâ€™s overall quality of life show that the company cares about the wellbeing.\n', 'Manage Work/Life balance', 0, '<p class="lead">\nManage Work/Life balance\n</p>	\nMake it known that work/life balance is a priority by offering work-from-home Fridays, unlimited vacation days, discounts on surrounding health and wellness programs, or childcare options. Offering incentives that improve a team memberâ€™s overall quality of life show that the company cares about the wellbeing.\n', 0, NULL, 4, NULL, 0, 0, 0, 0, NULL, 0),
	(6, 2, 3, NULL, '2019-12-27 15:07:22', 1, 1, 3, '<p class="lead">\nSustainable roofs\n</p>	\nRooftop gardens are even more effective because they bring the temperature down, provide shade, clean the air, and donâ€™t just bounce sunlight back into the atmosphere, which could potentially disrupt precipitation patterns, and also retain heat in the wintertime, bringing heating bills down.\n', 'Sustainable roofs', 0, '<p class="lead">\nSustainable roofs\n</p>	\nRooftop gardens are even more effective because they bring the temperature down, provide shade, clean the air, and donâ€™t just bounce sunlight back into the atmosphere, which could potentially disrupt precipitation patterns, and also retain heat in the wintertime, bringing heating bills down.\n', 0, NULL, 1, NULL, 0, 0, 0, 0, NULL, 0),
	(7, 2, 3, NULL, '2019-12-27 15:08:15', 1, 1, 5, '<p class="lead">\nBanning plastic\n</p>	\nThe vast majority of this plastic isnâ€™t recycled. It ends up in landfills, oceans, green spaces, and elsewhere, where it pollutes ecosystems, harms animals, and contaminates drinking water. For many governments around the world, this blanketing of the planet is a tipping point â€” the convenience of plastic no longer seems worth the environmental consequences.\n', 'Banning plastic', 0, '<p class="lead">\nBanning plastic\n</p>	\nThe vast majority of this plastic isnâ€™t recycled. It ends up in landfills, oceans, green spaces, and elsewhere, where it pollutes ecosystems, harms animals, and contaminates drinking water. For many governments around the world, this blanketing of the planet is a tipping point â€” the convenience of plastic no longer seems worth the environmental consequences.\n', 0, NULL, 2, NULL, 0, 0, 0, 0, NULL, 0),
	(8, 2, 3, NULL, '2019-12-27 15:11:30', 1, 1, 4, '<p class="lead">\nBicycle highways\n</p>	\nThe transportation sector is one of the largest sources of greenhouse gas emissions in the world, and cars and trucks make up a large part of that total. Cities feel the effects of vehicle emissions more acutely than the rest of the world â€” all the fumes lead to air pollution that significantly reduces quality of life.\r\nWhen it comes building serious bicycle highways, the Danes and the Dutch were the great pioneers. While the Danish supercykelstiers are concentrated in and around Copenhagen, the\n', 'Bicycle highways', 0, '<p class="lead">\nBicycle highways\n</p>	\nThe transportation sector is one of the largest sources of greenhouse gas emissions in the world, and cars and trucks make up a large part of that total. Cities feel the effects of vehicle emissions more acutely than the rest of the world â€” all the fumes lead to air pollution that significantly reduces quality of life.\r\nWhen it comes building serious bicycle highways, the Danes and the Dutch were the great pioneers. While the Danish supercykelstiers are concentrated in and around...\n', 0, NULL, 3, NULL, 0, 0, 0, 0, NULL, 0),
	(9, 2, 3, NULL, '2019-12-27 15:23:27', 0, 0, 4, '<p class="lead">\nCommuting with an electric bike\n</p>	\nResearch shows that e-bikes are 10 to 20 times more energy efficient than a car, and frankly, an e-bike is just plain fun to ride. Folding e-bikes like this one can give you a sweat-free, less stressful commute and get you out of your car, the fastest-growing contributor to greenhouse gases in our country\n', 'Commuting with an electric bike', 0, '<p class="lead">\nCommuting with an electric bike\n</p>	\nResearch shows that e-bikes are 10 to 20 times more energy efficient than a car, and frankly, an e-bike is just plain fun to ride. Folding e-bikes like this one can give you a sweat-free, less stressful commute and get you out of your car, the fastest-growing contributor to greenhouse gases in our country\n', 0, NULL, 4, NULL, 0, 0, 0, 0, NULL, 0);


--
-- Dumping data for table suggest_response
--
DELETE FROM suggest_response;
INSERT INTO suggest_response (id_response, id_suggest_submit, response_value, id_entry, id_resource_image) 
VALUES	
	(1, 3, 'Hire Happy Personalities', 1, NULL),
	(2, 3, 'A happy work environment attracts good people and in turn, promotes a culture of productivity and accountability â€“ a stressful environment will most definitely destroy the company culture.', 2, NULL),
	(3, 4, 'Wellness Program', 1, NULL),
	(4, 4, 'A wellness program that allows people to set health goals, access tools (trackers and apps). It can reward them for completing healthy activities and improvement in health through an innovative app', 2, NULL),
	(5, 2, 'Avoid micromanagement', 1, NULL),
	(6, 2, 'If employees feel like they are constantly on their bossâ€™s radar, they are not going to perform the way they normally would, and they will begin to resent their job.\r\nIt doesnâ€™t help anyone if half the day is spent recording and reporting what tasks were checked off and which ones werenâ€™t, so, give the team the trust and creative freedom that they deserve by setting clear expectations and fair boundaries.', 2, NULL),
	(7, 5, 'Manage Work/Life balance', 1, NULL),
	(8, 5, 'Make it known that work/life balance is a priority by offering work-from-home Fridays, unlimited vacation days, discounts on surrounding health and wellness programs, or childcare options. Offering incentives that improve a team memberâ€™s overall quality of life show that the company cares about the wellbeing.', 2, NULL),
	(9, 6, 'Sustainable roofs', 3, NULL),
	(10, 6, 'Rooftop gardens are even more effective because they bring the temperature down, provide shade, clean the air, and donâ€™t just bounce sunlight back into the atmosphere, which could potentially disrupt precipitation patterns, and also retain heat in the wintertime, bringing heating bills down.', 4, NULL),
	(11, 7, 'Banning plastic', 3, NULL),
	(12, 7, 'The vast majority of this plastic isnâ€™t recycled. It ends up in landfills, oceans, green spaces, and elsewhere, where it pollutes ecosystems, harms animals, and contaminates drinking water. For many governments around the world, this blanketing of the planet is a tipping point â€” the convenience of plastic no longer seems worth the environmental consequences.', 4, NULL),
	(13, 8, 'Bicycle highways', 3, NULL),
	(14, 8, 'The transportation sector is one of the largest sources of greenhouse gas emissions in the world, and cars and trucks make up a large part of that total. Cities feel the effects of vehicle emissions more acutely than the rest of the world â€” all the fumes lead to air pollution that significantly reduces quality of life.\r\nWhen it comes building serious bicycle highways, the Danes and the Dutch were the great pioneers. While the Danish supercykelstiers are concentrated in and around Copenhagen, the', 4, NULL),
	(15, 9, 'Commuting with an electric bike', 3, NULL),
	(16, 9, 'Research shows that e-bikes are 10 to 20 times more energy efficient than a car, and frankly, an e-bike is just plain fun to ride. Folding e-bikes like this one can give you a sweat-free, less stressful commute and get you out of your car, the fastest-growing contributor to greenhouse gases in our country', 4, NULL);

