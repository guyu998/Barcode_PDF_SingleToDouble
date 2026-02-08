package com.singletodouble.util.pdf;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfButtonFormField;

import java.util.List;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;


public class ExtractTextChunksInRegion {
    public static void main(String[] args) throws Exception {
//        PdfDocument pdfDoc = new PdfDocument(new PdfReader("D:\\个人文件\\\\单面转两面\\test2.pdf"));
//
//        // 定义你的区域（示例）
//        float x = 22, y = 126, width = 120, height = 20;
//        Rectangle rect = new Rectangle(x , y, width, height);
//
//        // 使用项目自定义的文本提取策略，该策略会收集所有文本块及其位置信息
//        TextWithLocationStrategy strategy = new TextWithLocationStrategy();
//        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
//        parser.processPageContent(pdfDoc.getFirstPage());
//
//        // 获取所有文本块
//        List<com.singletodouble.util.pdf.TextChunk> textChunks = strategy.getChunks();
//
//        System.out.println("Found " + textChunks.size() + " text chunks in region:");
//        for (com.singletodouble.util.pdf.TextChunk chunk : textChunks) {
//            // 检查文本块是否在指定区域内
//            if (isChunkInRegion(chunk, rect)) {
//                System.out.printf(
//                        "Text: '%s' | BBox[x=%.2f, y=%.2f, w=%.2f, h=%.2f]%n",
//                        chunk.text.trim(),
//                        chunk.x, chunk.y, chunk.width, chunk.height
//                );
//            }
//        }
//
//        pdfDoc.close();
        Map<String, String> formData = new java.util.HashMap<>();
        formData.put("F-No1-1", "73480-06430-C0");
        formData.put("F-No1-3", "7F6070W7010");
        formData.put("F-No1-4", "后座双带扣");
        // 生成QR码
        Map<String, byte[]> imageData = new java.util.HashMap<>();
        String qrContent = "999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999";
        byte[] qrCodeBytes = generateQRCode(qrContent, 60, 60);
        imageData.put("F-Img1-1", qrCodeBytes);
        imageData.put("B-Img1-1", qrCodeBytes);
        imageData.put("B-Img2-1", qrCodeBytes);
        fillAndFlattenForm("D:\\个人文件\\\\单面转两面\\sample.pdf", "D:\\个人文件\\\\单面转两面\\sample2.pdf", formData, imageData);

        compressPdf("D:\\个人文件\\\\单面转两面\\sample2.pdf", "D:\\个人文件\\\\单面转两面\\sample2_compressed.pdf");
     }

    /**
     * 对已生成的PDF文件进行后处理压缩
     * @param inputPdfPath 输入PDF路径
     * @param outputPdfPath 输出压缩后的PDF路径
     */
    public static void compressPdf(String inputPdfPath, String outputPdfPath) throws Exception {
        // 读取原始PDF文件
        PdfReader reader = new PdfReader(inputPdfPath);
        // 创建PDF写入器，设置最高压缩级别
        PdfWriter writer = new PdfWriter(outputPdfPath);
        writer.setCompressionLevel(9);

        // 初始化PDF文档
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        // 文档会自动进行压缩，无需额外操作
        // 关闭文档完成压缩过程
        pdfDoc.close();

        System.out.printf("PDF压缩完成: %s%n", outputPdfPath);
    }
    /**
     * 填充PDF表单并扁平化
     * @param inputPdfPath 输入PDF表单路径
     * @param outputPdfPath 输出PDF路径
     * @param formData 表单数据映射
     */
    public static void fillAndFlattenForm(String inputPdfPath, String outputPdfPath, Map<String, String> formData, Map<String, byte[]> imageData) throws Exception {
        // 创建PDF文档
        // 创建PDF文档，设置最高压缩级别
        PdfReader reader = new PdfReader(inputPdfPath);
        PdfWriter writer = new PdfWriter(outputPdfPath);
        // 设置压缩级别以优化文件大小（9为最高压缩）
        writer.setCompressionLevel(9);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        // 获取PDF表单
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        // 填充表单数据
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            // 获取表单字段并设置值
            PdfFormField field = form.getField(fieldName);
            if (field != null) {
                field.setValue(fieldValue);
                System.out.printf("已填充字段: %s = %s%n", fieldName, fieldValue);
            } else {
                System.out.printf("警告: 未找到字段 %s%n", fieldName);
            }
        }

        // 填充图像表单数据
        for (Map.Entry<String, byte[]> entry : imageData.entrySet()) {
            String fieldName = entry.getKey();
            byte[] imageBytes = entry.getValue();

            // 获取表单字段
            PdfFormField field = form.getField(fieldName);
            if (field != null && field instanceof PdfButtonFormField) {
                // 设置图像到按钮字段
                setImageInButtonField(pdfDoc, (PdfButtonFormField) field, imageBytes);
                System.out.printf("已填充图像字段: %s%n", fieldName);
            } else {
                System.out.printf("警告: 未找到图像字段 %s 或字段类型不是按钮字段%n", fieldName);
            }
        }
        // 扁平化表单（移除交互性）
        form.flattenFields();
        System.out.println("表单已扁平化");
//        optimizePdfContentStreams(pdfDoc);
        // 关闭文档
        pdfDoc.close();
        System.out.printf("已保存到: %s%n", outputPdfPath);
    }

    /**
     * iText 7.2.5兼容的PDF内容流优化方法
     * 通过遍历PDF对象并优化内容流来减小文件大小
     * @param pdfDoc PDF文档对象
     */
//    private static void optimizePdfContentStreams(PdfDocument pdfDoc) {
//        try {
//            // 获取PDF目录对象
//            com.itextpdf.kernel.pdf.PdfDictionary catalog = pdfDoc.getCatalog().getPdfObject();
//
//            // 遍历所有页面
//            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
//                com.itextpdf.kernel.pdf.PdfPage page = pdfDoc.getPage(i);
//                com.itextpdf.kernel.pdf.PdfDictionary pageDict = page.getPdfObject();
//
//                // 获取页面内容流
//                com.itextpdf.kernel.pdf.PdfObject contents = pageDict.get(com.itextpdf.kernel.pdf.PdfName.Contents);
//                if (contents != null) {
//                    // 标记内容为已修改，触发内部优化
//                    contents.setModified();
//                }
//
//                // 获取页面资源
//                com.itextpdf.kernel.pdf.PdfObject resources = pageDict.get(com.itextpdf.kernel.pdf.PdfName.Resources);
//                if (resources != null) {
//                    resources.setModified();
//                }
//            }
//
//            // 标记目录为已修改
//            catalog.setModified();
//
//            System.out.println("PDF内容流优化完成");
//
//        } catch (Exception e) {
//            System.err.println("优化PDF内容流时发生错误: " + e.getMessage());
//        }
//    }
    /**
     * 在按钮字段中设置图像
     * @param pdfDoc PDF文档
     * @param buttonField 按钮字段
     * @param imageBytes 图像字节数组
     */
    private static void setImageInButtonField(PdfDocument pdfDoc, PdfButtonFormField buttonField, byte[] imageBytes) throws Exception {
        // 读取图像数据
        ImageData imageData = ImageDataFactory.create(imageBytes);

        // 获取按钮字段的小部件列表
        java.util.List<com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation> widgets = buttonField.getWidgets();
        if (widgets.isEmpty()) {
            System.out.println("警告: 按钮字段没有小部件");
            return;
        }

        // 获取第一个小部件
        com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation widget = widgets.get(0);

        // 获取按钮字段的边界框
        com.itextpdf.kernel.geom.Rectangle widgetRect = widget.getRectangle().toRectangle();
        float fieldWidth = widgetRect.getWidth();
        float fieldHeight = widgetRect.getHeight();

        // 固定图片尺寸为2.17 * 2.17（单位：厘米）
        // 对于二维码等复杂图像，可能需要更大尺寸以确保完全显示
        float desiredWidth = 2.1f * 42f; // 增加到1.5倍大小以确保二维码完整显示
        float desiredHeight = 2.1f * 42f; // 增加到1.5倍大小以确保二维码完整显示

        // 计算图像缩放比例以适应指定尺寸
        float imageWidth = imageData.getWidth();
        float imageHeight = imageData.getHeight();
        float scaleX = desiredWidth / imageWidth;
        float scaleY = desiredHeight / imageHeight;
        float scale = Math.min(scaleX, scaleY); // 保持宽高比

        // 计算缩放后的图像尺寸和位置
        float scaledImageWidth = imageWidth * scale;
        float scaledImageHeight = imageHeight * scale;
        float x = (fieldWidth - scaledImageWidth) / 2;
        float y = (fieldHeight - scaledImageHeight) / 2;

        // 创建表单XObject作为按钮外观
        PdfFormXObject appearance = new PdfFormXObject(new com.itextpdf.kernel.geom.Rectangle(fieldWidth, fieldHeight));

        // 创建画布并添加图像
        com.itextpdf.kernel.pdf.canvas.PdfCanvas canvas = new com.itextpdf.kernel.pdf.canvas.PdfCanvas(appearance, pdfDoc);

        // 使用正确的addImage方法，iText 7.2.5中可用的格式
        canvas.addImageFittedIntoRectangle(imageData, new com.itextpdf.kernel.geom.Rectangle(x, y, scaledImageWidth, scaledImageHeight), true);

        // 设置按钮的正常外观
        widget.setNormalAppearance(appearance.getPdfObject());
    }
    /**
     * 生成二维码图像
     * @param content 二维码内容
     * @param width 二维码宽度
     * @param height 二维码高度
     * @return 二维码图像字节数组
     */
    public static byte[] generateQRCode(String content, int width, int height) throws Exception {
        // 使用最小化的二维码尺寸，平衡可读性和文件大小
        int optimalSize = Math.min(Math.max(width, 100), 100); // 限制在50-100之间

        // 生成二维码矩阵，使用最小纠错级别以减少数据量
        com.google.zxing.MultiFormatWriter writer = new com.google.zxing.MultiFormatWriter();
        Map<com.google.zxing.EncodeHintType, Object> hints = new java.util.HashMap<>();
        hints.put(com.google.zxing.EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.L);
        hints.put(com.google.zxing.EncodeHintType.MARGIN, 1); // 设置最小边距

        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, optimalSize, optimalSize, hints);
        // 将BitMatrix转换为BufferedImage
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        // 将BufferedImage转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 使用PNG格式并设置高压缩参数
        // 如果不支持压缩，退回到简单的PNG写入
        ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }
    /**
     * 检查文本块是否在指定区域内
     */
    private static boolean isChunkInRegion(com.singletodouble.util.pdf.TextChunk chunk, Rectangle region) {
        // 计算文本块的边界框
        float chunkLeft = chunk.x;
        float chunkBottom = chunk.y;
        float chunkRight = chunk.x + chunk.width;
        float chunkTop = chunk.y + chunk.height;

        // 检查是否有重叠
        return !(chunkRight < region.getX() ||
                 chunkLeft > region.getX() + region.getWidth() ||
                 chunkTop < region.getY() ||
                 chunkBottom > region.getY() + region.getHeight());
    }
}