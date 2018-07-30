DROP DATABASE IF EXISTS kheops;

CREATE DATABASE kheops
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE kheops;

CREATE TABLE users (
  pk BIGINT NOT NULL AUTO_INCREMENT,
  created_time DATETIME NOT NULL,
  updated_time DATETIME NOT NULL,
  google_id VARCHAR(255) NOT NULL,
  google_email VARCHAR(255) NOT NULL,

  PRIMARY KEY (pk),
  INDEX google_id_index (google_id),
  INDEX google_email_index (google_email),

  UNIQUE google_id_unique (google_id),
  UNIQUE google_email_unique (google_email)
);

CREATE TABLE studies (
  pk BIGINT NOT NULL AUTO_INCREMENT,
  created_time DATETIME NOT NULL,
  updated_time DATETIME NOT NULL,
  study_uid VARCHAR(255) NOT NULL,
  study_date VARCHAR(255),
  study_time VARCHAR(255),
  timezone_offset_from_utc VARCHAR(255),
  accession_number VARCHAR(255),
  referring_physician_name VARCHAR(4095),
  patient_name VARCHAR(4095),
  patient_id VARCHAR(255),
  patient_birth_date VARCHAR(255),
  patient_sex VARCHAR(255),
  study_id VARCHAR(255),

  populated BOOLEAN,

  PRIMARY KEY (pk),
  INDEX study_uid_index (study_uid),
  INDEX study_date_index (study_date),
  INDEX study_time_index (study_time),
  INDEX accession_number_index (accession_number),
  INDEX patient_id_index (patient_id),
  INDEX study_id_index (study_id),

  INDEX populated_index (populated),

  UNIQUE study_uid_unique (study_uid)
);

CREATE TABLE series (
  pk BIGINT NOT NULL AUTO_INCREMENT,
  created_time DATETIME NOT NULL,
  updated_time DATETIME NOT NULL,
  modality VARCHAR(255),
  timezone_offset_from_utc VARCHAR(255),
  series_description VARCHAR(255),
  series_uid VARCHAR(255) NOT NULL,
  series_number INT,
  number_of_series_related_instances INT,
  study_fk BIGINT NOT NULL,
  populated BOOLEAN,

  PRIMARY KEY (pk),
  INDEX series_uid_index (series_uid),
  INDEX study_fk_index (study_fk),
  INDEX populated_index (populated),

  FOREIGN KEY (study_fk)
    REFERENCES studies(pk)
    ON DELETE RESTRICT,

  UNIQUE series_uid_unique (series_uid)
);

CREATE TABLE user_series (
  user_fk BIGINT NOT NULL,
  series_fk BIGINT NOT NULL,

  PRIMARY KEY (user_fk, series_fk),

  FOREIGN KEY (user_fk)
    REFERENCES users(pk)
    ON DELETE RESTRICT,
  FOREIGN KEY (series_fk)
    REFERENCES series(pk)
    ON DELETE RESTRICT
);

CREATE TABLE capabilities (
  pk BIGINT NOT NULL AUTO_INCREMENT,
  created_time DATETIME NOT NULL,
  updated_time DATETIME NOT NULL,
  expiration_time DATETIME,
  revoked_time DATETIME,
  description VARCHAR(255),
  secret VARCHAR(255) CHARACTER SET latin1 COLLATE latin1_bin,
  user_fk BIGINT NOT NULL,

  PRIMARY KEY (pk),
  INDEX secret_index (secret),
  INDEX user_fk_index (user_fk),

  UNIQUE secret_unique (secret),

  FOREIGN KEY (user_fk)
    REFERENCES users(pk)
    ON DELETE RESTRICT
)
