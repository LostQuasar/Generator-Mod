package net.furia.generator.screen;

import net.furia.generator.GeneratorMod;
import net.furia.generator.block.entity.GeneratorBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class GeneratorScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final GeneratorBlockEntity blockEntity;

    public GeneratorScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(3));
    }

    public GeneratorScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.GENERATOR_SCREEN_HANDLER, syncId);
        checkSize(((Inventory) blockEntity), 3);
        this.inventory = ((Inventory) blockEntity);
        this.propertyDelegate = arrayPropertyDelegate;
        this.blockEntity = (GeneratorBlockEntity) blockEntity;

        // add GUI slots
        this.addSlot(new Slot(this.inventory, 0, 62, 26));
        this.addSlot(new Slot(this.inventory, 1, 98, 26));
        this.addSlot(new OutputSlot(this.inventory, 2, 80, 54));

        // player inventory + hotbar
        addPlayerInventory(playerInventory);

        this.addProperties(arrayPropertyDelegate);
    }

    public boolean isDisabled() { return propertyDelegate.get(2) > 0; }

    public boolean isCrafting() { return propertyDelegate.get(0) > 0; }

    public int getScaledProgress() {
        int i = propertyDelegate.get(0);
        int j = propertyDelegate.get(1);
        int pixels = 20;
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    public int getScaledPercentage() {
        int i = propertyDelegate.get(0);
        int j = propertyDelegate.get(1);
        return j != 0 && i != 0 ? i * 100 / j : 0;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public void addPlayerInventory(PlayerInventory playerInventory) {
        // inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
               this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        // hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
    }
}
