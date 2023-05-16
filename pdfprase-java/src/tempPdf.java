import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.Path;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.spire.pdf.PdfDocument;

import com.spire.pdf.PdfPageBase;
import jdk.internal.org.objectweb.asm.util.TraceAnnotationVisitor;

import java.awt.image.BufferedImage;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;



public class tempPdf {

    public static void main(String[] args)  {
        List<String> filePathBefore=getFilePath("./output");
        List<String> filePathMid=new ArrayList<>();//切割后的pdf位置
        List<String>fileImagSave=new ArrayList<>();//要存放图片的地址
        List<String>fileTxtSave=new ArrayList<>();//要存放的提取出来的txt位置
        for(int i=0;i<filePathBefore.size();i++){
            System.out.println(filePathBefore.get(i));
            filePathMid.add(filePathBefore.get(i)+File.separator+"pdf");
            System.out.println(filePathBefore.get(i)+File.separator+"pdf");
            fileImagSave.add(filePathBefore.get(i)+File.separator+"imag"+File.separator);
            System.out.println(filePathBefore.get(i)+File.separator+"imag"+File.separator);
            fileTxtSave.add(filePathBefore.get(i)+File.separator+"text"+File.separator);
        }
        System.out.println("--------------------------------");
        for(int i=0;i<filePathMid.size();i++){
            boolean getImagAndTxtFlag= true;
            while(getImagAndTxtFlag){
                List<String> splitPdf=getFilePath(filePathMid.get(i));//获取单个文件夹下的所有pdf文件的绝对路径
                String[] fileName=getFileName(filePathMid.get(i));
                List<String> splitFileName=new ArrayList<>();//获取单个文件夹下的每一个文件的名字
                for(int j=0;j<splitPdf.size();j++){
                    System.out.println(splitPdf.get(j));
                }
                for(int j=0;j<fileName.length;j++){//切割名字
                    String splitTempName=fileName[j].substring(0,fileName[j].length()-4);
                    if (splitTempName.equals("end")){
                        getImagAndTxtFlag=false;
                    }else{
                        splitFileName.add(splitTempName);
                    }
                }
                for(int j=0;j<splitFileName.size();j++){
                    System.out.println("sss"+splitFileName.get(j));
                }
                for(int j=0;j<splitPdf.size();j++){
                    try {
                        saveTxt(splitPdf.get(j),splitFileName.get(j),fileTxtSave.get(i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(splitPdf.get(j)+" finished save txt");
                    saveImag(splitPdf.get(j),splitFileName.get(j),fileImagSave.get(i));
                    System.out.println(splitPdf.get(j)+" finished save imag");
                    System.out.println(splitPdf.get(j)+"  "+splitFileName.get(j)+"  "+fileTxtSave.get(i)+"  "+j);
                    System.out.println(fileTxtSave.get(i)+splitFileName.get(j)+j+".txt");
                    File f=new File(splitPdf.get(j));
                    System.out.println(f.delete());
                }
            }
        }
    }
    public static boolean forceDelete(File file) {
        boolean result = file.delete();
        int tryCount = 0;
        while (!result && tryCount++ < 10) {
            System.gc();    //回收资源

            result = file.delete();
        }
        return result;
    }
    public static String[] getFileName(String folderPath){
        File folder=new File(folderPath);
        return folder.list();
    }
    public static void saveTxt(String filePath,String fileName,String savePath) throws IOException {
        PdfReader reader = new PdfReader(filePath);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        StringBuffer buff = new StringBuffer();
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i,
                    new SimpleTextExtractionStrategy());
            buff.append(strategy.getResultantText());
        }
        reader.close();

        saveTxtFile(savePath+fileName+".txt", buff.toString());
    }
    public static void saveTxtFile(String filePath,String content){
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
                    //System.out.println(fileName);
                    if(fileName.equals("end.txt")){
                        continue;
                    }else{
                        filePathList.add(rootPath + fileName);
                    }
                }
            }
        }
        return filePathList;
    }
    public static void saveImag(String filePath,String fileName,String savePath){
        //创建一个 PdfDocument 实例
        PdfDocument doc = new PdfDocument();
        //加载 PDF 示例文档
        doc.loadFromFile(filePath);
        //定义一个变量
        int index = 0;
        //遍历所有页面
        for (PdfPageBase page : (Iterable<PdfPageBase>) doc.getPages()) {
            if(page.extractImages()==null){
                break;
            }
            //从所给页面提取图片
            for ( BufferedImage image :page.extractImages()) {
                if(image==null){
                    break;
                }
                //指定输出文档的路径和名称
                File output = new File(savePath + String.format(fileName+"-"+"%d.jpg", index++));
                //将图像另存为.png文件
                try {
                    ImageIO.write(image, "PNG", output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}