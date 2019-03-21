package com.example.androidlabs;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * refer to prof's week8
 */
public class DetailFragment extends Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;

    public void setTablet(boolean tablet) { isTablet = tablet; }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(ChatRoomActivity.MSG_ID );

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_detail, container, false);

        //show the message
        TextView message = (TextView)result.findViewById(R.id.lab8Message);
        message.setText(dataFromActivity.getString(ChatRoomActivity.MSG_TEXT));

        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.lab8IDText);
        idView.setText("ID=" + id);

        //show the send / receive flag:
        TextView flagView = (TextView)result.findViewById(R.id.lab8SendRecv);
        flagView.setText(dataFromActivity.getBoolean(ChatRoomActivity.MSG_IS_SEND)? "SEND": "RECEIVE");

        // get the delete button, and add a click listener:
        Button deleteButton = (Button)result.findViewById(R.id.lab8DeleteButton);
        deleteButton.setOnClickListener( clk -> {

            if(isTablet) { //both the list and details are on the screen:
                ChatRoomActivity parent = (ChatRoomActivity)getActivity();
                parent.deleteMessageId((int)id); //this deletes the item and updates the list


                //now remove the fragment since you deleted it from the database:
                // this is the object to be removed, so remove(this):
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
            else //You are only looking at the details, you need to go back to the previous list page
            {
                ChatMessageActivity parent = (ChatMessageActivity) getActivity();
                Intent backToChatRoom = new Intent();
                backToChatRoom.putExtra(ChatRoomActivity.MSG_ID, dataFromActivity.getLong(ChatRoomActivity.MSG_ID ));

                parent.setResult(Activity.RESULT_OK, backToChatRoom); //send data back to ChatRoomActivity in onActivityResult()
                parent.finish(); //go back
            }
        });
        return result;
    }
}
