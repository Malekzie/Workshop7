// Contributor(s): Robbie
// Main: Robbie - Customer and staff support chat threads and messages REST API.

package com.sait.peelin.controller.v1;

import com.sait.peelin.dto.v1.ChatMessageDto;
import com.sait.peelin.dto.v1.ChatThreadDto;
import com.sait.peelin.dto.v1.PostChatMessageRequest;
import com.sait.peelin.repository.EmployeeSpecialtyRepository;
import com.sait.peelin.service.ChatService;
import com.sait.peelin.service.CurrentUserService;
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
 * HTTP resources for support chat under {@code /api/v1/chat}. Operations appear under the Chat tag in OpenAPI.
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Support chat between customers and staff.")
@SecurityRequirement(name = "bearer-jwt")
public class ChatRestController {

    private final ChatService chatService;
    private final CurrentUserService currentUserService;
    private final EmployeeSpecialtyRepository employeeSpecialtyRepository;

    record CreateThreadRequest(String category) {}

    @Operation(summary = "List open threads", description = "Open support threads with optional category filter.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Threads returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    @GetMapping("/threads")
    public List<ChatThreadDto> openThreads(
            @Parameter(description = "Optional category key such as general or order_issue")
            @RequestParam(required = false) String category) {
        return chatService.openThreads(category);
    }

    @Operation(summary = "List archived threads", description = "Closed threads for admin audit. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Threads returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    @GetMapping("/threads/archived")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ChatThreadDto> archivedThreads(
            @Parameter(description = "Optional category filter")
            @RequestParam(required = false) String category) {
        return chatService.archivedThreads(category);
    }

    @Operation(summary = "Create thread", description = "Starts a new support thread using the posted category or general when the body is empty.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Thread created"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    @PostMapping("/threads")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatThreadDto createThread(@RequestBody(required = false) CreateThreadRequest req) {
        String category = (req != null && req.category() != null) ? req.category() : "general";
        return chatService.createThread(category);
    }

    @Operation(summary = "My open thread", description = "Returns the open support thread for the signed-in customer when one exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thread returned or empty body when none"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    @GetMapping("/threads/me/open")
    public ChatThreadDto myOpenThread() {
        return chatService.getOpenThreadForCustomer();
    }

    @Operation(summary = "List thread messages", description = "Messages for a thread when the caller passes access checks.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Messages returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden for this thread", content = @Content),
            @ApiResponse(responseCode = "404", description = "Thread not found", content = @Content)
    })
    @GetMapping("/threads/{threadId}/messages")
    public List<ChatMessageDto> messages(@PathVariable Integer threadId) {
        return chatService.messages(threadId);
    }

    @Operation(summary = "Post thread message", description = "Appends a message when policy allows for the caller role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message stored"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden for this thread", content = @Content)
    })
    @PostMapping("/threads/{threadId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageDto post(@PathVariable Integer threadId,
                               @Valid @RequestBody PostChatMessageRequest req) {
        return chatService.postMessage(threadId, req);
    }

    @Operation(summary = "Assign thread", description = "Claims an open thread for the signed-in employee. Requires staff role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thread updated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Thread not found", content = @Content)
    })
    @PostMapping("/threads/{threadId}/assign")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ChatThreadDto assign(@PathVariable Integer threadId) {
        return chatService.assignEmployee(threadId);
    }

    @Operation(summary = "Mark thread read", description = "Clears unread state for the caller on this thread.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Read state updated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden for this thread", content = @Content)
    })
    @PostMapping("/threads/{threadId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable Integer threadId) {
        chatService.markRead(threadId);
    }

    @Operation(summary = "Close thread", description = "Marks the thread closed following access rules for the caller.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thread closed"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden for this thread", content = @Content),
            @ApiResponse(responseCode = "404", description = "Thread not found", content = @Content)
    })
    @PostMapping("/threads/{threadId}/close")
    public ChatThreadDto close(@PathVariable Integer threadId) {
        return chatService.closeThread(threadId);
    }

    record TransferRequest(java.util.UUID employeeUserId) {}

    @Operation(summary = "Transfer thread", description = "Reassigns the thread to another staff user id from the JSON body.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thread transferred"),
            @ApiResponse(responseCode = "400", description = "Missing employee user id", content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    @PostMapping("/threads/{threadId}/transfer")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public ChatThreadDto transfer(@PathVariable Integer threadId,
                                  @RequestBody TransferRequest req) {
        if (req == null || req.employeeUserId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "employeeUserId required");
        }
        return chatService.transferThread(threadId, req.employeeUserId());
    }

    @Operation(summary = "Reopen thread", description = "Moves a closed thread back to open. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thread reopened"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Thread not found", content = @Content)
    })
    @PostMapping("/threads/{threadId}/reopen")
    @PreAuthorize("hasRole('ADMIN')")
    public ChatThreadDto reopen(@PathVariable Integer threadId) {
        return chatService.reopenThread(threadId);
    }

    @Operation(summary = "My routing specialties", description = "Category tags linked to the signed-in staff member for chat routing.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Specialty labels returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content)
    })
    @GetMapping("/staff/me/specialties")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public List<String> mySpecialties() {
        com.sait.peelin.model.User me = currentUserService.requireUser();
        return employeeSpecialtyRepository.findByUserId(me.getUserId()).stream()
                .map(com.sait.peelin.model.EmployeeSpecialty::getCategory)
                .toList();
    }
}
