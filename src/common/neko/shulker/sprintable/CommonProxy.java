package neko.shulker.sprintable;

/**
 * 服务端 / 通用 Proxy。
 * 客户端专属逻辑（KeyBinding 等）不在这里实现，避免服务端加载时找不到客户端类。
 *
 * 子类 ClientProxy（仅客户端存在）会覆盖这些方法做真正的注册。
 */
public class CommonProxy
{
    /**
     * 注册客户端专属内容（KeyBinding、TickHandler 等）。
     * 服务端调用此方法时什么都不做。
     */
    public void registerClient()
    {
        // 服务端：no-op
    }
}
