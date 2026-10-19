package com.example.login.controller;

import com.example.login.model.Estudiante;
import com.example.login.repository.EstudianteRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Controller
public class ReportePdfController {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @GetMapping("/reportes/pdf/matriculas")
    public void descargarPdfMatriculas(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"matriculas.pdf\"");

        List<Estudiante> estudiantes = estudianteRepository.findAll();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Título
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Paragraph titulo = new Paragraph("Reporte de Matrículas - INO", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Tabla
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 4f, 3f, 2f, 2f});

        // Encabezados
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        String[] headers = {"NIE", "Nombre Completo", "Bachillerato", "Grado", "Sección"};
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
            cell.setBackgroundColor(new Color(74, 108, 247));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // Datos
        Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 10);
        for (Estudiante e : estudiantes) {
            table.addCell(new PdfPCell(new Phrase(e.getNie() != null ? e.getNie() : "N/A", fontData)));
            table.addCell(new PdfPCell(new Phrase(e.getNombreCompleto() != null ? e.getNombreCompleto() : "N/A", fontData)));
            table.addCell(new PdfPCell(new Phrase(e.getBachillerato() != null ? e.getBachillerato().getNombre() : "Sin asignar", fontData)));
            table.addCell(new PdfPCell(new Phrase(e.getGrado() != null ? e.getGrado().getNombre() : "Sin asignar", fontData)));
            table.addCell(new PdfPCell(new Phrase(e.getSeccion() != null ? e.getSeccion().getNombre() : "Sin asignar", fontData)));
        }

        document.add(table);
        document.close();
    }
}