package com.bx.magicSmp.patch;

import com.bx.magicSmp.menus.SellMenu;
import java.lang.reflect.Field;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Safety layer for the MagicSMP sell menu.
 *
 * Important: this listener NEVER changes the cursor and NEVER rebuilds/reopens a menu.
 * The real SellMenu remains responsible for multiplier/progression clicks.
 */
public final class SellGuiProtectionListener implements Listener {
    private static final Field CONFIRM_MODE_FIELD;

    static {
        Field f = null;
        try {
            f = SellMenu.class.getDeclaredField("confirmMode");
            f.setAccessible(true);
        } catch (ReflectiveOperationException ignored) {
        }
        CONFIRM_MODE_FIELD = f;
    }

    private SellMenu sellMenu(Inventory top) {
        if (top == null) return null;
        InventoryHolder holder = top.getHolder();
        return holder instanceof SellMenu menu ? menu : null;
    }

    private boolean isConfirmMode(SellMenu menu) {
        if (CONFIRM_MODE_FIELD == null) return false;
        try {
            return CONFIRM_MODE_FIELD.getBoolean(menu);
        } catch (IllegalAccessException ignored) {
            return false;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        SellMenu menu = sellMenu(top);
        if (menu == null) return;

        int raw = event.getRawSlot();
        boolean confirm = isConfirmMode(menu);

        // Instant mode: 0-44 are the player's sell slots; 45-53 are GUI controls.
        // Confirm mode: 0-52 are sell slots and 53 is the confirm button.
        boolean protectedTopSlot = raw >= 0 && raw < top.getSize()
                && (confirm ? raw == 53 : raw >= 45);

        if (protectedTopSlot) {
            // Cancel vanilla movement only. Do not alter cursor/item/menu state.
            // MagicSMP's SellMenu listener can still receive this event and execute
            // the multiplier/progression/back behavior itself.
            event.setCancelled(true);
            return;
        }

        // Never allow collect-to-cursor to vacuum matching GUI icons from the top menu.
        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        SellMenu menu = sellMenu(top);
        if (menu == null) return;

        boolean confirm = isConfirmMode(menu);
        for (int raw : event.getRawSlots()) {
            if (raw < 0 || raw >= top.getSize()) continue;
            if (confirm ? raw == 53 : raw >= 45) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
