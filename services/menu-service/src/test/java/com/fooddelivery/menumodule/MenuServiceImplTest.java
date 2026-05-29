package com.fooddelivery.menumodule;

import com.fooddelivery.menumodule.dto.request.MenuPatchDto;
import com.fooddelivery.menumodule.dto.request.MenuRequestDto;
import com.fooddelivery.menumodule.dto.response.MenuResponseDto;
import com.fooddelivery.menumodule.entity.MenuItem;
import com.fooddelivery.menumodule.entity.Restaurant;
import com.fooddelivery.menumodule.exception.InvalidRequestException;
import com.fooddelivery.menumodule.exception.MenuItemNotFoundException;
import com.fooddelivery.menumodule.exception.RestaurantNotFoundException;
import com.fooddelivery.menumodule.repository.MenuItemRepository;
import com.fooddelivery.menumodule.repository.RestaurantRepository;
import com.fooddelivery.menumodule.service.Impl.MenuServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    private MenuItem sampleItem;
    private Restaurant sampleRestaurant;
    private MenuRequestDto sampleDto;
    private MenuPatchDto samplePatchDto;

    @BeforeEach
    void setUp() {
        sampleRestaurant = new Restaurant();
        sampleRestaurant.setRestaurantId(1L);
        sampleRestaurant.setRestaurantName("A2B");
        sampleRestaurant.setLocation("Chennai");
        sampleRestaurant.setContactNumber("9000000011");

        sampleItem = new MenuItem();
        sampleItem.setItemId(1L);
        sampleItem.setName("Chicken Biryani");
        sampleItem.setDescription("Spicy biryani");
        sampleItem.setPrice(new BigDecimal("250.00"));
        sampleItem.setRestaurant(sampleRestaurant);
        sampleItem.setAvailable(true);
        sampleItem.setQuantity(20);

        sampleDto = new MenuRequestDto();
        sampleDto.setName("Chicken Biryani");
        sampleDto.setDescription("Spicy biryani");
        sampleDto.setPrice(new BigDecimal("250.00"));
        sampleDto.setRestaurantId(1L);
        sampleDto.setQuantity(20);

        samplePatchDto = new MenuPatchDto();
        samplePatchDto.setPrice(new BigDecimal("300.00"));
    }

    // =========================================================
    // addMenuItem tests
    // =========================================================

    @Test
    void testAddMenuItem_ShouldSaveSuccessfully() {
        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(sampleRestaurant));
        when(menuItemRepository.save(any(MenuItem.class)))
                .thenReturn(sampleItem); // ✅ save returns MenuItem!

        assertDoesNotThrow(() -> menuService.addMenuItem(sampleDto));
        verify(menuItemRepository, times(1))
                .save(any(MenuItem.class));
    }

    @Test
    void testAddMenuItem_EmptyName_ShouldThrowException() {
        sampleDto.setName("");
        assertThrows(InvalidRequestException.class,
                () -> menuService.addMenuItem(sampleDto));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void testAddMenuItem_NullName_ShouldThrowException() {
        sampleDto.setName(null);
        assertThrows(InvalidRequestException.class,
                () -> menuService.addMenuItem(sampleDto));
    }

    @Test
    void testAddMenuItem_ZeroPrice_ShouldThrowException() {
        sampleDto.setPrice(BigDecimal.ZERO);
        assertThrows(InvalidRequestException.class,
                () -> menuService.addMenuItem(sampleDto));
    }

    @Test
    void testAddMenuItem_NegativePrice_ShouldThrowException() {
        sampleDto.setPrice(new BigDecimal("-10.00"));
        assertThrows(InvalidRequestException.class,
                () -> menuService.addMenuItem(sampleDto));
    }

    @Test
    void testAddMenuItem_NullRestaurantId_ShouldThrowException() {
        sampleDto.setRestaurantId(null);
        assertThrows(InvalidRequestException.class,
                () -> menuService.addMenuItem(sampleDto));
    }

    @Test
    void testAddMenuItem_RestaurantNotFound_ShouldThrowException() {
        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(RestaurantNotFoundException.class,
                () -> menuService.addMenuItem(sampleDto));
    }

    // =========================================================
    // updateMenuItem tests
    // =========================================================

    @Test
    void testUpdateMenuItem_ShouldUpdateSuccessfully() {
        when(menuItemRepository.findById(1L))
                .thenReturn(Optional.of(sampleItem));
        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(sampleRestaurant));
        when(menuItemRepository.save(any(MenuItem.class)))
                .thenReturn(sampleItem); // ✅ fixed!

        assertDoesNotThrow(
                () -> menuService.updateMenuItem(1L, sampleDto));
        verify(menuItemRepository, times(1))
                .save(any(MenuItem.class));
    }

    @Test
    void testUpdateMenuItem_InvalidId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> menuService.updateMenuItem(0L, sampleDto));
    }

    @Test
    void testUpdateMenuItem_NegativeId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> menuService.updateMenuItem(-1L, sampleDto));
    }

    @Test
    void testUpdateMenuItem_ItemNotFound_ShouldThrowException() {
        when(menuItemRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(MenuItemNotFoundException.class,
                () -> menuService.updateMenuItem(99L, sampleDto));
    }

    @Test
    void testUpdateMenuItem_EmptyName_ShouldThrowException() {
        sampleDto.setName("");
        assertThrows(InvalidRequestException.class,
                () -> menuService.updateMenuItem(1L, sampleDto));
    }

    // =========================================================
    // deleteMenuItem tests
    // =========================================================

    @Test
    void testDeleteMenuItem_ShouldDeleteSuccessfully() {
        when(menuItemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(menuItemRepository).deleteById(1L);
        // ✅ deleteById IS void — doNothing() is correct here!

        assertDoesNotThrow(() -> menuService.deleteMenuItem(1L));
        verify(menuItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteMenuItem_InvalidId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> menuService.deleteMenuItem(0L));
    }

    @Test
    void testDeleteMenuItem_ItemNotFound_ShouldThrowException() {
        when(menuItemRepository.existsById(99L)).thenReturn(false);
        assertThrows(MenuItemNotFoundException.class,
                () -> menuService.deleteMenuItem(99L));
    }

    // =========================================================
    // getMenuByRestaurant tests
    // =========================================================

    @Test
    void testGetMenuByRestaurant_ShouldReturnList() {
        when(menuItemRepository
                .findByRestaurant_RestaurantId(1L))
                .thenReturn(Arrays.asList(sampleItem));

        List<MenuResponseDto> result =
                menuService.getMenuByRestaurant(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Chicken Biryani", result.get(0).getName());
    }

    @Test
    void testGetMenuByRestaurant_EmptyList_ShouldReturnEmpty() {
        when(menuItemRepository
                .findByRestaurant_RestaurantId(1L))
                .thenReturn(Collections.emptyList());

        List<MenuResponseDto> result =
                menuService.getMenuByRestaurant(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetMenuByRestaurant_InvalidId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> menuService.getMenuByRestaurant(0L));
    }

    // =========================================================
    // getMenuItemById tests
    // =========================================================

    @Test
    void testGetMenuItemById_ShouldReturnItem() {
        when(menuItemRepository.findById(1L))
                .thenReturn(Optional.of(sampleItem));

        MenuResponseDto result = menuService.getMenuItemById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getItemId());
        assertEquals("Chicken Biryani", result.getName());
        assertEquals(new BigDecimal("250.00"), result.getPrice());
    }

    @Test
    void testGetMenuItemById_InvalidId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> menuService.getMenuItemById(0L));
    }

    @Test
    void testGetMenuItemById_NotFound_ShouldThrowException() {
        when(menuItemRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(MenuItemNotFoundException.class,
                () -> menuService.getMenuItemById(99L));
    }

    // =========================================================
    // getAllAvailableItems tests
    // =========================================================

    @Test
    void testGetAllAvailableItems_ShouldReturnList() {
        when(menuItemRepository
                .findByAvailableTrueAndQuantityGreaterThan(0))
                .thenReturn(Arrays.asList(sampleItem));

        List<MenuResponseDto> result =
                menuService.getAllAvailableItems();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).isAvailable());
    }

    @Test
    void testGetAllAvailableItems_NoItems_ShouldThrowException() {
        when(menuItemRepository
                .findByAvailableTrueAndQuantityGreaterThan(0))
                .thenReturn(Collections.emptyList());

        assertThrows(MenuItemNotFoundException.class,
                () -> menuService.getAllAvailableItems());
    }

    // =========================================================
    // updateAvailability tests
    // =========================================================

    @Test
    void testUpdateAvailability_ShouldMarkUnavailable() {
        when(menuItemRepository.findById(1L))
                .thenReturn(Optional.of(sampleItem));
        when(menuItemRepository.save(any(MenuItem.class)))
                .thenReturn(sampleItem); // ✅ fixed!

        assertDoesNotThrow(
                () -> menuService.updateAvailability(1L, false));

        assertFalse(sampleItem.isAvailable());
        verify(menuItemRepository, times(1)).save(sampleItem);
    }

    @Test
    void testUpdateAvailability_ShouldMarkAvailable() {
        sampleItem.setAvailable(false);
        when(menuItemRepository.findById(1L))
                .thenReturn(Optional.of(sampleItem));
        when(menuItemRepository.save(any(MenuItem.class)))
                .thenReturn(sampleItem); // ✅ fixed!

        assertDoesNotThrow(
                () -> menuService.updateAvailability(1L, true));

        assertTrue(sampleItem.isAvailable());
    }

    @Test
    void testUpdateAvailability_InvalidId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> menuService.updateAvailability(0L, true));
    }

    @Test
    void testUpdateAvailability_ItemNotFound_ShouldThrowException() {
        when(menuItemRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(MenuItemNotFoundException.class,
                () -> menuService.updateAvailability(99L, true));
    }

    // =========================================================
    // reduceQuantity tests
    // =========================================================

    @Test
    void testReduceQuantity_ShouldReduceSuccessfully() {
        doNothing().when(menuItemRepository)
                .reduceQuantity(1L, 5);
        // ✅ reduceQuantity IS void — doNothing() correct!

        assertDoesNotThrow(
                () -> menuService.reduceQuantity(1L, 5));

        verify(menuItemRepository, times(1))
                .reduceQuantity(1L, 5);
    }

    @Test
    void testReduceQuantity_InvalidId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> menuService.reduceQuantity(0L, 5));
    }

    @Test
    void testReduceQuantity_ZeroQuantity_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> menuService.reduceQuantity(1L, 0));
    }

    @Test
    void testReduceQuantity_NegativeQuantity_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> menuService.reduceQuantity(1L, -5));
    }

    // =========================================================
    // patchMenuItem tests
    // =========================================================

    @Test
    void testPatchMenuItem_OnlyPrice_ShouldUpdatePrice() {
        when(menuItemRepository.findById(1L))
                .thenReturn(Optional.of(sampleItem));
        when(menuItemRepository.save(any(MenuItem.class)))
                .thenReturn(sampleItem); // ✅ fixed!

        samplePatchDto.setPrice(new BigDecimal("300.00"));
        assertDoesNotThrow(
                () -> menuService.patchMenuItem(1L, samplePatchDto));

        assertEquals(new BigDecimal("300.00"), sampleItem.getPrice());
        verify(menuItemRepository, times(1)).save(sampleItem);
    }

    @Test
    void testPatchMenuItem_OnlyName_ShouldUpdateName() {
        when(menuItemRepository.findById(1L))
                .thenReturn(Optional.of(sampleItem));
        when(menuItemRepository.save(any(MenuItem.class)))
                .thenReturn(sampleItem); // ✅ fixed!

        samplePatchDto = new MenuPatchDto();
        samplePatchDto.setName("Mutton Biryani");

        assertDoesNotThrow(
                () -> menuService.patchMenuItem(1L, samplePatchDto));

        assertEquals("Mutton Biryani", sampleItem.getName());
    }

    @Test
    void testPatchMenuItem_InvalidId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> menuService.patchMenuItem(0L, samplePatchDto));
    }

    @Test
    void testPatchMenuItem_ItemNotFound_ShouldThrowException() {
        when(menuItemRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(MenuItemNotFoundException.class,
                () -> menuService.patchMenuItem(99L, samplePatchDto));
    }

    @Test
    void testPatchMenuItem_NullFields_ShouldNotUpdate() {
        when(menuItemRepository.findById(1L))
                .thenReturn(Optional.of(sampleItem));
        when(menuItemRepository.save(any(MenuItem.class)))
                .thenReturn(sampleItem); // ✅ fixed!

        MenuPatchDto emptyPatch = new MenuPatchDto();
        assertDoesNotThrow(
                () -> menuService.patchMenuItem(1L, emptyPatch));

        assertEquals("Chicken Biryani", sampleItem.getName());
        assertEquals(new BigDecimal("250.00"), sampleItem.getPrice());
    }
}