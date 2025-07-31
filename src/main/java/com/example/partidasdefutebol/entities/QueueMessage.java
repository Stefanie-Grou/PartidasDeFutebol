package com.example.partidasdefutebol.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class QueueMessage {
    @Getter
    @Setter
    @NotNull(message = "Nenhuma operação atrelada ã mensagem. Operação cancelada.")
    String operation;

    @Getter
    @Setter
    ClubEntity message;

    @Getter
    @Setter
    Long id;

    @JsonCreator
    public QueueMessage(
            @JsonProperty("operation") String operation,
            @JsonProperty("message") ClubEntity message,
            @JsonProperty("id") Long id) {
        this.operation = operation;
        this.message = message;
        this.id = id;
    }
}
