package neko.shulker.sprintable;

import java.util.EnumSet;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.Potion;

public class SprintableTickHandler implements ITickHandler {

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

        if (!mod_Sprintable.isSprintKeyDown())
        {
            return;
        }

        EntityPlayerSP player = (EntityPlayerSP) tickData[0];
        boolean canSprint = (float) player.getFoodStats().getFoodLevel() > 6.0F;
        if (canSprint && !player.isUsingItem() && !player.isPotionActive(Potion.blindness)
                && player.movementInput.moveForward > 0.0F)
        {
            player.setSprinting(true);
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
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
}
