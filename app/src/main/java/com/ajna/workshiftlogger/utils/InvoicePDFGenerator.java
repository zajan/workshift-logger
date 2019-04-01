package com.ajna.workshiftlogger.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.model.Shift;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used to generate PDF Invoice according to pre-defined pattern.
 */
public class InvoicePDFGenerator {
    private static final String TAG = "InvoicePDFGenerator";

    private String folderName;
    private String fileName, invoiceNr, date, projectName, extra1, extra2;
    private String personName, personAddress1, personAddress2, phoneNr, email;
    private String clientName, clientOfficialName, clientAddress, clientBasePayment;
    private DateFormat dateFormat, timeFormat;

    public void setShifts(List<Shift> shifts, DateFormat dateFormat, DateFormat timeFormat) {
        this.shifts = new ArrayList<>(shifts);
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
    }

    private List<Shift> shifts;

    public InvoicePDFGenerator(String fileName, String folderName) {
        this.fileName = fileName;
        this.folderName = folderName;
    }

    public void setInvoiceNr(String invoiceNr) {
        this.invoiceNr = invoiceNr;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setPersonAddress1(String personAddress1) {
        this.personAddress1 = personAddress1;
    }

    public void setPersonAddress2(String personAddress2) {
        this.personAddress2 = personAddress2;
    }

    public void setPhoneNr(String phoneNr) {
        this.phoneNr = phoneNr;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setClientOfficialName(String clientOfficialName) {
        this.clientOfficialName = clientOfficialName;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void setClientBasePayment(String clientBasePayment) {
        this.clientBasePayment = clientBasePayment;
    }


    public File generateInvoice() {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }

        File pdfFile = new File(docsFolder.getAbsolutePath(), fileName + ".pdf");

        try {
            OutputStream output = new FileOutputStream(pdfFile);
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();

            // author data paragraph
            if (personName != null) {
                Paragraph p = new Paragraph(personName);
                p.setAlignment(Paragraph.ALIGN_RIGHT);
                document.add(p);
            }
            if (personAddress1 != null) {
                Paragraph p = new Paragraph(personAddress1);
                p.setAlignment(Paragraph.ALIGN_RIGHT);
                document.add(p);
            }
            if (personAddress2 != null) {
                Paragraph p = new Paragraph(personAddress2);
                p.setAlignment(Paragraph.ALIGN_RIGHT);
                document.add(p);
            }
            if (phoneNr != null) {
                Paragraph p = new Paragraph(phoneNr);
                p.setAlignment(Paragraph.ALIGN_RIGHT);
                document.add(p);
            }
            if (email != null) {
                Paragraph p = new Paragraph(email);
                p.setAlignment(Paragraph.ALIGN_RIGHT);
                document.add(p);
            }

            // client data paragraph
            if (clientOfficialName != null) {
                Paragraph p = new Paragraph(clientOfficialName);
                p.setAlignment(Paragraph.ALIGN_LEFT);
                p.setSpacingBefore(48f);
                document.add(p);
            }
            if (clientAddress != null) {
                Paragraph p = new Paragraph(clientAddress);
                p.setAlignment(Paragraph.ALIGN_LEFT);
                document.add(p);
            }

            // invoice data paragraph
            if (invoiceNr != null) {
                Paragraph p = new Paragraph("Invoice nr " + invoiceNr);
                p.setAlignment(Paragraph.ALIGN_LEFT);
                p.setSpacingBefore(48f);
                document.add(p);
            }

            if (date != null) {
                Paragraph p = new Paragraph(date);
                p.setAlignment(Paragraph.ALIGN_LEFT);
                document.add(p);
            }

            // project data paragraph
            if (projectName != null) {
                Paragraph p = new Paragraph(projectName);
                p.setAlignment(Paragraph.ALIGN_LEFT);
                document.add(p);
            }

            if (extra1 != null) {
                Paragraph p = new Paragraph(extra1);
                p.setAlignment(Paragraph.ALIGN_LEFT);
                document.add(p);
            }

            // shifts table
            if (shifts != null && shifts.size() > 0) {

                int totalCharge = 0;

                PdfPTable table = new PdfPTable(5);
                table.setSpacingBefore(48f);
                table.setWidthPercentage(100);
                table.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.setWidths(new int[]{3, 1, 2, 1, 2});
                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                table.addCell("Time");
                table.addCell("Pause");
                table.addCell("Base payment");
                table.addCell("Factor");
                table.addCell("Charge");

                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("");

                for(Shift shift : shifts){
                    table.addCell(dateFormat.format(shift.getStartTime()) + "  " + timeFormat.format(shift.getStartTime())
                            + "-" + timeFormat.format(shift.getEndTime()));
                    table.addCell(String.valueOf(shift.getPause()) + " min");
                    table.addCell(String.valueOf(shift.getBasePayment()) + " EUR");
                    double factor = (double) shift.getActualFactorInPercent().getFactorInPercent() / 100;
                    table.addCell(String.valueOf(factor));
                    table.addCell(String.valueOf(shift.calculatePayment()) + " EUR");

                    totalCharge += shift.calculatePayment();
                }

                document.add(table);

                Paragraph p = new Paragraph("Total: " + String.valueOf(totalCharge) + " EUR");
                p.setAlignment(Paragraph.ALIGN_RIGHT);
                p.setSpacingBefore(24f);
                document.add(p);
            }


            document.close();

        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
            return null;
        }
        return pdfFile;
    }

    public void previewPDF(File file, Context context) {
        Log.d(TAG, "previewPDF: starts");
        Uri uri = FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), file);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "previewPDF: Build.VERSION.SDK_INT >= Build.VERSION_CODES.N");
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            Log.d(TAG, "previewPDF: Build.VERSION.SDK_INT < Build.VERSION_CODES.N");
            intent.setDataAndType(uri, "application/pdf");
            intent = Intent.createChooser(intent, "Open File");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
