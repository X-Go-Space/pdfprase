import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class mergePdf3 {
    public static void main(String[] args) {
        try {
            mergePDF3();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void mergePDF3() throws FileNotFoundException {
        File file1 = new File("C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\resource\\2.1.pdf");
        File file2 = new File("C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\resource\\test.pdf");
        String outputFile = "2.4.pdf";
        FileInputStream stream1 = new FileInputStream(file1);
        FileInputStream stream2 = new FileInputStream(file2);
        //加载PDF示例文档
        InputStream[] streams = new FileInputStream[]{stream1, stream2};
        //合并PDF文档
        PdfDocumentBase doc = PdfDocument.mergeFiles(streams);
        //保存文档
        doc.save(outputFile);
        doc.close();
    }

}
