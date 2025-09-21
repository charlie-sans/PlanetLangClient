/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.finite.planetlangserver.types;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author GAMER
 */
public class Session {
    public List<SessionDocument> SessionDocuments;
    public String SessionName;
    
    public Session(String Sessionname)
    {
        if (Sessionname == null) throw new IllegalArgumentException("Session name Can not be null");
        else
            SessionName = Sessionname;
        SessionDocuments = new ArrayList<SessionDocument>();
        
    }
    public void AddDocument(SessionDocument doc)
    {
        SessionDocuments.add(doc);
        Logger.getLogger("PlanetLangFileSystem").log(Level.INFO, "created file with name: " + doc.FileName);
    }
    public SessionDocument getDoc(String name)
    {
        for (SessionDocument doc : SessionDocuments)
        {
         if (doc.FileName == name) return doc;
        }
        return null;
    }
    
}
