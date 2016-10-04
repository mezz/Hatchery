package com.gendeathrow.hatchery.block.feeder;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.gendeathrow.hatchery.Hatchery;

public class FeederBlock extends Block implements ITileEntityProvider
{

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	//public static final PropertyEnum QTY;
	protected static final AxisAlignedBB Base_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.125D, 0.875D);
	protected static final AxisAlignedBB Container_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D,0.5625D, 0.625);
    
	
	public FeederBlock() 
	{
		super(Material.WOOD);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setHardness(2);
		this.setCreativeTab(Hatchery.hatcheryTabs);
		this.setTickRandomly(true);
	}
	
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
    	if(!worldIn.isRemote)
    	{
    		FeederTileEntity te = (FeederTileEntity) worldIn.getTileEntity(pos);
    		
    		if(te.getSeedsInv() > 0)
    		{
    			AxisAlignedBB RANGE_AABB = new AxisAlignedBB(pos.getX() - 4, pos.getY(), pos.getZ() - 4, pos.getX() + 4, pos.getY() + 1, pos.getZ() + 4);
    			
    			List<EntityChicken> entitys = worldIn.getEntitiesWithinAABB(EntityChicken.class, RANGE_AABB);
    			for(EntityChicken entity : entitys)
    			{
    				if(te.getSeedsInv() > 0)
    				{
    				
    					if(entity.isChild())
    					{
    						te.decrSeedsInv();
    						entity.ageUp((int)((float)(-entity.getGrowingAge() / 20) * 0.35F), true);
    					}
    					else
    					{
    						te.decrSeedsInv();
    						entity.timeUntilNextEgg -= 5;
    					}
    				}
    			}
    		}
    	}
	}
	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	if(!worldIn.isRemote)
    	{
    		
			FeederTileEntity te = (FeederTileEntity) worldIn.getTileEntity(pos);
			
			if(heldItem != null && te != null && te.isItemValidForSlot(0, heldItem))
			{

				if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				{
					System.out.println("shift");
					te.setSeeds(1, heldItem);
				}
				else
				{
					te.setSeeds(heldItem.stackSize, heldItem);
				}
    			
    			return true;
			}
    	}
    	return true;
    }
	
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, Base_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, Container_AABB);

    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new FeederTileEntity();
	}
	
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }
    
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
    }
    
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
    
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
    
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    
    public boolean isFullyOpaque(IBlockState state)
    {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }
    
    public static EnumFacing getFacing(int meta)
    {
        return EnumFacing.getHorizontal(meta & 4);
    }
    
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta));
    }
    
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();

        return i;
    }
    
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
}