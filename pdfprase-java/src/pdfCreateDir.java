import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class pdfCreateDir {
    public static void main(String[] args) {
        //每个初始文件夹先创建好
        String[] fileName=getFileName("./resource");
        List<String> outputFilePath=new ArrayList<>();
        for(int i=0;i<fileName.length;i++){
            //System.out.println(fileName[i]);
            List<String> words = Arrays.asList(fileName[i].split("\\."));
            String dirName = "."+ File.separator+"output"+File.separator+String.join("", words);
            //System.out.println(dirName);
            outputFilePath.add(dirName);
            //createDir(dirName);
            createDir(dirName+File.separator+"pdfforimag");
            createDir(dirName+File.separator+"imag");
            createDir(dirName+File.separator+"text");
            createDir(dirName+File.separator+"pdffortext");
        }
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
