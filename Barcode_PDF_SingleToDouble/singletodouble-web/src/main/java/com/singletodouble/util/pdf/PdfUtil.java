package com.singletodouble.util.pdf;

import java.io.File;
import java.util.List;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
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
	
	public List<TextChunk> readPage(PdfDocument document, int pageNo) {
		if (document == null) {
			return null;
		}
		
        TextWithLocationStrategy strategy = new TextWithLocationStrategy();
        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        parser.processPageContent(document.getPage(pageNo));
        return strategy.getChunks();
	}

	public void closeDocument(PdfDocument document) {
		try {
			if (document != null) {
				PdfReader reader = document.getReader();
				if (reader != null) {
					reader.close();
					reader = null;
				}
			}
		} catch (Exception e) {
			
		} finally {
			if (document != null) {
				document.close();
				document = null;
			}
		}
	}
	
	public static void main(String[] args) {
		PdfUtil pdfUtil = new PdfUtil();
		PdfDocument document = pdfUtil.openDocument("E:\\project\\Barcode_PDF_SingleToDouble\\singletodouble-web\\test\\TRCW.pdf");
		
		// TRCW
		float startX = 22;
		float startY = 176;
		float endX = 138;
		float endY = 195;
		System.err.println(String.format("TRCW(Text=%s, position=(%s,%s,%s,%s)", pdfUtil.readText(document, 1, startX, startY, endX, endY), startX, startY, endX, endY));
		
		// 背番
		startX = 24;
		startY = 126;
		endX = 140;
		endY = 147;
		System.err.println(String.format("背番(Text=%s, position=(%s,%s,%s,%s)", pdfUtil.readText(document, 1, startX, startY, endX, endY), startX, startY, endX, endY));		

		// TR纳入日
		startX = 74;
		startY = 108;
		endX = 138;
		endY = 118;
		System.err.println(String.format("TR纳入日(Text=%s, position=(%s,%s,%s,%s)", pdfUtil.readText(document, 1, startX, startY, endX, endY), startX, startY, endX, endY));
		
		// SEQ
		startX = 57;
		startY = 90;
		endX = 140;
		endY = 101;
		System.err.println(String.format("SEQ(Text=%s, position=(%s,%s,%s,%s)", pdfUtil.readText(document, 1, startX, startY, endX, endY), startX, startY, endX, endY));		

		// 订货号
		startX = 72;
		startY = 72;
		endX = 138;
		endY = 82;
		System.err.println(String.format("订货号(Text=%s, position=(%s,%s,%s,%s)", pdfUtil.readText(document, 1, startX, startY, endX, endY), startX, startY, endX, endY));
		
		// 订单NO
		startX = 72;
		startY = 54;
		endX = 138;
		endY = 64;
		System.err.println(String.format("订单NO(Text=%s, position=(%s,%s,%s,%s)", pdfUtil.readText(document, 1, startX, startY, endX, endY), startX, startY, endX, endY));
	
		// 总箱数
		startX = 72;
		startY = 40;
		endX = 138;
		endY = 51;
		System.err.println(String.format("总箱数(Text=%s, position=(%s,%s,%s,%s)", pdfUtil.readText(document, 1, startX, startY, endX, endY), startX, startY, endX, endY));

		// 纳入指示数量
		startX = 72;
		startY = 28;
		endX = 138;
		endY = 39;
		System.err.println(String.format("纳入指示数量(Text=%s, position=(%s,%s,%s,%s)", pdfUtil.readText(document, 1, startX, startY, endX, endY), startX, startY, endX, endY));
		
		// 发行日
		startX = 72;
		startY = 8;
		endX = 138;
		endY = 19;
		System.err.println(String.format("发行日(Text=%s, position=(%s,%s,%s,%s)", pdfUtil.readText(document, 1, startX, startY, endX, endY), startX, startY, endX, endY));
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// 数据番号/数据量
		startX = 529;
		startY = 185;
		endX = 580;
		endY = 195;
		System.err.println(String.format("DataNo(Text=%s, position=(%s,%s,%s,%s)", pdfUtil.readText(document, 1, startX, startY, endX, endY), startX, startY, endX, endY));
		
		
		pdfUtil.closeDocument(document);
		
	}
}
