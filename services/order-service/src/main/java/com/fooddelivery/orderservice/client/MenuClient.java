package com.fooddelivery.orderservice.client;

import com.fooddelivery.orderservice.dto.response.MenuResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "MENU-SERVICE")
public interface MenuClient {

    // matches GET /api/menu/{itemId} in MenuController
    @GetMapping("/api/menu/{itemId}")
    MenuResponseDto getMenuItemById(@PathVariable("itemId") Long itemId);

    // matches PUT /api/menu/{itemId}/availability in MenuController
    @PutMapping("/api/menu/{itemId}/availability")
    void updateAvailability(@PathVariable("itemId") Long itemId,
                            @RequestParam("status") boolean status);

    @PutMapping("/api/menu/{itemId}/reduce")
    void reduceQuantity(@PathVariable("itemId") Long itemId,
                        @RequestParam("quantity") int quantity);
}