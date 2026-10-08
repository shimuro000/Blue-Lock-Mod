package com.bluelockmod.football;

import com.bluelockmod.registry.ModEntities;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class FootballItem extends Item {
    public FootballItem(Properties properties) { super(properties); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            FootballEntity ball = new FootballEntity(ModEntities.FOOTBALL.get(), level);
            ball.setPos(player.getX(), player.getEyeY() - 0.25D, player.getZ());
            level.addFreshEntity(ball);
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
