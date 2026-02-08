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
            Rectangle textRect = renderInfo.getAscentLine().getBoundingRectangle();
            
            // 判断文本是否与目标区域相交
            if (rect.getIntersection(textRect) != null) {
                super.eventOccurred(data, type);
            }
        }
    }

    public String getResultantText() {
        return super.getResultantText().trim();
    }
}
