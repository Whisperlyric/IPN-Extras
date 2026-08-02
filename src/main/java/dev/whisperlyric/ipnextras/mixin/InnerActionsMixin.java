package dev.whisperlyric.ipnextras.mixin;

import dev.whisperlyric.ipnextras.ContainerCategory;
import dev.whisperlyric.ipnextras.GcaContainerDetector;
import dev.whisperlyric.ipnextras.IpnExtrasClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.anti_ad.mc.ipnext.inventory.ItemArea;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

//#if MC>=260100
// IPN 26.1: InnerActions is a top-level class (not an inner class of GeneralInventoryActions)
// The sort call moved to SubTrackerActionsKt.sort (a static method, not on SubTracker interface)
// Method body is now in a separate lambda (innerDoSort$lambda$0), so locals aren't in the same frame
// Strategy: @ModifyArg on getAsSubTracker to intercept ItemArea before it's converted to SubTracker
@Mixin(targets = "org.anti_ad.mc.ipnext.inventory.InnerActions", remap = false)
public class InnerActionsMixin {

    @ModifyArg(
        method = "innerDoSort$lambda$0",
        at = @At(
            value = "INVOKE",
            target = "Lorg/anti_ad/mc/ipnext/inventory/AdvancedContainer$TrackerDsl;getAsSubTracker(Lorg/anti_ad/mc/ipnext/inventory/ItemArea;)Lorg/anti_ad/mc/ipnext/inventory/data/MutableSubTracker;"
        ),
        index = 0
    )
    private static ItemArea modifyTargetArea(ItemArea target) {
        filterSlotsForGca(target);
        return target;
    }

    private static void filterSlotsForGca(ItemArea target) {
        try {
            Minecraft client = Minecraft.getInstance();
            if (client.screen instanceof AbstractContainerScreen<?> containerScreen) {
                ContainerCategory category = GcaContainerDetector.detectGcaContainer(
                    containerScreen.getMenu(), containerScreen.getTitle());
                if (category != ContainerCategory.NOT_GCA_CONTAINER) {
                    List<Integer> currentSlots = new ArrayList<>(target.getSlotIndices());

                    if (category == ContainerCategory.GCA_FAKE_PLAYER_INVENTORY) {
                        currentSlots.removeIf(slotIndex -> slotIndex < 18 || slotIndex > 53);
                    } else if (category == ContainerCategory.GCA_FAKE_PLAYER_ENDER_CHEST) {
                        currentSlots.removeIf(slotIndex -> slotIndex < 27 || slotIndex > 53);
                    }

                    ((ItemAreaAccessor) (Object) target).setSlotIndices(currentSlots);
                }
            }
        } catch (Exception e) {
            IpnExtrasClient.LOGGER.error("Error filtering GCA slots: {}", e.getMessage(), e);
        }
    }
}
//#else
//$$ // MC < 26.1: InnerActions is an inner class of GeneralInventoryActions
//$$ // Sort is called on SubTracker interface directly, locals are in the same frame
//$$ // Strategy: @Inject with LocalCapture to intercept ItemArea before sort
//$$ @Mixin(targets = "org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions$InnerActions", remap = false)
//$$ public class InnerActionsMixin {
//$$
//$$     @Inject(
//$$         method = "innerDoSort",
//$$         at = @At(
//$$             value = "INVOKE",
//$$             target = "Lorg/anti_ad/mc/ipnext/inventory/data/SubTracker;sort(Lorg/anti_ad/mc/ipnext/item/rule/Rule;Lorg/anti_ad/mc/ipnext/config/PostAction;ZII)V"
//$$         ),
//$$         locals = LocalCapture.CAPTURE_FAILHARD,
//$$         cancellable = false
//$$     )
//$$     private void onInnerDoSort(
//$$         org.anti_ad.mc.ipnext.item.rule.Rule sortingRule,
//$$         org.anti_ad.mc.ipnext.config.PostAction postAction,
//$$         boolean forcePlayer,
//$$         AbstractContainerMenu container,
//$$         CallbackInfo ci,
//$$         org.anti_ad.mc.ipnext.inventory.AdvancedContainer tracker,
//$$         org.anti_ad.mc.ipnext.inventory.AreaTypes areaTypes,
//$$         boolean forcePlayerSide,
//$$         org.anti_ad.mc.ipnext.inventory.ItemArea target
//$$     ) {
//$$         try {
//$$             Minecraft client = Minecraft.getInstance();
//$$             if (client.screen instanceof AbstractContainerScreen<?> containerScreen) {
//$$                 ContainerCategory category = GcaContainerDetector.detectGcaContainer(container, containerScreen.getTitle());
//$$                 if (category != ContainerCategory.NOT_GCA_CONTAINER) {
//$$                     List<Integer> currentSlots = new ArrayList<>(target.getSlotIndices());
//$$
//$$                     if (category == ContainerCategory.GCA_FAKE_PLAYER_INVENTORY) {
//$$                         currentSlots.removeIf(slotIndex -> slotIndex < 18 || slotIndex > 53);
//$$                     } else if (category == ContainerCategory.GCA_FAKE_PLAYER_ENDER_CHEST) {
//$$                         currentSlots.removeIf(slotIndex -> slotIndex < 27 || slotIndex > 53);
//$$                     }
//$$
//$$                     ((ItemAreaAccessor) (Object) target).setSlotIndices(currentSlots);
//$$                 }
//$$             }
//$$         } catch (Exception e) {
//$$             IpnExtrasClient.LOGGER.error("Error filtering GCA slots: {}", e.getMessage(), e);
//$$         }
//$$     }
//$$ }
//#endif
