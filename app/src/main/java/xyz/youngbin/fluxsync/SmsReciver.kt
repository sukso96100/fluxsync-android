package xyz.youngbin.fluxsync

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.support.v4.content.LocalBroadcastManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.provider.Settings.Global.getString
import org.json.JSONObject
import xyz.youngbin.fluxsync.R.id.end
import xyz.youngbin.fluxsync.R.id.name
import java.util.logging.Logger
import kotlin.properties.Delegates
import xyz.youngbin.fluxsync.test.ContactUtil




class SmsReceiver : BroadcastReceiver() {
    lateinit var mLocalBM: LocalBroadcastManager
     @TargetApi(Build.VERSION_CODES.M)


    /////////////////////////
    override fun onReceive(context: Context?, intent: Intent?) {

         val callerPhoneNumber: String? = null


         mLocalBM = LocalBroadcastManager.getInstance(context)
         // SMS를 받았을 경우에만 반응하도록 if문을 삽입
         if (intent?.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
             var sms = StringBuilder();    // SMS문자를 저장할 곳  //var 로 변수를 선언한다는 키워드를 써주고 뒤에꺼에 따라서 타입이 알아서 정해진다.
             //굳이 타입 정할 때는  sms : 타입
             var bundle = intent?.extras;   // Bundle객체에 받는다. intent에 딸려온 정보를 가지고 있는 객체



             if (bundle != null) {
                 // 번들에 포함된 문자 데이터를 객체 배열로 받아온다
                 var pdusObj: Array<Any> = bundle.get("pdus") as Array<Any>; //키값이 pdus 인 것을 받아온다.

                 // SMS를 받아올 SmsMessage 배열을 만든다

                 var messages: Array<SmsMessage> = Array(pdusObj.size, { i ->
                     SmsMessage.createFromPdu(pdusObj[i] as ByteArray, bundle.getString("format"))

                 })




                 val messagesAddress = messages[0].originatingAddress

                 val CU = ContactUtil(context!!, messagesAddress)//context 시스템 자원 접근 할 때 (번호를 접근하기위해 시스템 자원에 접근)
                 var name = CU.contactName
                 if(name.isNullOrEmpty()) name = messagesAddress




                 Log.d("NotificationService",messagesAddress)
                 // SmsMessage배열에 담긴 데이터를 append메서드로 sms에 저장
                 for (smsMessage in messages) {
                     // getMessageBody메서드는 문자 본문을 받아오는 메서드
                     sms.append(smsMessage.messageBody);
                 }



                 val smsData = sms.toString() // StringBuilder 객체 sms를 String으로 변환
                 val smsIntent = Intent(Util.sendDataFilter)

                 var data = JSONObject()
                 data.put("name", name)
                 data.put("body", smsData)


//               val smsIntent = Intent(this, ConnectionService::class.java)
                 smsIntent.putExtra("command", "send")//커맨드로 send 할 때 아래부분을 같이 보낸다는 뜻으로 쓴다.
                 smsIntent.putExtra("eventName", "sms") //connection Service 부분에 있는 커넥 부분에 있는걸 받아서 보낸다 .
                 smsIntent.putExtra("content" , data.toString()) //내용을 문자열로 해서 보내는 것 !
                 Log.d("NotificationService", smsData)
                 Log.d("NotificationService","calling startService ...")
//               startService(smsIntent) // 이 함수가 호출되지 않음
                 mLocalBM.sendBroadcast(smsIntent)//인텐트로 보내는 부분
                 Log.d("NotificationService","... calling startService")
                 Toast.makeText(context, smsData, Toast.LENGTH_LONG).show()

             }
         }
     }


 }
