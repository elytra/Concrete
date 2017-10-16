package com.elytradev.concrete.utilpackets;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;

@ReceivedOn(Side.CLIENT)
public class MessageToast extends Message {
    public MessageToast(NetworkContext ctx) {
        super(ctx);
    }

    @Override
    protected void handle(EntityPlayer player) {
        if(player.world.isRemote) {
            Minecraft.getMinecraft().getToastGui().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT,
                    new TextComponentString("Test Test"),
                    new TextComponentString("1 2 3")));
        }
    }
}
