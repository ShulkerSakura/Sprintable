package neko.shulker.sprintable;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

/**
 * Sprintable mod 入口。
 *
 * 功能：为 1.3.2 Minecraft 添加一个独立的 sprint 按键（默认 LCTRL）。
 * 按下时与双击W效果一致（vanilla setSprinting）。
 *
 * i18n：从 jar 内 /assets/sprintable/lang/*.lang 加载翻译文件，
 *       修改/新增语言只需要改 lang 文件，不需要改代码。
 *
 * 注意：不使用 LanguageRegistry.loadLocalization()，
 *       因为它内部调 Properties.load(InputStream) 在 Java 8 下按 ISO-8859-1 读取，
 *       UTF-8 多字节中文会乱码。改用自己读 UTF-8 Reader 的方式。
 */
@Mod(modid = "sprintable", name = "Sprintable", version = "1.0.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, versionBounds = "[1.0.0]")
public class sprintableMod
{
    /** 支持的语言列表。新增语言只需在此添加，并放对应的 lang 文件到 jar 里。 */
    private static final String[] SUPPORTED_LANGS = {
        "en_US", "zh_CN", "zh_TW"
    };

    /** lang 文件在 jar 里的路径模板。 */
    private static final String LANG_PATH = "/assets/sprintable/lang/%s.lang";

    @SidedProxy(clientSide = "neko.shulker.sprintable.ClientProxy",
                serverSide = "neko.shulker.sprintable.CommonProxy")
    public static CommonProxy proxy;

    @Mod.PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        // 加载所有支持语言的 .lang 文件
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
    }

    @Mod.Init
    public void init(FMLInitializationEvent event)
    {
        // 客户端会调 ClientProxy.registerClient()（注册 KeyBinding + TickHandler）
        // 服务端会调 CommonProxy.registerClient()（no-op）
        proxy.registerClient();
    }
}
