package com.elytradev.concrete.utilpackets;

import com.elytradev.concrete.common.ExtensiveToast;
import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
public class DisplayToastMessage extends Message {

    @MarshalledAs("string")
    public String title;

    @MarshalledAs("string")
    public String subtitle;

    public DisplayToastMessage(NetworkContext ctx) {
        super(ctx);
    }

    public DisplayToastMessage(NetworkContext ctx, String title, String subtitle) {
        super(ctx);
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void handle(EntityPlayer player) {
        //Minecraft.getMinecraft().getToastGui().add(new ExtensiveToast(title, subtitle));
        Minecraft.getMinecraft().getToastGui().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE,
                new TextComponentString("hello"),new TextComponentString("World")));
    }
}
