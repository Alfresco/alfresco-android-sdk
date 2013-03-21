package org.alfresco.mobile.android.api.utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.bindings.impl.ClientVersion;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Response;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.util.Log;

public final class HttpUtils
{

    private HttpUtils()
    {

    }

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    public static Response invokeGET(UrlBuilder url, Map<String, List<String>> headers)
    {
        return invoke(url, "GET", null, headers, null, null, null, null);
    }

    public static Response invokeGET(UrlBuilder url, Map<String, List<String>> headers, BigInteger offset,
            BigInteger length)
    {
        return invoke(url, "GET", null, headers, null, offset, length, null);
    }

    public static Response invokePOST(UrlBuilder url, String contentType, Output writer)
    {
        return invoke(url, "POST", contentType, null, writer, null, null, null);
    }

    public static Response invokePOST(UrlBuilder url, String contentType, Output writer,
            Map<String, List<String>> headers)
    {
        return invoke(url, "POST", contentType, headers, writer, null, null, null);
    }

    public static Response invokePOST(UrlBuilder url, String contentType, Map<String, String> params)
    {
        return invoke(url, "POST", contentType, null, null, true, null, null, params);
    }
    
    private static Response invoke(UrlBuilder url, String method, String contentType,
            Map<String, List<String>> httpHeaders, Output writer, boolean forceOutput, BigInteger offset, BigInteger length,
            Map<String, String> params){
        try
        {
            Log.d("URL", url.toString());

            // connect
            HttpURLConnection conn = (HttpURLConnection) (new URL(url.toString())).openConnection();
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(writer != null || forceOutput);
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
                for (Map.Entry<String, List<String>> header : httpHeaders.entrySet())
                {
                    if (header.getValue() != null)
                    {
                        for (String value : header.getValue())
                        {
                            conn.addRequestProperty(header.getKey(), value);
                        }
                    }
                }
            }

            // range
            BigInteger tmpOffset = offset;
            if ((tmpOffset != null) || (length != null))
            {
                StringBuilder sb = new StringBuilder("bytes=");

                if ((tmpOffset == null) || (tmpOffset.signum() == -1))
                {
                    tmpOffset = BigInteger.ZERO;
                }

                sb.append(tmpOffset.toString());
                sb.append("-");

                if ((length != null) && (length.signum() == 1))
                {
                    sb.append(tmpOffset.add(length.subtract(BigInteger.ONE)).toString());
                }

                conn.setRequestProperty("Range", sb.toString());
            }

            conn.setRequestProperty("Accept-Encoding", "gzip,deflate");

            // add url form parameters
            if (params != null)
            {
                DataOutputStream ostream = null;
                OutputStream os = null;
                try
                {
                    os = conn.getOutputStream();
                    ostream = new DataOutputStream(os);

                    Set<String> parameters = params.keySet();
                    StringBuffer buf = new StringBuffer();

                    int paramCount = 0;
                    for (String it : parameters)
                    {
                        String parameterName = it;
                        String parameterValue = (String) params.get(parameterName);

                        if (parameterValue != null)
                        {
                            parameterValue = URLEncoder.encode(parameterValue, "UTF-8");
                            if (paramCount > 0)
                            {
                                buf.append("&");
                            }
                            buf.append(parameterName);
                            buf.append("=");
                            buf.append(parameterValue);
                            ++paramCount;
                        }
                    }
                    ostream.writeBytes(buf.toString());
                }
                finally
                {
                    if (ostream != null)
                    {
                        ostream.flush();
                        ostream.close();
                    }
                    IOUtils.closeStream(os);
                }
            }

            // send data

            if (writer != null)
            {
                //conn.setChunkedStreamingMode((64 * 1024) - 1);
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
            if ((respCode == HttpStatus.SC_OK) || (respCode == HttpStatus.SC_CREATED)
                    || (respCode == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION)
                    || (respCode == HttpStatus.SC_PARTIAL_CONTENT))
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

    private static Response invoke(UrlBuilder url, String method, String contentType,
            Map<String, List<String>> httpHeaders, Output writer, BigInteger offset, BigInteger length,
            Map<String, String> params)
    {
        return invoke(url, method, contentType, httpHeaders, writer, false, offset, length, params);
    }

}
