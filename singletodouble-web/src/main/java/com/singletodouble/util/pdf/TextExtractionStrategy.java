package com.singletodouble.util.pdf;

import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.geom.Rectangle;

public class TextExtractionStrategy extends LocationTextExtractionStrategy {
	private Rectangle rect;
	public TextExtractionStrategy(Rectangle rect) {
		this.rect = rect;
	}
	
	@Override
    public void eventOccurred(IEventData data, EventType type) {
        // 获取当前文本的边界框
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            
            // 获取文本边界框（包围盒）
            Rectangle baseRect = renderInfo.getBaseline().getBoundingRectangle();
            Rectangle ascentRect = renderInfo.getAscentLine().getBoundingRectangle();
            
            // 合并基线和上升线，得到完整高度
            float llx = baseRect.getLeft();           // 左
            float lly = baseRect.getBottom();         // 下（Y坐标，从左下角起）
            float urx = Math.max(baseRect.getRight(), ascentRect.getRight());
            float ury = Math.max(baseRect.getTop(), ascentRect.getTop());

            float width = urx - llx;
            float height = ury - lly;
            
            Rectangle textRect = new Rectangle(llx, lly, width, height);
            
            // 判断文本是否与目标区域相交
            if (isChunkInRegion(textRect, rect)) {
                super.eventOccurred(data, type);
            }
        }
    }
	
    @Override
    public String getResultantText() {
        return super.getResultantText().trim();
    }
    
    /**
     * 检查文本块是否在指定区域内
     */
    private boolean isChunkInRegion(Rectangle chunk, Rectangle region) {
        // 计算文本块的边界框
        float chunkLeft = chunk.getX();
        float chunkBottom = chunk.getY();
        float chunkRight = chunk.getX() + chunk.getWidth();
        float chunkTop = chunk.getY() + chunk.getHeight();

        // 检查是否有重叠
        return !(chunkRight < region.getX() || 
                 chunkLeft > region.getX() + region.getWidth() || 
                 chunkTop < region.getY() || 
                 chunkBottom > region.getY() + region.getHeight());
    }
}
