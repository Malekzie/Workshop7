// Contributor(s): Robbie
// Main: Robbie - Admin or dashboard JSON DTO for staff tools.

package com.sait.peelin.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "DashboardSummaryDto", description = "High level KPI block plus recent orders for the staff home view.")
public record DashboardSummaryDto(
        @Schema(description = "Rolling revenue total for the dashboard window.") BigDecimal totalRevenue,
        @Schema(description = "Order count for the dashboard window.") long totalOrders,
        @Schema(description = "Customer count snapshot.") long totalCustomers,
        @Schema(description = "Active catalog product count.") long totalProducts,
        @Schema(description = "Latest orders for the activity strip.") List<OrderDto> recentOrders
) {}
