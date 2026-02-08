package com.singletodouble.util.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;

public class TextWithLocationStrategy implements ITextExtractionStrategy {
    private final List<TextChunk> result = new ArrayList<>();

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            String text = renderInfo.getText();
            if (text == null || text.trim().isEmpty()) {
                return;
            }

            // 获取文本边界框（包围盒）
            Rectangle rect = renderInfo.getBaseline().getBoundingRectangle();
            Rectangle ascentBox = renderInfo.getAscentLine().getBoundingRectangle();

            // 合并基线和上升线，得到完整高度
            float llx = rect.getLeft();           // 左
            float lly = rect.getBottom();         // 下（Y坐标，从左下角起）
            float urx = Math.max(rect.getRight(), ascentBox.getRight());
            float ury = Math.max(rect.getTop(), ascentBox.getTop());

            float width = urx - llx;
            float height = ury - lly;

            // 注意：PDF 坐标系原点在左下角
            result.add(new TextChunk(text.trim(), llx, lly, width, height));
        }
    }

    @Override
    public String getResultantText() {
        return result.stream()
                .map(TextChunk::toString)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

    public List<TextChunk> getChunks() {
        return new ArrayList<>(result);
    }

	@Override
	public Set<EventType> getSupportedEvents() {
		return null;
	}
}
