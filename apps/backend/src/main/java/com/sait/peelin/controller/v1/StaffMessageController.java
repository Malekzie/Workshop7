// Contributor(s): Robbie
// Main: Robbie - Staff-only direct messaging between employees and admins.

package com.sait.peelin.controller.v1;

import com.sait.peelin.dto.v1.SendStaffMessageRequest;
import com.sait.peelin.dto.v1.StaffConversationDto;
import com.sait.peelin.dto.v1.StaffMessageDto;
import com.sait.peelin.dto.v1.StaffRecipientDto;
import com.sait.peelin.dto.v1.StartConversationRequest;
import com.sait.peelin.service.StaffMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Staff-only messaging under {@code /api/v1/messages} for conversations between employees.
 */
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
@Tag(name = "Staff Messages", description = "Staff-to-staff direct messaging.")
@SecurityRequirement(name = "bearer-jwt")
public class StaffMessageController {

    private final StaffMessageService staffMessageService;

    @Operation(summary = "List staff conversations", description = "Returns inbox threads for the signed-in staff member ordered by recent activity.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversations returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not staff", content = @Content)
    })
    @GetMapping("/conversations")
    public List<StaffConversationDto> conversations() {
        return staffMessageService.conversations();
    }

    @Operation(summary = "List staff message recipients", description = "Returns other admins and employees the caller may start a direct thread with.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recipients returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not staff", content = @Content)
    })
    @GetMapping("/recipients")
    public List<StaffRecipientDto> recipients() {
        return staffMessageService.listRecipients();
    }

    @Operation(summary = "Start or open staff conversation", description = "Finds an existing pairwise thread or creates one with the given recipient.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conversation returned"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not staff", content = @Content)
    })
    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public StaffConversationDto startConversation(@Valid @RequestBody StartConversationRequest req) {
        return staffMessageService.getOrCreateConversation(req.recipientId());
    }

    @Operation(summary = "List messages in staff conversation", description = "Returns chronological messages for a conversation the caller belongs to.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Messages returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not staff or not a participant", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conversation not found", content = @Content)
    })
    @GetMapping("/conversations/{convoId}/messages")
    public List<StaffMessageDto> messages(
            @Parameter(description = "Conversation id", example = "1")
            @PathVariable Integer convoId) {
        return staffMessageService.messages(convoId);
    }

    @Operation(summary = "Send staff message", description = "Appends a message to the thread when the caller is a participant.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message stored"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not staff or not a participant", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conversation not found", content = @Content)
    })
    @PostMapping("/conversations/{convoId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public StaffMessageDto send(
            @Parameter(description = "Conversation id", example = "1")
            @PathVariable Integer convoId,
            @Valid @RequestBody SendStaffMessageRequest req) {
        return staffMessageService.sendMessage(convoId, req.text());
    }

    @Operation(summary = "Mark staff conversation read", description = "Updates read pointers for the caller on the given thread.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Read state updated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not staff or not a participant", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conversation not found", content = @Content)
    })
    @PostMapping("/conversations/{convoId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(
            @Parameter(description = "Conversation id", example = "1")
            @PathVariable Integer convoId) {
        staffMessageService.markRead(convoId);
    }
}
