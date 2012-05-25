--
-- Insert action for updating all digg submit
--

INSERT INTO digglike_action (id_action,name_key,description_key,action_url,icon_url,action_permission,digg_state) VALUES 
(10,'digglike.action.updateAllDiggSubmit.name','digglike.action.updateAllDiggSubmit.description','jsp/admin/plugins/digglike/ConfirmUpdateAllDiggSubmit.jsp','images/admin/skin/plugins/digglike/actions/updatealldiggsubmit.png','UPDATE_ALL_DIGG_SUBMIT',1);
INSERT INTO digglike_action (id_action,name_key,description_key,action_url,icon_url,action_permission,digg_state) VALUES 
(11,'digglike.action.updateAllDiggSubmit.name','digglike.action.updateAllDiggSubmit.description','jsp/admin/plugins/digglike/ConfirmUpdateAllDiggSubmit.jsp','images/admin/skin/plugins/digglike/actions/updatealldiggsubmit.png','UPDATE_ALL_DIGG_SUBMIT',0);

--
-- Insert Editor bbcode
--
INSERT INTO digglike_entry_type (id_type,title,class_name) VALUES (6,'Editeur bbcode','fr.paris.lutece.plugins.digglike.business.EntryTypeEditorBbcode');

--
-- Add column Editor bbcode
--

ALTER TABLE digglike_digg ADD COLUMN active_editor_bbcode smallint default 0;
--
-- Add column number view
--


ALTER TABLE digglike_digg_submit ADD COLUMN number_view int default 0;