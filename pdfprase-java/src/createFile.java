import java.io.File;
import java.io.IOException;

public class createFile {
    public static void main(String[] args) {
        //2、在D:\Java_study\FilePathTest 目录下创建一个文件夹目录 javaFile
        //public boolean mkdir() 创建由此抽象路径名命名的目录
        File file2 = new File("C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\src\\testfile");
        boolean newjavaFile = file2.mkdir();
        System.out.println(newjavaFile);
    }
}
