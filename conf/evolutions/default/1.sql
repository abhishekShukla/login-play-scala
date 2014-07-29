# --- !Ups

CREATE TABLE `user_identities` (
  `application_user_id` varchar(150) NOT NULL DEFAULT '',
  `provider_user_id` varchar(150) NOT NULL DEFAULT '',
  `provider` varchar(20) NOT NULL DEFAULT '',
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `avatar_url` varchar(150) DEFAULT NULL,
  `auth_method` varchar(20) DEFAULT NULL,
  `password_crypt` varchar(20) DEFAULT NULL,
  `password` varchar(150) DEFAULT NULL,
  `oauth1_token` varchar(150) DEFAULT NULL,
  `oauth1_secret` varchar(150) DEFAULT NULL,
  `oauth2_token` varchar(250) DEFAULT NULL,
  `oauth2_expiry` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`provider_user_id`,`provider`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `tokens` (
  `uuid` varchar(100) NOT NULL DEFAULT '',
  `email` varchar(200) NOT NULL DEFAULT '',
  `creationTime` bigint(20) NOT NULL,
  `expirationTime` bigint(20) NOT NULL,
  `isSignUp` tinyint(1) NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# --- !Downs

DROP TABLE IF EXISTS user_identities;

DROP TABLE IF EXISTS tokens;



