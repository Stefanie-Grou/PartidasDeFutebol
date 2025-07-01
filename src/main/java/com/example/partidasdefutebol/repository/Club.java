package com.example.partidasdefutebol.repository;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import static com.example.partidasdefutebol.dto.BrazilianStates.isValidBrazilianState;

@Entity
@Table(name = "club", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"clubName", "stateAcronym"})
})
public class Club {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String clubName;
    @NotBlank
    @Size(min = 2, max = 2)
    private String stateAcronym;
    @PastOrPresent
    private LocalDate createdOn;

    private Boolean isActive;

    public Club(String clubName, String stateAcronym, LocalDate createdOn, Boolean isActive) {
        this.clubName = clubName;
        this.stateAcronym = stateAcronym;
        this.createdOn = createdOn;
        this.isActive = isActive;
    }

    public Club() {}

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getStateAcronym() {
        return stateAcronym;
    }

    public void setStateAcronym(String stateAcronym) {
        if (isValidBrazilianState(stateAcronym)) {
            this.stateAcronym = stateAcronym;
        }
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Club{" +
                "clubName='" + clubName + '\'' +
                ", stateAcronym='" + stateAcronym + '\'' +
                ", createdOn=" + createdOn +
                ", isActive=" + isActive +
                '}';
    }
}
