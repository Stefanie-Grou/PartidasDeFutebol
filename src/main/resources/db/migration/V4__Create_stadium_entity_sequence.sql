CREATE TABLE stadiums_seq (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          next_val BIGINT NOT NULL,
                          PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO stadiums_seq (next_val) VALUES (1);