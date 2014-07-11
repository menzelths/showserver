package de.qreator;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import com.jetdrone.vertx.yoke.Engine;
import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.engine.StringPlaceholderEngine;
import com.jetdrone.vertx.yoke.middleware.*;
import com.jetdrone.vertx.yoke.middleware.BridgeSecureHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.platform.Verticle;

/*
 This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class PingVerticle extends Verticle {

    public void start() {
        int port=8080;
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("Bitte den Port eingeben: ");
        try{
        String text=br.readLine();
        int p=Integer.parseInt(text);
        if (p!=0){
            port=p;
        }
        } catch(Exception e){
            e.printStackTrace();
        }
        
        
        Yoke yoke = new Yoke(this);
        
        JsonObject config = new JsonObject().putString("prefix", "/bridge");
        JsonArray inboundPermitted = new JsonArray();
        JsonObject inboundPermitted1 = new JsonObject().putString("address", "showserver.alle");
        inboundPermitted.add(inboundPermitted1);
        JsonObject inboundPermitted2 = new JsonObject().putString("address", "showserver.steuerung");
        inboundPermitted.add(inboundPermitted2);
        JsonArray outboundPermitted = new JsonArray();
        JsonObject outboundPermitted1 = new JsonObject().putString("address", "showserver.alle");
        outboundPermitted.add(outboundPermitted1);
        JsonObject outboundPermitted2 = new JsonObject().putString("address", "showserver.steuerung");
        outboundPermitted.add(outboundPermitted2);
        
        HttpServer server = vertx.createHttpServer();
        
        
        
        
        yoke.engine(new StringPlaceholderEngine("templates"));
        yoke.use(new ErrorHandler(true));
        yoke.use(new Static("./web"));
       
        yoke.use(new Router()
            .get("/", new Middleware() {

            @Override
            public void handle(YokeRequest request, Handler<Object> hndlr) {
              request.put("keys", "UqUtoRe"); // rennstrecke als standard setzen
              request.response().render("steuerung.shtml", hndlr);
            }
                
            })
            .get("/lade/:name/:id", new Middleware() {

            @Override
            public void handle(YokeRequest request, Handler<Object> hndlr) {
              request.put("keys", request.params().get("id"));
              request.response().render("steuerung.shtml", hndlr);
            }
                
            })
            .get("/steuerung",new Middleware(){
                public void handle(YokeRequest request, Handler<Object> hndlr) {
              request.put("keys", "ohobULUX");
              request.response().render("befehlssteuerung.shtml", hndlr);
            }
            })
            .get("/steuerung/:id",new Middleware(){
                public void handle(YokeRequest request, Handler<Object> hndlr) {
              request.put("keys", request.params().get("id"));
              request.response().render("befehlssteuerung.shtml", hndlr);
            }
            })
                
        );
        yoke.listen(server);
        vertx.createSockJSServer(server).bridge(config, inboundPermitted, outboundPermitted);
        
        server.listen(port);
        
    }
}
