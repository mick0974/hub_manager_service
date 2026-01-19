package com.sch.hub_manager_service.hub.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@Schema(name = "ApiError", description = "Rappresenta un errore restituito dalle API, inclusi eventuali errori di validazione")
public class ApiError {

    @Schema(description = "Codice HTTP dell'errore")
    private int code;

    @Schema(description = "Descrizione breve dell'errore HTTP")
    private String error;

    @Schema(description = "Messaggio dettagliato dell'errore")
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp in cui si è verificato l'errore",
            type = "string",
            format = "date-time")
    private Timestamp timestamp;

    @Schema(description = "Lista degli errori di validazione sui campi del DTO (presente solo in caso di errori di validazione)",
            nullable = true)
    private List<ValidationError> validationErrors;

    public ApiError(int code, String error, String message, Timestamp timestamp) {
        this.code = code;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ApiError(
            int code,
            String error,
            String message,
            Timestamp timestamp,
            List<ValidationError> validationErrors
    ) {
        this.code = code;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.validationErrors = validationErrors;
    }

    @Getter
    @Builder
    @Schema(name = "ValidationError", description = "Errore di validazione relativo a un singolo campo")
    public static class ValidationError {

        @Schema(description = "Nome del campo che ha causato l'errore")
        private String field;

        @Schema(description = "Valore rifiutato durante la validazione")
        private Object rejectedValue;

        @Schema(description = "Messaggio di errore associato al campo")
        private String message;
    }
}
