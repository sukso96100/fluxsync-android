package xyz.youngbin.fluxsync

import android.annotation.SuppressLint
import android.app.Notification.*
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telecom.ConnectionService
import org.json.JSONObject
import xyz.youngbin.fluxsync.R.id.remoteName

public class NotificationService : NotificationListenerService() {


    override fun onCreate() {
        super.onCreate()

    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)       //인자 부분


        val currentItem = sbn?.notification?.extras


        val title = currentItem?.get(EXTRA_TITLE)
        //이렇게하면 변수 sbn 에 해서 제목을 빼오고
        //? 물음표 부분은 널이면 실행하지말고 널이 아니면 실행하라는 뜻이다.

        val content = currentItem?.get(EXTRA_TITLE)
        //텍스트 부분으로 한다.
        //커맨드를 샌드로하고 컨테츠에다가 이 두개를 실어서 보내면 되는데 양식을 잡아서
        //제이슨 양식으로 해서 잡아서 보낸다 .

        val noti_id = currentItem?.get(EXTRA_NOTIFICATION_ID)

        var data = JSONObject()
        data.put("title", title)
        data.put("content", content)
        data.put("noti_id" , noti_id)

        var Service_intent = Intent(this, ConnectionService::class.java)
        Service_intent.putExtra("command", "send")//커맨드로 send 할 때 아래부분을 같이 보낸다는 뜻으로 쓴다.
        Service_intent.putExtra("eventName", "notify") //connection Service 부분에 있는 커넥 부분에 있는걸 받아서 보낸다 .
        Service_intent.putExtra("content" , data.toString()) //내용을 문자열로 해서 보내는 것 !
        startService(Service_intent)
        //json 쓴 걸 문자열로 바꿔주는 기능   자바에서 객체였는데 그걸 문자열로 바꾸는것 이쪽부분에 intent 해주면 된다 .
        //보내는건 deviceFragment 에 있다 .

        /* json 식으로 바꾼것 */



        /* {

 title : "알림 제목 " -string
 content : 알림 내용" -string

아이디가 있어야 알림버튼에서 누를수있다
알림 식별하는것 ! 것것것 !
 id : "알림 id" - int
앱에서 임의로 숫자값넣어서 보내면 된다.


  }

샌드를 할때 컨텐츠에서 하는데 이런 방식으로 문자열 데이터를 만들어서 실어서 보내면 된다.

문자열로 간다 그걸 제이슨 객체로 바꾸면 된다 그걸 자바 스크립트 내장함수로 사용해서 바꾸고

나라는 제이슨 파싱을 해서 컨텐츠 아이디 타이틀을 따서 노티파이 함수 호출

인자로 해서 컨텐츠 내용따서 그대로 하면 된다.
*/


    }

    /*서비스가 시작 되면 이부분이 호출되고 이것이 인자안에 메세지를 뽑아가지고 서비스를 이어서
    send 해서 보내주면 된다.*/

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
    }


    override fun getActiveNotifications(): Array<StatusBarNotification> {
        return super.getActiveNotifications()
    }

}

