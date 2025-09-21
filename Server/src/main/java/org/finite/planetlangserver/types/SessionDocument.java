/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.finite.planetlangserver.types;

import java.time.LocalDate;

/**
 *
 * @author GAMER
 */

public class SessionDocument {
    public String FileName;
    public String FileContents;
    public LocalDate DateOfCreation;
    
    /**
     * Session document provides the base class for all files synced over the network
     * @param filename
     * @param FileContents
     */
    public SessionDocument(String filename, String Filecontents, LocalDate datecreated)
    {
        if (filename == null) throw new IllegalArgumentException("file name can not be null");
        else
            FileName = filename;
        if (Filecontents == null) FileContents = null;
        else 
            FileContents = Filecontents;
     
    }
    @Override
    public String toString()
    {
        return FileContents;
    }
    
}
