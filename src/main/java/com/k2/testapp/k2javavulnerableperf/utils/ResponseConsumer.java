package com.k2.testapp.k2javavulnerableperf.utils;

import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.CharBuffer;

public class ResponseConsumer extends AsyncCharConsumer<String> {
    public static final String EMPTY_STRING = "";
    private String result = EMPTY_STRING;
    @Override
    protected void onResponseReceived(final org.apache.http.HttpResponse response) {
        result = String.valueOf(response.getStatusLine().getStatusCode());
    }

    @Override
    protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl)
            throws IOException {
        while (buf.hasRemaining()) {
            System.out.print(buf.get());
        }
    }

    @Override
    protected void releaseResources() {
    }

    @Override
    protected String buildResult(final HttpContext context) {
        return result;
    }
}