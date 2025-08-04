CREATE TABLE club(
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       state_acronym VARCHAR(2) NOT NULL,
                       created_on DATE NOT NULL,
                       is_active BOOLEAN,
                       PRIMARY KEY (id),
                       UNIQUE KEY (name, state_acronym)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;