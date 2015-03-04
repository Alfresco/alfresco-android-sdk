/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * 
 * This file is part of the Alfresco Mobile SDK.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.alfresco.mobile.android.api.network;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.IOUtils;
import org.apache.chemistry.opencmis.client.bindings.impl.ClientVersion;
import org.apache.chemistry.opencmis.client.bindings.impl.CmisBindingsHelper;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.DefaultHttpInvoker;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpInvoker;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.spi.AuthenticationProvider;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.util.Log;

public class NetworkHttpInvoker implements HttpInvoker
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHttpInvoker.class);

    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    public Response invokeGET(UrlBuilder url, BindingSession session)
    {
        return invoke(url, "GET", null, null, null, session, null, null);
    }

    public Response invokeGET(UrlBuilder url, BindingSession session, BigInteger offset, BigInteger length)
    {
        return invoke(url, "GET", null, null, null, session, offset, length);
    }

    public Response invokePOST(UrlBuilder url, String contentType, Output writer, BindingSession session)
    {
        return invoke(url, "POST", contentType, null, writer, session, null, null);
    }

    public Response invokePUT(UrlBuilder url, String contentType, Map<String, String> headers, Output writer,
            BindingSession session)
    {
        return invoke(url, "PUT", contentType, headers, writer, session, null, null);
    }

    public Response invokeDELETE(UrlBuilder url, BindingSession session)
    {
        return invoke(url, "DELETE", null, null, null, session, null, null);
    }

    protected HttpURLConnection getHttpURLConnection(URL url) throws IOException
    {
        return (HttpURLConnection) url.openConnection();
    }
    
    protected Response invoke(UrlBuilder url, String method, String contentType, Map<String, String> headers,
            Output writer, BindingSession session, BigInteger offset, BigInteger length)
    {
        try
        {
            // log before connect
            Log.d("URL", url.toString());
            if (LOG.isDebugEnabled())
            {
                LOG.debug(method + " " + url);
            }

            // connect
            HttpURLConnection conn = getHttpURLConnection(new URL(url.toString()));
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(writer != null);
            conn.setAllowUserInteraction(false);
            conn.setUseCaches(false);
            conn.setRequestProperty(HTTP.USER_AGENT, ClientVersion.OPENCMIS_CLIENT);

            // timeouts
            int connectTimeout = session.get(SessionParameter.CONNECT_TIMEOUT, -1);
            if (connectTimeout >= 0)
            {
                conn.setConnectTimeout(connectTimeout);
            }

            int readTimeout = session.get(SessionParameter.READ_TIMEOUT, -1);
            if (readTimeout >= 0)
            {
                conn.setReadTimeout(readTimeout);
            }

            // set content type
            if (contentType != null)
            {
                conn.setRequestProperty(HTTP.CONTENT_TYPE, contentType);
            }
            // set other headers
            if (headers != null)
            {
                for (Map.Entry<String, String> header : headers.entrySet())
                {
                    conn.addRequestProperty(header.getKey(), header.getValue());
                }
            }

            // authenticate
            AuthenticationProvider authProvider = CmisBindingsHelper.getAuthenticationProvider(session);
            if (authProvider != null)
            {
                Map<String, List<String>> httpHeaders = authProvider.getHTTPHeaders(url.toString());
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

                if (conn instanceof HttpsURLConnection)
                {
                    SSLSocketFactory sf = authProvider.getSSLSocketFactory();
                    if (sf != null)
                    {
                        ((HttpsURLConnection) conn).setSSLSocketFactory(sf);
                    }

                    HostnameVerifier hv = authProvider.getHostnameVerifier();
                    if (hv != null)
                    {
                        ((HttpsURLConnection) conn).setHostnameVerifier(hv);
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

            // compression
            Object compression = session.get(AlfrescoSession.HTTP_ACCEPT_ENCODING);
            if (compression == null)
            {
                conn.setRequestProperty("Accept-Encoding", "");
            }
            else
            {
                Boolean compressionValue;
                try
                {
                    compressionValue = Boolean.parseBoolean(compression.toString());
                    if (compressionValue)
                    {
                        conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
                    }
                    else
                    {
                        conn.setRequestProperty("Accept-Encoding", "");
                    }
                }
                catch (Exception e)
                {
                    conn.setRequestProperty("Accept-Encoding", compression.toString());
                }
            }

            // locale
            if (session.get(AlfrescoSession.HTTP_ACCEPT_LANGUAGE) instanceof String && session.get(AlfrescoSession.HTTP_ACCEPT_LANGUAGE) != null)
            {
                conn.setRequestProperty("Accept-Language", session.get(AlfrescoSession.HTTP_ACCEPT_LANGUAGE).toString());
            }

            // send data
            if (writer != null)
            {
                Object chunkTransfert = session.get(AlfrescoSession.HTTP_CHUNK_TRANSFERT);
                if (chunkTransfert != null && Boolean.parseBoolean(chunkTransfert.toString()))
                {
                    conn.setRequestProperty(HTTP.TRANSFER_ENCODING, "chunked");
                    conn.setChunkedStreamingMode(0);
                }

                conn.setConnectTimeout(900000);

                OutputStream connOut = null;

                Object clientCompression = session.get(SessionParameter.CLIENT_COMPRESSION);
                if ((clientCompression != null) && Boolean.parseBoolean(clientCompression.toString()))
                {
                    conn.setRequestProperty(HTTP.CONTENT_ENCODING, "gzip");
                    connOut = new GZIPOutputStream(conn.getOutputStream(), 4096);
                }
                else
                {
                    connOut = conn.getOutputStream();
                }

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

            // log after connect
            if (LOG.isTraceEnabled())
            {
                LOG.trace(method + " " + url + " > Headers: " + conn.getHeaderFields());
            }

            // forward response HTTP headers
            if (authProvider != null)
            {
                authProvider.putResponseHeaders(url.toString(), respCode, conn.getHeaderFields());
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

    // ///////////////////////////////////////////////
    // STATIC METHOD
    // ///////////////////////////////////////////////
    public static Response invokeGET(UrlBuilder url, Map<String, List<String>> headers)
    {
        return invoke(url, "GET", null, headers, null, null, null, null);
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
            Map<String, List<String>> httpHeaders, Output writer, BigInteger offset, BigInteger length,
            Map<String, String> params)
    {
        return invoke(url, method, contentType, httpHeaders, writer, false, offset, length, params);
    }

    private static Response invoke(UrlBuilder url, String method, String contentType,
            Map<String, List<String>> httpHeaders, Output writer, boolean forceOutput, BigInteger offset,
            BigInteger length, Map<String, String> params)
    {
        try
        {
            // Log.d("URL", url.toString());

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
                // conn.setChunkedStreamingMode((64 * 1024) - 1);
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

}
