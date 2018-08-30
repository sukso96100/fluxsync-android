package xyz.youngbin.fluxsync


import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract

import java.io.InputStream

/**
 * Created by youngbin on 14. 7. 12.
 */
class ContactUtil(private val mContext: Context, private val PhoneNumber: String) {
    var contactName: String? = null
        private set
    private var ContactId: String? = null
    private var ContactImage: Bitmap? = null
    private var IS: InputStream? = null
    private val cursor: Cursor?
    val contactImage: Bitmap?
        get() {
            ContactImage = BitmapFactory.decodeStream(IS)
            return ContactImage
        }

    init {


        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(PhoneNumber))
        //Things to Access
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID)
        cursor = mContext.contentResolver.query(uri, projection, null, null, null)
        if (cursor!!.moveToFirst()) {
            ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID))
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))

            // Get photo of contactId as input stream:
            val contacturi = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, java.lang.Long.parseLong(ContactId))
            IS = ContactsContract.Contacts.openContactPhotoInputStream(mContext.contentResolver, contacturi)
        } else {
        }
    }

    fun close() {
        cursor!!.close()
    }
}