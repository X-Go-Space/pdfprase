import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.springframework.util.FileCopyUtils;

public class mergePDF2 {
    public static void main(String[] args) {
        File file1 = new File("C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\resource\\2.3.pdf");
        File file2 = new File("C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\resource\\test.pdf");
        String outputFile = "2.4.pdf";
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
        mergePdfFiles(list,outputFile);
    }
    private static byte[] mergePdfFiles(List<InputStream> inputStreams, String outFile) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document();// 创建一个新的PDF
        byte[] pdfs = new byte[0];
        try {
            PdfCopy copy = new PdfCopy(document, bos);
            document.open();
            for (InputStream is : inputStreams) {// 取出单个PDF的数据
                PdfReader reader = new PdfReader(stream2Byte(is));
                int pageTotal = reader.getNumberOfPages();
                for (int pageNo = 1; pageNo <= pageTotal; pageNo++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, pageNo);
                    copy.addPage(page);
                }
                reader.close();
            }
            document.close();
            pdfs = bos.toByteArray();
            bos.close();
            copy.close();
            FileCopyUtils.copy(pdfs, new File(outFile));
        } catch (DocumentException | IOException e) {
            System.out.println("合并出现错误");
        }
        return pdfs;
    }

    private static byte[] stream2Byte(InputStream inputStream) {
        byte[] buffer = new byte[0];
        try (InputStream fis = inputStream; ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } catch (IOException e) {
        }
        return buffer;
    }

    public static boolean save2File(String fname, byte[] msg){
        OutputStream fos = null;
        try{
            File file = new File(fname);
            File parent = file.getParentFile();
            boolean bool;
            if ((!parent.exists()) &&
                    (!parent.mkdirs())) {
                return false;
            }
            fos = new FileOutputStream(file);
            fos.write(msg);
            fos.flush();
            return true;
        }catch (FileNotFoundException e){
            return false;
        }catch (IOException e){
            File parent;
            return false;
        }
        finally{
            if (fos != null) {
                try{
                    fos.close();
                }catch (IOException e) {}
            }
        }
    }


}
