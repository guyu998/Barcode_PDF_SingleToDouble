package com.singletodouble.util.pdf;

public class TextChunk {
	public String text;
    public float x;   // 左下角 X
    public float y;   // 左下角 Y
    public float width;
    public float height;

    public TextChunk(String text, float x, float y, float width, float height) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format("文本: '%s' | 位置: [%.1f, %.1f] | 大小: %.1fx%.1f",
                text, x, y, width, height);
    }
}
