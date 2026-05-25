package com.i.common.attendance.ui.home.ledgerreport.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.fonts.Font
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.i.common.attendance.BuildConfig
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// Data passed in from the Fragment
// ─────────────────────────────────────────────────────────────────────────────

data class LedgerPdfParams(
    val customerName: String,
    val fromDate: String,
    val toDate: String,
    val closingBalance: Double,       // negative = Dr side
    val closingBalanceLabel: String,  // e.g. "₹ 5,000 Dr"
    val creditLabel: String,          // e.g. "₹ 12,000 Cr" – blank if none
    val debitLabel: String,           // e.g. "₹ 5,000 Dr"  – blank if none
    val transactions: List<LedgerRowItem>
)

data class LedgerRowItem(
    val transactionDt: String,
    val transactionNo: String,
    val narration: String,
    val drAmt: String,
    val crAmt: String
)

sealed class PdfResult {
    data class Success(val file: File, val savedPath: String) : PdfResult()
    data class Failure(val error: String, val cause: Throwable? = null) : PdfResult()
}

// ─────────────────────────────────────────────────────────────────────────────
// Generator  –  direct Java → Kotlin translation, nothing changed in logic
// ─────────────────────────────────────────────────────────────────────────────

object LedgerPdfGenerator {

    // Exact same fonts as Java: new Font(Font.FontFamily.TIMES_ROMAN, ...)
    // In Kotlin iText the enum is accessed the same way.
    private val titleFont   = com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18f, com.itextpdf.text.Font.BOLD)
    private val subFont     = com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14f, com.itextpdf.text.Font.BOLD)
    private val captionNormal = com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 11f, com.itextpdf.text.Font.NORMAL)

    // ── Public entry point ────────────────────────────────────────────────────

    fun generate(
        context: Context,
        params: LedgerPdfParams,
        logoAssetName: String = "ic_delta_logo.png"
    ): PdfResult {
        return try {
            val outputFile = createOutputFile(context, params.customerName)
                ?: return PdfResult.Failure("Could not create output directory")

            val output = FileOutputStream(outputFile)
            val document = Document(PageSize.A4, 15f, 15f, 15f, 15f)
            val writer = PdfWriter.getInstance(document, output)
            val event = BlackBorder()
            writer.setPageEvent(event)

            document.open()
            addMetaData(document)
            addTitlePage(document, context, params, logoAssetName)
            document.close()
            output.close()

            PdfResult.Success(
                file = outputFile,
                savedPath = outputFile.parent ?: outputFile.absolutePath
            )
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
            PdfResult.Failure("PDF generation failed: ${e.message}", e)
        }
    }

    fun buildShareIntent(context: Context, pdfFile: File): Intent {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider",pdfFile)
        } else {
            Uri.fromFile(pdfFile)
        }
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    // ── File creation (same path as Java) ─────────────────────────────────────

    private fun createOutputFile(context: Context, customerName: String): File? {
        val fileCreate = context.getExternalFilesDir(null).toString() + "/MascotiAttendance/CreatePDF"
        val docsFolder = File(fileCreate)
        if (!docsFolder.exists()) docsFolder.mkdirs()
        val safeName = customerName.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        return File(docsFolder.absolutePath, "$safeName.pdf")
    }

    // ── Metadata (identical to Java) ──────────────────────────────────────────

    private fun addMetaData(document: Document) {
        try {
            document.addTitle("PDF")
            document.addSubject("Ledger Report")
            document.addKeywords("Java, PDF, iText")
            document.addAuthor("Delta Infosoft Pvt Ltd")
            document.addCreator("Delta Infosoft Pvt Ltd")
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // ── Content (line-by-line translation of Java addTitlePage) ───────────────

    private fun addTitlePage(
        document: Document,
        context: Context,
        params: LedgerPdfParams,
        logoAssetName: String
    ) {
        try {
            // Header
            val p0 = Paragraph("Ledger Report", titleFont)
            p0.alignment = Paragraph.ALIGN_CENTER
            p0.spacingAfter = 10f
            document.add(p0)

            val line0 = LineSeparator(1f, 100f, BaseColor(0, 0, 0, 68), Element.ALIGN_CENTER, -2f)
            document.add(line0)

            // Company name
            val p1 = Paragraph(params.customerName, subFont)
            p1.alignment = Paragraph.ALIGN_CENTER
            p1.spacingBefore = 5f
            p1.spacingAfter = 5f
            document.add(p1)

            // Customer name
            val p5 = Paragraph(params.customerName, captionNormal)
            p5.alignment = Paragraph.ALIGN_CENTER
            p5.spacingAfter = 5f
            document.add(p5)

            // Date range
            val p2 = Paragraph("${params.fromDate} to ${params.toDate}", captionNormal)
            p2.alignment = Paragraph.ALIGN_CENTER
            p2.spacingAfter = 5f
            document.add(p2)

            // Closing / Credit / Debit – one row with glue (identical to Java)
            val glue = Chunk(VerticalPositionMark())
            val p = Paragraph()

            if (params.closingBalance < 0) {
                p.add(Chunk(
                    "Closing Balance : ${params.closingBalanceLabel}",
                    com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12f, com.itextpdf.text.Font.BOLD, BaseColor.RED)
                ))
            } else {
                p.add(Chunk(
                    "Closing Balance : ${params.closingBalanceLabel}",
                    com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12f, com.itextpdf.text.Font.BOLD, BaseColor(0, 100, 0))
                ))
            }
            p.add(glue)

            if (params.creditLabel.trim().isEmpty()) {
                p.add(Chunk(
                    "Credit : ₹ 0 Cr",
                    com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12f, com.itextpdf.text.Font.BOLD, BaseColor(0, 100, 0))
                ))
            } else {
                p.add(Chunk(
                    "Credit : ${params.creditLabel.trim()}",
                    com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12f, com.itextpdf.text.Font.BOLD, BaseColor(0, 100, 0))
                ))
            }
            p.add(glue)

            if (params.debitLabel.trim().isEmpty()) {
                p.add(Chunk(
                    "Debit : ₹ 0 Dr",
                    com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12f, com.itextpdf.text.Font.BOLD, BaseColor.RED)
                ))
            } else {
                p.add(Chunk(
                    "Debit : ${params.debitLabel.trim()}",
                    com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12f, com.itextpdf.text.Font.BOLD, BaseColor.RED)
                ))
            }
            p.spacingAfter = 5f
            document.add(p)

            document.add(Chunk.NEWLINE)
            val line2 = LineSeparator(1f, 100f, BaseColor(0, 0, 0, 68), Element.ALIGN_CENTER, -2f)
            document.add(line2)
            document.add(Chunk.NEWLINE)

            // Table (identical columns to Java)
            val table = PdfPTable(5)
            table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
            table.defaultCell.verticalAlignment = Element.ALIGN_MIDDLE
            table.defaultCell.fixedHeight = 30f
            table.setTotalWidth(PageSize.A4.width)
            table.widthPercentage = 100f

            table.addCell("Date")
            table.addCell("TransactionNo")
            table.addCell("Narration1")
            table.addCell("Credit")
            table.addCell("Debit")

            table.headerRows = 0

            val cells = table.getRow(0).cells
            for (cell in cells) {
                cell.backgroundColor = BaseColor.LIGHT_GRAY
            }

            // Data rows (same formatting logic as Java)
            for (item in params.transactions) {
                var credit = ""
                var debit = ""

                if (item.drAmt.isNotEmpty()) {
                    if (!item.drAmt.startsWith("-")) {
                        val out = item.drAmt.replace("-", "").toDouble()
                        val integerValue = Math.round(out).toInt()
                        @SuppressLint("DefaultLocale")
                        val locale = Locale("en", "IN")
                        val decimalFormat = DecimalFormat.getCurrencyInstance(locale) as DecimalFormat
                        val totalD = decimalFormat.format(integerValue).replace(Regex("[,.]00$"), "")
                        debit = "$totalD Dr"
                    }
                }

                if (item.crAmt.isNotEmpty()) {
                    val out = item.crAmt.toDouble()
                    val integerValue = Math.round(out).toInt()
                    @SuppressLint("DefaultLocale")
                    val locale = Locale("en", "IN")
                    val decimalFormat = DecimalFormat.getCurrencyInstance(locale) as DecimalFormat
                    val totalD = decimalFormat.format(integerValue).replace(Regex("[,.]00$"), "")
                    credit = "$totalD Cr"
                }

                table.addCell(item.transactionDt)
                table.addCell(item.transactionNo.trim())
                table.addCell(item.narration)

                val creditCell = PdfPCell(Phrase(credit))
                creditCell.horizontalAlignment = Element.ALIGN_RIGHT
                creditCell.verticalAlignment = Element.ALIGN_MIDDLE
                table.addCell(creditCell)

                val debitCell = PdfPCell(Phrase(debit))
                debitCell.horizontalAlignment = Element.ALIGN_RIGHT
                debitCell.verticalAlignment = Element.ALIGN_MIDDLE
                table.addCell(debitCell)
            }

            document.add(table)
            document.add(Chunk.NEWLINE)

            // Powered By
            val p9 = Paragraph(
                "Powered By :- ",
                com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18f, com.itextpdf.text.Font.BOLD, BaseColor.BLACK)
            )
            p9.alignment = Paragraph.ALIGN_CENTER
            p9.spacingAfter = 5f
            document.add(p9)

            // Logo
            val ims = context.assets.open(logoAssetName)
            val bmp = BitmapFactory.decodeStream(ims)
            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = Image.getInstance(stream.toByteArray())
            image.alignment = Element.ALIGN_CENTER
            val map = ((document.pageSize.width - document.leftMargin() - document.rightMargin() - 0) / image.width) * 25f
            image.scalePercent(map)
            document.add(image)

        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // ── Black border on every page (identical to Java inner class) ────────────

    private class BlackBorder : PdfPageEventHelper() {
        override fun onEndPage(writer: PdfWriter, document: Document) {
            val canvas = writer.directContent
            val rect = document.pageSize
            rect.setBorder(Rectangle.BOX)
            rect.borderWidth = 2f
            rect.borderColor = BaseColor.BLACK
            rect.isUseVariableBorders = true
            canvas.rectangle(rect)
        }
    }
}