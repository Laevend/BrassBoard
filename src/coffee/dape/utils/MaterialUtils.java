package coffee.dape.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Laeven
 * @since 1.0.0
 */
public class MaterialUtils
{
	/**
	 * Returns the name of a material given the material enumerator
	 * @param mat The material
	 * @return The material name
	 */
	public static String getNameFromMaterial(Material mat)
	{
		StringBuilder sb = new StringBuilder();
		
		// Get raw material name represented by its enumerator
		String materialName = mat.toString().toLowerCase();
		String[] materialNameParts = materialName.split("_");
		
		// For each word in the material name
		for(String material : materialNameParts)
		{
			// Make the first letter of the word capital
			String capital = String.valueOf(material.charAt(0)).toUpperCase();
			String headlessWord = material.substring(1,material.length());
			
			// Append it to the material name
			sb.append(capital + headlessWord + " ");
		}
		
		// Return the complete material string but remove the last character because it's a space
		return sb.toString().substring(0,sb.toString().length() - 1);
	}
	
	/**
	 * Returns the material enumerator from its string name
	 * @param mat The name of the material
	 * @return The material type
	 */
	public static Material getMaterialFromName(String mat)
	{
		// turn the string upper case and replace all spaces with an underscore
		String materialName = mat.toUpperCase().replaceAll("\\s+","_");
		
		// Return the enumerator value of this material name
		return Material.valueOf(materialName);
	}
	
	/**
	 * Gets the string ID of an item/material (Example: minecraft:diamond)
	 * and converts it to a spigot material type so it can be used
	 * @param minecraftID The minecraft id of the item/material
	 * @return The material type
	 */
	public static Material getSpigotMaterial(String minecraftID)
	{
		return Material.matchMaterial(minecraftID);
	}
	
	/**
	 * Converts a Spigot material into a vanilla material with the 'minecraft:' namespace.
	 * @param mat Material
	 * @return minecraft:[material]
	 */
	public static String getMinecraftMaterial(Material mat)
	{
		return "minecraft:" + mat.toString().toLowerCase();
	}
	
	/**
	 * Converts a Spigot material into a vanilla material with the 'minecraft:' namespace.
	 * @param mat Material
	 * @return minecraft:[material]
	 */
	public static String getMinecraftMaterial(String mat)
	{
		return "minecraft:" + mat.toLowerCase();
	}
	
	/**
	 * Gets a spigot material type from a string
	 * @param material a string representation of a spigot material or a minecraft material id
	 * @return Material
	 */
	public static Material getMaterial(String material)
	{
		Material mat = null;
		
		try
		{
			mat = getMaterialFromName(material);
		}
		catch(Exception e) {}
		
		if(mat != null) { return mat; }
		
		String minecraftMaterial = null;
		
		if(!material.contains("minecraft"))
		{
			minecraftMaterial = getMinecraftMaterial(mat);
		}
		else
		{
			minecraftMaterial = material;
		}
		
		mat = getSpigotMaterial(minecraftMaterial);
		
		return mat;
	}
	
	/**
	 * Checks if a material type can render as an ItemStack inside an inventory
	 * @param mat Material
	 * @return true/false
	 */
	public static boolean canRenderAsItemStack(Material mat)
	{
		try
		{
			// Cannot render as ItemStack
			NoneItemStackMaterial.valueOf(mat.toString());
			return false;
		}
		catch(Exception e)
		{
			// Can render as ItemStack
			return true;
		}
	}
	
	/**
	 * Gets a list of ItemStacks which have random materials
	 * @param numberOfItems The number of random ItemStacks to get
	 * @return List of ItemStacks with random material types
	 */
	public static List<ItemStack> getListOfRandomMaterials(int numberOfItems)
	{
		List<ItemStack> mats = new ArrayList<>();
		
		Material[] mat = Material.values();
		
		for(int i = 0; i < numberOfItems; i++)
		{
			Material newMat = mat[MathUtils.getRandom(0,mat.length - 1)];
			
			while(!MaterialUtils.canRenderAsItemStack(newMat))
			{
				newMat = mat[MathUtils.getRandom(0,mat.length - 1)];
			}
			
			mats.add(new ItemStack(newMat));
		}
		
		return mats;
	}
	
	/**
	 * Gets a random material
	 * @return Random Material
	 */
	public static Material getRandomMaterial()
	{
		Material[] mat = Material.values();
		Material newMat = mat[MathUtils.getRandom(0,mat.length - 1)];
		
		while(!MaterialUtils.canRenderAsItemStack(newMat))
		{
			newMat = mat[MathUtils.getRandom(0,mat.length - 1)];
		}
		
		return newMat;
	}
	
	/**
	 * Checks if this material represented as a string is able to be turned into a material enum type
	 * @param mat Material enum represented as an uppercase string
	 * @return true if this material can be turned into a Material enum type
	 */
	public static boolean isMaterial(String mat)
	{
		try { Material.valueOf(mat.toUpperCase()); return true; } catch(Exception e) { return false; }
	}
	
	public enum NoneItemStackMaterial
	{
		POTTED_AZURE_BLUET,
		LIGHT_BLUE_WALL_BANNER,
		PURPLE_WALL_BANNER,
		BUBBLE_CORAL_WALL_FAN,
		POTTED_RED_MUSHROOM,
		SOUL_FIRE,
		POTTED_WARPED_ROOTS,
		POTTED_BROWN_MUSHROOM,
		BIRCH_WALL_SIGN,
		BLUE_WALL_BANNER,
		FIRE_CORAL_WALL_FAN,
		SKELETON_WALL_SKULL,
		WARPED_WALL_SIGN,
		ATTACHED_MELON_STEM,
		POTTED_CORNFLOWER,
		END_PORTAL,
		POTTED_SPRUCE_SAPLING,
		POTTED_ACACIA_SAPLING,
		DRAGON_WALL_HEAD,
		CAVE_AIR,
		CREEPER_WALL_HEAD,
		POTTED_ALLIUM,
		DEAD_BUBBLE_CORAL_WALL_FAN,
		SPRUCE_WALL_SIGN,
		DEAD_BRAIN_CORAL_WALL_FAN,
		REDSTONE_WIRE,
		PLAYER_WALL_HEAD,
		MAGENTA_WALL_BANNER,
		POTTED_FERN,
		POTTED_BLUE_ORCHID,
		RED_WALL_BANNER,
		BUBBLE_COLUMN,
		CRIMSON_WALL_SIGN,
		PINK_WALL_BANNER,
		PUMPKIN_STEM,
		POTTED_LILY_OF_THE_VALLEY,
		ZOMBIE_WALL_HEAD,
		SWEET_BERRY_BUSH,
		POTTED_ORANGE_TULIP,
		DEAD_HORN_CORAL_WALL_FAN,
		REDSTONE_WALL_TORCH,
		HORN_CORAL_WALL_FAN,
		LIGHT_GRAY_WALL_BANNER,
		BAMBOO_SAPLING,
		DARK_OAK_WALL_SIGN,
		FROSTED_ICE,
		TRIPWIRE,
		MELON_STEM,
		MOVING_PISTON,
		POTTED_OAK_SAPLING,
		POTTED_WHITE_TULIP,
		GRAY_WALL_BANNER,
		BLACK_WALL_BANNER,
		POTTED_OXEYE_DAISY,
		POTTED_JUNGLE_SAPLING,
		POTATOES,
		ATTACHED_PUMPKIN_STEM,
		LIME_WALL_BANNER,
		KELP_PLANT,
		END_GATEWAY,
		PISTON_HEAD,
		AIR,
		NETHER_PORTAL,
		BEETROOTS,
		DEAD_TUBE_CORAL_WALL_FAN,
		POTTED_BIRCH_SAPLING,
		WEEPING_VINES_PLANT,
		YELLOW_WALL_BANNER,
		DEAD_FIRE_CORAL_WALL_FAN,
		POTTED_RED_TULIP,
		POTTED_PINK_TULIP,
		CYAN_WALL_BANNER,
		POTTED_WARPED_FUNGUS,
		WALL_TORCH,
		SOUL_WALL_TORCH,
		CARROTS,
		BRAIN_CORAL_WALL_FAN,
		POTTED_CRIMSON_FUNGUS,
		POTTED_DANDELION,
		TUBE_CORAL_WALL_FAN,
		TALL_SEAGRASS,
		FIRE,
		POTTED_CACTUS,
		VOID_AIR,
		POTTED_DEAD_BUSH,
		POTTED_BAMBOO,
		ORANGE_WALL_BANNER,
		TWISTING_VINES_PLANT,
		WATER,
		JUNGLE_WALL_SIGN,
		WITHER_SKELETON_WALL_SKULL,
		POTTED_WITHER_ROSE,
		POTTED_CRIMSON_ROOTS,
		LAVA,
		COCOA,
		OAK_WALL_SIGN,
		BROWN_WALL_BANNER,
		POTTED_DARK_OAK_SAPLING,
		POTTED_POPPY,
		GREEN_WALL_BANNER,
		WHITE_WALL_BANNER,
		ACACIA_WALL_SIGN,
		;
	}
	
	/**
	 * Checks if a 2x1 block area is safe to teleport to by checking if it's obstructed by other blocks
	 * @param loc Location of the block below the player that they will land on after a teleport
	 * @return true if the teleport is safe, false otherwise
	 */
	public static boolean isSafeToTeleportTo(Location loc)
	{
		Location lowerBlockLoc = loc.clone().add(0,1,0);
		Location upperBlockLoc = loc.clone().add(0,2,0);
		
		return !isObstructed(lowerBlockLoc) && !isObstructed(upperBlockLoc) && loc.getBlock().getType() != Material.LAVA;
	}
	
	/**
	 * Checks if a block is obstructed. Obstructed meaning if the player were to be moved here they would no suffocate
	 * @param loc Location of block
	 * @return false if this block is NOT obstructed, otherwise true
	 */
	public static boolean isObstructed(Location loc)
	{
		return !NON_OBSTRUCTING_MATS.contains(loc.getBlock().getType());
	}
	
	 // Whitelisted blocks as blocks allowed to be teleported into when moving up or down the elevator
	public static final Set<Material> NON_OBSTRUCTING_MATS = Set.of
	(
		Material.AIR,
		Material.BLACK_CARPET,
		Material.BLUE_CARPET,
		Material.BROWN_CARPET,
		Material.CYAN_CARPET,
		Material.GRAY_CARPET,
		Material.GREEN_CARPET,
		Material.LIGHT_BLUE_CARPET,
		Material.LIGHT_GRAY_CARPET,
		Material.LIME_CARPET,
		Material.MAGENTA_CARPET,
		Material.MOSS_CARPET,
		Material.ORANGE_CARPET,
		Material.PINK_CARPET,
		Material.PURPLE_CARPET,
		Material.RED_CARPET,
		Material.WHITE_CARPET,
		Material.YELLOW_CARPET,
		Material.WATER,
		Material.COBWEB,
		Material.STONE_BUTTON,
		Material.POLISHED_BLACKSTONE_BUTTON,
		Material.OAK_BUTTON,
		Material.SPRUCE_BUTTON,
		Material.BIRCH_BUTTON,
		Material.JUNGLE_BUTTON,
		Material.ACACIA_BUTTON,
		Material.DARK_OAK_BUTTON,
		Material.CRIMSON_BUTTON,
		Material.WARPED_BUTTON,
		Material.STONE_PRESSURE_PLATE,
		Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
		Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
		Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
		Material.OAK_PRESSURE_PLATE,
		Material.SPRUCE_PRESSURE_PLATE,
		Material.BIRCH_PRESSURE_PLATE,
		Material.JUNGLE_PRESSURE_PLATE,
		Material.ACACIA_PRESSURE_PLATE,
		Material.DARK_OAK_PRESSURE_PLATE,
		Material.CRIMSON_PRESSURE_PLATE,
		Material.WARPED_PRESSURE_PLATE,
		Material.LADDER,
		Material.SEAGRASS,
		Material.SEA_PICKLE,
		Material.TORCH,
		Material.SOUL_TORCH,
		Material.WALL_TORCH,
		Material.SOUL_WALL_TORCH,
		Material.PAINTING,
		Material.VINE,
		Material.WEEPING_VINES,
		Material.TWISTING_VINES,
		Material.END_ROD,
		Material.REDSTONE_TORCH,
		Material.LEVER,
		Material.TRIPWIRE,
		Material.RAIL,
		Material.ACTIVATOR_RAIL,
		Material.DETECTOR_RAIL,
		Material.POWERED_RAIL,
		Material.IRON_DOOR,
		Material.OAK_DOOR,
		Material.SPRUCE_DOOR,
		Material.BIRCH_DOOR,
		Material.JUNGLE_DOOR,
		Material.ACACIA_DOOR,
		Material.DARK_OAK_DOOR,
		Material.CRIMSON_DOOR,
		Material.WARPED_DOOR,
		Material.IRON_TRAPDOOR,
		Material.OAK_TRAPDOOR,
		Material.SPRUCE_TRAPDOOR,
		Material.BIRCH_TRAPDOOR,
		Material.JUNGLE_TRAPDOOR,
		Material.ACACIA_TRAPDOOR,
		Material.DARK_OAK_TRAPDOOR,
		Material.CRIMSON_TRAPDOOR,
		Material.WARPED_TRAPDOOR,
		Material.OAK_SIGN,
		Material.SPRUCE_SIGN,
		Material.BIRCH_SIGN,
		Material.JUNGLE_SIGN,
		Material.ACACIA_SIGN,
		Material.DARK_OAK_SIGN,
		Material.CRIMSON_SIGN,
		Material.WARPED_SIGN,
		Material.WHITE_BANNER,
		Material.ORANGE_BANNER,
		Material.MAGENTA_BANNER,
		Material.LIGHT_BLUE_BANNER,
		Material.YELLOW_BANNER,
		Material.LIME_BANNER,
		Material.PINK_BANNER,
		Material.GRAY_BANNER,
		Material.LIGHT_GRAY_BANNER,
		Material.CYAN_BANNER,
		Material.PURPLE_BANNER,
		Material.BLUE_BANNER,
		Material.BROWN_BANNER,
		Material.GREEN_BANNER,
		Material.RED_BANNER,
		Material.BLACK_BANNER,
		Material.FLOWER_BANNER_PATTERN,
		Material.CREEPER_BANNER_PATTERN,
		Material.SKULL_BANNER_PATTERN,
		Material.MOJANG_BANNER_PATTERN,
		Material.GLOBE_BANNER_PATTERN,
		Material.PIGLIN_BANNER_PATTERN,
		Material.OAK_WALL_SIGN,
		Material.SPRUCE_WALL_SIGN,
		Material.BIRCH_WALL_SIGN,
		Material.ACACIA_WALL_SIGN,
		Material.JUNGLE_WALL_SIGN,
		Material.DARK_OAK_WALL_SIGN,
		Material.WHITE_WALL_BANNER,
		Material.ORANGE_WALL_BANNER,
		Material.MAGENTA_WALL_BANNER,
		Material.LIGHT_BLUE_WALL_BANNER,
		Material.YELLOW_WALL_BANNER,
		Material.LIME_WALL_BANNER,
		Material.PINK_WALL_BANNER,
		Material.GRAY_WALL_BANNER,
		Material.LIGHT_GRAY_WALL_BANNER,
		Material.CYAN_WALL_BANNER,
		Material.PURPLE_WALL_BANNER,
		Material.BLUE_WALL_BANNER,
		Material.BROWN_WALL_BANNER,
		Material.GREEN_WALL_BANNER,
		Material.RED_WALL_BANNER,
		Material.BLACK_WALL_BANNER,
		Material.CRIMSON_WALL_SIGN,
		Material.WARPED_WALL_SIGN,
		Material.SNOW,
		Material.POWDER_SNOW,
		
		Material.SCULK_VEIN
	);
}
