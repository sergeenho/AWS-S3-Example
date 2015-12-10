/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.serginho.actions;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.UploadedFile;
import org.serginho.awss3conn.Connection;

/**
 *
 * @author Sergio Sa Filho
 * @email code@serginho.org
 * @website www.Serginho.org
 * 
 */
@ManagedBean(name="AwsS3Bean")
@ViewScoped
public class AwsS3Bean {
    private List<UploadedFile> fileList = new ArrayList<UploadedFile>();
    
    public List<S3ObjectSummary> getFileList(){
        return new Connection().getFileList();
    }
    
    public DefaultStreamedContent downloadFile(String key){
        return new Connection().getFile(key);
    }
    
    public void deleteFile(String key){
        new Connection().deleteFile(key);
    }
    
    public void fileUpload(FileUploadEvent event){
        this.fileList.add(event.getFile());
    }
    
    public List<UploadedFile> getFileEditList() {
        return this.fileList;
    }
    
    public void removeEditFile(String fileName){
        for(int i = 0; i < this.fileList.size(); i++){
            if(this.fileList.get(i).getFileName().equals(fileName)){
                this.fileList.remove(i);
                return;
            }
        }
    }
    
    public void uploadEditFiles(){
        for(UploadedFile file : this.fileList)
            if(!new Connection().updaloadFile(file))
                removeEditFile(fileName(file.getFileName()));
    }
    
    public static String fileName(String key){
        return key.substring(key.lastIndexOf("/")+1);
    }
}
