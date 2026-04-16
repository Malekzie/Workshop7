package com.sait.peelin.service;

import com.sait.peelin.dto.v1.DataPointDto;
import com.sait.peelin.model.User;
import com.sait.peelin.model.UserRole;
import com.sait.peelin.repository.EmployeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private static final String ALL_BAKERIES = "All Bakeries";
    private static final String ALL_MY_BAKERIES = "All My Bakeries";

    @PersistenceContext
    private EntityManager entityManager;

    private final CurrentUserService currentUserService;
    private final EmployeeRepository employeeRepository;

    private static final String REVENUE_STATUS =
            " o.order_status::text IN ('completed', 'delivered')";

    private static final String IN_PROGRESS_STATUS =
            " o.order_status::text IN ('pending', 'paid', 'processing', 'ready', 'scheduled', 'out for delivery')";

    public String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private List<Integer> resolveBakeryScope() {
        User u = currentUserService.requireUser();
        if (u.getUserRole() == UserRole.admin) {
            return null;
        }
        return employeeRepository.findByUser_UserId(u.getUserId())
                .map(e -> List.of(e.getBakery().getId()))
                .orElse(List.of());
    }

    private void applyScopeAndBakeryName(StringBuilder sql, List<Object> params,
                                         LocalDate start, LocalDate end,
                                         String bakerySelection, List<Integer> scope) {
        if (scope != null && !scope.isEmpty()) {
            sql.append(" AND o.bakery_id IN (");
            for (int i = 0; i < scope.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append("?");
                params.add(scope.get(i));
            }
            sql.append(") ");
        }
        if (start != null) {
            sql.append(" AND CAST(o.order_placed_datetime AS date) >= ? ");
            params.add(start);
        }
        if (end != null) {
            sql.append(" AND CAST(o.order_placed_datetime AS date) <= ? ");
            params.add(end);
        }
        if (bakerySelection != null
                && !bakerySelection.isBlank()
                && !ALL_BAKERIES.equals(bakerySelection)
                && !ALL_MY_BAKERIES.equals(bakerySelection)) {
            sql.append(" AND b.bakery_name = ? ");
            params.add(bakerySelection);
        }
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':totalRevenue:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    public BigDecimal totalRevenue(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return BigDecimal.ZERO;
        }
        StringBuilder sql = new StringBuilder("""
            SELECT COALESCE(SUM(o.order_total), 0)
            FROM "order" o
            JOIN bakery b ON b.bakery_id = o.bakery_id
            WHERE """).append(REVENUE_STATUS);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        return executeSingleBigDecimal(sql.toString(), params);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':revenueOverTime:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<DataPointDto> revenueOverTime(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("""
                SELECT CAST(o.order_placed_datetime AS date) AS d,
                       COALESCE(SUM(o.order_total), 0)
                FROM "order" o
                JOIN bakery b ON b.bakery_id = o.bakery_id
                WHERE """).append(REVENUE_STATUS);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        sql.append(" GROUP BY 1 ORDER BY 1 ");
        return queryDataPoints(sql.toString(), params, true);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':revenueByBakery:' + #start + ':' + #end")
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<DataPointDto> revenueByBakery(LocalDate start, LocalDate end) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("""
                SELECT b.bakery_name, COALESCE(SUM(o.order_total), 0)
                FROM "order" o
                JOIN bakery b ON b.bakery_id = o.bakery_id
                WHERE """).append(REVENUE_STATUS);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, null, scope);
        sql.append(" GROUP BY b.bakery_id, b.bakery_name ORDER BY 2 DESC ");
        return queryDataPoints(sql.toString(), params, false);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':averageOrderValue:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    public BigDecimal averageOrderValue(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return BigDecimal.ZERO;
        }
        StringBuilder sql = new StringBuilder("""
                SELECT COALESCE(AVG(o.order_total), 0)
                FROM "order" o
                JOIN bakery b ON b.bakery_id = o.bakery_id
                WHERE """).append(REVENUE_STATUS);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        return executeSingleBigDecimal(sql.toString(), params);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':averageOrderValueOverTime:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<DataPointDto> averageOrderValueOverTime(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("""
                SELECT CAST(o.order_placed_datetime AS date) AS d,
                       COALESCE(AVG(o.order_total), 0)
                FROM "order" o
                JOIN bakery b ON b.bakery_id = o.bakery_id
                WHERE """).append(REVENUE_STATUS);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        sql.append(" GROUP BY 1 ORDER BY 1 ");
        return queryDataPoints(sql.toString(), params, true);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':completionRate:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    public BigDecimal completionRate(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return BigDecimal.ZERO;
        }
        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(
            (
                SUM(CASE WHEN o.order_status::text = 'completed' THEN 1 ELSE 0 END)::numeric
                / NULLIF(
                    SUM(CASE WHEN o.order_status::text <> 'cancelled' THEN 1 ELSE 0 END),
                    0
                ) * 100.0
            ),
            0
        )
        FROM "order" o
        JOIN bakery b ON b.bakery_id = o.bakery_id
        WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        return executeSingleBigDecimal(sql.toString(), params);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':completionRateOverTime:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<DataPointDto> completionRateOverTime(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("""
        SELECT CAST(o.order_placed_datetime AS date) AS d,
               COALESCE(
                   (
                       SUM(CASE WHEN o.order_status::text = 'completed' THEN 1 ELSE 0 END)::numeric
                       / NULLIF(
                           SUM(CASE WHEN o.order_status::text <> 'cancelled' THEN 1 ELSE 0 END),
                           0
                       ) * 100.0
                   ),
                   0
               ) AS rate
        FROM "order" o
        JOIN bakery b ON b.bakery_id = o.bakery_id
        WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        sql.append(" GROUP BY 1 ORDER BY 1 ");
        return queryDataPoints(sql.toString(), params, true);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':topProducts:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<DataPointDto> topProducts(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("""
                SELECT p.product_name,
                       COALESCE(SUM(oi.order_item_quantity), 0)
                FROM order_item oi
                JOIN product p ON p.product_id = oi.product_id
                JOIN "order" o ON o.order_id = oi.order_id
                JOIN bakery b ON b.bakery_id = o.bakery_id
                WHERE """).append(REVENUE_STATUS);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        sql.append(" GROUP BY p.product_id, p.product_name ORDER BY 2 DESC LIMIT 10 ");
        return queryDataPoints(sql.toString(), params, false);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':totalSalesByEmployee:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    public BigDecimal totalSalesByEmployee(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<OrderEmployeeAllocationRow> rows = employeeAllocationRows(start, end, bakerySelection, scope, REVENUE_STATUS);
        return totalAllocatedRevenue(rows);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':salesByEmployee:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    public List<DataPointDto> salesByEmployee(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }

        List<OrderEmployeeAllocationRow> rows = employeeAllocationRows(start, end, bakerySelection, scope, REVENUE_STATUS);
        return allocatedRevenueByEmployee(rows);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':bakeryNames'")
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<String> bakeryNames() {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }
        String sql;
        List<Object> params = new ArrayList<>();
        if (scope == null) {
            sql = "SELECT bakery_name FROM bakery ORDER BY bakery_name";
        } else {
            StringBuilder sb = new StringBuilder("SELECT bakery_name FROM bakery WHERE bakery_id IN (");
            for (int i = 0; i < scope.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append("?");
                params.add(scope.get(i));
            }
            sb.append(") ORDER BY bakery_name");
            sql = sb.toString();
        }
        Query q = entityManager.createNativeQuery(sql);
        bindParams(q, params);
        List<Object> rows = q.getResultList();
        List<String> out = new ArrayList<>();
        for (Object row : rows) {
            out.add((String) row);
        }
        return out;
    }

    //@Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':orderDatesInRange:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<LocalDate> orderDatesInRange(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT CAST(o.order_placed_datetime AS date) AS d
            FROM "order" o
            JOIN bakery b ON b.bakery_id = o.bakery_id
            WHERE o.order_status::text <> 'cancelled'
        """);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        sql.append(" ORDER BY 1 ");
        Query q = entityManager.createNativeQuery(sql.toString());
        bindParams(q, params);
        List<Object> dates = q.getResultList();
        List<LocalDate> out = new ArrayList<>();
        for (Object d : dates) {
            if (d instanceof Date dt) {
                out.add(dt.toLocalDate());
            }
        }
        return out;
    }

    private List<OrderEmployeeAllocationRow> employeeAllocationRows(LocalDate start,
                                                                    LocalDate end,
                                                                    String bakerySelection,
                                                                    List<Integer> scope,
                                                                    String statusClause) {
        StringBuilder sql = new StringBuilder("""
                SELECT o.order_id,
                       CAST(e.employee_id AS text),
                       TRIM(CONCAT(e.employee_first_name, ' ', e.employee_last_name)),
                       COALESCE(o.order_total, 0)
                FROM order_item oi
                JOIN "order" o ON o.order_id = oi.order_id
                JOIN bakery b ON b.bakery_id = o.bakery_id
                JOIN batch ba ON oi.batch_id = ba.batch_id
                JOIN employee e ON ba.employee_id = e.employee_id
                WHERE """).append(statusClause);

        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        sql.append("""
                 GROUP BY o.order_id, e.employee_id, e.employee_first_name, e.employee_last_name, o.order_total
                 ORDER BY o.order_id, e.employee_first_name, e.employee_last_name
                """);

        Query q = entityManager.createNativeQuery(sql.toString());
        bindParams(q, params);

        @SuppressWarnings("unchecked")
        List<Object[]> rawRows = q.getResultList();

        List<OrderEmployeeAllocationRow> rows = new ArrayList<>();
        for (Object[] row : rawRows) {
            String orderId = row[0] != null ? row[0].toString() : "";
            String employeeId = row[1] != null ? row[1].toString() : "";
            String employeeName = row[2] != null ? row[2].toString() : "";
            BigDecimal orderTotal = row[3] instanceof BigDecimal b
                    ? b
                    : BigDecimal.valueOf(((Number) row[3]).doubleValue());

            rows.add(new OrderEmployeeAllocationRow(orderId, employeeId, employeeName, orderTotal));
        }
        return rows;
    }

    private BigDecimal totalAllocatedRevenue(List<OrderEmployeeAllocationRow> rows) {
        Map<String, OrderAllocationBucket> byOrder = bucketByOrder(rows);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderAllocationBucket bucket : byOrder.values()) {
            if (!bucket.employees.isEmpty()) {
                total = total.add(bucket.orderTotal);
            }
        }
        return total;
    }

    private List<DataPointDto> allocatedRevenueByEmployee(List<OrderEmployeeAllocationRow> rows) {
        Map<String, OrderAllocationBucket> byOrder = bucketByOrder(rows);
        Map<String, BigDecimal> totals = new LinkedHashMap<>();

        for (OrderAllocationBucket bucket : byOrder.values()) {
            int employeeCount = bucket.employees.size();
            if (employeeCount == 0) continue;

            BigDecimal splitAmount = bucket.orderTotal.divide(
                    BigDecimal.valueOf(employeeCount),
                    6,
                    RoundingMode.HALF_UP
            );

            for (String employeeName : bucket.employees.values()) {
                totals.merge(employeeName, splitAmount, BigDecimal::add);
            }
        }

        List<Map.Entry<String, BigDecimal>> entries = new ArrayList<>(totals.entrySet());
        entries.sort((a, b) -> {
            int valueCompare = b.getValue().compareTo(a.getValue());
            if (valueCompare != 0) return valueCompare;
            return a.getKey().compareToIgnoreCase(b.getKey());
        });

        List<DataPointDto> out = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : entries) {
            out.add(new DataPointDto(entry.getKey(), entry.getValue()));
        }
        return out;
    }

    private Map<String, OrderAllocationBucket> bucketByOrder(List<OrderEmployeeAllocationRow> rows) {
        Map<String, OrderAllocationBucket> byOrder = new LinkedHashMap<>();

        for (OrderEmployeeAllocationRow row : rows) {
            OrderAllocationBucket bucket = byOrder.computeIfAbsent(
                    row.orderId,
                    ignored -> new OrderAllocationBucket(row.orderTotal)
            );

            if (bucket.orderTotal == null) {
                bucket.orderTotal = row.orderTotal;
            }

            bucket.employees.putIfAbsent(row.employeeId, row.employeeName);
        }

        return byOrder;
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':inProgressRevenue:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    public BigDecimal inProgressRevenue(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return BigDecimal.ZERO;
        }
        StringBuilder sql = new StringBuilder("""
                SELECT COALESCE(SUM(o.order_total), 0)
                FROM "order" o
                JOIN bakery b ON b.bakery_id = o.bakery_id
                WHERE """).append(IN_PROGRESS_STATUS);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        return executeSingleBigDecimal(sql.toString(), params);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':inProgressRevenueOverTime:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    public List<DataPointDto> inProgressRevenueOverTime(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("""
                SELECT CAST(o.order_placed_datetime AS date) AS d,
                       COALESCE(SUM(o.order_total), 0)
                FROM "order" o
                JOIN bakery b ON b.bakery_id = o.bakery_id
                WHERE """).append(IN_PROGRESS_STATUS);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, bakerySelection, scope);
        sql.append(" GROUP BY 1 ORDER BY 1 ");
        return queryDataPoints(sql.toString(), params, true);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':inProgressRevenueByBakery:' + #start + ':' + #end")
    @Transactional(readOnly = true)
    public List<DataPointDto> inProgressRevenueByBakery(LocalDate start, LocalDate end) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }
        StringBuilder sql = new StringBuilder("""
                SELECT b.bakery_name, COALESCE(SUM(o.order_total), 0)
                FROM "order" o
                JOIN bakery b ON b.bakery_id = o.bakery_id
                WHERE """).append(IN_PROGRESS_STATUS);
        List<Object> params = new ArrayList<>();
        applyScopeAndBakeryName(sql, params, start, end, null, scope);
        sql.append(" GROUP BY b.bakery_id, b.bakery_name ORDER BY 2 DESC ");
        return queryDataPoints(sql.toString(), params, false);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':inProgressTotalSalesByEmployee:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    public BigDecimal inProgressTotalSalesByEmployee(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<OrderEmployeeAllocationRow> rows = employeeAllocationRows(start, end, bakerySelection, scope, IN_PROGRESS_STATUS);
        return totalAllocatedRevenue(rows);
    }

    @Cacheable(value = "analytics", key = "#root.target.currentUsername() + ':inProgressSalesByEmployee:' + #start + ':' + #end + ':' + #bakerySelection")
    @Transactional(readOnly = true)
    public List<DataPointDto> inProgressSalesByEmployee(LocalDate start, LocalDate end, String bakerySelection) {
        List<Integer> scope = resolveBakeryScope();
        if (scope != null && scope.isEmpty()) {
            return List.of();
        }

        List<OrderEmployeeAllocationRow> rows = employeeAllocationRows(start, end, bakerySelection, scope, IN_PROGRESS_STATUS);
        return allocatedRevenueByEmployee(rows);
    }

    private BigDecimal executeSingleBigDecimal(String sql, List<Object> params) {
        Query q = entityManager.createNativeQuery(sql);
        bindParams(q, params);
        Object v = q.getSingleResult();
        if (v instanceof BigDecimal b) {
            return b;
        }
        if (v instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        return BigDecimal.ZERO;
    }

    private List<DataPointDto> queryDataPoints(String sql, List<Object> params, boolean dateLabel) {
        Query q = entityManager.createNativeQuery(sql);
        bindParams(q, params);
        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        List<DataPointDto> out = new ArrayList<>();
        for (Object[] row : rows) {
            String label;
            if (dateLabel && row[0] instanceof Date d) {
                label = d.toLocalDate().toString();
            } else {
                label = row[0] != null ? row[0].toString() : "";
            }
            BigDecimal val = row[1] instanceof BigDecimal b ? b
                    : BigDecimal.valueOf(((Number) row[1]).doubleValue());
            out.add(new DataPointDto(label, val));
        }
        return out;
    }

    private void bindParams(Query q, List<Object> params) {
        for (int i = 0; i < params.size(); i++) {
            q.setParameter(i + 1, params.get(i));
        }
    }

    private static final class OrderEmployeeAllocationRow {
        private final String orderId;
        private final String employeeId;
        private final String employeeName;
        private final BigDecimal orderTotal;

        private OrderEmployeeAllocationRow(String orderId, String employeeId, String employeeName, BigDecimal orderTotal) {
            this.orderId = orderId;
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.orderTotal = orderTotal;
        }
    }

    private static final class OrderAllocationBucket {
        private BigDecimal orderTotal;
        private final Map<String, String> employees = new LinkedHashMap<>();

        private OrderAllocationBucket(BigDecimal orderTotal) {
            this.orderTotal = orderTotal;
        }
    }
}