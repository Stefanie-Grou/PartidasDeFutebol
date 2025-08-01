CREATE TABLE stadium (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL,
                          state_acronym VARCHAR(2) NOT NULL,
                          cep VARCHAR(9) NOT NULL,
                          city VARCHAR(255) NOT NULL,
                          street VARCHAR(255) NOT NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY (name, state_acronym)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;