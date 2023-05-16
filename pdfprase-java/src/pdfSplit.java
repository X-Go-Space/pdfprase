import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.PdfMargins;
import com.spire.xls.core.converter.spreadsheet.shapes.msodrawing.BstoreContainer;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class pdfSplit {
    public static void main(String[] args) { //加载需要拆分的PDF文档
        //每个初始文件夹先创建好
        String[] fileName=getFileName("./resource");
        List<String> outputFilePath=new ArrayList<>();
        for(int i=0;i<fileName.length;i++){
            List<String> words = Arrays.asList(fileName[i].split("\\."));
            String dirName = "."+File.separator+"output"+File.separator+String.join("", words);
            outputFilePath.add(dirName);
        }
        //获取每个文件的绝对路径
        List<String> fileList=getFilePath("./resource");
        for(int i=0;i<fileList.size();i++){
            //System.out.println(fileList.get(i)+"    "+outputFilePath.get(i)+File.separator+"pdf"+"    "+fileName[i].substring(0,fileName[i].length()-4));
            splitPdf(fileList.get(i),outputFilePath.get(i)+File.separator+"pdf",fileName[i].substring(0,fileName[i].length()-4));
            saveEndFile(outputFilePath.get(i)+File.separator+"pdfforimag"+File.separator+"end.txt");
            saveEndFile(outputFilePath.get(i)+File.separator+"pdffortext"+File.separator+"end.txt");
        }
    }
    public static void saveEndFile(String filePath){
        String content = "";
        FileWriter fw = null;
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                file.createNewFile();
            }
            fw = new FileWriter(filePath);
            fw.write(content);
            fw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fw.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public static String splitPdf(String foldPath,String outPutDirPath,String fileName){
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile(foldPath);
        System.out.println("load "+foldPath+" finish");
        for (int pageIndex = 0; pageIndex < pdf.getPages().getCount(); pageIndex = pageIndex + 10) {
            PdfDocument newpdf = new PdfDocument();
            PdfPageBase page2; //将原PDF文档的第1、2页拆分，并保存到newpdf1
            if (pageIndex + 10 >= pdf.getPages().getCount()) {
                for (int i = pageIndex; i < pdf.getPages().getCount(); i++) {
                    page2 = newpdf.getPages().add(pdf.getPages().get(i).getSize(), new PdfMargins(0));
                    pdf.getPages().get(i).createTemplate().draw(page2, new Point2D.Float(0, 0));
                }
            } else {
                for (int i = pageIndex; i < pageIndex + 10; i++) {
                    page2 = newpdf.getPages().add(pdf.getPages().get(i).getSize(), new PdfMargins(0));
                    pdf.getPages().get(i).createTemplate().draw(page2, new Point2D.Float(0, 0));
                }
            }
            String fileNamePage=String.valueOf(pageIndex/10);
            while(fileNamePage.length()<5){
                fileNamePage="0"+fileNamePage;
            }
            newpdf.saveToFile(outPutDirPath+"fortext"+File.separator+fileName+fileNamePage+".pdf");
            newpdf.saveToFile(outPutDirPath+"forimag"+File.separator+fileName+fileNamePage+".pdf");
            newpdf.close();
        }
        System.out.println("success "+foldPath+" split");
        return "success split";
    }
    public static List<String> getFilePath(String folderPath) {
        File folder = new File(folderPath);
        List<String> filePathList = new ArrayList<>();
        String rootPath;
        if (folder.exists()) {
            String[] fileNameList = folder.list();
            if (null != fileNameList && fileNameList.length > 0) {
                if (folder.getPath().endsWith(File.separator)) {
                    rootPath = folder.getPath();
                } else {
                    rootPath = folder.getPath() + File.separator;
                }
                for (String fileName : fileNameList) {
                    filePathList.add(rootPath + fileName);
                }
            }
        }
        return filePathList;
    }
    public static String[] getFileName(String folderPath){
        File folder=new File(folderPath);
        return folder.list();
    }
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("create dir " + destDirName + " failed");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            System.out.println("create dir " + destDirName + " success");
            return true;
        } else {
            System.out.println("create dir " + destDirName + " failed");
            return false;
        }
    }
}
