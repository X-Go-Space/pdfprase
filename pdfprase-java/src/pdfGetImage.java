

import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

public class pdfGetImage {

    public static void main(String[] args) throws IOException {
        pdfImagesExtract();
    }

    private static void pdfImagesExtract() throws IOException {
        String path = "C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\resource\\1.pdf";
        PDDocument document = null;
        try {
            document = PDDocument.load(new FileInputStream(path));
        } catch (IOException e) {
            System.out.println("出现错误"+e.getMessage());
            return;
        }
        int size = document.getNumberOfPages();
        System.out.println(size);
        for (int i = 0; i < size; i++) {
            PDPage page = document.getPage(i);
            PDResources resources = page.getResources();
            Iterable<COSName> objs = resources.getXObjectNames();
            if (objs == null) {
                break;
            }
            Iterator<COSName> iterator = objs.iterator();
            while (iterator.hasNext()) {
                COSName key = iterator.next();
                if (resources.isImageXObject(key)) {
                    PDImageXObject image = (PDImageXObject) resources.getXObject(key);
                    BufferedImage bfimge = image.getImage();
                    //imageIoToFile(bfimge);
                    thumbnailsToFile(bfimge);//对应其中的一张
                    //生成pdf文件
             /*       String savePath = "E:\\DOWNLOAD\\images\\" + key.getName() + System.currentTimeMillis() + ".pdf";
                    PDDocument pdDocument = new PDDocument();
                    pdDocument.addPage(new PDPage());
                    PDPageContentStream contentStream = new PDPageContentStream(pdDocument,pdDocument.getPage(0), PDPageContentStream.AppendMode.APPEND,true);
                    contentStream.drawImage(image, 0,0,image.getWidth(),image.getHeight());
                    contentStream.close();
                    pdDocument.save(savePath);
                    pdDocument.close();
                    System.out.println(image.getSuffix() + ","+image.getHeight() +"," + image.getWidth());*/
                }
            }
        }
        document.close();
    }

    private static void thumbnailsToFile(BufferedImage bfimge) throws IOException {
        File file = new File("C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\output\\" + System.currentTimeMillis());
        Thumbnails.of(bfimge).scale(1f).outputQuality(0.8f).outputFormat("jpg").toFile(file);
    }

    private static void imageIoToFile(BufferedImage bfimge) throws IOException {
        File file = new File("C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\output\\" + System.currentTimeMillis() + ".png");
        int with = 900;
        int hight = 1440;
        BufferedImage imge = new BufferedImage(with, hight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = imge.getGraphics();
        graphics.drawImage(bfimge, 0, 0, with, hight, Color.LIGHT_GRAY, null);
        graphics.drawString("xx", 200, 600);
        graphics.dispose();
        ImageIO.write(imge, "PNG", file);
    }


}
