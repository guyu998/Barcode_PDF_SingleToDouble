package com.singletodouble.util.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扁平化表单生成器 - 使用 XObject 方式
 */
public class FlatteningFormGenerator {
    
    /**
     * 布局对齐方式枚举
     */
    public enum Alignment {
        LEFT,      // 靠左
        CENTER,    // 居中
        RIGHT      // 靠右
    }
    
    /**
     * 字段属性类
     */
    public static class FieldProperties {
        private final int pageNumber;      // 页号
        private final FontType fontType;   // 字库
        private final float fontSize;      // 字体大小
        private final Color fontColor;     // 字体颜色
        private final Alignment alignment; // 对齐方式
        
        public FieldProperties(int pageNumber, FontType fontType, float fontSize, Color fontColor, Alignment alignment) {
            this.pageNumber = pageNumber;
            this.fontType = fontType;
            this.fontSize = fontSize;
            this.fontColor = fontColor;
            this.alignment = alignment;
        }
        
        public int getPageNumber() { return pageNumber; }
        public FontType getFontType() { return fontType; }
        public float getFontSize() { return fontSize; }
        public Color getFontColor() { return fontColor; }
        public Alignment getAlignment() { return alignment; }
    }
    
    /**
     * 字体类型枚举
     */
    public enum FontType {
        HELVETICA,    // Helvetica 字体
        TIMES_ROMAN   // Times Roman 字体
    }
    
    private final PdfDocument pdfDoc;
    private final PdfFormXObject xObjectPage1; // 第1页的XObject
    private final PdfFormXObject xObjectPage2; // 第2页的XObject
    private final PdfFont helveticaFont; // HELVETICA 字体
    private final PdfFont timesRomanFont; // TIMES_ROMAN 字体
    private final Map<String, Rectangle> templateFieldPositions; // 模板字段位置信息
    private int pageCounter = 0;

    public FlatteningFormGenerator(String templatePath, String destPath) throws IOException {
        this.pdfDoc = new PdfDocument(new PdfWriter(destPath));

        // 1. 创建两种共享字体
        this.helveticaFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        // 使用 iText 内置的宋体映射
        this.timesRomanFont = PdfFontFactory.createFont(
                "STSong-Light",
                "UniGB-UCS2-H",
                PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED
        );
        pdfDoc.addFont(helveticaFont);
        pdfDoc.addFont(timesRomanFont);

        // 2. 读取模板并创建两页的 XObject，同时提取字段位置信息
        PdfDocument templateDoc = new PdfDocument(new PdfReader(templatePath));
        int numberOfPages = templateDoc.getNumberOfPages();
        
        if (numberOfPages < 2) {
            throw new IOException("模板需要至少2页");
        }
        
        // 提取模板中的字段位置信息
        this.templateFieldPositions = extractTemplateFieldPositions(templateDoc);
        
        // 创建第1页的XObject
        PdfPage templatePage1 = templateDoc.getPage(1);
        this.xObjectPage1 = templatePage1.copyAsFormXObject(pdfDoc);
        
        // 创建第2页的XObject
        PdfPage templatePage2 = templateDoc.getPage(2);
        this.xObjectPage2 = templatePage2.copyAsFormXObject(pdfDoc);
        
        templateDoc.close();
        
        System.out.println("已创建两页的 XObject 模板");
        System.out.println("提取到的字段位置信息: " + templateFieldPositions.size() + " 个字段");
        for (String fieldName : templateFieldPositions.keySet()) {
            System.out.println("- " + fieldName + ": " + templateFieldPositions.get(fieldName));
        }
    }

    /**
     * 从模板中提取字段位置信息
     */
    private Map<String, Rectangle> extractTemplateFieldPositions(PdfDocument templateDoc) {
        Map<String, Rectangle> fieldPositions = new HashMap<>();
        
        PdfAcroForm templateForm = PdfAcroForm.getAcroForm(templateDoc, false);
        if (templateForm != null) {
            Map<String, PdfFormField> templateFields = templateForm.getFormFields();
            if (templateFields != null) {
                for (Map.Entry<String, PdfFormField> entry : templateFields.entrySet()) {
                    String fieldName = entry.getKey();
                    PdfFormField field = entry.getValue();
                    
                    // 获取字段的位置信息
                    List<com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation> widgets = field.getWidgets();
                    if (widgets != null && !widgets.isEmpty()) {
                        Rectangle rect = widgets.get(0).getRectangle().toRectangle();
                        fieldPositions.put(fieldName, rect);
                    }
                }
            }
        }
        
        return fieldPositions;
    }

    /**
     * 添加一页并立即扁平化（支持字段属性）
     * @param fieldData 字段数据，key为字段名，value为字段值和属性
     * @param pageNumber 页号（1或2）
     */
    public void addFlattenedPageWithProperties(Map<String, Map.Entry<String, FieldProperties>> fieldData, int pageNumber) {
        pageCounter++;
        try {
            // 创建新页面
            PdfPage newPage = pdfDoc.addNewPage();
            PdfCanvas canvas = new PdfCanvas(newPage);

            // 根据传入的页号选择对应的 XObject
            PdfFormXObject xObject = (pageNumber == 1) ? xObjectPage1 : xObjectPage2;
            
            // 添加 XObject 背景
            canvas.addXObject(xObject);

            // 创建表单字段
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            
            // 创建字段并设置属性
            for (Map.Entry<String, Map.Entry<String, FieldProperties>> entry : fieldData.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue().getKey();
                FieldProperties properties = entry.getValue().getValue();

                // 使用唯一字段名避免冲突
                String uniqueFieldName = fieldName + "_" + pageCounter;

                // 获取模板中的字段位置，如果不存在则使用默认位置
                Rectangle fieldPosition = templateFieldPositions.get(fieldName);
                if (fieldPosition == null) {
                    // 使用默认位置
                    fieldPosition = new Rectangle(100, 700, 200, 20);
                    System.out.println("警告: 字段 " + fieldName + " 在模板中未找到，使用默认位置");
                } else {
                    // 直接使用模板中的原始位置，不调整位置
                    // 对齐方式通过设置字段的文本对齐属性来实现
                    System.out.println("使用模板原始位置: " + fieldName + " -> " + fieldPosition + " (对齐: " + properties.getAlignment() + ")");
                }

                // 创建文本字段
                PdfTextFormField field = PdfFormField.createText(
                        pdfDoc,
                        fieldPosition,
                        uniqueFieldName,
                        fieldValue
                );

                // 设置字体
                PdfFont selectedFont = properties.getFontType() == FontType.TIMES_ROMAN ? timesRomanFont : helveticaFont;
                field.setFont(selectedFont);

                // 设置字体大小
                field.setFontSize(properties.getFontSize());

                // 设置字体颜色
                field.setColor(properties.getFontColor());
                
                // 设置字段的文本对齐方式
                setFieldAlignment(field, properties.getAlignment());

                // 添加到表单
                form.addField(field, newPage);

                System.out.println("已添加字段: " + fieldName + " = " + fieldValue +
                        " (模板页号: " + pageNumber +
                        ", 字段页号: " + properties.getPageNumber() +
                        ", 字体: " + properties.getFontType() +
                        ", 大小: " + properties.getFontSize() +
                        ", 颜色: " + properties.getFontColor() +
                        ", 对齐: " + properties.getAlignment() + 
                        ", 位置: " + fieldPosition + ")");
            }

            // ⚡ 立即扁平化当前页
            form.flattenFields();
            System.out.println("第 " + pageCounter + " 页已生成并扁平化 (使用模板第" + pageNumber + "页)");
            
        } catch (Exception e) {
            System.err.println("添加字段失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据对齐方式设置字段的文本对齐属性
     */
    private void setFieldAlignment(PdfTextFormField field, Alignment alignment) {
        try {
            // 设置字段的文本对齐方式
            switch (alignment) {
                case CENTER:
                    field.setJustification(PdfFormField.ALIGN_CENTER);
                    break;
                case RIGHT:
                    field.setJustification(PdfFormField.ALIGN_RIGHT);
                    break;
                case LEFT:
                default:
                    field.setJustification(PdfFormField.ALIGN_LEFT);
                    break;
            }
        } catch (Exception e) {
            System.err.println("设置字段对齐方式失败: " + e.getMessage());
        }
    }

    /**
     * 添加一页并立即扁平化（简化版本，使用默认属性）
     * @param data 字段数据，key为字段名，value为字段值
     * @param pageNumber 页号（1或2）
     */
    public void addFlattenedPage(Map<String, String> data, int pageNumber) {
        // 创建默认的字段属性
        Map<String, Map.Entry<String, FieldProperties>> fieldData = new HashMap<>();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            // 默认属性：指定页号，Helvetica字体，12号字，黑色，靠左对齐
            FieldProperties properties = new FieldProperties(
                    pageNumber,
                    FontType.HELVETICA,
                    12f,
                    new DeviceRgb(0, 0, 0),
                    Alignment.LEFT
            );

            fieldData.put(entry.getKey(), new java.util.AbstractMap.SimpleEntry<>(entry.getValue(), properties));
        }

        // 调用支持属性的方法
        addFlattenedPageWithProperties(fieldData, pageNumber);
    }

    /**
     * 添加一页并立即扁平化（默认使用第1页模板）
     * @param data 字段数据，key为字段名，value为字段值
     */
    public void addFlattenedPage(Map<String, String> data) {
        addFlattenedPage(data, 1); // 默认使用第1页模板
    }

    /**
     * 添加一页并立即扁平化（支持字段属性，默认使用第1页模板）
     * @param fieldData 字段数据，key为字段名，value为字段值和属性
     */
    public void addFlattenedPageWithProperties(Map<String, Map.Entry<String, FieldProperties>> fieldData) {
        addFlattenedPageWithProperties(fieldData, 1); // 默认使用第1页模板
    }

    /**
     * 关闭文档
     */
    public void close() {
        pdfDoc.close();
    }

    // 使用示例
    public static void main(String[] args) throws IOException {
        // 示例: 生成6页文档，使用模板三次，每次填入不同的值
        System.out.println("示例: 生成6页文档（使用模板三次）");

        FlatteningFormGenerator generator = new FlatteningFormGenerator(
                "D:\\个人文件\\无锡理昌\\单面转两面\\materialTemplate.pdf",
                "D:\\个人文件\\无锡理昌\\单面转两面\\output_6pages.pdf"
        );

        // 第一次使用模板（第1-2页）
        System.out.println("第一次使用模板（第1-2页）");

        // 第1页：使用第1页模板
        Map<String, java.util.Map.Entry<String, FieldProperties>> page1Data1 = new java.util.HashMap<>();
        page1Data1.put("F-No1-1", new java.util.AbstractMap.SimpleEntry<>(
                "73480-06430-C0-P",
                new FieldProperties(1, FontType.HELVETICA, 14f, new DeviceRgb(255, 0, 0), Alignment.CENTER)
        ));
        page1Data1.put("F-No1-2", new java.util.AbstractMap.SimpleEntry<>(
                "LINE1(号口)",
                new FieldProperties(1, FontType.TIMES_ROMAN, 8f, new DeviceRgb(0, 0, 0), Alignment.RIGHT)
        ));
        generator.addFlattenedPageWithProperties(page1Data1, 1);

        // 第2页：使用第2页模板
        Map<String, java.util.Map.Entry<String, FieldProperties>> page2Data1 = new java.util.HashMap<>();
        page2Data1.put("B-No1-1", new java.util.AbstractMap.SimpleEntry<>(
                "LINE1(号口）",
                new FieldProperties(2, FontType.TIMES_ROMAN, 8f, new DeviceRgb(0, 0, 255), Alignment.LEFT)
        ));
        page2Data1.put("B-No1-2", new java.util.AbstractMap.SimpleEntry<>(
                "2270-P",
                new FieldProperties(2, FontType.HELVETICA, 10f, new DeviceRgb(0, 128, 0), Alignment.CENTER)
        ));
        generator.addFlattenedPageWithProperties(page2Data1, 2);

        // 第二次使用模板（第3-4页）
        System.out.println("第二次使用模板（第3-4页）");

        // 第3页：使用第1页模板（不同值）
        Map<String, java.util.Map.Entry<String, FieldProperties>> page1Data2 = new java.util.HashMap<>();
        page1Data2.put("F-No1-1", new java.util.AbstractMap.SimpleEntry<>(
                "73480-06431-C0-P",
                new FieldProperties(1, FontType.HELVETICA, 14f, new DeviceRgb(255, 0, 0), Alignment.CENTER)
        ));
        page1Data2.put("F-No1-2", new java.util.AbstractMap.SimpleEntry<>(
                "LINE1(号口）",
                new FieldProperties(1, FontType.TIMES_ROMAN, 8f, new DeviceRgb(0, 0, 0), Alignment.RIGHT)
        ));
        generator.addFlattenedPageWithProperties(page1Data2, 1);

        // 第4页：使用第2页模板（不同值）
        Map<String, java.util.Map.Entry<String, FieldProperties>> page2Data2 = new java.util.HashMap<>();
        page2Data2.put("B-No1-1", new java.util.AbstractMap.SimpleEntry<>(
                "LINE1(号口）",
                new FieldProperties(2, FontType.TIMES_ROMAN, 16f, new DeviceRgb(0, 0, 255), Alignment.LEFT)
        ));
        page2Data2.put("B-No1-2", new java.util.AbstractMap.SimpleEntry<>(
                "2271-P",
                new FieldProperties(2, FontType.HELVETICA, 10f, new DeviceRgb(0, 128, 0), Alignment.CENTER)
        ));
        generator.addFlattenedPageWithProperties(page2Data2, 2);

        // 第三次使用模板（第5-6页）
        System.out.println("第三次使用模板（第5-6页）");

        // 第5页：使用第1页模板（不同值）
        Map<String, java.util.Map.Entry<String, FieldProperties>> page1Data3 = new java.util.HashMap<>();
        page1Data3.put("F-No1-1", new java.util.AbstractMap.SimpleEntry<>(
                "73480-06432-C0-P",
                new FieldProperties(1, FontType.HELVETICA, 14f, new DeviceRgb(255, 0, 0), Alignment.CENTER)
        ));
        page1Data3.put("F-No1-2", new java.util.AbstractMap.SimpleEntry<>(
                "2026-02-03",
                new FieldProperties(1, FontType.TIMES_ROMAN, 12f, new DeviceRgb(0, 0, 0), Alignment.RIGHT)
        ));
        generator.addFlattenedPageWithProperties(page1Data3, 1);

        // 第6页：使用第2页模板（不同值）
        Map<String, java.util.Map.Entry<String, FieldProperties>> page2Data3 = new java.util.HashMap<>();
        page2Data3.put("B-No1-1", new java.util.AbstractMap.SimpleEntry<>(
                "7F6070W7012-P",
                new FieldProperties(2, FontType.TIMES_ROMAN, 16f, new DeviceRgb(0, 0, 255), Alignment.LEFT)
        ));
        page2Data3.put("B-No1-2", new java.util.AbstractMap.SimpleEntry<>(
                "2272-P",
                new FieldProperties(2, FontType.HELVETICA, 10f, new DeviceRgb(0, 128, 0), Alignment.CENTER)
        ));
        generator.addFlattenedPageWithProperties(page2Data3, 2);

        generator.close();
        System.out.println("6页文档生成完成！生成的文件包含6页，使用模板三次，每次填入不同的值");

    }
}