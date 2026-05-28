package com.fooddelivery.orderservice.serviceimpl;

import com.fooddelivery.orderservice.client.CustomerClient;
import com.fooddelivery.orderservice.client.MenuClient;
import com.fooddelivery.orderservice.dto.request.OrderItemRequestDto;
import com.fooddelivery.orderservice.dto.request.OrderRequestDto;
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

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CustomerClient customerClient,
            MenuClient menuClient) {

        this.orderRepository = orderRepository;
        this.customerClient = customerClient;
        this.menuClient = menuClient;
    }

    @Override
    public OrderResponseDto placeOrder(OrderRequestDto dto) {

        log.info("Placing order for customerId={}", dto.getCustomerId());

        // TODO: Uncomment when customer-service is available
        // customerClient.getCustomerById(dto.getCustomerId());
        log.info("MOCK: Skipping customer validation for customerId={}", dto.getCustomerId());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetails> detailsList = new ArrayList<>();

        // Build order first so we can link details to it
        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());
        order.setRestaurantId(dto.getRestaurantId());
        order.setCustomerAddress(dto.getCustomerAddress());
        order.setOrderStatus("PLACED");

        for (OrderItemRequestDto item : dto.getItems()) {

            // TODO: Uncomment when menu-service is available
            // MenuResponseDto menuItem = menuClient.getMenuItemById(item.getItemId());
            // if (!menuItem.isAvailable()) {
            //     throw new InvalidRequestException("Menu item not available: " + item.getItemId());
            // }
            // BigDecimal price = menuItem.getPrice();
            // menuClient.reduceQuantity(item.getItemId(), item.getQuantity());

            // MOCK — fixed price of 100 per item for testing
            BigDecimal price = new BigDecimal("100.00");
            log.info("MOCK: Using price={} for itemId={}", price, item.getItemId());

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

        return mapToDto(saved);
    }

    @Override
    public OrderResponseDto getOrderById(Long orderId) throws ResourceNotFoundException{

        log.info("Fetching orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId
                ));

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
    public List<OrderResponseDto> getOrdersByRestaurant(Long restaurantId) throws ResourceNotFoundException {

        log.info("Fetching orders for restaurantId={}", restaurantId);

        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);

        if (orders.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No orders found for restaurantId: " + restaurantId);
        }

        return orders.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    @Override
    public void updateOrderStatus(Long orderId, String status) throws ResourceNotFoundException{

        log.info("Updating orderId={} to status={}", orderId, status);

        if (status == null || status.isBlank()) {
            throw new InvalidRequestException("Order status cannot be empty");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId
                ));

        order.setOrderStatus(status);
        orderRepository.save(order);
        log.info("Status updated for orderId={}", orderId);
    }

    @Override
    public String getOrderStatus(Long orderId) throws ResourceNotFoundException{

        log.info("Getting status for orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId
                ));

        return order.getOrderStatus();
    }

    // ── Mapper ────────────────────────────────────────────────────────────────
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






