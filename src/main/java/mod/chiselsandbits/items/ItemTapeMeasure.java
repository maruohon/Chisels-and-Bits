package mod.chiselsandbits.items;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import mod.chiselsandbits.chiseledblock.data.BitLocation;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.core.ReflectionWrapper;
import mod.chiselsandbits.helpers.BitOperation;
import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.DeprecationHelper;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.IChiselModeItem;
import mod.chiselsandbits.interfaces.IItemScrollWheel;
import mod.chiselsandbits.modes.TapeMeasureModes;
import mod.chiselsandbits.network.NetworkRouter;
import mod.chiselsandbits.network.packets.PacketSetColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemTapeMeasure extends Item implements IChiselModeItem, IItemScrollWheel
{
	public ItemTapeMeasure()
	{
		setMaxStackSize( 1 );
	}

	@Override
	public void addInformation(
			final ItemStack stack,
			final EntityPlayer playerIn,
			final List<String> tooltip,
			final boolean advanced )
	{
		super.addInformation( stack, playerIn, tooltip, advanced );
		ChiselsAndBits.getConfig().helpText( LocalStrings.HelpTapeMeasure, tooltip, ClientSide.instance.getModeKey() );
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(
			final ItemStack itemStackIn,
			final World worldIn,
			final EntityPlayer playerIn,
			final EnumHand hand )
	{
		if ( playerIn.isSneaking() && playerIn.getEntityWorld().isRemote )
		{
			ClientSide.instance.tapeMeasures.clear();
		}

		return new ActionResult<ItemStack>( EnumActionResult.SUCCESS, itemStackIn );
	}

	@Override
	public EnumActionResult onItemUse(
			final ItemStack stack,
			final EntityPlayer playerIn,
			final World worldIn,
			final BlockPos pos,
			final EnumHand hand,
			final EnumFacing facing,
			final float hitX,
			final float hitY,
			final float hitZ )
	{
		if ( worldIn.isRemote )
		{
			if ( playerIn.isSneaking() )
			{
				ClientSide.instance.tapeMeasures.clear();
				return EnumActionResult.SUCCESS;
			}

			final Pair<Vec3d, Vec3d> PlayerRay = ModUtil.getPlayerRay( playerIn );
			final Vec3d ray_from = PlayerRay.getLeft();
			final Vec3d ray_to = PlayerRay.getRight();

			final RayTraceResult mop = playerIn.world.getBlockState( pos ).collisionRayTrace( playerIn.world, pos, ray_from, ray_to );
			if ( mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK )
			{
				final BitLocation loc = new BitLocation( mop, true, BitOperation.CHISEL );
				ClientSide.instance.pointAt( ChiselToolType.TAPEMEASURE, loc, hand );
			}
		}

		return EnumActionResult.SUCCESS;
	}

	@Override
	public String getHighlightTip(
			final ItemStack item,
			final String displayName )
	{
		if ( ChiselsAndBits.getConfig().itemNameModeDisplay )
		{
			return displayName + " - " + TapeMeasureModes.getMode( item ).string.getLocal() + " - " + DeprecationHelper.translateToLocal( "chiselsandbits.color." + getTapeColor( item ).getUnlocalizedName() );
		}

		return displayName;
	}

	public EnumDyeColor getTapeColor(
			final ItemStack item )
	{
		final NBTTagCompound compound = item.getTagCompound();
		if ( compound != null && compound.hasKey( "color" ) )
		{
			try
			{
				return EnumDyeColor.valueOf( compound.getString( "color" ) );
			}
			catch ( final IllegalArgumentException iae )
			{
				// nope!
			}
		}

		return EnumDyeColor.WHITE;
	}

	@Override
	public void scroll(
			final EntityPlayer player,
			final ItemStack stack,
			final int dwheel )
	{
		final EnumDyeColor color = getTapeColor( stack );
		int next = color.ordinal() + ( dwheel < 0 ? -1 : 1 );

		if ( next < 0 )
		{
			next = EnumDyeColor.values().length - 1;
		}

		if ( next >= EnumDyeColor.values().length )
		{
			next = 0;
		}

		final EnumDyeColor col = EnumDyeColor.values()[next];
		setTapeColor( stack, col );

		final PacketSetColor setColor = new PacketSetColor();
		setColor.chatNotification = ChiselsAndBits.getConfig().chatModeNotification;
		setColor.newColor = col;
		setColor.type = ChiselToolType.TAPEMEASURE;

		NetworkRouter.instance.sendToServer( setColor );
		ReflectionWrapper.instance.clearHighlightedStack();
	}

	public void setTapeColor(
			final ItemStack stack,
			final EnumDyeColor color )
	{
		stack.setTagInfo( "color", new NBTTagString( color.name() ) );
	}

}
