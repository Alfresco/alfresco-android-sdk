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

import org.alfresco.mobile.android.api.model.ContentFile;

/**
 * List of static methods to manage I/O operations.
 * 
 * @author Jean Marie Pascal
 */
public class IOUtils
{

    public static final int MAX_BUFFER_SIZE = 1024;

    public static void closeStream(Closeable stream)
    {
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
     * @throws IOException
     */
    public static boolean copyFile(InputStream src, long size, File dest)
    {
        ensureOrCreatePathAndFile(dest);
        OutputStream os = null;
        boolean copied = true;
        int downloaded = 0;

        try
        {
            os = new BufferedOutputStream(new FileOutputStream(dest));

            byte[] buffer = new byte[MAX_BUFFER_SIZE];

            while (size > 0)
            {
                if (size - downloaded < MAX_BUFFER_SIZE)
                {
                    buffer = new byte[(int) (size - downloaded)];
                }

                int read = src.read(buffer);
                if (read == -1 || read == 0) break;

                os.write(buffer, 0, read);
                downloaded += read;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeStream(src);
            closeStream(os);
        }
        return copied;
    }

    public static boolean copyStream(InputStream src, long size, OutputStream os) throws IOException
    {
        boolean copied = true;
        int downloaded = 0;

        try
        {
            os = new BufferedOutputStream(os);

            byte[] buffer = new byte[MAX_BUFFER_SIZE];

            while (size > 0)
            {
                if (size - downloaded < MAX_BUFFER_SIZE)
                {
                    buffer = new byte[(int) (size - downloaded)];
                }

                int read = src.read(buffer);
                if (read == -1 || read == 0) break;

                os.write(buffer, 0, read);
                downloaded += read;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeStream(src);
            closeStream(os);
        }
        return copied;
    }

    public static InputStream getContentFileInputStream(ContentFile contentFile)
    {

        try
        {
            if (contentFile != null) return new BufferedInputStream(new FileInputStream(contentFile.getFile()));
        }
        catch (FileNotFoundException e)
        {
        }
        return null;
    }
}
