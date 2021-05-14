package me.xmrvizzy.skyblocker.mixin;

import me.xmrvizzy.skyblocker.config.SkyblockerConfig;
import me.xmrvizzy.skyblocker.skyblock.dungeon.DungeonPuzzles;
import me.xmrvizzy.skyblocker.skyblock.dwarven.Fetchur;
import me.xmrvizzy.skyblocker.skyblock.dwarven.Puzzler;
import me.xmrvizzy.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(ChatHudListener.class)
public class ChatHudListenerMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onMessage(MessageType messageType, Text message, UUID senderUuid, CallbackInfo ci) {
        String msg = message.getString();

        if (Utils.isDungeons) {
            if (SkyblockerConfig.get().locations.dungeons.solveThreeWeirdos && msg.contains("[NPC]"))
                DungeonPuzzles.threeWeirdos(msg);
        }

        if (Utils.isSkyblock) {
            if (msg.contains("[OPEN MENU]")) {
                List<Text> siblings = message.getSiblings();
                for (Text sibling : siblings) {
                    if (sibling.getString().contains("[OPEN MENU]")) {
                        this.client.player.sendChatMessage(sibling.getStyle().getClickEvent().getValue());
                    }
                }
            }

            if (msg.contains("[NPC]")) {
                if (SkyblockerConfig.get().locations.dwarvenMines.solveFetchur &&
                        msg.contains("Fetchur")) {
                    Fetchur.solve(msg, ci);
                }

                if (SkyblockerConfig.get().locations.dwarvenMines.solvePuzzler &&
                        msg.contains("Puzzler"))
                    Puzzler.solve(msg);
            }

            if (SkyblockerConfig.get().messages.hideAbility &&
                    msg.matches("^This ability is( currently)? on cooldown for [0-9.]+( more )?s(econds?)?\\.$") ||
                    msg.matches("^No more charges, next one in [0-9.]+s!$"))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideHeal &&
                    msg.matches("^You healed yourself for [0-9,.]+ health!$") ||
                    msg.matches("^[a-zA-Z0-9_]{1,16} healed you for [0-9,.]+ health!$"))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideAOTE &&
                    msg.matches("^There are blocks in the way!$"))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideImplosion &&
                    msg.matches("^Your Implosion hit [0-9]+ enem(y|ies) for [0-9,.]+ damage\\.$"))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideMoltenWave &&
                    msg.contains("^Your Molten Wave hit [0-9]+ enem(y|(ies)) for [0-9,.]+ damage\\.$"))
                ci.cancel();
        }
    }
}

