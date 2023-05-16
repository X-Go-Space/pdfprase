import java.io.File;

public class fileDelete {
    public static void main(String[] args) {
        File f=new File("./tempresource/GjsaSKNH.pdf");
        System.out.println(f.delete());
    }
}
