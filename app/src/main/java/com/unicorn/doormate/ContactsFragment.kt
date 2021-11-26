package com.unicorn.doormate

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CursorAdapter
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader


@SuppressLint("InlinedApi", "ObsoleteSdkInt")
private val FROM_COLUMNS: Array<String> = arrayOf(
    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    } else {
        ContactsContract.Contacts.DISPLAY_NAME
    }
)

private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)



/*@SuppressLint("InlinedApi", "ObsoleteSdkInt")
private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    else
        ContactsContract.Contacts.DISPLAY_NAME
)*/

private const val CONTACT_ID_INDEX: Int = 0
private const val CONTACT_KEY_INDEX: Int = 1



/*@SuppressLint("InlinedApi", "ObsoleteSdkInt")
private val SELECTION: String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
    else
        "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"

private val searchString: String = ...
private val selectionArgs = arrayOf<String>(searchString)*/


/*private val SELECTION: String =
    "${ContactsContract.CommonDataKinds.Email.ADDRESS} LIKE ? AND " +
            "${ContactsContract.Data.MIMETYPE } = " +
            "${ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE}'"

private val searchString: String = ...
private val selectionArgs = arrayOf<String>(searchString)*/



@SuppressLint("InlinedApi", "ObsoleteSdkInt")
private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Data._ID,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        ContactsContract.Data.DISPLAY_NAME_PRIMARY
    else ContactsContract.Data.DISPLAY_NAME,
    ContactsContract.Data.CONTACT_ID,
    ContactsContract.Data.LOOKUP_KEY
)


private var searchString: String? = null
private val selectionArgs: Array<String> = arrayOf("")

class ContactsFragment :
    Fragment(),
    LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener {

    lateinit var contactsList: ListView
    var contactId: Long = 0
    var contactKey: String? = null
    var contactUri: Uri? = null
    private var cursorAdapter: SimpleCursorAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loaderManager.initLoader(0, null, this)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.also {
            contactsList = it.findViewById<ListView>(R.id.contact_list_view)
            cursorAdapter = SimpleCursorAdapter(
                it,
                R.layout.contacts_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0
            )
            contactsList.adapter = cursorAdapter
            contactsList.onItemClickListener = this
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val cursor: Cursor? = (parent.adapter as? CursorAdapter)?.cursor?.apply {
            moveToPosition(position)
            contactId = getLong(CONTACT_ID_INDEX)
            contactKey = getString(CONTACT_KEY_INDEX)
            contactUri = ContactsContract.Contacts.getLookupUri(contactId, contactKey)
        }
    }

    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
        selectionArgs[0] = "%$searchString%"
        return activity?.let {
            return CursorLoader(
                it,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                selectionArgs,
                null
            )
        } ?: throw IllegalStateException()
    }

/*
    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {

        val contentUri: Uri = Uri.withAppendedPath(
            ContactsContract.Contacts.CONTENT_FILTER_URI,
            Uri.encode(searchString)
        )

        return activity?.let {
            CursorLoader(
                it,
                contentUri,
                PROJECTION,
                null,
                null,
                null
            )
        } ?: throw IllegalStateException()
    }
*/

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        cursorAdapter?.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        cursorAdapter?.swapCursor(null)
    }
}