package net.furia.generator.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.furia.generator.GeneratorMod;
import net.furia.generator.recipe.GeneratorRecipes;
import net.furia.generator.screen.GeneratorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class GeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int OUTPUT_SLOT = 2;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 1;
    private int isDisabled = 0;

    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENERATOR_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> GeneratorBlockEntity.this.progress;
                    case 1 -> GeneratorBlockEntity.this.maxProgress;
                    case 2 -> GeneratorBlockEntity.this.isDisabled;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> GeneratorBlockEntity.this.progress = value;
                    case 1 -> GeneratorBlockEntity.this.maxProgress = value;
                    case 2 -> GeneratorBlockEntity.this.isDisabled = value;
                }
            }

            @Override
            public int size() {
                return 3;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.generator.generator");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GeneratorScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("generator.progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("generator.progress");
        super.readNbt(nbt);
    }


    public void tick(World world, BlockPos pos, BlockState state) {

        // check if it's client. (don't run)
        if (world.isClient()) {
            return;
        }

        // Check if it's receiving redstone power
        if (world.isReceivingRedstonePower(pos)) {
            disableBlock();
        } else {
            enableBlock();
        }

        // set max progress
        if (hasRecipe()) {
            Optional<GeneratorRecipes> recipe = getCurrentRecipe();
            this.maxProgress = recipe.get().getTime();
        } else {
            resetProgress();
        }

        // recipe logic
        if(canInsertIntoOutputSlot() && hasRecipe()) {
            increaseCraftingProgress();
            markDirty(world, pos, state);

            if(hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 20;
    }

    public void disableBlock() {
        this.isDisabled = 1;
    }

    public void enableBlock() {
        this.isDisabled = 0;
    }

    private boolean getIsDisabled() {
        return this.isDisabled == 1;
    }

    private void craftItem() {
        Optional<GeneratorRecipes> recipe = getCurrentRecipe();

        this.setStack(OUTPUT_SLOT, new ItemStack(recipe.get().getOutput(null).getItem(),
                this.getStack(OUTPUT_SLOT).getCount() + recipe.get().getOutput(null).getCount()));
    }

    private boolean hasCraftingFinished() {
        if (this.progress >= this.maxProgress) {
            this.progress = 0;
            return true;
        }
        return false;
    }

    private void increaseCraftingProgress() {
        if (!getIsDisabled()) {
            this.progress++;
        }
    }

    private boolean hasRecipe() {
        Optional<GeneratorRecipes> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack output = recipe.get().getOutput(null);

        return canInsertAmountIntoOutputSlot(output.getCount())
                && canInsertItemIntoOutputSlot(output);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.getStack(OUTPUT_SLOT).getCount() + count <= this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getItem() == output.getItem();

    }

    private boolean canInsertIntoOutputSlot() {
        return this.getStack(OUTPUT_SLOT).isEmpty() ||
                this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    private Optional<GeneratorRecipes> getCurrentRecipe() {
        SimpleInventory inventory = new SimpleInventory(this.size());
        for (int i = 0; i < this.size(); i++) {
            inventory.setStack(i, this.getStack(i));
        }
        return Objects.requireNonNull(this.getWorld()).getRecipeManager().getFirstMatch(GeneratorRecipes.Type.INSTANCE, inventory, this.getWorld());
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return (slot == 0 || slot == 1);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 2;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{2};
    }

}
