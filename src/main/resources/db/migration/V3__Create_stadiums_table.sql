CREATE TABLE stadium_entity (
                          stadium_id BIGINT NOT NULL AUTO_INCREMENT,
                          stadium_name VARCHAR(255) NOT NULL,
                          stadium_state VARCHAR(2) NOT NULL,
                          PRIMARY KEY (stadium_id),
                          UNIQUE KEY (stadium_name, stadium_state)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;