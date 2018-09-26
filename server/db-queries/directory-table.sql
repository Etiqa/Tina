CREATE TABLE directories (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` mediumint(8) unsigned NOT NULL,
  `j` JSON DEFAULT NULL,
  CHECK (JSON_VALID(j)),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`)
  REFERENCES users(id)
  ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;
