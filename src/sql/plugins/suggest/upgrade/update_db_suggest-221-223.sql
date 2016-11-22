--
-- Insert action for updating all suggest submit
--

INSERT INTO suggest_action (id_action,name_key,description_key,action_url,icon_url,action_permission,suggest_state) VALUES 
(10,'suggest.action.updateAllSuggestSubmit.name','suggest.action.updateAllSuggestSubmit.description','jsp/admin/plugins/suggest/ConfirmUpdateAllSuggestSubmit.jsp','images/admin/skin/plugins/suggest/actions/updateallsuggestsubmit.png','UPDATE_ALL_SUGGEST_SUBMIT',1);
INSERT INTO suggest_action (id_action,name_key,description_key,action_url,icon_url,action_permission,suggest_state) VALUES 
(11,'suggest.action.updateAllSuggestSubmit.name','suggest.action.updateAllSuggestSubmit.description','jsp/admin/plugins/suggest/ConfirmUpdateAllSuggestSubmit.jsp','images/admin/skin/plugins/suggest/actions/updateallsuggestsubmit.png','UPDATE_ALL_SUGGEST_SUBMIT',0);

--
-- Insert Editor bbcode
--
INSERT INTO suggest_entry_type (id_type,title,class_name) VALUES (6,'Editeur bbcode','fr.paris.lutece.plugins.suggest.business.EntryTypeEditorBbcode');

--
-- Add column Editor bbcode
--

ALTER TABLE suggest_suggest ADD COLUMN active_editor_bbcode smallint default 0;
--
-- Add column number view
--


ALTER TABLE suggest_suggest_submit ADD COLUMN number_view int default 0;