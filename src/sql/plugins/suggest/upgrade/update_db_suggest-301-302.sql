--
-- Table structure for table suggest_default_message
--
TRUNCATE suggest_action;
INSERT INTO suggest_action VALUES (1, 'suggest.action.modify.name', 'suggest.action.modify.description', 'jsp/admin/plugins/suggest/ModifySuggest.jsp', 'fa fa-pencil', 'MODIFY', 0);
INSERT INTO suggest_action VALUES (2, 'suggest.action.modify.name', 'suggest.action.modify.description', 'jsp/admin/plugins/suggest/ModifySuggest.jsp', 'fa fa-pencil', 'MODIFY', 1);
INSERT INTO suggest_action VALUES (3, 'suggest.action.manageSuggestSubmit.name', 'suggest.action.manageSuggestSubmit.description', 'jsp/admin/plugins/suggest/ManageSuggestSubmit.jsp', 'fa fa-inbox', 'MANAGE_SUGGEST_SUBMIT', 0);
INSERT INTO suggest_action VALUES (4, 'suggest.action.manageSuggestSubmit.name', 'suggest.action.manageSuggestSubmit.description', 'jsp/admin/plugins/suggest/ManageSuggestSubmit.jsp', 'fa fa-inbox', 'MANAGE_SUGGEST_SUBMIT', 1);
INSERT INTO suggest_action VALUES (5, 'suggest.action.disable.name', 'suggest.action.disable.description', 'jsp/admin/plugins/suggest/ConfirmDisableSuggest.jsp', 'fa fa-remove', 'CHANGE_STATE', 1);
INSERT INTO suggest_action VALUES (6, 'suggest.action.enable.name', 'suggest.action.enable.description', 'jsp/admin/plugins/suggest/DoEnableSuggest.jsp', 'fa fa-check', 'CHANGE_STATE', 0);
INSERT INTO suggest_action VALUES (7, 'suggest.action.copy.name', 'suggest.action.copy.description', 'jsp/admin/plugins/suggest/DoCopySuggest.jsp', 'fa fa-copy', 'COPY', 0);
INSERT INTO suggest_action VALUES (8, 'suggest.action.copy.name', 'suggest.action.copy.description', 'jsp/admin/plugins/suggest/DoCopySuggest.jsp', 'fa fa-copy', 'COPY', 1);
INSERT INTO suggest_action VALUES (9, 'suggest.action.delete.name', 'suggest.action.delete.description', 'jsp/admin/plugins/suggest/ConfirmRemoveSuggest.jsp', 'fa fa-trash', 'DELETE', 0);
INSERT INTO suggest_action VALUES (10, 'suggest.action.updateAllSuggestSubmit.name', 'suggest.action.updateAllSuggestSubmit.description', 'jsp/admin/plugins/suggest/ConfirmUpdateAllSuggestSubmit.jsp', 'fa fa-cog', 'UPDATE_ALL_SUGGEST_SUBMIT', 1);
INSERT INTO suggest_action VALUES (11, 'suggest.action.updateAllSuggestSubmit.name', 'suggest.action.updateAllSuggestSubmit.description', 'jsp/admin/plugins/suggest/ConfirmUpdateAllSuggestSubmit.jsp', 'fa fa-cog', 'UPDATE_ALL_SUGGEST_SUBMIT', 0);
