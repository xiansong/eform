/* EForm - Electronic Form System
 *
 * Copyright (C) 2013 Wu Xiaohu. All rights reserved.
 * Copyright (C) 2013 Cansiny Trade Co.,Ltd. All rights reserved.
 */
package com.cansiny.eform;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class Utils
{
    static public final int IMAGE_SIZE_LARGE = 1;
    static public final int IMAGE_SIZE_SMALL = 2;

    /**
     * This method converts dp unit to equivalent pixels, depending on
     * device density. 
     * 
     * @param dp A value in dp (density independent pixels) unit. Which we
     * 		need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on
     *		device density
     */
    static public float convertDpToPixel(float dp) {
	Context context = EFormApplication.getContext();
	DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	return dp * (metrics.densityDpi / 160f);
    }

    /**
     * This method converts device specific pixels to density independent pixels
     * 
     * @param px A value in px (pixels) unit. Which we need to convert into dp
     * @return A float value to represent dp equivalent to px value
     */
    static public float convertPixelsToDp(float px) {
	Context context = EFormApplication.getContext();
	DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	return px / (metrics.densityDpi / 160f);
    }

    static public Toast makeToast(CharSequence sequence,
	    int image_res, int image_size, int duration) {
	Context context = EFormApplication.getContext();

	LinearLayout layout = new LinearLayout(context);
	layout.setBackgroundResource(R.color.translucence);
	layout.setPadding(20, 10, 20, 10);

	if (image_res != 0) {
	    ImageView image = new ImageView(context);
	    image.setBackgroundResource(image_res);
	    image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

	    if (image_size == IMAGE_SIZE_SMALL) {
		image_size = (int) convertDpToPixel(48);
	    } else {
		image_size = (int) convertDpToPixel(64);
	    }
	    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		    image_size, image_size);
	    params.gravity = Gravity.CENTER_VERTICAL;
	    params.rightMargin = 10;
	    layout.addView(image, params);
	}

	TextView text_view = new TextView(context);
	text_view.setText(sequence);
	text_view.setTextColor(context.getResources().getColor(R.color.yellow));
	text_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
	text_view.setGravity(Gravity.CENTER_VERTICAL);

	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		ViewGroup.LayoutParams.WRAP_CONTENT,
		ViewGroup.LayoutParams.WRAP_CONTENT);
	params.gravity = Gravity.CENTER_VERTICAL;
	layout.addView(text_view, params);

	Toast toast = new Toast(context);
	toast.setView(layout);
	toast.setDuration(duration);
	toast.setGravity(Gravity.CENTER, 0, 0);

	return toast;
    }

    static Toast showToast(CharSequence sequence, int image_res, int image_size) {
	Toast toast = makeToast(sequence, image_res, image_size, Toast.LENGTH_SHORT);
	toast.show();
	return toast;
    }

    static Toast showToast(CharSequence sequence, int image_res) {
	return showToast(sequence, image_res, IMAGE_SIZE_LARGE);
    }

    static Toast showToast(CharSequence sequence) {
	return showToast(sequence, 0, IMAGE_SIZE_LARGE);
    }

    static Toast showToastLong(CharSequence sequence, int image_res, int image_size) {
	Toast toast = makeToast(sequence, image_res, image_size, Toast.LENGTH_LONG);
	toast.show();
	return toast;
    }

    static Toast showToastLong(CharSequence sequence, int image_res) {
	return showToastLong(sequence, image_res, IMAGE_SIZE_LARGE);
    }

    static Toast showToastLong(CharSequence sequence) {
	return showToastLong(sequence, 0, IMAGE_SIZE_LARGE);
    }


    static public UUID getDeviceId() {
	UUID uuid = null;

	Context context = EFormApplication.getContext();
	SharedPreferences prefs = context.getSharedPreferences("device", 0);
	String id = prefs.getString("deviceid", null);
	if (id != null)
	    return UUID.fromString(id);

	try {
	    String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	    if (!"9774d56d682e549c".equals(androidId)) {
		uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf-8"));
	    } else {
		String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		uuid = (deviceId != null) ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
	    }
	    prefs.edit().putString("deviceid", uuid.toString()).commit();
	    return uuid;
	} catch (UnsupportedEncodingException e) {
	    LogActivity.writeLog(e);
	    return UUID.randomUUID();
	}
    }


    static public class DialogFragment extends android.app.DialogFragment
    	implements OnClickListener

    {
	static final public String CLEAR_BUTTON_TAG = "ClearButton";

	private ArrayList<Toast> toasts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    toasts = new ArrayList<Toast>();
	}

	@Override
	public void onStart() {
	    super.onStart();

	    getDialog().getWindow().setSoftInputMode(
		    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
	    super.onDismiss(dialog);

	    if (getActivity() != null) {
		getActivity().getWindow().setSoftInputMode(
			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	    }
	    for (Toast toast : toasts) {
		toast.cancel();
	    }
	}

	protected void showToast(CharSequence sequence, int image_res, int image_size) {
	    toasts.add(Utils.showToast(sequence, image_res, image_size));
	}
	protected void showToast(CharSequence sequence, int image_res) {
	    showToast(sequence, image_res, Utils.IMAGE_SIZE_LARGE);
	}
	protected void showToast(CharSequence sequence) {
	    showToast(sequence, 0, Utils.IMAGE_SIZE_LARGE);
	}

	protected void showToastLong(CharSequence sequence, int image_res, int image_size) {
	    toasts.add(Utils.showToastLong(sequence, image_res, image_size));
	}
	protected void showToastLong(CharSequence sequence, int image_res) {
	    showToastLong(sequence, image_res, Utils.IMAGE_SIZE_LARGE);
	}
	protected void showToastLong(CharSequence sequence) {
	    showToastLong(sequence, 0, Utils.IMAGE_SIZE_LARGE);
	}

	protected void hideIme(View view) {
	    InputMethodManager imm =
		    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}

	protected void addClearButton(ViewGroup parent) {
	    Button button = new Button(getActivity());
	    button.setTag(CLEAR_BUTTON_TAG);
	    button.setBackgroundResource(R.drawable.clear);
	    button.setOnClickListener(this);

	    if (parent.getClass().equals(TableRow.class)) {
		TableRow.LayoutParams params = new TableRow.LayoutParams(
			(int) Utils.convertDpToPixel(26),
			(int) Utils.convertDpToPixel(26));
		parent.addView(button, params);
	    } else if(parent.getClass().equals(LinearLayout.class)) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			(int) Utils.convertDpToPixel(26),
			(int) Utils.convertDpToPixel(26));
		parent.addView(button, params);
	    } else {
		throw new IllegalArgumentException("不支持的父容器对象");
	    }
	}

	@Override
	public void onClick(View view) {
	    if (view.getTag() != null && view.getTag().equals(CLEAR_BUTTON_TAG)) {
		ViewGroup parent = (ViewGroup) view.getParent();
		int index = parent.indexOfChild(view);
		if (index <= 0)
		    return;
		View edittext = parent.getChildAt(index - 1);
		if (edittext instanceof EditText) {
		    ((EditText) edittext).setText("");
		    edittext.requestFocus();
		}
	    }
	}

    }


    static public class SerialDeviceAdapter extends BaseAdapter
    {
	private ArrayList<SerialDevice> devices;

	public SerialDeviceAdapter() {
	    devices = new ArrayList<SerialDevice>();

	    SerialPort.SerialPortFinder finder = new SerialPort.SerialPortFinder();
	    String[] results = finder.getAllDevices();
	    Arrays.sort(results);
	    for (String device : results) {
		String[] array = device.split(" ");
		if (!array[1].equals("g_serial")) {
		    devices.add(new SerialDevice(array[2], array[0], array[1]));
		}
	    }
	}

	@Override
	public int getCount() {
	    return devices.size();
	}

	@Override
	public Object getItem(int position) {
	    if (position < devices.size() && position >= 0) {
		return devices.get(position);
	    } else {
		return null;
	    }
	}

	@Override
	public long getItemId(int position) {
	    return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    SerialDevice device = null;
	    if (position <= devices.size())
		device = devices.get(position);

	    LinearLayout linear = new LinearLayout(parent.getContext());
	    linear.setOrientation(LinearLayout.HORIZONTAL);
	    linear.setPadding(10, 10, 10, 0);
	    linear.setGravity(Gravity.RIGHT);

	    TextView textview = new TextView(parent.getContext());
	    if (device != null) {
		String driver = device.getDriver();
		String name;
		if (driver.equalsIgnoreCase("serial"))
		    name = "标准串口 " + device.getName();
		else if (driver.equalsIgnoreCase("usbserial"))
		    name = "USB串口 " + device.getName();
		else
		    name = device.getName();
		textview.setText(name);
		textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
	    }
	    linear.addView(textview);

	    return linear;
	}

	public String getItemDevpath(int position) {
	    if (position < devices.size() && position >= 0) {
		SerialDevice device = devices.get(position);
		return device.devpath;
	    }
	    return null;
	}

	public class SerialDevice
	{
	    private String devpath;
	    private String name;
	    private String driver;
	    public SerialDevice(String devpath, String name, String driver) {
		this.devpath = devpath;
		this.name = name;
		this.driver = driver;
	    }
	    public String getDevpath() {
		return devpath;
	    }
	    public String getName() {
		return name;
	    }
	    public String getDriver() {
		return driver;
	    }
	}

    }
}
