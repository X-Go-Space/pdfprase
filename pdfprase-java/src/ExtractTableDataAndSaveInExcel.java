
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.PdfMargins;
import com.spire.pdf.utilities.PdfTable;
import com.spire.pdf.utilities.PdfTableExtractor;

import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;

public class ExtractTableDataAndSaveInExcel {
    public static void main(String[] args)throws IOException {
        //加载PDF文档
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile("C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\resource\\2.pdf");

        //创建StringBuilder类的实例
        StringBuilder builder = new StringBuilder();

        //抽取表格
        PdfTableExtractor extractor;
        PdfTable[] tableLists ;
        for(int pageIndex=0;pageIndex<pdf.getPages().getCount();pageIndex=pageIndex+10){
            PdfDocument newpdf = new PdfDocument();
            PdfPageBase page2; //将原PDF文档的第1、2页拆分，并保存到newpdf1
            if (pageIndex+10>=pdf.getPages().getCount()){
                for(int i = pageIndex;i<pdf.getPages().getCount();i++)
                {
                    page2 = newpdf.getPages().add(pdf.getPages().get(i).getSize(), new PdfMargins(0));
                    pdf.getPages().get(i).createTemplate().draw(page2, new Point2D.Float(0,0));
                }
            }else{
                for(int i = pageIndex;i<pageIndex+10;i++)
                {
                    page2 = newpdf.getPages().add(pdf.getPages().get(i).getSize(), new PdfMargins(0));
                    pdf.getPages().get(i).createTemplate().draw(page2, new Point2D.Float(0,0));
                }
            }

            extractor = new PdfTableExtractor(newpdf);
            for (int page = 0; page <newpdf.getPages().getCount(); page++) {
                System.out.println(page);
                tableLists = extractor.extractTable(page);
                if (tableLists != null && tableLists.length > 0) {
                    for (PdfTable table : tableLists)
                    {
                        int row = table.getRowCount();
                        int column = table.getColumnCount();
                        for (int i = 0; i < row; i++)
                        {
                            for (int j = 0; j < column; j++)
                            {
                                String text = table.getText(i, j);
                                builder.append(text+"$");
                            }
                            builder.append("\r\n");
                        }
                    }
                    int pageName=page+pageIndex;
                    FileWriter fileWriter = new FileWriter("C:\\Users\\wangjiahao\\IdeaProjects\\pdfprase\\output\\"+pageName+".txt");
                    fileWriter.write(builder.toString());
                    fileWriter.flush();
                    fileWriter.close();
                    builder = new StringBuilder();
                }
            }
        }
       /* for (int page = 0; page <pdf.getPages().getCount(); page++) {
            System.out.println(page);
            tableLists = extractor.extractTable(page);
            if (tableLists != null && tableLists.length > 0) {
                for (PdfTable table : tableLists)
                {
                    int row = table.getRowCount();
                    int column = table.getColumnCount();
                    for (int i = 0; i < row; i++)
                    {
                        for (int j = 0; j < column; j++)
                        {
                            String text = table.getText(i, j);
                            builder.append(text+"$");
                        }
                        builder.append("\r\n");
                    }
                }
                FileWriter fileWriter = new FileWriter(page+".txt");
                fileWriter.write(builder.toString());
                fileWriter.flush();
                fileWriter.close();
                builder = new StringBuilder();
            }
        }*/

        //将提取的表格内容写入txt文档


    }
}
 