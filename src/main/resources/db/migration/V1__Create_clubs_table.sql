CREATE TABLE club_entity (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       club_name VARCHAR(255) NOT NULL,
                       state_acronym VARCHAR(2) NOT NULL,
                       created_on DATETIME,
                       is_active BOOLEAN,
                       PRIMARY KEY (id),
                       UNIQUE KEY (club_name, state_acronym)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;