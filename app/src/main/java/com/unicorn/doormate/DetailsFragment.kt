package com.unicorn.doormate

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Data.CONTENT_URI
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

private val PROJECTION: Array<String> = arrayOf(
    ContactsContract.CommonDataKinds.Email._ID,
    ContactsContract.CommonDataKinds.Email.ADDRESS,
    ContactsContract.CommonDataKinds.Email.TYPE,
    ContactsContract.CommonDataKinds.Email.LABEL
)

/*private val PROJECTION: Array<out String> = arrayOf(
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
)*/

/*@SuppressLint("InlinedApi", "ObsoleteSdkInt")
private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Data._ID,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        ContactsContract.Data.DISPLAY_NAME_PRIMARY
    else ContactsContract.Data.DISPLAY_NAME,
    ContactsContract.Data.CONTACT_ID,
    ContactsContract.Data.LOOKUP_KEY
)*/

/*const val SELECTION: String = "${ContactsContract.Data.LOOKUP_KEY} = ?"
private val selectionArgs: Array<String> = arrayOf("")
private var lookupKey: String? = null

private const val SORT_ORDER = ContactsContract.Data.MIMETYPE*/


@SuppressLint("InlinedApi", "ObsoleteSdkInt")
val SELECTION: String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
    else
        "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"

private var searchString: String = ""
private val selectionArgs = arrayOf<String>(searchString)

private var lookupKey: String? = null
private const val SORT_ORDER = ContactsContract.Data.MIMETYPE

/*
private val SELECTION: String =
    "${ContactsContract.CommonDataKinds.Email.ADDRESS} LIKE ? AND " +
            "${ContactsContract.Data.MIMETYPE } = '${ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE}'"
*/



/*
private const val SELECTION =
    "$LOOKUP_KEY = ? AND " +
            "$MIMETYPE = '${ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE}'"
private val selectionArgs: Array<String> = arrayOf("")

private const val SORT_ORDER: String = "${ContactsContract.CommonDataKinds.Email.TYPE} ASC"
*/

private const val DETAILS_QUERY_ID: Int = 0


class DetailsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loaderManager.initLoader(DETAILS_QUERY_ID, null, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    private var mLoader: Loader<Cursor>? = null

    override fun onCreateLoader(loaderId: Int, args: Bundle?): Loader<Cursor> {
        mLoader = when(loaderId) {
            DETAILS_QUERY_ID -> {
                selectionArgs[0] = lookupKey.toString()
                activity?.let {
                    CursorLoader(
                        it,
                        CONTENT_URI,
                        PROJECTION,
                        SELECTION,
                        selectionArgs,
                        SORT_ORDER
                    )
                }
            }
            else -> {
                mLoader
            }
        }
        return mLoader!!
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        when(loader.id) {
            DETAILS_QUERY_ID -> {
                /*
                 * Process the resulting Cursor here.
                 */
            }
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
        }
    }
}