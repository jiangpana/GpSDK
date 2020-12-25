package com.rj.core.vk.requests;

import com.vk.api.sdk.VKApiManager;
import com.vk.api.sdk.VKApiResponseParser;
import com.vk.api.sdk.VKMethodCall;
import com.vk.api.sdk.exceptions.VKApiException;
import com.vk.api.sdk.internal.ApiCommand;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 包名:com.rj.googlesdk.vk.requests
 */
public class VKCommand extends ApiCommand<String> {
    private String method ;
    private Map<String, String> map;
    public VKCommand(String method, Map<String, String> map) {
        this.map=map;
        this.method=method;
    }

//    "users.get"
//VKMethodCall call= new VKMethodCall.Builder()
//        .args("user_ids", "ff")
//        .args("fields", "photo_200")
//        .build();

    @Override
    protected String onExecute(@NotNull VKApiManager manager) throws InterruptedException, IOException, VKApiException {
        Set<Map.Entry<String, String>> entries = map.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        VKMethodCall.Builder callBuilder= new VKMethodCall.Builder().method(method).version(manager.getConfig().getVersion());
        while(iterator.hasNext()){
            Map.Entry<String, String> next = iterator.next();
            callBuilder.args(next.getKey(), next.getValue());
        }
        return manager.execute(callBuilder.build(),new VKApiResponseParser<String>(){
            @Override
            public String parse(String response) {
                return response;
            }
        });
    }
}
