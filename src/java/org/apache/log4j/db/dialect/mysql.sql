# This SQL script creates the required tables by org.apache.log4j.db.DBAppender and 
# org.apache.log4j.db.DBReceiver.
#
# It is intended for MySQL databases. It has been tested on MySQL 4.1.1 with 
# INNODB tables.


BEGIN;
DROP TABLE IF EXISTS logging_event;
DROP TABLE IF EXISTS logging_event_property;
DROP TABLE IF EXISTS logging_event_exception;
COMMIT;


BEGIN;
CREATE TABLE logging_event 
  (
    sequence_number BIGINT NOT NULL,
    timestamp         BIGINT NOT NULL,
    rendered_message  TEXT NOT NULL,
    logger_name       VARCHAR(254) NOT NULL,
    level_string      VARCHAR(254) NOT NULL,
    ndc               TEXT,
    thread_name       VARCHAR(254),
    reference_flag    SMALLINT,
    event_id          INT NOT NULL AUTO_INCREMENT PRIMARY KEY
  );
COMMIT;

BEGIN;
CREATE TABLE logging_event_property
  (
    event_id	      INT NOT NULL,
    mapped_key        VARCHAR(254) NOT NULL,
    mapped_value      VARCHAR(254),
    PRIMARY KEY(event_id, mapped_key),
    FOREIGN KEY (event_id) REFERENCES logging_event(id)
  );
COMMIT;

BEGIN;
CREATE TABLE logging_event_exception
  (
    event_id         INT NOT NULL,
    i                SMALLINT NOT NULL,
    trace_line       VARCHAR(254) NOT NULL,
    PRIMARY KEY(event_id, i),
    FOREIGN KEY (event_id) REFERENCES logging_event(id)
  );
COMMIT;