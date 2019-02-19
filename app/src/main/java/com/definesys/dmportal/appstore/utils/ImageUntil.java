package com.definesys.dmportal.appstore.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;

/**
 * Created by 羽翎 on 2018/9/26.
 */

public class ImageUntil {
    public static String currentUUID;



    //圆角bitmap
    public static Bitmap getOvalBitmap(Bitmap bitmap){

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 保存文件的方法
     * @param code
     * @param context
     * @param mode 0.保存到缓存 1.保存到相册 2.保存到指定目录 3.保存头像
     * @return
     * @throws ParseException
     */
    public static String  saveBitmapFromView(Bitmap code,String picName, Context context,int mode) {
        String path = null;
        Bitmap bmp = null;
        Bitmap scaleBmp = null;
        try {
            bmp = code;
            // 缩小图片
            Matrix matrix = new Matrix();
            matrix.postScale(0.5f, 0.5f); //长和宽放大缩小的比例
            scaleBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            bmp = null;

            path = saveBitmap(scaleBmp, picName+".jpg", context, mode);

            scaleBmp.recycle();
            scaleBmp = null;

        } catch (Exception e) {
            throw e;
        } finally {
            if (bmp != null && !bmp.isRecycled()) {
                bmp = null;
            }

            if (scaleBmp != null && !scaleBmp.isRecycled()) {
                scaleBmp.recycle();
                scaleBmp = null;
            }
        }

        return path;
    }

/*
 * 保存文件，文件名为当前日期
 */

    private static String saveBitmap(Bitmap bitmap, String bitName, Context context, int  mode){
        String fileName ="" ;
        File file ;
        if( mode==0)
            fileName=context.getCacheDir()+"/"+bitName;
        else if(mode==1){
            if(Build.BRAND .equals("Xiaomi") ){ // 小米手机
                fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/"+bitName ;
            }else{  // Meizu 、Oppo
                fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
            }
        }else if(mode==2){
            if(!new File(context.getFilesDir()+"/leaveImages/").exists()){
                new File(context.getFilesDir()+"/leaveImages/").mkdir();
            }
            fileName=context.getFilesDir()+"/leaveImages/"+bitName;
        }else if(mode==3){
            fileName=context.getCacheDir()+"/"+bitName;
        }

        file = new File(fileName);
        boolean isDelete=true;
        if(file.exists()) {
            context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{fileName});//删除系统缩略图
            isDelete = file.delete();//删除SD中图片
        }
        if(isDelete) {
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                    // 插入图库
                    if (mode != 0)
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null);

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }
            if (mode != 0)// 发送广播，通知刷新图库的显示
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

        }
        return file.getPath();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public static String getUUID(String path){
        String []temps = path.split("/");
        return temps[temps.length-1].split("\\.")[0];
    }

    /**
     * 获取view的bitmap
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        Bitmap bitmap= Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

    }

    /** * 将图片存到本地 */
    public static File saveBitmap(Bitmap bm, String picName) {
        try {
            String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/leavePic/"+picName+".jpg";
            File f = new File(dir);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

            return f;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static File viewToPdf(View view,String fileName){
        PdfDocument document = new PdfDocument();//1, 建立PdfDocument
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                view.getMeasuredWidth(), view.getMeasuredHeight(), 1).create();//2
        PdfDocument.Page page = document.startPage(pageInfo);
        view.draw(page.getCanvas());//3
        document.finishPage(page);//4
        File file = null;
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/leavePic/"+fileName+".pdf";
            file=new File(path);
            if (file.exists()) {
                file.delete();
            }
            document.writeTo(new FileOutputStream(file));
            document.close();//5
        } catch (IOException e) {
            e.printStackTrace();
            document.close();//5
            return null;
        }
        return file;
    }

}
