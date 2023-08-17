package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameXml = "data.xml";

        List<Employee> list = parseCSV(columnMapping, fileName);
        List<Employee> listXml = parseXML(fileNameXml);

        String json = listToJson(list);
        String json2 = listToJson(listXml);

        writeString(json, "new_data_csv.json");
        writeString(json2, "new_data_xml.json");

    }

    private static List<Employee> parseXML(String fileNameXml) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileNameXml));
        NodeList nodeList = doc.getElementsByTagName("employee");
        List<Employee> listXml = read(nodeList);
        return listXml;
    }

    private static List<Employee> read(NodeList nodeList) {
        List<Employee> list = new ArrayList<Employee>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                Long id  = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                String firstName  = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName  = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country  = element.getElementsByTagName("country").item(0).getTextContent();
                Integer age  = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                list.add(new Employee(id ,firstName,lastName,country,age));
            }
        }
        return list;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csbReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csbReader).
                    withMappingStrategy(strategy).
                    build();
            List<Employee> list = csv.parse();
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Type>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter file = new
                FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}