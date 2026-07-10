package dev.tauri.jsg.core.common.item;

import dev.tauri.jsg.core.common.registry.CoreFonts;
import dev.tauri.jsg.core.common.registry.CoreTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class BlackChalkItem extends JSGItem implements SignApplicator {
    public BlackChalkItem() {
        super(new Item.Properties(), List.of(CoreTabs.TAB_TOOLS));
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean tryApplyToSign(Level level, SignBlockEntity sign, boolean frontSide, Player player) {
        if (level.isClientSide) return true;

        boolean success = sign.updateText((text) -> {
            boolean updated = false;
            Component[] rawMessages = text.getMessages(false);
            Component[] filteredMessages = text.getMessages(true);

            Component[] newRawMessages = new Component[4];
            Component[] newFilteredMessages = new Component[4];

            for (int i = 0; i < 4; i++) {
                if (rawMessages[i].getString().isEmpty() || rawMessages[i].getStyle().getFont() == CoreFonts.ANCIENT_FONT) {
                    newRawMessages[i] = rawMessages[i];
                    newFilteredMessages[i] = filteredMessages[i];
                    continue;
                }
                newRawMessages[i] = rawMessages[i].copy().withStyle(s -> s.withFont(CoreFonts.ANCIENT_FONT));
                newFilteredMessages[i] = filteredMessages[i].copy().withStyle(s -> s.withFont(CoreFonts.ANCIENT_FONT));
                updated = true;
            }

            if (updated) {
                return new SignText(newRawMessages, newFilteredMessages, text.getColor(), text.hasGlowingText());
            }
            return text;
        }, frontSide);

        if (success) {
            sign.setChanged();
            sign.saveWithFullMetadata(level.registryAccess());
            level.sendBlockUpdated(sign.getBlockPos(), sign.getBlockState(), sign.getBlockState(), 3);

            level.playSound(null, sign.getBlockPos(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }
}