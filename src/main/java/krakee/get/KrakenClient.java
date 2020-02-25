/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package krakee.get;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Kraken Trades REST client definition
 * @author rgt
 */
@Path("/Trades")
@RegisterRestClient
public interface KrakenClient {

    @GET
    public Response getTrade(@QueryParam("pair") String pair, @QueryParam("since") String since);
}
