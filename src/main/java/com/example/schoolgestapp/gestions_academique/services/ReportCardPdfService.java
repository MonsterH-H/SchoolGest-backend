package com.example.schoolgestapp.gestions_academique.services;

import com.example.schoolgestapp.entity.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * SERVICE : Génération de documents PDF pour les bulletins de notes.
 */
@Service
public class ReportCardPdfService {

    public byte[] generateReportCardPdf(ReportCard reportCard) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // Styles de police
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        // --- EN-TETE ---
        Paragraph title = new Paragraph("BULLETIN DE NOTES SEMESTRIEL", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // --- INFOS ETUDIANT ---
        PdfPTable studentInfoTable = new PdfPTable(2);
        studentInfoTable.setWidthPercentage(100);
        
        studentInfoTable.addCell(createCell("etudiant : " + reportCard.getStudent().getUser().getFirstName() + " " + reportCard.getStudent().getUser().getLastName(), fontBold));
        studentInfoTable.addCell(createCell("Code : " + reportCard.getStudent().getStudentCode(), fontBold));
        studentInfoTable.addCell(createCell("Classe : " + (reportCard.getStudent().getClasse() != null ? reportCard.getStudent().getClasse().getName() : "N/A"), fontNormal));
        studentInfoTable.addCell(createCell("Semestre : " + reportCard.getSemester().getName(), fontNormal));
        studentInfoTable.addCell(createCell("Année Académique : " + reportCard.getAcademicYear(), fontNormal));
        studentInfoTable.addCell(createCell("Rang : " + (reportCard.getRank() != null ? reportCard.getRank() : "En cours..."), fontBold));

        document.add(studentInfoTable);
        document.add(new Paragraph("\n"));

        // --- TABLEAU RESULTATS ---
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{40f, 20f, 20f, 20f});

        addTableHeader(table, fontBold);

        for (ModuleResult mResult : reportCard.getModuleResults()) {
            // Ligne Module
            PdfPCell moduleCell = new PdfPCell(new Phrase("Module : " + mResult.getModule().getName(), fontBold));
            moduleCell.setColspan(4);
            moduleCell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
            table.addCell(moduleCell);

            for (SubjectResult sResult : mResult.getSubjectResults()) {
                table.addCell(new Phrase(sResult.getSubject().getName(), fontNormal));
                table.addCell(new Phrase(String.format("%.2f", sResult.getCcAverage()), fontNormal));
                table.addCell(new Phrase(String.format("%.2f", sResult.getExamGrade()), fontNormal));
                table.addCell(new Phrase(String.format("%.2f", sResult.getFinalAverage()), fontBold));
            }

            // Moyenne Module
            PdfPCell avgModuleLabel = new PdfPCell(new Phrase("Moyenne Module (" + mResult.getModule().getCredits() + " Crédits)", fontBold));
            avgModuleLabel.setColspan(3);
            avgModuleLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(avgModuleLabel);
            table.addCell(new Phrase(String.format("%.2f", mResult.getAverage()), fontBold));
        }

        document.add(table);
        document.add(new Paragraph("\n"));

        // --- RESUME FINAL ---
        Paragraph footer = new Paragraph("MOYENNE GÉNÉRALE DU SEMESTRE : " + String.format("%.2f", reportCard.getAverage()) + " / 20", fontTitle);
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    private void addTableHeader(PdfPTable table, Font font) {
        table.addCell(new PdfPCell(new Phrase("Matière", font)));
        table.addCell(new PdfPCell(new Phrase("Moyenne CC", font)));
        table.addCell(new PdfPCell(new Phrase("Note Examen", font)));
        table.addCell(new PdfPCell(new Phrase("Moyenne Finale", font)));
    }

    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }
}
