create database kafka;
use kafka;

create user 'nativeuser'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'nativeuser'@'localhost';

drop table if exists event;
create table event (
  event_id INT NOT NULL,
  event_timestamp VARCHAR(255) NOT NULL,
  service_code VARCHAR(255),
  event_context VARCHAR(255),
  PRIMARY KEY (event_id)
);


drop table if exists event_input;
create table event_input (
  event_id INT NOT NULL,
  event_timestamp VARCHAR(255) NOT NULL,
  service_code VARCHAR(255),
  event_context VARCHAR(255),
  PRIMARY KEY (event_id)
);