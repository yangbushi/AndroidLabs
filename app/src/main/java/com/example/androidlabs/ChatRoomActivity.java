package com.example.androidlabs;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    public static final String MSG_ID = "ID";
    public static final String MSG_TEXT = "TEXT";
    public static final String MSG_IS_SEND = "IS_SEND";
    public static final int CHAT_MSG_ACTIVITY = 345;

    MyDatabaseOpenHelper dbOpener;
    SQLiteDatabase db;

    MyArrayAdapter<Message> adapter;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messages = new ArrayList<>();
        setContentView(R.layout.activity_chat_room);

        //get a database:
        dbOpener = new MyDatabaseOpenHelper(this);
        db = dbOpener.getWritableDatabase();
        loadMessagesFromDB(db);

        boolean isTablet = findViewById(R.id.lab8FragLocation) != null;

        // set the adapter for the listView
        adapter = new MyArrayAdapter<Message>(messages);
        ListView chatList = (ListView)findViewById(R.id.lab4ChatList);
        chatList.setAdapter(adapter);

        chatList.setOnItemClickListener((list, item, position, id) -> { // refer to prof's week8
            Bundle dataToPass = new Bundle();
            Message message = messages.get(position);
            dataToPass.putBoolean(MSG_IS_SEND, message.isSend() );
            dataToPass.putString(MSG_TEXT, message.getMessageText());
            dataToPass.putLong(MSG_ID, id);

            if(isTablet)
            {
                DetailFragment dFragment = new DetailFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.lab8FragLocation, dFragment) //Add the fragment in FrameLayout
                        .addToBackStack("AnyName") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(ChatRoomActivity.this, ChatMessageActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, CHAT_MSG_ACTIVITY); //make the transition
            }
        });

        Button sendButton = (Button)findViewById(R.id.lab4Send);
        sendButton.setOnClickListener(v -> showNewMessage(adapter, true, db));

        Button recvButton = (Button)findViewById(R.id.lab4Receive);
        recvButton.setOnClickListener(v -> showNewMessage(adapter, false, db));
    }

    /**
     * load messages from database
     * copy mostly from professor's week5
     */
    private void loadMessagesFromDB(SQLiteDatabase db) {
        //query all the results from the database:
        String [] columns = {MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.COL_TEXT, MyDatabaseOpenHelper.COL_IS_SEND};
        Cursor results = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);
        printCursor(results);

        //find the column indices:
        int isSendColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_IS_SEND);
        int textColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_TEXT);
        int idColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String text = results.getString(textColIndex);
            boolean isSend = results.getInt(isSendColumnIndex)==1? true: false;
            long id = results.getLong(idColIndex);

            //add the new message to the array list:
            messages.add(new Message(id, text, isSend));
        }
    }

    /**
     * add the typed message into the database and message array list
     * and refresh the adapter
     * @param adapter
     * @param isSend
     */
    private void showNewMessage(ListAdapter adapter, boolean isSend, SQLiteDatabase db) {
        EditText chatEdit = (EditText)findViewById(R.id.lab4Edit);
        String text = chatEdit.getText().toString();

        //add to the database and get the new ID
        ContentValues newRowValues = new ContentValues();
        newRowValues.put(MyDatabaseOpenHelper.COL_TEXT, text);
        newRowValues.put(MyDatabaseOpenHelper.COL_IS_SEND, isSend? 1: 0);
        //insert in the database:
        long newId = db.insert(MyDatabaseOpenHelper.TABLE_NAME, null, newRowValues);

        //now you have the newId, you can create the message object
        Message message = new Message(newId, text, isSend);
        //add the new message to the list:
        messages.add(message);
        ((MyArrayAdapter) adapter).notifyDataSetChanged();
        chatEdit.setText("");
    }

    /**
     * Copy from Professor's week4 branch
     * @param <E>
     */
    protected class MyArrayAdapter<E> extends BaseAdapter
    {
        private List<E> dataCopy = null;

        //Keep a reference to the data:
        public MyArrayAdapter(List<E> originalData)
        {
            dataCopy = originalData;
        }

        //You can give it an array
        public MyArrayAdapter(E [] array)
        {
            dataCopy = Arrays.asList(array);
        }


        //Tells the list how many elements to display:
        public int getCount()
        {
            return dataCopy.size();
        }


        public E getItem(int position){
            return dataCopy.get(position);
        }

        public View getView(int position, View old, ViewGroup parent)
        {
            //get an object to load a layout:
            LayoutInflater inflater = getLayoutInflater();

            //Recycle views if possible:
            View root = old;
            //If there are no spare layouts, load a new one:
            if(old == null)
                root = inflater.inflate(R.layout.single_row, parent, false);


            boolean isSend = ((Message)getItem(position)).isSend();
            ImageView chatImage = (ImageView)root.findViewById(R.id.lab4Image);
            TextView chatText = (TextView)root.findViewById(R.id.lab4ChatRow);
            chatText.setText(((Message)getItem(position)).getMessageText());
            if(isSend) {
                // set the picture and layout for the icon and text
                chatImage.setImageResource(R.drawable.row_send);
                RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams)chatImage.getLayoutParams();
                imageParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                chatImage.setLayoutParams(imageParams);

                // set the layout for the text
                RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams)chatText.getLayoutParams();
                textParams.removeRule(RelativeLayout.RIGHT_OF);
                textParams.addRule(RelativeLayout.LEFT_OF, R.id.lab4Image);
                chatText.setLayoutParams(textParams);

            } else {
                chatImage.setImageResource(R.drawable.row_receive);
                RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams)chatImage.getLayoutParams();
                imageParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                chatImage.setLayoutParams(imageParams);


                RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams)chatText.getLayoutParams();
                textParams.removeRule(RelativeLayout.LEFT_OF);
                textParams.addRule(RelativeLayout.RIGHT_OF, R.id.lab4Image);
                chatText.setLayoutParams(textParams);

            }

            //Return the view:
            return root;
        }


        //Return 0 for now. We will change this when using databases
        public long getItemId(int position)
        {
            return ((Message)getItem(position)).getId();
        }


    }
    /**
     * print the following information to the Log window after you run your query:
     *
     * •	The database version number
     * •	The number of columns in the cursor.
     * •	The name of the columns in the cursor.
     * •	The number of results in the cursor
     * •	Each row of results in the cursor.
     * @param c
     */
    public void printCursor( Cursor c) {
        Log.i("PrintCursor", "Database version: " + MyDatabaseOpenHelper.VERSION_NUM);
        Log.i("PrintCursor", "Number of columns: " + c.getColumnCount());
        for(int i = 0; i < c.getColumnCount(); i++)
            Log.i("PrintCursor", "Name of the columns: " + c.getColumnNames()[i]);
        Log.i("PrintCursor", "Number of results: " + c.getCount());
        Log.i("PrintCursor", "Each row of results: " + DatabaseUtils.dumpCursorToString(c));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHAT_MSG_ACTIVITY)
        {
            if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                long id = data.getLongExtra(MSG_ID, 0);
                deleteMessageId((int)id);
            }
        }
    }

    public void deleteMessageId(int id)
    {
        Log.i("Delete this message:" , " id="+id);

        // delete the message from database
        db.delete(MyDatabaseOpenHelper.TABLE_NAME, MyDatabaseOpenHelper.COL_ID + "= ?", new String[] {Long.toString(id)});

        // delete the message from adapter
        for(Message message: messages) {
            if(message.getId() == id) {
                messages.remove(message);
                break;
            }
        }

        adapter.notifyDataSetChanged();
    }
}
