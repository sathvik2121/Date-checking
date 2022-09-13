package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;


import com.itextpdf.text.DocumentException;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

@RestController
@SpringBootApplication
public class SpringMethodTestApplication {

	public static void main(String[] args) throws InvalidKeyException, DocumentException, URISyntaxException, StorageException, IOException, ParseException   {
		SpringMethodTestApplication ob1=new SpringMethodTestApplication();
		boolean message2=ob1.run();
		System.out.println(message2);
		SpringApplication.run(SpringMethodTestApplication.class, args);
	}

	@GetMapping("/")
	
	public String method2()
	{
		//System.out.println("done");
		return "welcome to datecheck";
	}
	
	@GetMapping("/testing")
	
	public boolean run() throws DocumentException, URISyntaxException, StorageException, InvalidKeyException, IOException, ParseException
	{
		
	
		 final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=pocfiledemoaccess;AccountKey=GY0kWJV+ik5mNByEavFYamMDMu5LecAHNK0VmiD6VPQka7s/4OrrATcBol4jOIxq33ID3yChL0W8+AStDb8VCA==;EndpointSuffix=core.windows.net";
		   
			//System.out.println("Azure Blob storage quick start sample");

			CloudStorageAccount storageAccount;
			CloudBlobClient blobClient = null;
		

			 
				// Parse the connection string and create a blob client to interact with Blob storage
				storageAccount = CloudStorageAccount.parse(storageConnectionString);
				blobClient = storageAccount.createCloudBlobClient();
		
				CloudBlobContainer container2=blobClient.getContainerReference("fileaccess");
				 CloudBlockBlob blob = container2.getBlockBlobReference("outputFile.pdf");
			
					
				blob.downloadAttributes();
				
			     Date d1=blob.getProperties().getLastModified();
			     String presentdate= d1.toString();
				    // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				     //Date datestr= formatter.parse(presentdate);
				    // String str1= datestr.toString();

				 
				   
				  File tempDate=File.createTempFile("Updated Date" , ".txt");
				  CloudBlockBlob blob1 = container2.getBlockBlobReference("Updated Date.txt");
				  OutputStream os=new FileOutputStream(tempDate);
				  blob1.download(os);
				  // Creating an object of BufferedReader class
			        BufferedReader br= new BufferedReader(new FileReader(tempDate));
			 
			       
			        String pastdate;
			       pastdate = new String(Files.readAllBytes(Paths.get(tempDate.getAbsolutePath())));
			       // while ((pastdate=br.readLine())!= null)
			       
			        SimpleDateFormat formatter1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
			        Date datestr1= formatter1.parse(presentdate);
			       
			         Date datestr2=formatter1.parse(pastdate);
			      
			         String change=null;
			         System.out.println(datestr1);
			         System.out.println(datestr2);
			        return datestr1.after(datestr2);
	}
}