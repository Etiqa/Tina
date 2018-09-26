CREATE TABLE users (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(250),
  `password` VARCHAR(250),
  `reset_pwd_token` VARCHAR(250) NULL,
  `reset_pwd_dt` DATETIME(6) NULL,
  `creation_date` DATETIME(6) NULL,
  `last_login` DATETIME(6) NULL,
  `ip` VARCHAR(40) NULL,
  PRIMARY KEY (`id`),
  UNIQUE(`email`)
) ENGINE=InnoDB;
