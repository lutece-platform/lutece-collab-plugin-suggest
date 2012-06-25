UPDATE core_admin_right  SET admin_url="jsp/admin/plugins/digglike/ManageDigg.jsp" WHERE id_right='DIGGLIKE_MANAGEMENT';
UPDATE core_admin_role_resource SET role_key="digg_manager" WHERE rbac_id=908;
UPDATE core_admin_role_resource SET role_key="digg_manager" WHERE rbac_id=909;
UPDATE core_admin_role_resource SET role_key="digg_manager" WHERE rbac_id=910;
UPDATE core_admin_role_resource SET role_key="digg_manager" WHERE rbac_id=911;

ALTER TABLE digglike_digg ADD COLUMN default_digg smallint default 0;
ALTER TABLE digglike_digg ADD COLUMN id_default_sort int;
ALTER TABLE digglike_digg ADD COLUMN active_digg_submit_type smallint default 0;


