/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.zeynconverter.ui;
import com.mycompany.zeynconverter.MainFrame;
import com.mycompany.zeynconverter.core.ListService;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import javax.swing.TransferHandler;

/**
 *
 * @author Zeyn
 */
public class FileDragHandler extends TransferHandler {
    private ListService listService;
    private MainFrame mainFrame;
    
    public FileDragHandler(ListService service, MainFrame frame) {
        this.listService = service;
        this.mainFrame = frame;
    }
    
    @Override
    public boolean canImport(TransferSupport support) {
        //check is the dragged data was a "file list" or not
        if(!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
            return false;
        }
        //tell OS to show "copy" icon
        support.setDropAction(COPY);
        return true;
    }
    
    @Override
    public boolean importData(TransferSupport support) {
        if(!canImport(support)){
            return false;
        }
        
        Transferable t = support.getTransferable();
        try{
            List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            
            listService.addFilesFromDrop(files);
            
            mainFrame.updateListView();
            
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
