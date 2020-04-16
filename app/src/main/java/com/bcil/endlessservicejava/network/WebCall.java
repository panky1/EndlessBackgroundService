package com.bcil.endlessservicejava.network;

import org.json.JSONException;

/**
 * Created by prashant.chaudhary on 2/18/2017.
 */
public interface WebCall {
   void getResponse(String response) throws JSONException;
}
