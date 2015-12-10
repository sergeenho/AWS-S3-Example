/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.serginho.awss3conn;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Sergio Sa Filho
 * @email code@serginho.org
 * @website www.Serginho.org
 * 
 */
public class Connection {
    private String amazonBucket = "amazon.bucket.name"; // Example: All Buckets /company.webapp.dev/test
                                                        // For this example, the bucket will be: company.webapp.dev
    private String key = "amazon.key" + "/";            // For this example, the key will be: test
    
    private AWSCredentials getAWSCredentials(){
        return new BasicAWSCredentials("amazon.accessKey", "amazon.secretKey");
    }
    
    private AmazonS3 getS3Client(){
        return new AmazonS3Client(getAWSCredentials());
    }
    
    public DefaultStreamedContent getFile(String key){       
        return new DefaultStreamedContent(getS3Client().getObject(new GetObjectRequest(this.amazonBucket, this.key)).getObjectContent(), URLConnection.guessContentTypeFromName(fileName(key)), fileName(key));
    }
    
    public List<S3ObjectSummary> getFileList(){
        AmazonS3 s3Client = getS3Client();

        ObjectListing objects = s3Client.listObjects(this.amazonBucket, this.key);
        
        List<S3ObjectSummary> fileList = new ArrayList<S3ObjectSummary>();
                
        do{
            for(S3ObjectSummary objectSummary : objects.getObjectSummaries()){
                if(objectSummary.getSize() != 0) // We don't need to display the root "key" (folder)
                    fileList.add(objectSummary);
            }
            objects = s3Client.listNextBatchOfObjects(objects);
        }while(objects.isTruncated());
        
        return fileList;
    }
    
    public boolean updaloadFile(UploadedFile uploadedFile){
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(uploadedFile.getSize());
        
        try {
            getS3Client().putObject(this.amazonBucket, this.key + uploadedFile.getFileName(), uploadedFile.getInputstream(), objectMetadata);
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    public void deleteFile(String fileName){
        getS3Client().deleteObject(new DeleteObjectRequest(this.amazonBucket, fileName)); // fileName == BUCKET + KEY
    }
        
    public static String fileName(String key){
        return key.substring(key.lastIndexOf("/")+1);
    }
}
