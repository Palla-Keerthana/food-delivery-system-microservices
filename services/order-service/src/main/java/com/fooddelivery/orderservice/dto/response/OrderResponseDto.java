//package com.example.ordermodule.dto.response;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class OrderResponseDto {
//
//    private Long orderId;
//    private Long customerId;
//    private Long restaurantId;
//    private String status;
//    private BigDecimal totalAmount;
//    private LocalDateTime orderTime;
//    private List<OrderItemResponseDto> items;
//}

package com.fooddelivery.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private String customerAddress;          // ← new
    private Double customerLat;              // ← new
    private Double customerLon;              // ← new
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderTime;
    private List<OrderItemResponseDto> items;
}