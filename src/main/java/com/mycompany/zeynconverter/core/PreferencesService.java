/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.zeynconverter.core;

import java.util.prefs.Preferences;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Zeyn
 */
public class PreferencesService {
    private Preferences prefs;
    
    private final String PREF_KEY_OUTPUT_PATH = "defaultOutputPath";
    
    public PreferencesService(){
        prefs = Preferences.userNodeForPackage(com.mycompany.zeynconverter.MainFrame.class);
    }
    
    public String loadPath(){
        String defaultPath = FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();
        
        return prefs.get(PREF_KEY_OUTPUT_PATH, defaultPath);
    }
    
    public void savePath(String path){
        if(path != null && !path.isEmpty()){
            prefs.put(PREF_KEY_OUTPUT_PATH, path);
        }
    }
}
