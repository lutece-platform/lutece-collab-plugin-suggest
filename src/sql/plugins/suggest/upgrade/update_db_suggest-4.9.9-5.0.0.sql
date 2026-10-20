-- liquibase formatted sql
-- changeset suggest:update_db_suggest-4.9.9-5.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE suggest_action SET action_url = 'jsp/admin/plugins/suggest/ManageSuggest.jsp?view=modifySuggest' WHERE action_url = 'jsp/admin/plugins/suggest/ModifySuggest.jsp';
UPDATE suggest_action SET action_url = 'jsp/admin/plugins/suggest/ManageSuggest.jsp?view=manageSuggestSubmit' WHERE action_url = 'jsp/admin/plugins/suggest/ManageSuggestSubmit.jsp';
UPDATE suggest_action SET action_url = 'jsp/admin/plugins/suggest/ManageSuggest.jsp?view=confirmDisableSuggest' WHERE action_url = 'jsp/admin/plugins/suggest/ConfirmDisableSuggest.jsp';
UPDATE suggest_action SET action_url = 'jsp/admin/plugins/suggest/ManageSuggest.jsp?action=enableSuggest' WHERE action_url = 'jsp/admin/plugins/suggest/DoEnableSuggest.jsp';
UPDATE suggest_action SET action_url = 'jsp/admin/plugins/suggest/ManageSuggest.jsp?action=copySuggest' WHERE action_url = 'jsp/admin/plugins/suggest/DoCopySuggest.jsp';
UPDATE suggest_action SET action_url = 'jsp/admin/plugins/suggest/ManageSuggest.jsp?view=confirmRemoveSuggest' WHERE action_url = 'jsp/admin/plugins/suggest/ConfirmRemoveSuggest.jsp';
UPDATE suggest_action SET action_url = 'jsp/admin/plugins/suggest/ManageSuggest.jsp?view=confirmUpdateAllDisplayOfSuggestSubmit' WHERE action_url = 'jsp/admin/plugins/suggest/ConfirmUpdateAllSuggestSubmit.jsp';
UPDATE suggest_action SET icon_url = 'x' WHERE icon_url = 'times';
UPDATE suggest_action SET icon_url = 'settings' WHERE icon_url = 'cog';

-- changeset suggest:update_db_suggest-4.9.9-5.0.0-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url = 'ti ti-bulb' WHERE id_right = 'SUGGEST_MANAGEMENT' AND icon_url = 'images/admin/skin/plugins/suggest/suggest.png';

-- changeset suggest:update_db_suggest-4.9.9-5.0.0-rev2.sql dbms:mariadb,mysql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE suggest_export_format SET xsl_file = UNHEX( SUBSTRING( xsl_file, 3 ) ) WHERE xsl_file LIKE '0x%';
UPDATE suggest_image SET image_content = UNHEX( SUBSTRING( image_content, 3 ) ) WHERE image_content LIKE '0x%';

-- changeset suggest:update_db_suggest-4.9.9-5.0.0-rev3.sql dbms:mariadb
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE suggest_response SET response_value = REGEXP_REPLACE( response_value, '<div id=''mediaspace([0-9]+)''></div><script type=''text/javascript'' src=''js/player/swfobject[.]js''></script><script type=''text/javascript''>.*?</script>', '<video class="suggest-video mw-100" controls preload="metadata" src="jsp/site/plugins/suggest/getVideo.jsp?video_id=\\1"></video>' ) WHERE response_value LIKE '%SWFObject%';
UPDATE suggest_suggest_submit SET suggest_submit_value = REGEXP_REPLACE( suggest_submit_value, '<div id=''mediaspace([0-9]+)''></div><script type=''text/javascript'' src=''js/player/swfobject[.]js''></script><script type=''text/javascript''>.*?</script>', '<video class="suggest-video mw-100" controls preload="metadata" src="jsp/site/plugins/suggest/getVideo.jsp?video_id=\\1"></video>' ) WHERE suggest_submit_value LIKE '%SWFObject%';
UPDATE suggest_suggest_submit SET suggest_submit_value_show_in_the_list = REGEXP_REPLACE( suggest_submit_value_show_in_the_list, '<div id=''mediaspace([0-9]+)''></div><script type=''text/javascript'' src=''js/player/swfobject[.]js''></script><script type=''text/javascript''>.*?</script>', '<video class="suggest-video mw-100" controls preload="metadata" src="jsp/site/plugins/suggest/getVideo.jsp?video_id=\\1"></video>' ) WHERE suggest_submit_value_show_in_the_list LIKE '%SWFObject%';

-- changeset suggest:update_db_suggest-4.9.9-5.0.0-rev4.sql dbms:mysql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE suggest_response SET response_value = REGEXP_REPLACE( response_value, '<div id=''mediaspace([0-9]+)''></div><script type=''text/javascript'' src=''js/player/swfobject[.]js''></script><script type=''text/javascript''>.*?</script>', '<video class="suggest-video mw-100" controls preload="metadata" src="jsp/site/plugins/suggest/getVideo.jsp?video_id=$1"></video>' ) WHERE response_value LIKE '%SWFObject%';
UPDATE suggest_suggest_submit SET suggest_submit_value = REGEXP_REPLACE( suggest_submit_value, '<div id=''mediaspace([0-9]+)''></div><script type=''text/javascript'' src=''js/player/swfobject[.]js''></script><script type=''text/javascript''>.*?</script>', '<video class="suggest-video mw-100" controls preload="metadata" src="jsp/site/plugins/suggest/getVideo.jsp?video_id=$1"></video>' ) WHERE suggest_submit_value LIKE '%SWFObject%';
UPDATE suggest_suggest_submit SET suggest_submit_value_show_in_the_list = REGEXP_REPLACE( suggest_submit_value_show_in_the_list, '<div id=''mediaspace([0-9]+)''></div><script type=''text/javascript'' src=''js/player/swfobject[.]js''></script><script type=''text/javascript''>.*?</script>', '<video class="suggest-video mw-100" controls preload="metadata" src="jsp/site/plugins/suggest/getVideo.jsp?video_id=$1"></video>' ) WHERE suggest_submit_value_show_in_the_list LIKE '%SWFObject%';

-- changeset suggest:update_db_suggest-4.9.9-5.0.0-rev5.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'suggest_suggest_user_info' AND column_name = 'department'
ALTER TABLE suggest_suggest_user_info ADD COLUMN department varchar(255) DEFAULT NULL;
