package neko.shulker.sprintable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

@Mod(modid = "sprintable", name = "Sprintable", version = "1.0.0", useMetadata = true)
public class SprintableMod {

    public static final String VERSION = "1.0.0";

    /** 创建 Logger  */
    private static final Logger logger = Logger.getLogger("Sprintable");

    /** 支持的语言列表。新增语言只需在此添加，并放对应的 lang 文件到 jar 里。 */
    private static final String[] SUPPORTED_LANGS = {
        "en_US",
        "zh_CN",
        "zh_TW"
    };
    /** lang 文件在 jar 里的路径模板。 */
    private static final String LANG_PATH = "/assets/sprintable/lang/%s.lang";

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event) {

        logger.info("Sprintable version: " + VERSION);
        logger.info("PreInit Loading ...");
        
        // 加载所有支持语言的 .lang 文件
        logger.info("Loading languages");
        // 手动用 UTF-8 InputStreamReader 读取，避免 Properties.load(InputStream) 的 ISO-8859-1 问题
        for (String lang : SUPPORTED_LANGS)
        {
            String path = String.format(LANG_PATH, lang);
            URL url = getClass().getResource(path);
            if (url == null)
            {
                continue;  // 该语言文件未找到，跳过（不影响其他语言）
            }

            Properties langPack = new Properties();
            InputStream is = null;
            BufferedReader reader = null;
            try
            {
                is = url.openStream();
                // 关键：用 UTF-8 而不是默认 ISO-8859-1
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                langPack.load(reader);
                LanguageRegistry.instance().addStringLocalization(langPack, lang);
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
                    if (is != null) is.close();
                }
                catch (java.io.IOException ex)
                {
                    // 忽略关闭异常
                }
            }
        }

        // 强制重新加载当前语言表，把 mod 注册的翻译合并进 vanilla translateTable
        // （不调这行的话，必须等用户在语言菜单切换一次才会生效）
        LanguageRegistry.reloadLanguageTable();
        logger.info("Languages loaded");
    }

    public static KeyBinding keyBindSprint;

    @Mod.Init
    public void init(FMLInitializationEvent event) {

        logger.info("Init Loading ...");

        keyBindSprint = new KeyBinding("key.sprint", 29);

        Minecraft mc = FMLClientHandler.instance().getClient();

        if (mc != null && mc.gameSettings != null) {
            GameSettings settings = mc.gameSettings;
            KeyBinding[] oldArray = settings.keyBindings;

            for (KeyBinding kb : oldArray) {
                if (kb == keyBindSprint || "key.sprint".equals(kb.keyDescription)) {
                    // 证明已经注入过键位，可以绕过
                    return;
                }
            }
            /* 创建新的长度为旧数组+1的键位数组，并拼接数组内容，将疾跑键位添加在数组最后 */
            KeyBinding[] newArray = new KeyBinding[oldArray.length + 1];
            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
            newArray[oldArray.length] = keyBindSprint;
            settings.keyBindings = newArray;

            /* 重建全局键位设置hash */
            KeyBinding.resetKeyBindingArrayAndHash();

        }

        /* 注册用于监听键位是否按下并触发疾跑的Tick级处理器 */
        TickRegistry.registerTickHandler(new SprintableTickHandler(), Side.CLIENT);

    }

    @Mod.PostInit
    public void postInit(FMLPostInitializationEvent event) {

        logger.info("Load Success");
    }
}
