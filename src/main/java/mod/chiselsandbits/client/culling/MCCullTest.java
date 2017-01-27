package mod.chiselsandbits.client.culling;

import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.helpers.ModUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

/**
 * Determine Culling using Block's Native Check.
 *
 * hardcode vanilla stained glass because that looks horrible.
 */
public class MCCullTest implements ICullTest, IBlockAccess
{

	private IBlockState a;
	private IBlockState b;

	@Override
	public boolean isVisible(
			final int mySpot,
			final int secondSpot )
	{
		if ( mySpot == 0 || mySpot == secondSpot )
		{
			return false;
		}

		a = ModUtil.getStateById( mySpot );
		if ( a == null )
		{
			a = Blocks.AIR.getDefaultState();
		}
		b = ModUtil.getStateById( secondSpot );
		if ( b == null )
		{
			b = Blocks.AIR.getDefaultState();
		}

		if ( a.getBlock() == Blocks.STAINED_GLASS && a.getBlock() == b.getBlock() )
		{
			return false;
		}

		try
		{
			return a.shouldSideBeRendered( this, BlockPos.ORIGIN, EnumFacing.NORTH );
		}
		catch ( final Throwable t )
		{
			// revert to older logic in the event of some sort of issue.
			return BlockBitInfo.getTypeFromStateID( mySpot ).shouldShow( BlockBitInfo.getTypeFromStateID( secondSpot ) );
		}
	}

	@Override
	public TileEntity getTileEntity(
			final BlockPos pos )
	{
		return null;
	}

	@Override
	public int getCombinedLight(
			final BlockPos pos,
			final int lightValue )
	{
		return 0;
	}

	@Override
	public IBlockState getBlockState(
			final BlockPos pos )
	{
		return pos.equals( BlockPos.ORIGIN ) ? a : b;
	}

	@Override
	public boolean isAirBlock(
			final BlockPos pos )
	{
		return getBlockState( pos ) == Blocks.AIR;
	}

	@Override
	public Biome getBiome(
			final BlockPos pos )
	{
		return Biomes.PLAINS;
	}

	public boolean extendedLevelsInChunkCache()
	{
		return false;
	}

	@Override
	public int getStrongPower(
			final BlockPos pos,
			final EnumFacing direction )
	{
		return 0;
	}

	@Override
	public WorldType getWorldType()
	{
		return WorldType.DEFAULT;
	}

	@Override
	public boolean isSideSolid(
			final BlockPos pos,
			final EnumFacing side,
			final boolean _default )
	{
		return false;
	}

}
