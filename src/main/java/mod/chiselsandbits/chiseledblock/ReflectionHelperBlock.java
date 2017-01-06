package mod.chiselsandbits.chiseledblock;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

class ReflectionHelperBlock extends Block
{
	public String MethodName;

	private void markMethod()
	{
		MethodName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
	}

	protected ReflectionHelperBlock()
	{
		super( Material.AIR );
	}

	@Override
	public float getBlockHardness(
			final @Nullable IBlockState state,
			final @Nullable World world,
			final @Nullable BlockPos pos )
	{
		markMethod();
		return 0;
	}

	@Override
	public void addCollisionBoxToList(
			final @Nullable IBlockState state,
			final @Nullable World worldIn,
			final @Nullable BlockPos pos,
			final @Nullable AxisAlignedBB p_185477_4_,
			final @Nullable List<AxisAlignedBB> p_185477_5_,
			final @Nullable Entity p_185477_6_,
			final boolean p_185477_7_ )
	{
		markMethod();
	}

	@Override
	public float getPlayerRelativeBlockHardness(
			final @Nullable IBlockState state,
			final @Nullable EntityPlayer player,
			final @Nullable World world,
			final @Nullable BlockPos pos )
	{
		markMethod();
		return 0;
	}

	@Override
	public float getExplosionResistance(
			final @Nullable Entity exploder )
	{
		markMethod();
		return 0;
	}

	@Override
	public float getExplosionResistance(
			final @Nullable World world,
			final @Nullable BlockPos pos,
			final @Nullable Entity exploder,
			final @Nullable Explosion explosion )
	{
		markMethod();
		return 0;
	}

	@Override
	public int quantityDropped(
			final @Nullable IBlockState state,
			final int fortune,
			final @Nullable Random random )
	{

		markMethod();
		return 0;
	}

	@Override
	public int quantityDropped(
			final @Nullable Random random )
	{
		markMethod();
		return 0;
	}

	@Override
	public int quantityDroppedWithBonus(
			final int fortune,
			final @Nullable Random random )
	{
		markMethod();
		return 0;
	}

	@Override
	public void onEntityCollidedWithBlock(
			final @Nullable World worldIn,
			final @Nullable BlockPos pos,
			final @Nullable IBlockState state,
			final @Nullable Entity entityIn )
	{
		markMethod();
	}
}