package com.github.commandcracker.melodymod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = MelodyMod.MODID, version = MelodyMod.VERSION)
public class MelodyMod {
    public static final String MODID = "melodymod";
    public static final String VERSION = "1.0";
    Minecraft mc;

    public MelodyMod() {
        mc = Minecraft.getMinecraft();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public boolean harpGuiIsOpen() {
        if (mc.thePlayer.openContainer instanceof ContainerChest) {
            return ((ContainerChest) mc.thePlayer.openContainer).getLowerChestInventory().getName().startsWith("Harp - ");
        }
        return false;
    }

    private ItemStack lastClickedItem;

    @SubscribeEvent
    public void onTick(TickEvent tickEvent) {
        try {
            if (tickEvent.phase == TickEvent.Phase.START && mc.currentScreen instanceof GuiChest && harpGuiIsOpen()) {
                IInventory inv = ((ContainerChest) mc.thePlayer.openContainer).getLowerChestInventory();
                for (int i = 0; i < inv.getSizeInventory(); ++i) {
                    ItemStack itemStack = inv.getStackInSlot(i);
                    if (itemStack != null && Item.getIdFromItem(itemStack.getItem()) == 155) {
                        if (itemStack != lastClickedItem) {
                            mc.getNetHandler().addToSendQueue(new C0EPacketClickWindow(
                                    mc.thePlayer.openContainer.windowId,
                                    i,
                                    0,
                                    3,
                                    itemStack,
                                    mc.thePlayer.openContainer.getNextTransactionID(mc.thePlayer.inventory)
                            ));
                            lastClickedItem = itemStack;
                        }
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
