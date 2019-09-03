package com.cytyk.wordserver.common;

import com.aspose.words.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

@Slf4j
public class Converter {

    public static void main(String[] args) throws Exception {
        pdf2Image("/Users/yuzu/temp/2.pdf",
                "/Users/yuzu/temp/3.png", 300, true);
    }

    public static boolean getLicense() throws Exception {
        boolean result = false;
        try {

            InputStream is = com.aspose.words.Document.class
                    .getResourceAsStream("/com.aspose.words.lic_2999.xml");
            License aposeLic = new License();
            if (!aposeLic.isLicensed()) {
                aposeLic.setLicense(is);
            }
            result = true;
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void doc2pdf(String inPath, String outPath) throws Exception {
        // 验证License 若不验证则转化出的pdf文档有水印
        if (!getLicense()) {
            throw new Exception("com.aspose.words lic ERROR!");
        }

        System.out.println(inPath + " -> " + outPath);

        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath);
            FileOutputStream os = new FileOutputStream(file);
            // word文档
            Document doc = new Document(inPath);
            // 支持RTF HTML,OpenDocument, PDF,EPUB, XPS转换
            doc.save(os, SaveFormat.PDF);
            long now = System.currentTimeMillis();
            System.out.println("convert OK! " + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doc2html(String inPath, String outPath) throws Exception {
        // 验证License 若不验证则转化出的pdf文档有水印
        if (!getLicense()) {
            throw new RuntimeException("com.aspose.words lic ERROR!");
        }

        System.out.println(inPath + " -> " + outPath);

        try {
            long old = System.currentTimeMillis();
            Document doc = new Document(inPath);

            HtmlSaveOptions options = new HtmlSaveOptions();
            options.setExportRoundtripInformation(true);
            doc.save(outPath, options);

            long now = System.currentTimeMillis();
            System.out.println("convert OK! " + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doc2htmlWithText(String inPath, String outPath, StringBuilder docText) throws Exception {
        // 验证License 若不验证则转化出的pdf文档有水印
        if (!getLicense()) {
            throw new RuntimeException("com.aspose.words lic ERROR!");
        }

        System.out.println(inPath + " -> " + outPath);

        try {
            long old = System.currentTimeMillis();
            Document doc = new Document(inPath);

            HtmlSaveOptions options = new HtmlSaveOptions();
            options.setExportRoundtripInformation(true);
            doc.save(outPath, options);
            long now = System.currentTimeMillis();
            log.info("convert OK! " + ((now - old) / 1000.0) + "秒");
            if (docText != null) {
                docText.append(doc.getFirstSection().getBody().getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doc2Image(String inPath, String outPath) throws Exception {
        // 验证License 若不验证则转化出的pdf文档有水印
        if (!getLicense()) {
            throw new Exception("com.aspose.words lic ERROR!");
        }

        InputStream in = new FileInputStream(inPath);
        OutputStream out = new FileOutputStream(outPath);

        Document doc = new Document(in);

        // Save the finished document to disk.
        doc.save(out, SaveFormat.PNG);
    }

    public static void doc2Image2(String inPath, String outPath) throws Exception {
        // 验证License 若不验证则转化出的pdf文档有水印
        if (!getLicense()) {
            throw new Exception("com.aspose.words lic ERROR!");
        }
        //ExStart:SetHorizontalAndVerticalImageResolution
        com.aspose.words.Document doc = new com.aspose.words.Document(inPath);

        //Renders a page of a Word document into a PNG image at a specific horizontal and vertical resolution.
        ImageSaveOptions options = new ImageSaveOptions(com.aspose.words.SaveFormat.PNG);

        options.setHorizontalResolution(600);
        options.setVerticalResolution(600);
        options.setPageCount(2);
        doc.save(outPath, options);
        //ExEnd:SetHorizontalAndVerticalImageResolution
    }

    /***
     * PDF文件转PNG图片，全部页数
     *
     * @param inPath pdf完整路径
     * @param outPath 图片存放完整路径
     * @param dpi dpi越大转换后越清晰，相对转换速度越慢
     * @Param isCrop 裁剪白色边框
     */
    private static boolean pdf2Image(String inPath, String outPath, int dpi, boolean isCrop) throws IOException {
        File file = new File(inPath);
        PDDocument pdDocument = null;
        try {
            if (createDirectory(new File(outPath).getParent())) {

                pdDocument = PDDocument.load(file);
                PDFRenderer renderer = new PDFRenderer(pdDocument);
                /* dpi越大转换后越清晰，相对转换速度越慢 */
                PDPageTree pageTree = pdDocument.getPages();
                ArrayList<BufferedImage> images = new ArrayList<>(1);
                int width = 0;
                int height = 0;
                for (int i = 0; i < pageTree.getCount(); i++) {
                    BufferedImage image = renderer.renderImageWithDPI(i, dpi);
                    // 先剪切图片的上下边
                    if (isCrop) {
                        image = cropImageTopAndBottom(image);
                        if (image == null) {
                            continue;
                        }
                    }
                    images.add(image);

                    width = image.getWidth();
                    height += image.getHeight();
                }

                BufferedImage finalImage;
                if (pageTree.getCount() > 1) {
                    // 多张图片，需要拼接
                    finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                    Graphics g = finalImage.createGraphics();

                    height = 0;
                    for (BufferedImage bImage : images) {
                        g.drawImage(bImage, 0, height, width, bImage.getHeight(), null);
                        height += bImage.getHeight();
                    }
                    // 释放此图形的上下文以及它使用的所有系统资源。
                    g.dispose();
                } else {
                    if (images.isEmpty()) {
                        throw new IOException("文件内容为空");
                    }
                    finalImage = images.get(0);
                }

                // 再剪裁左右空白区域
                if (isCrop) {
                    finalImage = cropImageLeftAndRight(finalImage);
                    if (finalImage == null) {
                        return false;
                    }
                }

                ImageIO.write(finalImage, "png", new File(outPath));
                System.out.println("PDF文档转PNG图片成功！" + outPath);
                return true;
            } else {
                System.out.println("PDF文档转PNG图片失败：" + "创建" + outPath + "失败");
                return false;
            }
        } finally {
            if (pdDocument != null) {
                try {
                    pdDocument.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean pdf2ImageCroped(String inPath, String outPath) throws IOException {
        return pdf2Image(inPath, outPath, 300, true);
    }

    /**
     * 裁剪空白边框，留出5个像素边框
     */
    private static BufferedImage cropImageTopAndBottom(BufferedImage bufferedImage) {
        int top = getTop(bufferedImage);
        if (top < 0) {
            return null;
        }
        int bottom = getBottom(bufferedImage);
        if (bottom < 0 || bottom <= top) {
            return null;
        }
        int left = 0;

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int subTop = top - 5 > 0 ? top - 5 : top;

        int subHeight = bottom + 11 <= height ? bottom - subTop + 11 : bottom - subTop;
        return bufferedImage.getSubimage(left, subTop, width, subHeight);
    }

    private static BufferedImage cropImageLeftAndRight(BufferedImage bufferedImage) {
        int left = getLeft(bufferedImage);
        if (left < 0) {
            return null;
        }
        int right = getRight(bufferedImage);
        if (right < 0 || right <= left) {
            return null;
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int subTop = 0;
        int subLeft = left - 5 > 0 ? left - 5 : left;

        int subWidth = right + 11 <= width ? right - subLeft + 11 : right - subLeft;
        return bufferedImage.getSubimage(subLeft, subTop, subWidth, height);
    }

    private static int getTop(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int x = 0; x < height; x++) {
            boolean isblank = true;
            for (int y = 0; y < width && isblank; y++) {
                if (bufferedImage.getRGB(y, x) != -1) {
                    isblank = false;
                }
            }
            if (!isblank) {
                return x;
            }
        }
        return -1;
    }

    private static int getBottom(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int x = height - 1; x >= 0; x--) {
            boolean isblank = true;
            for (int y = width - 1; y >= 0; y--) {
                if (bufferedImage.getRGB(y, x) != -1) {
                    isblank = false;
                }
            }
            if (!isblank) {
                return x;
            }
        }
        return -1;
    }

    private static int getRight(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int y = width - 1; y >= 0; y--) {
            boolean isblank = true;
            for (int x = height - 1; x >= 0; x--) {
                if (bufferedImage.getRGB(y, x) != -1) {
                    isblank = false;
                }
            }
            if (!isblank) {
                return y;
            }
        }
        return -1;
    }

    private static int getLeft(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int y = 0; y < width; y++) {
            boolean isblank = true;
            for (int x = 0; x < height && isblank; x++) {
                if (bufferedImage.getRGB(y, x) != -1) {
                    isblank = false;
                }
            }
            if (!isblank) {
                return y;
            }
        }
        return -1;
    }


    private static boolean createDirectory(String folder) {
        File dir = new File(folder);
        if (dir.exists()) {
            return true;
        } else {
            return dir.mkdirs();
        }
    }


//    public void ImageTset() throws Exception {
//        //创建四个文件对象（注：这里四张图片的宽度都是相同的，因此下文定义BufferedImage宽度就取第一只的宽度就行了）
//        File _file1 = new File("1.jpg");
//        File _file2 = new File("2.jpg");
//        File _file3 = new File("3.jpg");
//        File _file4 = new File("4.jpg");
//
//        BufferedImage src1 = javax.imageio.ImageIO.read(_file1);
//        BufferedImage src2 = javax.imageio.ImageIO.read(_file2);
//        BufferedImage src3 = javax.imageio.ImageIO.read(_file3);
//        BufferedImage src4 = javax.imageio.ImageIO.read(_file4);
//
//        //获取图片的宽度
//        int width = src1.getWidth(null);
//        //获取各个图像的高度
//        int height1 = src1.getHeight(null);
//        int height2 = src2.getHeight(null);
//        int height3 = src3.getHeight(null);
//        int height4 = src4.getHeight(null);
//
//        //构造一个类型为预定义图像类型之一的 BufferedImage。 宽度为第一只的宽度，高度为各个图片高度之和
//        BufferedImage tag = new BufferedImage(width, height1 + height2 + height3 + height4, BufferedImage.TYPE_INT_RGB);
//        //创建输出流
//        FileOutputStream out = new FileOutputStream("treasureMap.jpg");
//        //绘制合成图像
//        Graphics g = tag.createGraphics();
//        g.drawImage(src1, 0, 0, width, height1, null);
//        g.drawImage(src2, 0, height1, width , height2, null);
//        g.drawImage(src3, 0, height1 + height2, width, height3, null);
//        g.drawImage(src4, 0, height1 + height2 + height3, width, height4, null);
//        // 释放此图形的上下文以及它使用的所有系统资源。
//        g.dispose();
//        //将绘制的图像生成至输出流
//        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//        encoder.encode(tag);
//        //关闭输出流
//        out.close();
//        System.out.println("藏宝图出来了");
//    }

}
