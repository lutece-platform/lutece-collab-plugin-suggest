DROP TABLE IF EXISTS `digglike_action`;
CREATE TABLE IF NOT EXISTS `digglike_action` (
  `id_action` int(11) NOT NULL DEFAULT '0',
  `name_key` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description_key` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `action_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `icon_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `action_permission` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `digg_state` smallint(6) DEFAULT '0',
  PRIMARY KEY (`id_action`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO `digglike_action` (`id_action`, `name_key`, `description_key`, `action_url`, `icon_url`, `action_permission`, `digg_state`) VALUES
	(1, 'digglike.action.modify.name', 'digglike.action.modify.description', 'jsp/admin/plugins/digglike/ModifyDigg.jsp', 'icon-edit icon-white', 'MODIFY', 0),
	(2, 'digglike.action.modify.name', 'digglike.action.modify.description', 'jsp/admin/plugins/digglike/ModifyDigg.jsp', 'icon-edit icon-white', 'MODIFY', 1),
	(3, 'digglike.action.manageDiggSubmit.name', 'digglike.action.manageDiggSubmit.description', 'jsp/admin/plugins/digglike/ManageDiggSubmit.jsp', 'icon-inbox icon-white', 'MANAGE_DIGG_SUBMIT', 0),
	(4, 'digglike.action.manageDiggSubmit.name', 'digglike.action.manageDiggSubmit.description', 'jsp/admin/plugins/digglike/ManageDiggSubmit.jsp', 'icon-inbox icon-white', 'MANAGE_DIGG_SUBMIT', 1),
	(5, 'digglike.action.disable.name', 'digglike.action.disable.description', 'jsp/admin/plugins/digglike/ConfirmDisableDigg.jsp', 'icon-remove icon-white', 'CHANGE_STATE', 1),
	(6, 'digglike.action.enable.name', 'digglike.action.enable.description', 'jsp/admin/plugins/digglike/DoEnableDigg.jsp', 'icon-ok icon-white', 'CHANGE_STATE', 0),
	(7, 'digglike.action.copy.name', 'digglike.action.copy.description', 'jsp/admin/plugins/digglike/DoCopyDigg.jsp', 'icon-move icon-white', 'COPY', 0),
	(8, 'digglike.action.copy.name', 'digglike.action.copy.description', 'jsp/admin/plugins/digglike/DoCopyDigg.jsp', 'icon-move icon-white', 'COPY', 1),
	(9, 'digglike.action.delete.name', 'digglike.action.delete.description', 'jsp/admin/plugins/digglike/ConfirmRemoveDigg.jsp', 'icon-trash icon-white', 'DELETE', 0),
	(10, 'digglike.action.updateAllDiggSubmit.name', 'digglike.action.updateAllDiggSubmit.description', 'jsp/admin/plugins/digglike/ConfirmUpdateAllDiggSubmit.jsp', 'icon-cog icon-white', 'UPDATE_ALL_DIGG_SUBMIT', 1),
	(11, 'digglike.action.updateAllDiggSubmit.name', 'digglike.action.updateAllDiggSubmit.description', 'jsp/admin/plugins/digglike/ConfirmUpdateAllDiggSubmit.jsp', 'icon-cog icon-white', 'UPDATE_ALL_DIGG_SUBMIT', 0);

