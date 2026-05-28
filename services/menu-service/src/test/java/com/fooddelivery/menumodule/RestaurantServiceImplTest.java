package com.fooddelivery.menumodule;

import com.fooddelivery.menumodule.dto.request.RestaurantPatchDto;
import com.fooddelivery.menumodule.dto.request.RestaurantRequestDto;
import com.fooddelivery.menumodule.dto.response.RestaurantResponseDto;
import com.fooddelivery.menumodule.entity.Restaurant;
import com.fooddelivery.menumodule.exception.InvalidRequestException;
import com.fooddelivery.menumodule.exception.RestaurantNotFoundException;
import com.fooddelivery.menumodule.repository.MenuItemRepository;
import com.fooddelivery.menumodule.repository.RestaurantRepository;
import com.fooddelivery.menumodule.service.Impl.RestaurantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant sampleRestaurant;
    private RestaurantRequestDto sampleDto;
    private RestaurantPatchDto samplePatchDto;

    @BeforeEach
    void setUp() {
        sampleRestaurant = new Restaurant();
        sampleRestaurant.setRestaurantId(1L);
        sampleRestaurant.setUserId(1L);
        sampleRestaurant.setRestaurantName("A2B");
        sampleRestaurant.setLocation("Chennai");
        sampleRestaurant.setContactNumber("9000000011");

        sampleDto = new RestaurantRequestDto();
        sampleDto.setUserId(1L);
        sampleDto.setRestaurantName("A2B");
        sampleDto.setLocation("Chennai");
        sampleDto.setContactNumber("9000000011");

        samplePatchDto = new RestaurantPatchDto();
        samplePatchDto.setLocation("Bangalore");
    }

    // =========================================================
    // registerRestaurant tests
    // =========================================================

    @Test
    void testRegisterRestaurant_ShouldSaveSuccessfully() {
        when(restaurantRepository.save(any(Restaurant.class)))
                .thenReturn(sampleRestaurant); // ✅ fixed!

        assertDoesNotThrow(
                () -> restaurantService.registerRestaurant(sampleDto));
        verify(restaurantRepository, times(1))
                .save(any(Restaurant.class));
    }

    @Test
    void testRegisterRestaurant_EmptyName_ShouldThrowException() {
        sampleDto.setRestaurantName("");
        assertThrows(InvalidRequestException.class,
                () -> restaurantService.registerRestaurant(sampleDto));
    }

    @Test
    void testRegisterRestaurant_NullName_ShouldThrowException() {
        sampleDto.setRestaurantName(null);
        assertThrows(InvalidRequestException.class,
                () -> restaurantService.registerRestaurant(sampleDto));
    }

    @Test
    void testRegisterRestaurant_EmptyLocation_ShouldThrowException() {
        sampleDto.setLocation("");
        assertThrows(InvalidRequestException.class,
                () -> restaurantService.registerRestaurant(sampleDto));
    }

    @Test
    void testRegisterRestaurant_EmptyContact_ShouldThrowException() {
        sampleDto.setContactNumber("");
        assertThrows(InvalidRequestException.class,
                () -> restaurantService.registerRestaurant(sampleDto));
    }

    // =========================================================
    // getByUserId tests
    // =========================================================

    @Test
    void testGetByUserId_ShouldReturnRestaurant() {
        when(restaurantRepository.findByUserId(1L))
                .thenReturn(Optional.of(sampleRestaurant));

        RestaurantResponseDto result =
                restaurantService.getByUserId(1L);

        assertNotNull(result);
        assertEquals("A2B", result.getRestaurantName());
        assertEquals("Chennai", result.getLocation());
    }

    @Test
    void testGetByUserId_NotFound_ShouldThrowException() {
        when(restaurantRepository.findByUserId(99L))
                .thenReturn(Optional.empty());
        assertThrows(RestaurantNotFoundException.class,
                () -> restaurantService.getByUserId(99L));
    }

    // =========================================================
    // getRestaurant tests
    // =========================================================

    @Test
    void testGetRestaurant_ShouldReturnRestaurant() {
        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(sampleRestaurant));

        RestaurantResponseDto result =
                restaurantService.getRestaurant(1L);

        assertNotNull(result);
        assertEquals(1L, result.getRestaurantId());
        assertEquals("A2B", result.getRestaurantName());
    }

    @Test
    void testGetRestaurant_NotFound_ShouldThrowException() {
        when(restaurantRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(RestaurantNotFoundException.class,
                () -> restaurantService.getRestaurant(99L));
    }

    // =========================================================
    // getAllRestaurants tests
    // =========================================================

    @Test
    void testGetAllRestaurants_ShouldReturnList() {
        when(restaurantRepository.findAll())
                .thenReturn(Arrays.asList(sampleRestaurant));

        List<RestaurantResponseDto> result =
                restaurantService.getAllRestaurants();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A2B", result.get(0).getRestaurantName());
    }

    @Test
    void testGetAllRestaurants_EmptyList() {
        when(restaurantRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<RestaurantResponseDto> result =
                restaurantService.getAllRestaurants();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllRestaurants_MultipleRestaurants() {
        Restaurant second = new Restaurant();
        second.setRestaurantId(2L);
        second.setRestaurantName("DALCHINE");
        second.setLocation("Bangalore");
        second.setContactNumber("9000000022");

        when(restaurantRepository.findAll())
                .thenReturn(Arrays.asList(
                        sampleRestaurant, second));

        List<RestaurantResponseDto> result =
                restaurantService.getAllRestaurants();

        assertEquals(2, result.size());
        assertEquals("A2B", result.get(0).getRestaurantName());
        assertEquals("DALCHINE", result.get(1).getRestaurantName());
    }

    // =========================================================
    // updateRestaurant tests
    // =========================================================

    @Test
    void testUpdateRestaurant_ShouldUpdateSuccessfully() {
        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(sampleRestaurant));
        when(restaurantRepository.save(any(Restaurant.class)))
                .thenReturn(sampleRestaurant); // ✅ fixed!

        assertDoesNotThrow(
                () -> restaurantService.updateRestaurant(
                        1L, sampleDto));

        verify(restaurantRepository, times(1))
                .save(any(Restaurant.class));
    }

    @Test
    void testUpdateRestaurant_InvalidId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> restaurantService.updateRestaurant(
                        0L, sampleDto));
    }

    @Test
    void testUpdateRestaurant_NotFound_ShouldThrowException() {
        when(restaurantRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(RestaurantNotFoundException.class,
                () -> restaurantService.updateRestaurant(
                        99L, sampleDto));
    }

    @Test
    void testUpdateRestaurant_ShouldHaveUpdatedValues() {
        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(sampleRestaurant));
        when(restaurantRepository.save(any(Restaurant.class)))
                .thenReturn(sampleRestaurant); // ✅ fixed!

        sampleDto.setRestaurantName("A2B Updated");
        sampleDto.setLocation("Bangalore");
        restaurantService.updateRestaurant(1L, sampleDto);

        assertEquals("A2B Updated",
                sampleRestaurant.getRestaurantName());
        assertEquals("Bangalore",
                sampleRestaurant.getLocation());
    }

    // =========================================================
    // deleteRestaurant tests
    // =========================================================

    @Test
    void testDeleteRestaurant_ShouldDeleteSuccessfully() {
        when(restaurantRepository.existsById(1L))
                .thenReturn(true);
        doNothing().when(restaurantRepository).deleteById(1L);
        // ✅ deleteById IS void — doNothing() correct!

        assertDoesNotThrow(
                () -> restaurantService.deleteRestaurant(1L));

        verify(restaurantRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteRestaurant_InvalidId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> restaurantService.deleteRestaurant(0L));
    }

    @Test
    void testDeleteRestaurant_NotFound_ShouldThrowException() {
        when(restaurantRepository.existsById(99L))
                .thenReturn(false);
        assertThrows(RestaurantNotFoundException.class,
                () -> restaurantService.deleteRestaurant(99L));
    }

    // =========================================================
    // patchRestaurant tests
    // =========================================================

    @Test
    void testPatchRestaurant_OnlyLocation_ShouldUpdateLocation() {
        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(sampleRestaurant));
        when(restaurantRepository.save(any(Restaurant.class)))
                .thenReturn(sampleRestaurant); // ✅ fixed!

        assertDoesNotThrow(
                () -> restaurantService.patchRestaurant(
                        1L, samplePatchDto));

        assertEquals("Bangalore",
                sampleRestaurant.getLocation());
        verify(restaurantRepository, times(1))
                .save(sampleRestaurant);
    }

    @Test
    void testPatchRestaurant_OnlyName_ShouldUpdateName() {
        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(sampleRestaurant));
        when(restaurantRepository.save(any(Restaurant.class)))
                .thenReturn(sampleRestaurant); // ✅ fixed!

        RestaurantPatchDto patch = new RestaurantPatchDto();
        patch.setRestaurantName("A2B New");

        assertDoesNotThrow(
                () -> restaurantService.patchRestaurant(1L, patch));

        assertEquals("A2B New",
                sampleRestaurant.getRestaurantName());
    }

    @Test
    void testPatchRestaurant_InvalidId_ShouldThrowException() {
        assertThrows(InvalidRequestException.class,
                () -> restaurantService.patchRestaurant(
                        0L, samplePatchDto));
    }

    @Test
    void testPatchRestaurant_NotFound_ShouldThrowException() {
        when(restaurantRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(RestaurantNotFoundException.class,
                () -> restaurantService.patchRestaurant(
                        99L, samplePatchDto));
    }

    @Test
    void testPatchRestaurant_NullFields_ShouldNotUpdate() {
        when(restaurantRepository.findById(1L))
                .thenReturn(Optional.of(sampleRestaurant));
        when(restaurantRepository.save(any(Restaurant.class)))
                .thenReturn(sampleRestaurant); // ✅ fixed!

        RestaurantPatchDto emptyPatch = new RestaurantPatchDto();
        assertDoesNotThrow(
                () -> restaurantService.patchRestaurant(
                        1L, emptyPatch));

        assertEquals("A2B",
                sampleRestaurant.getRestaurantName());
        assertEquals("Chennai",
                sampleRestaurant.getLocation());
    }
}