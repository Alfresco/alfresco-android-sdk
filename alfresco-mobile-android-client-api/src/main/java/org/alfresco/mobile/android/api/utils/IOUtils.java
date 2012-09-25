/*******************************************************************************
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.mobile.android.api.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;

import org.alfresco.mobile.android.api.model.ContentFile;

import android.util.Log;

/**
 * List of static methods to manage I/O operations.
 * 
 * @author Jean Marie Pascal
 */
public final class IOUtils
{
    // Monitored input stream for UI feedback purposes.
    static class MonitoredBufferedInputStream extends BufferedInputStream
    {  
        ContentFile contentFile = null;
        
        public MonitoredBufferedInputStream(InputStream in)
        {
            super(in);
        }
        
        void setContentFile (ContentFile cf)
        {
            contentFile = cf;
        }

        @Override
        public synchronized int read() throws IOException
        {
            if (contentFile != null)
                contentFile.fileReadCallback(1);
            
            return super.read();
        }

        @Override
        public int read(byte[] b) throws IOException
        {
            int nBytes = super.read(b);
            
            if (contentFile != null)
                contentFile.fileReadCallback (nBytes);
            
            return nBytes;
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException
        {
            int nBytes = super.read(b, off, len);
            
            if (contentFile != null)
                contentFile.fileReadCallback (nBytes);
            
            return nBytes;
        }
    }


    private IOUtils()
    {

    }

    public static final int MAX_BUFFER_SIZE = 1024;

    public static void closeStream(Closeable stream)
    {
        Log.d("IOUTILS", "Stream close");

        if (stream != null)
        {
            try
            {
                stream.close();
            }
            catch (IOException e)
            {
                // Ignore
            }
        }
    }

    private static File createUniqueName(File file)
    {
        if (!file.exists()) { return file; }

        int index = 1;

        while (index < 500)
        {
            file = new File(file.getParentFile(), file.getName() + "-" + index);
            if (!file.exists()) { return file; }
            index++;
        }
        return null;
    }

    public static void ensureOrCreatePathAndFile(File contentFile)
    {
        contentFile.getParentFile().mkdirs();
        createUniqueName(contentFile);
    }

    /**
     * Copy inputStream into the specified file. If file already present, we
     * create a new one.
     * 
     * @param src
     * @param size : inputstream size.
     * @param dest
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean copyFile(InputStream src, File dest) throws FileNotFoundException, IOException
    {
        ensureOrCreatePathAndFile(dest);
        return copyStream(src, new FileOutputStream(dest));
    }

    public static boolean copyStream(InputStream src, OutputStream os) throws IOException
    {
        boolean copied = true;
        try
        {
            os = new BufferedOutputStream(os);
            MonitoredBufferedInputStream bis = new MonitoredBufferedInputStream(src);

            byte[] buffer = new byte[MAX_BUFFER_SIZE];

            int count;
            while ((count = bis.read(buffer)) != -1)
            {
                os.write(buffer, 0, count);
            }
            os.flush();
        }
        catch (IOException e)
        {
            copied = false;
            throw e;
        }
        finally
        {
            closeStream(os);
            closeStream(src);
        }
        return copied;
    }

    public static InputStream getContentFileInputStream(ContentFile contentFile)
    {
        try
        {
            if (contentFile != null)
            {
                MonitoredBufferedInputStream mb = new MonitoredBufferedInputStream(new FileInputStream(contentFile.getFile()));
                mb.setContentFile (contentFile);
                return mb;
            }
        }
        catch (FileNotFoundException e)
        {
        }
        return null;
    }
}
