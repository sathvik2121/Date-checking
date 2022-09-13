package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;


import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import com.itextpdf.text.DocumentException;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

@RestController
@SpringBootApplication
public class SpringMethodTestApplication {

	public static void main(String[] args) throws InvalidKeyException, DocumentException, URISyntaxException, StorageException  {
//		SpringMethodTestApplication ob1=new SpringMethodTestApplication();
//		LocalDateTime message2=ob1.run();
//		System.out.println(message2);
		SpringApplication.run(SpringMethodTestApplication.class, args);
	}

	@GetMapping("/")
	
	public String method2()
	{
		//System.out.println("done");
		return "hello hi pdf generation";
	}
	
	@GetMapping("/testing")
	
	public String run() throws DocumentException, URISyntaxException, StorageException, InvalidKeyException
	{
		
	
		 final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=pocfiledemoaccess;AccountKey=GY0kWJV+ik5mNByEavFYamMDMu5LecAHNK0VmiD6VPQka7s/4OrrATcBol4jOIxq33ID3yChL0W8+AStDb8VCA==;EndpointSuffix=core.windows.net";
		   
			System.out.println("Azure Blob storage quick start sample");

			CloudStorageAccount storageAccount;
			CloudBlobClient blobClient = null;
		

			 
				// Parse the connection string and create a blob client to interact with Blob storage
				storageAccount = CloudStorageAccount.parse(storageConnectionString);
				blobClient = storageAccount.createCloudBlobClient();
		
				CloudBlobContainer container2=blobClient.getContainerReference("fileaccess");
				 CloudBlockBlob blob = container2.getBlockBlobReference("outputFile.pdf");
			
					
				blob.downloadAttributes();
				
			     Date d1=blob.getProperties().getLastModified();
			   
			     return d1.toString();
			  
			     
			    
			     
			  
}
}