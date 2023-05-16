package AzureFunctions.AzureFunctions;




import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;

import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobProperties;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import org.apache.commons.io.FilenameUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Optional;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;




public class Function {
	 @FunctionName("blobList")
	    public String blobList(
	            @HttpTrigger(
	                name = "req",
	                methods = {HttpMethod.GET, HttpMethod.POST},
	                authLevel = AuthorizationLevel.ANONYMOUS)
	                HttpRequestMessage<Optional<String>> request)throws InvalidKeyException, URISyntaxException, StorageException, UnsupportedEncodingException, MalformedURLException
	    {
		    final String query = request.getQueryParameters().get("storageConnectionString");
	        final String storageConnectionString = request.getBody().orElse(query);
	        
	    		  CloudStorageAccount storageAccount;
	    		 CloudBlobClient blobClient = null;
	    		 storageAccount = CloudStorageAccount.parse(storageConnectionString);
	    		 blobClient = storageAccount.createCloudBlobClient();
	    		 CloudBlobContainer container=blobClient.getContainerReference("xmlcontainer");
	    		 List<String> list=new ArrayList<String>();
	    		 for (ListBlobItem blobItem : container.listBlobs()) {
	    					URL url=blobItem.getUri().toURL();
	    					String s=FilenameUtils.getName(url.getPath());
	    	    		    String result = URLDecoder.decode(s,"utf-8");
	    	    			list.add(result);
	    	    				}
	    		 if(list.size()>0)
	    				 return  String.join(",",list);
	    		 else 
	    			 return "empty";
	    	}
    
    @FunctionName("generatePDF")
    public String generatePDF(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request)throws URISyntaxException, StorageException, InvalidKeyException, ParserConfigurationException, SAXException, UnsupportedEncodingException
    {
        final String query = request.getQueryParameters().get("fileName");
        final String fileName = request.getBody().orElse(query);
        final String storagequery = request.getQueryParameters().get("storageConnectionString");
        final String storageConnectionString = request.getBody().orElse(storagequery);
        
        File xsltFile = null;
		CloudStorageAccount storageAccount;
		CloudBlobClient blobClient = null;
		String blobFileName=null;
		List<String> list=new ArrayList<String>();
		try {    
		
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer container2=blobClient.getContainerReference("fileaccess");
			CloudBlobContainer xmlcontainer=blobClient.getContainerReference("xmlcontainer");
			CloudBlobContainer buffercontainer=blobClient.getContainerReference("buffercontainer");	 
			xsltFile = File.createTempFile("template", ".xsl");
			CloudBlockBlob blob2 = container2.getBlockBlobReference("template.xsl");
	    	FileOutputStream xsloutput= new FileOutputStream(xsltFile);
	    	blob2.download(xsloutput);
	    	
	    	    File xmlFile=File.createTempFile("data", ".xml");
	    	    CloudBlockBlob blob3 = xmlcontainer.getBlockBlobReference(fileName);
	    	    FileOutputStream xmloutput= new FileOutputStream(xmlFile);
	    	    blob3.download(xmloutput);
	    	    StreamSource xmlSource = new StreamSource(xmlFile);
	    	    File outputFile=File.createTempFile("output", ".pdf");
	    	    FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
	    	    FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
	    	    OutputStream out3;
	    	    out3 = new java.io.FileOutputStream( outputFile);
	    	    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out3);
	    	    TransformerFactory factory =  new net.sf.saxon.TransformerFactoryImpl();
	    	    Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));
	    	    transformer.setParameter("fileName", fileName);
	    	    Result res = new SAXResult(fop.getDefaultHandler());
	    	    transformer.transform(xmlSource, res);
	    	    out3.close();
	    	    File picture1=File.createTempFile("picture1",".jpg");
	    	    PDDocument doc = PDDocument.load(outputFile);
	    	    PDPage page = doc.getPage(0);
	    	    CloudBlockBlob blob4 = container2.getBlockBlobReference("nvlaplogo.jpg");
	    	    FileOutputStream pic1= new FileOutputStream(picture1);
	    	    blob4.download(pic1);
	    	    PDImageXObject pdfimg= PDImageXObject.createFromFile(picture1.getAbsolutePath(), doc);
	    	    PDPageContentStream image= new PDPageContentStream(doc, page,AppendMode.APPEND, true, true);
	    	    image.drawImage(pdfimg, 330, 700,200,100);
	    	    image.close();
	    	    doc.save(outputFile.getAbsoluteFile());
	    	    String fileNameWithOutExt = FilenameUtils.removeExtension(fileName);
	    	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd");  
	    	    Date date = new Date();
	    	    String filename=fileNameWithOutExt+"_"+formatter.format(date);
	    	    doc.close();
	    	    CloudBlockBlob blob = buffercontainer.getBlockBlobReference(filename+".pdf");
	    	    blobFileName=filename+".pdf";
	    	    list.add(blobFileName);
	    	    blob.uploadFromFile(outputFile.getAbsolutePath());
	    	    BlobProperties props = blob.getProperties();
	    	    props.setContentType("application/pdf");
	    	    blob.uploadProperties();
	    	    xmloutput.close();
	    	    xsloutput.close();
	    	    pic1.close();
	    	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	            Document document = dBuilder.parse(xmlFile);
	            document.getDocumentElement().normalize();
	            NodeList nList = document.getElementsByTagName("ContactDelivery");
	            Node nNode = nList.item(0);
	            Element eElement = (Element) nNode;
	            String siteID= eElement.getElementsByTagName("Company").item(0).getTextContent();
	            list.add(siteID);
	    	    xsltFile.deleteOnExit();
	    	    outputFile.deleteOnExit();
	    	    xmlFile.deleteOnExit();
	    	    picture1.deleteOnExit();
	    	    
    			
    	}
		
	 catch (FOPException | IOException | TransformerException e) {
	       e.printStackTrace();
	} 
			return String.join(",",list);
	   }
    
    
    @FunctionName("moveFileIntoFolder")
    public String moveRejectedFile(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request) throws URISyntaxException, StorageException, InvalidKeyException, UnsupportedEncodingException
    {
    	final String queryDestContainer = request.getQueryParameters().get("destinationContainer");
        final String destinationContainer= request.getBody().orElse(queryDestContainer);
    	final String queryfileName = request.getQueryParameters().get("fileName");
        final String fileName= request.getBody().orElse(queryfileName);
        final String query = request.getQueryParameters().get("storageConnectionString");
        final String storageConnectionString = request.getBody().orElse(query);
        CloudStorageAccount storageAccount;
    	CloudBlobClient blobClient = null;
    	storageAccount = CloudStorageAccount.parse(storageConnectionString);
		blobClient = storageAccount.createCloudBlobClient();
		CloudBlobContainer buffercontainer=blobClient.getContainerReference("buffercontainer");
		 CloudBlockBlob blob = buffercontainer.getBlockBlobReference(fileName);
		 CloudBlobContainer destinationcontainerservice=blobClient.getContainerReference(destinationContainer);
		 CloudBlockBlob destinationblob = destinationcontainerservice.getBlockBlobReference(fileName);
		 destinationblob.startCopy(blob);
		return "successfull";
    }
   
}
