package mod.chiselsandbits.bitbag;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.network.NetworkRouter;
import mod.chiselsandbits.network.packets.PacketBagGui;
import mod.chiselsandbits.network.packets.PacketClearBagGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BagGui extends GuiContainer
{

	private static final ResourceLocation BAG_GUI_TEXTURE = new ResourceLocation( ChiselsAndBits.MODID, "textures/gui/container/bitbag.png" );
	private static int INNER_SLOT_SIZE = 16;

	private static GuiBagFontRenderer specialFontRenderer = null;
	private GuiIconButton trashBtn;

	public BagGui(
			final EntityPlayer player,
			final World world,
			final int x,
			final int y,
			final int z )
	{
		super( new BagContainer( player, world, x, y, z ) );

		allowUserInput = false;
		ySize = 239;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		buttonList.add( trashBtn = new GuiIconButton( 1, guiLeft - 18, guiTop + 0, "help.trash", ClientSide.trashIcon ) );
	}

	BagContainer getBagContainer()
	{
		return (BagContainer) inventorySlots;
	}

	@Override
	protected boolean checkHotbarKeys(
			final int keyCode )
	{
		if ( theSlot instanceof SlotBit )
		{
			theSlot = null;
		}

		return super.checkHotbarKeys( keyCode );
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(
			final float partialTicks,
			final int mouseX,
			final int mouseY )
	{
		final int xOffset = ( width - xSize ) / 2;
		final int yOffset = ( height - ySize ) / 2;

		GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );
		mc.getTextureManager().bindTexture( BAG_GUI_TEXTURE );
		this.drawTexturedModalRect( xOffset, yOffset, 0, 0, xSize, ySize );
	}

	private Slot getSlotAtPosition(
			final int x,
			final int y )
	{
		for ( int slotIdx = 0; slotIdx < getBagContainer().customSlots.size(); ++slotIdx )
		{
			final Slot slot = getBagContainer().customSlots.get( slotIdx );

			if ( isMouseOverSlot( slot, x, y ) )
			{
				return slot;
			}
		}

		return null;
	}

	@Override
	protected void mouseClicked(
			final int mouseX,
			final int mouseY,
			final int mouseButton ) throws IOException
	{
		// This is what vanilla does...
		final boolean duplicateButton = mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;

		final Slot slot = getSlotAtPosition( mouseX, mouseY );
		if ( slot != null )
		{
			final PacketBagGui bagGuiPacket = new PacketBagGui();

			bagGuiPacket.slotNumber = slot.slotNumber;
			bagGuiPacket.mouseButton = mouseButton;
			bagGuiPacket.duplicateButton = duplicateButton;
			bagGuiPacket.holdingShift = ClientSide.instance.holdingShift();

			bagGuiPacket.doAction( ClientSide.instance.getPlayer() );
			NetworkRouter.instance.sendToServer( bagGuiPacket );

			return;
		}

		super.mouseClicked( mouseX, mouseY, mouseButton );
	}

	@Override
	protected void drawGuiContainerForegroundLayer(
			final int mouseX,
			final int mouseY )
	{
		fontRendererObj.drawString( ChiselsAndBits.getItems().itemBitBag.getItemStackDisplayName( null ), 8, 6, 0x404040 );
		fontRendererObj.drawString( I18n.format( "container.inventory", new Object[0] ), 8, ySize - 93, 0x404040 );

		RenderHelper.enableGUIStandardItemLighting();

		if ( specialFontRenderer == null )
		{
			specialFontRenderer = new GuiBagFontRenderer( fontRendererObj, ChiselsAndBits.getConfig().bagStackSize );
		}

		for ( int slotIdx = 0; slotIdx < getBagContainer().customSlots.size(); ++slotIdx )
		{
			final Slot slot = getBagContainer().customSlots.get( slotIdx );

			final FontRenderer defaultFontRenderer = fontRendererObj;

			try
			{
				fontRendererObj = specialFontRenderer;
				drawSlot( slot );
			}
			finally
			{
				fontRendererObj = defaultFontRenderer;
			}

			if ( isMouseOverSlot( slot, mouseX, mouseY ) && slot.canBeHovered() )
			{
				final int xDisplayPos = slot.xPos;
				final int yDisplayPos = slot.yPos;
				theSlot = slot;

				GlStateManager.disableLighting();
				GlStateManager.disableDepth();
				GlStateManager.colorMask( true, true, true, false );
				drawGradientRect( xDisplayPos, yDisplayPos, xDisplayPos + INNER_SLOT_SIZE, yDisplayPos + INNER_SLOT_SIZE, 0x80FFFFFF, 0x80FFFFFF );
				GlStateManager.colorMask( true, true, true, true );
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
			}
		}

		if ( trashBtn.isMouseOver() )
		{
			if ( isValidBitItem() )
			{
				final String msgNotConfirm = getInHandItem() != null ? LocalStrings.TrashItem.getLocal( getInHandItem().getDisplayName() ) : LocalStrings.Trash.getLocal();
				final String msgConfirm = getInHandItem() != null ? LocalStrings.ReallyTrashItem.getLocal( getInHandItem().getDisplayName() ) : LocalStrings.ReallyTrash.getLocal();

				final List<String> text = Arrays
						.asList( new String[] { requireConfirm ? msgNotConfirm : msgConfirm } );
				drawHoveringText( text, mouseX - guiLeft, mouseY - guiTop, fontRendererObj );
			}
			else
			{
				final List<String> text = Arrays
						.asList( new String[] { LocalStrings.TrashInvalidItem.getLocal( getInHandItem().getDisplayName() ) } );
				drawHoveringText( text, mouseX - guiLeft, mouseY - guiTop, fontRendererObj );
			}
		}
		else
		{
			requireConfirm = true;
		}
	}

	private ItemStack getInHandItem()
	{
		return getBagContainer().thePlayer.inventory.getItemStack();
	}

	boolean requireConfirm = true;
	boolean dontThrow = false;

	@Override
	protected void actionPerformed(
			final GuiButton button ) throws IOException
	{
		if ( button == trashBtn )
		{
			if ( requireConfirm )
			{
				dontThrow = true;
				if ( isValidBitItem() )
				{
					requireConfirm = false;
				}
			}
			else
			{
				requireConfirm = true;
				// server side!
				NetworkRouter.instance.sendToServer( new PacketClearBagGui( getInHandItem() ) );
				dontThrow = false;
			}
		}
	}

	private boolean isValidBitItem()
	{
		return getInHandItem() == null || getInHandItem().getItem() == ChiselsAndBits.getItems().itemBlockBit;
	}

	@Override
	protected void handleMouseClick(
			final Slot slotIn,
			final int slotId,
			final int mouseButton,
			final ClickType type )
	{
		if ( ( type == ClickType.PICKUP || type == ClickType.THROW ) && dontThrow )
		{
			dontThrow = false;
			return;
		}

		super.handleMouseClick( slotIn, slotId, mouseButton, type );
	}

}
