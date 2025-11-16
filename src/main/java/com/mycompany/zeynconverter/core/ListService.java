/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.zeynconverter.core;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Zeyn
 */
public class ListService {
    private List<File> internalList = new ArrayList<>();
    
    //add file from "Add File(s)" button
    public void addFiles(File[] filesToAdd) {
        if(filesToAdd == null){
            return;
        }
        for(File file : filesToAdd) {
            //duplicate file filter
            if(!this.internalList.contains(file)){
                this.internalList.add(file);
            }
        }
    }
    
    //drag and drop logic
    public void addFilesFromDrop(List<File> filesToDrop){
        if(filesToDrop == null){
            return;
        }
        
        for (File file : filesToDrop) {
            String name = file.getName().toLowerCase();
            
            // extension filter
            if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp")) {
                //duplicate file filter
                if (!this.internalList.contains(file)) {
                    this.internalList.add(file);
                }
            }
        }
    }
    
    //deleting the file based on selected position (index)
    public void removeIndices(int[] indicesToRemove){
        for(int i = indicesToRemove.length - 1; i >= 0; i--) {
            int index = indicesToRemove[i];
            this.internalList.remove(index);
        }
    }
    
    //moving the selected file a step above
    public void moveUp(int index) {
        if (index > 0) {
            //change the position
            File file = this.internalList.remove(index);
            this.internalList.add(index - 1, file);
        }
    }
    
    //moving the selected file a step below
    public void moveDown(int index) {
        if (index != -1 && index < this.internalList.size() - 1) {
            //switch the position
            File file = this.internalList.remove(index);
            this.internalList.add(index + 1, file);
        }
    }
    
    //clearing the list
    public void clearList() {
        this.internalList.clear();
    }
    
    //getter to get file name list to be shown on GUI
    public List<String> getFileNames() {
        List<String> names = new ArrayList<>();
        for (File file : this.internalList) {
            names.add(file.getName());
        }
        return names;
    }
    
    //returning raw list to be sent to converter worker
    public List<File> getFiles() {
        //return the copy so that MainFrame wouldn't break it
        return new ArrayList<>(this.internalList);
    }
    
    //returning a file based index (it's used for path preview)
    public File getFileAt(int index) {
        if(index >= 0 && index < this.internalList.size()) {
            return this.internalList.get(index);
        }
        return null;
    }
    
    //checking is the list empty or not (for conversion necessary)
    public boolean isEmpty() {
        return this.internalList.isEmpty();
    }
}
