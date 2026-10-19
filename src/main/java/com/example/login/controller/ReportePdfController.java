package com.example.login.controller;

import com.example.login.model.Estudiante;
import com.example.login.model.Pago;
import com.example.login.repository.EstudianteRepository;
import com.example.login.repository.PagoRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class ReportePdfController {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PagoRepository pagoRepository;

    private static final String[] MESES = {
        "", "Enero","Febrero","Marzo","Abril","Mayo","Junio",
        "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
    };

    private String nombre(Estudiante e) {
        StringBuilder sb = new StringBuilder();
        if (e.getPrimerNombre() != null) sb.append(e.getPrimerNombre()).append(" ");
        if (e.getSegundoNombre() != null) sb.append(e.getSegundoNombre()).append(" ");
        if (e.getPrimerApellido() != null) sb.append(e.getPrimerApellido()).append(" ");
        if (e.getSegundoApellido() != null) sb.append(e.getSegundoApellido());
        return sb.toString().trim();
    }

    // =============================================
    // PDF - MATRÍCULAS
    // =============================================
    @GetMapping("/reportes/pdf/matriculas")
    public void descargarPdfMatriculas(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"matriculas.pdf\"");

        List<Estudiante> estudiantes = estudianteRepository.findAll();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Paragraph titulo = new Paragraph("Reporte de Matrículas - INO", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 4f, 3f, 2f, 2f});

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        String[] headers = {"NIE", "Nombre Completo", "Bachillerato", "Grado", "Sección"};
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
            cell.setBackgroundColor(new Color(74, 108, 247));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 10);
        for (Estudiante e : estudiantes) {
            table.addCell(new PdfPCell(new Phrase(e.getNie() != null ? e.getNie() : "N/A", fontData)));
            table.addCell(new PdfPCell(new Phrase(nombre(e), fontData)));
            table.addCell(new PdfPCell(new Phrase(e.getBachillerato() != null ? e.getBachillerato().getNombre() : "Sin asignar", fontData)));
            table.addCell(new PdfPCell(new Phrase(e.getGrado() != null ? e.getGrado().getNombre() : "Sin asignar", fontData)));
            table.addCell(new PdfPCell(new Phrase(e.getSeccion() != null ? e.getSeccion().getNombre() : "Sin asignar", fontData)));
        }

        document.add(table);
        document.close();
    }

    // =============================================
    // PDF - PAGOS POR MES
    // =============================================
    @GetMapping("/reportes/pdf/pagos")
    public void descargarPdfPagos(
            @RequestParam int mes,
            @RequestParam int anio,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"pagos_" + MESES[mes] + "_" + anio + ".pdf\"");

        List<Estudiante> estudiantes = estudianteRepository.findAll();

        Document document = new Document(PageSize.A4.rotate()); // Horizontal
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Paragraph titulo = new Paragraph("Reporte de Pagos - " + MESES[mes] + " " + anio, fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 4f, 2f, 2f, 2f});

        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        String[] headers = {"ID", "NIE", "Nombre", "Estado", "Monto", "Fecha Pago"};
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
            cell.setBackgroundColor(new Color(74, 108, 247));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 10);
        double totalRecaudado = 0;

        for (Estudiante e : estudiantes) {
            Optional<Pago> pago = pagoRepository.findByEstudiante_IdEstudianteAndMesAndAnio(e.getIdEstudiante(), mes, anio);

            table.addCell(new PdfPCell(new Phrase(String.valueOf(e.getIdEstudiante()), fontData)));
            table.addCell(new PdfPCell(new Phrase(e.getNie() != null ? e.getNie() : "—", fontData)));
            table.addCell(new PdfPCell(new Phrase(nombre(e), fontData)));

            if (pago.isPresent()) {
                Pago p = pago.get();
                
                PdfPCell estadoCell = new PdfPCell(new Phrase(p.getEstado(), fontData));
                if ("pagado".equals(p.getEstado())) {
                    estadoCell.setBackgroundColor(new Color(200, 240, 200)); // Verde
                } else {
                    estadoCell.setBackgroundColor(new Color(255, 200, 200)); // Rojo
                }
                table.addCell(estadoCell);

                table.addCell(new PdfPCell(new Phrase("$" + p.getMonto(), fontData)));
                table.addCell(new PdfPCell(new Phrase(
                    p.getFechaPago() != null ? p.getFechaPago().toString() : "—", fontData)));

                if ("pagado".equals(p.getEstado())) {
                    totalRecaudado += p.getMonto().doubleValue();
                }
            } else {
                table.addCell(new PdfPCell(new Phrase("Sin registro", fontData)));
                table.addCell(new PdfPCell(new Phrase("—", fontData)));
                table.addCell(new PdfPCell(new Phrase("—", fontData)));
            }
        }

        document.add(table);

        document.add(new Paragraph(" "));
        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
        Paragraph total = new Paragraph("TOTAL RECAUDADO: $" + String.format("%.2f", totalRecaudado), fontTotal);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.close();
    }
}