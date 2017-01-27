package mod.chiselsandbits.chiseledblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

public class HarvestWorld implements IBlockAccess
{

	IBlockState state;

	public HarvestWorld(
			final IBlockState state )
	{
		this.state = state;
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
		return state;
	}

	@Override
	public boolean isAirBlock(
			final BlockPos pos )
	{
		return Blocks.AIR == state;
	}

	public boolean extendedLevelsInChunkCache()
	{
		return false;
	}

	@Override
	public Biome getBiome(
			final BlockPos pos )
	{
		return Biomes.PLAINS;
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
		return true;
	}

}
