package mrkartoshki.rawlands.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModMusic {
    private static final String namespace = "rawlands";

    public static final SoundEvent DEAD_FOREST = registerSound(namespace, "music.dead_forest");

    public static void init () {
        //ok ig nothing has to go here to make it work... thats interesting
    }
    public static SoundEvent registerSound (String ns, String path) {
        SoundEvent temp = Registry.register(
                BuiltInRegistries.SOUND_EVENT,
                Identifier.fromNamespaceAndPath(ns, path),
                SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(ns, path))
        );

        return temp;
    }
}
