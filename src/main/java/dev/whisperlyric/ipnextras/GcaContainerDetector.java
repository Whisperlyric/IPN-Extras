package dev.whisperlyric.ipnextras;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class GcaContainerDetector {

    public static ContainerCategory detectGcaContainer(AbstractContainerScreen<?> screen) {
        if (screen == null) {
            return ContainerCategory.NOT_GCA_CONTAINER;
        }
        return detectGcaContainer(screen.getMenu(), screen.getTitle());
    }

    public static ContainerCategory detectGcaContainer(AbstractContainerMenu menu, Component screenTitle) {
        if (!(menu instanceof ChestMenu) || menu.slots.isEmpty()) {
            return ContainerCategory.NOT_GCA_CONTAINER;
        }

        ItemStack stack0 = menu.getSlot(0).getItem();
        CompoundTag tag = getCustomDataTag(stack0);

        if (tag == null || (!tag.contains("GcaClear") && !tag.contains("gca.clear"))) {
            return ContainerCategory.NOT_GCA_CONTAINER;
        }

        String titleStr = screenTitle.getString().toLowerCase();
        if (titleStr.contains("ender chest") || titleStr.contains("末影箱")) {
            return ContainerCategory.GCA_FAKE_PLAYER_ENDER_CHEST;
        }

        return ContainerCategory.GCA_FAKE_PLAYER_INVENTORY;
    }

    /**
     * Gets the custom NBT data from an ItemStack, compatible with both pre- and post-1.20.5 APIs.
     */
    private static CompoundTag getCustomDataTag(ItemStack stack) {
        //#if MC>=12005
        // MC 1.20.5+: DataComponents.CUSTOM_DATA
        var cd = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return cd != null ? cd.copyTag() : null;
        //#else
        //$$ // MC 1.20.1: getTag()
        //$$ return stack.getTag();
        //#endif
    }

    public static boolean isGcaContainer(AbstractContainerScreen<?> screen) {
        return detectGcaContainer(screen) != ContainerCategory.NOT_GCA_CONTAINER;
    }

    public static boolean isGcaContainer(AbstractContainerMenu menu, Component title) {
        return detectGcaContainer(menu, title) != ContainerCategory.NOT_GCA_CONTAINER;
    }
}