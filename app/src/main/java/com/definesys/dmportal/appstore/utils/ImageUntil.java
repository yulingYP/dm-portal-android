package com.definesys.dmportal.appstore.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * Created by 羽翎 on 2018/9/26.
 */

public class ImageUntil {

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
     * @param suore 图片
     * @param context c
     * @param mode 0.保存到缓存且回收bitmap 1.保存到相册且回收bitmap 2.保存到指定目录 3.保存到缓存不回收bitmap
     * @return 路径
     */
    public static String saveBitmapFromView(Bitmap suore,String picName, Context context,int mode) {
        String path =null;
        Bitmap bmp = null;
        Bitmap scaleBmp = null;
        try {
            bmp = suore;
            // 缩小图片
            Matrix matrix = new Matrix();
            float s = 1.0f;//缩放比例
            matrix.postScale(s, s); //长和宽放大缩小的比例
            scaleBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            path = saveBitmap(scaleBmp, picName+".jpg", context, mode);

        } catch (Exception e) {
            Log.d("mydemo",e.toString());
        }
        finally {
            if(mode!=3) {//回收bitmap
                if (bmp != null && !bmp.isRecycled()) {
                    bmp.recycle();
                }

                if (scaleBmp != null && !scaleBmp.isRecycled()) {
                    scaleBmp.recycle();
                }
            }
        }

        return path;
    }

/*
 * 保存文件，文件名为当前日期
 */

    private static String saveBitmap(Bitmap bitmap, String bitName, Context context, int  mode){
        String fileName="" ;
        File file ;
      if(mode==1){//保存到相册
            if(Build.BRAND .equals("Xiaomi") ){ // 小米手机
                fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/"+bitName ;
            }else{  // Meizu 、Oppo
                fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/"+bitName ;
            }
        }else if(mode==2){//保存到指定路径
          boolean flag = true;
            if(!new File(context.getFilesDir()+"/leaveImages/").exists()){
               flag= new File(context.getFilesDir()+"/leaveImages/").mkdir();
            }
            if(flag)
                fileName=context.getFilesDir()+"/leaveImages/"+bitName;
        }else {//保存到缓存
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
//                Bitmap.CompressFormat format=mode==4?Bitmap.CompressFormat.PNG:Bitmap.CompressFormat.JPEG;
                Bitmap.CompressFormat format=Bitmap.CompressFormat.JPEG;
                int quality =  100;
                if (bitmap.compress(format,quality , out)) {
                    out.flush();
                    out.close();
                    // 插入图库
                    if (mode != 0&&mode!=3)
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null);

                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            if (mode != 0&&mode!=3)// 发送广播，通知刷新图库的显示
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

        }
        return file.getPath();
    }

    /**
     * 获取view的bitmap
     * @param view v
     * @return b
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
            String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/leavePic/";
            File f = new File(dir);
            boolean flag = true;
            if (!f.exists()) {//不存在此文件夹
                flag=f.mkdir();//创建
            }
            if(flag){//文件夹存在或创建文件夹成功
                //创建图片
                f= new File(dir+picName+".jpg");
            }
            if(f.exists()){//图片已存在？
                //删除
                flag= f.delete();
            }
            if(flag) {//图片不存在或删除成功
                FileOutputStream out = new FileOutputStream(f);
                bm.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }

            return f;
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
        File file;
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/leavePic/"+fileName+".pdf";
            file=new File(path);
            boolean flag = true;
            if (file.exists()) {
                flag=file.delete();
            }
            if(flag) {
                document.writeTo(new FileOutputStream(file));
                document.close();//5
            }
        } catch (IOException e) {
            e.printStackTrace();
            document.close();//5
            return null;
        }
        return file;
    }

}
