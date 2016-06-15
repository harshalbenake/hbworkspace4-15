package utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contain all general purpose methods.
 * Created by <b>Harshal Benake</b> on 24/08/15.
 */
public class Utility {

    /**
     * Returns font type used by the applicaion.
     *
     * @return
     */
    public Typeface getFontTypeface(Context mContext) {
        return Typeface.createFromAsset(mContext.getAssets(), "font/MyriadPro-Regular.otf");
    }

    /**
     * This method is used to get installed application info.
     *
     * @param mContext mContext
     * @return ArrayList<PackageInfo>
     */
    public ArrayList<PackageInfo> getInstalledApps(Context mContext) {
        ArrayList<PackageInfo> res = new ArrayList<PackageInfo>();
        List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages(0);
        String currentPackageName = mContext.getPackageName();
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);

            ApplicationInfo ai = p.applicationInfo;

            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // System app - do something here
            } else {
                // User installed app?
                if (p.packageName.equalsIgnoreCase(currentPackageName) == false)//Skip Current Apps from listing.
                    res.add(p);
            }


        }
        return res;
    }

    /**
     * This method is used to get installed application package name from PackageManager.
     *
     * @return ArrayList<String>
     */
    public ArrayList<String> getInstalledPackageNames(Context mContext) {
        ArrayList<String> packgeList = new ArrayList<String>();
        ArrayList<PackageInfo> pInfos = getInstalledApps(mContext);
        for (PackageInfo pInfo : pInfos) {
            packgeList.add((pInfo.packageName));
        }
        return packgeList;
    }

    /**
     * This method is used to get Screen Height & width.
     *
     * @param activity
     * @return float[]{screen_width,screen_height}
     */
    public float[] getScreenResolutions(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        final int height = dm.heightPixels;
        final int width = dm.widthPixels;

        return new float[]{width, height};
    }

    /**
     * This method convets dp unit to equivalent device specific value in pixels.
     *
     * @param dp      A value in dp(Device independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent Pixels equivalent to dp according to device
     */
    public float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to device independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent db equivalent to px value
     */
    public float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;

    }

    /**
     * This method is use to check the device internet connectivity.
     *
     * @param context
     * @return true :if your device is connected to internet.
     * false :if your device is not connected to internet.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null)
            return false;
        if (info.getState() != State.CONNECTED)
            return false;

        return true;
    }

    /**
     * This method is used to get tower unique cid.
     */
    public String getDeviceProvider_CID(Context context) {
        String cid = "";
        final TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();

            if (location != null) {

                cid = "LAC: " + location.getLac() + " CID: " + location.getCid();

            }
        }
        return cid;
    }

    /**
     * This method return device name like device Manufacture + Model name.
     *
     * @return String deviceName
     */
    public String getDeviceName() {
        String deviceName = "";
        deviceName = Build.MANUFACTURER + " " + Build.MODEL;
        return deviceName;
    }

    /**
     * This method return device name like device Model name.
     *
     * @return String deviceName
     */
    public String getDeviceModelName() {
        String deviceModelName = "";
        deviceModelName = Build.MODEL;
        return deviceModelName;
    }


    /**
     * This method return device name like device Manufacture + Model name.
     *
     * @return String deviceName
     */
    public String getDeviceSDKVersion() {
        String deviceSDK = Build.VERSION.RELEASE;
        return deviceSDK;
    }

    /**
     * This method return used to get device id from TelephonyManager / SECURE . <Br>
     * <Br>
     * <B> A 64-bit number (as a hex string) that is randomly generated on the devices first boot and should remain constant for the lifetime of the device. (The value may change if a factory reset is performed on the device.) </B> <br>
     * Help : http://blog.vogella.com/2011/04/11/android-unique-identifier/ <br>
     * <Br>
     *
     * @return <B>String deviceID</B>
     */
    public String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();// default actual device ID
        if (deviceId == null || deviceId.equalsIgnoreCase("")) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }

    /**
     * This function returns whether the input string is valid or not.
     *
     * @param inputStr
     * @return
     */
    public boolean isValidText(CharSequence inputStr) {

        String expression = "^[a-zA-Z]+[a-zA-Z0-9 '$._]*$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * This function returns whether the input string is valid or not.
     * This is made for username spl validation.
     *
     * @param inputStr
     * @return
     */
    public boolean isValidTextSpl(CharSequence inputStr) {

        String expression = "^[0-9a-zA-Z]+$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This function returns whether the input email ID is valid or not.
     *
     * @param inputStr
     * @return
     */
    public boolean isValidEmail(CharSequence inputStr) {
        String expression = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This function returns whether the input password is valid or not.
     *
     * @param inputStr
     * @return
     */
    public boolean isValidPassword(CharSequence inputStr) {
        String expression = "((?=.*\\\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This function returns whether the input data  is valid or not.
     *
     * @param inputStr
     * @return
     */
    public static boolean isValidDataReg(CharSequence inputStr,String expression) {
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method is used to hide soft keyboard.
     *
     * @param activity
     */
    public void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to hide soft keyboard.
     *
     * @param activity
     * @param editText
     */
    public static void hideSoftKeyboard(Activity activity, EditText editText) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * hide keyboard on actvity startup(incase of edittext focus)
     *
     * @param activity
     */
    public static void hideKeyboardOnStartup(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * This method is used to show soft keyboard.
     *
     * @param activity
     */
    public void showSoftKeyboard(Activity activity) {
        InputMethodManager inputMgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * This function let you to convert string in Title Case.
     *
     * @param givenString
     * @return
     */
    public String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }


    /**
     * This function returns the device's unique serial number.
     *
     * @return
     */
    public String getDeviceSerialNumber() {
        String serial = null;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return serial;
    }

    public byte[] bitmapTobyteArray(Context context, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] rawBytes = baos.toByteArray();

        return rawBytes;
    }

    /**
     * This method is used to write content into file.
     *
     * @param context context
     * @param text    text
     */
    public void writeLogIntoFile(Context context, String path, String text) {
        try {
            File file = new File(path);
            FileOutputStream outputStream = new FileOutputStream(file, true);

            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to delete file from sd-card.
     *
     * @param context context
     * @param path    text
     */
    public void deleteFile(Context context, String path) {
        try {
            File file = new File(path);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * save Database To Sdcard
     *
     * @param context
     */
    public static void saveDatabaseToSdcard(Context context) {
        try {
            InputStream myInput = new FileInputStream("/data/data/com.lds/databases/" + "LDS.db");

            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "LDS.db");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                }
            }

            OutputStream myOutput = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + "LDS.db");

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
        }
    }


    /**
     * get Number From String
     *
     * @param input
     * @return
     */

    public String getNumberFromString(final CharSequence input) {
        final StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c > 47 && c < 58) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * convert Minutes To Millis.
     *
     * @param minutes
     * @return
     */
    public long convertMinutesToMillis(int minutes) {
        return TimeUnit.MINUTES.toMillis(minutes);
    }

    /**
     * get Current Data Time now
     *
     * @param context
     * @return
     */
    public String getCurrentDataTimeNow(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = simpleDateFormat.format(calendar.getTime());
        return formattedDate;
    }

    /**
     * check of Application is Brought To Background
     * @param context
     * @return
     */
    public static boolean isApplicationBroughtToBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            for (ActivityManager.RunningTaskInfo componentName : tasks) {
                ComponentName topActivity = componentName.topActivity;
                System.out.println("topActivity: " + topActivity.getPackageName());
                if (!topActivity.getPackageName().equals(context.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * convert Array To String
     * @param array
     * @return
     */
    public static String convertArrayToString(String[] array,String strSeparator){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    /**
     * convert String To Array
     * @param str
     * @return
     */
    public static String[] convertStringToArray(String str,String strSeparator){
        String[] strArr = str.split(strSeparator);
        return strArr;
    }

    /**
     * convert BitMap To String
     * @param bitmap
     * @return
     */
    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
        byte [] bytes = byteArrayOutputStream.toByteArray();
        String temp= Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }

    /**
     * convert String To BitMap
     * @param encodedString
     * @return bitmap (from given string)
     */
    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if arrayList has duplicates values.
     * @param list
     * @return
     */
    public static boolean hasDuplicates(ArrayList<String> list) {
        // See if duplicates exist (see above example).
        for (int i = 0; i < list.size(); i++) {
            for (int x = i + 1; x < list.size(); x++) {
                if (list.get(i) == list.get(x)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * remove arrayList duplicates values.
     * @param list
     * @return
     */
    public static ArrayList<String> removeDuplicates(ArrayList<String> list) {
        // Remove Duplicates: place them in new list (see above example).
        ArrayList<String> result = new ArrayList<>();
        HashSet<String> set = new HashSet<>();
        for (String item : list) {
            if (!set.contains(item)) {
                result.add(item);
                set.add(item);
            }
        }
        return result;
    }
}