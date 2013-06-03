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
ALTER TABLE digglike_digg_submit ADD COLUMN disable_comment smallint default 0;


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




	
	
	