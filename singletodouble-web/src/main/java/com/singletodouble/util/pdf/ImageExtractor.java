package com.singletodouble.util.pdf;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

public class ImageExtractor implements IEventListener {
    private Rectangle rect;
    private byte[] extractedImageBytes = null;

    public ImageExtractor(Rectangle rect) {
    	this.rect = rect;
    }

    @Override
    public void eventOccurred(IEventData data, EventType type) {
        if (type == EventType.RENDER_IMAGE && data instanceof ImageRenderInfo) {
            ImageRenderInfo imageInfo = (ImageRenderInfo) data;
           
            // 2. 获取图片左下角坐标 (Vector对象)
            Vector startPoint = imageInfo.getStartPoint();
            float imgX = startPoint.get(Vector.I1);
            float imgY = startPoint.get(Vector.I2);

            // 3. 获取图片变换矩阵，用于计算宽高
            Matrix ctm = imageInfo.getImageCtm();

            // 4. 推导图片宽度和高度
            //    矩阵 [a b c; d e f; 0 0 1] 中：
            //    a 是水平缩放，e 是垂直缩放（一般情况下）
            //    若图片有旋转，需用向量长度计算
            float width = (float) Math.sqrt(Math.pow(ctm.get(Matrix.I11), 2) 
                                          + Math.pow(ctm.get(Matrix.I21), 2));
            float height = (float) Math.sqrt(Math.pow(ctm.get(Matrix.I12), 2) 
                                           + Math.pow(ctm.get(Matrix.I22), 2));
            
            Rectangle imgRect = new Rectangle(imgX, imgY, width, height);

            if (isImageInRegion(imgRect, rect)) {
                PdfImageXObject imageXObject = imageInfo.getImage();
                extractedImageBytes = imageXObject.getImageBytes();	
            }
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
        return Collections.unmodifiableSet(EnumSet.of(EventType.RENDER_IMAGE));
    }

    private boolean isImageInRegion(Rectangle chunk, Rectangle region) {
        // 计算图片区域的边界框
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

    public byte[] getBytes() {
        return extractedImageBytes;
    }
}
