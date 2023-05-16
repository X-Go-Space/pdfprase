

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class pdfGetText {
    static String fileName = "C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\resource\\3.pdf";
    public static void main(String[] args) throws Exception {

        readFile();
        readPage();
        readTextImage();
        readRectangle();
    }

    /**
     * 一次获取整个文件的内容
     *
     * @throws Exception
     */
    public static void readFile() throws Exception {
        File file = new File(fileName);
        RandomAccessFile is = new RandomAccessFile(file, "r");
        PDFParser parser = new PDFParser(is);
        parser.parse();
        PDDocument doc = parser.getPDDocument();
        PDFTextStripper textStripper = new PDFTextStripper();
        String s = textStripper.getText(doc);
        System.out.println("总页数：" + doc.getNumberOfPages());
        System.out.println("输出内容：");
        System.out.println(s);
        doc.close();
    }

    /**
     * 分页获取文字的内容
     *
     * @throws Exception
     */
    public static void readPage() throws Exception {
        File file = new File(fileName);
        RandomAccessFile is = new RandomAccessFile(file, "r");
        PDFParser parser = new PDFParser(is);
        parser.parse();
        PDDocument doc = parser.getPDDocument();
        PDFTextStripper textStripper = new PDFTextStripper();
        for (int i = 1; i <= doc.getNumberOfPages(); i++) {
            textStripper.setStartPage(i);
            textStripper.setEndPage(i);
            // 一次输出多个页时，按顺序输出
            textStripper.setSortByPosition(true);
            String s = textStripper.getText(doc);
            System.out.println("当前页：" + i);
            System.out.println("输出内容：");
            System.out.println(s);
        }
        doc.close();
    }

    /**
     * 读取文本内容和图片
     *
     * @throws Exception
     */
    public static void readTextImage() throws Exception {
        File file = new File(fileName);
        PDDocument doc = PDDocument.load(file);
        PDFTextStripper textStripper = new PDFTextStripper();
        for (int i = 1; i <= doc.getNumberOfPages(); i++) {
            textStripper.setStartPage(i);
            textStripper.setEndPage(i);
            String s = textStripper.getText(doc);
            System.out.println("第 " + i + " 页 :" + s);
            // 读取图片
            PDPage page = doc.getPage(i - 1);
            PDResources resources = page.getResources();
            // 获取页中的对象
            Iterable<COSName> xobjects = resources.getXObjectNames();
            if (xobjects != null) {
                Iterator<COSName> imageIter = xobjects.iterator();
                while (imageIter.hasNext()) {
                    COSName cosName = imageIter.next();
                    boolean isImageXObject = resources.isImageXObject(cosName);
                    if (isImageXObject) {
                        // 获取每页资源的图片
                        PDImageXObject ixt = (PDImageXObject) resources.getXObject(cosName);
                        File outputfile = new File("第 " + (i) + " 页" + cosName.getName() + ".jpg");
                        ImageIO.write(ixt.getImage(), "jpg", outputfile);
                    }
                }
            }
        }
        doc.close();
    }

    /**
     * 区域解析
     *
     * @throws Exception
     */
    public static void readRectangle() throws Exception {
        String filePath = fileName;
        File file = new File(filePath);
        PDDocument doc = PDDocument.load(file);
        // 这个四边形所在区域在 y轴向下为正，x轴向右为正。
        int x = 35;
        int y = 300;
        int width = 50;
        int height = 50;
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        // 划定区域
        Rectangle2D rect = new Rectangle(x, y, width, height);
        stripper.addRegion("area", rect);
        PDPage page = doc.getPage(1);
        stripper.extractRegions(page);
        // 获取区域的text
        String data = stripper.getTextForRegion("area");
        data = data.trim();
        System.out.println(data);
        doc.close();
    }
}
