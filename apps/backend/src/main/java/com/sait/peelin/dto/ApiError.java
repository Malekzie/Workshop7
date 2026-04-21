// Contributor(s): Robbie
// Main: Robbie - Standard API error envelope for clients.

package com.sait.peelin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(name = "ApiError", description = "JSON error envelope returned from global exception handling.")
public record ApiError(
        @Schema(description = "HTTP status code.") int status,
        @Schema(description = "Primary human readable error text.") String message,
        @Schema(description = "Optional field level or nested validation messages.") List<String> details,
        @Schema(description = "Server instant when the error was generated.") OffsetDateTime timestamp
) {}
