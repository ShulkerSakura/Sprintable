package neko.shulker.sprintable;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.potion.Potion;

import java.util.EnumSet;

public class SprintableTickHandler implements ITickHandler {

    KeyBinding keyBindSprint = SprintableMod.keyBindSprint;

    private boolean isSprintKeyPressed() {
        return keyBindSprint != null && keyBindSprint.pressed;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        /* 判断tick类型是否包含玩家 */
        if (!type.contains(TickType.PLAYER))
        {
            return;
        }
        /* 判断 */
        if (tickData.length == 0 || !(tickData[0] instanceof EntityPlayerSP))
        {
            return;
        }

        EntityPlayerSP player = (EntityPlayerSP) tickData[0];
        /* 判断 */
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
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        // DO NOTHING
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel() {
        return "SprintableTickHandler";
    }
}
