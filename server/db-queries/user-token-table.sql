CREATE TABLE user_token (
  `user_id` mediumint(8) unsigned NOT NULL,
  `token` VARCHAR(250) NOT NULL,
  `refreshed_on` DATETIME(6) NULL,
  UNIQUE (`user_id`),
  FOREIGN KEY (`user_id`)
  REFERENCES users(id)
  ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;
