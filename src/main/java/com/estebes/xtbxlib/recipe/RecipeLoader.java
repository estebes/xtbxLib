package com.estebes.xtbxlib.recipe;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RecipeLoader {
    private String filePath;

    public RecipeLoader(String filePath) {
        this.filePath = filePath;
    }


    public boolean readRecipeFile() {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(this.filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            inputStream.close();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return true;
    }








    /**
     *
     * @param filePath String containing the path to the file.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    /**
     *
     * @return The string containing the path to the file containing the recipe to be loaded.
     */
    public String getFilePath() {
        return this.filePath;
    }

}
