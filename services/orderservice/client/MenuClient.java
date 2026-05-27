package com.fooddelivery.orderservice.client;

import com.fooddelivery.orderservice.dto.response.MenuResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "menu-service")
// ↑ must match spring.application.name in menu-service's application.properties
public interface MenuClient {

    @GetMapping("/api/menu/{itemId}")
    MenuResponseDto getMenuItemById(@PathVariable("itemId") Long itemId);

    @PutMapping("/api/menu/{itemId}/reduce")
    void reduceQuantity(
            @PathVariable("itemId") Long itemId,
            @RequestParam("quantity") int quantity
    );
}
