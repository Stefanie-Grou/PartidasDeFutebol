package com.example.partidasdefutebol.dto;

import com.example.partidasdefutebol.entities.Club;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
public class QueueMessageDTO {
    @Getter
    @Setter
    @NotNull(message = "Nenhuma operação atrelada à mensagem. Operação cancelada.")
    @NotNull
    String operation;

    @Getter
    @Setter
    @NotNull(message = "Nenhuma mensagem atrelada à operação. Operação cancelada.")
    @NotNull
    Club message;

    @Getter
    @Setter
    Long id;


    @JsonCreator
    public QueueMessageDTO(
            @JsonProperty(value = "operation", required = true) String operation,
            @JsonProperty(value = "message", required = true) Club message,
            @JsonProperty(value = "id") Long id) {
        this.operation = operation;
        this.message = message;
        this.id = id;
    }
}
