package com.globaltrade.scms.core.inventory;

import com.globaltrade.scms.common.exception.InventoryShortageException;
import com.globaltrade.scms.core.entity.InventoryLevel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceBeanTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private InventoryServiceBean inventoryService;

    private InventoryLevel mockLevel;

    @BeforeEach
    public void setUp() {
        mockLevel = new InventoryLevel();
        mockLevel.setInventoryLevelId(1L);
        mockLevel.setQuantityAvailable(10);
        mockLevel.setReorderLevel(5);
    }

    @Test
    public void testDeductStock_Success() throws InventoryShortageException {
        // Mock the database find with Pessimistic Lock
        when(em.find(InventoryLevel.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(mockLevel);

        // Execute
        inventoryService.deductStock(1L, 4);

        // Verify state changed correctly
        assertEquals(6, mockLevel.getQuantityAvailable());
        verify(em).find(InventoryLevel.class, 1L, LockModeType.PESSIMISTIC_WRITE);
    }

    @Test
    public void testDeductStock_InsufficientStock_ThrowsApplicationException() {
        when(em.find(InventoryLevel.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(mockLevel);

        // Verify that the custom ApplicationException is thrown
        Exception exception = assertThrows(InventoryShortageException.class, () -> {
            inventoryService.deductStock(1L, 15);
        });

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        // Verify state DID NOT change (proving transaction rollback logic works)
        assertEquals(10, mockLevel.getQuantityAvailable());
    }
}