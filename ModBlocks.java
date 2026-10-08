package com.bluelockmod.registry;

import com.bluelockmod.BlueLockMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BlueLockMod.MOD_ID);
    public static final RegistryObject<Block> MATCH_TERMINAL = register("match_terminal", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(2.0F));
    public static final RegistryObject<Block> TRAINING_STATION = register("training_station", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(2.0F));

    private static RegistryObject<Block> register(String name, BlockBehaviour.Properties properties) {
        RegistryObject<Block> block = BLOCKS.register(name, () -> new Block(properties));
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
    private ModBlocks() {}
}
