package com.elytradev.concrete.utilpackets;

import com.elytradev.concrete.common.ExtensiveToast;
import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.client.gui.toasts.IToast.TEXTURE_TOASTS;

@ReceivedOn(Side.CLIENT)
public class DisplayToastMessage extends Message {

	@MarshalledAs("string")
	public String title;

	@MarshalledAs("string")
	public String subtitle;

	@MarshalledAs("i64")
	public long timing;

	@MarshalledAs("string")
	public String texture;

	@MarshalledAs("i32")
	public int titleColor;

    @MarshalledAs("i32")
	public int subtitleColor;

    @MarshalledAs("i32")
	public int textureX;

    @MarshalledAs("i32")
	public int textureY;

	private static final long DEFAULT_TIMING = 5000;
	private static final int DEFAULT_TITLE_COLOR = -256;
	private static final int DEFAULT_SUBTITLE_COLOR = -1;
	private static final int DEFAULT_X = 0;
	private static final int DEFAULT_Y = 96;

	public DisplayToastMessage(NetworkContext ctx) {
		super(ctx);
	}

	public DisplayToastMessage(NetworkContext ctx, @Nonnull String title) {
		this(ctx, title, null, DEFAULT_TIMING, TEXTURE_TOASTS.toString(), DEFAULT_TITLE_COLOR, DEFAULT_SUBTITLE_COLOR, DEFAULT_X, DEFAULT_Y);
	}

	public DisplayToastMessage(NetworkContext ctx, @Nonnull String title, @Nullable String subtitle) {
		this(ctx, title, subtitle, DEFAULT_TIMING, TEXTURE_TOASTS.toString(), DEFAULT_TITLE_COLOR, DEFAULT_SUBTITLE_COLOR, DEFAULT_X, DEFAULT_Y);
	}

	public DisplayToastMessage(NetworkContext ctx, @Nonnull String title, @Nullable String subtitle, long timing) {
		this(ctx, title, subtitle, timing, TEXTURE_TOASTS.toString(), DEFAULT_TITLE_COLOR, DEFAULT_SUBTITLE_COLOR, DEFAULT_X, DEFAULT_Y);
	}


	public DisplayToastMessage(NetworkContext ctx,
                               @Nonnull String title,
                               @Nullable String subtitle,
                               long timing,
                               @Nonnull String texture,
                               int titleColor,
                               int subtitleColor,
                               int textureX,
                               int textureY) {
	    super(ctx);
	    this.title = title;
	    this.subtitle = subtitle;
	    this.timing = timing;
	    this.texture = texture;
        this.titleColor  = titleColor;
        this.subtitleColor = subtitleColor;
        this.textureX = textureX;
        this.textureY = textureY;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer player) {
		Minecraft.getMinecraft().getToastGui().add(new ExtensiveToast(title, subtitle, timing, texture, titleColor, subtitleColor, textureX, textureY));
	}
}
