package com.singletodouble.util.pdf;

import com.itextpdf.kernel.geom.Rectangle;

public class TextChunk {
	private String text;
	private float x;   // 左下角 X
	private float y;   // 左下角 Y
	private float width;
	private float height;
	private int pageNumber; // 页码

    public TextChunk(String text, float x, float y, float width, float height) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pageNumber = 1; // 默认页码为1
    }

    public TextChunk(String text, float x, float y, float width, float height, int pageNumber) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pageNumber = pageNumber;
    }
    
    public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

    /**
     * 检查文本块是否在指定区域内
     */
    public boolean inRegion(Rectangle region) {
        // 计算文本块的边界框
        float chunkLeft = getX();
        float chunkBottom = getY();
        float chunkRight = getX() + getWidth();
        float chunkTop = getY() + getHeight();

        // 检查是否有重叠
        return !(chunkRight < region.getX() || 
                 chunkLeft > region.getX() + region.getWidth() || 
                 chunkTop < region.getY() || 
                 chunkBottom > region.getY() + region.getHeight());
    }
    
	@Override
    public String toString() {
        return String.format("页码: %d | 文本: '%s' | 位置: [%.1f, %.1f] | 大小: %.1fx%.1f",
                pageNumber, text, x, y, width, height);
    }
}