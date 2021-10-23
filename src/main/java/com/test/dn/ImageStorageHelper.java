package com.test.dn;

import com.test.dn.constants.Constants;
import com.test.dn.constants.ErrConstants;
import com.test.dn.exceptions.ImageStorageException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Logger;

public class ImageStorageHelper
{
    private static final Logger LOG = Logger.getLogger(ImageStorageHelper.class.getName());

    private String dirPath;
    private String newDirPath;

    private ImageStorageHelper()
    {
        // do nothing
    }

    public ImageStorageHelper(final String dirPath) throws ImageStorageException
    {
        //validate file
        final File dir = new File(dirPath);
        if (dir.isDirectory() == false)
        {
            throw new ImageStorageException(ErrConstants.ERR_CODE_NOT_DIRECTORY, ErrConstants.ERR_MSG_NOT_DIRECTORY);
        }
        this.dirPath = dirPath;
    }

    public ImageStorageHelper(final String dirPath, final String newDirPath) throws ImageStorageException
    {
        //validate file
        final File dir = new File(dirPath);
        if (dir.isDirectory() == false)
        {
            throw new ImageStorageException(ErrConstants.ERR_CODE_NOT_DIRECTORY, ErrConstants.ERR_MSG_NOT_DIRECTORY);
        }

        // validate new directory
        isDirectoryExists(newDirPath);

        this.dirPath = dirPath;
        this.newDirPath = newDirPath;
    }

    private void isDirectoryExists(String newDirPath) throws ImageStorageException
    {
        final File newDir = new File(newDirPath);
        if (newDir.exists() == false)
        {
            try
            {
                newDir.mkdir();
                LOG.info(String.format("Creating new Directory on Path : %s", newDirPath));
            }
            catch (Exception ex)
            {
                throw new ImageStorageException(ErrConstants.ERR_CODE_UNABLE_TO_CREATE_DIRECTORY,
                        ErrConstants.ERR_MSG_UNABLE_TO_CREATE_DIRECTORY, ex);
            }
        }
    }

    /**
     *  It reads input directory JPEG image's metadata and obtains creation date
     *  And then It will persist new image with timestamp label in case of different output directory
     *  Else it will rename the existing image in same directory
     *
     * @throws ImageStorageException
     */
    public void processImages() throws ImageStorageException
    {
        final File dir = new File(this.dirPath);
        for (final File img: dir.listFiles())
        {
            try
            {
                final ImageMetadata im = Imaging.getMetadata(img);
                TiffImageMetadata curIM = null;

                if (im == null)
                {
                    throw new ImageStorageException(ErrConstants.ERR_CODE_NO_IMAGE_METADATA_FOUND,
                            ErrConstants.ERR_MSG_NO_IMAGE_METADATA_FOUND);
                }

                if (im instanceof JpegImageMetadata) {
                    curIM = ((JpegImageMetadata) im).getExif();
                }
                else
                {
                    continue;
                }
                final TiffField dtField = curIM.findField(TiffTagConstants.TIFF_TAG_DATE_TIME);

                LOG.info(String.format("Image Name : %s And Created On : %s", img.getName(), dtField.getValue().toString()));

                if (dtField.getValue().toString() != null)
                {
                    saveImage(img, dtField);
                }
            }
            catch (ImageReadException | IOException | ParseException e)
            {
                throw new ImageStorageException(ErrConstants.ERR_CODE_IO_IMAGE_PROCESSING,
                        ErrConstants.ERR_MSG_IO_IMAGE_PROCESSING, e);
            }
        }
    }

    /**
     *  To save image with specific naming format ex. yyyy-MM-dd HH:ss
     *
     * @param img
     * @param dtField
     * @throws ImageReadException
     * @throws ParseException
     * @throws IOException
     */
    private void saveImage(final File img, final TiffField dtField) throws ImageReadException, ParseException, IOException
    {
        // In my scenario I found date 2019:08:23 23:19:10
        final Date imgTime = Constants.IN_DATE_FORMAT.parse(dtField.getValue().toString());

        // you can not save file on windows with name contains : so replaced with _
        final String fmt = Constants.OP_DATE_FORMAT.format(imgTime).replace(":", "_");

        if (this.newDirPath != null)
        {
            final File newFile = new File(this.newDirPath+"/"+fmt+".jpeg");
            FileUtils.copyFile(img, newFile);
            return;
        }
        else
        {
            img.renameTo(new File(this.dirPath+"/"+fmt+".jpeg"));
            return;
        }
    }


    public static void main(String args[])
    {
        /**
         * scenarios :
         *  1> rename files in same directory
         *    a> if someone creating new files will it impact on existing processing
         *    b>
         *  2> take output directory location
         *    a> If provided directory not exists
         *  3> Iterate each image from directory (condition 1)
         *  4> Read Exif from each image
         *  5> Rename Image object
         *  6> Save image to dir (condition 1 and 2)
         */

        try
        {
            String filePath = ImageStorageHelper.class.getClassLoader().getResource("")
                    .getPath().replace("target/classes/", "");
            ImageStorageHelper helper = new ImageStorageHelper(filePath+"temp", filePath+"temp_new");
            helper.processImages();
        }
        catch (ImageStorageException e)
        {
            e.printStackTrace();
        }
    }
}
