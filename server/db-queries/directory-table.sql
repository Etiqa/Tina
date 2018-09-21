CREATE TABLE directories (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `j` JSON DEFAULT NULL,
  CHECK (JSON_VALID(j)),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
