package com.unicorn.doormate

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import androidx.fragment.app.ListFragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

private val PROJECTION: Array<String> = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.DISPLAY_NAME,
    ContactsContract.Contacts.CONTACT_STATUS,
    ContactsContract.Contacts.CONTACT_PRESENCE,
    ContactsContract.Contacts.PHOTO_ID,
    ContactsContract.Contacts.LOOKUP_KEY
)


/*


private val PHONE_NUMBER_PROJECTION: Array<String> = arrayOf(
    ContactsContract.CommonDataKinds.Identity._ID,
    ContactsContract.CommonDataKinds.Identity.LOOKUP_KEY,
    ContactsContract.CommonDataKinds.Phone.NUMBER
)

private const val PHONE_NUMBER_SELECTION: String = "${ContactsContract.CommonDataKinds.Identity.LOOKUP_KEY} = ?"

private val selectionArgs: Array<String> = arrayOf("")


*/



class ContactsFragment :
    ListFragment(),
    SearchView.OnQueryTextListener,
    LoaderManager.LoaderCallbacks<Cursor> {

    private lateinit var mAdapter: SimpleCursorAdapter

    private var curFilter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loaderManager.initLoader(0, null, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEmptyText("No match")

        setHasOptionsMenu(true)

        mAdapter = SimpleCursorAdapter(
            activity,
            android.R.layout.simple_list_item_2,
            null,
            arrayOf(
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.LOOKUP_KEY
            ),
            intArrayOf(android.R.id.text1, android.R.id.text2),
            0
        )
        listAdapter = mAdapter
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add("Search").apply {
            setIcon(android.R.drawable.ic_menu_search)
            setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = SearchView(activity).apply {
                setOnQueryTextListener(this@ContactsFragment)
            }
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        curFilter = if (newText?.isNotEmpty() == true) newText else null
        loaderManager.restartLoader(0, null, this)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {

        return true
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {

                Log.i("FragmentComplexList", "Item clicked: $id")
    }

    override fun onCreateLoader(id: Int, args: Bundle?): CursorLoader {

        val baseUri: Uri = if (curFilter != null && curFilter?.length != 0) {
            Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(curFilter))
        } else {
            ContactsContract.Contacts.CONTENT_URI
        }

        val select: String = "((${ContactsContract.Contacts.DISPLAY_NAME} NOTNULL) AND (" +
                "${ContactsContract.Contacts.HAS_PHONE_NUMBER}=1) AND (" +
                "${ContactsContract.Contacts.DISPLAY_NAME} != ''))"
        return (activity as? Context)?.let { context ->
            CursorLoader(
                context,
                baseUri,
                PROJECTION,
                select,
                null,
                "${ContactsContract.Contacts.DISPLAY_NAME} COLLATE LOCALIZED ASC"
            )
        } ?: throw Exception("Activity cannot be null")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        mAdapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mAdapter.swapCursor(null)
    }
}

