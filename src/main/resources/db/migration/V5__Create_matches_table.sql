CREATE TABLE matches (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         home_club_id BIGINT,
                         away_club_id BIGINT,
                         home_club_number_of_goals BIGINT,
                         away_club_number_of_goals BIGINT,
                         stadium_id BIGINT,
                         match_date DATETIME,
                         PRIMARY KEY (id),
                         FOREIGN KEY (home_club_id) REFERENCES club(id),
                         FOREIGN KEY (away_club_id) REFERENCES club(id),
                         FOREIGN KEY (stadium_id) REFERENCES stadium(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;