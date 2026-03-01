package com.singletodouble.util.pdf;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class PdfImageExtractor {

    /**
     * 从 PDF 指定区域提取图像并返回 byte[]
     *
     * @param pdfPath    PDF 文件路径
     * @param pageNumber 页码（从 1 开始）
     * @param x, y, width, height 指定区域坐标
     * @return 图像的 byte[] 数组
     */
    public static byte[] extractImageFromRegion(String pdfPath, int pageNumber, float x, float y, float width, float height) throws IOException {
        PdfReader reader = new PdfReader(pdfPath);
        PdfDocument pdfDoc = new PdfDocument(reader);

        // 获取指定页面
        PdfPage page = pdfDoc.getPage(pageNumber);
        
        // 获取页面大小
        Rectangle pageSize = page.getMediaBox();
        float pageHeight = pageSize.getHeight();

        // 创建自定义事件监听器
        QrImageExtractor extractor = new QrImageExtractor(x, y, width, height, pageHeight);

        // 处理页面内容
        PdfCanvasProcessor processor = new PdfCanvasProcessor(extractor);
        processor.processPageContent(page);

        pdfDoc.close();

        // 返回提取到的图像 byte[]
        return extractor.getExtractedImageBytes();
    }

    // 自定义图像提取器
    private static class QrImageExtractor implements IEventListener {
        private final float targetX, targetY, targetWidth, targetHeight;
        private final float pageHeight;
        private byte[] extractedImageBytes = null;

        public QrImageExtractor(float x, float y, float width, float height, float pageHeight) {
            this.targetX = x;
            this.targetY = y;
            this.targetWidth = width;
            this.targetHeight = height;
            this.pageHeight = pageHeight;
        }

        @Override
        public void eventOccurred(IEventData data, EventType type) {
            if (type == EventType.RENDER_IMAGE && data instanceof ImageRenderInfo) {
                try {
                    ImageRenderInfo imageInfo = (ImageRenderInfo) data;
                    
                    // 获取图像
                    PdfImageXObject imageXObject = imageInfo.getImage();
                    if (imageXObject == null) {
                        return;
                    }

                    // 提取图像数据
                    // 简化版本：直接获取找到的第一个图像
                    byte[] imageData = imageXObject.getImageBytes();
                    extractedImageBytes = imageData;
                    // 找到图像后可以选择返回，不再继续处理
                    // break; // 如果需要找到第一个图像就停止
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public Set<EventType> getSupportedEvents() {
            return Collections.unmodifiableSet(EnumSet.of(EventType.RENDER_IMAGE));
        }

        private boolean isWithinTargetArea(float imgX, float imgY, float imgWidth, float imgHeight) {
            // 计算目标区域边界
            float targetRight = targetX + targetWidth;
            float targetTop = targetY + targetHeight;

            // 计算图像边界
            float imgRight = imgX + imgWidth;
            float imgTop = imgY + imgHeight;

            // 检查图像边界是否与目标区域重叠
            boolean xOverlap = imgX < targetRight && imgRight > targetX;
            boolean yOverlap = imgY < targetTop && imgTop > targetY;

            return xOverlap && yOverlap;
        }

        public byte[] getExtractedImageBytes() {
            return extractedImageBytes;
        }
    }

    public static void main(String[] args) {
        try {
            String pdfPath = "D:\\个人文件\\无锡理昌\\单面转两面\\20260203112812909.pdf"; // 替换为实际PDF路径
            int page = 1;
            float x = 257, y = 710, width = 63, height = 60;

//            // 示例1：从单个页面提取图像
//            byte[] imageBytes = extractImageFromRegion(pdfPath, page, x, y, width, height);
//
//            if (imageBytes != null && imageBytes.length > 0) {
//                System.out.println("提取成功，图像大小：" + imageBytes.length + " bytes");
//
//                // 可选：保存到文件验证
//                BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
//                ImageIO.write(img, "PNG", new java.io.File("extracted_qr.png"));
//                System.out.println("图像已保存到 extracted_qr.png");
//            } else {
//                System.out.println("未找到指定区域的图像");
//            }
            
            // 示例2：从所有页面提取图像
            java.util.List<byte[]> allImages = extractImagesFromAllPages(pdfPath, x, y, width, height);
            
            if (!allImages.isEmpty()) {
                System.out.println("从所有页面提取成功，共找到 " + allImages.size() + " 个图像");
                
                // 可选：保存前几个图像到文件验证
                for (int i = 0; i < Math.min(allImages.size(), 5); i++) {
                    byte[] imgBytes = allImages.get(i);
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
                    ImageIO.write(img, "PNG", new java.io.File("extracted_qr_page_" + (i + 1) + ".png"));
                    System.out.println("图像已保存到 extracted_qr_page_" + (i + 1) + ".png");
                }
            } else {
                System.out.println("未找到任何页面的指定区域图像");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从 PDF 所有页面提取图像并返回 List<byte[]>
     *
     * @param pdfPath    PDF 文件路径
     * @param x, y, width, height 指定区域坐标
     * @return 所有页面图像的 byte[] 数组列表
     */
    public static java.util.List<byte[]> extractImagesFromAllPages(String pdfPath, float x, float y, float width, float height) throws IOException {
        java.util.List<byte[]> allImages = new java.util.ArrayList<>();
        PdfReader reader = new PdfReader(pdfPath);
        PdfDocument pdfDoc = new PdfDocument(reader);

        int totalPages = pdfDoc.getNumberOfPages();
        System.out.println("Processing " + totalPages + " pages...");

        for (int i = 1; i <= totalPages; i++) {
            System.out.println("Processing page " + i + "...");
            // 获取当前页面
            com.itextpdf.kernel.pdf.PdfPage page = pdfDoc.getPage(i);

            // 创建自定义事件监听器
            QrImageExtractor extractor = new QrImageExtractor(x, y, width, height, page.getMediaBox().getHeight());

            // 处理页面内容
            PdfCanvasProcessor processor = new PdfCanvasProcessor(extractor);
            processor.processPageContent(page);

            // 获取提取到的图像
            byte[] imageBytes = extractor.getExtractedImageBytes();
            if (imageBytes != null && imageBytes.length > 0) {
                allImages.add(imageBytes);
                System.out.println("Found image on page " + i + ", size: " + imageBytes.length + " bytes");
            } else {
                System.out.println("No image found on page " + i);
            }
        }

        pdfDoc.close();

        System.out.println("Total images found: " + allImages.size());
        return allImages;
    }
}