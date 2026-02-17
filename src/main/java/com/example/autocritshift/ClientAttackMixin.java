package com.example.autocritshift;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ClientAttackMixin {

    private int shiftTicks = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return;

        if (shiftTicks > 0) {
            client.options.sneakKey.setPressed(true);
            shiftTicks--;
        } else {
            client.options.sneakKey.setPressed(false);
        }
    }

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void onAttack(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null) return;

        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof SwordItem)) return;

        if (!(client.crosshairTarget instanceof EntityHitResult)) return;

        boolean isCritical =
                !player.isOnGround() &&
                player.fallDistance > 0.0F &&
                !player.isSprinting() &&
                !player.isTouchingWater() &&
                !player.hasVehicle() &&
                !player.isClimbing();

        if (isCritical) {
            shiftTicks = 2;
        }
    }
}
