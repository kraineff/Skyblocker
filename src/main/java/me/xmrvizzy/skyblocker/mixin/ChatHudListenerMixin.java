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

    private final static String NUMBER_REGEX = "[0-9]{1-3}(,[0-9]{3})*(.[0-9])?";
    private final static String ABILITY_COOLDOWN_REGEX = "^This ability is( currently)? on cooldown for " + NUMBER_REGEX + "( more )?s(econds?)?\\.$";
    private final static String ZOMBIE_SWORD_CHARGES_REGEX = "^No more charges, next one in " + NUMBER_REGEX + "s!$";
    private final static String HEALED_REGEX = "^(You|[a-zA-Z0-9_]{1,16}) healed you(rself)? for " + NUMBER_REGEX + " health!$";
    private final static String BLOCKS_IN_WAY_REGEX = "^There are blocks in the way!$";
    private final static String MAGIC_AOE_REGEX = "^Your %s hit " + NUMBER_REGEX + " enem(y|ies) for  " + NUMBER_REGEX + " damage\\.$";
    private final static String IMPLOSION_REGEX = String.format(MAGIC_AOE_REGEX, "Implosion");
    private final static String MOLTEN_WAVE_REGEX = String.format(MAGIC_AOE_REGEX, "Molten Wave");

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
                    msg.matches(ABILITY_COOLDOWN_REGEX) ||
                    msg.matches(ZOMBIE_SWORD_CHARGES_REGEX))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideHeal &&
                    msg.matches(HEALED_REGEX))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideAOTE &&
                    msg.matches(BLOCKS_IN_WAY_REGEX))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideImplosion &&
                    msg.matches(IMPLOSION_REGEX))
                ci.cancel();

            if (SkyblockerConfig.get().messages.hideMoltenWave &&
                    msg.contains(MOLTEN_WAVE_REGEX))
                ci.cancel();
        }
    }
}

