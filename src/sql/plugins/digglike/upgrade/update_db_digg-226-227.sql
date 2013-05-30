--
-- Table structure for table digglike_digg_attribute		
--
DROP TABLE IF EXISTS digglike_digg_attribute CASCADE;
CREATE TABLE digglike_digg_attribute (
	id_digg INT DEFAULT 0 NOT NULL,
	attribute_key varchar(255) NOT NULL,
	attribute_value varchar(255) NOT NULL,
	PRIMARY KEY (id_digg, attribute_key)
);


--
-- Add column disable_vote,is_pinned
--

ALTER TABLE digglike_digg_submit ADD COLUMN disable_vote int default 0;
ALTER TABLE digglike_digg_submit ADD COLUMN is_pinned int default 0;


TRUNCATE digglike_action;
--
-- Dumping data for table digglike_action
--
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (1, 'digglike.action.modify.name', 'digglike.action.modify.description', 'jsp/admin/plugins/digglike/ModifyDigg.jsp', 'icon-edit icon-white', 'MODIFY', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (2, 'digglike.action.modify.name', 'digglike.action.modify.description', 'jsp/admin/plugins/digglike/ModifyDigg.jsp', 'icon-edit icon-white', 'MODIFY', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (3, 'digglike.action.manageDiggSubmit.name', 'digglike.action.manageDiggSubmit.description', 'jsp/admin/plugins/digglike/ManageDiggSubmit.jsp', 'icon-inbox icon-white', 'MANAGE_DIGG_SUBMIT', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (4, 'digglike.action.manageDiggSubmit.name', 'digglike.action.manageDiggSubmit.description', 'jsp/admin/plugins/digglike/ManageDiggSubmit.jsp', 'icon-inbox icon-white', 'MANAGE_DIGG_SUBMIT', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (5, 'digglike.action.manageDiggSubmitOrder.name', 'digglike.action.manageDiggSubmitOrder.description', 'jsp/admin/plugins/digglike/ManageDiggSubmitOrder.jsp', ' icon-arrow-up icon-white', 'MANAGE_DIGG_SUBMIT', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (6, 'digglike.action.manageDiggSubmitOrder.name', 'digglike.action.manageDiggSubmitOrder.description', 'jsp/admin/plugins/digglike/ManageDiggSubmitOrder.jsp', ' icon-arrow-up icon-white', 'MANAGE_DIGG_SUBMIT', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (7, 'digglike.action.disable.name', 'digglike.action.disable.description', 'jsp/admin/plugins/digglike/ConfirmDisableDigg.jsp', 'icon-remove icon-white', 'CHANGE_STATE', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (8, 'digglike.action.enable.name', 'digglike.action.enable.description', 'jsp/admin/plugins/digglike/DoEnableDigg.jsp', 'icon-ok icon-white', 'CHANGE_STATE', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (9, 'digglike.action.copy.name', 'digglike.action.copy.description', 'jsp/admin/plugins/digglike/DoCopyDigg.jsp', 'icon-move icon-white', 'COPY', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (10, 'digglike.action.copy.name', 'digglike.action.copy.description', 'jsp/admin/plugins/digglike/DoCopyDigg.jsp', 'icon-move icon-white', 'COPY', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (11, 'digglike.action.delete.name', 'digglike.action.delete.description', 'jsp/admin/plugins/digglike/ConfirmRemoveDigg.jsp', 'icon-trash icon-white', 'DELETE', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (12, 'digglike.action.updateAllDiggSubmit.name', 'digglike.action.updateAllDiggSubmit.description', 'jsp/admin/plugins/digglike/ConfirmUpdateAllDiggSubmit.jsp', 'icon-cog icon-white', 'UPDATE_ALL_DIGG_SUBMIT', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (13, 'digglike.action.updateAllDiggSubmit.name', 'digglike.action.updateAllDiggSubmit.description', 'jsp/admin/plugins/digglike/ConfirmUpdateAllDiggSubmit.jsp', 'icon-cog icon-white', 'UPDATE_ALL_DIGG_SUBMIT', 0);

--
-- Table structure for table digglike_reported_message		
--
DROP TABLE IF EXISTS digglike_reported_message CASCADE;
CREATE TABLE digglike_reported_message (
	id_reported_message int default 0 NOT NULL,
	id_digg_submit int default NULL,
	date_reported timestamp NULL,
	reported_value long varchar,
	PRIMARY KEY (id_reported_message)
	);

 
CREATE INDEX index_digglike_reported_message ON digglike_reported_message (id_digg_submit);
ALTER TABLE digglike_reported_message ADD CONSTRAINT fk_digglike_reported_message FOREIGN KEY (id_digg_submit)
	REFERENCES digglike_digg_submit (id_digg_submit);
	
	
	
	
--
-- Table structure for table digglike_digg_digg_submit_type
--
DROP TABLE IF EXISTS digglike_digg_digg_submit_type CASCADE;
CREATE TABLE digglike_digg_digg_submit_type (
	id_digg int default 0 NOT NULL,
	id_type int default 0 NOT NULL,
	PRIMARY KEY (id_digg,id_type)
);



ALTER TABLE digglike_digg_digg_submit_type ADD CONSTRAINT fk_digglike_digg_digg_submit_type FOREIGN KEY (id_digg)
	REFERENCES digglike_digg (id_digg);

ALTER TABLE digglike_digg_digg_submit_type ADD CONSTRAINT fk_digglike_digg_submit_type FOREIGN KEY (id_type)
	REFERENCES digglike_digg_submit_type (id_type);

--
-- Drop Column  active_digg_submit_type
--	
ALTER TABLE  digglike_digg  DROP COLUMN active_digg_submit_type; 
	
	
	