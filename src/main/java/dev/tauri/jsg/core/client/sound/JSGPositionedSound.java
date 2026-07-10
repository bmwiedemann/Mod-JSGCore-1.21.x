package dev.tauri.jsg.core.client.sound;

import dev.tauri.jsg.core.JSGCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JSGPositionedSound extends AbstractTickableSoundInstance {
    protected double fullVolumeDistance = 32;
    protected double hearAbleDistance = 64;
    protected float maxVolume;

    public JSGPositionedSound(BlockPos pos, SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource, boolean loop, float volume) {
        super(soundEvent, soundSource, randomSource);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.looping = loop;
        this.maxVolume = volume;
        this.relative = false;
    }

    public void stopPlaying() {
        getManager().stop(this);
    }

    public boolean isPlaying() {
        return Minecraft.getInstance().getSoundManager().isActive(this);
    }

    public void play() {
        try {
            if (!isPlaying())
                getManager().play(this);
        } catch (Exception e) {
            JSGCore.logger.error("", e);
        }
    }

    public Vec3 getPosition() {
        return new Vec3(x, y, z);
    }

    public double getDistanceFromSource() {
        LocalPlayer player = Minecraft.getInstance().player;
        Vec3 playerPos = (player == null ? JSGCore.lastPlayerPosInWorld.getCenter() : player.position());
        return getPosition().distanceTo(playerPos);
    }

    public float getVolume() {
        float localVolume;
        double distanceFromSource = getDistanceFromSource();

        var fullDistance = fullVolumeDistance;
        var maxDistance = hearAbleDistance;

        if (fullDistance >= maxDistance)
            maxDistance = fullDistance + 1;

        if (distanceFromSource <= fullDistance)
            localVolume = getMaxVolume();
        else if (distanceFromSource <= maxDistance)
            localVolume = (float) (getMaxVolume() * ((distanceFromSource - fullDistance) / (maxDistance - fullDistance)));
        else
            localVolume = getMinVolume();

        return localVolume;
    }

    public float getMaxVolume() {
        return maxVolume;
    }

    public float getMinVolume() {
        return 0.0F;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }


    private static SoundManager getManager() {
        return Minecraft.getInstance().getSoundManager();
    }

    @Override
    public void tick() {
    }
}
