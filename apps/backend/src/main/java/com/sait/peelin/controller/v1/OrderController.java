// Contributor(s): Samantha
// Main: Samantha - Order placement checkout resume Stripe handoff and order lookups.

package com.sait.peelin.controller.v1;

import com.sait.peelin.dto.v1.*;
import com.sait.peelin.service.OrderService;
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
import java.util.UUID;

/**
 * Order lifecycle and checkout APIs under {@code /api/v1/orders}.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Guest checkout plus signed-in order history. Staff status edits need ADMIN or EMPLOYEE role.")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "List my orders", description = "Returns all orders belonging to the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping
    public List<OrderDto> list() {
        return orderService.listForCurrentUser();
    }

    @Operation(summary = "Get order", description = "Returns a single order by ID. Customers may only retrieve their own orders.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Order belongs to another customer", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/{id}")
    public OrderDto get(@PathVariable UUID id) {
        return orderService.get(id);
    }

    @Operation(summary = "Get order by order number", description = "Returns an order by its public order number. Guests send the checkout email as the email query parameter. Wrong email returns 404 so strangers cannot probe orders. Signed-in customers must own the row.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found or email did not match", content = @Content)
    })
    @GetMapping("/by-number/{orderNumber}")
    public OrderDto getByOrderNumber(
            @PathVariable String orderNumber,
            @Parameter(description = "Contact email used at checkout. Required for guest lookups and ignored for staff.")
            @RequestParam(required = false) String email) {
        return orderService.getByOrderNumber(orderNumber, email);
    }

    @Operation(summary = "Checkout", description = "Places a new order from the posted cart lines plus delivery method and bakery context. Guests may call without a token.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or insufficient stock", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CheckoutSessionResponse checkout(@Valid @RequestBody CheckoutRequest req) {
        return orderService.checkout(req);
    }

    @Operation(summary = "Confirm Stripe payment", description = "Verifies the PaymentIntent succeeded and marks the order paid. Call after Payment Sheet completes. Helps local dev when webhooks are not wired.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order returned with updated status"),
            @ApiResponse(responseCode = "400", description = "Intent does not match order", content = @Content),
            @ApiResponse(responseCode = "403", description = "Logged-in customer does not own this order", content = @Content),
            @ApiResponse(responseCode = "409", description = "Payment not succeeded at Stripe", content = @Content)
    })
    @PostMapping("/{id}/confirm-stripe-payment")
    public OrderDto confirmStripePayment(
            @PathVariable UUID id,
            @Valid @RequestBody ConfirmStripePaymentRequest body) {
        return orderService.confirmStripePayment(id, body);
    }

    @Operation(summary = "Resume Stripe payment", description = "For pending_payment orders returns a PaymentIntent client secret to reopen Payment Sheet or marks paid when Stripe already captured funds.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client secret to present the sheet, or order already paid"),
            @ApiResponse(responseCode = "400", description = "Order not awaiting payment", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not allowed to view this order", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/{id}/resume-stripe-payment")
    public ResumePaymentSessionResponse resumeStripePayment(@PathVariable UUID id) {
        return orderService.resumeStripePayment(id);
    }

    @Operation(summary = "Update order status", description = "Changes workflow status on an order for example from PREPARING to READY. Needs ADMIN or EMPLOYEE role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition", content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PatchMapping("/{id}/status")
    public OrderDto patchStatus(@PathVariable UUID id, @Valid @RequestBody OrderStatusPatchRequest req) {
        return orderService.updateStatus(id, req);
    }

    @Operation(summary = "Mark order delivered", description = "Marks an order delivered and may store delivery notes. Needs ADMIN or EMPLOYEE role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order marked as delivered"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @PatchMapping("/{id}/delivered")
    public OrderDto markDelivered(@PathVariable UUID id, @RequestBody(required = false) OrderDeliveredPatchRequest req) {
        return orderService.markDelivered(id, req != null ? req : new OrderDeliveredPatchRequest());
    }

    @Operation(summary = "Accept delivery", description = "Customer accepts receipt of a delivery order when the workflow needs an explicit acknowledgement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order updated"),
            @ApiResponse(responseCode = "403", description = "Caller cannot modify this order", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PatchMapping("/{id}/accept-delivery")
    public OrderDto acceptDelivery(@PathVariable UUID id) {
        return orderService.acceptDelivery(id);
    }
}
