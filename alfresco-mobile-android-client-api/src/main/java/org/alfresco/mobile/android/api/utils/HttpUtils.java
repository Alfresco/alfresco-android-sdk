package org.alfresco.mobile.android.api.utils;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.bindings.impl.ClientVersion;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Response;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

public final class HttpUtils
{
    
    private HttpUtils(){
        
    }

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    
    public static Response invokeGET(UrlBuilder url, Map<String, List<String>> headers) {
        return invoke(url, "GET", null, headers, null, null, null);
    }

    public static Response invokeGET(UrlBuilder url, Map<String, List<String>> headers, BigInteger offset, BigInteger length) {
        return invoke(url, "GET", null, headers, null, offset, length);
    }
    
    public static Response invokePOST(UrlBuilder url, String contentType, Output writer)
    {
        return invoke(url, "POST", contentType, null, writer, null, null);
    }

    private static Response invoke(UrlBuilder url, String method, String contentType, Map<String, List<String>> httpHeaders,
            Output writer, BigInteger offset, BigInteger length)
    {
        try
        {

            // connect
            HttpURLConnection conn = (HttpURLConnection) (new URL(url.toString())).openConnection();
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(writer != null);
            conn.setAllowUserInteraction(false);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", ClientVersion.OPENCMIS_CLIENT);

            // set content type
            if (contentType != null)
            {
                conn.setRequestProperty("Content-Type", contentType);
            }
            // set other headers
            if (httpHeaders != null)
            {
                for (Map.Entry<String, List<String>> header : httpHeaders.entrySet()) {
                    if (header.getValue() != null) {
                        for (String value : header.getValue()) {
                            conn.addRequestProperty(header.getKey(), value);
                        }
                    }
                }
            }

            // range
            if ((offset != null) || (length != null))
            {
                StringBuilder sb = new StringBuilder("bytes=");

                if ((offset == null) || (offset.signum() == -1))
                {
                    offset = BigInteger.ZERO;
                }

                sb.append(offset.toString());
                sb.append("-");

                if ((length != null) && (length.signum() == 1))
                {
                    sb.append(offset.add(length.subtract(BigInteger.ONE)).toString());
                }

                conn.setRequestProperty("Range", sb.toString());
            }

            conn.setRequestProperty("Accept-Encoding", "gzip,deflate");

            // send data
            if (writer != null)
            {
                conn.setChunkedStreamingMode((64 * 1024) - 1);

                OutputStream connOut = null;

                connOut = conn.getOutputStream();

                OutputStream out = new BufferedOutputStream(connOut, BUFFER_SIZE);
                writer.write(out);
                out.flush();
            }

            // connect
            conn.connect();

            // get stream, if present
            int respCode = conn.getResponseCode();
            InputStream inputStream = null;
            if ((respCode == 200) || (respCode == 201) || (respCode == 203) || (respCode == 206))
            {
                inputStream = conn.getInputStream();
            }

            // get the response
            return new Response(respCode, conn.getResponseMessage(), conn.getHeaderFields(), inputStream,
                    conn.getErrorStream());
        }
        catch (Exception e)
        {
            throw new CmisConnectionException("Cannot access " + url + ": " + e.getMessage(), e);
        }
    }

}
