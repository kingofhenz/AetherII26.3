package com.aetherteam.aetherii.mixin.mixins.common;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.level.portal.TeleportTransition;
import com.aetherteam.aetherii.attachment.AetherIIDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(at = @At(value = "HEAD"), method = "disconnect()V")
    private void disconnect(CallbackInfo ci) {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
        serverPlayer.getAttachedOrCreate(AetherIIDataAttachments.AERBUNNY_MOUNT).removeAerbunny();
    }

    /**
     * NeoForge's {@code PlayerRespawnPositionEvent}: outposts override the respawn location.
     */
    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("RETURN"), cancellable = true)
    private void aether_ii$respawnPosition(CallbackInfoReturnable<TeleportTransition> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        TeleportTransition transition = player.getAttachedOrCreate(AetherIIDataAttachments.OUTPOST_TRACKER).findOutpostRespawnLocation(player);
        if (transition != null) {
            cir.setReturnValue(transition);
        }
    }

    /**
     * {@code EntityTravelToDimensionEvent} for players: ServerPlayer overrides {@code Entity#teleport} without calling
     * super, so {@link EntityMixin}'s hook never sees players.
     */
    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;", at = @At("HEAD"), cancellable = true)
    private void aether_ii$travelToDimension(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (transition.newLevel().dimension() != self.level().dimension()
                && !com.aetherteam.aetherii.event.AetherIIEvents.ENTITY_TRAVEL_TO_DIMENSION.invoker().allowTravel(self, transition.newLevel().dimension())) {
            cir.setReturnValue(null);
        }
    }
}
