package com.pervasive.project.kidsTracker;

import android.telephony.SmsManager;
import android.widget.Toast;


public class SendAlert 
{

	public void sendMsg(String phoneNum, String textMessage)
	{
		String sent = "SMS SENT";
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNum, null, textMessage, null, null);
		Toast.makeText(MainActivity.getContext(), textMessage,
				Toast.LENGTH_SHORT).show();


	}	

}
