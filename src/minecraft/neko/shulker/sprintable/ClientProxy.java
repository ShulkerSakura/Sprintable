package neko.shulker.sprintable;

import net.minecraft.src.GameSettings;
import net.minecraft.src.KeyBinding;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.registry.TickRegistry;

/**
 * 客户端 Proxy。只在客户端（src/minecraft 侧）存在。
 *
 * 实现：运行时把 sprint KeyBinding 注入到 vanilla GameSettings.keyBindings 数组末尾。
 *
 * 优点：
 * - 不修改任何 vanilla 源码
 * - 不修改任何 .class 文件（reobf 后只有 mod 自己的 class）
 * - 不需要 ASM / coremod
 * - 与其他 mod 兼容（各自追加自己的 KeyBinding，互不干扰）
 * - 按键显示在"控制设置"面板，用户可在游戏内改键
 * - 按键状态自动保存到 options.txt
 *
 * 原理：
 * 1. @Mod.Init 阶段 Minecraft.gameSettings 已经创建完毕
 * 2. new KeyBinding(...) 构造函数会自动注册到全局 keybindArray + hash
 * 3. 反射/直接访问 GameSettings.keyBindings（public 字段），扩展数组追加 keyBindSprint
 * 4. 调用 KeyBinding.resetKeyBindingArrayAndHash() 重建全局 hash
 * 5. 之后 vanilla 的 GuiControlsScrollPanel 会自动渲染新按键
 *    vanilla 的 saveOptions/loadOptions 会自动保存/加载这个按键
 */
public class ClientProxy extends CommonProxy
{
    /** 疾跑按键绑定。LCTRL 默认键码 29（LWJGL Keyboard.KEY_LCONTROL）。 */
    public static KeyBinding keyBindSprint;

    @Override
    public void registerClient()
    {
        // 1. 创建 KeyBinding（构造函数自动注册到全局 keybindArray + hash）
        keyBindSprint = new KeyBinding("key.sprint", 29);

        // 2. 把 keyBindSprint 注入到 GameSettings.keyBindings 数组末尾
        Minecraft mc = FMLClientHandler.instance().getClient();
        if (mc != null && mc.gameSettings != null)
        {
            GameSettings settings = mc.gameSettings;
            KeyBinding[] old = settings.keyBindings;
            // 检查是否已经注入过（防止重复）
            for (KeyBinding kb : old)
            {
                if (kb == keyBindSprint || "key.sprint".equals(kb.keyDescription))
                {
                    // 已注入，跳过
                    return;
                }
            }
            // 扩展数组
            KeyBinding[] newArray = new KeyBinding[old.length + 1];
            System.arraycopy(old, 0, newArray, 0, old.length);
            newArray[old.length] = keyBindSprint;
            settings.keyBindings = newArray;

            // 3. 重建全局 hash（让新按键能被 Keyboard 事件命中）
            KeyBinding.resetKeyBindingArrayAndHash();
        }

        // 4. 注册 tick handler
        TickRegistry.registerTickHandler(new SprintTickHandler(), Side.CLIENT);
    }

    /**
     * 查询 sprint 按键是否被按住。
     * 由 SprintTickHandler 通过反射调用（common 侧不能直接 import 此类）。
     */
    public static boolean isSprintKeyDown()
    {
        return keyBindSprint != null && keyBindSprint.pressed;
    }
}
