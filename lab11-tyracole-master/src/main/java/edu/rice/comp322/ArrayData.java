package edu.rice.comp322;

public  class ArrayData  {
    public final int[] dataArray;
    public final int width;
    public final int height;
 
    public ArrayData(int width, int height) {
        this(new int[width * height], width, height);
    }

    /**
     * ArrayData constructor.
     * @param dataArray the array containing the data.
     * @param width the width of the array.
     * @param height the height of the array.
     */
    public ArrayData(int[] dataArray, int width, int height) {
        this.dataArray = dataArray;
        this.width = width;
        this.height = height;
    }
 
    public int get(int x, int y) {
        return dataArray[y * width + x];
    }
 
    public void set(int x, int y, int value) {
        dataArray[y * width + x] = value;
    }
}
 
