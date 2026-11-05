package com.project.healthsystem.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportField;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportRowDTO;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportStatisticsDTO;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

@Service
public class PatientReportPdfService {
    private static final int ROWS_PER_FLUSH = 100;
    private static final float MARGIN = 42;
    private static final float BANNER_HEIGHT = 82;
    private static final Color BLUE = new Color(5, 94, 153);
    private static final Color PALE = new Color(242, 245, 250);
    private static final Color BORDER = new Color(215, 224, 237);
    private static final Color STRIPE = new Color(249, 250, 252);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE);
    private static final Font INFO_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final Font SECTION_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);

    public void write(OutputStream outputStream, List<PatientReportField> columns,
                      PatientReportStatisticsDTO statistics,
                      IntFunction<List<PatientReportRowDTO>> batchLoader) {
        Document document = new Document(PageSize.A4.rotate(), MARGIN, MARGIN, BANNER_HEIGHT + 20, 38);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        writer.setCloseStream(false);
        writer.setPageEvent(new ReportPageEvent());
        document.addTitle("Relatório Geral");
        document.addCreator("Vitalya");
        document.open();
        // OpenPDF applies new margins starting on the next page.
        document.setMargins(MARGIN, MARGIN, 20, 38);
        try {
            document.add(new Paragraph("Gerado em: " + LocalDateTime.now().format(DATE_FORMATTER), INFO_FONT));
            Paragraph heading = new Paragraph("Estatísticas do Relatório", SECTION_FONT);
            heading.setSpacingBefore(8);
            heading.setSpacingAfter(12);
            document.add(heading);
            document.add(statisticsRow(
                statisticsCell("Totais", List.of(
                    "Total de atendimentos: " + statistics.totalAppointments(),
                    "Pacientes únicos: " + statistics.uniquePatients(),
                    "Profissionais envolvidos: " + statistics.involvedProfessionals())),
                statisticsCell("Por Status", entries(statistics.byStatus()))));
            document.add(statisticsRow(
                statisticsCell("Por Prioridade", entries(statistics.byPriority())),
                statisticsCell("Por Tipo de Serviço", entries(statistics.byServiceType()))));

            PdfPTable table = createTable(columns);
            int offset = 0;
            int pendingRows = 0;
            int rowNumber = 0;
            List<PatientReportRowDTO> batch = batchLoader.apply(offset);
            while (!batch.isEmpty()) {
                for (PatientReportRowDTO row : batch) {
                    Color background = rowNumber++ % 2 == 0 ? Color.WHITE : STRIPE;
                    for (PatientReportField column : columns) {
                        table.addCell(dataCell(column.extractFrom(row), background, columns.size()));
                    }
                    pendingRows++;
                    if (pendingRows == ROWS_PER_FLUSH) {
                        document.add(table);
                        pendingRows = 0;
                    }
                }
                offset += batch.size();
                batch = batchLoader.apply(offset);
            }
            table.setComplete(true);
            document.add(table);
            if (statistics.totalAppointments() == 0) {
                Paragraph empty = new Paragraph("Nenhum registro encontrado para os filtros informados.", INFO_FONT);
                empty.setSpacingBefore(12);
                document.add(empty);
            }
        } finally {
            document.close();
        }
    }

    private List<String> entries(Map<String, Long> values) {
        return values.isEmpty() ? List.of("Nenhum registro") : values.entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue()).toList();
    }

    private PdfPCell statisticsCell(String title, List<String> lines) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(PALE);
        cell.setBorderColor(BORDER);
        cell.setBorderWidth(0.5f);
        cell.setPadding(12);
        Paragraph paragraph = new Paragraph(title + ":", HEADER_FONT);
        paragraph.setLeading(12);
        cell.addElement(paragraph);
        for (String line : lines) {
            Paragraph item = new Paragraph("• " + line, CELL_FONT);
            item.setLeading(12);
            cell.addElement(item);
        }
        return cell;
    }

    private PdfPTable statisticsRow(PdfPCell left, PdfPCell right) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setKeepTogether(true);
        table.setSpacingAfter(12);
        left.setBorder(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
        right.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
        table.addCell(left);
        table.addCell(right);
        return table;
    }

    private PdfPTable createTable(List<PatientReportField> columns) {
        float[] widths = new float[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            widths[i] = switch (columns.get(i)) {
                case SCHEDULED_AT, CREATED_AT, SERVICE_TYPE -> 2.2f;
                case PRIORIT -> 1.9f;
                case STATUS -> 2f;
                default -> columns.get(i).getWidth();
            };
        }
        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage(100);
        table.setComplete(false);
        table.setSplitLate(false);
        for (PatientReportField column : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(column.getLabel(), columns.size() > 9 ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7) : HEADER_FONT));
            styleCell(cell, PALE);
            table.addCell(cell);
        }
        table.setHeaderRows(1);
        return table;
    }

    private PdfPCell dataCell(String value, Color background, int columnCount) {
        PdfPCell cell = new PdfPCell(new Phrase(value == null || value.isBlank() ? "-" : value, columnCount > 9 ? FontFactory.getFont(FontFactory.HELVETICA, 7) : CELL_FONT));
        styleCell(cell, background);
        return cell;
    }

    private void styleCell(PdfPCell cell, Color background) {
        cell.setBackgroundColor(background);
        cell.setBorderColor(BORDER);
        cell.setBorderWidth(0.5f);
        cell.setPadding(4);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setMinimumHeight(25);
    }

    private static class ReportPageEvent extends PdfPageEventHelper {
        private PdfTemplate totalPages;
        private BaseFont footerFont;

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalPages = writer.getDirectContent().createTemplate(40, 14);
            footerFont = INFO_FONT.getCalculatedBaseFont(false);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            float width = document.getPageSize().getWidth();
            float height = document.getPageSize().getHeight();
            canvas.saveState();
            if (writer.getPageNumber() == 1) {
                canvas.setColorFill(BLUE);
                canvas.rectangle(0, height - BANNER_HEIGHT, width, BANNER_HEIGHT);
                canvas.fill();
                drawHeaderContent(canvas, height);
            }
            canvas.setColorFill(Color.DARK_GRAY);
            String prefix = "Página " + writer.getPageNumber() + " de ";
            float x = (width - footerFont.getWidthPoint(prefix, 8) - 16) / 2;
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase(prefix, FontFactory.getFont(FontFactory.HELVETICA, 8)), x, 20, 0);
            canvas.addTemplate(totalPages, x + footerFont.getWidthPoint(prefix, 8), 20);
            canvas.restoreState();
        }

        // Branding is isolated here so the official logo can be added later.
        private void drawHeaderContent(PdfContentByte canvas, float pageHeight) {
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase("Relatório Geral", TITLE_FONT), MARGIN, pageHeight - 49, 0);
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            totalPages.beginText();
            totalPages.setFontAndSize(footerFont, 8);
            totalPages.setTextMatrix(0, 0);
            totalPages.showText(String.valueOf(writer.getPageNumber() - 1));
            totalPages.endText();
        }
    }
}