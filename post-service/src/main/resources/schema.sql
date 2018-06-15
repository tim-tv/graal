drop table if exists service_secure_access_token;
create table service_secure_access_token (
  app_id VARCHAR(255) PRIMARY KEY NOT NULL,
  token VARCHAR(255) NOT NULL,
  expires_at TIMESTAMP
);

drop table if exists service_secure_details;
create table service_secure_details (
  app_id VARCHAR(255) PRIMARY KEY NOT NULL,
  app_secret VARCHAR(255) NOT NULL
);

insert into service_secure_details values('aggregation-service-id', 'aggregation-service-secret');
