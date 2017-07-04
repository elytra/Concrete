/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017:
 * 	William Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
 * 	and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.concrete.inventory.gui.client;

import java.io.IOException;

import com.elytradev.concrete.common.GuiDrawing;
import com.elytradev.concrete.inventory.gui.ConcreteContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;

public class ConcreteContainerGui extends GuiContainer {
	public static final int PADDING = 8;
	private final ConcreteContainer container;
	
	public ConcreteContainerGui(ConcreteContainer container) {
		super(container);
		this.container = container;
		this.xSize = container.getRootPanel().getWidth();
		this.ySize = container.getRootPanel().getHeight();
	}
	
	/*
	 * RENDERING NOTES:
	 * 
	 * * "width" and "height" are the width and height of the overall screen
	 * * "xSize" and "ySize" are the width and height of the panel to render
	 * * "left" and "top" are *actually* self-explanatory
	 * * coordinates start at 0,0 at the topleft of the screen.
	 */
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground(); //Call this so Forge can post a BackgroundDrawnEvent
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}
	
	/*
	 * These methods are called frequently and empty, meaning they're probably *meant* for subclasses to override to
	 * provide core GUI functionality.
	 */
	
	@Override
	public void initGui() {
		super.initGui();
		
		if (this.container.getRootPanel() != null) {
			this.container.getRootPanel().initClient();
			this.container.validate();
		}
	}
	
	@Override
	public void updateScreen() { //Will probably use this for animation!
		//System.out.println("updateScreen");
		super.updateScreen();
	}
	
	@Override
	public void onGuiClosed() {
		//System.out.println("onGuiClosed");
		super.onGuiClosed();
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		//...yeah, we're going to go ahead and override that.
		return false;
	}
	
	/*
	 * While these methods are implemented in GuiScreen, chances are we'll be shadowing a lot of the GuiScreen methods
	 * in order to implement our own button protocol and more advanced features.
	 */
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
	}
	
	/*
	 * We'll probably wind up calling some of this manually, but they do useful things for us so we may leave
	 * them unharmed.
	 */
	
	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);
		//System.out.println("setWorldAndResolution:" + width + "x" + height);
		guiLeft = (width  / 2) - (xSize / 2);
		guiTop =  (height / 2) - (ySize / 2);
	}
	
	@Override
	public void setGuiSize(int width, int height) {
		super.setGuiSize(width, height);
		//System.out.println("setGuiSize:" + width + "x" + height);
		guiLeft = (width  / 2) - (xSize / 2);
		guiTop =  (height / 2) - (ySize / 2);
	}
	
	/*
	 * SPECIAL FUNCTIONS: Where possible, we want to draw everything based on *actual GUI state and composition* rather
	 * than relying on pre-baked textures that the programmer then needs to carefully match up their GUI to.
	 */
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GuiDrawing.drawGuiPanel(guiLeft - PADDING, guiTop - PADDING, xSize + ((PADDING - 1) * 2), ySize + ((PADDING - 1) * 2));
		
		if (this.container.getRootPanel() != null) {
			this.container.getRootPanel().paintBackground(guiLeft, guiTop);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (this.container.getRootPanel() != null) {
			this.container.getRootPanel().paintForeground(0, 0);
			//(0, 0) because of GlStateManager.translate(guiLeft, guiTop, 0.0F) in GuiContainer.drawScreen
		}
	}
}
