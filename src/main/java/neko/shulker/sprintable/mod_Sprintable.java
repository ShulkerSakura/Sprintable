package neko.shulker.sprintable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.src.BaseMod;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.ModLoader;

public class mod_Sprintable extends BaseMod {

    private static final String[] SUPPORTED_LANGS = {
        "en_US", "zh_CN", "zh_TW"
    };

    private static final String LANG_PATH = "/assets/sprintable/lang/%s.lang";

    public static KeyBinding keyBindSprint;

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    public static boolean isSprintKeyDown()
    {
        if (keyBindSprint == null)
        {
            return false;
        }

        int keyCode = keyBindSprint.keyCode;
        return keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode);
    }

    private void loadLocalizations()
    {
        for (String lang : SUPPORTED_LANGS)
        {
            URL url = getClass().getResource(String.format(LANG_PATH, lang));
            if (url == null)
            {
                continue;
            }

            Properties langPack = new Properties();
            InputStream input = null;
            BufferedReader reader = null;
            try
            {
                input = url.openStream();
                reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                langPack.load(reader);
                FMLCommonHandler handler = FMLCommonHandler.instance();
                Enumeration keys = langPack.keys();
                while (keys.hasMoreElements())
                {
                    String key = (String) keys.nextElement();
                    handler.addStringLocalization(key, lang, langPack.getProperty(key));
                }
            }
            catch (java.io.IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (reader != null) reader.close();
                    if (input != null) input.close();
                }
                catch (java.io.IOException e)
                {
                }
            }
        }
    }

    @Override
    public void load()
    {
        loadLocalizations();
        keyBindSprint = new KeyBinding("key.sprint", 29);
        ModLoader.registerKey(this, keyBindSprint, true);
        FMLCommonHandler.instance().registerTickHandler(new SprintableTickHandler());
        System.out.println("[Sprintable] Loaded");
    }

    @Override
    public void modsLoaded()
    {
        System.out.println("[Sprintable] Loading Success");
    }
}
