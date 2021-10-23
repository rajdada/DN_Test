package com.test.dn;

import com.test.dn.constants.ErrConstants;
import com.test.dn.constants.TestConstants;
import com.test.dn.exceptions.ImageStorageException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RunWith(JUnit4.class)
public class ImageStorageHelperTests
{
    private String filePath = this.getClass().getClassLoader().getResource("")
            .getPath().replace("target/test-classes/", "temp");

    @Test
    public void testImageStorageHelperWithDirNegative()
    {
        try
        {
            ImageStorageHelper negConst = new ImageStorageHelper("dummy");
        }
        catch (ImageStorageException e)
        {
            Assert.assertEquals(ErrConstants.ERR_CODE_NOT_DIRECTORY, e.getErrorCode());
        }
    }

    @Test
    public void testImageStorageHelperWithDir()
    {
        try
        {
            ImageStorageHelper constr = new ImageStorageHelper(filePath);
        }
        catch (ImageStorageException e)
        {
            Assert.assertEquals(ErrConstants.ERR_CODE_NOT_DIRECTORY, e.getErrorCode());
        }
    }

    @Test
    public void testImageStorageHelperWithDirAndNewDir()
    {
        String newFilePath = filePath.replace("temp", "temp_new");
        try
        {
            ImageStorageHelper negConst = new ImageStorageHelper(filePath, newFilePath);
        }
        catch (ImageStorageException e)
        {
            Assert.assertEquals(ErrConstants.ERR_CODE_NOT_DIRECTORY, e.getErrorCode());
        }
    }

    @Test
    public void testIsDirectoryExists()
    {
        String newFilePath = filePath.replace("temp", "temp_new/new");
        try
        {
            ImageStorageHelper negConst = new ImageStorageHelper(filePath);
            // Accessing private method using reflection
            Method method = ImageStorageHelper.class.getDeclaredMethod("isDirectoryExists", String.class);
            method.setAccessible(true);
            method.invoke(negConst, newFilePath);

        }
        catch (ImageStorageException e)
        {
            //Not needed
        }
        catch (NoSuchMethodException e)
        {
            Assert.assertFalse(TestConstants.ERR_MSG_REFLECTION_NO_SUCH_METHOD, true);
        }
        catch (IllegalAccessException e)
        {
            Assert.assertFalse(TestConstants.ERR_MSG_ILLEGAL_ACCESS, true);
        }
        catch (InvocationTargetException e)
        {
            Assert.assertFalse(TestConstants.ERR_MSG_INVOCATION_TARGET, true);
        }

        //Clean up
        try
        {
            Thread.sleep(5000); // TO visualize directory exists
            FileUtils.deleteDirectory(new File(newFilePath));
        }
        catch (IOException | InterruptedException e)
        {
            //Ignore
        }
    }

    @Test
    public void testProcessImage()
    {
        String newFilePath = filePath.replace("temp", "temp_new");
        try
        {
            ImageStorageHelper constr = new ImageStorageHelper(filePath, newFilePath);
            constr.processImages();
        }
        catch (ImageStorageException e)
        {
            Assert.assertEquals(ErrConstants.ERR_CODE_IO_IMAGE_PROCESSING, e.getErrorCode());
        }

        // Verify with test image
        File imgFile = new File(newFilePath+"/2021-03-28 14_08.jpeg");
        if (imgFile.exists() == true)
        {
            Assert.assertTrue(true);
        }
        else
        {
            Assert.assertFalse(TestConstants.ERR_MSG_FILE_NOT_EXISTS, true);
        }

        //Clean up
        try
        {
            Thread.sleep(5000); // TO visualize directory exists
            imgFile.delete();
        }
        catch (InterruptedException e)
        {
            //Ignore
        }
    }

}
