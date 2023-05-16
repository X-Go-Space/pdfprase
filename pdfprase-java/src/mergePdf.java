import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentBase;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class mergePdf {
    public static void main(String[] args) {
        File file1 = new File(".\\resource\\test.pdf");
        File file2 = new File(".\\resource\\test2.pdf");
        String outputFile = "2.5.pdf";
        FileInputStream stream1 = null;
        try {
            stream1 = new FileInputStream(file1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileInputStream stream2 = null;
        try {
            stream2 = new FileInputStream(file2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //加载PDF示例文档
        List<InputStream> list=new ArrayList<>();
        list.add(stream1);
        list.add(stream2);
        mergePDFbox(list,outputFile);
    }
    private static void mergePDFbox(List<InputStream> list, String outputFile){
        // 合并PDF文档法二
        File outFile = new File(outputFile);
        PDFMergerUtility merger = new PDFMergerUtility();
        list.forEach(merger::addSource);
        merger.setDestinationFileName(outFile.getName());
        try {
            // 合并PDF
            merger.mergeDocuments(null);
        } catch (IOException e) {
            System.out.println("合并错误");
        }
    }
}