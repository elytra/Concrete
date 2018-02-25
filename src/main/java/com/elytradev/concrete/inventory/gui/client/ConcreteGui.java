/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018:
 * 	Una Thompson (unascribed),
 * 	Isaac Ellingson (Falkreon),
 * 	Jamie Mansfield (jamierocks),
 * 	Alex Ponebshek (capitalthree),
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

import com.elytradev.concrete.inventory.gui.ConcreteContainer;
import com.elytradev.concrete.inventory.gui.widget.WPanel;
import com.elytradev.concrete.inventory.gui.widget.WWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;

public class ConcreteGui extends GuiContainer {
	public static final int PADDING = 8;
	private final ConcreteContainer container;
	private WWidget lastResponder = null;
	
	public ConcreteGui(ConcreteContainer container) {
		super(container);
		this.container = container;
		this.xSize = 18 * 9;
		this.ySize = 18 * 9;
	}
	
	/*
	 * RENDERING NOTES:
	 * 
	 * * "width" and "height" are the width and height of the overall screen
	 * * "xSize" and "ySize" are the width and height of the panel to render
	 * * "left" and "top" are *actually* self-explanatory
	 * * coordinates start at 0,0 at the topleft of the screen.
	 */
	
	//@Override
	//public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	//}
	
	
	/*
	 * These methods are called frequently and empty, meaning they're probably *meant* for subclasses to override to
	 * provide core GUI functionality.
	 */
	
	@Override
	public void initGui() {
		container.validate();
		super.initGui();
	}
	
	//Will probably re-activate for animation!
	//@Override
	//public void updateScreen() {
	//	System.out.println("updateScreen");
	//}
	
	@Override
	public void onGuiClosed() {
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
		if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
			this.mc.player.closeScreen();
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		int containerX = mouseX-guiLeft;
		int containerY = mouseY-guiTop;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return;
		lastResponder = container.doMouseDown(containerX, containerY, mouseButton);
	}
	
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) { //Testing shows that STATE IS ACTUALLY BUTTON
		super.mouseReleased(mouseX, mouseY, state);
		int containerX = mouseX-guiLeft;
		int containerY = mouseY-guiTop;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return;
		WWidget responder = container.doMouseUp(containerX, containerY, state);
		if (responder!=null && responder==lastResponder) container.doClick(containerX, containerY, state);
		lastResponder = null;
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		int containerX = mouseX-guiLeft;
		int containerY = mouseY-guiTop;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return;
		container.doMouseDrag(containerX, containerY, clickedMouseButton);
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
		
		WPanel basePanel = container.getRootPanel();
		if (basePanel!=null) {
			xSize = basePanel.getWidth();
			ySize = basePanel.getHeight();
		}
		guiLeft = (width  / 2) - (xSize / 2);
		guiTop =  (height / 2) - (ySize / 2);
		
	}
	
	@Override
	public void setGuiSize(int w, int h) {
		super.setGuiSize(w, h);
		
		WPanel basePanel = container.getRootPanel();
		if (basePanel!=null) {
			xSize = basePanel.getWidth();
			ySize = basePanel.getHeight();
		}
		
		guiLeft = (width  / 2) - (xSize / 2);
		guiTop =  (height / 2) - (ySize / 2);
		
	}
	
	/*
	 * SPECIAL FUNCTIONS: Where possible, we want to draw everything based on *actual GUI state and composition* rather
	 * than relying on pre-baked textures that the programmer then needs to carefully match up their GUI to.
	 */
	
	private int multiplyColor(int color, float amount) {
		int a = color & 0xFF000000;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8  & 255) / 255.0F;
		float b = (color       & 255) / 255.0F;
		
		r = Math.min(r*amount, 1.0f);
		g = Math.min(g*amount, 1.0f);
		b = Math.min(b*amount, 1.0f);
		
		int ir = (int)(r*255);
		int ig = (int)(g*255);
		int ib = (int)(b*255);
		
		return    a |
				(ir << 16) |
				(ig <<  8) |
				 ib;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		WPanel root = this.container.getRootPanel();
		if (root==null) return;
		
		if (container.shouldDrawPanel()) {
			int panelColor = container.getColor();
			int shadowColor = multiplyColor(panelColor, 0.50f);
			int hilightColor = multiplyColor(panelColor, 1.25f);
			
			GuiDrawing.drawGuiPanel(guiLeft - PADDING, guiTop - PADDING, xSize + ((PADDING - 1) * 2), ySize + ((PADDING - 1) * 2),
					shadowColor, panelColor, hilightColor, 0xFF000000);
		}
		
		if (inventorySlots != null && root != null) {
			root.paintBackground(guiLeft, guiTop);
		}
		
		//TODO: Change this to a label that lives in the rootPanel instead
		fontRenderer.drawString(container.getLocalizedName(), guiLeft, guiTop, container.getTitleColor());
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (inventorySlots != null && this.container.getRootPanel() != null) {
			this.container.getRootPanel().paintForeground(guiLeft, guiTop, mouseX, mouseY);
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}
}
