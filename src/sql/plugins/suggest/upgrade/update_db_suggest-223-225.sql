-- liquibase formatted sql
-- changeset suggest:update_db_suggest-223-225.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'suggest_suggest' AND column_name = 'default_suggest'
UPDATE core_admin_right  SET admin_url="jsp/admin/plugins/suggest/ManageSuggest.jsp" WHERE id_right='SUGGEST_MANAGEMENT';
UPDATE core_admin_role_resource SET role_key="suggest_manager" WHERE rbac_id=908;
UPDATE core_admin_role_resource SET role_key="suggest_manager" WHERE rbac_id=909;
UPDATE core_admin_role_resource SET role_key="suggest_manager" WHERE rbac_id=910;
UPDATE core_admin_role_resource SET role_key="suggest_manager" WHERE rbac_id=911;

ALTER TABLE suggest_suggest ADD COLUMN default_suggest smallint default 0;
ALTER TABLE suggest_suggest ADD COLUMN id_default_sort int;
ALTER TABLE suggest_suggest ADD COLUMN active_suggest_submit_type smallint default 0;


