package com.estebes.xtbxlib.recipe;

import com.estebes.xtbxlib.util.Logger;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeParser {
    private String filePath;

    String regexCheck1 = "(?m)^(#?)(.*)$";
    String regexCheck2 = "[\\s]*([a-zA-Z0-9]+)::([a-zA-Z0-9]+)[\\s]*\\(([\\s]*<.*>[\\s]*){1,9}=>([\\s]*<.*>[\\s]*)\\)[\\s]*;";

    String regexItemStackOutput = "<[\\s]*([^\\s<>]+):([^@#\\s<>]+)(?:#(\\d*))?(?:@(\\d*|W))?[\\s]*>";
    String regexItemStackInput = "<[\\s]*([^\\s<>]*):([^@#\\s<>]+)(?:@(\\d*|W))?[\\s]*>";

    private String unparsedRecipes;

    public RecipeParser(String filePath) {
        this.filePath = filePath;
    }


    /**
     * This function opens the file containing the recipes to be parsed.
     * @return True if the operation was successful and false otherwise.
     */
    public boolean loadRecipeFile() {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(this.filePath);
            unparsedRecipes = IOUtils.toString(inputStream, "UTF-8");
            inputStream.close();
        }
        catch (NullPointerException nullPointerException) {
            nullPointerException.printStackTrace();
            return false;
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     *
     * @return
     */
    public boolean parseRecipeFile() {
        Pattern pattern = Pattern.compile(regexCheck1);
        Matcher matcher = pattern.matcher(unparsedRecipes);
        while (matcher.find()) {
            if (matcher.group(1).isEmpty()) {
                parseRecipeLine(matcher.group(0));
            }
        }
        return true;
    }


    public boolean parseRecipeLine(String recipeData) {
        Pattern pattern = Pattern.compile(regexCheck2);
        Matcher matcher = pattern.matcher(recipeData);
        if (matcher.find()) {
            // Group 1
            if (!matcher.group(1).equals("Recipe")) {
                Logger.warn("Invalid syntax: " + matcher.group(1) + " in " + matcher.group(0));
                return false;
            }

            // Group 2
            if (matcher.group(2).equals("Shaped")) {
                return true;
            }
            else if (matcher.group(2).equals("Shapeless")) {
                return parseShapelessRecipe(matcher.group(3), matcher.group(4));
            }
            else if (matcher.group(2).equals("IC2Shaped")) {

            }
            else if (matcher.group(2).equals("IC2Shapeless")) {

            }
            else {
                Logger.warn("Invalid syntax: " + matcher.group(2) + " in " + matcher.group(0));
                return false;
            }
        }
        Logger.warn("Invalid syntax: " + recipeData);
        return false;
    }


    /**
     *
     * @param inputData
     * @param outputData
     * @return
     */
    public boolean parseShapelessRecipe(String inputData, String outputData) {
        ArrayList<Object> inputInfo = new ArrayList<Object>();
        ItemStack outputItemStack = null;
        Pattern inputPattern = Pattern.compile(regexItemStackInput);
        Matcher inputMatcher = inputPattern.matcher(inputData);
        while (inputMatcher.find()) {
            ItemStack itemStack;
            if ((itemStack = parseInputItemStack(inputMatcher.group(0))) != null) {
                inputInfo.add(itemStack);
            }
        }

        Pattern outputPattern = Pattern.compile(regexItemStackOutput);
        Matcher outputMatcher = outputPattern.matcher(outputData);
        if (outputMatcher.find()) {
            outputItemStack = parseOutputItemStack(outputMatcher.group(0));
        }

        if (!inputInfo.isEmpty() && outputItemStack != null) {
            GameRegistry.addRecipe(new ShapelessOreRecipe(outputItemStack, inputInfo.toArray()));
            return true;
        }

        Logger.warn("Invalid recipe");
        return false;
    }


    /**
     *
     * @return
     */
    @Optional.Method(modid = "IC2")
    public boolean addIC2ShapelessRecipe() {
        return false;
    }


    public boolean parseShapedInput(String inputData) {
        return false;
    }

    public boolean parseOutput(String outputData) {
        return false;
    }


    /**
     *
     * @param itemStackData String containing the data to be parsed.
     * @return Null.
     */
    public ItemStack parseInputItemStack(String itemStackData) {
        Pattern pattern = Pattern.compile(regexItemStackInput);
        Matcher matcher = pattern.matcher(itemStackData);
        if (matcher.find()) {
            Item inputItem;
            int metadata;

            if (matcher.group(1).isEmpty()) {
                OreDictionary.doesOreNameExist(matcher.group(2));
            }

            if ((inputItem = GameRegistry.findItem(matcher.group(1), matcher.group(2))) == null) {
                return null;
            }

            if (matcher.group(3) == null || matcher.group(3).isEmpty()) {
                metadata = 0;
            }
            else {
                if (matcher.group(3).equals("W")) {
                    metadata = OreDictionary.WILDCARD_VALUE;
                }
                else {
                    metadata = Integer.parseInt(matcher.group(3));
                }
            }

            return new ItemStack(inputItem, 1, metadata);
        }
        Logger.warn("Invalid syntax: " + itemStackData);
        return null;
    }


    /**
     *
     * @param itemstackData
     * @return Null if the itemstack is invalid and the parsed itemstack otherwise.
     */
    public ItemStack parseOutputItemStack(String itemstackData) {
        Pattern pattern = Pattern.compile(regexItemStackOutput);
        Matcher matcher = pattern.matcher(itemstackData);
        if (matcher.find()) {
            Item outputItem;
            int count, metadata;
            if ((outputItem = GameRegistry.findItem(matcher.group(1), matcher.group(2))) == null) {
                return null;
            }

            if (matcher.group(3) == null || matcher.group(3).isEmpty()) {
                count = 1;
            }
            else {
                count = Integer.parseInt(matcher.group(3));
                if (count > 64) {
                    count = 64;
                }
                else if (count < 1) {
                    count = 1;
                }
            }

            if (matcher.group(4) == null || matcher.group(4).isEmpty()) {
                metadata = 0;
            }
            else {
                if (matcher.group(4).equals("W")) {
                    metadata = OreDictionary.WILDCARD_VALUE;
                }
                else {
                    metadata = Integer.parseInt(matcher.group(4));
                }
            }

            return new ItemStack(outputItem, count, metadata);
        }
        Logger.warn("Invalid syntax: " + itemstackData);
        return null;
    }
}
