--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
('SUGGEST_MANAGEMENT','suggest.adminFeature.suggest_management.name',2,'jsp/admin/plugins/suggest/ManageSuggest.jsp','suggest.adminFeature.suggest_management.description',0,'suggest','APPLICATIONS','images/admin/skin/plugins/suggest/suggest.png',NULL);


--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right (id_right,id_user) VALUES ('SUGGEST_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('SUGGEST_MANAGEMENT',2);

--
-- Dumping data for table core_admin_role
--
INSERT INTO core_admin_role (role_key,role_description) VALUES ('suggest_manager','Gestion des suggests');
-- INSERT INTO core_admin_role (role_key,role_description) VALUES ('suggest_manager_admin','Administration des suggests');

--
-- Dumping data for table core_admin_role_resource
--
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES 
 (908,'suggest_manager','SUGGEST_DEFAULT_MESSAGE_TYPE','*','*');
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES  
 (910,'suggest_manager','SUGGEST_EXPORT_FORMAT_TYPE','*','*');
 INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES  
 (911,'suggest_manager','SUGGEST_CATEGORY_TYPE','*','*');
 INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES 
 (909,'suggest_manager','SUGGEST_SUGGEST_TYPE','*','*');

--
-- Dumping data for table core_user_role
--
-- INSERT INTO core_user_role (role_key,id_user) VALUES ('suggest_manager_admin',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('suggest_manager',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('suggest_manager',2);
