package com.unicorn.doormate

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

// A UI Fragment must inflate its View


class ContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
    }
}

/*
 * Defines an array that contains column names to move from
 * the Cursor to the ListView.
 */
@SuppressLint("InlinedApi")
private val FROM_COLUMNS: Array<String> = arrayOf(
    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    } else {
        ContactsContract.Contacts.DISPLAY_NAME
    }
)
/*
 * Defines an array that contains resource ids for the layout views
 * that get the Cursor column contents. The id is pre-defined in
 * the Android framework, so it is prefaced with "android.R.id"
 */
private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)

abstract class ContactsFragment :
    Fragment(),
    LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener {

    // Define global mutable variables
    // Define a ListView object
    lateinit var contactsList: ListView

    // Define variables for the contact the user selects
    // The contact's _ID value
    var contactId: Long = 0

    // The contact's LOOKUP_KEY
    var contactKey: String? = null

    // A content URI for the selected contact
    var contactUri: Uri? = null

    // An adapter that binds the result Cursor to the ListView
    private val cursorAdapter: SimpleCursorAdapter? = null

}

// A UI Fragment must inflate its View
override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    // Inflate the fragment layout
    return inflater.inflate(R.layout.contact_list_fragment, container, false)
}


override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    // Gets the ListView from the View list of the parent activity
    activity?.also {
        contactsList = it.findViewById<ListView>(R.id.contact_list_view)
        // Gets a CursorAdapter
        cursorAdapter = SimpleCursorAdapter(
            it,
            R.layout.contact_list_item,
            null,
            FROM_COLUMNS, TO_IDS,
            0
        )
        // Sets the adapter for the ListView
        contactsList.adapter = cursorAdapter
    }
}

fun onActivityCreated(savedInstanceState:Bundle) {
    // Set the item click listener to be the current fragment.
    contactsList.onItemClickListener = this
}

@SuppressLint("InlinedApi")
private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    else
        ContactsContract.Contacts.DISPLAY_NAME
)

// The column index for the _ID column
private const val CONTACT_ID_INDEX: Int = 0
// The column index for the CONTACT_KEY column
private const val CONTACT_KEY_INDEX: Int = 1

// Defines the text expression
@SuppressLint("InlinedApi")
private val SELECTION: String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
    else
        "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
// Defines a variable for the search string
private val searchString: String = ...
// Defines the array to hold values that replace the ?
private val selectionArgs = arrayOf<String>(searchString)

override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
    // Get the Cursor
    val cursor: Cursor? = (parent.adapter as? CursorAdapter)?.cursor?.apply {
        // Move to the selected contact
        moveToPosition(position)
        // Get the _ID value
        contactId = getLong(CONTACT_ID_INDEX)
        // Get the selected LOOKUP KEY
        contactKey = getString(CONTACT_KEY_INDEX)
        // Create the contact's content Uri
        contactUri = ContactsContract.Contacts.getLookupUri(contactId, mContactKey)
        /*
         * You can use contactUri as the content URI for retrieving
         * the details for a contact.
         */
    }
}

class ContactsFragment :
    Fragment(),
    LoaderManager.LoaderCallbacks<Cursor> {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Always call the super method first
        super.onCreate(savedInstanceState)
        // Initializes the loader
        loaderManager.initLoader(0, null, this)
        }
    }

override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
    /*
     * Makes search string into pattern and
     * stores it in the selection array
     */
    selectionArgs[0] = "%$mSearchString%"
    // Starts the query
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
    // Put the result Cursor in the adapter for the ListView
    cursorAdapter?.swapCursor(cursor)
}

override fun onLoaderReset(loader: Loader<Cursor>) {
    // Delete the reference to the existing Cursor
    cursorAdapter?.swapCursor(null)
}

@SuppressLint("InlinedApi")
private val PROJECTION: Array<out String> = arrayOf(
    /*
     * The detail data row ID. To make a ListView work,
     * this column is required.
     */
    ContactsContract.Data._ID,
    // The primary display name
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        ContactsContract.Data.DISPLAY_NAME_PRIMARY
    else
        ContactsContract.Data.DISPLAY_NAME,
    // The contact's _ID, to construct a content URI
    ContactsContract.Data.CONTACT_ID,
    // The contact's LOOKUP_KEY, to construct a content URI
    ContactsContract.Data.LOOKUP_KEY
)

/*
 * Constructs search criteria from the search string
 * and email MIME type
 */
private val SELECTION: String =
    /*
     * Searches for an email address
     * that matches the search string
     */
    "${ContactsContract.CommonDataKinds.Email.ADDRESS} LIKE ? AND " +
            /*
             * Searches for a MIME type that matches
             * the value of the constant
             * Email.CONTENT_ITEM_TYPE. Note the
             * single quotes surrounding Email.CONTENT_ITEM_TYPE.
             */
            "${ContactsContract.Data.MIMETYPE } = '${ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE}'"

private var searchString: String? = null
private val selectionArgs: Array<String> = arrayOf("")

override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
    // OPTIONAL: Makes search string into pattern
    searchString = "%$mSearchString%"

    searchString?.also {
        // Puts the search string into the selection criteria
        selectionArgs[0] = it
    }
    // Starts the query
    return activity?.let {
        CursorLoader(
            it,
            ContactsContract.Data.CONTENT_URI,
            PROJECTION,
            SELECTION,
            selectionArgs,
            null
        )
    } ?: throw IllegalStateException()
}

override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
    /*
     * Appends the search string to the base URI. Always
     * encode search strings to ensure they're in proper
     * format.
     */
    val contentUri: Uri = Uri.withAppendedPath(
        ContactsContract.Contacts.CONTENT_FILTER_URI,
        Uri.encode(searchString)
    )
    // Starts the query
    return activity?.let {
        CursorLoader(
            it,
            contentUri,
            PROJECTION2,
            null,
            null,
            null
        )
    } ?: throw IllegalStateException()
}

private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Data._ID,
    ContactsContract.Data.MIMETYPE,
    ContactsContract.Data.DATA1,
    ContactsContract.Data.DATA2,
    ContactsContract.Data.DATA3,
    ContactsContract.Data.DATA4,
    ContactsContract.Data.DATA5,
    ContactsContract.Data.DATA6,
    ContactsContract.Data.DATA7,
    ContactsContract.Data.DATA8,
    ContactsContract.Data.DATA9,
    ContactsContract.Data.DATA10,
    ContactsContract.Data.DATA11,
    ContactsContract.Data.DATA12,
    ContactsContract.Data.DATA13,
    ContactsContract.Data.DATA14,
    ContactsContract.Data.DATA15
)

// Defines the selection clause
private const val SELECTION: String = "${ContactsContract.Data.LOOKUP_KEY} = ?"
// Defines the array to hold the search criteria
private val selectionArgs: Array<String> = arrayOf("")
/*
 * Defines a variable to contain the selection value. Once you
 * have the Cursor from the Contacts table, and you've selected
 * the desired row, move the row's LOOKUP_KEY value into this
 * variable.
 */
private var lookupKey: String? = null

/*
 * Defines a string that specifies a sort order of MIME type
 */
private const val SORT_ORDER = ContactsContract.Data.MIMETYPE

// Defines a constant that identifies the loader
private const val DETAILS_QUERY_ID: Int = 0

class DetailsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initializes the loader framework
        loaderManager.initLoader(DETAILS_QUERY_ID, null, this)

    }
}

override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
    // Choose the proper action
    mLoader = when(loaderId) {
        DETAILS_QUERY_ID -> {
            // Assigns the selection parameter
            selectionArgs[0] = lookupKey
            // Starts the query
            activity?.let {
                CursorLoader(
                    it,
                    ContactsContract.Data.CONTENT_URI,
                    PROJECTION,
                    SELECTION,
                    selectionArgs,
                    SORT_ORDER
                )
            }
        }
            ...
    }
    return mLoader
}

override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
    when(loader.id) {
        DETAILS_QUERY_ID -> {
            /*
             * Process the resulting Cursor here.
             */
        }
            ...
    }
}

override fun onLoaderReset(loader: Loader<Cursor>) {
    when (loader.id) {
        DETAILS_QUERY_ID -> {
            /*
             * If you have current references to the Cursor,
             * remove them here.
             */
        }
            ...
    }
}

private val PROJECTION: Array<String> = arrayOf(
    ContactsContract.CommonDataKinds.Email._ID,
    ContactsContract.CommonDataKinds.Email.ADDRESS,
    ContactsContract.CommonDataKinds.Email.TYPE,
    ContactsContract.CommonDataKinds.Email.LABEL
)

/*
 * Defines the selection clause. Search for a lookup key
 * and the Email MIME type
 */
private const val SELECTION =
    "${ContactsContract.Data.LOOKUP_KEY} = ? AND " +
            "${ContactsContract.Data.MIMETYPE} = '${Email.CONTENT_ITEM_TYPE}'"
...
// Defines the array to hold the search criteria
private val selectionArgs: Array<String> = arrayOf("")

private const val SORT_ORDER: String = "${ContactsContract.CommonDataKinds.Email.TYPE} ASC"

