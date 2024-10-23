// XmlReader.java
package com.github.Frenadol.utils;

import com.github.Frenadol.model.Message;
import com.github.Frenadol.model.User;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XmlReader {

    public String filePath;
    private static String usersPathChat = "UsersData.xml";

    public XmlReader(String filePath) {
        this.filePath = filePath;
    }

    public static List<User> getUsersFromXML(String filePath) {
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
                    String name = element.getElementsByTagName("username").item(0).getTextContent();
                    String password = element.getElementsByTagName("password").item(0).getTextContent();
                    String imageBase64 = element.getElementsByTagName("image").item(0).getTextContent();
                    byte[] image = java.util.Base64.getDecoder().decode(imageBase64);

                    List<User> contacts = new ArrayList<>();
                    NodeList contactNodes = element.getElementsByTagName("contact");
                    for (int j = 0; j < contactNodes.getLength(); j++) {
                        Element contactElement = (Element) contactNodes.item(j);
                        String contactName = contactElement.getElementsByTagName("username").item(0).getTextContent();
                        String contactPassword = contactElement.getElementsByTagName("password").item(0).getTextContent();
                        String contactImageBase64 = contactElement.getElementsByTagName("image").item(0).getTextContent();
                        byte[] contactImage = java.util.Base64.getDecoder().decode(contactImageBase64);
                        contacts.add(new User(contactName, contactPassword, contactImage, null));
                    }

                    // Create the User object and add it to the list
                    users.add(new User(name, password, image, contacts));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public static void saveUsersToXML(List<User> users, String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("users");
            document.appendChild(root);

            for (User user : users) {
                Element userElement = document.createElement("user");

                Element name = document.createElement("username");
                name.appendChild(document.createTextNode(user.getName()));
                userElement.appendChild(name);

                Element password = document.createElement("password");
                password.appendChild(document.createTextNode(user.getPassword()));
                userElement.appendChild(password);

                Element image = document.createElement("image");
                String imageBase64 = java.util.Base64.getEncoder().encodeToString(user.getProfileImage());
                image.appendChild(document.createTextNode(imageBase64));
                userElement.appendChild(image);

                Element contactsElement = document.createElement("contacts");
                for (User contact : user.getContacts()) {
                    Element contactElement = document.createElement("contact");

                    Element contactName = document.createElement("username");
                    contactName.appendChild(document.createTextNode(contact.getName()));
                    contactElement.appendChild(contactName);

                    Element contactPassword = document.createElement("password");
                    contactPassword.appendChild(document.createTextNode(contact.getPassword()));
                    contactElement.appendChild(contactPassword);

                    Element contactImage = document.createElement("image");
                    String contactImageBase64 = java.util.Base64.getEncoder().encodeToString(contact.getProfileImage());
                    contactImage.appendChild(document.createTextNode(contactImageBase64));
                    contactElement.appendChild(contactImage);

                    contactsElement.appendChild(contactElement);
                }
                userElement.appendChild(contactsElement);

                root.appendChild(userElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public static List<Message> getMessagesFromXML(String filePath) {
        List<Message> messages = new ArrayList<>();
        List<User> users = getUsersFromXML(usersPathChat); // Ensure users are loaded from the correct path

        try {
            File xmlFile = new File(filePath);
            if (!xmlFile.exists()) {
                return messages; // If the file does not exist, return an empty list
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("message");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // Get message details from XML
                    String senderName = element.getElementsByTagName("sender").item(0).getTextContent();
                    String receiverName = element.getElementsByTagName("receiver").item(0).getTextContent();
                    String content = element.getElementsByTagName("content").item(0).getTextContent();
                    String timestampStr = element.getElementsByTagName("timestamp").item(0).getTextContent();

                    User sender = findUserByName(users, senderName);
                    User receiver = findUserByName(users, receiverName);

                    // Validate if both users are found
                    if (sender != null && receiver != null) {
                        LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);
                        // Create the Message object and add it to the list
                        messages.add(new Message(sender, receiver, content, timestamp));
                    } else {
                        System.err.println("Error: Sender or Receiver not found for message: " + content);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return messages; // Return the list of messages
    }

    private static User findUserByName(List<User> users, String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }
}