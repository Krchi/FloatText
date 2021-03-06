package tool.xfy9326.floattext.Method;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.view.View.*;
import android.view.WindowManager.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import tool.xfy9326.floattext.*;
import tool.xfy9326.floattext.Utils.*;
import tool.xfy9326.floattext.View.*;

public class FloatTextSettingMethod
{
    private boolean longClicked;
	
	public static void showDlist (final Context ctx)
	{
		final String[] dynamiclist = ctx.getResources().getStringArray(R.array.floatsetting_dynamic_list);
		String[] dynamicname = ctx.getResources().getStringArray(R.array.floatsetting_dynamic_name);
		String[] result = new String[dynamiclist.length + 1];
		for (int i = 0;i < dynamiclist.length;i++)
		{
			result[i] = "<" + dynamiclist[i] + ">" + "\n" + dynamicname[i];
		}
		result[dynamiclist.length] = ctx.getString(R.string.dynamic_num_tip);
		AlertDialog.Builder list = new AlertDialog.Builder(ctx)
			.setTitle(R.string.dynamic_list_title)
			.setItems(result, new DialogInterface.OnClickListener(){
				public void onClick (DialogInterface d, int i)
				{
					if (i != dynamiclist.length)
					{
						ClipboardManager clip = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
						if (((App)ctx.getApplicationContext()).HtmlMode)
						{
							clip.setText("#" + dynamiclist[i] + "#");
						}
						else
						{
							clip.setText("<" + dynamiclist[i] + ">");
						}
						Toast.makeText(ctx, R.string.copy_ok, Toast.LENGTH_SHORT).show();
					}
				}
			});
		list.show();
	}

    public static String IntColortoHex (int color)
    {
        String R, G, B, C;
        StringBuffer sb = new StringBuffer();
        C = Integer.toHexString(Color.alpha(color));
        R = Integer.toHexString(Color.red(color));
        G = Integer.toHexString(Color.green(color));
        B = Integer.toHexString(Color.blue(color));
        C = C.length() == 1 ? "0" + C : C;
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        sb.append("#");
        sb.append(C.toUpperCase());
        sb.append(R.toUpperCase());
        sb.append(G.toUpperCase());
        sb.append(B.toUpperCase());
        return sb.toString();
    }

    public static FloatTextView CreateFloatView (final Context ctx, String Text, Float Size, int Paint, boolean Thick, int speed, int id, boolean shadow, float shadowx, float shadowy, float shadowradius, int shadowcolor)
    {
        FloatTextView floatview = new FloatTextView(ctx, ((App)ctx.getApplicationContext()).getHtmlMode());
        floatview.setText(Text.toString());
        floatview.setTextSize(Size);
        floatview.setTextColor(Paint);
        floatview.setMoveSpeed(speed);
        floatview.setID(id);
        floatview.setShadow(shadow, shadowx, shadowy, shadowradius, shadowcolor);
        floatview.getPaint().setFakeBoldText(Thick);
        floatview.setTypefaceFile(typeface_fix(ctx));
        return floatview;
    }

    public static String typeface_fix (Context ctx)
    {
        SharedPreferences setdata = ctx.getSharedPreferences("ApplicationSettings", Activity.MODE_PRIVATE);
        String filename = setdata.getString("DefaultTTFName", "Default");
        if (filename.equalsIgnoreCase("Default"))
        {
            return filename;
        }
        else
        {
            String typeface_path = Environment.getExternalStorageDirectory().toString() + "/FloatText/TTFs/" + filename + ".ttf";
            String typeface_path2 = Environment.getExternalStorageDirectory().toString() + "/FloatText/TTFs/" + filename + ".TTF";
            File f1 = new File(typeface_path);
            File f2 = new File(typeface_path2);
            if (f1.exists())
            {
                return f1.getAbsolutePath();
            }
            else if (f2.exists())
            {
                return f2.getAbsolutePath();
            }
        }
        setdata.edit().putString("DefaultTTFName", "Default").commit();
        Toast.makeText(ctx, R.string.text_typeface_err, Toast.LENGTH_SHORT).show();
        return "Default";
    }

    public static FloatLinearLayout CreateLayout (Context ctx, int ID)
    {
        FloatLinearLayout layout = new FloatLinearLayout(ctx, ID);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
        return layout;
    }

    public static WindowManager.LayoutParams CreateFloatLayout (Context ctx, WindowManager wm, FloatTextView floatview, FloatLinearLayout layout, boolean show, boolean top, boolean move, int bac, boolean fs, float fl, float fw)
    {
        return CreateFloatLayout(ctx, wm, floatview, layout, show, 100, 150, top, move, bac, fs, fl, fw);
    }

    public static WindowManager.LayoutParams CreateFloatLayout (Context ctx, WindowManager wm, FloatTextView floatview, FloatLinearLayout layout, boolean show, float px, float py, boolean top, boolean move, int bac, boolean fs, float fl, float fw)
    {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
        if (top)
        {
            wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }
        else
        {
            wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = (int)px;
        wmParams.y = (int)py;
        wmParams.format = PixelFormat.TRANSLUCENT;
		if (fs)
		{
			wmParams.width = (int)fl;
			wmParams.height = (int)fw;
		}
		else
		{
			wmParams.width = LayoutParams.WRAP_CONTENT;
			wmParams.height = LayoutParams.WRAP_CONTENT;
		}
		wmParams.windowAnimations = R.style.floatwin_anim;
        layout.setLayout_default_flags(wmParams.flags);
        layout.setTop(true);
        layout.setAddPosition(px, py);
        layout.setBackgroundColor(bac);
        layout.setFloatLayoutParams(wmParams);
        layout.changeShowState(show);
        layout.setPadding(3, 3, 3, 3);
        layout.addView(floatview);
        if (show)
        {
            wm.addView(layout, wmParams);
        }
        if (((App)ctx.getApplicationContext()).getMovingMethod())
        {
            floatview.setMoving(move, 0);
        }
        else
        {
            floatview.setMoving(move, 1);
        }
        return wmParams;
    }

    public static void askforpermission (Activity act, int code)
    {
        final int askcode = code;
        final Activity activity = act;
        final Context ctx = act;
        AlertDialog.Builder dialog = new AlertDialog.Builder(act)
            .setTitle(R.string.ask_for_premission)
            .setMessage(R.string.ask_for_premisdion_msg)
            .setPositiveButton(R.string.done, new DialogInterface.OnClickListener(){
                public void onClick (DialogInterface p1, int p2)
                {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + ctx.getPackageName()));
                    activity.startActivityForResult(intent, askcode);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                public void onClick (DialogInterface p1, int p2)
                {
                    activity.finish();
                }
            })
            .setCancelable(false);
        dialog.show();
    }

    public static void savedata (Context ctx, FloatTextView fv, FloatLinearLayout fll, String text, WindowManager.LayoutParams layout)
    {
        App utils = ((App)ctx.getApplicationContext());
        ArrayList<FloatTextView> floatdata = utils.getFloatView();
        ArrayList<String> floattext = utils.getFloatText();
        ArrayList<WindowManager.LayoutParams> floatlayout = utils.getFloatLayout();
        ArrayList<FloatLinearLayout> floatlinearlayout = utils.getFloatlinearlayout();
        floatdata.add(fv);
        floattext.add(text);
        floatlayout.add(layout);
        floatlinearlayout.add(fll);
    }

    public OnTouchListener ButtonOnLongRepeatClickListener (final int Code, final Handler handler)
    {
        OnTouchListener controllisten = new OnTouchListener(){
            public boolean onTouch (View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    longClicked = true;
                    Thread t = new Thread(){  
                        @Override  
                        public void run ()
                        {  
                            super.run();  
                            while (longClicked)  
                            {
                                handler.sendEmptyMessage(Code);
                                try
                                {  
                                    Thread.sleep(100);  
                                }
                                catch (InterruptedException e)
                                {  
                                    e.printStackTrace();  
                                }  
                            }  
                        }  
                    };  
                    t.start();  
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {  
                    longClicked = false;
                }  
                return false;
            }
        };
        return controllisten;
    }
}
