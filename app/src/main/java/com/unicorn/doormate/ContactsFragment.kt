package com.unicorn.doormate

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

class ContactsFragment :
    Fragment(R.layout.fragment_contacts),
    LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener {

    @SuppressLint("InlinedApi", "ObsoleteSdkInt")
    private val FROM_COLUMNS: Array<String> = arrayOf(
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        } else {
            ContactsContract.Contacts.DISPLAY_NAME
        }
    )

    @SuppressLint("InlinedApi", "ObsoleteSdkInt")
    private val SELECTION: String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
        else
            "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
    private val searchString: String = ""
    private val selectionArgs = arrayOf(searchString)

    private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)

    private lateinit var contactsList: ListView

    private var contactId: Long = 0
    private var contactKey: String? = null
    private var contactUri: Uri? = null
    private var cursorAdapter: SimpleCursorAdapter? = null

    @SuppressLint("InlinedApi", "ObsoleteSdkInt")
    private val PROJECTION: Array<out String> = arrayOf(
        ContactsContract.Data._ID,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            ContactsContract.Data.DISPLAY_NAME_PRIMARY
        else
            ContactsContract.Data.DISPLAY_NAME,
        ContactsContract.Data.LOOKUP_KEY
    )

    private val CONTACT_ID_INDEX: Int = 0
    private val CONTACT_KEY_INDEX: Int = 1

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
        Log.i("asdf", this.toString())
        activity?.also {
            contactsList = it.findViewById(R.id.contact_list_view)
            contactsList.onItemClickListener = this
            cursorAdapter = SimpleCursorAdapter(
                it,
                R.layout.contacts_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0
            )
            contactsList.adapter = cursorAdapter
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        (parent.adapter as? CursorAdapter)?.cursor?.apply {
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

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        cursorAdapter?.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        cursorAdapter?.swapCursor(null)
    }

    companion object {
        fun newInstance() =
            ContactsFragment().apply {
                return@apply
            }
    }
}