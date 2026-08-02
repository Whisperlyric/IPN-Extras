package dev.whisperlyric.ipnextras.mixin;

import org.anti_ad.mc.ipnext.inventory.ItemArea;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ItemArea.class, remap = false)
public interface ItemAreaAccessor {

    //#if MC<260100
    // In MC < 26.1 IPN, getSlotIndices() is not public - accessor needed for field access
    @Accessor("slotIndices")
    List<Integer> getSlotIndices();
    //#endif
    // In MC >= 26.1, ItemArea already has public getSlotIndices() - no accessor needed

    @Accessor("slotIndices")
    void setSlotIndices(List<Integer> slotIndices);
}
