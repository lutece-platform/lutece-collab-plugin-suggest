-- liquibase formatted sql
-- changeset suggest:prerun-record-installed-version-4.0.0
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT ( SELECT COUNT(id_workflow) FROM suggest_suggest WHERE 1 = 0 ) + ( SELECT COUNT(*) FROM core_datastore WHERE entity_key LIKE '%core.plugins.status.suggest.version' ) + ( 1 - ( SELECT COUNT(*) FROM core_admin_right WHERE id_right = 'SUGGEST_MANAGEMENT' ) )
INSERT INTO core_datastore ( entity_key, entity_value ) VALUES ( 'core.plugins.status.suggest.version', '4.0.0' );

-- changeset suggest:prerun-record-installed-version-2.0.0
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT ( SELECT COUNT(*) FROM core_datastore WHERE entity_key LIKE '%core.plugins.status.suggest.version' ) + ( 1 - ( SELECT COUNT(*) FROM core_admin_right WHERE id_right = 'SUGGEST_MANAGEMENT' ) )
INSERT INTO core_datastore ( entity_key, entity_value ) VALUES ( 'core.plugins.status.suggest.version', '2.0.0' );
