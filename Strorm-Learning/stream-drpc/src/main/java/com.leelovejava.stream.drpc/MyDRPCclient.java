package com.leelovejava.stream.drpc;


import org.apache.storm.Config;
import org.apache.storm.generated.DRPCExecutionException;
import org.apache.storm.thrift.TException;
import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;

import java.util.Map;

public class MyDRPCclient {

    /**
     * @param args
     */
    public static void main(String[] args) {

        Config conf = new Config();
        conf.setDebug(false);
        DRPCClient client;
        try {
            client = new DRPCClient(conf, "node1", 3772);


            String result = client.execute("exclamation", "11,22");

            System.out.println(result);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
