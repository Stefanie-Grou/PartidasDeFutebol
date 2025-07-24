package com.example.partidasdefutebol.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class StadiumEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long stadiumId;

    @NotBlank
    @Getter
    @Setter
    private String stadiumName;

    @NotBlank
    @Getter
    @Setter
    private String stadiumState;

    @NotBlank
    @Getter
    @Setter
    private String city;

    @Getter
    @Setter
    private String street;

    @Getter
    @Setter
    @NotBlank
    private String cep;
}
