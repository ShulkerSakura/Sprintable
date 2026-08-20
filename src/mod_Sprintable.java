package net.minecraft.src;

import java.util.Properties;

import net.minecraft.client.Minecraft;

public class mod_Sprintable extends BaseMod
{
    private static final String SPRINT_KEY_NAME = "key.sprint";
    private static final int DEFAULT_SPRINT_KEY = 29;

    private KeyBinding keyBindSprint;
    private boolean wasSprintKeyDown;
    private String lastLanguage;

    @Override
    public String getVersion()
    {
        return "1.0.0";
    }

    @Override
    public void load()
    {
        keyBindSprint = new KeyBinding(SPRINT_KEY_NAME, DEFAULT_SPRINT_KEY);
        injectSprintKeyBinding();
        ModLoader.SetInGameHook(this, true, false);
        updateSprintLocalization();
        System.out.println("[Sprintable] Loaded");
    }

    @Override
    public boolean OnTickInGame(float partialTick, Minecraft minecraft)
    {
        updateSprintLocalization();

        if (minecraft.thePlayer == null || minecraft.currentScreen != null)
        {
            wasSprintKeyDown = false;
            return true;
        }

        boolean sprintKeyDown = keyBindSprint.pressed;
        if (sprintKeyDown && !wasSprintKeyDown)
        {
            minecraft.thePlayer.setSprinting(true);
        }

        wasSprintKeyDown = sprintKeyDown;
        return true;
    }

    private void injectSprintKeyBinding()
    {
        Minecraft minecraft = ModLoader.getMinecraftInstance();
        if (minecraft == null || minecraft.gameSettings == null)
        {
            return;
        }

        GameSettings settings = minecraft.gameSettings;
        KeyBinding[] keyBindings = settings.keyBindings;
        for (int i = 0; i < keyBindings.length; i++)
        {
            if (SPRINT_KEY_NAME.equals(keyBindings[i].keyDescription))
            {
                keyBindSprint = keyBindings[i];
                return;
            }
        }

        KeyBinding[] newKeyBindings = new KeyBinding[keyBindings.length + 1];
        System.arraycopy(keyBindings, 0, newKeyBindings, 0, keyBindings.length);
        newKeyBindings[keyBindings.length] = keyBindSprint;
        settings.keyBindings = newKeyBindings;

        KeyBinding.resetKeyBindingArrayAndHash();
    }

    private void updateSprintLocalization()
    {
        String language = StringTranslate.getInstance().func_44024_c();
        if (language.equals(lastLanguage))
        {
            return;
        }

        lastLanguage = language;
        String label = "Sprint";
        if ("zh_CN".equals(language) || "zh_TW".equals(language))
        {
            label = "\u75be\u8dd1";
        }

        try
        {
            Properties translateTable = (Properties)ModLoader.getPrivateValue(
                    StringTranslate.class, StringTranslate.getInstance(), 1);
            translateTable.put(SPRINT_KEY_NAME, label);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
