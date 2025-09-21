/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.finite.planetlangserver;
import java.io.IOException;
import org.finite.planetlangserver.Networking.Server;
/**
 *
 * @author GAMER
 */
public class PlanetlangServer {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Server srv = new Server();
        try {
            srv.start(8000);
        } catch (IOException ex) {
            System.getLogger(PlanetlangServer.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
