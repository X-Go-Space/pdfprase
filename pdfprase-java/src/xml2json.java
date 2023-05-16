
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;


import java.io.*;
import java.util.List;

public class xml2json {
    public static void main(String[] args) {
        // ָ��Ҫ�������ļ���
        File folder = new File("./xmlresource/xmldir");
        int id=1;
        // �����ļ����µ������ļ������ļ���
        for (File file : folder.listFiles()) {
            // ������ļ������ӡ�ļ�·��
            String returnObj= null;
            try {
                returnObj = String.valueOf(readFile(file.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = xml2json.xml2Json(returnObj);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            System.out.println(jsonObject1);
            String content = jsonObject1.toString();

            // ָ��Ҫд����ļ�·��
            File fileJSON = new File("./xmlresource/jsondir/"+id+"output.json");

            try {
                // ����Ҫд����ļ�
                fileJSON.createNewFile();

                // д���ļ�
                FileWriter fw = new FileWriter(fileJSON);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();

                // ��ӡ��д����ļ�·��
                System.out.println("��д���ļ���" + fileJSON.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            id++;
        }

    }
    public static StringBuffer readFile(String strFile) throws IOException {
        StringBuffer strSb = new StringBuffer();
        InputStreamReader inStrR = new InputStreamReader(new FileInputStream(strFile), "UTF-8");
        // character streams
        BufferedReader br = new BufferedReader(inStrR);
        String line = br.readLine();
        while (line != null) {
            strSb.append(line).append("\r\n");
            line = br.readLine();
        }
        String sub="<!DOCTYPE business:PatentDocumentAndRelated SYSTEM \"/DTDS/ExternalStandards/ipphdb-entities.dtd\"[]>";
        int index = strSb.indexOf(sub);
        if (index != -1) {
            strSb.replace(index, index + sub.length(), "");
        }

        return strSb;
    }
    public static JSONObject xml2Json(String xmlStr) throws DocumentException {
        Document   doc = DocumentHelper.parseText(xmlStr);

        JSONObject json = new JSONObject();
        dom4j2Json(doc.getRootElement(), json);
        return json;
    }

    /**
     * xmlתjson
     *
     * @param element
     * @param json
     */
    private static void dom4j2Json(Element element, JSONObject json) {
        // ���������
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute) o;
            if (StringUtils.isNotBlank(attr.getValue())) {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if (chdEl.isEmpty() && StringUtils.isNotBlank(element.getText())) {// ���û����Ԫ��,ֻ��һ��ֵ
            json.put(element.getName(), element.getText());
        }
        for (Element e : chdEl) {// ����Ԫ��
            if (!e.elements().isEmpty()) {// ��Ԫ��Ҳ����Ԫ��
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) {// �����Ԫ���Ѵ���,��תΪjsonArray
                        JSONObject jsono = (JSONObject) o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }
            } else {// ��Ԫ��û����Ԫ��
                for (Object o : element.attributes()) {
                    Attribute attr = (Attribute) o;
                    if (StringUtils.isNotBlank(attr.getValue())) {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty()) {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }
}
