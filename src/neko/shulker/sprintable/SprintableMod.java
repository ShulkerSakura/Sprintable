package neko.shulker.sprintable;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import java.util.logging.Logger;

@Mod(modid = "sprintable", name = "Sprintable", version = "1.0.0", useMetadata = true)
public class SprintableMod {

    public static final String VERSION = "1.0.0";

    @Mod.Instance("sprintable")
    public static SprintableMod instance;

    /** 创建 Logger  */
    private static final Logger logger = Logger.getLogger("Sprintable");

    public static KeyBinding keyBindSprint;

    @Mod.EventHandler
    public void preInit(FMLInitializationEvent event) {
        logger.info("Loading...");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        keyBindSprint = new KeyBinding("key.sprint", 29);
        Minecraft mc = FMLClientHandler.instance().getClient();
        GameSettings settings = mc.gameSettings;
        if (settings != null) {
            KeyBinding[] oldArray = settings.keyBindings;
            for (KeyBinding kb : oldArray) {
                if (kb == keyBindSprint || "key.sprint".equals(kb.keyDescription)) {
                    return;
                }
            }
            KeyBinding[] newArray = new KeyBinding[oldArray.length + 1];
            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
            newArray[oldArray.length] = keyBindSprint;
            settings.keyBindings = newArray;

            KeyBinding.resetKeyBindingArrayAndHash();
        }
        TickRegistry.registerTickHandler(new SprintableTickHandler(), Side.CLIENT);
    }

    @Mod.EventHandler
    public void postInit(FMLInitializationEvent event) {
        logger.info("Load Success!");
    }
}
