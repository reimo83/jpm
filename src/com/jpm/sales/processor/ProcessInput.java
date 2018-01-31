package com.jpm.sales.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Processes input files from the input directory.
 *
 */
public class ProcessInput
{
  private Schema schema;
  private String inputDirectory;
  private String processedDirectory;
  private Map<String, Product> processedData = new HashMap<>();

  /**
   * Initializes directory parameters
   *
   * @param inputDirectory
   * @param processedDirectory
   */
  public ProcessInput(String inputDirectory, String processedDirectory)
  {
    this.inputDirectory = inputDirectory;
    this.processedDirectory = processedDirectory;
  }

  /**
   * Processes all files from the input directory
   */
  public void processInputFiles()
  {
    InputStream xsdFile;

    try
    {
      xsdFile = new FileInputStream("sale.xsd");
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      schema = factory.newSchema(new StreamSource(xsdFile));
    }
    catch (Exception ex)
    {
      Logger.getLogger(ProcessInput.class.getName()).log(Level.SEVERE, null, ex);
      System.exit(0);
    }

    File directory = new File(inputDirectory);
    File[] fileList = directory.listFiles();
    Boolean isValid = Boolean.TRUE;
    String validationMessage;
    Integer i = 0;

    for (File file : fileList)
    {
      i++;

      if (file.isFile())
      {
        System.out.print(file.getName() + " ");

        try
        {
          isValid = this.validateInputFile(
            new FileInputStream(inputDirectory + file.getName())
          );
          validationMessage = (isValid.equals(Boolean.TRUE)) ? "is valid" : "IS INVALID";
          System.out.println(validationMessage);
          this.processFile(
            inputDirectory + file.getName(), processedDirectory + file.getName()
          );
        }
        catch (FileNotFoundException ex)
        {
          Logger.getLogger(ProcessInput.class.getName()).log(Level.SEVERE, null, ex);
        }
      }

      if (i % 10 == 0)
      {
        this.processMediumReport();
      }

      if (i % 50 == 0)
      {
        this.processFinalReport();
      }
    }
  }

  /**
   * Validates input XML file against XSD schema.
   *
   * @param xmlFile Input file for validation
   * @return
   */
  private Boolean validateInputFile(InputStream xmlFile)
  {
    try
    {
      Validator validator = schema.newValidator();
      validator.validate(new StreamSource(xmlFile));

      return Boolean.TRUE;
    }
    catch(Exception ex)
    {
        return Boolean.FALSE;
    }
  }

  /**
   *
   * @param filename Path of the input file
   * @param target Path for the target file after successful data processing
   * @return
   */
  private Boolean processFile(String filename, String target)
  {
    String productType;
    Integer quantity;
    Float price;
    String currency;
    Float currentPrice;

    try
    {
      File xmlFile = new File(filename);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(xmlFile);
      doc.getDocumentElement().normalize();
      doc.getDocumentElement().getNodeName();
      NodeList itemList = doc.getElementsByTagName("item");

      for (int i = 0; i < itemList.getLength(); i++)
      {
        Node node = itemList.item(i);

        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
          Element element = (Element)node;
          productType = element.getAttribute("productType");
          quantity = new Integer(element.getAttribute("quantity"));
          price = new Float(element.getAttribute("price"));
          currency =  element.getAttribute("currency");
          this.addData(productType, price, quantity);
        }
      }

      Files.move(Paths.get(filename), Paths.get(target), REPLACE_EXISTING);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return Boolean.FALSE;
    }

    return Boolean.TRUE;
  }

  /**
   * Add new values to the data structure
   *
   * @param productType Product type
   * @param price Price of a item
   * @param quantity Number of items
   */
  private void addData(String productType, Float price, Integer quantity)
  {
    if (processedData.containsKey(productType))
    {
      Product product = processedData.get(productType);
      product.add(quantity, price);
      processedData.put(productType, product);
    }
    else
    {
      processedData.put(productType, new Product(quantity, price));
    }
  }

  /**
   * Generates the medium report
   */
  private void processMediumReport()
  {
    System.out.println("--- 10 ---");
    Product product;

    for (Map.Entry<String, Product> entry : processedData.entrySet())
    {
      product = entry.getValue();
      System.out.println(
        "Product type: " + entry.getKey() + " / Total price: " +
        String.format("%.2f", product.getTotalPrice()) + " / Total quantity: " +
        product.getTotalQuantity()
      );
    }
  }

  /**
   * Generates the final report
   */
  private void processFinalReport()
  {
    System.out.println("--- 50 ---");

    //TODO business requirements are little bit unclear at the moment
  }
}
