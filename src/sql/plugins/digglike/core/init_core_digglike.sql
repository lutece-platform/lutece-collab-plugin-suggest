--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
('DIGGLIKE_MANAGEMENT','digglike.adminFeature.digglike_management.name',2,'jsp/admin/plugins/digglike/ManagePluginDigglike.jsp','digglike.adminFeature.digglike_management.description',0,'digglike','APPLICATIONS','images/admin/skin/plugins/digglike/digglike.png',NULL);


--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right (id_right,id_user) VALUES ('DIGGLIKE_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('DIGGLIKE_MANAGEMENT',2);

--
-- Dumping data for table core_admin_role
--
INSERT INTO core_admin_role (role_key,role_description) VALUES ('digg_manager','Gestion des diggs');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('digg_manager_admin','Administration des diggs');

--
-- Dumping data for table core_admin_role_resource
--
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES 
 (908,'digg_manager_admin','DIGGLIKE_DEFAULT_MESSAGE_TYPE','*','*');
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES  
 (910,'digg_manager_admin','DIGGLIKE_EXPORT_FORMAT_TYPE','*','*');
 INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES  
 (911,'digg_manager_admin','DIGGLIKE_CATEGORY_TYPE','*','*');
 INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES 
 (909,'digg_manager','DIGGLIKE_DIGG_TYPE','*','*');

--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('digg_manager_admin',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('digg_manager',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('digg_manager',2);
