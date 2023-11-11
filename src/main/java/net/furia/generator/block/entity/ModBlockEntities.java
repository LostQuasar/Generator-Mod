package net.furia.generator.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.furia.generator.GeneratorMod;
import net.furia.generator.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<GeneratorBlockEntity> GENERATOR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            new Identifier(GeneratorMod.MOD_ID, "generator_block_entity"),
            FabricBlockEntityTypeBuilder.create(
                    GeneratorBlockEntity::new, ModBlocks.GENERATOR_BLOCK).build(null));

    public static void registerBlockEntities() {
        GeneratorMod.LOGGER.info("Registering Mod Block Entities...");

    }
}
