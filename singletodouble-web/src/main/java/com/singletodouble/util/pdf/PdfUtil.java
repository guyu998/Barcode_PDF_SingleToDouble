package com.singletodouble.util.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.util.IOUtils;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;

public class PdfUtil {
	
	public PdfDocument openDocument(File pdfFile) {
		if (!pdfFile.exists()) {
			return null;
		}
		
		return openDocument(pdfFile.getAbsolutePath());
	}

	public PdfDocument openDocument(String pdfFile) {
		PdfReader reader = null;
		try {
			reader = new PdfReader(pdfFile);
		} catch (Exception e) {
			
		}
        return new PdfDocument(reader);
	}
	
	public PdfDocument openDocument(InputStream in) {
		PdfReader reader = null;
		try {
			reader = new PdfReader(in);
		} catch (Exception e) {
			
		}
        return new PdfDocument(reader);
	}

	public int getPageSize(PdfDocument document) {
		if (document == null) {
			return 0;
		}
		
		return document.getNumberOfPages();
	}
	
	public String readText(PdfDocument document, int pageNo, float startX, float startY, float endX, float endY) {
		if (document == null) {
			return null;
		}

		Rectangle region = new Rectangle(startX, startY, endX - startX, endY - startY);
		TextExtractionStrategy strategy = new TextExtractionStrategy(region);
        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        parser.processPageContent(document.getPage(pageNo));
        
        return strategy.getResultantText();
	}
	
	public List<TextChunk> readPageText(PdfDocument document, int pageNo) {
		if (document == null) {
			return null;
		}
		
        TextWithLocationStrategy strategy = new TextWithLocationStrategy();
        strategy.setCurrentPageNumber(pageNo);
        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        parser.processPageContent(document.getPage(pageNo));
        
        return strategy.getChunks();
	}
	
	public List<TextChunk> readFileText(PdfDocument document) {
		if (document == null) {
			return null;
		}
		
        TextWithLocationStrategy strategy = new TextWithLocationStrategy();
        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        for (int i = 1; i <= getPageSize(document); i++) {
            PdfPage page = document.getPage(i);
            // 设置当前页码
            strategy.setCurrentPageNumber(i);
            parser.processPageContent(page);
        }

        return strategy.getChunks();
	}
	
	public byte[] readImage(PdfDocument document, int pageNo, float startX, float startY, float endX, float endY) {
		if (document == null) {
			return null;
		}

		Rectangle region = new Rectangle(startX, startY, endX - startX, endY - startY);
        ImageExtractor extractor = new ImageExtractor(region);
        PdfCanvasProcessor processor = new PdfCanvasProcessor(extractor);
        processor.processPageContent(document.getPage(pageNo));

        return extractor.getBytes();
	}

	public void closeDocument(PdfDocument document) {
		IOUtils.closeQuietly(document);
	}
	
    public static void main(String[] args) {
        try {
            String pdfPath = "C:\\Users\\Administrator\\Desktop\\20260203112812909_TRCW_TRCW002_EKGKanbanForm.pdf"; // 替换为实际PDF路径

            // 示例1：从单个页面提取图像
            PdfReader reader = new PdfReader(pdfPath);
            PdfDocument pdfDoc = new PdfDocument(reader);
            
            PdfUtil pdfUtil = new PdfUtil();
            byte[] imageBytes = pdfUtil.readImage(pdfDoc, 1, 257, 710, 320, 770);

            if (imageBytes != null && imageBytes.length > 0) {
                System.out.println("提取成功，图像大小：" + imageBytes.length + " bytes");

                // 可选：保存到文件验证
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
                ImageIO.write(img, "PNG", new java.io.File("c:\\extracted_qr.png"));
                System.out.println("图像已保存到 extracted_qr.png");
            } else {
                System.out.println("未找到指定区域的图像");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
