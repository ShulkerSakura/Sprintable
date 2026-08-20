package neko.shulker.sprintable;

import java.util.EnumSet;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.Potion;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

/**
 * Sprint 按键处理。
 *
 * 每个 PLAYER tick 检查 sprint 按键是否按下，按下且条件满足就 setSprinting(true)。
 * 复用 vanilla 的 sprint 限制（饥饿 > 6、不在使用物品、无失明等），不破坏生存平衡。
 *
 * 此类放在 common 侧，但只在客户端被注册和执行（ClientProxy.registerClient 里注册）。
 * 由于此类编译在 common 侧，不能直接 import ClientProxy（在 src/minecraft），
 * 用反射调用 ClientProxy.isSprintKeyDown() 读取按键状态。
 */
public class SprintTickHandler implements ITickHandler
{
    private static Boolean clientProxyChecked = null;
    private static java.lang.reflect.Method isSprintKeyDownMethod = null;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (!type.contains(TickType.PLAYER))
        {
            return;
        }

        if (tickData.length == 0 || !(tickData[0] instanceof EntityPlayerSP))
        {
            return;
        }

        EntityPlayerSP player = (EntityPlayerSP) tickData[0];

        if (!isSprintKeyPressed())
        {
            return;
        }

        // 复用 vanilla sprint 触发条件
        boolean canSprint = player.getFoodStats().getFoodLevel() > 6.0F
                || player.capabilities.allowFlying;

        if (canSprint
                && !player.isUsingItem()
                && !player.isPotionActive(Potion.blindness)
                && player.movementInput.moveForward > 0.0F)
        {
            player.setSprinting(true);
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        // 不处理
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel()
    {
        return "SprintableTickHandler";
    }

    /**
     * 通过反射调用 ClientProxy.isSprintKeyDown()，避免 common 侧编译时找不到 ClientProxy。
     * 反射只解析一次 Method，后续调用开销很小。
     * 服务端此方法返回 false（找不到 ClientProxy 类）。
     */
    private boolean isSprintKeyPressed()
    {
        try
        {
            if (clientProxyChecked == null)
            {
                try
                {
                    Class<?> clientProxy = Class.forName("neko.shulker.sprintable.ClientProxy");
                    isSprintKeyDownMethod = clientProxy.getMethod("isSprintKeyDown");
                    clientProxyChecked = Boolean.TRUE;
                }
                catch (Throwable t)
                {
                    clientProxyChecked = Boolean.FALSE;
                    return false;
                }
            }

            if (clientProxyChecked == Boolean.FALSE)
            {
                return false;
            }

            Object result = isSprintKeyDownMethod.invoke(null);
            return (Boolean) result;
        }
        catch (Throwable t)
        {
            return false;
        }
    }
}
