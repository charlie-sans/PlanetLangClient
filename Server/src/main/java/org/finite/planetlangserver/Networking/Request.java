/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.finite.planetlangserver.Networking;

import org.json.*;

/**
 *
 * @author GAMER
 */
public class Request {
    public String type;
    public Auth.AuthType authType;
    public JSONObject item;
    /// Creates a new request object to handle request contents.
    /// <summary>
    /// designed for sending content over the network, a request handles the content and if it can send to the endpoint that it needs to go towards.
    ///</summary>
    public Request(String Type, Auth.AuthType Authtype,JSONObject messagecontents)
    {
        // super stupid method thing.
        if (Type == null) throw new IllegalArgumentException("type can not be null");
        else 
            type = Type;
        if (Authtype == null) authType = Auth.AuthType.Guest;
        else
            authType = Authtype;
        if (messagecontents == null) item = new JSONObject();
        else 
            item = messagecontents;
    }
    public String Debug()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Request Type: " + type);
        sb.append("Request auth type: " + authType.toString());
        sb.append("Request Contents: " + item.toString());
        return sb.toString();
    }
    public String toString()
    {
        return item.toString();
    }
}
