package com.example.partidasdefutebol.repository;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "stadium", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"stadiumName", "stadiumState"})})
public class Stadium {
    @Id
    @GeneratedValue
    private Long stadiumId;
    @NotBlank
    @Size(min = 3, max = 255)
    private String stadiumName;
    @NotBlank
    private String stadiumState;

    public Stadium(String stadiumName, String stadiumState) {
        this.stadiumName = stadiumName;
        this.stadiumState = stadiumState;
    }

    public Stadium() {}

    public Long getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(Long stadiumId) {
        this.stadiumId = stadiumId;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public String getStadiumState() {
        return stadiumState;
    }

    public void setStadiumState(String stadiumState) {
        this.stadiumState = stadiumState;
    }

    @Override
    public String toString() {
        return "Stadium{" +
                "name='" + stadiumName + '\'' +
                '}';
    }
}
