
--
-- Table structure for table digglike_image
--
DROP TABLE IF EXISTS digglike_image;
CREATE TABLE digglike_image (
	id_resource_image  int NOT NULL,
	image_content LONG VARBINARY DEFAULT NULL,
	image_mime_type varchar(100) DEFAULT NULL,
	PRIMARY KEY (id_resource_image)
);
--
-- Table structure for table digglike_response
--
ALTER TABLE  digglike_response ADD COLUMN id_resource_image int DEFAULT NULL;



--
-- Table structure for table digglike_digg_submit_type
--

ALTER TABLE digglike_digg_submit_type ADD COLUMN id_resource_image int DEFAULT NULL;
ALTER TABLE digglike_digg_submit_type DROP COLUMN image_content;
ALTER TABLE digglike_digg_submit_type DROP COLUMN image_mime_type;
ALTER TABLE digglike_digg_submit_type DROP COLUMN image_url;

--
-- Table structure for table digglike_digg_attribute		
--
DROP TABLE IF EXISTS digglike_digg_attribute CASCADE;
CREATE TABLE digglike_digg_attribute (
	id_digg INT DEFAULT 0 NOT NULL,
	attribute_key varchar(255) NOT NULL,
	attribute_value long varchar DEFAULT NULL DEFAULT NULL,
	PRIMARY KEY (id_digg, attribute_key)
);


--
-- Add column disable_vote,is_pinned,disable_comment,id_image_resource,comment_number
--

ALTER TABLE digglike_digg_submit ADD COLUMN disable_vote int default 0;
ALTER TABLE digglike_digg_submit ADD COLUMN is_pinned int default 0;
ALTER TABLE digglike_digg_submit ADD COLUMN disable_comment smallint default 0;
ALTER TABLE digglike_digg_submit ADD COLUMN id_image_resource int default NULL;
ALTER TABLE digglike_digg_submit ADD COLUMN comment_number int default 0;
--
-- Table structure for table digglike_reported_message		
--
DROP TABLE IF EXISTS digglike_reported_message ;
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
DROP TABLE IF EXISTS digglike_digg_digg_submit_type;
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

--
-- Dumping data for table digglike_export_format
--
TRUNCATE digglike_export_format;

INSERT INTO digglike_export_format (id_export, title, description, extension, xsl_file) VALUES (1, 'xml', 'Exporter les propositions au format xml', 'xml', 0x3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D2249534F2D383835392D31223F3E0D0A3C78736C3A7374796C6573686565742076657273696F6E3D22312E302220786D6C6E733A78736C3D22687474703A2F2F7777772E77332E6F72672F313939392F58534C2F5472616E73666F726D223E0D0A3C78736C3A6F7574707574206D6574686F643D22786D6C222076657273696F6E3D22312E302220656E636F64696E673D2249534F2D383835392D312220696E64656E743D22796573222063646174612D73656374696F6E2D656C656D656E74733D22646967672D7375626D69742D76616C756520646967672D7375626D69742D63617465676F7279222F3E0D0A3C78736C3A74656D706C617465206D617463683D222F223E0D0A203C78736C3A6170706C792D74656D706C617465732073656C6563743D2264696767222F3E200D0A3C2F78736C3A74656D706C6174653E0D0A0D0A3C78736C3A74656D706C617465206D617463683D2264696767223E0D0A090D0A093C646967673E0D0A09093C646967672D7469746C653E0D0A0909093C78736C3A76616C75652D6F662073656C6563743D22646967672D7469746C65222F3E0D0A09093C2F646967672D7469746C653E0D0A09093C646967672D7375626D6974733E0D0A0909093C78736C3A6170706C792D74656D706C617465732073656C6563743D22646967672D7375626D6974732F646967672D7375626D6974222F3E200D0A09093C2F646967672D7375626D6974733E0D0A09093C646967672D656E74726965733E0D0A0909093C78736C3A6170706C792D74656D706C617465732073656C6563743D22646967672D656E74726965732F656E747279222F3E200D0A09093C2F646967672D656E74726965733E0D0A09090D0A093C2F646967673E090D0A3C2F78736C3A74656D706C6174653E0D0A0D0A3C78736C3A74656D706C617465206D617463683D22646967672D7375626D6974223E0D0A093C646967672D7375626D69743E0D0A09090D0A09093C646967672D7375626D69742D7469746C653E0D0A0909093C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D7469746C65222F3E0D0A09093C2F646967672D7375626D69742D7469746C653E0D0A09090D0A09090D0A09093C646967672D7375626D69742D646174652D726573706F6E73653E0D0A0909093C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D646174652D726573706F6E7365222F3E0D0A09093C2F646967672D7375626D69742D646174652D726573706F6E73653E0D0A09090D0A09093C646967672D7375626D69742D73636F72653E0D0A0909093C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D73636F7265222F3E0D0A093C2F646967672D7375626D69742D73636F72653E0D0A09090D0A09093C646967672D7375626D69742D6E756D6265722D766F74653E0D0A0909093C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D6E756D6265722D766F7465222F3E0D0A093C2F646967672D7375626D69742D6E756D6265722D766F74653E0D0A09090D0A09093C646967672D7375626D69742D6E756D6265722D636F6D6D656E743E0D0A0909093C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D6E756D6265722D636F6D6D656E74222F3E0D0A09093C2F646967672D7375626D69742D6E756D6265722D636F6D6D656E743E0D0A09090D0A09093C646967672D7375626D69742D63617465676F72793E0D0A0909093C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D63617465676F7279222F3E0D0A09093C2F646967672D7375626D69742D63617465676F72793E0D0A09093C646967672D7375626D69742D747970653E0D0A0909093C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D74797065222F3E0D0A09093C2F646967672D7375626D69742D747970653E0D0A09093C646967672D7375626D69742D726573706F6E7365733E0D0A0909093C78736C3A6170706C792D74656D706C617465732073656C6563743D22646967672D7375626D69742D726573706F6E7365732F726573706F6E7365222F3E0D0A09093C2F646967672D7375626D69742D726573706F6E7365733E0D0A093C2F646967672D7375626D69743E0D0A3C2F78736C3A74656D706C6174653E0D0A0D0A0D0A093C78736C3A74656D706C617465206D617463683D22656E747279223E0D0A09093C656E7472793E0D0A0909093C7469746C653E0D0A090909093C78736C3A76616C75652D6F662073656C6563743D227469746C65222F3E0D0A0909093C2F7469746C653E0D0A0909093C69643E0D0A090909093C78736C3A76616C75652D6F662073656C6563743D226964222F3E0D0A0909093C2F69643E0D0A0909093C747970652D69643E0D0A090909093C78736C3A76616C75652D6F662073656C6563743D22747970652D6964222F3E0D0A0909093C2F747970652D69643E0D0A09093C2F656E7472793E0D0A093C2F78736C3A74656D706C6174653E0D0A0D0A093C78736C3A74656D706C617465206D617463683D22726573706F6E7365223E0D0A0920093C726573706F6E73653E0D0A092009093C726573706F6E73652D76616C75653E0D0A090909093C78736C3A76616C75652D6F662073656C6563743D22726573706F6E73652D76616C7565222F3E0D0A0909093C2F726573706F6E73652D76616C75653E0D0A0909093C78736C3A6170706C792D74656D706C617465732073656C6563743D22656E747279222F3E0D0A09093C2F726573706F6E73653E0D0A093C2F78736C3A74656D706C6174653E0D0A0D0A3C2F78736C3A7374796C6573686565743E);
INSERT INTO digglike_export_format (id_export, title, description, extension, xsl_file) VALUES (2, 'csv propositions', 'Exporter les propositions au format csv', 'csv', 0x3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D225554462D38223F3E0D0A3C78736C3A7374796C6573686565742076657273696F6E3D22312E302220786D6C6E733A78736C3D22687474703A2F2F7777772E77332E6F72672F313939392F58534C2F5472616E73666F726D223E0D0A093C78736C3A6F7574707574206D6574686F643D2274657874222F3E0D0A093C78736C3A74656D706C617465206D617463683D222F64696767223E0D0A09093C78736C3A6170706C792D74656D706C617465732073656C6563743D22646967672D656E74726965732F656E747279222F3E0D0A09093C78736C3A746578743E3B22646174652070726F706F736974696F6E223B2273636F7265223B226E6F6D62726520646520766F7465223B226E6F6D62726520646520636F6D6D656E7461697265223B22547970652064652070726F706F736974696F6E223B2263617426233233333B676F726965223C2F78736C3A746578743E0D0A09093C78736C3A746578743E262331303B3C2F78736C3A746578743E0D0A09093C78736C3A6170706C792D74656D706C617465732073656C6563743D22646967672D7375626D6974732F646967672D7375626D6974222F3E0D0A093C2F78736C3A74656D706C6174653E0D0A090D0A093C78736C3A74656D706C617465206D617463683D22646967672D7375626D6974223E0D0A090909093C78736C3A6170706C792D74656D706C617465732073656C6563743D22646967672D7375626D69742D726573706F6E7365732F726573706F6E7365222F3E0D0A090909093C78736C3A746578743E3B223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D646174652D726573706F6E7365222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D73636F7265222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D6E756D6265722D766F7465222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D6E756D6265722D636F6D6D656E74222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D74797065222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D63617465676F7279222F3E3C78736C3A746578743E223C2F78736C3A746578743E203C78736C3A746578743E262331303B3C2F78736C3A746578743E0D0A093C2F78736C3A74656D706C6174653E0D0A090D0A090D0A093C78736C3A74656D706C617465206D617463683D22656E747279223E0D0A09093C78736C3A746578743E223C2F78736C3A746578743E0D0A09093C78736C3A76616C75652D6F662073656C6563743D227469746C65222F3E0D0A09093C78736C3A746578743E223C2F78736C3A746578743E0D0A09093C78736C3A696620746573743D22706F736974696F6E2829213D6C6173742829223E0D0A0909093C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09093C2F78736C3A69663E0D0A093C2F78736C3A74656D706C6174653E0D0A090D0A090D0A090D0A093C78736C3A74656D706C617465206D617463683D22726573706F6E7365223E0D0A09203C78736C3A746578743E223C2F78736C3A746578743E0D0A09093C78736C3A76616C75652D6F662073656C6563743D22726573706F6E73652D76616C7565222F3E0D0A09093C78736C3A746578743E223C2F78736C3A746578743E0D0A09093C78736C3A696620746573743D22706F736974696F6E2829213D6C6173742829223E0D0A0909093C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09093C2F78736C3A69663E0D0A093C2F78736C3A74656D706C6174653E0D0A3C2F78736C3A7374796C6573686565743E);
INSERT INTO digglike_export_format (id_export, title, description, extension, xsl_file) VALUES (3, 'csv commentaires', 'Export les commentaires au format csv', 'csv', 0x3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D225554462D38223F3E0D0A3C78736C3A7374796C6573686565742076657273696F6E3D22312E302220786D6C6E733A78736C3D22687474703A2F2F7777772E77332E6F72672F313939392F58534C2F5472616E73666F726D223E0D0A093C78736C3A6F7574707574206D6574686F643D2274657874222F3E0D0A093C78736C3A74656D706C617465206D617463683D222F64696767223E0D0A09093C78736C3A746578743E22636F6D6D656E7461697265223B226461746520647520636F6D6D656E7461697265223B227479706520646520636F6D6D656E7461697265223B227469747265206465206C612070726F706F736974696F6E223B22646174652070726F706F736974696F6E223B2273636F7265223B226E6F6D62726520646520766F7465223B6E6F6D62726520646520636F6D6D656E7461697265223B22747970652064652070726F706F736974696F6E223B222263617426233233333B676F726965223C2F78736C3A746578743E0D0A09093C78736C3A746578743E262331303B3C2F78736C3A746578743E0D0A09093C78736C3A6170706C792D74656D706C617465732073656C6563743D22646967672D7375626D6974732F646967672D7375626D6974222F3E0D0A093C2F78736C3A74656D706C6174653E0D0A093C78736C3A74656D706C617465206D617463683D222F646967672F646967672D7375626D6974732F646967672D7375626D6974223E0D0A090D0A093C78736C3A666F722D656163682073656C6563743D22646967672D7375626D69742D636F6D6D656E74732F646967672D7375626D69742D636F6D6D656E74223E0D0A090D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D636F6D6D656E742D76616C7565222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D636F6D6D656E742D64617465222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E22636F6D6D656E74616972652064652070726F706F736974696F6E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909090D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F646967672D7375626D69742D7469746C65222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F646967672D7375626D69742D646174652D726573706F6E7365222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F646967672D7375626D69742D73636F7265222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F646967672D7375626D69742D6E756D6265722D766F7465222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F646967672D7375626D69742D6E756D6265722D636F6D6D656E74222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F646967672D7375626D69742D74797065222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F646967672D7375626D69742D63617465676F7279222F3E3C78736C3A746578743E223C2F78736C3A746578743E203C78736C3A746578743E262331303B3C2F78736C3A746578743E0D0A090909093C78736C3A666F722D656163682073656C6563743D22646967672D7375626D69742D636F6D6D656E74732F646967672D7375626D69742D636F6D6D656E74223E0D0A09090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D636F6D6D656E742D76616C7565222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D22646967672D7375626D69742D636F6D6D656E742D64617465222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09090909093C78736C3A746578743E22636F6D6D656E746169726520646520636F6D6D656E7461697265223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F2E2E2F2E2E2F646967672D7375626D69742D7469746C65222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F2E2E2F2E2E2F646967672D7375626D69742D646174652D726573706F6E7365222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F2E2E2F2E2E2F646967672D7375626D69742D73636F7265222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F2E2E2F2E2E2F646967672D7375626D69742D6E756D6265722D766F7465222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F2E2E2F2E2E2F646967672D7375626D69742D6E756D6265722D636F6D6D656E74222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F2E2E2F2E2E2F646967672D7375626D69742D74797065222F3E3C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A746578743E3B3C2F78736C3A746578743E0D0A09090909093C78736C3A746578743E223C2F78736C3A746578743E3C78736C3A76616C75652D6F662073656C6563743D222E2E2F2E2E2F2E2E2F2E2E2F646967672D7375626D69742D63617465676F7279222F3E3C78736C3A746578743E223C2F78736C3A746578743E203C78736C3A746578743E262331303B3C2F78736C3A746578743E0D0A090909093C2F78736C3A666F722D65616368203E0D0A093C2F78736C3A666F722D656163683E0D0A093C2F78736C3A74656D706C6174653E0D0A3C2F78736C3A7374796C6573686565743E);

DROP TABLE IF EXISTS digglike_action;
CREATE TABLE IF NOT EXISTS digglike_action (
  id_action int(11) NOT NULL DEFAULT '0',
  name_key varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  description_key varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  action_url varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  icon_url varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  action_permission varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  digg_state smallint(6) DEFAULT '0',
  PRIMARY KEY (id_action)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (1, 'digglike.action.modify.name', 'digglike.action.modify.description', 'jsp/admin/plugins/digglike/ModifyDigg.jsp', 'icon-edit icon-white', 'MODIFY', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (2, 'digglike.action.modify.name', 'digglike.action.modify.description', 'jsp/admin/plugins/digglike/ModifyDigg.jsp', 'icon-edit icon-white', 'MODIFY', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (3, 'digglike.action.manageDiggSubmit.name', 'digglike.action.manageDiggSubmit.description', 'jsp/admin/plugins/digglike/ManageDiggSubmit.jsp', 'icon-inbox icon-white', 'MANAGE_DIGG_SUBMIT', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (4, 'digglike.action.manageDiggSubmit.name', 'digglike.action.manageDiggSubmit.description', 'jsp/admin/plugins/digglike/ManageDiggSubmit.jsp', 'icon-inbox icon-white', 'MANAGE_DIGG_SUBMIT', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (5, 'digglike.action.disable.name', 'digglike.action.disable.description', 'jsp/admin/plugins/digglike/ConfirmDisableDigg.jsp', 'icon-remove icon-white', 'CHANGE_STATE', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (6, 'digglike.action.enable.name', 'digglike.action.enable.description', 'jsp/admin/plugins/digglike/DoEnableDigg.jsp', 'icon-ok icon-white', 'CHANGE_STATE', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (7, 'digglike.action.copy.name', 'digglike.action.copy.description', 'jsp/admin/plugins/digglike/DoCopyDigg.jsp', 'icon-plus-sign icon-white', 'COPY', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (8, 'digglike.action.copy.name', 'digglike.action.copy.description', 'jsp/admin/plugins/digglike/DoCopyDigg.jsp', 'icon-plus-sign icon-white', 'COPY', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (9, 'digglike.action.delete.name', 'digglike.action.delete.description', 'jsp/admin/plugins/digglike/ConfirmRemoveDigg.jsp', 'icon-trash icon-white', 'DELETE', 0);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (10, 'digglike.action.updateAllDiggSubmit.name', 'digglike.action.updateAllDiggSubmit.description', 'jsp/admin/plugins/digglike/ConfirmUpdateAllDiggSubmit.jsp', 'icon-refresh icon-white', 'UPDATE_ALL_DIGG_SUBMIT', 1);
INSERT INTO digglike_action (id_action, name_key, description_key, action_url, icon_url, action_permission, digg_state) VALUES (11, 'digglike.action.updateAllDiggSubmit.name', 'digglike.action.updateAllDiggSubmit.description', 'jsp/admin/plugins/digglike/ConfirmUpdateAllDiggSubmit.jsp', 'icon-refresh icon-white', 'UPDATE_ALL_DIGG_SUBMIT', 0);

	
	
	
	