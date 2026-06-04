//
//
//
//package com.fooddelivery.orderservice.serviceimpl;
//
//import com.fooddelivery.orderservice.client.CustomerClient;
//import com.fooddelivery.orderservice.client.MenuClient;
//import com.fooddelivery.orderservice.dto.request.OrderItemRequestDto;
//import com.fooddelivery.orderservice.dto.request.OrderRequestDto;
//import com.fooddelivery.orderservice.dto.response.CustomerResponseDto;
//import com.fooddelivery.orderservice.dto.response.MenuResponseDto;
//import com.fooddelivery.orderservice.dto.response.OrderItemResponseDto;
//import com.fooddelivery.orderservice.dto.response.OrderResponseDto;
//import com.fooddelivery.orderservice.entity.Order;
//import com.fooddelivery.orderservice.entity.OrderDetails;
//import com.fooddelivery.orderservice.exception.InvalidRequestException;
//import com.fooddelivery.orderservice.exception.ResourceNotFoundException;
//import com.fooddelivery.orderservice.repository.OrderRepository;
//import com.fooddelivery.orderservice.service.OrderService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class OrderServiceImpl implements OrderService {
//
//    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
//
//    private final OrderRepository orderRepository;
//    private final CustomerClient customerClient;
//    private final MenuClient menuClient;
//
//    public OrderServiceImpl(
//            OrderRepository orderRepository,
//            CustomerClient customerClient,
//            MenuClient menuClient) {
//        this.orderRepository = orderRepository;
//        this.customerClient = customerClient;
//        this.menuClient = menuClient;
//    }
//
//    @Override
//    public OrderResponseDto placeOrder(OrderRequestDto dto) {
//
//        log.info("Placing order for customerId={}", dto.getCustomerId());
//
//        // Step 1 — Validate customer exists
//        CustomerResponseDto customer = customerClient.getCustomerById(dto.getCustomerId());
//        if (customer == null) {
//            throw new ResourceNotFoundException(
//                    "Customer not found with id: " + dto.getCustomerId());
//        }
//        log.info("Customer validated: customerId={}", customer.getCustomerId());
//
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        List<OrderDetails> detailsList = new ArrayList<>();
//
//        Order order = new Order();
//        order.setCustomerId(dto.getCustomerId());
//        order.setRestaurantId(dto.getRestaurantId());
//        order.setCustomerAddress(dto.getCustomerAddress());
//        order.setOrderStatus("PAYMENT_PENDING");
//
//        // Step 2 — Validate each item and get real price from menu-service
//        for (OrderItemRequestDto item : dto.getItems()) {
//
//            MenuResponseDto menuItem = menuClient.getMenuItemById(item.getItemId());
//
//            // Step 3 — Check item belongs to the same restaurant
//            if (!menuItem.getRestaurantId().equals(dto.getRestaurantId())) {
//                throw new InvalidRequestException(
//                        "Item " + item.getItemId() +
//                                " does not belong to restaurant " + dto.getRestaurantId());
//            }
//
//            // Step 4 — Check item is available
//            if (!menuItem.isAvailable()) {
//                throw new InvalidRequestException(
//                        "Menu item is not available: " + menuItem.getName());
//            }
//
//            // Step 5 — Check enough quantity in stock
//            if (menuItem.getQuantity() < item.getQuantity()) {
//                throw new InvalidRequestException(
//                        "Insufficient stock for item: " + menuItem.getName() +
//                                ". Available: " + menuItem.getQuantity() +
//                                ", Requested: " + item.getQuantity());
//            }
//
//            // Step 6 — Get real price from menu-service
//            BigDecimal price = menuItem.getPrice();
//            log.info("Item validated: itemId={}, price={}, qty={}",
//                    item.getItemId(), price, item.getQuantity());
//
//            totalAmount = totalAmount.add(
//                    price.multiply(BigDecimal.valueOf(item.getQuantity()))
//            );
//
//            OrderDetails details = new OrderDetails();
//            details.setItemId(item.getItemId());
//            details.setQuantity(item.getQuantity());
//            details.setPrice(price);
//            details.setOrder(order);
//            detailsList.add(details);
//        }
//
//        order.setTotalAmount(totalAmount);
//        order.setOrderDetails(detailsList);
//
//        Order saved = orderRepository.save(order);
//        log.info("Order saved, orderId={}", saved.getOrderId());
//
//        // Step 7 — Reduce stock in menu-service for each ordered item
//        for (OrderItemRequestDto item : dto.getItems()) {
//            menuClient.reduceQuantity(item.getItemId(), item.getQuantity());
//            log.info("Stock reduced for itemId={}, qty={}", item.getItemId(), item.getQuantity());
//        }
//
//        return mapToDto(saved);
//    }
//
//    @Override
//    public OrderResponseDto getOrderById(Long orderId) throws ResourceNotFoundException {
//        log.info("Fetching orderId={}", orderId);
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Order not found with id: " + orderId));
//        return mapToDto(order);
//    }
//
//    @Override
//    public List<OrderResponseDto> getOrdersByCustomer(Long customerId)
//            throws ResourceNotFoundException {
//        log.info("Fetching orders for customerId={}", customerId);
//        List<Order> orders = orderRepository.findByCustomerId(customerId);
//        if (orders.isEmpty()) {
//            throw new ResourceNotFoundException(
//                    "No orders found for customerId: " + customerId);
//        }
//        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<OrderResponseDto> getOrdersByRestaurant(Long restaurantId)
//            throws ResourceNotFoundException {
//        log.info("Fetching orders for restaurantId={}", restaurantId);
//        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
//        if (orders.isEmpty()) {
//            throw new ResourceNotFoundException(
//                    "No orders found for restaurantId: " + restaurantId);
//        }
//        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
//    }
//
//    @Override
//    public void updateOrderStatus(Long orderId, String status)
//            throws ResourceNotFoundException {
//        log.info("Updating orderId={} to status={}", orderId, status);
//        if (status == null || status.isBlank()) {
//            throw new InvalidRequestException("Order status cannot be empty");
//        }
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Order not found with id: " + orderId));
//        order.setOrderStatus(status);
//        orderRepository.save(order);
//        log.info("Status updated for orderId={}", orderId);
//    }
//
//    @Override
//    public String getOrderStatus(Long orderId) throws ResourceNotFoundException {
//        log.info("Getting status for orderId={}", orderId);
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Order not found with id: " + orderId));
//        return order.getOrderStatus();
//    }
//
//    // ── Mapper ───────────────────────────────────────────────────────────────
//    private OrderResponseDto mapToDto(Order order) {
//        List<OrderItemResponseDto> itemDtos = order.getOrderDetails()
//                .stream()
//                .map(d -> new OrderItemResponseDto(
//                        d.getItemId(),
//                        d.getQuantity(),
//                        d.getPrice()
//                ))
//                .collect(Collectors.toList());
//
//        OrderResponseDto response = new OrderResponseDto();
//        response.setOrderId(order.getOrderId());
//        response.setCustomerId(order.getCustomerId());
//        response.setRestaurantId(order.getRestaurantId());
//        response.setCustomerAddress(order.getCustomerAddress());
//        response.setStatus(order.getOrderStatus());
//        response.setTotalAmount(order.getTotalAmount());
//        response.setOrderTime(order.getOrderTime());
//        response.setItems(itemDtos);
//        return response;
//    }
//}







package com.fooddelivery.orderservice.serviceimpl;

import com.fooddelivery.orderservice.client.CustomerClient;
import com.fooddelivery.orderservice.client.MenuClient;
import com.fooddelivery.orderservice.client.PaymentClient;
import com.fooddelivery.orderservice.dto.request.OrderItemRequestDto;
import com.fooddelivery.orderservice.dto.request.OrderRequestDto;
import com.fooddelivery.orderservice.dto.response.CustomerResponseDto;
import com.fooddelivery.orderservice.dto.response.MenuResponseDto;
import com.fooddelivery.orderservice.dto.response.OrderItemResponseDto;
import com.fooddelivery.orderservice.dto.response.OrderResponseDto;
import com.fooddelivery.orderservice.entity.Order;
import com.fooddelivery.orderservice.entity.OrderDetails;
import com.fooddelivery.orderservice.exception.InvalidRequestException;
import com.fooddelivery.orderservice.exception.ResourceNotFoundException;
import com.fooddelivery.orderservice.repository.OrderRepository;
import com.fooddelivery.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final MenuClient menuClient;
    private final PaymentClient paymentClient;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CustomerClient customerClient,
            MenuClient menuClient,
            PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.customerClient = customerClient;
        this.menuClient = menuClient;
        this.paymentClient = paymentClient;
    }

    @Override
    public OrderResponseDto placeOrder(OrderRequestDto dto) {

        log.info("Placing order for customerId={}", dto.getCustomerId());

        CustomerResponseDto customer = customerClient.getCustomerById(dto.getCustomerId());
        if (customer == null) {
            throw new ResourceNotFoundException(
                    "Customer not found with id: " + dto.getCustomerId());
        }
        log.info("Customer validated: customerId={}", customer.getCustomerId());

        // Use address from request, fallback to customer profile address
        String deliveryAddress = (dto.getCustomerAddress() != null &&
                !dto.getCustomerAddress().isBlank())
                ? dto.getCustomerAddress()
                : customer.getAddress();

        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            throw new InvalidRequestException(
                    "Delivery address is required. Please provide address or update your profile.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetails> detailsList = new ArrayList<>();

        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());
        order.setRestaurantId(dto.getRestaurantId());
        order.setCustomerAddress(deliveryAddress);
        order.setOrderStatus("PAYMENT_PENDING");

        for (OrderItemRequestDto item : dto.getItems()) {

            MenuResponseDto menuItem = menuClient.getMenuItemById(item.getItemId());

            if (!menuItem.getRestaurantId().equals(dto.getRestaurantId())) {
                throw new InvalidRequestException(
                        "Item " + item.getItemId() +
                                " does not belong to restaurant " + dto.getRestaurantId());
            }

            if (!menuItem.isAvailable()) {
                throw new InvalidRequestException(
                        "Menu item is not available: " + menuItem.getName());
            }

            if (menuItem.getQuantity() < item.getQuantity()) {
                throw new InvalidRequestException(
                        "Insufficient stock for item: " + menuItem.getName() +
                                ". Available: " + menuItem.getQuantity() +
                                ", Requested: " + item.getQuantity());
            }

            BigDecimal price = menuItem.getPrice();
            log.info("Item validated: itemId={}, price={}, qty={}",
                    item.getItemId(), price, item.getQuantity());

            totalAmount = totalAmount.add(
                    price.multiply(BigDecimal.valueOf(item.getQuantity()))
            );

            OrderDetails details = new OrderDetails();
            details.setItemId(item.getItemId());
            details.setQuantity(item.getQuantity());
            details.setPrice(price);
            details.setOrder(order);
            detailsList.add(details);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderDetails(detailsList);

        Order saved = orderRepository.save(order);
        log.info("Order saved, orderId={}", saved.getOrderId());

        for (OrderItemRequestDto item : dto.getItems()) {
            menuClient.reduceQuantity(item.getItemId(), item.getQuantity());
            log.info("Stock reduced for itemId={}, qty={}", item.getItemId(), item.getQuantity());
        }

        return mapToDto(saved);
    }

    @Override
    public OrderResponseDto getOrderById(Long orderId) throws ResourceNotFoundException {
        log.info("Fetching orderId={}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));
        return mapToDto(order);
    }

    @Override
    public List<OrderResponseDto> getOrdersByCustomer(Long customerId) {
        log.info("Fetching orders for customerId={}", customerId);
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDto> getOrdersByRestaurant(Long restaurantId)
            throws ResourceNotFoundException {
        log.info("Fetching orders for restaurantId={}", restaurantId);
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No orders found for restaurantId: " + restaurantId);
        }
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(Long orderId, String status)
            throws ResourceNotFoundException {
        log.info("Updating orderId={} to status={}", orderId, status);
        if (status == null || status.isBlank()) {
            throw new InvalidRequestException("Order status cannot be empty");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));
        order.setOrderStatus(status);
        orderRepository.save(order);
        log.info("Status updated for orderId={}", orderId);
    }

    @Override
    public String getOrderStatus(Long orderId) throws ResourceNotFoundException {
        log.info("Getting status for orderId={}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));
        return order.getOrderStatus();
    }

    @Override
    public void cancelOrder(Long orderId)
            throws ResourceNotFoundException, InvalidRequestException {

        log.info("Cancel order request for orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        String currentStatus = order.getOrderStatus();

        // Check if order can be cancelled
        if (currentStatus.equals("PREPARING") ||
                currentStatus.equals("OUT_FOR_DELIVERY") ||
                currentStatus.equals("DELIVERED")) {
            throw new InvalidRequestException(
                    "Order cannot be cancelled at this stage: " + currentStatus);
        }

        if (currentStatus.equals("CANCELLED")) {
            throw new InvalidRequestException("Order is already cancelled");
        }

        // If payment was made, initiate refund
        if (currentStatus.equals("CONFIRMED")) {
            try {
                paymentClient.refundByOrderId(orderId);
                log.info("Refund initiated for orderId={}", orderId);
            } catch (Exception e) {
                log.error("Refund failed for orderId={}: {}", orderId, e.getMessage());
                throw new InvalidRequestException(
                        "Cannot cancel order — refund failed: " + e.getMessage());
            }
        }

        // Restore stock in menu-service for each item
        for (OrderDetails item : order.getOrderDetails()) {
            try {
                menuClient.restoreQuantity(item.getItemId(), item.getQuantity());
                log.info("Stock restored for itemId={}, qty={}",
                        item.getItemId(), item.getQuantity());
            } catch (Exception e) {
                log.error("Stock restore failed for itemId={}: {}",
                        item.getItemId(), e.getMessage());
            }
        }

        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);
        log.info("Order cancelled successfully for orderId={}", orderId);
    }

    // ── Mapper ───────────────────────────────────────────────────────────────
    private OrderResponseDto mapToDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getOrderDetails()
                .stream()
                .map(d -> new OrderItemResponseDto(
                        d.getItemId(),
                        d.getQuantity(),
                        d.getPrice()
                ))
                .collect(Collectors.toList());

        OrderResponseDto response = new OrderResponseDto();
        response.setOrderId(order.getOrderId());
        response.setCustomerId(order.getCustomerId());
        response.setRestaurantId(order.getRestaurantId());
        response.setCustomerAddress(order.getCustomerAddress());
        response.setStatus(order.getOrderStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderTime(order.getOrderTime());
        response.setItems(itemDtos);
        return response;
    }
}