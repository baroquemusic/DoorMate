package com.unicorn.doormate

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.QuickContactBadge
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import java.io.FileNotFoundException
import java.io.IOException

class ContactBadgeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_badge)
    }
}

// The Cursor that contains contact rows
var mCursor: Cursor? = null
// The index of the _ID column in the Cursor
var idColumn: Int = 0
// The index of the LOOKUP_KEY column in the Cursor
var lookupKeyColumn: Int = 0
// A content URI for the desired contact
var contactUri: Uri? = null
// A handle to the QuickContactBadge view
lateinit var mBadge: QuickContactBadge

mBadge = findViewById(R.id.quickbadge)

mCursor?.let { cursor ->
    /*
     * Insert code here to move to the desired cursor row
     */
    // Gets the _ID column index
    idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
    // Gets the LOOKUP_KEY index
    lookupKeyColumn = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)
    // Gets a content URI for the contact
    contactUri = ContactsContract.Contacts.getLookupUri(
        cursor.getLong(idColumn),
        cursor.getString(lookupKeyColumn)
    )
    mBadge.assignContactUri(contactUri)
}

// The column in which to find the thumbnail ID
var thumbnailColumn: Int = 0
/*
 * The thumbnail URI, expressed as a String.
 * Contacts Provider stores URIs as String values.
 */
var thumbnailUri: String? = null

mCursor?.let { cursor ->
    /*
     * Gets the photo thumbnail column index if
     * platform version >= Honeycomb
     */
    thumbnailColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
        // Otherwise, sets the thumbnail column to the _ID column
    } else {
        idColumn
    }
    /*
     * Assuming the current Cursor position is the contact you want,
     * gets the thumbnail ID
     */
    thumbnailUri = cursor.getString(thumbnailColumn)
}

/**
 * Load a contact photo thumbnail and return it as a Bitmap,
 * resizing the image to the provided image dimensions as needed.
 * @param photoData photo ID Prior to Honeycomb, the contact's _ID value.
 * For Honeycomb and later, the value of PHOTO_THUMBNAIL_URI.
 * @return A thumbnail Bitmap, sized to the provided width and height.
 * Returns null if the thumbnail is not found.
 */
private fun loadContactPhotoThumbnail(photoData: String): Bitmap? {
    // Creates an asset file descriptor for the thumbnail file.
    var afd: AssetFileDescriptor? = null
    // try-catch block for file not found
    try {
        // Creates a holder for the URI.
        val thumbUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If Android 3.0 or later
            // Sets the URI from the incoming PHOTO_THUMBNAIL_URI
            Uri.parse(photoData)
        } else {
            // Prior to Android 3.0, constructs a photo Uri using _ID
            /*
             * Creates a contact URI from the Contacts content URI
             * incoming photoData (_ID)
             */
            val contactUri: Uri =
                Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, photoData)
            /*
             * Creates a photo URI by appending the content URI of
             * Contacts.Photo.
             */
            Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
        }

        /*
         * Retrieves an AssetFileDescriptor object for the thumbnail
         * URI
         * using ContentResolver.openAssetFileDescriptor
         */
        afd = activity?.contentResolver?.openAssetFileDescriptor(thumbUri, "r")
        /*
         * Gets a file descriptor from the asset file descriptor.
         * This object can be used across processes.
         */
        return afd?.fileDescriptor?.let {fileDescriptor ->
            // Decode the photo file and return the result as a Bitmap
            // If the file descriptor is valid
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, null)
        }
    } catch (e: FileNotFoundException) {
        /*
         * Handle file not found errors
         */
        null
    } finally {
        // In all cases, close the asset file descriptor
        try {
            afd?.close()
        } catch (e: IOException) {
        }
    }
}

/*
 * Decodes the thumbnail file to a Bitmap.
 */
mThumbnailUri?.also { thumbnailUri ->
    loadContactPhotoThumbnail(thumbnailUri).also { thumbnail ->
        /*
         * Sets the image in the QuickContactBadge
         * QuickContactBadge inherits from ImageView, so
         */
        badge.setImageBitmap(thumbnail)
    }
}

/**
 * Defines a class that hold resource IDs of each item layout
 * row to prevent having to look them up each time data is
 * bound to a row.
 */
private data class ViewHolder(
    internal var displayname: TextView? = null,
    internal var quickcontact: QuickContactBadge? = null
)

/**
 *
 *
 */
private inner class ContactsAdapter(
    context: Context,
    val inflater: LayoutInflater = LayoutInflater.from(context)
) : CursorAdapter(context, null, 0) {
    ...
    override fun newView(
        context: Context,
        cursor: Cursor,
        viewGroup: ViewGroup
    ): View {
        /* Inflates the item layout. Stores resource IDs in a
         * in a ViewHolder class to prevent having to look
         * them up each time bindView() is called.
         */
        return inflater.inflate(
            R.layout.contact_list_layout,
            viewGroup,
            false
        ).also { view ->
            view.tag = ViewHolder().apply {
                displayname = view.findViewById(R.id.displayname)
                quickcontact = view.findViewById(R.id.quickcontact)
            }
        }
    }

    ...

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        (view?.tag as? ViewHolder)?.also { holder ->
            cursor?.apply {
                ...
                // Sets the display name in the layout
                holder.displayname?.text = getString(displayNameIndex)
                ...
                /*
                 * Generates a contact URI for the QuickContactBadge.
                 */
                ContactsContract.Contacts.getLookupUri(
                    getLong(idIndex),
                    cursor.getString(lookupKeyIndex)
                ).also { contactUri ->
                    holder.quickcontact?.assignContactUri(contactUri)
                }

                getString(photoDataIndex)?.also {photoData ->
                    /*
                     * Decodes the thumbnail file to a Bitmap.
                     * The method loadContactPhotoThumbnail() is defined
                     * in the section "Set the Contact URI and Thumbnail"
                     */
                    loadContactPhotoThumbnail(photoData)?.also { thumbnailBitmap ->
                        /*
                         * Sets the image in the QuickContactBadge
                         * QuickContactBadge inherits from ImageView
                         */
                        holder.quickcontact?.setImageBitmap(thumbnailBitmap)
                    }
                }
            }
        }
    }
}

/*
 * Defines a projection based on platform version. This ensures
 * that you retrieve the correct columns.
 */
private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Contacts._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    } else {
        ContactsContract.Contacts.DISPLAY_NAME
    },
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        ContactsContract.Contacts.PHOTO_FILE_ID
    } else {
        /*
         * Although it's not necessary to include the
         * column twice, this keeps the number of
         * columns the same regardless of version
         */
        ContactsContract.Contacts._ID
    }
)

class ContactsFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    // Defines a ListView
    private val listView: ListView? = null
    // Defines a ContactsAdapter
    private val adapter: ContactsAdapter? = null

    // Defines a Cursor to contain the retrieved data
    private val cursor: Cursor? = null
    /*
     * As a shortcut, defines constants for the
     * column indexes in the Cursor. The index is
     * 0-based and always matches the column order
     * in the projection.
     */
    // Column index of the _ID column
    private val idIndex = 0
    // Column index of the LOOKUP_KEY column
    private val lookupKeyIndex = 1
    // Column index of the display name column
    private val displayNameIndex = 3
    /*
     * Column index of the photo data column.
     * It's PHOTO_THUMBNAIL_URI for Honeycomb and later,
     * and _ID for previous versions.
     */
    private val photoDataIndex: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 3 else 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(...).let { view ->
            ...
            /*
             * Gets a handle to the ListView in the file
             * contact_list_layout.xml
             */
            listView = view.findViewById<ListView>(R.id.contact_list)
            mAdapter?.also {
                listView?.adapter = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
         * Instantiates the subclass of
         * CursorAdapter
         */
        mAdapter = activity?.let {
            ContactsAdapter(it).also { adapter ->
                // Sets up the adapter for the ListView
                listView?.adapter = adapter
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        // When the loader has completed, swap the cursor into the adapter.
        mAdapter?.swapCursor(cursor)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Removes remaining reference to the previous Cursor
        adapter?.swapCursor(null)
    }




