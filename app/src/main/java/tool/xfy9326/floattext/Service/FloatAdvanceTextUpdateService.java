package tool.xfy9326.floattext.Service;

import android.accessibilityservice.*;
import android.content.*;
import android.os.*;
import android.view.accessibility.*;
import java.util.*;
import tool.xfy9326.floattext.*;
import tool.xfy9326.floattext.Activity.*;
import tool.xfy9326.floattext.Method.*;

public class FloatAdvanceTextUpdateService extends AccessibilityService
{
	private String currentactivity;
	private String notifymes;
	private String toasts;
	private boolean sentrule = false;

	@Override
	public void onCreate ()
	{
		currentactivity = toasts = notifymes = getString(R.string.loading);
		FloatManageMethod.setWinManager(this);
		super.onCreate();
	}

	@Override
	public void onAccessibilityEvent (AccessibilityEvent event)
	{
		int eventType = event.getEventType();
        switch (eventType)
		{
			case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
				List<CharSequence> text = event.getText();
				if (!text.isEmpty())
				{
					String str = text.toString();
					if (str != "[]")
					{
						str = str.substring(1, str.length() - 1);
						sentrule = getRule();
						if (sentrule)
						{
							if (event.getClassName().toString().contains("Toast"))
							{
								toasts = str;
							}
							else
							{
								notifymes = str;
							}
						}
						else
						{
							if (event.getClassName().toString().contains("Toast"))
							{
								toasts = str;
							}
						}
						sendmes();
					}
				}
				break;
			case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
				currentactivity = event.getClassName().toString();
				sendmes();
				break;
		}
	}

	@Override
	public void onInterrupt ()
	{
	}

	private boolean getRule ()
	{
		if (Build.VERSION.SDK_INT < 18 || !GlobalSetActivity.isNotificationListenerEnabled(this))
		{
			return true;
		}
		return false;
	}

	private void sendmes ()
	{
		Intent intent = new Intent();
		intent.setAction(FloatServiceMethod.TEXT_ADVANCE_UPDATE_ACTION);
		intent.putExtra("CurrentActivity", currentactivity);
		if (sentrule)
		{
			intent.putExtra("NotifyMes", notifymes);
		}
		intent.putExtra("Toasts", toasts);
		sendBroadcast(intent);
	}

}
