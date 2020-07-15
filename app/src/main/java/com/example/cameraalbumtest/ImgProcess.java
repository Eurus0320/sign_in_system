package com.example.cameraalbumtest;

import android.util.Log;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class ImgProcess {
    int bitWidth;
    String res = "";
    int width;
    /*
     * @param data Y of YUV
     * @return bits 01
     */


    public ImgProcess(byte[] data, int height, int width, int scanFreq, int LEDFreq){
       /* File file1 = new File(Environment.getExternalStorageDirectory(), "ImgData.doc");
        File file2 = new File(Environment.getExternalStorageDirectory(), "ImgValid.doc");
        String fileName = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera";
        if (file1.exists())
            file1.delete();
        if (file2.exists())
            file2.delete();
        try {
            file1.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file1);
            // 获取BufferedOutputStream对象
            BufferedOutputStream  bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(data);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*byte[] reverse = new byte[height * width];
        for (int i = 0; i < height ; i++)
        {
            for (int j = 0; j < width; j++)
            {
                reverse[i * width + j] = data[j * height + i];
            }
        }
        Mat ttt = new Mat(height, width, CvType.CV_8UC1);
        ttt.put(0, 0, reverse);

        File localFile4 = new File (fileName + "OUTPUTOrigin.jpeg");
        Bitmap mBitmap4 = null;
        mBitmap4 = Bitmap.createBitmap(ttt.cols(), ttt.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(ttt, mBitmap4);
        FileOutputStream fileOutputStream4 = null;
        try{
            fileOutputStream4 = new FileOutputStream(localFile4);
            mBitmap4.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream4);
            fileOutputStream4.flush();
            fileOutputStream4.close();

            Log.d("test IMGProcess", "图片已保存至本地");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        //Log.i("test IMGProcess", Arrays.toString(data));
        //Log.i("test IMGProcess", "Start");
        //Log.i("test IMGProcess", Integer.toString(LEDFreq));
        this.width = width;
        this.bitWidth = (int)(1.6 * scanFreq * width  / LEDFreq);
        //Log.i("test IMGProcess", "bitWidth Get");
        ArrayList<Byte> valid = FindVaildLine(data, width, height);
        System.out.print(valid);
        //Log.i("test IMGProcess", "valid Get");
        int length = valid.size() / width;

        byte [] newdata = new byte[length * height];
        Mat mainData = new Mat(length, width, CvType.CV_8UC1);
        for (int i = 0; i < length * width; i++) {
            byte tmp = valid.get(i);
            newdata[i] = tmp;
        }
        mainData.put(0, 0, newdata);
        Judge(mainData);
        //Mat HPF2 = HPFfilter(mainData);
        Mat mainData2 = new Mat(length, width, CvType.CV_8UC1);
        Imgproc.equalizeHist(mainData, mainData2);

        /*byte[] Equalized = HistEqualize(valid, width, length);
        try {
            file2.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file2);
            // 获取BufferedOutputStream对象
            BufferedOutputStream  bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(Equalized);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //Log.i("test IMGProcess", "Equalized Get");
        /*Mat img = new Mat(length, width, CvType.CV_8UC1);
        img.put(0, 0, Equalized);
        Log.i("test IMGProcess", "Mat Get");

        File localFile = new File (fileName + "OUTPUTValid.jpeg");
        File localFile2 = new File (fileName + "OUTPUTEqual.jpeg");
        Bitmap mBitmap1 = null;
        mBitmap1 = Bitmap.createBitmap(mainData.cols(), mainData.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mainData, mBitmap1);
        Bitmap mBitmap2 = null;
        mBitmap2 = Bitmap.createBitmap(mainData2.cols(), mainData2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mainData2, mBitmap2);
        FileOutputStream fileOutputStream = null;
        FileOutputStream fileOutputStream2 = null;
        try{
            fileOutputStream = new FileOutputStream(localFile);
            fileOutputStream2 = new FileOutputStream(localFile2);
            mBitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            mBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream2);
            fileOutputStream.flush();
            fileOutputStream.close();
            fileOutputStream2.flush();
            fileOutputStream2.close();

            Log.d("test IMGProcess", "图片已保存至本地");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Judge(mainData2);
        */
        Mat HPF = HPFfilter(mainData2);
       /* for (int i = 0; i < HPF.rows(); i++) {
            byte[] ToSovled = new byte[HPF.cols()];
            HPF.get(i, 0, ToSovled);
            //System.out.println(Arrays.toString(ToSovled));
        }*/


        /*File localFile3 = new File (fileName + "OUTPUTHPF.jpeg");
        Bitmap mBitmap3 = null;
        mBitmap3 = Bitmap.createBitmap(mainData.cols(), mainData.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(HPF, mBitmap3);
        FileOutputStream fileOutputStream3 = null;
        try{
            fileOutputStream3 = new FileOutputStream(localFile3);
            mBitmap3.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream3);
            fileOutputStream3.flush();
            fileOutputStream3.close();

            Log.d("test IMGProcess", "图片已保存至本地");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File localFile5 = new File (fileName + "OUTPUTHPF2.jpeg");
        Bitmap mBitmap5 = null;
        mBitmap5 = Bitmap.createBitmap(mainData.cols(), mainData.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(HPF2, mBitmap5);
        FileOutputStream fileOutputStream5 = null;
        try{
            fileOutputStream5 = new FileOutputStream(localFile5);
            mBitmap3.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream5);
            fileOutputStream5.flush();
            fileOutputStream5.close();

            Log.d("test IMGProcess", "图片已保存至本地");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("test IMGProcess", "HPF Get");
        //Judge(HPF);
        //Judge(HPF2);
        */
    }

    /*public String Process(byte[] data, int height, int width, int scanFreq, int LEDFreq){

        this.bitWidth = scanFreq * width / LEDFreq;
        Log.i("test IMGProcess", "bitWidth Get");
        ArrayList<Byte> valid = FindVaildLine(data, width, height);
        Log.i("test IMGProcess", "valid Get");
        int length = valid.size() / width;
        byte[] Equalized = HistEqualize(valid, width, length);

        Mat img = new Mat(length, width, CvType.CV_8UC1);
        img.put(0, 0, Equalized);
        Mat HPF = HPFfilter(img);

        return Judge(HPF);
    }*/

    private ArrayList<Byte> FindVaildLine(byte[] data, int width, int height){
        int i, j;
        ArrayList<Byte> valid = new ArrayList<>();
        for (i = 0; i < height ; i++)
        {
            int sum = 0;
            for (j = 0; j < width; j++)
            {
                if (data[j * height + i] == (byte)0xFF)
                    break;
                sum += data[j * height + i] & 0xFF;
            }
            if (j == width)
            {
                if (sum / (float)width >= 1.0)
                {
                    for (j = 0; j < width; j++)
                    {
                        valid.add(data[j * height + i]);
                    }
                }
            }
        }
        return valid;
    }

    private byte[] HistEqualize(ArrayList<Byte> valid, int width, int length){
        int i, j;
        int[] histogram = new int[256];
        for (i = 0 ; i < length; i++)
            for (j = 0; j < width; j++)
            {
                int grey = valid.get(j + i * width) & 0xff;
                histogram[grey]++;
            }
        double[] prSum = new double[256];
        prSum[0] =  (double) histogram[0] / width * length;
        for (i = 1; i < 256; i++)
        {
            prSum[i] = prSum[i - 1] + (double)histogram[i] / width * length;
        }

        byte[] Equalized = new byte[length * width];
        for (i = 0; i < length; i++) {
            for (j = 0; j < width; j++) {
                Equalized[j + i * width] = (byte) (255 * prSum[valid.get(j + i * width) & 0xff]);
            }
        }
        return Equalized;
    }

    private Mat HPFfilter(Mat img)
    {
        //Optimal Size
        Mat padded = new Mat();
        int addPixelRows = Core.getOptimalDFTSize(img.rows());
        int addPixelCols = Core.getOptimalDFTSize(img.cols());
        Core.copyMakeBorder(img, padded, 0, addPixelRows - img.rows(), 0, addPixelCols - img.cols(), Core.BORDER_CONSTANT, Scalar.all(0));
     //   Log.i("test IMGProcess", "1 Get");
        padded.convertTo(padded, CvType.CV_32F);
        List<Mat> planes = new ArrayList<Mat>();
        Mat complexImage = new Mat();
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        //dft
        Core.merge(planes, complexImage);
       // Log.i("test IMGProcess", "2 Get");
        Core.dft(complexImage, complexImage);
       // Log.i("test IMGProcess", "3 Get");
        //Set HPF
        Mat mag = new Mat(complexImage, new Rect(0, 0, complexImage.cols() & -2, complexImage.rows() & -2));
        int n = 2;
        double D0 = 5.0;
        //shift
        int cx = mag.cols() / 2;
        int cy = mag.rows() / 2;
        Mat tmp = new Mat();
        Mat q0 = new Mat(mag, new Rect(0, 0, cx , cy));
        Mat q1 = new Mat(mag, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(mag, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(mag, new Rect(cx, cy, cx, cy));
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
       // Log.i("test IMGProcess", "4 Get");
        //HPF
        double h;
        double[] value;
        for (int y = 0; y < mag.rows(); y++)
        {
            for (int x = 0; x < mag.cols(); x++)
            {
                double d = sqrt(pow((y - cy), 2) + pow((x - cx), 2));
                if (d == 0)
                    h = 0.0;
                else
                    h = 1.0 / (1.0 + pow((D0 / d), 2 * n));
                value = mag.get(y, x);
                value[0] *= h;
                mag.put(y, x, value);
            }
        }
        Log.i("test IMGProcess", "5 Get");
        //inverse shift
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
        //idft
        Mat invDFT = new Mat();
        Core.idft(mag, invDFT, Core.DFT_SCALE | Core.DFT_REAL_OUTPUT, 0);
        Mat BluredImage = new Mat();

        invDFT.convertTo(BluredImage, CvType.CV_8U, 1, 128);
        return new Mat(BluredImage, new Rect(0, 0, img.cols(), img.rows()));
    }

    private void Judge(Mat HPF) {
        int[] num = new int[HPF.cols()];
        for (int i = 0; i < HPF.rows(); i++)
        {
            byte[] ToSovled = new byte[HPF.cols()];
            HPF.get(i, 0, ToSovled);
            final WeightedObservedPoints obs = new WeightedObservedPoints();
            for (int j = 0; j < HPF.cols(); j++)
            {
                obs.add(j + 1, ToSovled[j] & 0xff);
            }

            final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);

            final double[] coeff = fitter.fit(obs.toList());

            for (int j = 0; j < HPF.cols(); j++)
            {
                double fitted = coeff[0] + coeff[1] * (j+1) + coeff[2] * pow(j + 1, 2);
                if (fitted < ToSovled[j]) {
                    num[j]++;
                }
            }
        }
       // Log.i("test IMGProcess", "6 Get");
        char[] bits = new char[HPF.cols()];
      //  Log.i("test IMGProcess", Integer.toString(HPF.rows()));
        for (int j = 0; j < HPF.cols(); j++)
        {
            if (num[j] > HPF.rows() * 1 / 2)
                bits[j] = '1';
            else
                bits[j] = '0';
        }

        String s = String.copyValueOf(bits);
      //  Log.i("test IMGProcess", s);

        StringBuilder tmp = new StringBuilder();
        char t = '2';
        int count = 0;
        boolean flag = false;
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) != t)
            {
                if (t != '2')
                {
                    if (flag) {
                        if (count > this.bitWidth / 2) {
                            int NumLight = count / this.bitWidth;
                            if (count - this.bitWidth * NumLight > this.bitWidth * (NumLight + 1) - count)
                                NumLight++;
                            for (int j = 0; j < NumLight; j++)
                                tmp.append(t);
                        }
                    }
                    flag = true;
                }

                t = s.charAt(i);
                count = 1;
            }
            else
                count++;
        }
        this.res = tmp.toString();
       // Log.i("test IMGProcess", this.res);
    }

    /*private String Judge2(Mat HPF) {
        int[] num = new int[HPF.cols()];
        for (int i = 0; i < HPF.rows(); i++)
        {
            byte[] ToSovled = new byte[HPF.cols()];
            HPF.get(i, 0, ToSovled);
            final WeightedObservedPoints obs = new WeightedObservedPoints();
            for (int j = 0; j < HPF.cols(); j++)
            {
                obs.add(j + 1, ToSovled[j] & 0xff);
            }

            final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);

            final double[] coeff = fitter.fit(obs.toList());

            for (int j = 0; j < HPF.cols(); j++)
            {
                double fitted = coeff[0] + coeff[1] * (j+1);
                if (fitted < ToSovled[j]) {
                    num[j]++;
                }
            }
        }
        Log.i("test IMGProcess", "6 Get");
        char[] bits = new char[HPF.cols()];
        Log.i("test IMGProcess", Integer.toString(HPF.rows()));
        for (int j = 0; j < HPF.cols(); j++)
        {
            if (num[j] > HPF.rows() * 1 / 2)
                bits[j] = '1';
            else
                bits[j] = '0';
        }

        String s = String.copyValueOf(bits);
        Log.i("test IMGProcess", s);
        StringBuilder rep = new StringBuilder("0");
        while(s.contains(rep.toString() + '0'))
        {
            rep.append('0');
        }
        this.bitWidth = rep.length();
        int ones, begin = s.indexOf(rep.toString()) % this.bitWidth;
        Log.i("test IMGProcess", Integer.toString(begin));
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; begin + i * this.bitWidth < HPF.cols(); i++ )
        {
            ones = 0;
            for (int j = begin + i * this.bitWidth; j < begin + (i + 1) * this.bitWidth && j < HPF.cols(); j++ )
            {
                ones += bits[j] - '0';
            }
            tmp.append(ones > this.bitWidth / 2 ? '1' : '0');
        }
        this.res = tmp.toString();
        Log.i("test IMGProcess", this.res);
        return res;
    }*/

    boolean judge(String inputStr){
        int count = 0, index = 0;
        StringBuilder tmp;
        if (this.res.charAt(0) == '1') {
            tmp = new StringBuilder(inputStr);
            inputStr = tmp.reverse().toString();
        }
        //Log.i("test IMGProcess", inputStr);
        int t = 0, pre = 0;
        char curr = ' ';
        for (int i = 0; i < this.res.length();) {
            if (t == 0)
            {
                curr = this.res.charAt(i);
                pre = i;
                t ++;
            }
            else if (this.res.charAt(i) != curr && t == 1)
            {
                curr = this.res.charAt(i);
                t++;
            }
            else if (this.res.charAt(i) != curr && t == 2) {
                curr = this.res.charAt(i);
                t--;
                if (this.res.substring(pre, i).equals(inputStr))
                    count++;
                pre = i;
            }
            i++;
        }
       Log.i("test IMGProcess", Integer.toString(count));
        return count > this.res.length() / (inputStr.length() * 2);
    }
}
