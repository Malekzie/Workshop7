package com.sait.peelin.service;

import com.sait.peelin.dto.v1.CheckoutRequest;
import com.sait.peelin.dto.v1.CheckoutSessionResponse;
import com.sait.peelin.model.*;
import com.sait.peelin.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.model.PaymentIntent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private RewardRepository rewardRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private BakeryRepository bakeryRepository;
    @Mock private ProductRepository productRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private BatchRepository batchRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private CurrentUserService currentUserService;
    @Mock private StripeService stripeService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Customer customer;
    private Bakery bakery;
    private Product product;
    private RewardTier rewardTier;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(UUID.randomUUID());
        user.setUserRole(UserRole.customer);

        rewardTier = new RewardTier();
        rewardTier.setRewardTierDiscountRate(BigDecimal.valueOf(10));

        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setUser(user);
        customer.setRewardTier(rewardTier);
        customer.setCustomerRewardBalance(100);

        bakery = new Bakery();
        bakery.setId(1);

        product = new Product();
        product.setId(101);
        product.setProductName("Test Bread");
        product.setProductBasePrice(BigDecimal.valueOf(5.0));
    }

    @Test
    void checkout_SuccessfulCustomerOrder() {
        // Arrange
        when(currentUserService.requireUser()).thenReturn(user);
        when(customerRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.of(customer));
        when(bakeryRepository.findById(1)).thenReturn(Optional.of(bakery));
        when(productRepository.findById(101)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(UUID.randomUUID());
            when(orderRepository.findById(o.getId())).thenReturn(Optional.of(o));
            return o;
        });

        PaymentIntent mockIntent = mock(PaymentIntent.class);
        try {
            when(stripeService.createPaymentIntent(any(UUID.class), any(BigDecimal.class))).thenReturn(mockIntent);
            when(mockIntent.getId()).thenReturn("pi_test_id");
            when(mockIntent.getClientSecret()).thenReturn("pi_test_secret");
        } catch (Exception ignored) {}

        CheckoutRequest req = new CheckoutRequest();
        req.setBakeryId(1);
        req.setOrderMethod(OrderMethod.pickup);
        req.setPaymentMethod(PaymentMethod.credit_card);

        CheckoutRequest.CheckoutLineRequest line = new CheckoutRequest.CheckoutLineRequest();
        line.setProductId(101);
        line.setQuantity(2);
        req.setItems(List.of(line));

        // Act
        CheckoutSessionResponse result = orderService.checkout(req);

        // Assert
        assertNotNull(result);
        assertNotNull(result.orderId());
        assertNotNull(result.orderNumber());
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(orderItemRepository, atLeastOnce()).save(any(OrderItem.class));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void checkout_FailsIfDeliveryMissingAddress() {
        // Arrange
        when(currentUserService.requireUser()).thenReturn(user);
        when(customerRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.of(customer));
        when(bakeryRepository.findById(1)).thenReturn(Optional.of(bakery));

        CheckoutRequest req = new CheckoutRequest();
        req.setBakeryId(1);
        req.setOrderMethod(OrderMethod.delivery); // Delivery!
        req.setAddressId(null); // Missing!

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> orderService.checkout(req));
    }

    @Test
    void checkout_StaffPlacingOrderForCustomer() {
        // Arrange
        User admin = new User();
        admin.setUserId(UUID.randomUUID());
        admin.setUserRole(UserRole.admin);

        when(currentUserService.requireUser()).thenReturn(admin);
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(bakeryRepository.findById(1)).thenReturn(Optional.of(bakery));
        when(productRepository.findById(101)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(UUID.randomUUID());
            when(orderRepository.findById(o.getId())).thenReturn(Optional.of(o));
            return o;
        });

        PaymentIntent mockIntent = mock(PaymentIntent.class);
        try {
            when(stripeService.createPaymentIntent(any(UUID.class), any(BigDecimal.class))).thenReturn(mockIntent);
            when(mockIntent.getId()).thenReturn("pi_test_id");
            when(mockIntent.getClientSecret()).thenReturn("pi_test_secret");
        } catch (Exception ignored) {}

        CheckoutRequest req = new CheckoutRequest();
        req.setBakeryId(1);
        req.setCustomerId(customer.getId()); // Providing customer ID
        req.setOrderMethod(OrderMethod.pickup);
        req.setPaymentMethod(PaymentMethod.cash);

        CheckoutRequest.CheckoutLineRequest line = new CheckoutRequest.CheckoutLineRequest();
        line.setProductId(101);
        line.setQuantity(1);
        req.setItems(List.of(line));

        // Act
        CheckoutSessionResponse result = orderService.checkout(req);

        // Assert
        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }
}
