package com.example.androidlabs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messages = new ArrayList<>();
        setContentView(R.layout.activity_chat_room);

        ListAdapter adapter = new MyArrayAdapter<Message>(messages);
        ListView chatList = (ListView)findViewById(R.id.lab4ChatList);
        chatList.setAdapter(adapter);

        Button sendButton = (Button)findViewById(R.id.lab4Send);
        sendButton.setOnClickListener(v -> showNewMessage(adapter, true));

        Button recvButton = (Button)findViewById(R.id.lab4Receive);
        recvButton.setOnClickListener(v -> showNewMessage(adapter, false));
    }

    /**
     * add the typed message into the message array list
     * and refresh the adapter
     * @param adapter
     * @param isSend
     */
    private void showNewMessage(ListAdapter adapter, boolean isSend) {
        EditText chatEdit = (EditText)findViewById(R.id.lab4Edit);
        Message message = new Message(chatEdit.getText().toString(), isSend);
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
            return 0;
        }
    }
}
