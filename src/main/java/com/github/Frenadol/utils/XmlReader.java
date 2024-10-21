package com.github.Frenadol.utils;

import com.github.Frenadol.model.User;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlReader {

    private String filePath;

    public XmlReader(String filePath) {
        this.filePath = filePath;
    }


    public List<User> getUsersFromXML() {
        List<User> users = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("user");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String name = element.getElementsByTagName("name").item(0).getTextContent();
                    String image = element.getElementsByTagName("image").item(0).getTextContent();

                    // Crear el objeto User y añadirlo a la lista
                    users.add(new User(name,image));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }
}
