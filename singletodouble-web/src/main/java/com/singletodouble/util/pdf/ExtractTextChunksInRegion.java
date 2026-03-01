package com.singletodouble.util.pdf;

import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;


public class ExtractTextChunksInRegion {
    public static void main(String[] args) throws Exception {
//        PdfDocument pdfDoc = new PdfDocument(new PdfReader("D:\\个人文件\\无锡理昌\\单面转两面\\20260203112812909.pdf"));
//
//        // 定义你的区域（示例）
//        float x = 151, y = 657, width = 32, height = 13;
//        Rectangle rect = new Rectangle(x , y, width, height);
//        // 获取所有文本块
//        // 使用项目自定义的文本提取策略，该策略会收集所有文本块及其位置信息
//        TextWithLocationStrategy strategy = new TextWithLocationStrategy();
////        parser.processPageContent(pdfDoc.getFirstPage());
//        // 处理所有页面
//        int totalPages = pdfDoc.getNumberOfPages();
//        System.out.println("Processing " + totalPages + " pages...");

//        for (int i = 1; i <= totalPages; i++) {
//            PdfPage page = pdfDoc.getPage(i);
//            System.out.println("Processing page " + i + "...");
//            // 设置当前页码
//            strategy.setCurrentPageNumber(i);
//            PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
//            parser.processPageContent(page);
//        }
//        // 获取所有文本块
//        List<com.singletodouble.util.pdf.TextChunk> textChunks = strategy.getChunks();
//        System.out.println("Found " + textChunks.size() + " text chunks in all pages:");
//        for (com.singletodouble.util.pdf.TextChunk chunk : textChunks) {
//            // 检查文本块是否在指定区域内
//            if (isChunkInRegion(chunk, rect)) {
//                System.out.printf(
//                        "Text: '%s' | BBox[x=%.2f, y=%.2f, w=%.2f, h=%.2f, p=%d]%n",
//                        chunk.getText().trim(),
//                        chunk.getX(), chunk.getY(), chunk.getWidth(), chunk.getHeight(),chunk.getPageNumber());
//            }
//        }






        // 从指定区域读取图像
//        List<byte[]> imageBytesList = new ArrayList<>();
//
//
//        pdfDoc.close();
//        Map<String, String> formData = new java.util.HashMap<>();
//        formData.put("F-No1-1", "73480-06430-C0");
//        formData.put("F-No1-3", "7F6070W7010");
//        formData.put("F-No1-4", "后座双带扣");
//        // 示例1：生成单页PDF
//        Map<String, byte[]> imageData = new java.util.HashMap<>();
        String qrContent = "0123456789          0123456789          0123456789           012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
//        byte[] qrCodeBytes = generateQRCode(qrContent, 60, 60);
//        imageData.put("F-Img1-1", qrCodeBytes);
//        imageData.put("B-Img1-1", qrCodeBytes);
//        imageData.put("B-Img2-1", qrCodeBytes);
//        fillAndFlattenForm("D:\\个人文件\\无锡理昌\\单面转两面\\sample.pdf", "D:\\个人文件\\无锡理昌\\单面转两面\\sample2.pdf", formData, imageData);

//        compressPdf("D:\\个人文件\\无锡理昌\\单面转两面\\sample2.pdf", "D:\\个人文件\\无锡理昌\\单面转两面\\sample2_compressed.pdf");
        
        // 示例2：生成多页PDF，每页有不同的表单内容
        List<Map<String, String>> multiFormData = new ArrayList<>();
        List<Map<String, byte[]>> multiImageData = new ArrayList<>();
        
        // 生成5页不同的数据
        for (int i = 1; i <= 2; i++) {
            Map<String, String> pageFormData = new HashMap<>();
            pageFormData.put("F-No1-1", "73480-06430-C0-P" + i);
            pageFormData.put("F-No1-3", "7F6070W7010-P" + i);
            pageFormData.put("F-No1-4", "后座双带扣-P" + i);
            pageFormData.put("F-No1-7", "2270");
            pageFormData.put("F-No2-7", "2270");
            pageFormData.put("F-No1-11", "119");
            pageFormData.put("F-No1-12", "120");
            pageFormData.put("F-No1-29", "121");
            pageFormData.put("F-No2-29", "121");
            Map<String, byte[]> pageImageData = new HashMap<>();
            String pageQrContent = qrContent.substring(0, 180);
            byte[] pageQrCodeBytes = generateQRCode(pageQrContent, 60, 60);
//            pageImageData.put("F-Img1-1", pageQrCodeBytes);
            pageImageData.put("B-Img1-1", pageQrCodeBytes);
//            pageImageData.put("F-Img2-1", pageQrCodeBytes);
//            pageImageData.put("F-Img3-1", pageQrCodeBytes);
//            pageImageData.put("F-Img4-1", pageQrCodeBytes);
//            pageImageData.put("B-Img2-1", pageQrCodeBytes);
//            pageImageData.put("B-Img3-1", pageQrCodeBytes);
//            pageImageData.put("B-Img4-1", pageQrCodeBytes);
            
            multiFormData.add(pageFormData);
            multiImageData.add(pageImageData);
        }
        
        generateMultiPagePdf("D:\\个人文件\\无锡理昌\\单面转两面\\materialTemplate.pdf",
                            "D:\\个人文件\\无锡理昌\\单面转两面\\multi_page_sample.pdf", 
                            multiFormData, multiImageData);
        
        compressPdf("D:\\个人文件\\无锡理昌\\单面转两面\\multi_page_sample.pdf",
                   "D:\\个人文件\\无锡理昌\\单面转两面\\multi_page_sample_compressed.pdf");
     }

     /* 基于模板生成多页PDF，每页有不同的表单数据
     * @param templatePath 模板PDF路径
     * @param outputPdfPath 输出PDF路径
     * @param formDataList 表单数据列表，每个元素对应一页
     * @param imageDataList 图像数据列表，每个元素对应一页
     */
    public static void generateMultiPagePdf(String templatePath, String outputPdfPath,
                                            List<Map<String, String>> formDataList,
                                            List<Map<String, byte[]>> imageDataList) throws Exception {

        if (formDataList.isEmpty()) {
            System.out.println("表单数据列表为空，无法生成PDF");
            return;
        }

        // 创建输出PDF文档（空文档）
        PdfWriter writer = new PdfWriter(outputPdfPath);
        writer.setCompressionLevel(9);
        PdfDocument outputPdfDoc = new PdfDocument(writer);
        PdfFont customFont = null;
        try {
            // 使用系统中的黑体字体（您可以根据需要修改字体路径）
            String fontPath = "C:/Windows/Fonts/simhei.ttf"; // 黑体
            if (java.nio.file.Files.exists(java.nio.file.Paths.get(fontPath))) {
                customFont = PdfFontFactory.createFont(fontPath, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                PdfFontFactory.createFont(StandardFonts.HELVETICA);
                System.out.println("已加载自定义字体: " + fontPath);
            }
        } catch (Exception e) {
            System.err.println("无法加载自定义字体: " + e.getMessage());
        }
        // 为每个表单数据生成一页
        for (int i = 0; i < formDataList.size(); i++) {
            System.out.println("生成第 " + (i + 1) + " 页...");

            // 步骤1: 读取模板并进行修改
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfReader templateReader = new PdfReader(templatePath);
            PdfWriter templateWriter = new PdfWriter(baos);
            PdfDocument templateDoc = new PdfDocument(templateReader, templateWriter);
            templateWriter.setCompressionLevel(9);
            // 获取当前页的表单数据
            Map<String, String> currentFormData = formDataList.get(i);
            Map<String, byte[]> currentImageData = i < imageDataList.size() ? imageDataList.get(i) : new HashMap<>();

            // 填充并扁平化表单
            PdfAcroForm form = PdfAcroForm.getAcroForm(templateDoc, true);

            // 填充文本表单数据
            for (Map.Entry<String, String> entry : currentFormData.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();

                PdfFormField field = form.getField(fieldName);
                if (field != null) {
                    field.setValue(fieldValue);
                    System.out.printf("第%d页已填充字段: %s = %s%n", i + 1, fieldName, fieldValue);
                } else {
                    System.out.printf("第%d页警告: 未找到字段 %s%n", i + 1, fieldName);
                }
            }

            // 填充图像数据 - 直接在指定位置绘制二维码
            for (Map.Entry<String, byte[]> entry : currentImageData.entrySet()) {
                String fieldName = entry.getKey();
                byte[] imageBytes = entry.getValue();

                // 不依赖于按钮字段，直接在指定位置绘制二维码
                // 这里我们假设所有图像数据都需要绘制在 (258, 711) 位置
                drawQrCodeAtPosition(templateDoc, imageBytes, 258, 711);
                System.out.printf("第%d页已在指定位置绘制二维码: %s%n", i + 1, fieldName);
            }

            // 扁平化表单
            form.flattenFields();
            System.out.printf("第%d页表单已扁平化%n", i + 1);

            // 关闭模板文档以保存修改
            templateDoc.close();

            // 步骤2: 重新读取修改后的文档并复制页面
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            PdfReader modifiedReader = new PdfReader(bais);
            PdfDocument modifiedDoc = new PdfDocument(modifiedReader);

            // 复制页面到输出文档
            modifiedDoc.copyPagesTo(1, 2, outputPdfDoc);

            // 关闭修改后的文档
            modifiedDoc.close();
        }

        // 关闭输出文档
        outputPdfDoc.close();

        System.out.println("成功生成 " + formDataList.size() + " 页PDF: " + outputPdfPath);
    }
    /**
     * 填充单个页面的表单数据
     */
    private static void fillPageFormData(PdfDocument pdfDoc, PdfPage page, 
                                        Map<String, String> formData, 
                                        Map<String, byte[]> imageData) throws Exception {
        // 获取PDF表单
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        // 加载STSong-Light字体
        PdfFont songFont = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", pdfDoc);
        // 填充表单数据
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            
            PdfFormField field = form.getField(fieldName);
            if (field != null) {
                // 设置字段值
                field.setValue(fieldValue);
                // 设置字体为STSong-Light
                if (field instanceof PdfTextFormField) {
                    field.setFont(songFont);
                    System.out.printf("已填充字段并设置字体: %s = %s%n", fieldName, fieldValue);
                } else {
                    System.out.printf("已填充字段: %s = %s%n", fieldName, fieldValue);
                }
            } else {
                System.out.printf("警告: 未找到字段 %s%n", fieldName);
            }
        }
        
        // 填充图像数据 - 直接在指定位置绘制二维码
        for (Map.Entry<String, byte[]> entry : imageData.entrySet()) {
            String fieldName = entry.getKey();
            byte[] imageBytes = entry.getValue();
            
            // 不依赖于按钮字段，直接在指定位置绘制二维码
            drawQrCodeAtPosition(pdfDoc, imageBytes, 258, 711);
            System.out.printf("已在指定位置绘制二维码: %s%n", fieldName);
        }
        
        // 关键：扁平化表单（移除交互性）
        form.flattenFields();
        System.out.println("表单已扁平化");
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
        writer.setCompressionLevel(9);
        writer.setSmartMode(true);
        pdfDoc.setFlushUnusedObjects(true);
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

        // 填充图像数据 - 直接在指定位置绘制二维码
        for (Map.Entry<String, byte[]> entry : imageData.entrySet()) {
            String fieldName = entry.getKey();
            byte[] imageBytes = entry.getValue();

            // 不依赖于按钮字段，直接在指定位置绘制二维码
            drawQrCodeAtPosition(pdfDoc, imageBytes, 258, 711);
            System.out.printf("已在指定位置绘制二维码: %s%n", fieldName);
        }
        // 扁平化表单（移除交互性）
        form.flattenFields();
        System.out.println("表单已扁平化");
        // 关闭文档
        pdfDoc.close();
        System.out.printf("已保存到: %s%n", outputPdfPath);
    }


    /**
     * 在指定位置直接绘制二维码图像
     * @param pdfDoc PDF文档
     * @param buttonField 按钮字段（未使用，保持兼容性）
     * @param imageBytes 图像字节数组
     */
    private static void setImageInButtonField(PdfDocument pdfDoc, PdfButtonFormField buttonField, byte[] imageBytes) throws Exception {
        // 调用新方法，保持兼容性
        drawQrCodeAtPosition(pdfDoc, imageBytes, 258, 711);
    }
    
    /**
     * 在指定位置直接绘制二维码（放在底层，底色透明）
     * @param pdfDoc PDF文档
     * @param imageBytes 图像字节数组
     * @param x X坐标
     * @param y Y坐标
     */
    private static void drawQrCodeAtPosition(PdfDocument pdfDoc, byte[] imageBytes, float x, float y) throws Exception {
        // 读取二维码图像数据
        ImageData qrCodeData = ImageDataFactory.create(imageBytes);

        // 获取当前页面（假设处理的是第一页）
        PdfPage page = pdfDoc.getPage(1);
        if (page == null) {
            System.out.println("警告: PDF文档没有页面，无法绘制二维码");
            return;
        }

        // 创建画布，使用appendMode=false确保绘制在底层
        PdfCanvas canvas = new PdfCanvas(page, false);

        // 设置二维码大小
        float qrSize = 70; // 二维码尺寸

        // 获取二维码图像的原始尺寸
        float qrCodeWidth = qrCodeData.getWidth();
        float qrCodeHeight = qrCodeData.getHeight();

        // 计算缩放比例
        float scale = qrSize / Math.max(qrCodeWidth, qrCodeHeight);

        // 应用变换
        canvas.saveState();
        
        // 设置透明度 - 确保二维码背景透明
        PdfExtGState extGState = new PdfExtGState();
        extGState.setFillOpacity(1.0f);
        extGState.setStrokeOpacity(1.0f);
        canvas.setExtGState(extGState);
        
        canvas.concatMatrix(scale, 0, 0, scale, x, y);

        // 添加图像
        canvas.addImageAt(qrCodeData, 0, 0, false);
        canvas.restoreState();
        
        System.out.println("二维码已绘制在底层位置（底色透明）: x=" + x + ", y=" + y);
    }

     /* 生成二维码图像
     * @param content 二维码内容
     * @param width 二维码宽度
     * @param height 二维码高度
     * @return 二维码图像字节数组
     */
    public static byte[] generateQRCode(String content, int width, int height) throws Exception {
        // 验证内容，确保空格存在
        System.out.println("QR Code Content Length: " + content.length());
        System.out.println("QR Code Content: '" + content + "'");
        
        // 使用最小化的二维码尺寸，平衡可读性和文件大小
        int optimalSize = Math.min(Math.max(width, 100), 100);
        
        // 生成二维码矩阵，使用最小纠错级别以减少数据量
        com.google.zxing.MultiFormatWriter writer = new com.google.zxing.MultiFormatWriter();
        Map<com.google.zxing.EncodeHintType, Object> hints = new java.util.HashMap<>();
        hints.put(com.google.zxing.EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.L);
        hints.put(com.google.zxing.EncodeHintType.MARGIN, 1);
        hints.put(com.google.zxing.EncodeHintType.CHARACTER_SET, "UTF-8"); // 确保字符集正确
        
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, optimalSize, optimalSize, hints);
        
        // 创建具有透明背景的BufferedImage
        BufferedImage bufferedImage = new BufferedImage(optimalSize, optimalSize, BufferedImage.TYPE_INT_ARGB);
        
        // 填充透明背景
        for (int x = 0; x < optimalSize; x++) {
            for (int y = 0; y < optimalSize; y++) {
                if (bitMatrix.get(x, y)) {
                    // 二维码黑色部分
                    bufferedImage.setRGB(x, y, 0xFF000000); // 不透明黑色
                } else {
                    // 透明背景
                    bufferedImage.setRGB(x, y, 0x00000000); // 完全透明
                }
            }
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }
    /**
     * 检查文本块是否在指定区域内
     */
    private static boolean isChunkInRegion(com.singletodouble.util.pdf.TextChunk chunk, Rectangle region) {

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